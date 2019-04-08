package com.paytomat.tezos;

import com.paytomat.core.util.Base58;
import com.paytomat.core.util.ByteSerializer;
import com.paytomat.core.util.BytesUtil;

import org.bouncycastle.math.ec.rfc8032.Ed25519;


/**
 * created by Alex Ivanov on 2019-04-08.
 */
public class SecretKey {

    private final byte[] privateKey;

    public SecretKey(byte[] privateKeyBytes) {
        if (privateKeyBytes.length == 32) {
            byte[] pubKey = new byte[32];
            Ed25519.generatePublicKey(privateKeyBytes, 0, pubKey, 0);

            this.privateKey = new byte[64];
            System.arraycopy(privateKeyBytes, 0, this.privateKey, 0, 32);
            System.arraycopy(pubKey, 0, this.privateKey, 32, 32);
        } else if (privateKeyBytes.length == 64) {
            this.privateKey = privateKeyBytes;
        } else if (privateKeyBytes.length == 68 && BytesUtil.startsWith(privateKeyBytes, Constants.SECRET_KEY_PREFIX)) {
            this.privateKey = BytesUtil.removeFromStart(privateKeyBytes, Constants.SECRET_KEY_PREFIX.length);
        } else {
            throw new IllegalArgumentException("Invalid private key length");
        }

        if (privateKey.length != 64)
            throw new IllegalArgumentException("Invalid private key length");
    }

    public SecretKey(String privateKeyStr) {
        byte[] bytes = Base58.decodeChecked(privateKeyStr);
        if (bytes == null || bytes.length == 0)
            throw new IllegalArgumentException("Wrong private key format");

        this.privateKey = BytesUtil.removeFromStart(bytes, Constants.SECRET_KEY_PREFIX.length);
    }

    public byte[] getBytes() {
        return privateKey;
    }

    public String getWif() {
        return ByteSerializer.create()
                .write(Constants.SECRET_KEY_PREFIX)
                .write(privateKey)
                .toBase58WithChecksum();
    }

    @Override
    public String toString() {
        return getWif();
    }
}
