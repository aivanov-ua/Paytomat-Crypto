package com.payomat.eos.transaction;

import com.paytomat.core.util.ByteSerializer;
import com.paytomat.core.util.Serializable;

/**
 * created by Alex Ivanov on 2019-02-11.
 */
public class EosWaitWeight implements Serializable {

    private final int waitSec;
    private final short weight;

    public EosWaitWeight(int waitSec, short weight) {
        this.waitSec = waitSec;
        this.weight = weight;
    }

    @Override
    public byte[] serialize() {
        return new ByteSerializer()
                .writeLE(waitSec)
                .writeLE(weight)
                .serialize();
    }

}
