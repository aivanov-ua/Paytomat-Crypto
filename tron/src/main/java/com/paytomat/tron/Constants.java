package com.paytomat.tron;

/**
 * created by Alex Ivanov on 2018-12-27.
 */
public interface Constants {

    byte PREFIX_BYTE_MAINNET = 0x41;
    byte PREFIX_BYTE_TESTNET = (byte) 0xA0;

    int PRECISION = 6;
    long PRECISION_EXP = (long) Math.pow(10, PRECISION);

}
