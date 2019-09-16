package com.paytomat.btc.transaction;

/**
 * created by Alex Ivanov on 2019-09-13.
 */
public abstract class Message {

    public abstract byte[] serialize();

    public abstract int getMessageSize();
}
