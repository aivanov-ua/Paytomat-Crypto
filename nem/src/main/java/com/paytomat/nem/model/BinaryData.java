package com.paytomat.nem.model;

import com.paytomat.nem.utils.HexEncoder;

import java.util.Arrays;

public class BinaryData {

    /**
     * BinaryData with zero length.
     */
    public static final BinaryData EMPTY = new BinaryData(new byte[0]);

    protected final byte[] rawData;

    public BinaryData(final byte[] rawData) {
        this.rawData = rawData;
    }

    public BinaryData(final String rawDataHex) {
        this.rawData = HexEncoder.getBytes(rawDataHex);
    }

    public int length() {
        return rawData.length;
    }

    public byte[] getRaw() {
        return rawData;
    }

    public String toHexStr() {
        return HexEncoder.getString(rawData);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(rawData);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BinaryData that = (BinaryData) o;

        return Arrays.equals(rawData, that.rawData);
    }

    @Override
    public String toString() {
        return toHexStr();
    }
}