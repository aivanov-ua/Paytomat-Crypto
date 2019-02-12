package com.paytomat.btc.network.prod;

import com.paytomat.btc.network.NetworkParams;

/**
 * created by Alex Ivanov on 6/12/18.
 */
public class LTCProdNetParams extends ProdNetParams {
    private static LTCProdNetParams params;

    public static synchronized NetworkParams getParams() {
        if (params == null) {
            params = new LTCProdNetParams();
        }
        return params;
    }

    private LTCProdNetParams() {
        super(2, 0xB0, 0x30, 0x32, 50);
    }
}
