package com.payomat.eos.signature;

import java.math.BigInteger;

/**
 * created by Alex Ivanov on 2019-02-11.
 */
public class ECSignature {

    public final BigInteger r, s;

    public ECSignature(BigInteger r, BigInteger s) {
        this.r = r;
        this.s = s;
    }

    public int getRLength() {
        return r.toByteArray().length;
    }

    public int getSLength() {
        return s.toByteArray().length;
    }
}
