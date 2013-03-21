package org.wkh.bateman.trade;

import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.SortedMap;
import java.util.TreeMap;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import junit.framework.TestCase;

public class TimeSeriesTest extends TestCase {

    public DateTime today;
    public DateTime yesterday;
    public DateTime twoDaysAgo;
    public TreeMap<DateTime, BigDecimal> prices;
    public TimeSeries series;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        
        today = new DateTime();
        yesterday = today.minusDays(1);
        twoDaysAgo = today.minusDays(2);

        prices = new TreeMap<DateTime, BigDecimal>();
        prices.put(today, new BigDecimal(9.0));
        prices.put(yesterday, new BigDecimal(11.0));
        prices.put(twoDaysAgo, new BigDecimal(10.0));

        series = new TimeSeries(prices);
    }

    public void testPriceQuerying() throws Exception {
        assertEquals(new BigDecimal(11), series.priceAt(yesterday));
    }

    public void testSlicing() throws Exception {
        prices.put(today.plusDays(1), BigDecimal.ZERO);
        prices.put(today.plusDays(2), BigDecimal.ZERO);
        prices.put(today.plusDays(7), BigDecimal.ZERO);

        series = new TimeSeries(prices);

        SortedMap<DateTime, BigDecimal> slice = series.dateSlice(today.plusDays(1), today.plusDays(7));

        assertEquals(today.plusDays(1), slice.firstKey());
        assertTrue(slice.containsKey(today.plusDays(2)));
        assertEquals(today.plusDays(7), slice.lastKey());
        assertEquals(3, slice.keySet().size());
    }

    public void testGettingPreviousEntries() throws Exception {
        prices.put(today.plusDays(1), BigDecimal.ZERO);
        prices.put(today.plusDays(2), BigDecimal.ONE);

        assertEquals(series.previousValues(today.plusDays(2), 4),
                Arrays.asList(new BigDecimal[] { BigDecimal.ONE, BigDecimal.ZERO, new BigDecimal(9.0), new BigDecimal(11.0) } ));
    }

    public void testSimpleMovingAverage() {
        assertEquals(new BigDecimal(10.0), series.simpleMovingAverage(today, 2));

        assertEquals(new BigDecimal(10.5), series.simpleMovingAverage(yesterday, 2));

        assertEquals(new BigDecimal(10.0), series.simpleMovingAverage(today, 3));
    }

    public void testMax() {
        assertEquals(new BigDecimal(11.0), series.max(today, 2));
        prices.put(today.plusDays(1), new BigDecimal(14));
        assertEquals(new BigDecimal(14), series.max(today.plusDays(1), 2));
    }

    public void testMin() {
        assertEquals(new BigDecimal(9.0), series.min(today, 2));
        prices.put(today.plusDays(1), new BigDecimal(5));
        assertEquals(new BigDecimal(5), series.min(today.plusDays(1), 4));
    }
}
