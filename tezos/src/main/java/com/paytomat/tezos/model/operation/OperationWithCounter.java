package com.paytomat.tezos.model.operation;

import java.util.Map;

/**
 * created by Alex Ivanov on 2019-04-11.
 */
public class OperationWithCounter {

    private static AbstractOperation deduceOperation(Map<String, Object> map) {
        String kind = (String) map.get("kind");
        if (kind == null) throw new IllegalArgumentException("Unknown operation provided");
        switch (kind) {
            case Operations.KIND_TRANSACTION:
                return new TransactionOperation(map);
            case Operations.KIND_REVEAL:
                return new RevealOperation(map);
        }
        return null;
    }

    /// The internal operation.
    private final AbstractOperation operation;

    /// The address counter for the internal operation.
    private final String counter;

    public OperationWithCounter(AbstractOperation operation, int counter) {
        this.operation = operation;
        this.counter = String.valueOf(counter);
    }

    public OperationWithCounter(Map<String, Object> map) {
        this.operation = deduceOperation(map);
        this.counter = (String) map.get("counter");
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = operation.toMap();
        map.put("counter", counter);
        return map;
    }

}
