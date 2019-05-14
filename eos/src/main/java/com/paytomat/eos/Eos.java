package com.paytomat.eos;

import com.paytomat.core.util.HashUtil;
import com.paytomat.eos.signature.ECDSA;
import com.paytomat.eos.signature.ECSignature;
import com.paytomat.eos.signature.Signature;

import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;

import java.math.BigInteger;

import static com.paytomat.core.util.BytesUtil.toBytesLE;
import static com.paytomat.eos.EosTransactionException.CODE_WRONG_HASH_SIZE;
import static com.paytomat.eos.EosTransactionException.CODE_WRONG_SIGNATURE_INPUT;

/**
 * created by Alex Ivanov on 2019-02-11.
 */
public class Eos {

    public final static String CURRENCY_NAME = "EOS";
    //TEST NET
    public final static String CHAIN_ID_HEX_TESTNET = "038f4b0fc8ff18a4f0842a8f0564611f6e96e8535901dd45e43ac8691a1c4dca"; // http://jungle.cryptolions.io:38888/v1/chain/get_info
    //PROD NET
    public final static String CHAIN_ID_HEX_PRODNET = "aca376f206b8fc25a6ed44dbdc66547c36c6c33e3a119ffbeaef943642f0e906";
    public final static byte CURRENCY_PRECISION = 4;
    public final static double PRECISION_MULTIPLY = Math.pow(10.0, Eos.CURRENCY_PRECISION);

    public static byte[] encodeName(String encode) {
        long name = 0;
        int i = 0;
        char[] input = encode.toCharArray();
        for (; getCharFromArray(input, i) != ' ' && i < 12; ++i) {
            name |= (charToSymbol(input[i]) & 0x1f) << (64 - 5 * (i + 1));
        }
        if (i == 12 && input.length > 12) name |= charToSymbol(input[12]) & 0x0F;
        return toBytesLE(name);
    }

    private static char getCharFromArray(char[] array, int pos) {
        return pos >= array.length ? ' ' : array[pos];
    }

    private static long charToSymbol(char c) {
        if (c >= 'a' && c <= 'z')
            return (c - 'a') + 6;
        if (c >= '1' && c <= '5')
            return (c - '1') + 1;
        return 0;
    }

    public static Signature signTransactionRaw(byte[] inData, PrivateKey privateKey) {
        PublicKey publicKey = privateKey.toPublicKey(true);
        if (inData == null || inData.length == 0 || privateKey.isEmpty() || publicKey.isEmpty() ||
                publicKey.getPublicKeyBytes().length != 33) {
            throw new EosTransactionException("Wrong input data", CODE_WRONG_SIGNATURE_INPUT);
        }

        byte[] data = HashUtil.sha256(inData).getBytes();
        if (data.length != 32) {
            throw new EosTransactionException("dataSha256: 32 byte buffer required", CODE_WRONG_HASH_SIZE);
        }
        int nonce = 0;

        final X9ECParameters params = SECNamedCurves.getByName("secp256k1");
        final ECDomainParameters curveParams = new ECDomainParameters(params.getCurve(), params.getG(), params.getN(), params.getH());

        ECDSA ecdsa = ECDSA.createSha256Instance();
        while (true) {
            ECSignature signature = ecdsa.sign(curveParams, data, privateKey, nonce++);
            if (signature.getRLength() == 32 && signature.getSLength() == 32) {
                int i = ecdsa.calcPubKeyRecoveryParam(curveParams, new BigInteger(1, data), signature, privateKey);
                i += 4; // compressed
                i += 27; // compact  //  24 or 27 :( forcing odd-y 2nd key candidate)
                return new Signature(signature.r, signature.s, i);
            }
            if (nonce % 10 == 0 && nonce != 0) {
                System.out.println("WARN: " + nonce + " attempts to find canonical signature");
            }
        }
    }

}
