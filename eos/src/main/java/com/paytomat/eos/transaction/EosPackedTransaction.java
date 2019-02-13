package com.paytomat.eos.transaction;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * created by Alex Ivanov on 2019-02-12.
 */
public class EosPackedTransaction {

    private static final String KEY_SIGNATURES = "signatures";
    private static final String KEY_COMPRESSION = "compression";
    private static final String KEY_PACKED_CONTEXT_FREE_DATA = "packed_context_free_data";
    private static final String KEY_PACKED_TRX = "packed_trx";

    private final String[] signatures;
    private final EosTransactionCompression compression;
    private final String packedContextFreeData;
    private final String packedTrx;

    public EosPackedTransaction(String[] signatures, EosTransactionCompression compression, String packedContextFreeData, String packedTrx) {
        this.signatures = signatures;
        this.compression = compression;
        this.packedContextFreeData = packedContextFreeData;
        this.packedTrx = packedTrx;
    }

    public String[] getSignatures() {
        return signatures;
    }

    public EosTransactionCompression getCompression() {
        return compression;
    }

    public String getPackedContextFreeData() {
        return packedContextFreeData;
    }

    public String getPackedTrx() {
        return packedTrx;
    }

    public String toJson() {
        return new Gson().toJson(toMap());
    }

    public Map<String, Object> toMap() {
        Map<String, Object> res = new HashMap<>();
        res.put(KEY_SIGNATURES, signatures);
        res.put(KEY_COMPRESSION, compression.getValue());
        res.put(KEY_PACKED_CONTEXT_FREE_DATA, packedContextFreeData);
        res.put(KEY_PACKED_TRX, packedTrx);
        return res;
    }
}
