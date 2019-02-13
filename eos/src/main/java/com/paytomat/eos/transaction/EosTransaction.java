package com.paytomat.eos.transaction;

import com.paytomat.eos.Eos;
import com.paytomat.eos.PrivateKey;
import com.paytomat.eos.signature.Signature;
import com.paytomat.core.util.ByteSerializer;

import org.bouncycastle.util.encoders.Hex;

import java.util.Arrays;

/**
 * created by Alex Ivanov on 2019-02-12.
 */
public class EosTransaction {

    private final PrivateKey privateKey;
    private final String chainIdHex;
    private final int expiration;
    private final short refBlockNum;
    private final int refBlockPrefix;
    private final int netUsageWords;
    private final byte maxCpuUsageMs;
    private final int delaySec;
    private final EosAction[] contextFreeActions;
    private final EosAction[] actions;
    private final EosExtentionType[] transactionExtensions;

    public EosTransaction(PrivateKey privateKey, String chainIdHex, long expiration, short refBlockNum, int refBlockPrefix, int netUsageWords, byte maxCpuUsageMs, int delaySec, EosAction[] contextFreeActions, EosAction[] actions, EosExtentionType[] transactionExtensions) {
        this.privateKey = privateKey;
        this.chainIdHex = chainIdHex;
        this.expiration = (int) (expiration / 1000);
        this.refBlockNum = refBlockNum;
        this.refBlockPrefix = refBlockPrefix;
        this.netUsageWords = netUsageWords;
        this.maxCpuUsageMs = maxCpuUsageMs;
        this.delaySec = delaySec;
        this.contextFreeActions = contextFreeActions;
        this.actions = actions;
        this.transactionExtensions = transactionExtensions;
    }

    public byte[] serialize() {
        return new ByteSerializer()
                .writeLE(expiration)
                .writeLE(refBlockNum)
                .writeLE(refBlockPrefix)
                .writeVarInt32(netUsageWords)
                .write(maxCpuUsageMs)
                .writeVarInt32(delaySec)
                .write(contextFreeActions, true)
                .write(actions, true)
                .write(transactionExtensions, true)
                .serialize();
    }

    public String toSignedJson() {
        return getPackedTx().toJson();
    }

    public EosPackedTransaction getPackedTx() {
        byte[] serialized = serialize();
        byte[] trail = new byte[32];
        Arrays.fill(trail, (byte) 0);
        byte[] signData = new ByteSerializer()
                .writeHex(chainIdHex)
                .write(serialized)
                .write(trail)
                .serialize();

        Signature signature = Eos.signTransactionRaw(signData, privateKey);

        return new EosPackedTransaction(new String[]{signature.toString()},
                EosTransactionCompression.NONE,
                "",
                Hex.toHexString(serialized));
    }
}
