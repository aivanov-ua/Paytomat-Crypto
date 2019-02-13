package com.paytomat.nem.model;

import com.paytomat.nem.constants.MessageType;
import com.paytomat.nem.crypto.CryptoException;
import com.paytomat.nem.crypto.PrivateKey;
import com.paytomat.nem.crypto.PublicKey;
import com.paytomat.nem.utils.Ed25519Helper;

import static com.paytomat.nem.constants.Constants.MAX_MESSAGE_LENGTH_BYTES;


public final class MessageDraft {

    public static boolean isLengthValid(final String message, final boolean encrypted) {
        if (message == null) { return true; }
        final int length = encrypted ? Ed25519Helper.getEncryptedMessageLength(message) : message.getBytes().length;
        return length <= MAX_MESSAGE_LENGTH_BYTES;
    }

    private MessageType type = MessageType.NOT_ENCRYPTED;
    /**
     * The value is the actual (possibly encrypted) message data.
     */
    private BinaryData payload;

    /**
     * Creates a non-encrypted message from data.
     */
    private MessageDraft(final byte[] data) {
        payload = new BinaryData(data);
    }

    public BinaryData getPayload() {
        return payload;
    }

    public boolean hasPayload() {
        return payload != null && payload.length() > 0;
    }

    public boolean isEncrypted() {
        return type == MessageType.ENCRYPTED;
    }

    public void encryptPayload(final PrivateKey privateKeyAcc1, final PublicKey publicKeyAcc2)
            throws CryptoException {
        if (payload == null || payload.length() == 0) {
            return;
        }
        if (type == MessageType.ENCRYPTED) {
            throw new CryptoException("Already encrypted");
        }
        payload = Ed25519Helper.Ed25519BlockCipherEncrypt(payload, privateKeyAcc1, publicKeyAcc2);
        type = MessageType.ENCRYPTED;
    }

    /**
     * Creates a non-encrypted message from data, or null if data is null or empty.
     */
    public static MessageDraft create(final byte[] data) {
        return data != null && data.length > 0 ? new MessageDraft(data) : null;
    }

    /**
     * @return a (possibly) readable message.
     */
    @Override
    public String toString() {
        return payload != null ? new String(payload.getRaw()) : "null";
    }
}