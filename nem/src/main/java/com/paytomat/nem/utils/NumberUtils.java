package com.paytomat.nem.utils;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * created by Alex Ivanov on 6/1/18.
 */
public class NumberUtils {

    private static NumberFormat getNFormatInstanceForCurrentLocale() {
        final NumberFormat instance = NumberFormat.getInstance(Locale.ENGLISH);
        instance.setRoundingMode(RoundingMode.DOWN);
        return instance;
    }

    public static String toAmountString(final double d) {
        final NumberFormat format = getNFormatInstanceForCurrentLocale();
        format.setMinimumFractionDigits(0);
        format.setMaximumFractionDigits(6);
        return format.format(d);
    }

    public static String toString(final int i) {
        return getNFormatInstanceForCurrentLocale().format(i);
    }
}
