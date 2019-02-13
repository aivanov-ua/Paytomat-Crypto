package com.paytomat.nem.constants;

/**
 * created by Alex Ivanov on 6/1/18.
 */
public class NetworkVersion {

    private static final NetworkVersion MAIN_NETWORK = new NetworkVersion((byte) 0x68);
    public static final NetworkVersion TEST_NETWORK = new NetworkVersion((byte) 0x98);

    public static NetworkVersion provideNetwork(boolean isTestnet) {
        return isTestnet ? TEST_NETWORK : MAIN_NETWORK;
    }

    public final byte version;

    private NetworkVersion(byte version) {
        this.version = version;
    }


}
