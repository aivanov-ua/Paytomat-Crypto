package com.paytomat.btc.transaction;

/**
 * created by Alex Ivanov on 7/19/18.
 */
public class Output {

    public final long value;
    public final Script script;

    public Output(long value, Script script) {
        this.value = value;
        this.script = script;
    }

}
