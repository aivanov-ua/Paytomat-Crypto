package com.paytomat.btc.network.test;

import com.paytomat.btc.network.NetworkParams;

/**
 * created by Alex Ivanov on 6/12/18.
 */
public class BCHTestNetParams extends TestNetParams {
    private static BCHTestNetParams params;

    public static synchronized NetworkParams getParams() {
        if (params == null) {
            params = new BCHTestNetParams();
        }
        return params;
    }

    private BCHTestNetParams() {
        super(1, 0xEF, 0x6F, 0xC4, 1);
    }
}
