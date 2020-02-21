package com.paytomat.btc.model;

/**
 * created by Alex Ivanov on 2020-02-20.
 */
public class UtxoModel {

    public final String value;
    public final long outputIndex;
    public final String subScriptHex;
    public final String address;
    public final String txHex;

    public UtxoModel(String value, long outputIndex, String subScriptHex, String address, String txHex) {
        this.value = value;
        this.outputIndex = outputIndex;
        this.subScriptHex = subScriptHex;
        this.address = address;
        this.txHex = txHex;
    }
}
