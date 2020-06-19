package com.paytomat.tron;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.paytomat.core.util.HashUtil;
import com.paytomat.tronj.protos.Contract;
import com.paytomat.tronj.protos.Protocol;

import org.bouncycastle.util.encoders.Hex;

import java.util.Calendar;
import java.util.Objects;

import static com.paytomat.tronj.protos.Protocol.Transaction.Contract.ContractType.TransferAssetContract;
import static com.paytomat.tronj.protos.Protocol.Transaction.Contract.ContractType.TransferContract;


/**
 * created by Alex Ivanov on 2019-01-11.
 */
public class TxHelper {

    public static final String ASSET_NAME_TRX = "TRX";

    public static String send(PrivateKey privateKey, String to, long amount, String refBlockBytes, String refBlockHash, String assetName) {
        ByteString ownerBS = ByteString.copyFrom(new Address(privateKey, false).getBytes());
        ByteString toBS = ByteString.copyFrom(new Address(to, false).getBytes());

        Protocol.Transaction.Contract.Builder builder = Protocol.Transaction.Contract.newBuilder();


        if (Objects.equals(assetName, ASSET_NAME_TRX)) {
            Contract.TransferContract transfer = Contract.TransferContract.newBuilder()
                    .setOwnerAddress(ownerBS)
                    .setToAddress(toBS)
                    .setAmount(amount)
                    .build();
            builder.setType(TransferContract)
                    .setParameter(Any.pack(transfer));
        } else {
            ByteString assetNameBs = ByteString.copyFrom(assetName.getBytes());
            Contract.TransferAssetContract transferAsset = Contract.TransferAssetContract.newBuilder()
                    .setOwnerAddress(ownerBS)
                    .setToAddress(toBS)
                    .setAssetName(assetNameBs)
                    .setAmount(amount)
                    .build();
            builder.setType(TransferAssetContract)
                    .setParameter(Any.pack(transferAsset));
        }

        Calendar calendar = Calendar.getInstance();
        long timestamp = calendar.getTimeInMillis();
        calendar.add(Calendar.HOUR, 10);
        long expiration = calendar.getTimeInMillis();
        calendar.clear();

        Protocol.Transaction.raw raw = Protocol.Transaction.raw.newBuilder()
                .addContract(builder)
                .setTimestamp(timestamp)
                .setExpiration(expiration)
                .setRefBlockHash(ByteString.copyFrom(Hex.decode(refBlockHash)))
                .setRefBlockBytes(ByteString.copyFrom(Hex.decode(refBlockBytes)))
                .build();

        byte[] txIdBytes = HashUtil.sha256(raw.toByteArray()).getBytes();

        Signature signature = new Signature(txIdBytes, privateKey);

        byte[] rawTx = Protocol.Transaction.newBuilder()
                .setRawData(raw)
                .addSignature(ByteString.copyFrom(signature.toByteArray()))
                .build()
                .toByteArray();
        return Hex.toHexString(rawTx);
    }
}
