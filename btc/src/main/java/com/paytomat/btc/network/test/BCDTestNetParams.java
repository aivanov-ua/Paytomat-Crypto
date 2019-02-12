package com.paytomat.btc.network.test;

import com.paytomat.btc.network.NetworkParams;

public class BCDTestNetParams extends TestNetParams {

    private static BCDTestNetParams params;

    public static synchronized NetworkParams getParams() {
        if (params == null) {
            params = new BCDTestNetParams();
        }
        return params;
    }

    BCDTestNetParams() {
        super(999, 0xEF, 0x6F, 0xC4, 1);
    }
}
