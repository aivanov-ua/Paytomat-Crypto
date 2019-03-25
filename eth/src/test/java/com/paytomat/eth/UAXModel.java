package com.paytomat.eth;

import java.util.Objects;

/**
 * created by Alex Ivanov on 3/25/19.
 */
public class UAXModel {

    final String message;
    final byte v;
    final String r;
    final String s;

    public UAXModel(String message, byte v, String r, String s) {
        this.message = message;
        this.v = v;
        this.r = r;
        this.s = s;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UAXModel uaxModel = (UAXModel) o;
        return v == uaxModel.v &&
                Objects.equals(message, uaxModel.message) &&
                Objects.equals(r, uaxModel.r) &&
                Objects.equals(s, uaxModel.s);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, v, r, s);
    }

    @Override
    public String toString() {
        return "UAXModel{" +
                "message='" + message + '\'' +
                ", v=" + v +
                ", r='" + r + '\'' +
                ", s='" + s + '\'' +
                '}';
    }
}
