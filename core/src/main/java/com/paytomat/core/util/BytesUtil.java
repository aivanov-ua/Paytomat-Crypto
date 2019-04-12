package com.paytomat.core.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * created by Alex Ivanov on 2019-02-11.
 */
public class BytesUtil {

    public static ByteBuffer provideBufferLE(int size) {
        return ByteBuffer.allocate(size).order(ByteOrder.LITTLE_ENDIAN);
    }

    public static ByteBuffer provideBufferBE(int size) {
        return ByteBuffer.allocate(size).order(ByteOrder.BIG_ENDIAN);
    }

    public static byte[] toBytesLE(short value) {
        return provideBufferLE(2).putShort(value).array();
    }

    public static byte[] toBytes(int value) {
        return ByteBuffer.allocate(4).putInt(value).array();
    }

    public static byte[] toBytesLE(int value) {
        return provideBufferLE(4).putInt(value).array();
    }

    public static byte[] toBytesBE(int value) {
        return provideBufferBE(4).putInt(value).array();
    }

    public static byte[] toBytesLE(long value) {
        return provideBufferLE(8).putLong(value).array();
    }

    public static byte[] toBytesLE(byte[] value) {
        return provideBufferLE(value.length).put(value).array();
    }

    public static byte[] trimLeadingBytes(byte[] bytes, byte b) {
        int offset = 0;
        for (; offset < bytes.length - 1; offset++) {
            if (bytes[offset] != b) {
                break;
            }
        }
        return Arrays.copyOfRange(bytes, offset, bytes.length);
    }

    public static byte[] trimLeadingZeroes(byte[] bytes) {
        return trimLeadingBytes(bytes, (byte) 0);
    }

    public static byte[] removeFromStart(byte[] bytes, int count) {
        return removeRange(bytes, 0, count);
    }

    public static byte[] removeRange(byte[] bytes, int startIndex, int count) {
        if (bytes == null || bytes.length <= count) return new byte[0];
        byte[] newArray = new byte[bytes.length - count];

        System.arraycopy(bytes, 0, newArray, 0, startIndex);
        System.arraycopy(bytes, startIndex + count, newArray, startIndex, bytes.length - count - startIndex);
        return newArray;
    }

    public static boolean startsWith(byte[] bytes, byte[] startsWith) {
        if (bytes.length < startsWith.length) return false;
        for (int i = 0; i < startsWith.length; i++) {
            if (bytes[i] != startsWith[i]) return false;
        }
        return true;
    }
}
