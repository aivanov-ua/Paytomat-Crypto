package com.paytomat.nem.transaction;

import com.paytomat.nem.constants.MessageType;
import com.paytomat.nem.constants.TransactionType;
import com.paytomat.nem.crypto.PublicKey;
import com.paytomat.nem.model.Address;
import com.paytomat.nem.model.MessageDraft;
import com.paytomat.nem.model.Mosaic;
import com.paytomat.nem.model.Xems;
import com.paytomat.nem.utils.FeeHelper;
import com.paytomat.nem.utils.SizeOf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static com.paytomat.nem.constants.TransactionType.TRANSFER_TRANSACTION;

/**
 * created by Alex Ivanov on 6/1/18.
 */
public class TransferTransactionDraft extends AbstractTransactionDraft {

    private static final int VERSION = 2;

    public final Address recipientAddress;
    public final Xems amount;
    public final MessageDraft message;
    public final Mosaic[] mosaics;

    public TransferTransactionDraft(PublicKey signer, Address recipient, Xems amount, MessageDraft message, boolean isTestnet, Mosaic[] mosaics) {
        super(VERSION, signer, isTestnet);
        this.recipientAddress = recipient;
        this.amount = amount != null ? amount : Xems.ZERO;
        this.message = message;
        this.mosaics = mosaics;
    }

    @Override
    public TransactionType getType() {
        return TRANSFER_TRANSACTION;
    }

    @Override
    public Xems calculateMinimumFee() {
        int length = message != null && message.hasPayload() ? message.getPayload().length() : 0;
        Mosaic mosaic = mosaics.length > 0 ? mosaics[0] : null;
        return FeeHelper.transferFee(mosaic, amount, length);
    }

    @Override
    protected void serializeAdditional(final ByteArrayOutputStream os)
            throws IOException {
        final byte[] recipientBytes = recipientAddress.getEncoded().getBytes(StandardCharsets.UTF_8);
        writeAsLeBytes(os, recipientBytes.length);
        os.write(recipientBytes);
        if (mosaics == null || mosaics.length == 0) {
            writeAsLeBytes(os, amount.getAsMicro());
        } else {
            writeAsLeBytes(os, Xems.ONE.getAsMicro());
        }
        writeMessage(os);
        writeMosaic(os);
    }

    private void writeMessage(final ByteArrayOutputStream os) throws IOException {
        if (message != null && message.hasPayload()) {
            // already checked if it has payload.
            final int messageFieldLength = // 4 bytes message type + 4 byte length of payload + payload bytes
                    SizeOf.INT + SizeOf.INT + message.getPayload().length();
            writeAsLeBytes(os, messageFieldLength);
            writeAsLeBytes(os, (message.isEncrypted() ? MessageType.ENCRYPTED : MessageType.NOT_ENCRYPTED).messageType);
            writeAsLeBytes(os, message.getPayload().length());
            os.write(message.getPayload().getRaw());
        } else {
            writeAsLeBytes(os, 0);
        }
    }

    private void writeMosaic(final ByteArrayOutputStream os) throws IOException {
        if (mosaics != null) {
            writeAsLeBytes(os, mosaics.length);
            Arrays.sort(mosaics);
            for (Mosaic mosaic : mosaics) {
                byte[] mosaicNameSpaceIdBytes = mosaic.namespace.getBytes(StandardCharsets.UTF_8);
                byte[] mosaicNameBytes = mosaic.name.getBytes(StandardCharsets.UTF_8);
                int mosaicIdStructLength = SizeOf.INT + mosaicNameSpaceIdBytes.length + SizeOf.INT + mosaicNameBytes.length;
                int mosaicStructLength = SizeOf.INT + mosaicIdStructLength + SizeOf.LONG;

                writeAsLeBytes(os, mosaicStructLength);
                writeAsLeBytes(os, mosaicIdStructLength);
                writeAsLeBytes(os, mosaicNameSpaceIdBytes.length);
                os.write(mosaicNameSpaceIdBytes);
                writeAsLeBytes(os, mosaicNameBytes.length);
                os.write(mosaicNameBytes);
                writeAsLeBytes(os, amount.getAsMicro());
            }
        } else {
            writeAsLeBytes(os, 0);
        }
    }
}
