package com.payomat.eos.signature;

import com.payomat.eos.EosTransactionException;
import com.payomat.eos.PrivateKey;
import com.payomat.eos.util.Wrapper;
import com.paytomat.core.util.HashUtil;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.util.Arrays;

import static com.payomat.eos.EosTransactionException.CODE_CANNOT_RECOVER_PUB_KEY;

/**
 * A deterministic K calculator based on the algorithm in section 3.2 of RFC 6979.
 */
public class ECDSA {
    private static final BigInteger ZERO = BigInteger.ZERO;
    private final HMac hMac;

    /**
     * Base constructor.
     *
     * @param digest digest to build the HMAC on.
     */
    private ECDSA(Digest digest) {
        this.hMac = new HMac(digest);
    }

    public static ECDSA createSha256Instance() {
        return new ECDSA(new SHA256Digest());
    }

    private static ECPoint pointFromX(ECDomainParameters ecParams, boolean isOdd, BigInteger x) {
        ECCurve.Fp curve = (ECCurve.Fp) ecParams.getCurve();
        BigInteger p = curve.getQ();
        BigInteger pOverFour = p.add(BigInteger.ONE).shiftRight(2);

        BigInteger alpha = x.pow(3).add(curve.getA().toBigInteger().multiply(x))
                .add(curve.getB().toBigInteger()).mod(p);
        BigInteger beta = alpha.modPow(pOverFour, p);

        BigInteger y = beta;

        boolean isBetaEven = beta.and(BigInteger.ONE).equals(BigInteger.ZERO);

        if (isBetaEven ^ !isOdd) {
            y = p.subtract(y);
        }

        return curve.createPoint(x, y);
    }

    private DeterministicK deterministicGenerateK(ECDomainParameters curveParams, byte[] data, PrivateKey privateKey, int nonce) {
        byte[] origData = data.clone();
        if (nonce != 0) {
            byte[] nonceBytes = new byte[nonce];
            java.util.Arrays.fill(nonceBytes, (byte) 0);
            byte[] dataWithNonce = new byte[data.length + nonceBytes.length];
            System.arraycopy(data, 0, dataWithNonce, 0, data.length);
            System.arraycopy(nonceBytes, 0, dataWithNonce, data.length, nonceBytes.length);

            data = HashUtil.sha256(dataWithNonce).getBytes();
        }


        byte[] x = privateKey.getPrivateKeyBytes();
        byte[] k = new byte[32];
        byte[] v = new byte[32];

        byte[] singleZero = new byte[1];
        singleZero[0] = 0;
        byte[] singleOne = new byte[1];
        singleOne[0] = 1;

        //STEP B
        Arrays.fill(v, (byte) 1);
        //STEP C
        Arrays.fill(k, (byte) 0);
        //STEP D
        hMacSha256(k, k, v, singleZero, x, data);
        //STEP E
        hMacSha256(k, v, v);
        //STEP F
        hMacSha256(k, k, v, singleOne, x, data);
        //STEP G
        hMacSha256(k, v, v);
        // Step H1/H2a, ignored as tlen === qlen (256 bit)
        // Step H2b
        hMacSha256(k, v, v);

        BigInteger T = new BigInteger(1, v);

        Wrapper<BigInteger> rWr = new Wrapper<>(ZERO), sWr = new Wrapper<>(ZERO);

        // Step H3, repeat until T is within the interval [1, n - 1]
        while (T.signum() <= 0 || T.compareTo(curveParams.getN()) >= 0 || !checkSig(curveParams,
                T, origData, privateKey, rWr, sWr)) {
            hMacSha256(k, k, v, singleZero);
            hMacSha256(k, v, v);

            // Step H1/H2a, again, ignored as tlen === qlen (256 bit)
            // Step H2b again
            hMacSha256(k, v, v);

            T = new BigInteger(1, v);
//            System.out.println("__LOOP + T = " + T);
        }

        return new DeterministicK(T, rWr.get(), sWr.get());
    }

    private void hMacSha256(byte[] secret, byte[] out, byte[]... inputs) {
        hMac.init(new KeyParameter(secret));
        for (byte[] in : inputs) {
            if (in.length > 1) hMac.update(in, 0, in.length);
            else hMac.update(in[0]);
        }
        hMac.doFinal(out, 0);
    }

    private boolean checkSig(ECDomainParameters curveParams, BigInteger k, byte[] data,
                             PrivateKey privateKey, Wrapper<BigInteger> rWr, Wrapper<BigInteger> sWr) {
        // find canonically valid signature
        BigInteger e = new BigInteger(1, data);
        BigInteger n = curveParams.getN();

        ECPoint Q = curveParams.getG().multiply(k);

        if (Q.isInfinity()) return false;

        BigInteger r = Q.normalize().getAffineXCoord().toBigInteger().mod(n);

        if (r.signum() == 0) return false;

        BigInteger ePlusDOnR = e.add(privateKey.getPrivateKeyAsBigInt().multiply(r));
        BigInteger s = k.modInverse(n).multiply(ePlusDOnR).mod(n);

        rWr.set(r);
        sWr.set(s);

        return s.signum() != 0;
    }

    public ECSignature sign(ECDomainParameters curveParams, byte[] data, final PrivateKey privateKey, int nonce) {
        BigInteger n = curveParams.getN();

        DeterministicK deterministicK = deterministicGenerateK(curveParams, data, privateKey, nonce);

        BigInteger nOverTwo = n.shiftRight(1);
        // enforce low S values, see bip62: 'low s values in signatures'
        BigInteger s = deterministicK.s;
        if (s.compareTo(nOverTwo) > 0) {
            s = n.subtract(s);
        }
        return new ECSignature(deterministicK.r, s);
    }

    /**
     * Calculate pubkey extraction parameter.
     * <p>
     * When extracting a pubkey from a signature, we have to
     * distinguish four different cases. Rather than putting this
     * burden on the verifier, Bitcoin includes a 2-bit value with the
     * signature.
     * <p>
     * This function simply tries all four cases and returns the value
     * that resulted in a successful pubkey recovery.
     */
    public int calcPubKeyRecoveryParam(ECDomainParameters curveParams, BigInteger data, ECSignature signature, PrivateKey privateKey) {
        ECPoint pkQ = privateKey.toPublicKey(true).getQ();

        for (int i = 0; i < 4; i++) {
            ECPoint Qprime = recoverPubKey(curveParams, data, signature, i);
//
//            String pk = new PublicKey(Qprime, true).toHex();
//            String srcPk = new PublicKey(pkQ, true).toHex();
//            System.out.println("__RECOVERED PUBKEY " + pk);
//            System.out.println("__RECOVERED Q " + srcPk);

            // 1.6.2 Verify Q
            if (Qprime.equals(pkQ)) {
                return i;
            }
        }
        throw new EosTransactionException("Cannot recover pubKey", CODE_CANNOT_RECOVER_PUB_KEY);
    }

    private ECPoint recoverPubKey(ECDomainParameters curveParams, BigInteger data, ECSignature signature, int i) {
        BigInteger n = curveParams.getN();
        ECPoint G = curveParams.getG();

        BigInteger r = signature.r;
        BigInteger s = signature.s;

        if (r.signum() <= 0 || r.compareTo(n) >= 0) {
            throw new EosTransactionException("Cannot recover pubKey, problem with r", CODE_CANNOT_RECOVER_PUB_KEY);
        }
        if (s.signum() <= 0 && r.compareTo(n) >= 0) {
            throw new EosTransactionException("Cannot recover pubKey, problem with s", CODE_CANNOT_RECOVER_PUB_KEY);
        }

        // A set LSB signifies that the y-coordinate is odd
        boolean isYOdd = i % 2 == 1;
        // The more significant bit specifies whether we should use the
        // first or second candidate key.
        boolean isSecondKey = i > 1;
        // 1.1 Let x = r + jn
        BigInteger x = isSecondKey ? r.add(n) : r;
        ECPoint R = pointFromX(curveParams, isYOdd, x);

        // 1.4 Check that nR is at infinity
        ECPoint nR = R.multiply(n);
        if (!nR.isInfinity()) {
            throw new EosTransactionException("nR is not a valid curve point", CODE_CANNOT_RECOVER_PUB_KEY);
        }

        // Compute -e from e
        BigInteger eNeg = data.negate().mod(n);

        // 1.6.1 Compute Q = r^-1 (sR -  eG)
        //               Q = r^-1 (sR + -eG)
        BigInteger rInv = r.modInverse(n);

        ECPoint Q = R.multiply(s).add(G.multiply(eNeg)).multiply(rInv);
        if (!Q.isValid())
            throw new EosTransactionException("INVALID Q POINT", CODE_CANNOT_RECOVER_PUB_KEY);

        return Q;
    }
}
