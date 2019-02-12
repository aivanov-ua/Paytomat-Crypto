package com.paytomat.btc.network.test;

import com.paytomat.btc.network.NetworkParams;

/**
 * created by Alex Ivanov on 6/12/18.
 */
public class LTCTestNetParams extends TestNetParams {
    private static LTCTestNetParams params;

    public static synchronized NetworkParams getParams() {
        if (params == null) {
            params = new LTCTestNetParams();
        }
        return params;
    }

    private LTCTestNetParams() {
        super(1, 0xEF, 0x6F, 0x3A, 50);
    }
}
