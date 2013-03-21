package org.wkh.bateman.trade;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Session {
    private List<Trade> trades;
    private Account account;
    private Conditions conditions;
    private List<BigDecimal> profitCurve;
    private DescriptiveStatistics stats;

    public Session(Account account, final Conditions conditions) {
        stats = new DescriptiveStatistics();

        profitCurve = new ArrayList<BigDecimal>();

        trades = new ArrayList<Trade>();
        this.account = account;
        this.conditions = conditions;
    }

    public void addTrade(Trade trade) throws Exception {
        if (trades.contains(trade))
            throw new Exception("Cannot add the same trade more than once");
        else if (trades.size() > 0) {
            if (lastTrade().isOpen() || lastTrade().getClose().compareTo(trade.getOpen()) > 0)
                throw new Exception("Trades cannot overlap and must be added in increasing chronological order");
        }


        trades.add(trade);

        if (trade.isClosed())
            tabulateClosedTrade(trade);
        else if (trade.isOpen()) {
            BigDecimal purchasePrice = trade.getPurchasePrice();
            DateTime time = trade.getOpen().plusSeconds(1);
            account.withdraw(purchasePrice, time);
        }
    }

    public void tabulateClosedTrade(Trade trade) throws Exception {
        BigDecimal profit = trade.profit();
        account.profit(trade.getSellPrice(), trade.getClose());
        profitCurve.add(profit);
        stats.addValue(profit.doubleValue());
    }

    public Conditions getConditions() {
        return conditions;
    }

    public List<BigDecimal> getProfitCurve() {
        return profitCurve;
    }

    public Account getAccount() {
        return account;
    }

    public BigDecimal grossProfit() throws Exception {
        BigDecimal sum = new BigDecimal(0);

        for (Trade trade : trades)
            sum = sum.add(trade.profit());

        return sum;
    }

    public double sharpeRatio() {
        if(stats.getValues().length == 0)
            return 0.00001;

        double stdDev = stats.getStandardDeviation();
        return stats.getMean() / (stdDev == 0.0 ? 100.0 : stdDev);
    }

    public List<Trade> getTrades() {
        return trades;
    }

    public Trade lastTrade() {
        return trades.get(trades.size() - 1);
    }

    public void closeLastTrade(DateTime time) throws Exception {
        lastTrade().setClose(time);

        tabulateClosedTrade(lastTrade());
    }

    public boolean inMarket(final DateTime dateTime) {
        return (Collections2.filter(trades, new Predicate<Trade>() {
            @Override
            public boolean apply(Trade input) {
                return input.includesDate(dateTime);
            }
        })).size() > 0;
    }

    public Trade tradeAt(final DateTime date) {
        for(Trade trade : trades) {
            Interval interval = new Interval(trade.getOpen(), trade.getClose());
            if(interval.contains(date)) {
                return trade;
            }
        }

        return null;
    }

    // TODO fuck this awful code

    public void dumpTo(String directory, int generation) throws Exception {
        long now = System.currentTimeMillis();
        String sessionStr = now + "_generation_" + generation;
        String dataFilename = sessionStr + "_trades.dat";
        String directoryStr = directory + (directory.endsWith("?") ? "" : "/");
        String dataPath = directoryStr + dataFilename;

        System.out.println("writing data to " + dataPath);

        BufferedWriter out = new BufferedWriter(new FileWriter(dataPath));

        out.write("# Trade log\n");
        out.write("# Open Close OpenPrice ClosePrice Type Size OutlayCost Profit Balance\n");

        DateTimeFormatter fmt = DateTimeFormat.forPattern("Y-MM-dd");

        for(Trade trade : trades) {
            String openStr = fmt.print(trade.getOpen());
            String closeStr = fmt.print(trade.getClose());

            out.write(openStr + " " + closeStr + " " + trade.getAsset().priceAt(trade.getOpen()) + " " +
                    trade.getAsset().priceAt(trade.getClose()) + " " + trade.getType() + " " + trade.getSize() + " " +
                    trade.getPurchasePrice() + " " + trade.profit() + " " + account.valueAtTime(trade.getClose()) + "\n");
        }

        out.write("\n\n");

        TimeSeries series = trades.get(0).getAsset().getTimeSeries();

        String oldType = null;

        out.write("# Price history by trade status\n");

        for(Map.Entry<DateTime, BigDecimal> entry : series.getPrices().entrySet()) {
            String defaultState = "NOT_IN_MARKET";
            String currentType = defaultState;

            DateTime date = entry.getKey();
            boolean inMarket = inMarket(date);
            String dateStr = fmt.print(date);

            if(inMarket) {
                Trade currentTrade = tradeAt(date);

                if(currentTrade == null) {
                    throw new Exception("Can't find trade for date " + date);
                }

                currentType = currentTrade.getType().toString();
            }

            if(oldType != null && !currentType.equals(oldType)) {
                out.write(dateStr + " " + entry.getValue() + " " + oldType + "\n\n\n");
            }

            out.write(dateStr + " " + entry.getValue() + " " + currentType + "\n");

            oldType = currentType;
        }

        out.close();
    }
}
