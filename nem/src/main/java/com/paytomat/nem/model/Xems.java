package com.paytomat.nem.model;

import com.paytomat.nem.utils.NumberUtils;

/**
 * created by Alex Ivanov on 6/1/18.
 */
public class Xems {

    public static final Xems ZERO = Xems.fromMicro(0);
    public static final Xems ONE = Xems.fromXems(1);
    public static final double MICRO_IN_XEM = 1_000_000.0;

    private long amountMicro;
    private final double microPrecision;

    public static Xems fromXems(double amount) {
        return fromXems(amount, MICRO_IN_XEM);
    }

    public static Xems fromXems(double amount, double microPrecision) {
        return fromMicro((long) (amount * microPrecision), microPrecision);
    }

    public static Xems fromXems(double amount, int precision) {
        return fromXems(amount, Math.pow(10, precision));
    }

    public static Xems fromMicro(long amountMicro) {
        return fromMicro(amountMicro, MICRO_IN_XEM);
    }

    public static Xems fromMicro(long amountMicro, double microPrecision) {
        return new Xems(amountMicro, microPrecision);
    }

    private Xems(long amountMicro, double microPrecision) {
        this.amountMicro = amountMicro;
        this.microPrecision = microPrecision;
    }

    private double getAsFractional() {
        return amountMicro / microPrecision;
    }

    public long getAsMicro() {
        return amountMicro;
    }

    private void setAmount(double amount) {
        amountMicro = (long) (amountMicro * microPrecision);
    }

    public boolean isMoreThan(Xems amount) {
        return this.amountMicro > amount.amountMicro;
    }

    public void addXems(final double amount) {
        amountMicro += (long) (amount * microPrecision);
    }

    public void addXemsMicro(final long amountMicroXem) {
        this.amountMicro += amountMicroXem;
    }

    public void removeXemsMicro(final long amountMicroXem) {
        this.amountMicro -= amountMicroXem;
    }

    public long getIntegerPart() {
        return (long) (this.amountMicro / microPrecision);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Xems xems = (Xems) o;

        return amountMicro == xems.amountMicro;
    }

    @Override
    public int hashCode() {
        return (int) (amountMicro ^ (amountMicro >>> 32));
    }

    public String toFractionalString() {
        return NumberUtils.toAmountString(getAsFractional());
    }

    @Override
    public String toString() {
        return NumberUtils.toAmountString(getAsFractional());
    }

}
