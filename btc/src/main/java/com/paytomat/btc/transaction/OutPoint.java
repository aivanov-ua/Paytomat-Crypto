package com.paytomat.btc.transaction;

import java.util.Arrays;

/**
 * created by Alex Ivanov on 7/18/18.
 */
public class OutPoint {
    public final byte[] hash;//32-byte hash of the transaction from which we want to redeem an output
    public final int index;//Four-byte field denoting the output index we want to redeem from the transaction with the above hash (output number 2 = output index 1)

    public OutPoint(byte[] hash, int index) {
        this.hash = hash;
        this.index = index;
    }

    public boolean isNull() {
        boolean isZeroes = true;
        for (byte b : hash) {
            if (b != 0) {
                isZeroes = false;
                break;
            }
        }
        return isZeroes && index == -1;
    }

    @Override
    public String toString() {
        return "OutPoint{" +
                "hash=" + Arrays.toString(hash) +
                ", index=" + index +
                '}';
    }


}
