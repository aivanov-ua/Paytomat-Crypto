package com.paytomat.btc.network.test;

import com.paytomat.btc.network.NetworkParams;

/**
 * created by Alex Ivanov on 6/12/18.
 */
class TestNetParams extends NetworkParams {
    TestNetParams(int coinType, int wifHeader, int addressHeader, int multisigAddressHeader, long minFeePerByte) {
        super(coinType, false, (byte) wifHeader, addressHeader, multisigAddressHeader, minFeePerByte);
    }
}
