package com.paytomat.tron;

import com.paytomat.tron.crypto.ECDSASignature;
import com.paytomat.tron.util.RecoverUtil;

import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * created by Alex Ivanov on 2019-01-08.
 */
public class Signature {

    private final byte[] input;
    private final PrivateKey privateKey;

    public Signature(byte[] input, PrivateKey privateKey) {
        if (input.length != 32)
            throw new IllegalArgumentException("Expected 32 byte input to ECDSA signature, not " + input.length);
        if (privateKey == null) throw new IllegalArgumentException("No Private Key provided");

        this.input = input;
        this.privateKey = privateKey;
    }

    /**
     * Signs the given hash and returns the R and S components as BigIntegers and putData them in
     * ECDSASignature
     *
     * @return ECDSASignature signature that contains the R and S components
     */
    public ECDSASignature doSign() {
        ECDSASigner signer = new ECDSASigner(new HMacDSAKCalculator(new
                SHA256Digest()));
        ECPrivateKeyParameters privKeyParams = new ECPrivateKeyParameters
                (privateKey.getPrivateBigInt(), CurveConstants.CURVE);
        signer.init(true, privKeyParams);
        BigInteger[] components = signer.generateSignature(input);
        return new ECDSASignature(components[0], components[1]).toCanonical();
    }

    /**
     * Takes the keccak hash (32 bytes) of data and returns the ECDSA signature
     *
     * @return - signature
     */
    public ECDSASignature sign() {
        ECDSASignature sig = doSign();
        // Now we have to work backwards to figure out the recId needed to
        // recover the signature.
        int recId = -1;
        byte[] thisKey = privateKey.toPublicKey().getPubKeyBytes();
        for (int i = 0; i < 4; i++) {
            byte[] k = RecoverUtil.recoverPubBytesFromSignature(i, sig, input);
            if (k != null && Arrays.equals(k, thisKey)) {
                recId = i;
                break;
            }
        }
        if (recId == -1) {
            throw new RuntimeException("Could not construct a recoverable key" +
                    ". This should never happen.");
        }
        sig.v = (byte) (recId + 27);
        return sig;
    }

    public byte[] toByteArray() {
        return sign().toByteArray();
    }
}
