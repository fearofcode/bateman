package org.wkh.bateman.trade;

import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class FixedPercentageAllocationStrategy implements MoneyManagementStrategy {
    private BigDecimal ratio;
    private Asset asset;
    private final MathContext mc = new MathContext(10, RoundingMode.DOWN);

    public FixedPercentageAllocationStrategy(double ratio, Asset asset) throws Exception {
        if (ratio > 1.0) {
            throw new Exception("Cannot risk more than all of account");
        } else if (ratio <= 0.0) {
            throw new Exception("Ratio must be > 0");
        }
        this.ratio = new BigDecimal(ratio);
        this.asset = asset;
    }

    @Override
    public int sizePosition(Account account, DateTime time) {
        BigDecimal price = asset.priceAt(time);
        BigDecimal allocated = account.getCurrentAmount().multiply(ratio);
        BigDecimal shares = allocated.divide(price, 2, RoundingMode.HALF_UP);
        int i = shares.round(mc).intValue();

        return i;
    }
}