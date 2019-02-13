package com.paytomat.eos.transaction.actiondata;

import com.paytomat.core.util.Serializable;

/**
 * created by Alex Ivanov on 2019-02-11.
 */
public abstract class ActionData implements Serializable {

    public abstract String getAuthorization();

}
