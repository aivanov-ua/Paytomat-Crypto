package com.paytomat.tron.crypto;

import com.paytomat.tron.crypto.cryptohash.Keccak256;
import com.paytomat.tron.crypto.cryptohash.Keccak512;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Provider;
import java.security.Security;

/**
 * created by Alex Ivanov on 2018-12-27.
 */
public final class TronCastleProvider {

    private static final Provider INSTANCE;

    public static Provider getInstance() {
        return INSTANCE;
    }

    static {
        Provider p = Security.getProvider("SC");

        INSTANCE = (p != null) ? p : new BouncyCastleProvider();
        INSTANCE.put("MessageDigest.TRON-KECCAK-256", Keccak256.class.getName());
        INSTANCE.put("MessageDigest.TRON-KECCAK-512", Keccak512.class.getName());
    }

}
