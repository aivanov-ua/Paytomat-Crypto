package com.payomat.eos.transaction;

import com.payomat.eos.EosTransactionException;
import com.payomat.eos.util.FormatUtil;
import com.paytomat.core.util.ByteSerializer;
import com.paytomat.core.util.Serializable;

import static com.payomat.eos.EosTransactionException.CODE_INVALID_SYMBOL;
import static com.paytomat.core.util.BytesUtil.toBytesLE;

/**
 * created by Alex Ivanov on 2019-02-11.
 */
public class EosAsset implements Serializable {

    public final double quantity;
    public final String currencyName;
    public final byte currencyPrecision;

    public EosAsset(double quantity, String currencyName, byte currencyPrecision) {
        this.quantity = quantity;
        this.currencyName = currencyName;
        this.currencyPrecision = currencyPrecision;
    }

    public byte[] serializedValue() {
        return toBytesLE(FormatUtil.serializeQuantity(quantity, currencyPrecision));
    }

    public byte[] serializedSymbol() {
        int symbolPad = 7 - currencyName.length();
        if (symbolPad < 0)
            throw new EosTransactionException("Invalid symbol", CODE_INVALID_SYMBOL);

        return new ByteSerializer()
                .write(currencyPrecision)
                .writeLE(currencyName)
                .write(new byte[symbolPad])
                .serialize();
    }

    @Override
    public byte[] serialize() {
        return new ByteSerializer()
                .write(serializedValue())
                .write(serializedSymbol())
                .serialize();
    }
}
