package com.paytomat.btc.network.prod;

import com.paytomat.btc.network.NetworkParams;

/**
 * created by Alex Ivanov on 6/12/18.
 */
public class BCHProdNetParams extends ProdNetParams {
    private static BCHProdNetParams params;

    public static synchronized NetworkParams getParams() {
        if (params == null) {
            params = new BCHProdNetParams();
        }
        return params;
    }

    private BCHProdNetParams() {
        super(145, 0x80, 0x00, 0x05, 1);
    }
}
