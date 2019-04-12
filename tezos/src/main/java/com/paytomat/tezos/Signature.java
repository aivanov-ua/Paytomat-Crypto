package com.paytomat.tezos;

import com.paytomat.core.util.ByteSerializer;
import com.paytomat.core.util.HashUtil;
import com.paytomat.tezos.model.SignatureResult;

import org.bouncycastle.math.ec.rfc8032.Ed25519;

import static com.paytomat.tezos.Constants.SIGN_PREFIX;
import static com.paytomat.tezos.Constants.WATTERMARK;

/**
 * created by Alex Ivanov on 2019-04-09.
 */
public class Signature {

    /**
     * Computes a detached signature for a message with the sender's secret key.
     *
     * @param message:   The message to encrypt
     * @param secretKey: The sender's secret key
     * @return computed signature
     */
    public static byte[] signature(byte[] message, SecretKey secretKey) {
        byte[] signature = new byte[64];
        Ed25519.sign(secretKey.getBytes(), 0, message, 0, message.length, signature, 0);
        return signature;
    }

    public static boolean verify(byte[] signature, PublicKey publicKey, byte[] message) {
        return Ed25519.verify(signature, 0, publicKey.getBytes(), 0, message, 0, message.length);
    }

    public static SignatureResult signForgedOperation(byte[] bytes, SecretKey secretKey) {
        byte[] watermarkedBytes = ByteSerializer.create().write(WATTERMARK).write(bytes).serialize();

        byte[] hashedOperation = HashUtil.blake2b256(watermarkedBytes, 0, watermarkedBytes.length);
        byte[] signature = signature(hashedOperation, secretKey);

        String edsig = ByteSerializer.create().write(SIGN_PREFIX).write(signature).toBase58WithChecksum();

        String sbytes = ByteSerializer.create().write(bytes).write(signature).toString();
        return new SignatureResult(hashedOperation, signature, edsig, sbytes);
    }
}
