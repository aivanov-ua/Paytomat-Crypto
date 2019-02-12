package com.paytomat.btc.network;

/**
 * created by Alex Ivanov on 6/11/18.
 */
public class NetworkParams {

    /**
     * BIP44 coin type
     *
     * @see <a href="https://github.com/satoshilabs/slips/blob/master/slip-0044.md">SLIP-44 docs</a>
     */
    private final int coinType;
    /**
     * Shows if network params refers to production network of blockchain
     */
    private final boolean isProdNet;

    /**
     * Wif header for current network
     *
     * @see <a href="https://github.com/libbitcoin/libbitcoin/wiki/Altcoin-Version-Mappings">Altcoin mappings</a>
     */
    private final byte wifHeader;

    /**
     * p2pkh Address header
     *
     * @see <a href="https://github.com/libbitcoin/libbitcoin/wiki/Altcoin-Version-Mappings">Altcoin mappings</a>
     */
    private final int addressHeader;

    /**
     * p2sh Multisig address header
     *
     * @see <a href="https://github.com/libbitcoin/libbitcoin/wiki/Altcoin-Version-Mappings">Altcoin mappings</a>
     */
    private final int multisigAddressHeader;

    /**
     * minimal allowed fee per transaction byte
     */
    private final long minFeePerByte;

    public NetworkParams(int coinType, boolean isProdNet, byte wifHeader, int addressHeader, int multisigAddressHeader, long minFeePerByte) {
        this.coinType = coinType;
        this.isProdNet = isProdNet;
        this.wifHeader = wifHeader;
        this.addressHeader = addressHeader;
        this.multisigAddressHeader = multisigAddressHeader;
        this.minFeePerByte = minFeePerByte;
    }

    public final int getCoinType() {
        return coinType;
    }

    public final boolean isProdNet() {
        return isProdNet;
    }

    public final byte getWifHeader() {
        return wifHeader;
    }

    public final int getStandardAddressHeader() {
        return addressHeader;
    }

    public final int getMultisigAddressHeader() {
        return multisigAddressHeader;
    }

    public final long getMinFeePerByte() {
        return minFeePerByte;
    }

    public final boolean verifyAddressHeaders(int keyhashType) {
        return keyhashType == addressHeader || keyhashType == multisigAddressHeader;
    }

}
