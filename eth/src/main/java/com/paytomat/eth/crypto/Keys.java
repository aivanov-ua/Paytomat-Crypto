package com.paytomat.eth.crypto;

import com.paytomat.core.util.HashUtil;
import com.paytomat.eth.utils.Numeric;
import com.paytomat.eth.utils.Strings;

import java.math.BigInteger;

/**
 * created by Alex Ivanov on 2019-02-15.
 */
public class Keys {

    static final int PUBLIC_KEY_SIZE = 64;

    public static final int ADDRESS_SIZE = 160;
    public static final int ADDRESS_LENGTH_IN_HEX = ADDRESS_SIZE >> 2;
    static final int PUBLIC_KEY_LENGTH_IN_HEX = PUBLIC_KEY_SIZE << 1;

    public static String getAddress(ECKeyPair ecKeyPair) {
        return getAddress(ecKeyPair.getPublicKey());
    }

    public static String getAddress(BigInteger publicKey) {
        return getAddress(
                Numeric.toHexStringWithPrefixZeroPadded(publicKey, PUBLIC_KEY_LENGTH_IN_HEX));
    }

    public static String getAddress(String publicKey) {
        String publicKeyNoPrefix = Numeric.cleanHexPrefix(publicKey);

        if (publicKeyNoPrefix.length() < PUBLIC_KEY_LENGTH_IN_HEX) {
            publicKeyNoPrefix = Strings.zeros(
                    PUBLIC_KEY_LENGTH_IN_HEX - publicKeyNoPrefix.length())
                    + publicKeyNoPrefix;
        }
        String hash = HashUtil.sha3(publicKeyNoPrefix);
        return hash.substring(hash.length() - ADDRESS_LENGTH_IN_HEX);  // right most 160 bits
    }

}
