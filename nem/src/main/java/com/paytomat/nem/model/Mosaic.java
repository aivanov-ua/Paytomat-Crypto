package com.paytomat.nem.model;

import java.util.Objects;

/**
 * created by Alex Ivanov on 2019-01-25.
 */
public class Mosaic implements Comparable<Mosaic> {

    public final String namespace;
    public final String name;
    public final int divisibility;
    public final long totalSupply;


    public Mosaic(String namespace, String name, int divisibility, long totalSupply) {
        this.namespace = namespace;
        this.name = name;
        this.divisibility = divisibility;
        this.totalSupply = totalSupply;
    }

    public String getFullName() {
        return namespace + ":" + name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mosaic mosaic = (Mosaic) o;
        return divisibility == mosaic.divisibility &&
                Double.compare(mosaic.totalSupply, totalSupply) == 0 &&
                Objects.equals(namespace, mosaic.namespace) &&
                Objects.equals(name, mosaic.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace, name, divisibility, totalSupply);
    }

    @Override
    public String toString() {
        return "Mosaic{" +
                "namespaceId='" + namespace + '\'' +
                ", name='" + name + '\'' +
                ", divisibility=" + divisibility +
                ", totalSupply=" + totalSupply +
                '}';
    }

    @Override
    public int compareTo(Mosaic mosaic) {
        return getFullName().compareTo(mosaic.getFullName());
    }
}
