package com.paytomat.tron;

import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;

/**
 * created by Alex Ivanov on 2018-12-27.
 */
public class PublicKey {

    private final ECPoint point;

    public PublicKey(byte[] pubBytes) {
        this.point = CurveConstants.CURVE.getCurve().decodePoint(pubBytes);
    }

    public PublicKey(PrivateKey privateKey) {
        this.point = CurveConstants.CURVE.getG().multiply(privateKey.getPrivateBigInt());
    }

    public byte[] getPubKeyBytes() {
        return point.getEncoded(false);
    }

    public Address toAddress(byte prefix) {
        return new Address(this, prefix);
    }

    @Override
    public String toString() {
        return Hex.toHexString(point.getEncoded(false));
    }
}
