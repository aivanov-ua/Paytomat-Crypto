package com.paytomat.core.util;

import org.bouncycastle.util.encoders.Hex;

import static com.paytomat.core.util.BytesUtil.toBytes;
import static com.paytomat.core.util.BytesUtil.toBytesBE;
import static com.paytomat.core.util.BytesUtil.toBytesLE;

/**
 * created by Alex Ivanov on 2019-02-11.
 */
public class ByteSerializer {

    public static ByteSerializer create() {
        return new ByteSerializer();
    }

    private byte[] bytes;
    private int lastIndex = 0;


    public ByteSerializer() {
        this(0);
    }

    public ByteSerializer(int initialSize) {
        this.bytes = new byte[initialSize];
    }

    private void ensureCapacity(int additionalBytes) {
        if (bytes.length < lastIndex + additionalBytes) {
            byte[] temp = new byte[lastIndex + additionalBytes];
            System.arraycopy(bytes, 0, temp, 0, bytes.length);
            this.bytes = temp;
        }
    }

    public ByteSerializer write(byte[] bytes, int offset, int count) {
        ensureCapacity(count);
        System.arraycopy(bytes, offset, this.bytes, lastIndex, count);
        this.lastIndex += count;
        return this;
    }

    public ByteSerializer write(byte[] bytes) {
        return write(bytes, 0, bytes.length);
    }

    public ByteSerializer writeLE(byte[] bytes) {
        return write(toBytesLE(bytes));
    }

    public ByteSerializer write(byte value) {
        return write(new byte[]{value});
    }

    public ByteSerializer write(boolean value) {
        return write((byte) (value ? 1 : 0));
    }

    public ByteSerializer writeLE(short value) {
        return write(toBytesLE(value));
    }

    public ByteSerializer write(int value) {
        return write(toBytes(value));
    }

    public ByteSerializer writeLE(int value) {
        return write(toBytesLE(value));
    }

    public ByteSerializer writeBE(int value) {
        return write(toBytesBE(value));
    }

    public ByteSerializer writeLE(long value) {
        return write(toBytesLE(value));
    }

    public ByteSerializer write(String value) {
        return write(value.getBytes());
    }

    public ByteSerializer writeLE(String value) {
        return writeLE(value.getBytes());
    }

    public ByteSerializer writeVarInt32(int varInt32) {
        return write(VarInt32.write(varInt32));
    }

    public <T extends Serializable> ByteSerializer write(T serializable) {
        return write(serializable.serialize());
    }

    public <T extends Serializable> ByteSerializer write(T[] serializableArray) {
        return write(serializableArray, false);
    }

    public <T extends Serializable> ByteSerializer write(T[] serializableArray, boolean writeAsVarInt32) {
        if (writeAsVarInt32) writeVarInt32(serializableArray.length);
        else writeLE(serializableArray.length);

        for (T item : serializableArray) {
            write(item.serialize());
        }
        return this;
    }

    public ByteSerializer writeHex(String hex) {
        return write(Hex.decode(hex));
    }

    public byte[] serialize() {
        return bytes;
    }

    @Override
    public String toString() {
        return Hex.toHexString(serialize());
    }

    public String toBase58() {
        return Base58.encode(serialize());
    }

    public String toBase58WithChecksum() {
        return Base58.encodeWithChecksum(serialize());
    }
}
