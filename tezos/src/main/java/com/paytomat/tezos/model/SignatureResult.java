package com.paytomat.tezos.model;

import org.bouncycastle.util.encoders.Hex;

/**
 * created by Alex Ivanov on 2019-04-10.
 */
public class SignatureResult {

    public final String hashedOperation;
    public final String signature;
    public final String edsig;
    public final String sBytes;

    public SignatureResult(byte[] hashedOperation, byte[] signature, String edsig, String sBytes) {
        this.hashedOperation = Hex.toHexString(hashedOperation);
        this.signature = Hex.toHexString(signature);
        this.edsig = edsig;
        this.sBytes = sBytes;
    }
}
