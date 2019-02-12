package com.paytomat.btc.network.prod;

import com.paytomat.btc.network.NetworkParams;

/**
 * created by Alex Ivanov on 6/12/18.
 */
public class BTCProdNetParams extends ProdNetParams {
    private static BTCProdNetParams params;

    public static synchronized NetworkParams getParams() {
        if (params == null) {
            params = new BTCProdNetParams();
        }
        return params;
    }

    private BTCProdNetParams() {
        super(0, 0x80, 0x00, 0x05, 1);
    }
}
