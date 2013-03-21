package org.wkh.bateman.trade;

import org.joda.time.DateTime;

/**
 * User: warrenhenning
 * Date: 8/31/12
 * Time: 5:23 AM
 */

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
