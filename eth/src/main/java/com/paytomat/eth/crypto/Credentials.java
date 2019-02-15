package com.paytomat.eth.crypto;

import com.paytomat.eth.utils.Numeric;

import java.util.Objects;

/**
 * created by Alex Ivanov on 2019-02-15.
 */
public class Credentials {

    public static Credentials create(ECKeyPair ecKeyPair) {
        String address = Numeric.prependHexPrefix(Keys.getAddress(ecKeyPair));
        return new Credentials(ecKeyPair, address);
    }

    public static Credentials create(String privateKey, String publicKey) {
        return create(new ECKeyPair(Numeric.toBigInt(privateKey), Numeric.toBigInt(publicKey)));
    }

    public static Credentials create(String privateKey) {
        return create(ECKeyPair.create(Numeric.toBigInt(privateKey)));
    }

    private final ECKeyPair ecKeyPair;
    private final String address;

    private Credentials(ECKeyPair ecKeyPair, String address) {
        this.ecKeyPair = ecKeyPair;
        this.address = address;
    }

    public ECKeyPair getEcKeyPair() {
        return ecKeyPair;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Credentials that = (Credentials) o;

        if (!Objects.equals(ecKeyPair, that.ecKeyPair)) {
            return false;
        }

        return Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        int result = ecKeyPair != null ? ecKeyPair.hashCode() : 0;
        result = 31 * result + (address != null ? address.hashCode() : 0);
        return result;
    }
}
