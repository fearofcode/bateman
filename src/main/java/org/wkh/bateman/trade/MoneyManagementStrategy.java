package org.wkh.bateman.trade;

import org.joda.time.DateTime;

public interface MoneyManagementStrategy {
    public int sizePosition(Account account, DateTime time);
}
