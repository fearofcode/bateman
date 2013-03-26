package org.wkh.bateman.trade;

import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import static junit.framework.Assert.*;

public class SessionTest extends TradeTest {

    Session session;
    TreeMap<DateTime, BigDecimal> toyPrices;
    Account account;
    TimeSeries series;
    Asset asset;
    Trade winningTrade, losingTrade;
    private Conditions conditions;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        toyPrices = new TreeMap<DateTime, BigDecimal>();
        toyPrices.put(today.minusDays(5), new BigDecimal(10));
        toyPrices.put(today.minusDays(4), new BigDecimal(15));
        toyPrices.put(today.minusDays(3), new BigDecimal(20));
        toyPrices.put(today.minusDays(2), new BigDecimal(10));
        toyPrices.put(today.minusDays(1), new BigDecimal(5));
        toyPrices.put(today, new BigDecimal(10));

        series = new TimeSeries(toyPrices);
        asset = new Asset("FOO", series);

        conditions = new Conditions(new BigDecimal(1), new BigDecimal(0));
        winningTrade = new Trade(asset, today.minusDays(5), today.minusDays(3), 1, TradeType.LONG, conditions); // 10 -> 20
        losingTrade = new Trade(asset, today.minusDays(3), today.minusDays(1), 1, TradeType.LONG, conditions); // 20 -> 5

        account = new Account(new BigDecimal(100), today.minusDays(6));

        session = new Session(account, conditions);

        session.addTrade(winningTrade);
        session.addTrade(losingTrade);
    }

    public void testGettingLastTrade() {
        assertEquals(losingTrade, session.lastTrade());
    }

    public void testInMarket() {
        assertTrue(session.inMarket(today.minusDays(4)));
        assertFalse(session.inMarket(today));
    }

    public void testAddingTrades() throws Exception {
        Session session = new Session(new Account(new BigDecimal(100), trade.getOpen().minusDays(1)), new Conditions(new BigDecimal(0.01), new BigDecimal(0.005)));
        session.addTrade(trade);
        assertEquals(trade.profit(), session.grossProfit());
    }

    public void testAllowsOpenTrades() throws Exception {
        session.addTrade(openTrade);
    }

    public void testForbidDuplicateTrades() {
        boolean threw = false;

        try {
            session.addTrade(trade);
            session.addTrade(trade);
        } catch (Exception ex) {
            threw = true;
        }

        assertTrue(threw);
    }

    public void testForbidOverlappingTrades() throws Exception {
        boolean threw = false;

        try {
            session = new Session(account, conditions);

            Trade trade1 = new Trade(asset, twoDaysAgo, null);
            Trade trade2 = new Trade(asset, yesterday, null);

            session.addTrade(trade1);
            session.addTrade(trade2);
        } catch (Exception ex) {
            threw = true;
        }

        assertTrue(threw);
    }

    public void testWithdrawingAndDepositingWithOpenTrades() throws Exception {
        boolean threw = false;

        account = new Account(new BigDecimal(100), openTrade.getOpen().minusDays(1));
        session = new Session(account, conditions);

        BigDecimal accountBalanceBefore = account.getCurrentAmount();

        session.addTrade(openTrade);

        BigDecimal accountBalanceAfter = account.getCurrentAmount();

        assertTrue(accountBalanceAfter.compareTo(accountBalanceBefore) < 0);
        assertEquals(new BigDecimal(89), accountBalanceAfter);

        session.closeLastTrade(today);
        assertEquals(new BigDecimal(98), account.getCurrentAmount());
    }

    public void testComputingProfitCurve() throws Exception {
        assertEquals(Arrays.asList(new BigDecimal[]{new BigDecimal(9), new BigDecimal(-16)}), session.getProfitCurve());
    }

    public void testComputingSharpeRatio() throws Exception {
        assertEquals(-0.198, session.sharpeRatio(), 0.0001);
    }
}
