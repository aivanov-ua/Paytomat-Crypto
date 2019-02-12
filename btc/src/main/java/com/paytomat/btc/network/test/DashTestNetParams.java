package com.paytomat.btc.network.test;

import com.paytomat.btc.network.NetworkParams;

/**
 * created by Alex Ivanov on 6/12/18.
 */
public class DashTestNetParams extends TestNetParams {
    private static DashTestNetParams params;

    public static synchronized NetworkParams getParams() {
        if (params == null) {
            params = new DashTestNetParams();
        }
        return params;
    }

    private DashTestNetParams() {
        super(1, 0xEF, 0x8C, 0xC4, 1);
    }
}
