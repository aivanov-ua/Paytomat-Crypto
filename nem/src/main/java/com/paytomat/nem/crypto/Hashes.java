package com.paytomat.nem.crypto;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.KeccakDigest;

/**
 * Static class that exposes hash functions.
 */
public class Hashes {

    /**
     * Performs a SHA3-256 hash of the concatenated inputs.
     *
     * @param inputs The byte arrays to concatenate and hash.
     * @return The hash of the concatenated inputs.
     * @throws CryptoException if the hash operation failed.
     */
    public static byte[] sha3_256(final byte[]... inputs) {
        return hash(new KeccakDigest(256), inputs);
    }

    /**
     * Performs a SHA3-512 hash of the concatenated inputs.
     *
     * @param inputs The byte arrays to concatenate and hash.
     * @return The hash of the concatenated inputs.
     * @throws CryptoException if the hash operation failed.
     */
    public static byte[] sha3_512(final byte[]... inputs) {
        return hash(new KeccakDigest(512), inputs);
    }


    private static byte[] hash(final Digest digest, final byte[]... inputs) {
        for (final byte[] input : inputs) {
            digest.update(input, 0, input.length);
        }
        byte[] result = new byte[digest.getDigestSize()];
        digest.doFinal(result, 0);
        return result;
    }
}
