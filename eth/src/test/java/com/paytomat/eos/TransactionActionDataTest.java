package com.paytomat.eos;

import org.junit.Test;

import com.paytomat.eos.transaction.actiondata.TransactionActionData;

public class TransactionActionDataTest {

    @Test
    public void testSerializationOfMinEosValue() {
        TransactionActionData data = new TransactionActionData("from", "to", 0.0001, "EOS", (byte)4, "MEMO");
        data.serialize();
    }

    @Test(expected = EosTransactionException.class)
    public void testSerializationOfLessThenMinEosValue() {
        TransactionActionData data = new TransactionActionData("from", "to", 0.00001, "EOS", (byte)4, "MEMO");
        data.serialize();
    }

    @Test
    public void testSerializationRegular() {
        TransactionActionData data = new TransactionActionData("from", "to", 0.078, "EOS", (byte)4, "MEMO");
        data.serialize();
    }
}
