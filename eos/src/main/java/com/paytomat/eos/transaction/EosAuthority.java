package com.paytomat.eos.transaction;

import com.paytomat.core.util.ByteSerializer;
import com.paytomat.core.util.Serializable;

/**
 * created by Alex Ivanov on 2019-02-11.
 */
public class EosAuthority implements Serializable {

    private final int theresold;
    private final EosKeyWeight[] keys;
    private final EosPermissionLevelWeight[] accounts;
    private final EosWaitWeight[] waits;

    public EosAuthority(int theresold, EosKeyWeight[] keys, EosPermissionLevelWeight[] accounts, EosWaitWeight[] waits) {
        this.theresold = theresold;
        this.keys = keys;
        this.accounts = accounts;
        this.waits = waits;
    }

    @Override
    public byte[] serialize() {
        return new ByteSerializer()
                .writeLE(theresold)
                .write(keys, true)
                .write(accounts, true)
                .write(waits, true)
                .serialize();
    }
}
