package org.wkh.bateman.trade;

import java.math.BigDecimal;
import static junit.framework.Assert.assertEquals;

public class AssetTest extends TimeSeriesTest {

    public Asset asset;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        asset = new Asset("QQQQ", series);
    }

    public void testQuerying() {

        assertEquals(new BigDecimal(11.0), asset.priceAt(today.minusDays(1)));
    }
}
