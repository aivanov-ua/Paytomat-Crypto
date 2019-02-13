package com.paytomat.nem.constants;

import com.paytomat.nem.utils.TimeValue;

/**
 * created by Alex Ivanov on 6/1/18.
 */
public interface Constants {
    int MINIMAL_FEE_MSG_CHAR = 32;
    int MAX_MESSAGE_LENGTH_BYTES = 1024;

    long NEM_GENESIS_BLOCK_TIME_MILLIS = 1427587585000L;

    long MIN_MOSAIC_SUPPLY = 10_000;
    long MIN_FEE = 50_000;
    long MAX_XEM_FEE = 1_250_000L;
    long MIN_FEE_CAP_AMOUNT = 10_000_000_000L;
    long MAX_MOSAIC_QUANTITY = 9_000_000_000_000_000L;
    double SUPPLY_ADJUSTMENT_FACTOR = 0.8;


    TimeValue DEFAULT_DEADLINE = TimeValue.fromValue(6 * 3600); // 6 hours in seconds
}
