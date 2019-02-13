package com.paytomat.tron;

import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;

/**
 * created by Alex Ivanov on 2018-12-26.
 */
public class PrivateKey {

    private final BCECPrivateKey privateKey;

    public PrivateKey(byte[] privateKeyBytes) {
        privateKey = new BCECPrivateKey("EC",
                new ECPrivateKeySpec(new BigInteger(1, privateKeyBytes), CurveConstants.CURVE_SPEC),
                null);
    }

    public PrivateKey(String privateKeyStr) {
        this(Hex.decode(privateKeyStr));
    }

    public byte[] getPrivateKeyBytes() {
        return privateKey.getD().toByteArray();
    }

    public BigInteger getPrivateBigInt() {
        return privateKey.getD();
    }

    public PublicKey toPublicKey() {
        return new PublicKey(this);
    }

    @Override
    public String toString() {
        return Hex.toHexString(normalizePrivateKey());
    }

    private byte[] normalizePrivateKey() {
        byte[] pk = getPrivateKeyBytes();
        byte[] normKey = new byte[32];
        int offset = pk.length - 32;
        for (int i = offset; i < pk.length; i++)
            normKey[i - offset] = (i >= 0 ? pk[i] : 0);
        return normKey;
    }
}