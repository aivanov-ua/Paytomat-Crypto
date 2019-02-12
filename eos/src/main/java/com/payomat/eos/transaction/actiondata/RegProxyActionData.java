package com.payomat.eos.transaction.actiondata;

import com.paytomat.core.util.ByteSerializer;

import static com.payomat.eos.Eos.encodeName;

/**
 * created by Alex Ivanov on 2019-02-12.
 */
public class RegProxyActionData extends ActionData {

    private final String proxy;
    private final boolean isProxy;

    public RegProxyActionData(String proxy, boolean isProxy) {
        this.proxy = proxy;
        this.isProxy = isProxy;
    }

    @Override
    public String getAuthorization() {
        return proxy;
    }

    @Override
    public byte[] serialize() {
        return new ByteSerializer()
                .write(encodeName(proxy))
                .write(isProxy)
                .serialize();
    }

    @Override
    public String toString() {
        return "RegProxyActionData{" +
                "proxy='" + proxy + '\'' +
                ", isProxy=" + isProxy +
                '}';
    }
}
