package org.wkh.bateman.fetch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;
import org.wkh.bateman.trade.TimeSeries;

public class GoogleQuoteFetcher implements QuoteFetcher {
    @Override
    public String fetchQuotes(String symbol, int days, int interval) 
            throws Exception {
        
        String url = "http://www.google.com/finance/getprices?i=" + interval + 
                "&p=" + days + "d&f=d,o,h,l,c,v&df=cpct&q=" + symbol;
        
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);

        HttpResponse response = httpclient.execute(httpGet);

        HttpEntity entity = response.getEntity();
        
        String body = EntityUtils.toString(entity);
        EntityUtils.consume(entity);
    
        httpGet.releaseConnection();
        
        return body;
    }
    
    @Override
    public List<Quote> parseQuotes(String quoteList, int interval) {
        final int dropLines = 6;
        
        String[] lines = quoteList.split("\n");
        
        lines = Arrays.copyOfRange(lines, dropLines, lines.length);
        
        List<Quote> quotes = new ArrayList<Quote>();
        
        for(String line: lines)
        {
            if(line.startsWith("TIMEZONE_OFFSET")) {
                continue;
            }
            
            String[] parts = line.split(",");
            
            String dateStr = parts[0];
            
            DateTime date;
            
            if(dateStr.startsWith("a")) {
                final String intPart = dateStr.substring(1);
                final int timestamp = Integer.parseInt(intPart);
                date = new DateTime((long)timestamp*1000L);
            } else {
                DateTime previousDate = quotes.get(quotes.size()-1).getOpenDate();
                date = previousDate.plusSeconds(interval);
            }
            
            Quote quote = new Quote(date, 
                    interval, 
                    Double.parseDouble(parts[4]), 
                    Double.parseDouble(parts[2]), 
                    Double.parseDouble(parts[3]), 
                    Double.parseDouble(parts[1]), 
                    Integer.parseInt(parts[5]));
            
            quotes.add(quote);
        }
        
        return quotes;
    }
    
    public TimeSeries fetchAndParse(String symbol, int days, int interval) throws Exception {
        String requestResult = fetchQuotes(symbol, days, interval);
        List<Quote> parsed = parseQuotes(requestResult, interval);
        
        QuoteCollection qc = new QuoteCollection();
        
        return qc.convertQuoteToTimeSeries(parsed);
    }
}
