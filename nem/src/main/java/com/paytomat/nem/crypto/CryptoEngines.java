package com.paytomat.nem.crypto;

import com.paytomat.nem.crypto.ed25519.Ed25519CryptoEngine;

/**
 * Static class that exposes crypto engines.
 */
public class CryptoEngines {

	private static final CryptoEngine DEFAULT_ENGINE;

	static {
		DEFAULT_ENGINE = new Ed25519CryptoEngine();
	}

	/**
	 * Gets the default crypto engine.
	 *
	 * @return The default crypto engine.
	 */
	public static CryptoEngine defaultEngine() {
		return DEFAULT_ENGINE;
	}
}
