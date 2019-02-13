package com.paytomat.waves;

import com.google.gson.Gson;
import com.paytomat.core.util.Base58;

import org.whispersystems.curve25519.Curve25519;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.paytomat.waves.Asset.isWaves;

public class Transaction {
    private static final byte TRANSFER = 4;

    private static final int MIN_BUFFER_SIZE = 120;
    private static final Curve25519 cipher = Curve25519.getInstance(Curve25519.BEST);

    public final String id;
    private final String signature;
    public final Map<String, Object> data;

    private Transaction(PrivateKeyAccount account, ByteBuffer buffer, Object... items) {
        byte[] bytes = toBytes(buffer);
        this.id = hash(bytes);
        this.signature = sign(account, bytes);

        HashMap<String, Object> map = new HashMap<>();
        for (int i = 0; i < items.length; i += 2) {
            Object value = items[i + 1];
            if (value != null) {
                map.put((String) items[i], value);
            }
        }
        this.data = Collections.unmodifiableMap(map);
    }

    public String getJson() {
        HashMap<String, Object> toJson = new HashMap<>(data);
        toJson.put("id", id);
        toJson.put("signature", signature);
        return new Gson().toJson(toJson);
    }

    private static byte[] toBytes(ByteBuffer buffer) {
        byte[] bytes = new byte[buffer.position()];
        buffer.position(0);
        buffer.get(bytes);
        return bytes;
    }

    private static String hash(byte[] bytes) {
        return Base58.encode(Hash.hash(bytes, 0, bytes.length, Hash.BLAKE2B256));
    }

    private static String sign(PrivateKeyAccount account, byte[] bytes) {
        return Base58.encode(cipher.calculateSignature(account.getPrivateKey(), bytes));
    }

    private static void putAsset(ByteBuffer buffer, String assetId) {
        if (isWaves(assetId)) {
            buffer.put((byte) 0);
        } else {
            buffer.put((byte) 1).put(Base58.decode(assetId));
        }
    }

    public static Transaction makeTransferTx(PrivateKeyAccount account, String toAddress,
                                             long amount, String assetId, long fee, String feeAssetId, long timestamp) {
        byte[] attachmentBytes = "".getBytes();
        int datalen = (isWaves(assetId) ? 0 : 32) +
                (isWaves(feeAssetId) ? 0 : 32) +
                attachmentBytes.length + MIN_BUFFER_SIZE;

        ByteBuffer buf = ByteBuffer.allocate(datalen);
        buf.put(TRANSFER).put(account.getPublicKey());
        putAsset(buf, assetId);
        putAsset(buf, feeAssetId);
        buf.putLong(timestamp).putLong(amount).putLong(fee).put(Base58.decode(toAddress))
                .putShort((short) attachmentBytes.length).put(attachmentBytes);

        return new Transaction(account, buf,
                "senderPublicKey", Base58.encode(account.getPublicKey()),
                "recipient", toAddress,
                "amount", amount,
                "assetId", Asset.toJsonObject(assetId),
                "fee", fee,
                "feeAssetId", Asset.toJsonObject(feeAssetId),
                "timestamp", timestamp,
                "attachment", Base58.encode(attachmentBytes));
    }

}
