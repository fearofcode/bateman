package org.wkh.bateman.fetch;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;

public class GoogleQuoteFetcher extends QuoteFetcher {

    @Override
    public String fetchQuotes(String symbol, int days, int interval)
            throws Exception {

        String url = "http://www.google.com/finance/getprices?i=" + interval
                + "&p=" + days + "d&f=d,o,h,l,c,v&df=cpct&q=" + symbol;

        return fetchURLasString(url);
    }

    @Override
    public List<Quote> parseQuotes(String quoteList, int interval) {
        String[] lines = dropLines(quoteList, 6);

        List<Quote> quotes = new ArrayList<Quote>();

        for (String line : lines) {
            if (line.startsWith("TIMEZONE_OFFSET")) {
                continue;
            }

            String[] parts = line.split(",");

            String dateStr = parts[0];

            DateTime date;

            if (dateStr.startsWith("a")) {
                final String intPart = dateStr.substring(1);
                final int timestamp = Integer.parseInt(intPart);
                date = new DateTime((long) timestamp * 1000L);
            } else {
                DateTime previousDate = quotes.get(quotes.size() - 1).getOpenDate();
                date = previousDate.plusSeconds(interval);
            }

            Quote quote = new Quote(date,
                    interval,
                    new BigDecimal(parts[4]),
                    new BigDecimal(parts[2]),
                    new BigDecimal(parts[3]),
                    new BigDecimal(parts[1]),
                    Integer.parseInt(parts[5]));

            quotes.add(quote);
        }

        return quotes;
    }
}
