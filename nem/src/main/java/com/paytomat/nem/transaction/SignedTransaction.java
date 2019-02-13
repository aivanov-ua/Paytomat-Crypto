package com.paytomat.nem.transaction;

import com.paytomat.nem.crypto.DsaSigner;
import com.paytomat.nem.model.BinaryData;
import com.paytomat.nem.utils.HexEncoder;

/**
 * Class represents immutable binary data that can be signed.
 */
public final class SignedTransaction extends BinaryData {
    private final byte[] _signature;

    public SignedTransaction(final byte[] rawTransaction, final DsaSigner signer) {
        super(rawTransaction);
        _signature = signer.sign(rawData).getBytes();
    }

    public byte[] getSignature() {
        return _signature;
    }

    public String getSignatureHex() {
        return HexEncoder.getString(_signature);
    }
}
