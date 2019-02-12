package com.paytomat.btc.transaction;

/**
 * created by Alex Ivanov on 7/18/18.
 */
public class ZenScript extends Script {

    public ZenScript(byte[] bytes) {
        super(bytes);
    }

    public ZenScript(byte[] data1, byte[] data2) {
        super(data1, data2);
    }

    @Override
    boolean isPay2PublicKeyHash() {
        return bytes.length > 28 &&
                bytes[0] == OP_DUP &&
                bytes[1] == OP_HASH160 &&
                bytes[2] == 20;
    }

    @Override
    boolean isPubkey() {
        return bytes.length > 2 &&
                ScriptHelper.getScriptTokenLengthAt(bytes, 0) == bytes.length - 1 &&
                bytes[24] == OP_CHECKSIG;
    }

    @Override
    boolean isPayToScriptHash() {
        return bytes.length > 23 &&
                bytes[0] == OP_HASH160 &&
                bytes[1] == 0x14 &&
                bytes[22] == OP_EQUAL;
    }
}