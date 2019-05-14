package com.paytomat.core;

import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.crypto.params.ECDomainParameters;

import java.math.BigInteger;

/**
 * created by Alex Ivanov on 2019-05-14.
 */
public interface Constants {

    X9ECParameters SECP256k1_PARAMS = CustomNamedCurves.getByName("secp256k1");
    ECDomainParameters SECP256k1_CURVE = new ECDomainParameters(SECP256k1_PARAMS.getCurve(),
            SECP256k1_PARAMS.getG(), SECP256k1_PARAMS.getN(), SECP256k1_PARAMS.getH());
    BigInteger SECP256k1_HALF_ORDER = SECP256k1_PARAMS.getN().shiftRight(1);


}
