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
        SignatureResult signatureResult = Signature.signForgedOperation(Hex.decode(dataToSign), secretKey);
        OperationWithCounter[] operationArray = new OperationWithCounter[operations.length];
        for (int i = 0; i < operations.length; i++) {
            operationArray[i] = new OperationWithCounter(operations[i]);
        }
        return new Transaction(branch, protocol, operationArray, signatureResult);
    }

    private final String branch;
    private final String protocol;
    private final OperationWithCounter[] operations;
    private final SignatureResult signature;

    public Transaction(String branch, String protocol, OperationWithCounter[] operations, SignatureResult signature) {
        this.branch = branch;
        this.protocol = protocol;
        this.operations = operations;
        this.signature = signature;
    }

    public SignatureResult getSignature() {
        return signature;
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
        map.put("signature", signature.edsig);
        return map;
    }

}
