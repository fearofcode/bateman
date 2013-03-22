package org.wkh.bateman.model;

import java.math.BigDecimal;
import java.util.TreeMap;
import static junit.framework.Assert.assertEquals;
import junit.framework.TestCase;
import org.joda.time.DateTime;
import org.wkh.bateman.trade.Account;
import org.wkh.bateman.trade.Asset;
import org.wkh.bateman.trade.Conditions;
import org.wkh.bateman.trade.FixedPercentageAllocationStrategy;
import org.wkh.bateman.trade.MoneyManagementStrategy;
import org.wkh.bateman.trade.Session;
import org.wkh.bateman.trade.TimeSeries;
import org.wkh.bateman.trade.Trade;

public class BuyZoneModelTest extends TestCase {
    TreeMap<DateTime, BigDecimal> toyPrices;
    Account account;
    TimeSeries series;
    Asset asset;
    DateTime today;
    Conditions conditions;
    MoneyManagementStrategy moneyManager;
    Session session;
    
    double buyTrigger;
    double sellTrigger;
    double stopLoss;
    
    BuyZoneModel instance;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        
        today = DateTime.now();

        toyPrices = new TreeMap<DateTime, BigDecimal>();
        toyPrices.put(today, new BigDecimal(10));
        toyPrices.put(today.plusMinutes(1), new BigDecimal(10.5));
        toyPrices.put(today.plusMinutes(2), new BigDecimal(11.3));
        toyPrices.put(today.plusMinutes(3), new BigDecimal(12));
        toyPrices.put(today.plusDays(1), new BigDecimal(10.1));
        toyPrices.put(today.plusDays(1).plusMinutes(1), new BigDecimal(9.5));
        series = new TimeSeries(toyPrices);
        asset = new Asset("FOO", series);

        account = new Account(new BigDecimal(1000), today.minusDays(6));

        conditions = new Conditions(BigDecimal.ZERO, BigDecimal.ZERO);
        moneyManager = new FixedPercentageAllocationStrategy(0.2, asset);
        session = new Session(account, conditions);

        buyTrigger = 0.4;
        sellTrigger = 1.0;
        stopLoss = 0.25;
        
        instance = new BuyZoneModel(account, asset, conditions, 
                moneyManager, buyTrigger, sellTrigger, stopLoss);
    }
    
    public void testBuy() throws Exception {
        assertEquals(instance.buy(today, session), false);
        assertEquals(instance.buy(today.plusMinutes(1), session), true);
        
        assertEquals(instance.buy(today.plusMinutes(2), session), true);
        session.addTrade(new Trade(asset, today.plusMinutes(1)));
        assertEquals(instance.buy(today.plusMinutes(2), session), false);
        
    }

    public void testSell() throws Exception {
        assertEquals(instance.sell(today.plusMinutes(2), session), false);
        
        session.addTrade(new Trade(asset, today.plusMinutes(1)));
        
        assertEquals(instance.sell(today.plusMinutes(2), session), false);
        assertEquals(instance.sell(today.plusMinutes(3), session), true);
    }
    
    public void testEndOfDaySell() throws Exception {
        sellTrigger = 1337.0;
        instance = new BuyZoneModel(account, asset, conditions, 
                moneyManager, buyTrigger, sellTrigger, stopLoss);
        
        session.addTrade(new Trade(asset, today.plusMinutes(1)));
        assertEquals(instance.sell(today.plusMinutes(3), session), true);
    }
}
