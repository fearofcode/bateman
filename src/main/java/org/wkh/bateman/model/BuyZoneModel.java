package org.wkh.bateman.model;

import java.math.BigDecimal;
import org.joda.time.DateTime;
import org.wkh.bateman.trade.Account;
import org.wkh.bateman.trade.Asset;
import org.wkh.bateman.trade.Conditions;
import org.wkh.bateman.trade.MoneyManagementStrategy;
import org.wkh.bateman.trade.Rule;
import org.wkh.bateman.trade.Session;

/* 
 * Here's the idea for this project: lots of stocks, if they go open by a small amount, will go 
 * up a significant amount, regardless of how they finish at the end of the 
 * trading day. This model buys if a stock has gone up a certain amount, then 
 * sells once it reaches a specified constant threshold.
 * 
 * A plot of this model, in other words, consists of two horizontal lines on a
 * chart of a time zeries.
 * 
 * It is about the simplest "buy low, sell high model" one could think of.
 */
public class BuyZoneModel extends Rule {
    private Asset asset;
    private BigDecimal stopLoss;
    private BigDecimal buyTrigger;
    private BigDecimal sellTrigger;
    
    public BuyZoneModel(Account account, Asset asset, Conditions conditions, 
            MoneyManagementStrategy moneyManager, double buyTrigger, 
            double sellTrigger, double stopLoss) {
        super(account, asset, conditions, moneyManager);
    
        this.asset = asset;
        this.buyTrigger = new BigDecimal(buyTrigger);
        this.sellTrigger = new BigDecimal(sellTrigger);
        this.stopLoss = new BigDecimal(stopLoss);
    }

    @Override
    public boolean buy(DateTime time, Session session) {
        BigDecimal open = asset.getTimeSeries().openOnDay(time);
        BigDecimal current = asset.getTimeSeries().priceAt(time);
        BigDecimal increase = current.subtract(open);
        
        return !session.inMarket(time) && increase.compareTo(buyTrigger) > 0;
    }

    @Override
    public boolean sell(DateTime time, Session session) {        
        BigDecimal open = asset.getTimeSeries().openOnDay(time);
        BigDecimal buyPrice = open.add(buyTrigger);
        BigDecimal current = asset.getTimeSeries().priceAt(time);
        BigDecimal difference = current.subtract(buyPrice);
        
        // TODO exit if at end of day
        boolean inMarket = session.inMarket(time);
        boolean thresholdReached = difference.compareTo(sellTrigger) > 0;
        
        boolean stopLossReached = buyPrice.subtract(current).compareTo(stopLoss) > 0;
        
        return inMarket && (thresholdReached || stopLossReached);
    }
}
