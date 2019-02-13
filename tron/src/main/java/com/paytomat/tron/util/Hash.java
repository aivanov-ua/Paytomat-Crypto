package com.paytomat.tron.util;

import com.paytomat.tron.crypto.TronCastleProvider;
import com.paytomat.tron.crypto.cryptohash.Keccak256;

import java.security.MessageDigest;
import java.security.Security;
import java.util.Arrays;

/**
 * created by Alex Ivanov on 2018-12-27.
 */
public class Hash {

    static {
        Security.addProvider(TronCastleProvider.getInstance());
    }

    public static byte[] sha3(byte[] input) {
        MessageDigest digest = new Keccak256();
        digest.update(input);
        return digest.digest();
    }

    /**
     * Calculates RIGTMOST160(SHA3(input)). This is used in address calculations. *
     *
     * @param input - data
     * @return - add_pre_fix + 20 right bytes of the hash keccak of the data
     */
    public static byte[] sha3omit12(byte[] input, byte prefix) {
        byte[] hash = sha3(input);
        byte[] address = Arrays.copyOfRange(hash, 11, hash.length);
        address[0] = prefix;
        return address;
    }
}
