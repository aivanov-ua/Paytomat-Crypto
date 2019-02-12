package com.paytomat.btc.network.prod;

import com.paytomat.btc.network.NetworkParams;

public class BCDProdNetParams extends ProdNetParams {

    private static BCDProdNetParams params;

    public static synchronized NetworkParams getParams() {
        if (params == null) {
            params = new BCDProdNetParams();
        }
        return params;
    }

    BCDProdNetParams() {
        super(999, 0x80, 0, 5, 1);
    }
}
