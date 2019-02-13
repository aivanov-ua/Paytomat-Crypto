package com.paytomat.eos.transaction.actiondata;

import com.paytomat.core.util.ByteSerializer;

import static com.paytomat.eos.Eos.encodeName;

/**
 * created by Alex Ivanov on 2019-02-11.
 */
public class BuyRamBytesActionData extends ActionData {

    private final String payer;
    private final String receiver;
    private final int bytesCount;

    public BuyRamBytesActionData(String payer, String receiver, int bytesCount) {
        this.payer = payer;
        this.receiver = receiver;
        this.bytesCount = bytesCount;
    }

    @Override
    public String getAuthorization() {
        return payer;
    }

    @Override
    public byte[] serialize() {
        return ByteSerializer.create()
                .write(encodeName(payer))
                .write(encodeName(receiver))
                .writeLE(bytesCount)
                .serialize();
    }
}
