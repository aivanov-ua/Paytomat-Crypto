package com.paytomat.eos.transaction;

import com.paytomat.core.util.ByteSerializer;
import com.paytomat.core.util.Serializable;

import static com.paytomat.eos.Eos.encodeName;

/**
 * created by Alex Ivanov on 2019-02-12.
 */
public class EosActionAuthorization implements Serializable {

    private final String actor;
    private final String permission;

    private EosActionAuthorization(String actor, String permission) {
        this.actor = actor;
        this.permission = permission;
    }

    public EosActionAuthorization(String actor, EosActionAuthorizationPermission permission) {
        this(actor, permission.getValue());
    }

    public String getActor() {
        return actor;
    }

    public String getPermission() {
        return permission;
    }

    @Override
    public byte[] serialize() {
        return new ByteSerializer()
                .write(encodeName(actor))
                .write(encodeName(permission))
                .serialize();
    }
}
