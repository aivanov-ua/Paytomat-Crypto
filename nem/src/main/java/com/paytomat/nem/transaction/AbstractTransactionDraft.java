package com.paytomat.nem.transaction;

import com.paytomat.core.util.BytesUtil;
import com.paytomat.nem.constants.NetworkVersion;
import com.paytomat.nem.constants.TransactionType;
import com.paytomat.nem.crypto.CryptoException;
import com.paytomat.nem.crypto.PublicKey;
import com.paytomat.nem.model.Xems;
import com.paytomat.nem.utils.TimeValue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static com.paytomat.nem.constants.Constants.NEM_GENESIS_BLOCK_TIME_MILLIS;

/**
 * created by Alex Ivanov on 6/1/18.
 */
public abstract class AbstractTransactionDraft {

    protected final int version;
    public TimeValue timestamp = TimeValue.INVALID;
    protected TimeValue deadline = TimeValue.INVALID;

    public PublicKey signer;
    public Xems fee;

    public AbstractTransactionDraft(int transactionVersion, final PublicKey signer, boolean isTestnet) {
        this.version = transactionVersion | NetworkVersion.provideNetwork(isTestnet).version << 24;
        this.signer = signer;
    }

    public void setFee(Xems fee) {
        if (fee == null) {
            this.fee = null;
            return;
        }
        if (calculateMinimumFee().isMoreThan(fee)) {
            throw new CryptoException("Fee is smaller than minimum");
        }
        this.fee = fee;
    }

    public abstract Xems calculateMinimumFee();

    public abstract TransactionType getType();

    public final void serialize(final ByteArrayOutputStream outputStream) throws CryptoException {
        try {
            if (timestamp.equals(TimeValue.INVALID)) {
                timestamp = getNetworkTimeSeconds(System.currentTimeMillis());
            }
            if (deadline.equals(TimeValue.INVALID)) {
                deadline = timestamp.addDefaultDeadline();
            }
            if (fee == null) {
                fee = calculateMinimumFee();
            }

            writeAsLeBytes(outputStream, getType().value);
            writeAsLeBytes(outputStream, version);
            writeAsLeBytes(outputStream, timestamp.getValue());
            writeAsLeBytes(outputStream, signer.getRaw().length);
            outputStream.write(signer.getRaw());
            writeAsLeBytes(outputStream, fee.getAsMicro());
            writeAsLeBytes(outputStream, deadline.getValue());
            serializeAdditional(outputStream);
        } catch (IOException e) {
            throw new CryptoException("Failed to serialize transaction", e);
        }
    }

    public static TimeValue getNetworkTimeSeconds(long currentTime) {
        return new TimeValue((int) ((currentTime - NEM_GENESIS_BLOCK_TIME_MILLIS) / 1000));
    }

    protected abstract void serializeAdditional(final ByteArrayOutputStream outputStream)
            throws IOException;

    protected void writeAsLeBytes(final OutputStream outputStream, final int value)
            throws IOException {
        outputStream.write(BytesUtil.toBytesLE(value));
    }

    protected void writeAsLeBytes(final OutputStream outputStream, final long value)
            throws IOException {
        outputStream.write(BytesUtil.toBytesLE(value));
    }

}
