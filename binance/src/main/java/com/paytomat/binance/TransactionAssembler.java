package com.paytomat.binance;

import com.google.protobuf.ByteString;
import com.paytomat.binance.protos.Binance;
import com.paytomat.btc.PrivateKey;
import com.paytomat.core.util.BytesUtil;

/**
 * created by Alex Ivanov on 2019-05-14.
 */
public class TransactionAssembler {

    private final String chainId;
    private final int accountNumber;
    private final long sequence;

    public TransactionAssembler(String chainId, int accountNumber, long sequence) {
        this.chainId = chainId;
        this.accountNumber = accountNumber;
        this.sequence = sequence;
    }

    SignData getSignData(String memo, long source, byte[] data, String from, String to, long amountRaw, String denom) {
        return new SignData(chainId, String.valueOf(accountNumber), String.valueOf(sequence), memo,
                String.valueOf(source), from, to, amountRaw, denom);
    }

    byte[] encodeTransferMessage(SignData msg) {
        Binance.SendOrder proto = msg.getMsgs()[0].toProto();
        return BytesUtil.aminoWrap(proto.toByteArray(), MessageType.Send.getTypePrefixBytes(), false);
    }

    byte[] sign(SignData msg, PrivateKey privateKey) {
        return Signature.signMessage(msg.toByteArray(), privateKey);
    }

    byte[] encodeSignature(byte[] signatureBytes, PrivateKey privateKey) {
        byte[] pubKeyBytes = privateKey.getPublicKey().getBytes();
        byte[] pubKeyPrefix = MessageType.PubKey.getTypePrefixBytes();
        byte[] pubKeySignBytes = new byte[pubKeyBytes.length + pubKeyPrefix.length + 1];
        System.arraycopy(pubKeyPrefix, 0, pubKeySignBytes, 0, pubKeyPrefix.length);
        pubKeySignBytes[pubKeyPrefix.length] = (byte) 33;
        System.arraycopy(pubKeyBytes, 0, pubKeySignBytes, pubKeyPrefix.length + 1, pubKeyBytes.length);

        Binance.Signature stdSignature = Binance.Signature.newBuilder()
                .setPubKey(ByteString.copyFrom(pubKeySignBytes))
                .setSignature(ByteString.copyFrom(signatureBytes))
                .setAccountNumber(accountNumber)
                .setSequence(sequence)
                .build();

        return BytesUtil.aminoWrap(stdSignature.toByteArray(), MessageType.StdSignature.getTypePrefixBytes(), false);
    }

    byte[] encodeTx(String memo, long source, byte[] data, byte[] msg, byte[] signature) {
        Binance.Transaction.Builder stdTxBuilder = Binance.Transaction.newBuilder()
                .addMsgs(ByteString.copyFrom(msg))
                .addSignatures(ByteString.copyFrom(signature))
                .setMemo(memo)
                .setSource(source);
        if (data != null) {
            stdTxBuilder = stdTxBuilder.setData(ByteString.copyFrom(data));
        }
        Binance.Transaction stdTx = stdTxBuilder.build();

        return BytesUtil.aminoWrap(stdTx.toByteArray(), MessageType.StdTx.getTypePrefixBytes(), true);
    }

    public byte[] assemble(String memo, long source, byte[] data, String from, String to, long amountRaw, String denom, PrivateKey pk) {
        SignData signData = getSignData(memo, source, null, from, to, amountRaw, denom);

        byte[] signature = sign(signData, pk);

        byte[] encodedMsg = encodeTransferMessage(signData);
        byte[] encodedSign = encodeSignature(signature, pk);

        return encodeTx(memo, source, data, encodedMsg, encodedSign);
    }
}
