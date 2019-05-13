package com.paytomat.binance;

import com.paytomat.btc.PrivateKey;
import com.paytomat.core.util.HashUtil;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERSequenceGenerator;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;

/**
 * created by Alex Ivanov on 2019-05-09.
 */
public class Signature {

    public static byte[] signMessage(byte[] message, PrivateKey privateKey) {
        return signMessage(message, privateKey.getBytes());
    }

    public static byte[] signMessage(byte[] message, byte[] privateKey) {
        return signData(HashUtil.sha256(message).getBytes(), privateKey);
    }

    public static byte[] signData(byte[] data, byte[] privateKey) {
        final X9ECParameters params = SECNamedCurves.getByName("secp256k1");
        final ECDomainParameters curveParams = new ECDomainParameters(params.getCurve(), params.getG(), params.getN(), params.getH());

        ECDSASigner signer = new ECDSASigner();
        signer.init(true, new ECPrivateKeyParameters(new BigInteger(privateKey), curveParams));
        BigInteger[] signature = signer.generateSignature(data);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            DERSequenceGenerator seq = new DERSequenceGenerator(baos);
            seq.addObject(new ASN1Integer(signature[0]));
            seq.addObject(new ASN1Integer(toCanonicalS(signature[1])));
            seq.close();
            return baos.toByteArray();
        } catch (IOException e) {
            return new byte[0];
        }
    }

    private static BigInteger toCanonicalS(BigInteger s) {
        final X9ECParameters params = SECNamedCurves.getByName("secp256k1");
        final ECDomainParameters curveParams = new ECDomainParameters(params.getCurve(), params.getG(), params.getN(), params.getH());
        if (s.compareTo(curveParams.getN().shiftRight(1)) <= 0) {
            return s;
        } else {
            return curveParams.getN().subtract(s);
        }
    }

}
