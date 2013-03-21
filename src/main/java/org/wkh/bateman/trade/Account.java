package org.wkh.bateman.trade;

import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.TreeMap;

public class Account {
    private TreeMap<DateTime, BigDecimal> equityCurve;
    private BigDecimal currentAmount;

    public Account(BigDecimal startingAmount, DateTime time) throws Exception {
        this.currentAmount = startingAmount;
        equityCurve = new TreeMap<DateTime, BigDecimal>();

        writeToEquityCurve(time);
    }

    private void complainIfOverwriting(DateTime time) throws Exception {
        if(equityCurve.containsKey(time)) {
            throw new Exception("Cannot overwrite an existing price: " + time);
        }
    }

    public TreeMap<DateTime, BigDecimal>  getEquityCurve() {
        return equityCurve;
    }

    public BigDecimal valueAtTime(DateTime time) {
        return equityCurve.get(time);
    }

    public BigDecimal getCurrentAmount() {
        return equityCurve.lastEntry().getValue();
    }

    public void profit(BigDecimal amount, DateTime time) throws Exception {
        currentAmount = currentAmount.add(amount);

        writeToEquityCurve(time);
    }

    private void writeToEquityCurve(DateTime time) throws Exception {
        complainIfOverwriting(time);

        equityCurve.put(time, currentAmount);
    }

    public void lose(BigDecimal amount, DateTime time) throws Exception {
        if (currentAmount.subtract(amount).compareTo(BigDecimal.ZERO) == -1) {
            throw new Exception("Account is overdrawn");
        }

        currentAmount = currentAmount.subtract(amount);

        writeToEquityCurve(time);
    }

    public void withdraw(BigDecimal amount, DateTime time) throws Exception {
        lose(amount, time);
    }
}
