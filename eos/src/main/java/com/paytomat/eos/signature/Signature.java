package com.paytomat.eos.signature;

import com.paytomat.core.util.ByteSerializer;
import com.paytomat.core.util.HashUtil;

import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;

/**
 * created by Alex Ivanov on 2019-02-11.
 */
public class Signature {

    public final BigInteger r, s;
    public final int i;

    public Signature(BigInteger r, BigInteger s, int i) {
        this.r = r;
        this.s = s;
        this.i = i;
    }

    public byte[] toByteArray() {
        return ByteSerializer.create()
                .write((byte) i)
                .write(r.toByteArray(), 0, 32)
                .write(s.toByteArray(), 0, 32)
                .serialize();
    }

    public String toHex() {
        return Hex.toHexString(toByteArray());
    }

    public String toString() {
        byte[] bytes = toByteArray();

        byte[] bytesK1 = ByteSerializer.create()
                .write(bytes)
                .write("K1")
                .serialize();
        byte[] checksum = HashUtil.ripemd160(bytesK1).firstFourBytes();

        String pkBase58 = ByteSerializer.create()
                .write(bytes)
                .write(checksum)
                .toBase58();
        return "SIG_K1_" + pkBase58;
    }
}