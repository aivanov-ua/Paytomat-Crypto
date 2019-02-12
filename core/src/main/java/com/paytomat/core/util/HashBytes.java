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



import org.bouncycastle.util.encoders.Hex;

import java.io.Serializable;
import java.util.Arrays;

/**
 * represents the result of a SHA256 hashing operation prefer to use the static
 * factory methods.
 */
public class HashBytes implements Serializable, Comparable<HashBytes> {
    private static final long serialVersionUID = 1L;

    private static final int HASH_LENGTH = 32;

    final private byte[] _bytes;
    private int _hash;

    HashBytes(byte[] bytes) {
        this._bytes = bytes;
        _hash = -1;
    }

    /**
     * takes 32 bytes and stores them as hash. does not actually hash, this is
     * done in HashUtils
     *
     * @param bytes to be stored
     */
    public static HashBytes of(byte[] bytes) {
        return new HashBytes(bytes);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof HashBytes && Arrays.equals(_bytes, ((HashBytes) other)._bytes);
    }

    @Override
    public int hashCode() {
        if (_hash == -1) {
            final int offset = _bytes.length - 4;
            _hash = 0;
            for (int i = 0; i < 4; i++) {
                _hash <<= 8;
                _hash |= (((int) _bytes[offset + i]) & 0xFF);
            }
        }
        return _hash;
    }

    @Override
    public String toString() {
        return toHex();
    }

    public byte[] getBytes() {
        return _bytes;
    }

    @Override
    public int compareTo(HashBytes o) {
        for (int i = 0; i < HASH_LENGTH; i++) {
            byte myByte = _bytes[i];
            byte otherByte = o._bytes[i];

            final int compare = Integer.compare(myByte, (int) otherByte);
            if (compare != 0)
                return compare;
        }
        return 0;
    }

    public int length() {
        return HASH_LENGTH;
    }

    public byte[] firstFourBytes() {
        byte[] ret = new byte[4];
        System.arraycopy(_bytes, 0, ret, 0, 4);
        return ret;
    }

    private String toHex() {
        return Hex.toHexString(_bytes);
    }

}
