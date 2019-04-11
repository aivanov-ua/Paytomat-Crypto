package com.paytomat.tezos.model.operation;

import com.paytomat.tezos.model.FeeModel;
import com.paytomat.tezos.model.Tez;

import java.util.HashMap;
import java.util.Map;

/**
 * created by Alex Ivanov on 2019-04-11.
 */
public abstract class AbstractOperation {

    final String source;
    final String kind;
    final FeeModel feeModel;


    public AbstractOperation(String source, String kind, FeeModel feeModel) {
        this.source = source;
        this.kind = kind;
        this.feeModel = feeModel;
    }

    public AbstractOperation(Map<String, Object> map) {
        this.source = (String) map.get("source");
        this.kind = (String) map.get("kind");

        String storageLimit = (String) map.get("storage_limit");
        String gasLimit = (String) map.get("gas_limit");
        String fee = (String) map.get("fee");
        this.feeModel = new FeeModel(Tez.fromRPCFormat(fee), Tez.fromRPCFormat(gasLimit), Tez.fromRPCFormat(storageLimit));
    }

    protected FeeModel defaultFee() {
        return new FeeModel(Tez.ZERO, Tez.ZERO, Tez.ZERO);
    }

    public boolean requiresReveal() {
        return Operations.KIND_TRANSACTION.equals(kind);
    }


    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("kind", kind);
        map.put("source", source);

        FeeModel fee = feeModel != null ? feeModel : defaultFee();
        map.put("storage_limit", fee.storageLimit.toRPC());
        map.put("gas_limit", fee.gasLimit.toRPC());
        map.put("fee", fee.fee.toRPC());

        return map;
    }
}
