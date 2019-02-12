package com.payomat.eos.transaction.actiondata;

import com.paytomat.core.util.ByteSerializer;

import static com.payomat.eos.Eos.encodeName;

/**
 * created by Alex Ivanov on 2019-02-12.
 */
public class SellRamBytesActionData extends ActionData {

    private final String account;
    private final long bytesCount;

    public SellRamBytesActionData(String account, int bytesCount) {
        this.account = account;
        this.bytesCount = bytesCount;
    }

    @Override
    public String getAuthorization() {
        return account;
    }

    @Override
    public byte[] serialize() {
        return new ByteSerializer()
                .write(encodeName(account))
                .writeLE(bytesCount)
                .serialize();
    }

}
