package com.paytomat.bip44generator.extendedkey;

/**
 * created by Alex Ivanov on 2020-02-24.
 */
public class ExtendedKeyInfoFactory {

    public static ExtendedKeyInfo getInfo(boolean isP2PKH) {
        if (isP2PKH) {
            return new P2PKHExtendedJKeyInfo();
        } else {
            return new P2SHExtendedKeyInfo();
        }
    }

}
