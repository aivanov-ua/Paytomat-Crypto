package com.paytomat.core.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * created by Alex Ivanov on 2019-02-11.
 */
public class BytesUtil {

    public static ByteBuffer provideBufferLE(int size) {
        return ByteBuffer.allocate(size).order(ByteOrder.LITTLE_ENDIAN);
    }

    public static byte[] toBytesLE(short value) {
        return provideBufferLE(2).putShort(value).array();
    }

    public static byte[] toBytesLE(int value) {
        return provideBufferLE(4).putInt(value).array();
    }

    public static byte[] toBytesLE(long value) {
        return provideBufferLE(8).putLong(value).array();
    }

    public static byte[] toBytesLE(byte[] value) {
        return provideBufferLE(value.length).put(value).array();
    }
}
