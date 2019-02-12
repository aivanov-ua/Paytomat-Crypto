package com.paytomat.btc.network.prod;

import com.paytomat.btc.network.NetworkParams;

public class BTXProdNetParams extends ProdNetParams {

    private static BTXProdNetParams params;

    public static synchronized NetworkParams getParams() {
        if (params == null) {
            params = new BTXProdNetParams();
        }
        return params;
    }

    BTXProdNetParams() {
        super(160, 0x80, 0x03, 0x7D, 1);
    }
}
