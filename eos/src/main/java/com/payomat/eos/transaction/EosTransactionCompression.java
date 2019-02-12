package com.payomat.eos.transaction;

/**
 * created by Alex Ivanov on 2019-02-12.
 */
public enum EosTransactionCompression {
    NONE("none");

    private final String value;

    EosTransactionCompression(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
