package com.paytomat.btc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * created by Alex Ivanov on 2019-02-12.
 */
public class Coin {

    public static long parseValue(String valueStr, BigDecimal precision) {
        try {
            return new BigDecimal(valueStr).multiply(precision).setScale(0, BigDecimal.ROUND_HALF_DOWN).longValueExact();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static BigInteger parseValueToBigInt(String valueStr, BigDecimal precision) {
        try {
            return new BigDecimal(valueStr).multiply(precision).setScale(0, BigDecimal.ROUND_HALF_DOWN).toBigIntegerExact();
        } catch (Exception e) {
            e.printStackTrace();
            return BigInteger.ZERO;
        }
    }

    public static String convertSatoshiToBtc(String satoshiAmount, BigDecimal precision) {
        try {
            return new BigDecimal(satoshiAmount).divide(precision, 8, RoundingMode.HALF_DOWN).toPlainString();
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    public static long calcMinimumFee(long feePerByte, int txLenBytes) {
        return feePerByte * txLenBytes;
    }

}
