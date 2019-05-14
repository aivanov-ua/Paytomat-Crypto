package com.paytomat.binance;

import com.paytomat.btc.PrivateKey;
import com.paytomat.btc.PublicKey;
import com.paytomat.core.util.Bech32;
import com.paytomat.core.util.BytesUtil;

import java.util.Arrays;

/**
 * created by Alex Ivanov on 2019-05-09.
 */
public class Address {

    public static boolean isValid(String address) {
        try {
            Bech32.HrpAndData data = Bech32.bech32Decode(address);
            if (!Arrays.equals(data.getHrp(), Constants.ADDRESS_PREFIX.getBytes())) return false;
            byte[] converted = BytesUtil.convertBits(data.getData(), 5, 8, false);
            int convertedLength = converted != null ? converted.length : 0;
            return convertedLength >= 2 && convertedLength <= 40;
        } catch (Exception ignored) {
            return false;
        }
    }

    private final String address;

    public Address(PrivateKey privateKey) {
        this(privateKey.getPublicKey());
    }

    public Address(PublicKey publicKey) {
        byte[] hash = publicKey.getPubKeyHash();
        byte[] converted = BytesUtil.convertBits(hash, 8, 5, true);
        this.address = Bech32.bech32Encode(Constants.ADDRESS_PREFIX, converted);
    }

    public Address(String address) {
        if (!isValid(address)) throw new IllegalArgumentException("Address is invalid");
        this.address = address;
    }

    public byte[] getBytes() {
        byte[] dec = Bech32.bech32Decode(address).getData();
        return BytesUtil.convertBits(dec, 5, 8, false);
    }

    @Override
    public String toString() {
        return address;
    }
}
