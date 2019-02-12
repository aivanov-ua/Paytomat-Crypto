package com.paytomat.btc.network.prod;

import com.paytomat.btc.network.NetworkParams;

/**
 * created by Alex Ivanov on 9/17/18.
 */
public class XZCProdNetParams extends ProdNetParams {
    private static XZCProdNetParams params;

    public static synchronized NetworkParams getParams() {
        if (params == null) {
            params = new XZCProdNetParams();
        }
        return params;
    }

    private XZCProdNetParams() {
        super(136, 0xD2, 0x52, 0x07, 1);
    }
}