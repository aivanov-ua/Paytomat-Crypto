package com.paytomat.btc;

import com.paytomat.btc.network.NetworkParams;
import com.paytomat.core.ec.EcTools;
import com.paytomat.core.ec.Parameters;
import com.paytomat.core.ec.Point;
import com.paytomat.core.util.Base58;
import com.paytomat.core.util.HashUtil;

import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;

import static com.paytomat.btc.BitcoinException.CODE_BAD_FORMAT;

/**
 * created by Alex Ivanov on 2019-02-12.
 */
public class PrivateKey {

    private final byte[] privateKey;
    private final boolean isCompressed;
    private BigInteger privateKeyBigInt = null;
    private PublicKey publicKey = null;

    public PrivateKey(byte[] privateKey, boolean isCompressed) {
        if (privateKey.length != 32) {
            throw new IllegalArgumentException("The length must be 32 bytes");
        }
        this.privateKey = privateKey;
        this.isCompressed = isCompressed;
    }

    public PrivateKey(String privateKeyWif) {
        byte[] encoded = Base58.decode(privateKeyWif);
        if (encoded == null || (encoded.length != 38 && encoded.length != 37)) {
            throw new BitcoinException(CODE_BAD_FORMAT, "Wrong wif format");
        }

        this.isCompressed = encoded.length == 38;
        this.privateKey = new byte[32];
        System.arraycopy(encoded, 1, this.privateKey, 0, 32);
    }

    public PrivateKey(String privateKeyHex, boolean isCompressed) {
        this(Hex.decode(privateKeyHex), isCompressed);
    }

    public BigInteger getBigInteger() {
        if (privateKeyBigInt == null) {
            byte[] keyBytes = new byte[33];
            System.arraycopy(privateKey, 0, keyBytes, 1, 32);
            privateKeyBigInt = new BigInteger(keyBytes);
        }
        return privateKeyBigInt;
    }

    public byte[] getBytes() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        if (publicKey == null) {
            Point Q = EcTools.multiply(Parameters.G, getBigInteger());
            if (isCompressed) {
                // Convert Q to a compressed point on the curve
                Q = new Point(Q.getCurve(), Q.getX(), Q.getY(), true);
            }
            publicKey = new PublicKey(Q.getEncoded());
        }
        return publicKey;
    }

    public String getWif(NetworkParams networkParams) {
        int encodedSize = isCompressed ? 38 : 37;
        byte[] encoded = new byte[encodedSize];
        //NETWORK
        encoded[0] = networkParams.getWifHeader();
        //KEY BYTES
        System.arraycopy(privateKey, 0, encoded, 1, privateKey.length);
        //COMPRESSED INDICATOR
        if (isCompressed) encoded[33] = 0x01;
        //CHECKSUM
        byte[] checksum = HashUtil.doubleSha256(encoded, 0, encodedSize - 4).firstFourBytes();
        System.arraycopy(checksum, 0, encoded, encodedSize - 4, 4);
        return Base58.encode(encoded);
    }

    @Override
    public String toString() {
        return Hex.toHexString(privateKey);
    }

}
