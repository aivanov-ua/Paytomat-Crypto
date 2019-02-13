package com.paytomat.nem.crypto;

import com.paytomat.nem.model.Address;
import com.paytomat.nem.utils.HexEncoder;

import java.util.Arrays;

/**
 * Represents a public key.
 */
public class PublicKey {
	private final byte[] value;

	/**
	 * Creates a new public key.
	 *
	 * @param bytes The raw public key value.
	 */
	public PublicKey(final byte[] bytes) {
		this.value = bytes;
	}

	/**
	 * Creates a public key from a hex string.
	 *
	 * @param hex The hex string.
	 * @return The new public key.
	 */
	public static PublicKey fromHexString(final String hex) {
		try {
			return new PublicKey(HexEncoder.getBytes(hex));
		} catch (final IllegalArgumentException e) {
			throw new CryptoException(e);
		}
	}

	/**
	 * Gets the raw public key value.
	 *
	 * @return The raw public key value.
	 */
	public byte[] getRaw() {
		return this.value;
	}

	public Address toAddress(boolean isTestnet) {
		return Address.fromPublicKey(this, isTestnet);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(this.value);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null || !(obj instanceof PublicKey)) {
			return false;
		}

		final PublicKey rhs = (PublicKey)obj;
		return Arrays.equals(this.value, rhs.value);
	}

	@Override
	public String toString() {
		return HexEncoder.getString(this.value);
	}
}
