package com.paytomat.tezos;

import com.paytomat.core.util.Base58;
import com.paytomat.core.util.ByteSerializer;
import com.paytomat.core.util.BytesUtil;

/**
 * created by Alex Ivanov on 2019-04-08.
 */
public class PublicKey {

    private final byte[] publicKey;

    public PublicKey(String pubKeyStr) {
        this(BytesUtil.removeFromStart(Base58.decodeChecked(pubKeyStr), Constants.SECRET_KEY_PREFIX.length));
    }

    public PublicKey(byte[] pubKeyBytes) {
        if (pubKeyBytes.length != 32)
            throw new IllegalArgumentException("Illegal public key length");
        this.publicKey = pubKeyBytes;
    }

    public PublicKey(SecretKey secretKey) {
        this(BytesUtil.removeFromStart(secretKey.getBytes(), 32));
    }

    public byte[] getBytes() {
        return publicKey;
    }

    @Override
    public String toString() {
        return ByteSerializer.create()
                .write(Constants.PUBLIC_KEY_PREFIX)
                .write(publicKey)
                .toBase58WithChecksum();
    }
}
