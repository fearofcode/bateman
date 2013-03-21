package org.wkh.bateman.trade;

import org.wkh.bateman.trade.util.IndicatorParameter;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * User: warrenhenning
 * Date: 8/31/12
 * Time: 12:54 AM
 */

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

    public synchronized List<BigDecimal> previousValues(DateTime time, int period) {
        IndicatorParameter ip = new IndicatorParameter(time, period);

        if(previousPriceCache.containsKey(ip))
            return previousPriceCache.get(ip);

        for(DateTime dt : prices.keySet()) {
            SortedMap<DateTime, BigDecimal> slice = dateSlice(dt, time);

            if(slice.size() == period) {
                ArrayList<BigDecimal> priceList = new ArrayList<BigDecimal>(slice.values());
                Collections.reverse(priceList);

                previousPriceCache.put(ip, priceList);

                return priceList;
            }
        }

        return null;
    }

    public synchronized BigDecimal simpleMovingAverage(DateTime time, int period) {
        IndicatorParameter ip = new IndicatorParameter(time, period);

        if(smaCache.containsKey(ip))
            return smaCache.get(ip);

        List<BigDecimal> values = previousValues(time, period);

        BigDecimal sum = BigDecimal.ZERO;

        for(BigDecimal value: values)
            sum = sum.add(value);

        try {
            BigDecimal sma = sum.divide(new BigDecimal(period));
            smaCache.put(ip, sma);
            return sma;
        } catch(ArithmeticException e) {
            BigDecimal sma = sum.divide(new BigDecimal(period), 3, RoundingMode.HALF_UP);
            smaCache.put(ip, sma);
            return sma;
        }

    }

    public synchronized BigDecimal max(DateTime time, int period) {
        IndicatorParameter ip = new IndicatorParameter(time, period);

        if(maxCache.containsKey(ip))
            return maxCache.get(ip);

        List<BigDecimal> values = previousValues(time, period);

        BigDecimal ipMax = Collections.max(values);

        maxCache.put(ip, ipMax);

        return ipMax;
    }

    public synchronized BigDecimal min(DateTime time, int period) {
        IndicatorParameter ip = new IndicatorParameter(time, period);

        if(minCache.containsKey(ip))
            return minCache.get(ip);

        List<BigDecimal> values = previousValues(time, period);

        BigDecimal ipMin = Collections.min(values);

        minCache.put(ip, ipMin);

        return ipMin;
    }

    public synchronized double roc(DateTime date, int period) {
        IndicatorParameter ip = new IndicatorParameter(date, period);

        if(rocCache.containsKey(ip))
            return rocCache.get(ip);

        BigDecimal oldPrice = previousValues(date, period).get(period-1);
        BigDecimal currentPrice = priceAt(date);

        double ipRoc = currentPrice.subtract(oldPrice).divide(oldPrice.equals(BigDecimal.ZERO) ? new BigDecimal(0.001) : oldPrice, 2, RoundingMode.HALF_UP).doubleValue();

        rocCache.put(ip, ipRoc);
        return ipRoc;
    }
}

