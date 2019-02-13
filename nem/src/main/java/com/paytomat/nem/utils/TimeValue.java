package com.paytomat.nem.utils;

import static com.paytomat.nem.constants.Constants.DEFAULT_DEADLINE;

public final class TimeValue implements Comparable<TimeValue> {
    private final int _seconds;

    public static final TimeValue INVALID = new TimeValue(-1);

    public TimeValue(final int seconds) {
        this._seconds = seconds;
    }

    public int getValue() {
        return _seconds;
    }

    public static TimeValue fromValue(final int value) {
        return new TimeValue(value);
    }

    public TimeValue add(final TimeValue value) {
        return new TimeValue(_seconds + value._seconds);
    }

    public TimeValue addDefaultDeadline() {
        return add(DEFAULT_DEADLINE);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimeValue timeValue = (TimeValue)o;

        return _seconds == timeValue._seconds;
    }

    @Override
    public int hashCode() {
        return _seconds;
    }

    @Override
    public int compareTo(final TimeValue another) {
        if (this.equals(another)) { return 0;}
        return this._seconds > another._seconds ? 1 : -1;
    }

    @Override
    public String toString() {
        return NumberUtils.toString(_seconds);
    }
}