package com.payomat.eos;

import com.paytomat.core.util.Base58;
import com.paytomat.core.util.ByteSerializer;
import com.paytomat.core.util.HashUtil;
import com.paytomat.core.util.Serializable;

import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;

import java.util.Arrays;

/**
 * created by Alex Ivanov on 2019-02-11.
 */
public class PublicKey implements Serializable {

    public static final int LENGTH_WITHOUT_CHECKSUM = 33;

    public static boolean isValid(String pubKeyString) {
        if (!pubKeyString.startsWith("EOS")) return false;

        String keyShortened = pubKeyString.replace("EOS", "");
        byte[] bytes = Base58.decode(keyShortened);
        if (bytes == null || bytes.length != LENGTH_WITHOUT_CHECKSUM + 4) return false;

        byte[] publicKeyBytes = new byte[LENGTH_WITHOUT_CHECKSUM];
        System.arraycopy(bytes, 0, publicKeyBytes, 0, publicKeyBytes.length);
        byte[] checksum = new byte[4];
        System.arraycopy(bytes, publicKeyBytes.length, checksum, 0, 4);
        byte[] checksumCalc = HashUtil.ripemd160(publicKeyBytes).firstFourBytes();
        return Arrays.equals(checksum, checksumCalc);
    }

    private final ECPoint Q;
    private final byte[] publicKeyBytes;

    public PublicKey(ECPoint Q, boolean isCompressed) {
        this.Q = Q;
        this.publicKeyBytes = Q.getEncoded(isCompressed);
    }

    public PublicKey(String key) {
        Q = null;
        String keyShortened = key.replace("EOS", "");
        byte[] bytes = Base58.decode(keyShortened);
        this.publicKeyBytes = new byte[bytes.length - 4];
        System.arraycopy(bytes, 0, publicKeyBytes, 0, publicKeyBytes.length);
    }

    public ECPoint getQ() {
        return Q;
    }

    public byte[] getPublicKeyBytes() {
        return publicKeyBytes;
    }

    public boolean isEmpty() {
        return publicKeyBytes == null || publicKeyBytes.length == 0;
    }

    public String toHex() {
        return Hex.toHexString(publicKeyBytes);
    }

    @Override
    public byte[] serialize() {
        if (publicKeyBytes.length < LENGTH_WITHOUT_CHECKSUM)
            throw new EosTransactionException("Invalid PublicKey", EosTransactionException.CODE_WRONG_PUBLIC_KEY);

        return new ByteSerializer()
                .writeVarInt32(0)
                .write(publicKeyBytes, 0, LENGTH_WITHOUT_CHECKSUM)
                .serialize();
    }

    @Override
    public String toString() {
        byte[] checksum = HashUtil.ripemd160(publicKeyBytes).firstFourBytes();
        byte[] pkBytes = new byte[publicKeyBytes.length + 4];
        System.arraycopy(publicKeyBytes, 0, pkBytes, 0, publicKeyBytes.length);
        System.arraycopy(checksum, 0, pkBytes, publicKeyBytes.length, 4);
        return "EOS" + Base58.encode(pkBytes);
    }

}
