package org.wkh.bateman.trade;

import org.joda.time.DateTime;

public abstract class MoneyManagementStrategy {
    public abstract int sizePosition(Account account, DateTime time);
}
