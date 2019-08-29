package com.paytomat.tron.util;

import com.paytomat.core.crypto.ECDSASignature;

import org.bouncycastle.asn1.x9.X9IntegerConverter;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;

import static com.paytomat.tron.CurveConstants.CURVE;

/**
 * created by Alex Ivanov on 2019-01-08.
 */
public class RecoverUtil {

    /**
     * <p>Given the components of a signature and a selector value, recover and return the public key
     * that generated the signature according to the algorithm in SEC1v2 section 4.1.6.</p>
     *
     * <p> <p>The recId is an index from 0 to 3 which indicates which of the 4 possible allKeys is the
     * correct one. Because the key recovery operation yields multiple potential allKeys, the correct
     * key must either be stored alongside the signature, or you must be willing to try each recId in
     * turn until you find one that outputs the key you are expecting.</p>
     *
     * <p> <p>If this method returns null it means recovery was not possible and recId should be
     * iterated.</p>
     *
     * <p> <p>Given the above two points, a correct usage of this method is inside a for loop from 0
     * to 3, and if the output is null OR a key that is not the one you expect, you try again with the
     * next recId.</p>
     *
     * @param recId       Which possible key to recover.
     * @param sig         the R and S components of the signature, wrapped.
     * @param messageHash Hash of the data that was signed.
     * @return 65-byte encoded public key
     */
    public static byte[] recoverPubBytesFromSignature(int recId,
                                                      ECDSASignature sig,
                                                      byte[] messageHash) {
        assertThat(recId >= 0, "recId must be positive");
        assertThat(sig.r.signum() >= 0, "r must be positive");
        assertThat(sig.s.signum() >= 0, "s must be positive");
        assertThat(messageHash != null, "messageHash must not be null");
        // 1.0 For j from 0 to h   (h == recId here and the loop is outside
        // this function)
        //   1.1 Let x = r + jn
        BigInteger n = CURVE.getN();  // Curve order.
        BigInteger i = BigInteger.valueOf((long) recId / 2);
        BigInteger x = sig.r.add(i.multiply(n));
        //   1.2. Convert the integer x to an octet string X of length mlen
        // using the conversion routine
        //        specified in Section 2.3.7, where mlen = ⌈(log2 p)/8⌉ or
        // mlen = ⌈m/8⌉.
        //   1.3. Convert the octet string (16 set binary digits)||X to an
        // elliptic curve point R using the
        //        conversion routine specified in Section 2.3.4. If this
        // conversion routine outputs “invalid”, then
        //        do another iteration of Step 1.
        //
        // More concisely, what these points mean is to use X as a compressed
        // public key.
        ECCurve.Fp curve = (ECCurve.Fp) CURVE.getCurve();
        BigInteger prime = curve.getQ();  // Bouncy Castle is not consistent
        // about the letter it uses for the prime.
        if (x.compareTo(prime) >= 0) {
            // Cannot have point co-ordinates larger than this as everything
            // takes place modulo Q.
            return null;
        }
        // Compressed allKeys require you to know an extra bit of data about the
        // y-coord as there are two possibilities.
        // So it's encoded in the recId.
        ECPoint R = decompressKey(x, (recId & 1) == 1);
        //   1.4. If nR != point at infinity, then do another iteration of
        // Step 1 (callers responsibility).
        if (!R.multiply(n).isInfinity()) {
            return null;
        }
        //   1.5. Compute e from M using Steps 2 and 3 of ECDSA signature
        // verification.
        BigInteger e = new BigInteger(1, messageHash);
        //   1.6. For k from 1 to 2 do the following.   (loop is outside this
        // function via iterating recId)
        //   1.6.1. Compute a candidate public key as:
        //               Q = mi(r) * (sR - eG)
        //
        // Where mi(x) is the modular multiplicative inverse. We transform
        // this into the following:
        //               Q = (mi(r) * s ** R) + (mi(r) * -e ** G)
        // Where -e is the modular additive inverse of e, that is z such that
        // z + e = 0 (mod n). In the above equation
        // ** is point multiplication and + is point addition (the EC group
        // operator).
        //
        // We can find the additive inverse by subtracting e from zero then
        // taking the mod. For example the additive
        // inverse of 3 modulo 11 is 8 because 3 + 8 mod 11 = 0, and -3 mod
        // 11 = 8.
        BigInteger eInv = BigInteger.ZERO.subtract(e).mod(n);
        BigInteger rInv = sig.r.modInverse(n);
        BigInteger srInv = rInv.multiply(sig.s).mod(n);
        BigInteger eInvrInv = rInv.multiply(eInv).mod(n);
        ECPoint.Fp q = (ECPoint.Fp) ECAlgorithms.sumOfTwoMultiplies(CURVE.getG(),
                eInvrInv, R, srInv);
        return q.getEncoded(/* compressed */ false);
    }

    private static void assertThat(boolean value, String message) {
        if (!value) throw new Error(message);
    }

    private static ECPoint decompressKey(BigInteger xBN, boolean yBit) {
        X9IntegerConverter x9 = new X9IntegerConverter();
        byte[] compEnc = x9.integerToBytes(xBN, 1 + x9.getByteLength(CURVE
                .getCurve()));
        compEnc[0] = (byte) (yBit ? 0x03 : 0x02);
        return CURVE.getCurve().decodePoint(compEnc);
    }
}
