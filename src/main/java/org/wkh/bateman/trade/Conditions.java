package org.wkh.bateman.trade;

import java.math.BigDecimal;

public class Conditions {
    private BigDecimal commissions;
    private BigDecimal slippage;

    public Conditions(BigDecimal commissions, BigDecimal slippage) {
        this.commissions = commissions;
        this.slippage = slippage;
    }

    public BigDecimal getCommissions() {
        return commissions;
    }

    public BigDecimal getSlippage() {
        return slippage;
    }

    public static Conditions getZero() {
        return new Conditions(BigDecimal.ZERO, BigDecimal.ZERO);
    }
}
