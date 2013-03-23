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
    public static double[] optimizeTriggers(final Account account, final Asset asset, final Conditions conditions, 
            final MoneyManagementStrategy moneyManager) {
        FitnessFunction fitness = new FitnessFunction() {

            public double evaluate(double[] x) {
                double buyTrigger = x[0];
                double sellTrigger = x[1];
                double stopLoss = x[2];
                
                BuyZoneModel model = new BuyZoneModel(account, asset, conditions, moneyManager, buyTrigger, sellTrigger, stopLoss);
                
                try {
                    Session tradingSession = model.generateSignals(asset.getTimeSeries().beginningOfSeries(), 
                        asset.getTimeSeries().lastOfSeries());
                    
                    return -tradingSession.grossProfit().doubleValue();
                } catch(Exception ex) {
                    ex.printStackTrace();
                    
                    return Double.MAX_VALUE;
                }
            }
        };
        
        BigDecimal lastPrice = asset.priceAt(asset.getTimeSeries().lastOfSeries());
        
        double lastPriceFraction = lastPrice.multiply(new BigDecimal(0.05)).doubleValue();
        
        double[] xmin = new double[] {0.0, 0.0, 0.0};
        double[] xmax = new double[] {lastPriceFraction, lastPriceFraction, lastPriceFraction};
        
        SimpleParticleSwarmOptimizer optimizer = new SimpleParticleSwarmOptimizer(fitness, xmin, xmax, 250);
        
        return optimizer.learn();
    }
    
    public static void main(String[] args) throws Exception {
        DateTime today = DateTime.now();

        GoogleQuoteFetcher fetcher = new GoogleQuoteFetcher();
       
        int days = 30;
        String symbol = "AAPL";
        
        TimeSeries series = fetcher.fetchAndParse(symbol, days, 60); // one minute
        
        Asset asset = new Asset(symbol, series);

        Account account = new Account(new BigDecimal(100000), today.minusDays(days));

        Conditions conditions = new Conditions(new BigDecimal(10.0), new BigDecimal(0.0001));
        MoneyManagementStrategy moneyManager = new FixedPercentageAllocationStrategy(0.75, asset);

        BuyZoneModel instance;
        
        double buyTrigger = 0.25;
        double sellTrigger = 2.0;
        double stopLoss = 1.5;
        
        instance = new BuyZoneModel(account, asset, conditions, 
                moneyManager, buyTrigger, sellTrigger, stopLoss);
        
        Session session = instance.generateSignals(series.beginningOfSeries(), series.lastOfSeries());
        
        session.dumpTo(".", 1);
        
        /*double[] bestOffsets = optimizeTriggers(account, asset, conditions, moneyManager);
        
        double buyTrigger = bestOffsets[0];
        double sellTrigger = bestOffsets[1];
        double stopLoss = bestOffsets[2];
        
        System.out.println("buyTrigger: " + buyTrigger);
        System.out.println("sellTrigger; " + sellTrigger);
        System.out.println("stopLoss: " + stopLoss);*/
        
        
    }
}
