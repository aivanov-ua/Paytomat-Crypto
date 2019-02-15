package com.paytomat.eth.utils;

import java.math.BigInteger;

import static com.paytomat.eth.crypto.Keys.ADDRESS_LENGTH_IN_HEX;

/**
 * created by Alex Ivanov on 2019-02-15.
 */
public class WalletUtils {

    public static final BigInteger GAS_PRICE = BigInteger.valueOf(22_000_000_000L);

    public static boolean isValidAddress(String input) {
        String cleanInput = Numeric.cleanHexPrefix(input);

        try {
            Numeric.toBigIntNoPrefix(cleanInput);
        } catch (NumberFormatException e) {
            return false;
        }

        return cleanInput.length() == ADDRESS_LENGTH_IN_HEX;
    }
}
