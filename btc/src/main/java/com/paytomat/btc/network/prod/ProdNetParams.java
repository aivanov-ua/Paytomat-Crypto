package com.paytomat.btc.network.prod;

import com.paytomat.btc.network.NetworkParams;

/**
 * created by Alex Ivanov on 6/12/18.
 */
class ProdNetParams extends NetworkParams {
    ProdNetParams(int coinType, int wifHeader, int addressHeader, int multisigAddressHeader, long minFeePerByte) {
        super(coinType, true, (byte) (wifHeader & 0xFF), addressHeader, multisigAddressHeader, minFeePerByte);
    }
}
