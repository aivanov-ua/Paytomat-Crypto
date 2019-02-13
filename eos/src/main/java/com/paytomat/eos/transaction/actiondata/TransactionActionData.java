package com.paytomat.eos.transaction.actiondata;

import com.paytomat.eos.EosTransactionException;
import com.paytomat.eos.transaction.EosAsset;
import com.paytomat.core.util.ByteSerializer;

import static com.paytomat.eos.Eos.encodeName;
import static com.paytomat.eos.EosTransactionException.CODE_AMOUNT_TOO_SMALL;

/**
 * created by Alex Ivanov on 2019-02-12.
 */
public class TransactionActionData extends ActionData {

    private final String from;
    private final String to;
    private final EosAsset quantity;
    private final String memo;

    public TransactionActionData(String from, String to, double quantity, String currencyName, byte currencyPrecision, String memo) {
        this.from = from;
        this.to = to;
        this.quantity = new EosAsset(quantity, currencyName, currencyPrecision);
        this.memo = memo;
    }

    @Override
    public String getAuthorization() {
        return from;
    }

    @Override
    public byte[] serialize() {
        byte[] serializedMemoString = memo.getBytes();
        double dust = Math.pow(0.1, quantity.currencyPrecision); // decimal 10^-4 causes overflow, but 0.1^4 is okay...
        if (quantity.quantity < dust)
            throw new EosTransactionException("Quantity is smaller than dust", CODE_AMOUNT_TOO_SMALL);

        return new ByteSerializer()
                .write(encodeName(from))
                .write(encodeName(to))
                .write(quantity.serializedValue())
                .write(quantity.serializedSymbol())
                .writeVarInt32(serializedMemoString.length)
                .write(serializedMemoString)
                .serialize();
    }
}
