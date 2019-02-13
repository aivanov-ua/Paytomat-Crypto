package com.paytomat.eos.transaction;

import com.paytomat.eos.EosTransactionException;
import com.paytomat.eos.util.FormatUtil;
import com.paytomat.core.util.ByteSerializer;
import com.paytomat.core.util.Serializable;

import static com.paytomat.eos.EosTransactionException.CODE_INVALID_SYMBOL;
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

    public static boolean validateEosSymbol(String symbol) {
        int length = symbol.length();
        if (length == 0 || length > 7) return false;
        for (int i = 0; i < length; ++i) {
            if (symbol.charAt(i) < 'A' || symbol.charAt(i) > 'Z') return false;
        }
        return true;
    }
}
