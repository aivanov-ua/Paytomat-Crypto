package com.paytomat.btc.network.test;

import com.paytomat.btc.network.NetworkParams;

/**
 * created by Alex Ivanov on 6/12/18.
 */
public class BTCTestNetParams extends TestNetParams {
    private static BTCTestNetParams params;

    public static synchronized NetworkParams getParams() {
        if (params == null) {
            params = new BTCTestNetParams();
        }
        return params;
    }

    private BTCTestNetParams() {
        super(1, 0xEF, 0x6F, 0xC4, 1);
    }
}
