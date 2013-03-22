package org.wkh.bateman.trade;

import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.Map;
import java.util.SortedMap;

public abstract class Rule {
    private Account account;
    private Asset asset;
    private Conditions conditions;
    private MoneyManagementStrategy moneyManager;

    protected Rule(Account account, Asset asset, Conditions conditions, MoneyManagementStrategy moneyManager) {
        this.account = account;
        this.asset = asset;
        this.conditions = conditions;
        this.moneyManager = moneyManager;
    }

    public abstract boolean buy(DateTime time, Session session);

    public abstract boolean sell(DateTime time, Session session);

    public synchronized Session generateSignals(DateTime start, DateTime end) throws Exception {
        Session session = new Session(account, conditions);

        SortedMap<DateTime, BigDecimal> slice = asset.getTimeSeries().dateSlice(start, end);


        for (Map.Entry<DateTime, BigDecimal> kv : slice.entrySet()) {
            DateTime time = kv.getKey();

            boolean doBuy = buy(time, session);
            boolean doSell = sell(time, session);
            if (doBuy || doSell) {
                if (!processSignal(session, time, doBuy, end)) break;
            }
        }

        if (session.getTrades().size() > 0) {
            Trade lastTrade = session.lastTrade();

            if (lastTrade.isOpen()) {
                lastTrade.setClose(end);
                session.tabulateClosedTrade(lastTrade);
            }
        }

        return session;
    }

    private boolean processSignal(Session session, DateTime time, boolean buy, DateTime end) throws Exception {
        /* If we have an open trade, close it out if the signal is the opposite of our current direction */
        if (session.inMarket(time)) {
            Trade lastTrade = session.lastTrade();

            boolean sell = sell(time, session);

            if ((sell && lastTrade.getType() == TradeType.LONG) || (buy && lastTrade.getType() == TradeType.SHORT)) {
                session.closeLastTrade(time);
            }
        } else {
            /* Open a new trade at the current time in the indicated direction */
            if (!makeSizedTrade(session, time, buy ? TradeType.LONG : TradeType.SHORT, end)) return false;
        }

        return true;
    }

    private boolean makeSizedTrade(Session session, DateTime time, TradeType type, DateTime end) throws Exception {
        // not going to open a trade on the last day of the session
        if(time.compareTo(end) >= 0)
            return false;

        int size = moneyManager.sizePosition(account, time);

        /* If we can't buy anything, we can't trade anymore, so stop the session */
        if (size == 0) {
            return false;
        }

        Trade trade = new Trade(asset, time, null, size, type, conditions);
        session.addTrade(trade);
        return true;
    }
}
