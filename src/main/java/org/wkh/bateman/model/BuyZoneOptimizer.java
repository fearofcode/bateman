package org.wkh.bateman.model;

import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;
import org.joda.time.DateTime;
import org.wkh.bateman.pso.FitnessFunction;
import org.wkh.bateman.pso.SimpleParticleSwarmOptimizer;
import org.wkh.bateman.trade.Account;
import org.wkh.bateman.trade.Asset;
import org.wkh.bateman.trade.Conditions;
import org.wkh.bateman.trade.FixedPercentageAllocationStrategy;
import org.wkh.bateman.trade.GoogleQuoteFetcher;
import org.wkh.bateman.trade.MoneyManagementStrategy;
import org.wkh.bateman.trade.Rule;
import org.wkh.bateman.trade.Session;
import org.wkh.bateman.trade.TimeSeries;

/* This is the heart of the project: 
 * 
 *   Use PSO to find optimal buy and sell triggers for a BuyZoneModel.
 * 
 * Everything else in the project basically builds up to this. How exciting!
 *  
 */
public class BuyZoneOptimizer {

    public static double[] optimizeTriggers(final TimeSeries series, final String symbol, final int days, 
            double commissions, final double slippage, final int initialBalance, final double allocation, final double maxPercentage, 
            final int generations) throws Exception {

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

        BigDecimal lastPrice = asset.priceAt(asset.getTimeSeries().lastOfSeries());

        double lastPriceFraction = lastPrice.multiply(new BigDecimal(maxPercentage)).doubleValue();

        double[] xmin = new double[]{0.0, 0.0, 0.0};
        double[] xmax = new double[]{lastPriceFraction, lastPriceFraction, lastPriceFraction};

        SimpleParticleSwarmOptimizer optimizer = new SimpleParticleSwarmOptimizer(fitness, xmin, xmax, generations);

        return optimizer.learn();
    }

    public static void main(String[] args) throws Exception {
        
        int days = 30;
        String symbol = "AAPL";
        final double commission = 10.0; // $10.00 a trade
        final double slippage = 1.0E-4; // 0.01% mean slippage
        final int initialBalance = 100000; // $100,000 to start with
        final double accountAllocation = 0.75; // risk 75% of capital
        final double maxPercentage = 0.01; // restrict parameters to 1.5% of the opening price on first day's data
        final int generations = 25; // generations to train for
        
        DateTime today = DateTime.now();

        GoogleQuoteFetcher fetcher = new GoogleQuoteFetcher();

        TimeSeries series = fetcher.fetchAndParse(symbol, days, 60); // one minute
        
        double[] bestOffsets = optimizeTriggers(series, symbol, days, commission, 
                slippage, initialBalance, accountAllocation, maxPercentage, generations);

        double buyTrigger = bestOffsets[0];
        double sellTrigger = bestOffsets[1];
        double stopLoss = bestOffsets[2];

        System.out.println("buyTrigger: " + buyTrigger);
        System.out.println("sellTrigger; " + sellTrigger);
        System.out.println("stopLoss: " + stopLoss);

        Asset asset = new Asset(symbol, series);

        Account account = new Account(new BigDecimal(initialBalance), today.minusDays(days));

        Conditions conditions = new Conditions(new BigDecimal(commission), new BigDecimal(slippage));
        MoneyManagementStrategy moneyManager = new FixedPercentageAllocationStrategy(accountAllocation, asset);

        BuyZoneModel instance = new BuyZoneModel(account, asset, conditions,
                moneyManager, buyTrigger, sellTrigger, stopLoss);

        Session session = instance.generateSignals(series.beginningOfSeries(), series.lastOfSeries());

        session.dumpTo(".", 1);


    }
}
