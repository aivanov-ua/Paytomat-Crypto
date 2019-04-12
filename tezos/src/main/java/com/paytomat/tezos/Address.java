package com.paytomat.tezos;

import com.paytomat.core.util.Base58;
import com.paytomat.core.util.ByteSerializer;
import com.paytomat.core.util.BytesUtil;
import com.paytomat.core.util.HashUtil;

/**
 * created by Alex Ivanov on 2019-04-08.
 */
public class Address {

    /**
     * Validates address
     *
     * @param address address to validate
     * @return if given address is valid
     */
    public static boolean verifyAddress(String address) {
        byte[] addressBytes = Base58.decodeChecked(address);
        if (addressBytes == null) return false;
        return addressBytes.length == 20 + Constants.ADDRESS_PREFIX.length && BytesUtil.startsWith(addressBytes, Constants.ADDRESS_PREFIX);
    }

    private byte[] addressBytes;

    public Address(byte[] addressBytes) {
        if (addressBytes == null || addressBytes.length != 20)
            throw new IllegalArgumentException("Illegal address length, should be 20");
        this.addressBytes = addressBytes;
    }

    public Address(String addressStr) {
        this(BytesUtil.removeFromStart(Base58.decodeChecked(addressStr), Constants.ADDRESS_PREFIX.length));
    }

    public Address(PublicKey publicKey) {
        this(HashUtil.blake2b(publicKey.getBytes(), 0, publicKey.getBytes().length, 160));
    }

    public Address(SecretKey secretKey) {
        this(new PublicKey(secretKey));
    }

    public byte[] getBytes() {
        return addressBytes;
    }

    @Override
    public String toString() {
        return ByteSerializer.create()
                .write(Constants.ADDRESS_PREFIX)
                .write(addressBytes)
                .toBase58WithChecksum();
    }
}
