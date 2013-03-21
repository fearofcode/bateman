package org.wkh.bateman.quote;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SymbolCollectionFetcher {
    private static Logger logger = LoggerFactory.getLogger(SymbolCollectionFetcher.class.getName()); 
    
    public static Map<String, List<Quote>> fetchAllQuotes(QuoteFetcher fetcher) throws Exception {
        Map<String, List<Quote>> quotes = new HashMap<String, List<Quote>>();
        
        logger.info("Starting quote fetching");
        
        Properties properties = new Properties();
        properties.load(ClassLoader.getSystemResourceAsStream("application.properties"));
        
        String symbolPath = properties.getProperty("symbolPath");
        int days = Integer.parseInt(properties.getProperty("days"));
        int interval = Integer.parseInt(properties.getProperty("interval"));
        
        Path paths = Paths.get(symbolPath);
        List<String> symbols = Files.readAllLines(paths, StandardCharsets.UTF_8);
        
        for(String symbol : symbols) {
            symbol = symbol.replaceAll("\\s","");
            
            logger.info("Fetching quotes for " + symbol);
            List<Quote> quoteList = fetcher.parseQuotes(fetcher.fetchQuotes(symbol, days, interval), interval);
            quotes.put(symbol, quoteList);
        }
        
        logger.info("Done fetching quotes");
        
        return quotes;
    } 
    
     public static Map<String, List<Quote>> fetchAllGoogleQuotes() throws Exception {
         return fetchAllQuotes(new GoogleQuoteFetcher());
     }
}
