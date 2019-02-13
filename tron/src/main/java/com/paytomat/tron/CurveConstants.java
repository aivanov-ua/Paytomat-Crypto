package com.paytomat.tron;

import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.jce.spec.ECParameterSpec;

import java.math.BigInteger;

/**
 * created by Alex Ivanov on 2019-01-08.
 */
public class CurveConstants {

    /**
     * The parameters of the secp256k1 curve.
     */
    public static final ECDomainParameters CURVE;
    public static final ECParameterSpec CURVE_SPEC;

    public static final BigInteger HALF_CURVE_ORDER;
    public static final BigInteger SECP256K1N =
            new BigInteger("fffffffffffffffffffffffffffffffebaaedce6af48a03bbfd25e8cd0364141", 16);

    static {
        // All clients must agree on the curve to use by agreement.
        X9ECParameters params = SECNamedCurves.getByName("secp256k1");
        CURVE = new ECDomainParameters(params.getCurve(), params.getG(),
                params.getN(), params.getH());
        CURVE_SPEC = new ECParameterSpec(params.getCurve(), params.getG(),
                params.getN(), params.getH());
        HALF_CURVE_ORDER = params.getN().shiftRight(1);
    }

}
