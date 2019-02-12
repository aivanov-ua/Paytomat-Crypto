package com.paytomat.btc.network.test;

import com.paytomat.btc.network.NetworkParams;

/**
 * created by Alex Ivanov on 6/12/18.
 */
public class ZENTestNetParams extends TestNetParams {
    private static ZENTestNetParams params;

    public static synchronized NetworkParams getParams() {
        if (params == null) {
            params = new ZENTestNetParams();
        }
        return params;
    }

    private ZENTestNetParams() {
        super(121, 0x80, 0x2089, 0x2096, 1);
    }
}
