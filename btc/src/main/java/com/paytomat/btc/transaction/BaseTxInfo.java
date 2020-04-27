package com.paytomat.btc.transaction;

import java.util.List;

/**
 * created by Alex Ivanov on 7/19/18.
 */
public class BaseTxInfo {
    final long amountForRecipient, change, fee;
    final List<UnspentOutputInfo> outputsToSpend;

    public BaseTxInfo(long fee, long change, long amountForRecipient, List<UnspentOutputInfo> outputsToSpend) {
        this.fee = fee;
        this.change = change;
        this.amountForRecipient = amountForRecipient;
        this.outputsToSpend = outputsToSpend;
    }
}
