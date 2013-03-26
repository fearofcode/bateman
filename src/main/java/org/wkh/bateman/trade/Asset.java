package org.wkh.bateman.trade;

import org.joda.time.DateTime;

import java.math.BigDecimal;

public class Asset {

    private String symbol;
    private TimeSeries timeSeries;

    public Asset(String symbol, TimeSeries timeSeries) {
        this.symbol = symbol;
        this.timeSeries = timeSeries;
    }

    public BigDecimal priceAt(DateTime date) {
        return timeSeries.priceAt(date);
    }

    public TimeSeries getTimeSeries() {
        return timeSeries;
    }
}
