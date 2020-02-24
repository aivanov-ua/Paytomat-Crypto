package com.paytomat.bip44generator.extendedkey;

/**
 * created by Alex Ivanov on 2020-02-24.
 */
class P2PKHExtendedJKeyInfo implements ExtendedKeyInfo {

    @Override
    public int getPrivate() {
        return 0x0488ade4;
    }

    @Override
    public int getPublic() {
        return 0x0488b21e;
    }
}
