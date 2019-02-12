package com.paytomat.core.util;

import org.bouncycastle.util.encoders.Hex;

import java.util.ArrayList;
import java.util.List;

import static com.paytomat.core.util.BytesUtil.toBytesBE;
import static com.paytomat.core.util.BytesUtil.toBytesLE;

/**
 * created by Alex Ivanov on 2019-02-11.
 */
public class ByteSerializer {

    public static ByteSerializer create() {
        return new ByteSerializer();
    }

    private List<byte[]> bytesList = new ArrayList<>();

    public ByteSerializer write(byte[] bytes, int offset, int count) {
        byte[] res = new byte[count];
        System.arraycopy(bytes, offset, res, 0, count);
        return write(res);
    }

    public ByteSerializer write(byte[] bytes) {
        this.bytesList.add(bytes);
        return this;
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
        int size = 0;
        for (byte[] bytes : bytesList) {
            size += bytes.length;
        }
        byte[] res = new byte[size];
        int offset = 0;
        for (byte[] bytes : bytesList) {
            System.arraycopy(bytes, 0, res, offset, bytes.length);
            offset += bytes.length;
        }
        return res;
    }

    @Override
    public String toString() {
        return Hex.toHexString(serialize());
    }

    public String toBase58() {
        return Base58.encode(serialize());
    }
}
