package com.payomat.eos.transaction.actiondata;

import com.payomat.eos.transaction.EosAuthority;
import com.paytomat.core.util.ByteSerializer;

import static com.payomat.eos.Eos.encodeName;

/**
 * created by Alex Ivanov on 2019-02-11.
 */
public class NewAccountActionData extends ActionData {
    private final String creator;
    private final String name;
    private final EosAuthority owner;
    private final EosAuthority active;

    public NewAccountActionData(String creator, String name, EosAuthority owner, EosAuthority active) {
        this.creator = creator;
        this.name = name;
        this.owner = owner;
        this.active = active;
    }

    @Override
    public String getAuthorization() {
        return creator;
    }

    @Override
    public byte[] serialize() {
        return new ByteSerializer()
                .write(encodeName(creator))
                .write(encodeName(name))
                .write(owner)
                .write(active)
                .serialize();
    }
}
