package com.paytomat.btc;

import com.paytomat.btc.network.NetworkParams;
import com.paytomat.btc.network.NetworkParamsFactory;
import com.paytomat.core.util.Base58;
import com.paytomat.core.util.HashUtil;

import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.util.Arrays;

import static com.paytomat.btc.BitcoinException.CODE_BAD_FORMAT;

/**
 * created by Alex Ivanov on 2019-02-12.
 */
public class Address {

    private static final int NUM_ADDRESS_HASH = 20;
    private static final int NUM_ADDRESS_BYTES_SHORT = 21;
    private static final int NUM_ADDRESS_BYTES_EXTENDED = 22;

    public static Address fromString(String address) {
        if (address == null || address.length() == 0) {
            return null;
        }
        byte[] bytes = Base58.decodeChecked(address);
        if (bytes == null || !validateBytesLength(bytes)) {
            return null;
        }
        return new Address(bytes, address);
    }

    private static boolean validateBytesLength(byte[] bytes) {
        return (bytes.length == NUM_ADDRESS_BYTES_SHORT || bytes.length == NUM_ADDRESS_BYTES_EXTENDED);
    }

    public static Address fromString(String addressStr, String tokenSymbol, boolean isTestnet) {
        return fromString(addressStr, NetworkParamsFactory.getParams(tokenSymbol, isTestnet));
    }

    public static Address fromString(String addressStr, NetworkParams networkParams) {
        Address address = fromString(addressStr);
        if (address == null || !address.isValid(networkParams)) return null;
        return address;
    }

    private final byte[] bytes;
    private final String address;

    private Address(byte[] addressBytes, String address) {
        this.bytes = addressBytes;
        this.address = address;
    }

    public Address(byte[] bytes, NetworkParams networkParams) {
        if (bytes.length != NUM_ADDRESS_HASH) {
            throw new BitcoinException(CODE_BAD_FORMAT, "Cannot create address from bytes");
        }
        byte[] headerBytes = BigInteger.valueOf(networkParams.getStandardAddressHeader()).toByteArray();
        this.bytes = new byte[headerBytes.length + NUM_ADDRESS_HASH];
        System.arraycopy(headerBytes, 0, this.bytes, 0, headerBytes.length);
        System.arraycopy(bytes, 0, this.bytes, headerBytes.length, NUM_ADDRESS_HASH);

        byte[] addressBytes = new byte[this.bytes.length + 4];
        System.arraycopy(this.bytes, 0, addressBytes, 0, this.bytes.length);
        byte[] checksum = HashUtil.doubleSha256(addressBytes, 0, this.bytes.length).firstFourBytes();
        System.arraycopy(checksum, 0, addressBytes, this.bytes.length, 4);
        this.address = Base58.encode(addressBytes);
    }

    private boolean isValid(NetworkParams network) {
        int version = getVersion();
        return validateBytesLength(bytes) && ((network.getStandardAddressHeader() == version || network.getMultisigAddressHeader() == version));
    }

    public int getVersion() {
        if (bytes.length == NUM_ADDRESS_BYTES_SHORT) return bytes[0];
        else return ((bytes[0] << 8) | (bytes[1] & 0xFF));
    }

    public byte[] getHash160() {
        if (bytes.length == NUM_ADDRESS_BYTES_SHORT)
            return Arrays.copyOfRange(bytes, 1, bytes.length);
        else return Arrays.copyOfRange(bytes, 2, bytes.length);
    }

    public String getBytesHex() {
        return Hex.toHexString(bytes);
    }

    @Override
    public String toString() {
        return address;
    }

}
