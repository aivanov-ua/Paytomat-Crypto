package com.paytomat.eos.transaction;

import com.paytomat.core.util.ByteSerializer;
import com.paytomat.core.util.Serializable;

/**
 * created by Alex Ivanov on 2019-02-11.
 */
public class EosPermissionLevelWeight implements Serializable {

    private final EosPermissionLevel permissionLevel;
    private final short weight;

    public EosPermissionLevelWeight(EosPermissionLevel permissionLevel, short weight) {
        this.permissionLevel = permissionLevel;
        this.weight = weight;
    }

    @Override
    public byte[] serialize() {
        return new ByteSerializer()
                .write(permissionLevel)
                .writeLE(weight)
                .serialize();
    }
}
