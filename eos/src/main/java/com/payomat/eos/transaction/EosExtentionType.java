package com.payomat.eos.transaction;

import com.payomat.eos.EosTransactionException;
import com.paytomat.core.util.Serializable;

import static com.payomat.eos.EosTransactionException.CODE_NOT_IMPLEMENTED;

/**
 * created by Alex Ivanov on 2019-02-12.
 */
public class EosExtentionType implements Serializable {

    private final char type;
    private final byte[] data;

    public EosExtentionType(char type, byte[] data) {
        this.type = type;
        this.data = data;
    }

    @Override
    public byte[] serialize() {
        throw new EosTransactionException("EOS Extensions are not implemented yet", CODE_NOT_IMPLEMENTED);
    }

    public char getType() {
        return type;
    }

    public byte[] getData() {
        return data;
    }

}
