package com.paytomat.tron;

import com.paytomat.core.util.Base58;
import com.paytomat.tron.util.Hash;

import org.bouncycastle.util.encoders.Hex;

import java.util.Arrays;

/**
 * created by Alex Ivanov on 2018-12-27.
 */
public class Address {

    /**
     * Validates address
     *
     * @param address address to validate
     * @return if given address is valid
     */
    public static boolean verifyAddress(String address, boolean isTestnet) {
        byte[] addressBytes = Base58.decodeChecked(address);
        if (addressBytes == null) return false;
        byte header = isTestnet ? Constants.PREFIX_BYTE_TESTNET : Constants.PREFIX_BYTE_MAINNET;
        return addressBytes.length == 21 && addressBytes[0] == header;
    }

    private final byte[] addressBytes;

    public Address(PrivateKey privateKey, boolean isTestNet) {
        this(privateKey.toPublicKey(), isTestNet);
    }

    public Address(PrivateKey privateKey, byte networkHeader) {
        this(privateKey.toPublicKey(), networkHeader);
    }

    public Address(PublicKey publicKey, boolean isTestNet) {
        this(publicKey, isTestNet ? Constants.PREFIX_BYTE_TESTNET : Constants.PREFIX_BYTE_MAINNET);
    }

    public Address(PublicKey publicKey, byte networkHeader) {
        byte[] pubKeyBytes = publicKey.getPubKeyBytes();
        this.addressBytes = Hash.sha3omit12(Arrays.copyOfRange(pubKeyBytes, 1, pubKeyBytes.length), networkHeader);
    }

    public Address(String address, boolean isTestNet) {
        if (!verifyAddress(address, isTestNet))
            throw new IllegalArgumentException("Invalid address format");
        this.addressBytes = Base58.decodeChecked(address);
        if (this.addressBytes == null)
            throw new IllegalArgumentException("Invalid address format");
    }

    public byte[] getBytes() {
        return addressBytes;
    }

    public String toHex() {
        return Hex.toHexString(addressBytes);
    }

    @Override
    public String toString() {
        return Base58.encodeWithChecksum(addressBytes);
    }
}
