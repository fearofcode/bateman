package org.wkh.bateman.trade;

import static junit.framework.Assert.assertEquals;
import java.math.BigDecimal;

public class MoneyManagementStrategyTest extends RuleTest {

    public void testFixedPercentage() throws Exception {
        FixedPercentageAllocationStrategy strategy = new FixedPercentageAllocationStrategy(0.2, asset);
        assertEquals(20, strategy.sizePosition(account, today.minusDays(5)));

        account = new Account(new BigDecimal(9), today.minusDays(6));
        assertEquals(0, strategy.sizePosition(account, today.minusDays(5)));
    }
}
