package com.paytomat.btc.transaction;

import com.paytomat.core.util.ByteSerializer;
import com.paytomat.core.util.Serializable;
import com.paytomat.core.util.VarInt32;

/**
 * created by Alex Ivanov on 7/19/18.
 */
public class Input extends Message implements Serializable {

    public final OutPoint outPoint;
    public final Script scriptSig;
    public final int sequence;

    public Input(OutPoint outPoint, Script scriptSig, int sequence) {
        this.outPoint = outPoint;
        this.scriptSig = scriptSig;
        this.sequence = sequence;
    }

    @Override
    public String toString() {
        return "Input{" +
                "outPoint=" + outPoint +
                ", scriptSig=" + scriptSig +
                ", sequence=" + sequence +
                '}';
    }

    @Override
    public byte[] serialize() {
        byte[] scriptSigBytes = this.scriptSig == null ? new byte[0] : this.scriptSig.bytes;
        return ByteSerializer.create()
                .write(outPoint.serialize())
                .writeVarInt32(scriptSigBytes.length)
                .write(scriptSigBytes)
                .writeLE(sequence)
                .serialize();

    }

    @Override
    public int getMessageSize() {
        byte[] scriptBytes = this.scriptSig == null ? new byte[0] : this.scriptSig.bytes;
        return outPoint.getMessageSize() + VarInt32.size(scriptBytes.length) + scriptBytes.length + 4; //outpoint size(36) + scriptBytes size(mostly 1 byte) + scriptBytes + sequence(4)
    }
}