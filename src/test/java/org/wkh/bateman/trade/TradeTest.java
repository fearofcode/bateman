package org.wkh.bateman.trade;

import java.math.BigDecimal;

import static junit.framework.Assert.*;

public class TradeTest extends AssetTest {

    Trade trade;
    Trade openTrade;
    Trade multipleShareTrade;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        trade = new Trade(asset, yesterday, today);
        openTrade = new Trade(asset, yesterday);
        multipleShareTrade = new Trade(asset, twoDaysAgo, yesterday, 500, TradeType.LONG, new Conditions(new BigDecimal(10), BigDecimal.ZERO));
    }

    public void testDontAllowIncorrectDates() {
        boolean threw = false;

        try {
            new Trade(asset, today, yesterday);
        } catch (Exception ex) {
            threw = true;
        }

        assertTrue(threw);
    }

    public void testOpenClosed() {
        assertFalse(trade.isOpen());
        assertTrue(trade.isClosed());

        assertTrue(openTrade.isOpen());
        assertFalse(openTrade.isClosed());
    }

    public void testCantPlaceTradeOnNonexistentData() {
        boolean threw = false;

        try {
            new Trade(asset, today.minusDays(3), yesterday);
        } catch (Exception ex) {
            threw = true;
        }

        assertTrue(threw);
    }

    public void testComputingTradePurchasePrice() throws Exception {
        trade = new Trade(asset, yesterday, null, 2, TradeType.LONG, Conditions.getZero());
        assertEquals(new BigDecimal(22), trade.getPurchasePrice());

        trade = new Trade(asset, yesterday, null, 2, TradeType.LONG, new Conditions(new BigDecimal(2), BigDecimal.ZERO));
        assertEquals(new BigDecimal(24), trade.getPurchasePrice());
    }

    public void testComputingTradeSellPrice() throws Exception {
        trade = new Trade(asset, yesterday, today, 2, TradeType.LONG, Conditions.getZero());

        assertEquals(new BigDecimal(18), trade.getSellPrice());

        // We are assuming that there is an entry fee and no exit fee

        trade = new Trade(asset, yesterday, today, 2, TradeType.LONG, new Conditions(new BigDecimal(2), BigDecimal.ZERO));

        assertEquals(new BigDecimal(18), trade.getSellPrice());
    }

    public void testCantCalculateProfitLossOnOpenTrade() {
        boolean threw = false;

        try {
            openTrade.profit();
        } catch (Exception ex) {
            threw = true;
        }

        assertTrue(threw);

    }

    // TODO These tests currently assumed we are unleveraged. Do we want to change this?
    // TODO These tests assume prices can move continuously. Share prices may be discrete.
    // TODO These tests assume we always make market orders only.
    public void testComputingProfitLossWithoutCommissionsOrSlippage() throws Exception {
        assertEquals(trade.profit(), new BigDecimal(-2));
        multipleShareTrade.setConditions(Conditions.getZero());
        assertEquals(multipleShareTrade.profit(), new BigDecimal(500));

        Trade losingMultipleShareTrade = new Trade(asset, yesterday, today, 500);
        assertEquals(losingMultipleShareTrade.profit(), new BigDecimal(-1000));
    }

    public void testShortProfit() throws Exception {
        Trade shortTrade = new Trade(asset, yesterday, today, 1, TradeType.SHORT, Conditions.getZero());
        // TODO: make sure this is actually the result we should expect
        assertEquals(shortTrade.profit(), new BigDecimal(2));
    }

    public void testComputingProfitLossWithCommissionsNoSlippage() throws Exception {
        assertEquals(multipleShareTrade.profit(), new BigDecimal(490));
    }

    public void testProfitLossWithCommissionAndSlippage() throws Exception {
        multipleShareTrade.setConditions(new Conditions(new BigDecimal(10), new BigDecimal(0.01)));
        assertEquals(multipleShareTrade.profit().doubleValue(), 385.0, 0.0001);
    }
}
