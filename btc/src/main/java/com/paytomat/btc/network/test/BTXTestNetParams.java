package com.paytomat.btc.network.test;

import com.paytomat.btc.network.NetworkParams;

public class BTXTestNetParams extends TestNetParams {

    private static BTXTestNetParams params;

    public static synchronized NetworkParams getParams() {
        if (params == null) {
            params = new BTXTestNetParams();
        }
        return params;
    }

    BTXTestNetParams() {
        super(160, 0xEF, 0x6F, 0xC4, 1);
    }
}
