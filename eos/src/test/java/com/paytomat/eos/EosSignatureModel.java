package com.paytomat.eos;

import java.util.Objects;

/**
 * created by Alex Ivanov on 10/16/18.
 */
public class EosSignatureModel {

    private final String data;
    private final String privateKey;
    private final String sig;

    public EosSignatureModel(String data, String privateKey, String sig) {
        this.data = data;
        this.privateKey = privateKey;
        this.sig = sig;
    }

    public String getData() {
        return data;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public String getSig() {
        return sig;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EosSignatureModel that = (EosSignatureModel) o;
        return Objects.equals(data, that.data) &&
                Objects.equals(privateKey, that.privateKey) &&
                Objects.equals(sig, that.sig);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data, privateKey, sig);
    }

    @Override
    public String toString() {
        return "EosSignatureModel{" +
                "data='" + data + '\'' +
                ", privateKey='" + privateKey + '\'' +
                ", sig='" + sig + '\'' +
                '}';
    }
}