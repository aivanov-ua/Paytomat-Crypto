package com.paytomat.waves;


import org.whispersystems.curve25519.java.curve_sigs;

import java.util.Arrays;

public class PrivateKeyAccount extends PublicKeyAccount {

    private final byte[] privateKey;

    private PrivateKeyAccount(byte[] privateKey, char scheme) {
        super(publicKey(privateKey), scheme);
        this.privateKey = privateKey;
    }

    private PrivateKeyAccount(byte[] publicKey, byte[] privateKey, char scheme) {
        super(publicKey, scheme);
        this.privateKey = privateKey;
    }

    public static PrivateKeyAccount fromPrivateKeyBytes(byte[] privateKey, char scheme) {
        if (privateKey.length != 32)
            throw new RuntimeException("wrong seed length");

        byte[] pk = Arrays.copyOf(privateKey, 32);
        pk[0] &= 248;
        pk[31] &= 127;
        pk[31] |= 64;

        return new PrivateKeyAccount(publicKey(pk), privateKey, scheme);
    }

    public final byte[] getPrivateKeyBytes() {
        return Arrays.copyOf(privateKey, privateKey.length);
    }

    public final byte[] getPrivateKey() {
        byte[] pk =  Arrays.copyOf(privateKey, privateKey.length);
        pk[0] &= 248;
        pk[31] &= 127;
        pk[31] |= 64;
        return pk;
    }

    private static byte[] publicKey(byte[] privateKey) {
        byte[] publicKey = new byte[32];
        curve_sigs.curve25519_keygen(publicKey, privateKey);
        return publicKey;
    }
}
