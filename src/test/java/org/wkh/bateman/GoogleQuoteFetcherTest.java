package org.wkh.bateman;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import static junit.framework.Assert.assertEquals;
import junit.framework.TestCase;

public class GoogleQuoteFetcherTest extends TestCase {
    
    public GoogleQuoteFetcherTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testParseQuotes() throws Exception {
        String samplePath = "src/main/resources/sample_response.txt";
        
        Scanner scan = new Scanner(new File(samplePath));  
        scan.useDelimiter("\\Z");  
        String sampleResponse = scan.next(); 
        
        GoogleQuoteFetcher fetcher = new GoogleQuoteFetcher();
        
        List<Quote> quotes = fetcher.parseQuotes(sampleResponse, 60);
        
        Quote firstQuote = quotes.get(0);
        assertEquals(firstQuote.getOpen(), 444.05);
        assertEquals(firstQuote.getHigh(), 444.19);
        assertEquals(firstQuote.getLow(), 443.8);
        assertEquals(firstQuote.getClose(), 443.8);
        assertEquals(firstQuote.getVolume(), 78179);
        assertEquals(firstQuote.getOpenDate().getTime(), 1362061800000L);
        
        assertEquals(quotes.size(), 5873);
        
    }
}
