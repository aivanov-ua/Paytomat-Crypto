package com.paytomat.tezos;

import com.paytomat.tezos.model.FeeModel;
import com.paytomat.tezos.model.Tez;
import com.paytomat.tezos.model.operation.TransactionOperation;

import java.util.Map;

/**
 * created by Alex Ivanov on 2019-04-11.
 */
public class TransactionHelper {
    public static Map<String, Object> prepareTransferOperation(String from, String to, Tez amount, FeeModel fee) {
        return new TransactionOperation(amount, from, to, fee).toMap();
    }
}