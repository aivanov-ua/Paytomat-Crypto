package com.paytomat.nem.crypto;

import com.paytomat.nem.utils.HexEncoder;

import java.math.BigInteger;

/**
 * Represents a private key.
 */
public class PrivateKey {

    private final BigInteger value;
    private final byte[] privateKeyBytes;

    /**
     * Creates a new private key.
     *
     * @param value The raw private key value.
     */
    public PrivateKey(final BigInteger value) {
        this.value = value;
        this.privateKeyBytes = value.toByteArray();
    }

    public PrivateKey(final BigInteger value, final byte[] privateKeyBytes) {
        this.value = value;
        this.privateKeyBytes = privateKeyBytes;
    }

    /**
     * Gets the raw private key value.
     *
     * @return The raw private key value.
     */
    public BigInteger getRaw() {
        return this.value;
    }

    public PublicKey toPublicKey() {
        return new KeyPair(this).getPublicKey();
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof PrivateKey)) {
            return false;
        }

        final PrivateKey rhs = (PrivateKey) obj;
        return this.value.equals(rhs.value);
    }

    @Override
    public String toString() {
        return HexEncoder.getString(privateKeyBytes);
    }

    /**
     * Creates a private key from a hex string.
     *
     * @param hex The hex string.
     * @return The new private key.
     */
    public static PrivateKey fromHexString(final String hex) {
        try {
            byte[] bytes = HexEncoder.getBytes(hex);
            return new PrivateKey(new BigInteger(1, bytes), bytes);
        } catch (final IllegalArgumentException e) {
            throw new CryptoException(e);
        }
    }

    public static PrivateKey fromBytes(final byte[] bytes) {
        try {
            return new PrivateKey(new BigInteger(1, bytes), bytes);
        } catch (final IllegalArgumentException e) {
            throw new CryptoException(e);
        }
    }

}
