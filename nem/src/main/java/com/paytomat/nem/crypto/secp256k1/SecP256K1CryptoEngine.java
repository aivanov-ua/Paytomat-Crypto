package com.paytomat.nem.crypto.secp256k1;

import com.paytomat.nem.crypto.BlockCipher;
import com.paytomat.nem.crypto.CryptoEngine;
import com.paytomat.nem.crypto.Curve;
import com.paytomat.nem.crypto.DsaSigner;
import com.paytomat.nem.crypto.KeyAnalyzer;
import com.paytomat.nem.crypto.KeyGenerator;
import com.paytomat.nem.crypto.KeyPair;

/**
 * Class that wraps the SECP256K1 specific implementation.
 */
public class SecP256K1CryptoEngine implements CryptoEngine {

	@Override
	public Curve getCurve() {
		return SecP256K1Curve.secp256k1();
	}

	@Override
	public DsaSigner createDsaSigner(final KeyPair keyPair) {
		return new SecP256K1DsaSigner(keyPair);
	}

	@Override
	public KeyGenerator createKeyGenerator() {
		return new SecP256K1KeyGenerator();
	}

	@Override
	public BlockCipher createBlockCipher(final KeyPair senderKeyPair, final KeyPair recipientKeyPair) {
		return new SecP256K1BlockCipher(senderKeyPair, recipientKeyPair);
	}

	@Override
	public KeyAnalyzer createKeyAnalyzer() {
		return new SecP256K1KeyAnalyzer();
	}
}
