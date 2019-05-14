package com.paytomat.core.util;

/**
 * created by Alex Ivanov on 2019-05-14.
 */
public class VarLong {

    private final long value;

    public VarLong(long value) {
        this.value = value;
    }

    public int size() {
        long value = this.value;
        // handle two popular special cases up front ...
        if ((value & (~0L << 7)) == 0L) {
            return 1;
        }
        if (value < 0L) {
            return 10;
        }
        // ... leaving us with 8 remaining, which we can divide and conquer
        int n = 2;
        if ((value & (~0L << 35)) != 0L) {
            n += 4;
            value >>>= 28;
        }
        if ((value & (~0L << 21)) != 0L) {
            n += 2;
            value >>>= 14;
        }
        if ((value & (~0L << 14)) != 0L) {
            n += 1;
        }
        return n;
    }

    public final byte[] toBytes() {
        long value = this.value;
        ByteSerializer serializer = new ByteSerializer();
        while (true) {
            if ((value & ~0x7FL) == 0) {
                serializer.write((byte) value);
                return serializer.serialize();
            } else {
                serializer.write((byte) (((int) value & 0x7F) | 0x80));
                value >>>= 7;
            }
        }
    }

}
