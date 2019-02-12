package com.paytomat.btc.network.prod;

import com.paytomat.btc.network.NetworkParams;

/**
 * created by Alex Ivanov on 6/12/18.
 */
public class DashProdNetParams extends ProdNetParams {
    private static DashProdNetParams params;

    public static synchronized NetworkParams getParams() {
        if (params == null) {
            params = new DashProdNetParams();
        }
        return params;
    }

    private DashProdNetParams() {
        super(5, 0xCC, 0x4C, 0x10, 1);
    }
}
