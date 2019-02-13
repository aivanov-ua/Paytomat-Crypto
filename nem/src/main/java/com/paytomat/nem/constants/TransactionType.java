package com.paytomat.nem.constants;

/**
 * created by Alex Ivanov on 6/1/18.
 */
public class TransactionType {

    public static final TransactionType TRANSFER_TRANSACTION = new TransactionType(0x101);

    public final int value;

    private TransactionType(int value) {
        this.value = value;
    }

}
