package com.paytomat.core.util;

public class VarInt32 {

    public static byte[] write(int value) {
        byte[] result = new byte[size(value)];
        int pos = 0;
        while (true) {
            if ((value & ~0x7F) == 0) {
                result[pos] = (byte) value;
                return result;
            } else {
                result[pos++] = (byte) ((value & 0x7F) | 0x80);
                value >>>= 7;
            }
        }
    }

    public static int size(final int value) {
        if ((value & (0xffffffff << 7)) == 0)
            return 1;
        if ((value & (0xffffffff << 14)) == 0)
            return 2;
        if ((value & (0xffffffff << 21)) == 0)
            return 3;
        if ((value & (0xffffffff << 28)) == 0)
            return 4;
        return 5;
    }

    public static int read(byte[] data, int idx) {

        byte tmp = data[idx];
        if (tmp >= 0) {
            return tmp;
        }
        int result = tmp & 0x7f;
        if ((tmp = data[idx + 1]) >= 0) {
            result |= tmp << 7;
        } else {
            result |= (tmp & 0x7f) << 7;
            if ((tmp = data[idx + 2]) >= 0) {
                result |= tmp << 14;
            } else {
                result |= (tmp & 0x7f) << 14;
                if ((tmp = data[idx + 3]) >= 0) {
                    result |= tmp << 21;
                } else {
                    result |= (tmp & 0x7f) << 21;
                    result |= (tmp = data[idx + 4]) << 28;
                    if (tmp < 0) {
                        // Discard upper 32 bits.
                        for (int i = 0; i < 5; i++) {
                            if (data[idx + 5] >= 0) {
                                return result;
                            }
                        }
                        throw new IllegalArgumentException();
                    }
                }
            }
        }
        return result;
    }
}