package com.paytomat.eos.transaction;

/**
 * created by Alex Ivanov on 2019-02-12.
 */
public enum EosActionAuthorizationPermission {
    ACTIVE("active"),
    OWNER("owner");


    private final String value;

    EosActionAuthorizationPermission(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}