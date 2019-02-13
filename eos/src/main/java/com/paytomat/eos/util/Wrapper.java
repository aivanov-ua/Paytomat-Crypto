package com.paytomat.eos.util;

/**
 * created by Alex Ivanov on 7/5/18.
 */
public class Wrapper<T> {

    private T obj;

    public Wrapper(T obj) {
        this.obj = obj;
    }

    public T get() {
        return obj;
    }

    public void set(T obj) {
        this.obj = obj;
    }
}