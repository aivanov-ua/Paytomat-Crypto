package com.paytomat.btc;

import com.paytomat.btc.network.NetworkParams;
import com.paytomat.core.util.HashUtil;

import org.bouncycastle.util.encoders.Hex;

/**
 * created by Alex Ivanov on 2019-02-12.
 */
public class PublicKey {

    private final byte[] publicKey;
    private byte[] pubKeyHash = null;
    private Address address = null;

    public PublicKey(byte[] publicKey) {
        this.publicKey = publicKey;
    }

    public byte[] getBytes() {
        return publicKey;
    }

    public byte[] getPubKeyHash() {
        if (pubKeyHash == null) {
            pubKeyHash = HashUtil.addressHash(publicKey);
        }
        return pubKeyHash;
    }

    public Address getAddress(NetworkParams networkParams) {
        if (address == null) {
            address = new Address(getPubKeyHash(), networkParams);
        }
        return address;
    }

    @Override
    public String toString() {
        return Hex.toHexString(publicKey);
    }

}
