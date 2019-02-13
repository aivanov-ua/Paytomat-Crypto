package com.paytomat.eos;

/**
 * created by Alex Ivanov on 2019-02-11.
 */
public class KeyPair {

    public final String accountName;
    public final PublicKey publicKey;
    public final PrivateKey privateKey;

    public KeyPair(String accountName, PublicKey publicKey, PrivateKey privateKey) {
        this.accountName = accountName;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public KeyPair(String accountName, PrivateKey privateKey) {
        this(accountName, privateKey.toPublicKey(true), privateKey);
    }

}
