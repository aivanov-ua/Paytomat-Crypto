package com.paytomat.eos.transaction.actiondata;

import com.paytomat.eos.transaction.EosAsset;
import com.paytomat.core.util.ByteSerializer;

import static com.paytomat.eos.Eos.encodeName;

/**
 * created by Alex Ivanov on 2019-02-11.
 */
public class BuyRamEosActionData extends ActionData {

    private final String payer;
    private final String receiver;
    private final EosAsset quantity;

    public BuyRamEosActionData(String payer, String receiver, EosAsset quantity) {
        this.payer = payer;
        this.receiver = receiver;
        this.quantity = quantity;
    }

    @Override
    public String getAuthorization() {
        return payer;
    }

    @Override
    public byte[] serialize() {
        return new ByteSerializer()
                .write(encodeName(payer))
                .write(encodeName(receiver))
                .write(quantity)
                .serialize();
    }

}
