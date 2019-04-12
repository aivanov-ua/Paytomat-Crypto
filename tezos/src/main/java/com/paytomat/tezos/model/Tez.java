package com.paytomat.tezos.model;

import com.paytomat.tezos.Constants;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * created by Alex Ivanov on 2019-04-11.
 */
public class Tez {

    private static final BigDecimal TEZ_IN_UTEZ = BigDecimal.TEN.pow(Constants.DECIMAL_DIGIT_COUNT);
    public static final Tez ZERO = new Tez(0.0);

    public static Tez fromRPCFormat(String utezValue) {
        return new Tez(new BigDecimal(utezValue).divide(TEZ_IN_UTEZ, Constants.DECIMAL_DIGIT_COUNT, RoundingMode.DOWN));
    }

    private final BigInteger utezAmount;

    public Tez(double tezValue) {
        this(BigDecimal.valueOf(tezValue));
    }

    public Tez(String tezValue) {
        this(new BigDecimal(tezValue));
    }

    public Tez(BigDecimal tezValue) {
        this.utezAmount = tezValue.multiply(TEZ_IN_UTEZ).toBigInteger();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Tez)) return false;
        return utezAmount.equals(((Tez) o).utezAmount);
    }

    public String toRPC() {
        return utezAmount.toString();
    }

    public String toDisplayString() {
        return new BigDecimal(utezAmount).divide(TEZ_IN_UTEZ, Constants.DECIMAL_DIGIT_COUNT, RoundingMode.DOWN).toPlainString();
    }

    @Override
    public String toString() {
        return toDisplayString();
    }
}
