package com.paytomat.bip44generator.extendedkey;

/**
 * created by Alex Ivanov on 2020-02-24.
 */
class P2SHExtendedKeyInfo implements ExtendedKeyInfo {

    @Override
    public int getPrivate() {
        return 0x049d7878;
    }

    @Override
    public int getPublic() {
        return 0x049d7cb2;
    }
}
