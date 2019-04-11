package com.paytomat.nem.crypto.ed25519;

import com.paytomat.nem.crypto.CryptoEngines;
import com.paytomat.nem.crypto.KeyGenerator;
import com.paytomat.nem.crypto.KeyPair;
import com.paytomat.nem.crypto.PrivateKey;
import com.paytomat.nem.crypto.PublicKey;
import com.paytomat.nem.crypto.ed25519.arithmetic.Ed25519EncodedFieldElement;
import com.paytomat.nem.crypto.ed25519.arithmetic.Ed25519Group;
import com.paytomat.nem.crypto.ed25519.arithmetic.Ed25519GroupElement;
import com.paytomat.nem.utils.ArrayUtils;

import java.security.SecureRandom;

/**
 * Implementation of the key generator for Ed25519.
 */
public class Ed25519KeyGenerator implements KeyGenerator {
    private final SecureRandom random;

    public Ed25519KeyGenerator() {
        this.random = new SecureRandom();
    }

    @Override
    public KeyPair generateKeyPair() {
        final byte[] seed = new byte[32];
        this.random.nextBytes(seed);

        // seed is the private key.
        final PrivateKey privateKey = new PrivateKey(ArrayUtils.toBigInteger(seed));

        return new KeyPair(privateKey, CryptoEngines.defaultEngine());
    }

    @Override
    public PublicKey derivePublicKey(final PrivateKey privateKey) {
        final Ed25519EncodedFieldElement a = Ed25519Utils.prepareForScalarMultiply(privateKey);

        // a * base point is the public key.
        final Ed25519GroupElement pubKey = Ed25519Group.BASE_POINT.scalarMultiply(a);

        // verification of signatures will be about twice as fast when pre-calculating
        // a suitable table of group elements.
        return new PublicKey(pubKey.encode().getRaw());
    }
}
