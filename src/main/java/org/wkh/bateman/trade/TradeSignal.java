package org.wkh.bateman.trade;

import org.joda.time.DateTime;

public class TradeSignal {
    private TradeType type;
    private DateTime date;

    public TradeSignal(DateTime date, TradeType type) {
        this.type = type;
        this.date = date;
    }

    public TradeType getType() {
        return type;
    }

    public DateTime getDate() {
        return date;
    }
}
