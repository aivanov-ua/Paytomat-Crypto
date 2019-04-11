package com.paytomat.tezos.model.operation;

import com.paytomat.tezos.Address;
import com.paytomat.tezos.PublicKey;
import com.paytomat.tezos.model.FeeModel;
import com.paytomat.tezos.model.Tez;

import java.util.Map;

/**
 * created by Alex Ivanov on 2019-04-11.
 */
public class RevealOperation extends AbstractOperation {

    private final String publicKey;

    public RevealOperation(PublicKey publicKey) {
        this(publicKey, null);
    }

    public RevealOperation(Map<String, Object> map) {
        super(map);
        this.publicKey = (String) map.get("public_key");
    }

    public RevealOperation(PublicKey publicKey, FeeModel operationFees) {
        super(new Address(publicKey).toString(), Operations.KIND_REVEAL, operationFees);
        this.publicKey = publicKey.toString();
    }

    @Override
    protected FeeModel defaultFee() {
        return new FeeModel(new Tez("0.001269"), new Tez("0.010000"), Tez.ZERO);
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = super.toMap();
        map.put("public_key", publicKey);
        return map;
    }
}
