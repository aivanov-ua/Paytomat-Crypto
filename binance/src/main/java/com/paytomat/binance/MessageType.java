package com.paytomat.binance;

import org.bouncycastle.util.encoders.Hex;

/**
 * created by Alex Ivanov on 2019-05-14.
 */
public enum MessageType {

    Send("2A2C87FA"),
    StdSignature(null),
    PubKey("EB5AE987"),
    StdTx("F0625DEE");

    private byte[] typePrefixBytes;

    MessageType(String typePrefix) {
        if (typePrefix == null) {
            this.typePrefixBytes = new byte[0];
        } else
            this.typePrefixBytes = Hex.decode(typePrefix);
    }

    public byte[] getTypePrefixBytes() {
        return typePrefixBytes;
    }
}
