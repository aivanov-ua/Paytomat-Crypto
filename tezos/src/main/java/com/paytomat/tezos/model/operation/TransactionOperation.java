package com.paytomat.tezos.model.operation;

import com.paytomat.tezos.model.FeeModel;
import com.paytomat.tezos.model.Tez;

import java.util.Map;

/**
 * created by Alex Ivanov on 2019-04-11.
 */
public class TransactionOperation extends AbstractOperation {

    private final Tez amount;
    private final String destination;
    private final Map<String, Object> params;

    /**
     * @param amount      The amount of XTZ to transact.
     * @param source      The address that is sending the XTZ.
     * @param destination The address that is receiving the XTZ.
     * @param fee         OperationFees for the transaction. If nil, default fees are used.
     */
    public TransactionOperation(
            Tez amount,
            String source,
            String destination,
            FeeModel fee) {
        this(amount, source, destination, null, fee);
    }

    /**
     * @param amount      The amount of XTZ to transact.
     * @param source      The address that is sending the XTZ.
     * @param destination The address that is receiving the XTZ.
     * @param params      Optional parameters to include in the transaction if the call is being made to a smart contract.
     * @param fee         OperationFees for the transaction. If nil, default fees are used.
     */
    public TransactionOperation(
            Tez amount,
            String source,
            String destination,
            Map<String, Object> params,
            FeeModel fee) {
        super(source, Operations.KIND_TRANSACTION, fee);
        this.amount = amount;
        this.destination = destination;
        this.params = params;
    }

    public TransactionOperation(Map<String, Object> map) {
        super(map);
        this.amount = Tez.fromRPCFormat((String) map.get("amount"));
        this.destination = (String) map.get("destination");
        this.params = (Map<String, Object>) map.get("parameters");
    }


    @Override
    protected FeeModel defaultFee() {
        return new FeeModel(new Tez("0.001272"), new Tez("0.010100"), new Tez("0.000257"));
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = super.toMap();
        map.put("amount", amount.toRPC());
        map.put("destination", destination);
        if (params != null) {
            map.put("parameters", params);
        }
        return map;
    }
}
