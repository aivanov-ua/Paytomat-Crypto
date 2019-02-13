package com.paytomat.eos.util;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * created by Alex Ivanov on 2019-02-12.
 */
public class FormatUtil {

    public static Long serializeQuantity(double quantity, byte precision) {
        NumberFormat format = NumberFormat.getInstance(Locale.ENGLISH);
        format.setGroupingUsed(false);
        format.setMinimumFractionDigits(precision);
        format.setMaximumFractionDigits(precision);
        format.setRoundingMode(RoundingMode.UP);
        return Long.parseLong(format.format(quantity).replace(".", ""));
    }

    public static String formatEosValue(long quantity, byte precision, String currencyName) {
        return formatEosValue(quantity, precision) + " " + currencyName;
    }

    public static String formatEosValue(double quantity, byte precision, String currencyName) {
        return formatEosValue(quantity, precision) + " " + currencyName;
    }

    public static String formatEosValue(long quantity, byte precision) {
        NumberFormat format = NumberFormat.getInstance(Locale.ENGLISH);
        format.setGroupingUsed(false);
        format.setMinimumFractionDigits(precision);
        format.setMaximumFractionDigits(precision);
        format.setRoundingMode(RoundingMode.HALF_UP);
        return format.format(convertEosValueToDouble(quantity, precision));
    }

    public static double convertEosValueToDouble(long quantity, byte precision) {
        return quantity / (Math.pow(10, precision));
    }

    public static String formatEosValue(double value, byte precision) {
        NumberFormat format = NumberFormat.getInstance(Locale.ENGLISH);
        format.setGroupingUsed(false);
        format.setMinimumFractionDigits(precision);
        format.setMaximumFractionDigits(precision);
        format.setRoundingMode(RoundingMode.HALF_UP);
        return format.format(value);
    }
}
