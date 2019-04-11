package com.paytomat.tezos.model;

/**
 * created by Alex Ivanov on 2019-04-11.
 */
public class FeeModel {

    public final Tez fee;
    public final Tez gasLimit;
    public final Tez storageLimit;

    public FeeModel(Tez fee, Tez gasLimit, Tez storageLimit) {
        this.fee = fee;
        this.gasLimit = gasLimit;
        this.storageLimit = storageLimit;
    }
}
