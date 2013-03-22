package org.wkh.bateman.trade;

import org.wkh.bateman.trade.util.IndicatorParameter;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.*;

public class TimeSeries {
    private TreeMap<DateTime, BigDecimal> prices;
    private HashMap<IndicatorParameter, List<BigDecimal>> previousPriceCache;
    private HashMap<IndicatorParameter, BigDecimal> smaCache;
    private HashMap<IndicatorParameter, Double> rocCache;
    private HashMap<IndicatorParameter, BigDecimal> maxCache;
    private HashMap<IndicatorParameter, BigDecimal> minCache;

    public TimeSeries(TreeMap<DateTime, BigDecimal> prices) throws Exception {
        this.prices = prices;
        this.previousPriceCache = new HashMap<IndicatorParameter, List<BigDecimal>>();
        this.smaCache = new HashMap<IndicatorParameter, BigDecimal>();
        this.rocCache = new HashMap<IndicatorParameter, Double>();
        this.maxCache = new HashMap<IndicatorParameter, BigDecimal>();
        this.minCache = new HashMap<IndicatorParameter, BigDecimal>();
    }
    
    public BigDecimal openOnDay(DateTime date) {
        DateTime midnight = date.toDateMidnight().toDateTime();
        NavigableMap<DateTime, BigDecimal> previousPrices = prices.subMap(midnight, true, date, true);
        return previousPrices.firstEntry().getValue();
    }
    
    public BigDecimal closeOnDay(DateTime date) {
        DateTime midnight = date.toDateMidnight().toDateTime();
        DateTime nextDay = midnight.plusDays(1);
        return prices.floorEntry(nextDay).getValue();
    }
    
    public BigDecimal priceAt(DateTime date) {
        return prices.get(date);
    }

    public boolean hasPriceAt(DateTime date) {
        return prices.containsKey(date);
    }

    public DateTime beginningOfSeries() {
        return prices.firstKey();
    }

    public DateTime lastOfSeries() {
        return prices.lastKey();
    }

    public SortedMap<DateTime, BigDecimal> dateSlice(DateTime startPoint, DateTime endPoint) {
        return prices.subMap(startPoint, true, endPoint, true);
    }

    public TreeMap<DateTime, BigDecimal> getPrices() {
        return prices;
    }

    void removeDays(int i) {
        DateTime first = beginningOfSeries();
        DateTime midnight = first.toDateMidnight().toDateTime();
        DateTime cutoff = midnight.plusDays(i);
        
        prices = new TreeMap<DateTime, BigDecimal>(prices.tailMap(cutoff));
    }

}

