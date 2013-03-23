package org.wkh.bateman.model;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import java.math.BigDecimal;
import org.joda.time.DateTime;
import org.wkh.bateman.trade.Account;
import org.wkh.bateman.trade.Asset;
import org.wkh.bateman.trade.Conditions;
import org.wkh.bateman.trade.MoneyManagementStrategy;
import org.wkh.bateman.trade.Rule;
import org.wkh.bateman.trade.Session;
import org.wkh.bateman.trade.Trade;

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
        if(session.inMarket(time))
            return false;
        
        BigDecimal open = asset.getTimeSeries().openOnDay(time);
        BigDecimal current = asset.priceAt(time);
        BigDecimal increase = current.subtract(open);
        
        final DateTime midnight = time.toDateMidnight().toDateTime();
        final DateTime nextDay = midnight.plusDays(1);
        
        boolean tradedToday = (Collections2.filter(session.getTrades(), new Predicate<Trade>() {
            @Override
            public boolean apply(Trade trade) {
                return trade.getOpen().compareTo(midnight) > 0 && trade.getClose().compareTo(nextDay) < 0;
            }
        })).size() > 0;
        
        return !tradedToday && increase.compareTo(buyTrigger) >= 0;
    }

    @Override
    public boolean sell(DateTime time, Session session) {        
        if(!session.inMarket(time))
            return false;
        
        BigDecimal open = asset.getTimeSeries().openOnDay(time);
        DateTime buyDate = session.lastTrade().getOpen();
        BigDecimal buyPrice = asset.priceAt(buyDate);
        BigDecimal current = asset.priceAt(time);
        BigDecimal difference = current.subtract(buyPrice);
        
        // exit if at end of day, if our sell trigger threshold is reached,
        // or if we hit our stop loss
        
        DateTime endOfDay = asset.getTimeSeries().closeOnDay(time);
        
        boolean atEndOfDay = time.compareTo(endOfDay) >= 0;
        
        boolean thresholdReached = difference.compareTo(sellTrigger) >= 0;
        
        boolean stopLossReached = buyPrice.subtract(current).compareTo(stopLoss) >= 0;
        
        return atEndOfDay || thresholdReached || stopLossReached;
    }
}
