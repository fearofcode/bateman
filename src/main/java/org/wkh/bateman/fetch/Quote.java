package org.wkh.bateman.fetch;

import org.joda.time.DateTime;

public class Quote {
    private DateTime openDate;
    private int interval;
    private double open;
    private double high;
    private double low;
    private double close;
    private int volume;
    
    public Quote(DateTime openDate, int interval, double open, double high, double low, double close, int volume) {
        this.openDate = openDate;
        this.interval = interval;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }

    @Override
    public String toString() {
        return "Date = " + openDate + ", OHLC = " + open + "/" + high + "/" + 
                low + "/" + close + ", Volume = " + volume;
    }
    
    public DateTime getOpenDate() {
        return openDate;
    }

    public int getInterval() {
        return interval;
    }

    public double getOpen() {
        return open;
    }

    public double getHigh() {
        return high;
    }

    public double getLow() {
        return low;
    }

    public double getClose() {
        return close;
    }

    public int getVolume() {
        return volume;
    }
}
