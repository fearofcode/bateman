package org.wkh.bateman.trade;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import static junit.framework.Assert.assertEquals;
import junit.framework.TestCase;
import org.joda.time.DateTime;

public class QuoteCollectionTest extends TestCase {
    
    public QuoteCollectionTest(String testName) {
        super(testName);
    }
    
    public void testConvertQuoteToTimeSeries() throws Exception {
        DateTime open = new DateTime();
        DateTime later = open.plusMinutes(1);
        List<Quote> quotes = Arrays.asList(new Quote(open, 60, 1.0, 1.2, 0.9, 1.1, 42), 
                new Quote(later, 60, 1.3, 1.8, 0.7, 1.5, 43));
        
        QuoteCollection collection = new QuoteCollection();
        TreeMap<DateTime, BigDecimal> prices = new TreeMap<DateTime, BigDecimal>();
        prices.put(open, BigDecimal.ONE);
        prices.put(later, new BigDecimal(1.3));
        
        TimeSeries expResult = new TimeSeries(prices);
        TimeSeries result = collection.convertQuoteToTimeSeries(quotes);
        
        assertEquals(expResult.getPrices().get(open), result.getPrices().get(open));
        assertEquals(expResult.getPrices().get(later), result.getPrices().get(later));
    }
}
