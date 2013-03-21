package org.wkh.bateman.trade;

import org.joda.time.DateTime;
import static junit.framework.Assert.assertEquals;
import junit.framework.TestCase;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AccountTest extends TestCase {
    Account account;

    public AccountTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        account = new Account(new BigDecimal(100000), DateTime.now());
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testValue() {
        assertEquals(account.getCurrentAmount(), new BigDecimal(100000));
    }

    public void testProfitLoss() throws Exception {
        account.profit(new BigDecimal(1000), DateTime.now().plusMillis(1));
        assertEquals(account.getCurrentAmount(), new BigDecimal(101000));

        account.lose(new BigDecimal(500), DateTime.now().plusMillis(2));
        assertEquals(account.getCurrentAmount(), new BigDecimal(100500));
    }

    public void testWithdraw() throws Exception {
        account.withdraw(new BigDecimal(10000), DateTime.now().plusSeconds(1));
        assertEquals(account.getCurrentAmount(), new BigDecimal(90000));
    }

    
    public void testOverdraw() {
        boolean threw = false;
        try {
            account.lose(new BigDecimal(100001), DateTime.now());
        } catch (Exception ex) {
            threw = true;
        }
        
        assertTrue(threw);
    }

    public void testEquityCurve() throws Exception {
        account.profit(new BigDecimal(1), DateTime.now().plusSeconds(1));
        account.lose(new BigDecimal(1), DateTime.now().plusSeconds(2));
        final Object[] actual = account.getEquityCurve().values().toArray();
        final Object[] expected = Arrays.asList(new BigDecimal[]{new BigDecimal(100000),
                     new BigDecimal(100001), new BigDecimal(100000)}).toArray();

        assertEquals(actual[0], expected[0]);
        assertEquals(actual[1], expected[1]);
        assertEquals(actual[2], expected[2]);
    }
}
