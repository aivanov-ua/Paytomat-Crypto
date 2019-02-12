package com.paytomat.btc;

import java.util.Objects;

/**
 * created by Alex Ivanov on 2019-02-12.
 */
public class AddressModel {

    public final String address;
    public final String publicKey;
    public final String privateKey;

    public AddressModel(String address, String publicKey, String privateKey) {
        this.address = address;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddressModel that = (AddressModel) o;
        return Objects.equals(address, that.address) &&
                Objects.equals(publicKey, that.publicKey) &&
                Objects.equals(privateKey, that.privateKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, publicKey, privateKey);
    }

    @Override
    public String toString() {
        return "AddressModel{" +
                "address='" + address + '\'' +
                ", publicKey='" + publicKey + '\'' +
                ", privateKey='" + privateKey + '\'' +
                '}';
    }
}
