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

import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;

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
}
