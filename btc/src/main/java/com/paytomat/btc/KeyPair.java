package com.paytomat.btc;

import com.paytomat.btc.network.NetworkParams;

/**
 * created by Alex Ivanov on 2019-02-12.
 */
public class KeyPair {

    public final PrivateKey privateKey;
    public final PublicKey publicKey;

    public KeyPair(PrivateKey privateKey) {
        this.privateKey = privateKey;
        this.publicKey = privateKey.getPublicKey();
    }

    public Address getAddress(NetworkParams networkParams) {
        return publicKey.getAddress(networkParams);
    }
}
