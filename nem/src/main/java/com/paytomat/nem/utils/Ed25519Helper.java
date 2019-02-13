package com.paytomat.nem.utils;

import com.paytomat.nem.crypto.KeyPair;
import com.paytomat.nem.crypto.PrivateKey;
import com.paytomat.nem.crypto.PublicKey;
import com.paytomat.nem.crypto.ed25519.Ed25519BlockCipher;
import com.paytomat.nem.model.BinaryData;

public final class Ed25519Helper {

    private static final int AES_BLOCK_SIZE = 16;
    private static final int SALT_LENGTH = 32; // same as public key

    public static BinaryData Ed25519BlockCipherEncrypt(
            final BinaryData input,
            final PrivateKey account1, final PublicKey account2) {
        final KeyPair senderKeyPair = new KeyPair(account1);
        final KeyPair recipientKeyPair = new KeyPair(null, account2);
        final byte[] inputBytes = input.getRaw();
        final byte[] encryptedBytes = new Ed25519BlockCipher(senderKeyPair, recipientKeyPair).encrypt(inputBytes);
        return new BinaryData(encryptedBytes);
    }

    public static int getEncryptedMessageLength(final String message) {
        String msg = message != null ? message : "";
        return getEncryptedMessageLength(msg.getBytes().length);
    }

    private static int getEncryptedMessageLength(final int inputLengthBytes) {
        if (inputLengthBytes == 0) {
            return 0;
        }
        return SALT_LENGTH + AES_BLOCK_SIZE + ((inputLengthBytes / AES_BLOCK_SIZE + 1) * AES_BLOCK_SIZE); // http://stackoverflow.com/questions/3716691/relation-between-input-and-ciphertext-length-in-aes
    }
}
