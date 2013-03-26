package org.wkh.bateman.model;

import java.math.BigDecimal;
import java.util.List;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.wkh.bateman.fetch.Quote;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wkh.bateman.pso.FitnessFunction;
import org.wkh.bateman.pso.SimpleParticleSwarmOptimizer;
import org.wkh.bateman.trade.Account;
import org.wkh.bateman.trade.Asset;
import org.wkh.bateman.trade.Conditions;
import org.wkh.bateman.trade.FixedPercentageAllocationStrategy;
import org.wkh.bateman.fetch.GoogleQuoteFetcher;
import org.wkh.bateman.fetch.YahooQuoteFetcher;
import org.wkh.bateman.trade.MoneyManagementStrategy;
import org.wkh.bateman.trade.Session;
import org.wkh.bateman.trade.TimeSeries;

/*
 * This is the heart of the project: 
 * 
 *   Use PSO to find optimal buy and sell triggers for a BuyZoneModel.
 * 
 * Everything else in the project basically builds up to this. How exciting!
 *  
 */
public class BuyZoneOptimizer {
    private static Logger logger = LoggerFactory.getLogger(SimpleParticleSwarmOptimizer.class.getName());
    
    public static double[] optimizeTriggers(final TimeSeries series, final String symbol, final int days,
            double commissions, final double slippage, final int initialBalance, final double allocation,
            final double minBuy, final double minSell, final double minStop, final double maxBuy,
            final double maxSell, final double maxStop, final int generations) throws Exception {

        final Asset asset = new Asset(symbol, series);

        final Conditions conditions = new Conditions(new BigDecimal(commissions), new BigDecimal(slippage));

        final MoneyManagementStrategy moneyManager = new FixedPercentageAllocationStrategy(allocation, asset);

        FitnessFunction fitness = new FitnessFunction() {
            public double evaluate(double[] x) {
                double buyTrigger = x[0];
                double sellTrigger = x[1];
                double stopLoss = x[2];

                try {
                    Account account = new Account(new BigDecimal(initialBalance), DateTime.now().minusDays(days));

                    BuyZoneModel model = new BuyZoneModel(account, asset, conditions, moneyManager, buyTrigger, sellTrigger, stopLoss);

                    Session tradingSession = model.generateSignals(asset.getTimeSeries().beginningOfSeries(),
                            asset.getTimeSeries().lastOfSeries());

                    //return -tradingSession.grossProfit().doubleValue();
                    return -tradingSession.sharpeRatio();
                } catch (Exception ex) {
                    ex.printStackTrace();

                    return Double.MAX_VALUE;
                }
            }
        };

        double[] xmin = new double[]{minBuy, minSell, 0.0};
        double[] xmax = new double[]{maxBuy, maxSell, maxStop};

        SimpleParticleSwarmOptimizer optimizer = new SimpleParticleSwarmOptimizer(fitness, xmin, xmax, generations);

        return optimizer.learn();
    }

    public static double getMedianHighOpenSpread(String symbol, int days) throws Exception {
        YahooQuoteFetcher yahooFetcher = new YahooQuoteFetcher();
        String quoteStr = yahooFetcher.fetchQuotes(symbol, days, 60*60*24);
        List<Quote> dailyQuoteList = yahooFetcher.parseQuotes(quoteStr, 60*60*24);
        
        DescriptiveStatistics stats = new DescriptiveStatistics();
        
        for(Quote quote : dailyQuoteList) {
            stats.addValue(quote.getHigh().subtract(quote.getOpen()).doubleValue());
        }
        
        return stats.getPercentile(50);
    }
    
    public static void main(String[] args) throws Exception {

        int days = 30;
        String symbol = args.length > 0 ? args[0] : "AAPL"; // default to AAPL if nothing provided
        
        logger.info("Fetching data for symbol " + symbol);
        
        double yearlyMedianDailyIncrease = getMedianHighOpenSpread(symbol, 365);
        
        GoogleQuoteFetcher fetcher = new GoogleQuoteFetcher();

        TimeSeries series = fetcher.fetchAndParse(symbol, days, 60); // one minute
        BigDecimal firstPrice = series.getPrices().firstEntry().getValue();
        
        BigDecimal lastBidAskSpread = new YahooQuoteFetcher().fetchBidAskSpread(symbol);
        
        final double minBuy = 0; // allow buying at open price
        final double minSell = firstPrice.multiply(new BigDecimal("0.002")).doubleValue(); // 0.2% of first price to sell (which is hopefully on the order of twice the bid-ask spread)
        final double minStop = minSell;
        final double maxBuy = yearlyMedianDailyIncrease;
        final double maxSell = yearlyMedianDailyIncrease;
        final double maxStop = yearlyMedianDailyIncrease;

        logger.info("Minimum buy:" + minBuy);
        logger.info("Minimum sell: " + minSell);
        logger.info("Minimum stop: " + minStop);
        logger.info("Max buy: " + maxBuy);
        logger.info("Max sell: " + maxSell);
        logger.info("Max stop: " + maxStop);
        
        final double commission = 10.0; // $10.00 a trade
        final double slippage = 1.0E-3; // 0.1% mean slippage on each side of a trade, which should also account for bid-ask spread
        final int initialBalance = 100000; // $100,000 to start with
        final double accountAllocation = 0.75; // risk 75% of capital
        final int generations = 100; // generations to train for

        DateTime today = DateTime.now();


        double[] bestOffsets = optimizeTriggers(series, symbol, days, commission,
                slippage, initialBalance, accountAllocation, minBuy,
                minSell, minStop, maxBuy, maxSell, maxStop, generations);

        double buyTrigger = bestOffsets[0];
        double sellTrigger = bestOffsets[1];
        double stopLoss = bestOffsets[2];

        System.out.println("\n\nBuy trigger: " + buyTrigger);
        System.out.println("Sell trigger: " + sellTrigger);
        System.out.println("Stop loss: " + stopLoss);

        Asset asset = new Asset(symbol, series);

        Account account = new Account(new BigDecimal(initialBalance), today.minusDays(days));

        Conditions conditions = new Conditions(new BigDecimal(commission), new BigDecimal(slippage));
        MoneyManagementStrategy moneyManager = new FixedPercentageAllocationStrategy(accountAllocation, asset);

        BuyZoneModel instance = new BuyZoneModel(account, asset, conditions,
                moneyManager, buyTrigger, sellTrigger, stopLoss);

        Session session = instance.generateSignals(series.beginningOfSeries(), series.lastOfSeries());

        session.dumpTo(".", symbol);


    }
}
