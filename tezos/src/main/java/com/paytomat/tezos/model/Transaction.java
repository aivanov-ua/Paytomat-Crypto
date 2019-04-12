package com.paytomat.tezos.model;

import com.paytomat.tezos.SecretKey;
import com.paytomat.tezos.Signature;
import com.paytomat.tezos.model.operation.OperationWithCounter;

import org.bouncycastle.util.encoders.Hex;

import java.util.HashMap;
import java.util.Map;

/**
 * created by Alex Ivanov on 2019-04-11.
 */
public class Transaction {

    public static Transaction create(String branch, String protocol, Map<String, Object>[] operations, String dataToSign, SecretKey secretKey) {
        String signature = Signature.signForgedOperation(Hex.decode(dataToSign), secretKey).edsig;
        OperationWithCounter[] operationArray = new OperationWithCounter[operations.length];
        for (int i = 0; i < operations.length; i++) {
            operationArray[i] = new OperationWithCounter(operations[i]);
        }
        return new Transaction(branch, protocol, operationArray, signature);
    }

    private final String branch;
    private final String protocol;
    private final OperationWithCounter[] operations;
    private final String signature;

    public Transaction(String branch, String protocol, OperationWithCounter[] operations, String signature) {
        this.branch = branch;
        this.protocol = protocol;
        this.operations = operations;
        this.signature = signature;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("branch", branch);
        map.put("protocol", protocol);
        Map[] contents = new Map[operations.length];
        for (int i = 0; i < operations.length; i++) {
            contents[i] = operations[i].toMap();
        }
        map.put("contents", contents);
        map.put("signature", signature);
        return map;
    }

}
