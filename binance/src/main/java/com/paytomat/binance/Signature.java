package com.paytomat.binance;

import com.paytomat.btc.PrivateKey;
import com.paytomat.core.crypto.ECDSASignature;
import com.paytomat.core.util.HashUtil;

import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;

import java.math.BigInteger;

import static com.paytomat.core.Constants.SECP256k1_CURVE;

/**
 * created by Alex Ivanov on 2019-05-09.
 */
public class Signature {

    public static byte[] signMessage(byte[] message, PrivateKey privateKey) {
        return signMessage(message, privateKey.getBytes());
    }

    public static byte[] signMessage(byte[] message, byte[] privateKey) {
        return signData(HashUtil.sha256(message).getBytes(), new BigInteger(1, privateKey));
    }

    public static byte[] signMessage(byte[] message, String privateKey) {
        return signData(HashUtil.sha256(message).getBytes(), new BigInteger(1, privateKey.getBytes()));
    }

    public static byte[] signData(byte[] data, BigInteger privateKey) {
        ECDSASigner signer = new ECDSASigner(new HMacDSAKCalculator(new SHA256Digest()));
        ECPrivateKeyParameters privKey = new ECPrivateKeyParameters(privateKey, SECP256k1_CURVE);
        signer.init(true, privKey);
        BigInteger[] components = signer.generateSignature(data);
        return new ECDSASignature(components[0], components[1]).toCanonicalised().toCompact();
    }

}
