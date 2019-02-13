package com.paytomat.bip44generator;

import java.util.Objects;

/**
 * created by Alex Ivanov on 2019-02-12.
 */
public class GeneratorModel {

    private final String path;
    private final String mnemonic;
    private final String privateKey;

    public GeneratorModel(String path, String mnemonic, String privateKey) {
        this.path = path;
        this.mnemonic = mnemonic;
        this.privateKey = privateKey;
    }

    public String getPath() {
        return path;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeneratorModel that = (GeneratorModel) o;
        return Objects.equals(path, that.path) &&
                Objects.equals(mnemonic, that.mnemonic) &&
                Objects.equals(privateKey, that.privateKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, mnemonic, privateKey);
    }

    @Override
    public String toString() {
        return "Model{" +
                "path='" + path + '\'' +
                "mnemonic='" + mnemonic + '\'' +
                ", privateKey='" + privateKey + '\'' +
                '}';
    }
}
