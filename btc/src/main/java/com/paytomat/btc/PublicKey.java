package com.paytomat.btc;

import com.paytomat.btc.network.NetworkParams;
import com.paytomat.core.util.ByteSerializer;
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
            pubKeyHash = HashUtil.sha256ripemd160(publicKey);
        }
        return pubKeyHash;
    }

    public Address getAddress(NetworkParams networkParams, boolean isP2SH) {
        if (address == null || address.isP2PKH() == isP2SH) {
            if (isP2SH) {
                byte[] program = ByteSerializer.create().write(new byte[]{0x00, 0x14}).write(getPubKeyHash()).serialize();
                byte[] hash = HashUtil.sha256ripemd160(program);
                address = new Address(hash, networkParams, true);
            } else {
                address = new Address(getPubKeyHash(), networkParams, false);
            }
        }
        return address;
    }

    @Override
    public String toString() {
        return Hex.toHexString(publicKey);
    }

}
