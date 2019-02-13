package com.paytomat.eos.transaction.actiondata;

import com.paytomat.eos.transaction.EosAsset;
import com.paytomat.core.util.ByteSerializer;

import static com.paytomat.eos.Eos.encodeName;

/**
 * created by Alex Ivanov on 2019-02-11.
 */
public class DelegateBwActionData extends ActionData {

    private final String from;
    private final String receiver;
    private final EosAsset stakeNetQuantity;
    private final EosAsset stakeCpuQuantity;
    private final boolean transfer;

    public DelegateBwActionData(String from, String receiver, EosAsset stakeNetQuantity, EosAsset stakeCpuQuantity, boolean transfer) {
        this.from = from;
        this.receiver = receiver;
        this.stakeNetQuantity = stakeNetQuantity;
        this.stakeCpuQuantity = stakeCpuQuantity;
        this.transfer = transfer;
    }

    @Override
    public String getAuthorization() {
        return from;
    }

    @Override
    public byte[] serialize() {
        return new ByteSerializer()
                .write(encodeName(from))
                .write(encodeName(receiver))
                .write(stakeNetQuantity)
                .write(stakeCpuQuantity)
                .write(transfer)
                .serialize();
    }

    @Override
    public String toString() {
        return "DelegateBwActionData{" +
                "from='" + from + '\'' +
                ", receiver='" + receiver + '\'' +
                ", stakeNetQuantity=" + stakeNetQuantity +
                ", stakeCpuQuantity=" + stakeCpuQuantity +
                ", transfer=" + transfer +
                '}';
    }
}
