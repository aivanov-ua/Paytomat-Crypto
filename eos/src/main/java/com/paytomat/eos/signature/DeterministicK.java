package com.paytomat.eos.signature;

import java.math.BigInteger;

/**
 * created by Alex Ivanov on 2019-02-11.
 */
class DeterministicK {

    public final BigInteger k, r, s;

    public DeterministicK(BigInteger k, BigInteger r, BigInteger s) {
        this.k = k;
        this.r = r;
        this.s = s;
    }

}
