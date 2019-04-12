package com.paytomat.waves;

import com.paytomat.core.util.Base58;
import com.paytomat.core.util.HashUtil;

import java.nio.ByteBuffer;
import java.util.Arrays;


public class PublicKeyAccount implements Account {

    private final char scheme;
    private final byte[] publicKey;
    private final String address;

    PublicKeyAccount(byte[] publicKey, char scheme) {
        this.scheme = scheme;
        this.publicKey = publicKey;
        this.address = Base58.encode(address(publicKey, scheme));
    }

    public final byte[] getPublicKey() {
        return Arrays.copyOf(publicKey, publicKey.length);
    }

    public final String getAddress() {
        return address;
    }

    public final char getScheme() {
        return scheme;
    }

    static byte[] secureHash(byte[] message, int ofs, int len) {
        byte[] blake2b = HashUtil.blake2b256(message, ofs, len);
        return HashUtil.sha3(blake2b);
    }

    private static byte[] address(byte[] publicKey, char scheme) {
        ByteBuffer buf = ByteBuffer.allocate(26);
        byte[] hash = secureHash(publicKey, 0, publicKey.length);
        buf.put((byte) 1).put((byte) scheme).put(hash, 0, 20);
        byte[] checksum = secureHash(buf.array(), 0, 22);
        buf.put(checksum, 0, 4);
        return buf.array();
    }
}
