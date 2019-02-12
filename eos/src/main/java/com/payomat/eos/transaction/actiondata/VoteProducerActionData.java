package com.payomat.eos.transaction.actiondata;

import com.paytomat.core.util.ByteSerializer;

import java.util.Arrays;

import static com.payomat.eos.Eos.encodeName;

/**
 * created by Alex Ivanov on 2019-02-12.
 */
public class VoteProducerActionData extends ActionData {

    private final String voter;
    private final String proxy;
    private final String[] producers;

    public VoteProducerActionData(String voter, String proxy, String[] producers) {
        this.voter = voter;
        this.proxy = proxy;
        if (producers.length < 30) this.producers = producers;
        else throw new IllegalArgumentException("Producers count must be in [0,30]");
    }

    @Override
    public String getAuthorization() {
        return voter;
    }

    @Override
    public byte[] serialize() {
        ByteSerializer serializer = new ByteSerializer()
                .write(encodeName(voter))
                .write(encodeName(proxy))
                .writeVarInt32(producers.length);
        for (String producer : producers) {
            serializer.write(encodeName(producer));
        }
        return serializer.serialize();
    }

    @Override
    public String toString() {
        return "VoteProducerActionData{" +
                "voter='" + voter + '\'' +
                ", proxy='" + proxy + '\'' +
                ", producers=" + Arrays.toString(producers) +
                '}';
    }

}
