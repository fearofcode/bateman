package org.wkh.bateman.trade.util;

import org.joda.time.DateTime;

/**
 * User: warrenhenning Date: 9/20/12 Time: 4:11 AM
 */
public class IndicatorParameter {

    private DateTime time;
    private int period;

    public IndicatorParameter(DateTime time, int period) {
        this.time = time;
        this.period = period;
    }

    public DateTime getTime() {
        return time;
    }

    public int getPeriod() {
        return period;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        IndicatorParameter that = (IndicatorParameter) o;

        if (period != that.period) {
            return false;
        }
        return time.equals(that.time);
    }

    @Override
    public int hashCode() {
        int result = time.hashCode();
        result = 31 * result + period;
        return result;
    }
}
