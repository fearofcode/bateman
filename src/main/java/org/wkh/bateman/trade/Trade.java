package org.wkh.bateman.trade;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.math.BigDecimal;

// TODO this doesn't support stop losses
public class Trade {

    private Asset asset;
    private DateTime open;
    private DateTime close;
    private int size;
    private Conditions conditions;

    public TradeType getType() {
        return type;
    }
    private TradeType type;

    public Trade(Asset asset, DateTime open) throws Exception {
        this(asset, open, null);
    }

    public Trade(Asset asset, DateTime open, DateTime close) throws Exception {
        this(asset, open, close, 1);
    }

    public Trade(Asset asset, DateTime open, DateTime close, int size) throws Exception {
        this(asset, open, close, size, TradeType.LONG, new Conditions(BigDecimal.ZERO, BigDecimal.ZERO));
    }

    public Trade(Asset asset, DateTime open, DateTime close, int size, TradeType type, Conditions conditions) throws Exception {
        this.asset = asset;
        this.open = open;
        this.close = close;
        this.size = size;
        this.type = type;
        this.conditions = conditions;

        if (!asset.getTimeSeries().hasPriceAt(open) || (isClosed() && !asset.getTimeSeries().hasPriceAt(close))) {
            throw new Exception("Cannot place trades at dates for which no data exists");
        }

        if (isClosed() && (open.compareTo(close) >= 0)) {
            throw new Exception("Open must come before close and existing for a non-zero amount of time");
        }
    }

    public void setOpen(DateTime open) {
        this.open = open;
    }

    public void setClose(DateTime close) throws Exception {
        if (close.compareTo(open) <= 0) {
            throw new Exception("Trades must be closed after the open, not before or at the same instant");
        }

        this.close = close;
    }

    public DateTime getOpen() {
        return open;
    }

    public DateTime getClose() {
        return close;
    }

    public Asset getAsset() {
        return asset;
    }

    public int getSize() {
        return size;
    }

    public void setConditions(Conditions conditions) {
        this.conditions = conditions;
    }

    public boolean includesDate(DateTime time) {
        if (isClosed()) {
            return new Interval(open, close).contains(time);
        } else {
            return open.compareTo(time) <= 0;
        }
    }

    public boolean isOpen() {
        return close == null;
    }

    public boolean isClosed() {
        return close != null;
    }

    public BigDecimal openPrice() {
        return asset.priceAt(open);
    }

    public BigDecimal closePrice() {
        return asset.priceAt(close);
    }

    // TODO this doesn't account for borrowing costs/interest when shorting
    public BigDecimal profit() throws Exception {
        if (isOpen()) {
            throw new Exception("Can't calculate profit/loss of an open trade");
        }

        BigDecimal sellPrice = getSellPrice();
        BigDecimal purchasePrice = getPurchasePrice();
        return type == TradeType.LONG ? sellPrice.subtract(purchasePrice) : purchasePrice.subtract(sellPrice);
    }

    public BigDecimal getPurchasePrice() {
        BigDecimal basePurchasePrice = openPrice().add(openPrice().multiply(conditions.getSlippage()));
        return basePurchasePrice.multiply(new BigDecimal(size)).add(conditions.getCommissions());
    }

    public BigDecimal getSellPrice() {
        BigDecimal baseClosePrice = closePrice().subtract(closePrice().multiply(conditions.getSlippage()));
        return baseClosePrice.multiply(new BigDecimal(size));
    }
}
