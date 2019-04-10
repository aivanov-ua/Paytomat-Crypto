/*
 * Copyright 2013, 2014 Megion Research & Development GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.paytomat.core.util;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.Blake2bDigest;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jcajce.provider.digest.Keccak;
import org.bouncycastle.util.encoders.Hex;

import java.security.MessageDigest;
import java.util.Arrays;

/**
 * Various hashing utilities used in the Bitcoin system.
 */
public class HashUtil {

    public static HashBytes sha256(byte[] data) {
        SHA256Digest digest = new SHA256Digest();
        digest.update(data, 0, data.length);
        byte[] result = new byte[32];
        digest.doFinal(result, 0);
        return HashBytes.of(result);
    }

    public static void sha512(byte[] data, int dataLen, byte[] result) {
        SHA512Digest digest = new SHA512Digest();
        digest.update(data, 0, dataLen);
        digest.doFinal(result, 0);
    }

    public static HashBytes doubleSha256(byte[] data) {
        return doubleSha256(data, 0, data.length);
    }

    public static HashBytes doubleSha256(byte[] data, int offset, int length) {
        SHA256Digest digest = new SHA256Digest();
        digest.update(data, offset, length);
        byte[] result = new byte[32];
        digest.doFinal(result, 0);
        return sha256(result);
    }

    public static HashBytes ripemd160(byte[] data) {
        byte[] out = new byte[20];
        RIPEMD160Digest ripeMD160 = new RIPEMD160Digest();
        ripeMD160.update(data, 0, data.length);
        ripeMD160.doFinal(out, 0); // This also resets the hash function for
        // next use
        return new HashBytes(out);
    }

    /**
     * Calculate the RipeMd160 value of the SHA-256 of an array of bytes. This is
     * how a Bitcoin address is derived from public key bytes.
     *
     * @param pubkeyBytes A Bitcoin public key as an array of bytes.
     * @return The Bitcoin address as an array of bytes.
     */
    public static byte[] addressHash(byte[] pubkeyBytes) {
        byte[] sha256 = sha256(pubkeyBytes).getBytes();
        byte[] out = new byte[20];
        RIPEMD160Digest ripeMD160 = new RIPEMD160Digest();
        ripeMD160.update(sha256, 0, sha256.length);
        ripeMD160.doFinal(out, 0); // This also resets the hash function for
        // next use
        return out;
    }

    public static byte[] sha256ripemd160(byte[] data) {
        //https://en.bitcoin.it/wiki/Technical_background_of_Bitcoin_addresses
        //1 - Take the corresponding public key generated with it (65 bytes, 1 byte 0x04, 32 bytes corresponding to X coordinate, 32 bytes corresponding to Y coordinate)
        //2 - Perform SHA-256 hashing on the public key
        byte[] sha256hash = sha256(data).getBytes();
        //3 - Perform RIPEMD-160 hashing on the result of SHA-256
        return ripemd160(sha256hash).getBytes();
    }

    public static byte[] hmacSha512(byte[] key, byte[] message) {
        HMac hmac = new HMac(new SHA512Digest());
        hmac.init(new KeyParameter(key));
        hmac.update(message, 0, message.length);
        byte[] result = new byte[hmac.getMacSize()];
        hmac.doFinal(result, 0);
        return result;
    }

    public static byte[] sha3(byte[] input) {
        MessageDigest digest = new Keccak.Digest256();
        digest.update(input);
        return digest.digest();
    }

    public static String sha3(String hexInput) {
        byte[] bytes = Hex.decode(hexInput);
        byte[] result = sha3(bytes);
        return Hex.toHexString(result);
    }

    public static byte[] sha3omit12(byte[] input, byte prefix) {
        byte[] hash = sha3(input);
        byte[] address = Arrays.copyOfRange(hash, 11, hash.length);
        address[0] = prefix;
        return address;
    }

    public static byte[] blake2b256(byte[] input, int offset, int length) {
        return blake2b(input, offset, length, 256);
    }

    public static byte[] blake2b(byte[] input, int offset, int length, int digestSize) {
        final Digest alg = new Blake2bDigest(digestSize);

        byte[] res = new byte[alg.getDigestSize()];
        alg.update(input, offset, length);
        alg.doFinal(res, 0);
        return res;
    }
}
