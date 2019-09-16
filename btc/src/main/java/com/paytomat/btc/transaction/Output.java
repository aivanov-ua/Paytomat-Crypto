package com.paytomat.btc.transaction;

import com.paytomat.core.util.ByteSerializer;
import com.paytomat.core.util.Serializable;
import com.paytomat.core.util.VarInt32;

/**
 * created by Alex Ivanov on 7/19/18.
 */
public class Output extends Message implements Serializable {

    public final long value;
    public final Script script;

    public Output(long value, Script script) {
        this.value = value;
        this.script = script;
    }

    @Override
    public byte[] serialize() {
        byte[] scriptBytes = this.script == null ? new byte[0] : this.script.bytes;
        return ByteSerializer.create()
                .writeLE(value)
                .writeVarInt32(scriptBytes.length)
                .write(scriptBytes)
                .serialize();
    }

    @Override
    public int getMessageSize() {
        byte[] scriptBytes = this.script == null ? new byte[0] : this.script.bytes;
        return 8 + VarInt32.size(scriptBytes.length) + scriptBytes.length;//value(8) + script size + script bytes
    }
}
