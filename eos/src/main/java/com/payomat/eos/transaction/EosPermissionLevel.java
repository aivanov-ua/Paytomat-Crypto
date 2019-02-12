package com.payomat.eos.transaction;

import com.payomat.eos.Eos;
import com.paytomat.core.util.ByteSerializer;
import com.paytomat.core.util.Serializable;

/**
 * created by Alex Ivanov on 2019-02-11.
 */
public class EosPermissionLevel implements Serializable {

    private final String actor;
    private final String permission;

    public EosPermissionLevel(String actor, String permission) {
        this.actor = actor;
        this.permission = permission;
    }

    @Override
    public byte[] serialize() {
        return new ByteSerializer()
                .write(Eos.encodeName(actor))
                .write(Eos.encodeName(permission))
                .serialize();
    }
}
