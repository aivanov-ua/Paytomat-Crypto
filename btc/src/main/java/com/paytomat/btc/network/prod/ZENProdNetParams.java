package com.paytomat.btc.network.prod;

import com.paytomat.btc.network.NetworkParams;

/**
 * created by Alex Ivanov on 6/12/18.
 */
public class ZENProdNetParams extends ProdNetParams {
    private static ZENProdNetParams params;

    public static synchronized NetworkParams getParams() {
        if (params == null) {
            params = new ZENProdNetParams();
        }
        return params;
    }

    private ZENProdNetParams() {
        super(121, 0x80, 0x2089, 0x2096, 1);
    }
}
