package org.wkh.bateman.quote;

import java.util.List;

public interface QuoteFetcher {
    String fetchQuotes(String symbol, int days, int interval) throws Exception;

    List<Quote> parseQuotes(String quoteList, int interval);    
}
