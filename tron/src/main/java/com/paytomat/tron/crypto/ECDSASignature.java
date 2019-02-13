package com.paytomat.tron.crypto;

import com.paytomat.tron.CurveConstants;
import com.paytomat.tron.util.BigIntUtils;
import com.paytomat.tron.util.BytesUtil;

import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;

/**
 * created by Alex Ivanov on 2019-01-08.
 */
public class ECDSASignature {

    public static ECDSASignature fromComponents(byte[] r, byte[] s) {
        return new ECDSASignature(new BigInteger(1, r),
                new BigInteger(1, s));
    }

    public static ECDSASignature fromComponents(byte[] r, byte[] s, byte v) {
        ECDSASignature signature = fromComponents(r, s);
        signature.v = v;
        return signature;
    }

    public final BigInteger r, s;
    public byte v;

    public ECDSASignature(BigInteger r, BigInteger s) {
        this.r = r;
        this.s = s;
    }

    public boolean validateSignature() {
        if (v != 27 && v != 28) return false;

        if (BigIntUtils.isLessThan(r, BigInteger.ONE)) return false;
        if (BigIntUtils.isLessThan(s, BigInteger.ONE)) return false;

        if (!BigIntUtils.isLessThan(r, CurveConstants.SECP256K1N)) return false;
        return BigIntUtils.isLessThan(s, CurveConstants.SECP256K1N);
    }

    public ECDSASignature toCanonical() {
        if (!BigIntUtils.isBiggerThan(s, CurveConstants.HALF_CURVE_ORDER)) return this;
            // The order of the curve is the number of valid points that
            // exist on that curve. If S is in the upper
            // half of the number of valid points, then bring it back to
            // the lower half. Otherwise, imagine that
            //    N = 10
            //    s = 8, so (-8 % 10 == 2) thus both (r, 8) and (r, 2)
            // are valid solutions.
            //    10 - 8 == 2, giving us always the latter solution,
            // which is canonical.
        else return new ECDSASignature(r, CurveConstants.CURVE.getN().subtract(s));
    }

    public String toBase64() {
        byte[] signData = new byte[65]; //1byte(header) + 32(r) + 32 (s)

        signData[0] = v;
        System.arraycopy(BigIntUtils.bigIntToBytes(r, 32), 0, signData, 1, 32);
        System.arraycopy(BigIntUtils.bigIntToBytes(s, 32), 0, signData, 33, 32);
        return Base64.toBase64String(signData);
    }

    public byte[] toByteArray() {
        final byte fixedV = this.v >= 27
                ? (byte) (this.v - 27)
                : this.v;

        return BytesUtil.append(
                BigIntUtils.bigIntToBytes(this.r, 32),
                BigIntUtils.bigIntToBytes(this.s, 32),
                new byte[]{fixedV});
    }

    public String toHex() {
        return Hex.toHexString(toByteArray());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ECDSASignature signature = (ECDSASignature) o;

        if (!r.equals(signature.r)) {
            return false;
        }
        return s.equals(signature.s);
    }

    @Override
    public int hashCode() {
        int result = r.hashCode();
        result = 31 * result + s.hashCode();
        return result;
    }
}
