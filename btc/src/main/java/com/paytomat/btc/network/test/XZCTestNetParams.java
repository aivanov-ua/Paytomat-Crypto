package com.paytomat.btc.network.test;

import com.paytomat.btc.network.NetworkParams;

/**
 * created by Alex Ivanov on 9/17/18.
 */
public class XZCTestNetParams extends TestNetParams {

    private static XZCTestNetParams params;

    public static synchronized NetworkParams getParams() {
        if (params == null) {
            params = new XZCTestNetParams();
        }
        return params;
    }

    private XZCTestNetParams() {
        super(1, 0xEF, 0x6F, 0xC4, 1);
    }
}
