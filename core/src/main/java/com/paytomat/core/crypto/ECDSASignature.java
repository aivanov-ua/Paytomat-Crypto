package com.paytomat.core.crypto;

import com.paytomat.core.util.ByteSerializer;
import com.paytomat.core.util.BytesUtil;

import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;

import static com.paytomat.core.Constants.SECP256k1_CURVE;
import static com.paytomat.core.Constants.SECP256k1_HALF_ORDER;

/**
 * created by Alex Ivanov on 2019-05-14.
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

    /**
     * Constructs a signature with the given components. Does NOT automatically canonicalise the signature.
     */
    public ECDSASignature(BigInteger r, BigInteger s) {
        this.r = r;
        this.s = s;
    }

    /**
     * Returns true if the S component is "low", that means it is below {@link com.paytomat.core.Constants#SECP256k1_HALF_ORDER}. See <a
     * href="https://github.com/bitcoin/bips/blob/master/bip-0062.mediawiki#Low_S_values_in_signatures">BIP62</a>.
     */
    public boolean isCanonical() {
        return s.compareTo(SECP256k1_HALF_ORDER) <= 0;
    }

    /**
     * Will automatically adjust the S component to be less than or equal to half the curve order, if necessary.
     * This is required because for every signature (r,s) the signature (r, -s (mod N)) is a valid signature of
     * the same message. However, we dislike the ability to modify the bits of a Bitcoin transaction after it's
     * been signed, as that violates various assumed invariants. Thus in future only one of those forms will be
     * considered legal and the other will be banned.
     */
    public ECDSASignature toCanonicalised() {
        if (!isCanonical()) {
            // The order of the curve is the number of valid points that exist on that curve. If S is in the upper
            // half of the number of valid points, then bring it back to the lower half. Otherwise, imagine that
            //    N = 10
            //    s = 8, so (-8 % 10 == 2) thus both (r, 8) and (r, 2) are valid solutions.
            //    10 - 8 == 2, giving us always the latter solution, which is canonical.
            return new ECDSASignature(r, SECP256k1_CURVE.getN().subtract(s));
        } else {
            return this;
        }
    }

    public byte[] toCompact() {
        byte[] result = new byte[64];
        System.arraycopy(BytesUtil.bigIntToBytes(r, 32), 0, result, 0, 32);
        System.arraycopy(BytesUtil.bigIntToBytes(s, 32), 0, result, 32, 32);
        return result;
    }

    public byte[] toByteArray() {
        final byte fixedV = this.v >= 27
                ? (byte) (this.v - 27)
                : this.v;

        return ByteSerializer.create()
                .write(this.r, 32)
                .write(this.s, 32)
                .write(fixedV)
                .serialize();
    }

    public String toHex() {
        return Hex.toHexString(toByteArray());
    }
}
