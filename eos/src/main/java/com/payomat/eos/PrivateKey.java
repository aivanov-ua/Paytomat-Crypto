package com.payomat.eos;

import com.paytomat.core.util.Base58;
import com.paytomat.core.util.ByteSerializer;
import com.paytomat.core.util.HashUtil;

import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;

/**
 * created by Alex Ivanov on 2019-02-11.
 */
public class PrivateKey {
    private static final X9ECParameters params = SECNamedCurves.getByName("secp256k1");
    private static final ECDomainParameters EC_PARAMS = new ECDomainParameters(params.getCurve(), params.getG(), params.getN(), params.getH());

    private final byte[] privateKeyBytes;

    public PrivateKey(byte[] privateKeyBytes) {
        if (privateKeyBytes.length != 32) throw new RuntimeException("Wrong private key size");
        this.privateKeyBytes = privateKeyBytes;
    }

    public PrivateKey(String privateKeyStr) {
        byte[] wifBytes = Base58.decodeChecked(privateKeyStr);
        if (wifBytes == null) {
            throw new RuntimeException("Wrong private key");
        }
        this.privateKeyBytes = new byte[32];
        System.arraycopy(wifBytes, 1, this.privateKeyBytes, 0, 32);
    }

    public byte[] getPrivateKeyBytes() {
        return privateKeyBytes;
    }

    public boolean isEmpty() {
        return privateKeyBytes.length == 0;
    }

    public BigInteger getPrivateKeyAsBigInt() {
        return new BigInteger(1, privateKeyBytes);
    }

    public String toHex() {
        return Hex.toHexString(privateKeyBytes);
    }

    public PublicKey toPublicKey(boolean isCompressed) {
        synchronized (EC_PARAMS) {
            ECPoint Q = EC_PARAMS.getG().multiply(getPrivateKeyAsBigInt());
            return new PublicKey(Q, isCompressed);
        }
    }

    @Override
    public String toString() {
        byte[] wifBytes = ByteSerializer.create()
                .write((byte) 0x80)
                .write(privateKeyBytes)
                .serialize();
        byte[] checkSum = HashUtil.doubleSha256(wifBytes).firstFourBytes();

        return ByteSerializer.create()
                .write(wifBytes)
                .write(checkSum)
                .toBase58();
    }
}
