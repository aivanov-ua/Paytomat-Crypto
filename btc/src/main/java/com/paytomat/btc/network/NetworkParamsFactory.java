package com.paytomat.btc.network;

import com.paytomat.btc.network.prod.BCDProdNetParams;
import com.paytomat.btc.network.prod.BCHProdNetParams;
import com.paytomat.btc.network.prod.BTCProdNetParams;
import com.paytomat.btc.network.prod.BTXProdNetParams;
import com.paytomat.btc.network.prod.DashProdNetParams;
import com.paytomat.btc.network.prod.LTCProdNetParams;
import com.paytomat.btc.network.prod.XZCProdNetParams;
import com.paytomat.btc.network.prod.ZENProdNetParams;
import com.paytomat.btc.network.test.BCDTestNetParams;
import com.paytomat.btc.network.test.BCHTestNetParams;
import com.paytomat.btc.network.test.BTCTestNetParams;
import com.paytomat.btc.network.test.BTXTestNetParams;
import com.paytomat.btc.network.test.DashTestNetParams;
import com.paytomat.btc.network.test.LTCTestNetParams;
import com.paytomat.btc.network.test.XZCTestNetParams;
import com.paytomat.btc.network.test.ZENTestNetParams;

/**
 * created by Alex Ivanov on 6/12/18.
 */
public class NetworkParamsFactory {

    public static NetworkParams getParams(String tokenSymbol, boolean isTestNet) {
        if (isTestNet) return getTestNetParams(tokenSymbol);
        else return getProdNetParams(tokenSymbol);
    }

    private static NetworkParams getProdNetParams(String tokenSymbol) {
        switch (tokenSymbol.toUpperCase()) {
            case "BTC":
                return BTCProdNetParams.getParams();
            case "LTC":
                return LTCProdNetParams.getParams();
            case "DASH":
                return DashProdNetParams.getParams();
            case "BCH":
                return BCHProdNetParams.getParams();
            case "XZC":
                return XZCProdNetParams.getParams();
            case "ZEN":
                return ZENProdNetParams.getParams();
            case "BTX":
                return BTXProdNetParams.getParams();
            case "BCD":
                return BCDProdNetParams.getParams();
            default:
                throw new RuntimeException("Provided coin is not supported");
        }
    }

    private static NetworkParams getTestNetParams(String tokenSymbol) {
        switch (tokenSymbol.toUpperCase()) {
            case "BTC":
                return BTCTestNetParams.getParams();
            case "LTC":
                return LTCTestNetParams.getParams();
            case "DASH":
                return DashTestNetParams.getParams();
            case "BCH":
                return BCHTestNetParams.getParams();
            case "XZC":
                return XZCTestNetParams.getParams();
            case "ZEN":
                return ZENTestNetParams.getParams();
            case "BTX":
                return BTXTestNetParams.getParams();
            case "BCD":
                return BCDTestNetParams.getParams();
            default:
                throw new RuntimeException("Provided coin is not supported");
        }
    }

}
