package com.paytomat.eos.transaction;

import com.paytomat.eos.PublicKey;
import com.paytomat.core.util.ByteSerializer;
import com.paytomat.core.util.Serializable;

/**
 * created by Alex Ivanov on 2019-02-11.
 */
public class EosKeyWeight implements Serializable {

    private final PublicKey publicKey;
    private final short weight;

    public EosKeyWeight(PublicKey publicKey, short weight) {
        this.publicKey = publicKey;
        this.weight = weight;
    }

    @Override
    public byte[] serialize() {
        return new ByteSerializer()
                .write(publicKey.serialize())
                .writeLE(weight)
                .serialize();
    }
}
