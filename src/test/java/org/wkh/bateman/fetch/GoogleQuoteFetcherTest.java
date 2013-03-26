package org.wkh.bateman.fetch;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import static junit.framework.Assert.assertEquals;
import junit.framework.TestCase;

public class GoogleQuoteFetcherTest extends TestCase {
    public void testParseQuotes() throws Exception {
        String samplePath = "src/main/resources/sample_google_response.csv";
        
        Scanner scan = new Scanner(new File(samplePath));  
        scan.useDelimiter("\\Z");  
        String sampleResponse = scan.next().replaceAll("\r\n", "\n"); 
        
        GoogleQuoteFetcher fetcher = new GoogleQuoteFetcher();
        
        List<Quote> quotes = fetcher.parseQuotes(sampleResponse, 60);
        
        Quote firstQuote = quotes.get(0);
        assertEquals(firstQuote.getOpen(), new BigDecimal("444.05"));
        assertEquals(firstQuote.getHigh(), new BigDecimal("444.19"));
        assertEquals(firstQuote.getLow(), new BigDecimal("443.8"));
        assertEquals(firstQuote.getClose(), new BigDecimal("443.8"));
        assertEquals(firstQuote.getVolume(), 78179);
        assertEquals(firstQuote.getOpenDate().getMillis(), 1362061800000L);
        
        assertEquals(quotes.size(), 5873);
        
    }
}
