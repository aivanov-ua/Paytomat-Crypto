package com.paytomat.bip44generator;

import org.junit.Assert;
import org.junit.Test;

/**
 * created by Alex Ivanov on 2020-02-24.
 */
public class AccountExtendedTest {

    @Test
    public void checkExtendedKeysP2PKH() {
        String seedStr = "wreck wild fat atom nation action carry buyer title famous grant pretty lens fix midnight hood dream wealth easy beyond scout mobile proof potato";
        String expectedPriv = "xprv9xt5ak324bbyZwkC3VUEMYhmFDtgFjANw7Js317KDe7JWBNJtTS8Je7yXocnq5PjbTjsJnV1JbGPUAP2e9dxQe93fmkgSx9vUh9AHNG1pLR";
        String expectedPub = "xpub6BsRzFZutyAGnRpf9X1EigeVoFjAfBtEJLETqPWvmyeHNyhTRzkNrSSTP3tvM8UELHgjBEy2Zny1PMRzbFsW7LUi1cJ4L9V3Cq8vQrWY2no";

        MasterSeed seed = Bip44Generator.generateSeedFromWordList(seedStr.split(" "));
        AccountExtendedKey key = new AccountExtendedKey(seed, 0, true);

        Assert.assertEquals(expectedPub, key.getAccountPublic());
        Assert.assertEquals(expectedPriv, key.getAccountPrivate());
    }

    @Test
    public void checkExtendedKeysP2SH() {
        String seedStr = "wreck wild fat atom nation action carry buyer title famous grant pretty lens fix midnight hood dream wealth easy beyond scout mobile proof potato";
        String expectedPriv = "yprvAHc993Gb5Rebq2gez9XEKYAbPZnY6ZB4kEJbZGadjwU9bUbC6WwdxDATZkrQi67EFHVBf2TBSi9N3QEsndZMzNrrSZTByA3cfLTkwN9VgMa";
        String expectedPub = "ypub6WbVYYoUuoCu3Wm86B4Egg7Kwbd2W1tv7TECMezFJH18UGvLe4FtW1UwR1J6MFkp5pQLQkdRd7B2QDmCcCKxNgGqgCDWQVedTAQWSBxeHnx";

        MasterSeed seed = Bip44Generator.generateSeedFromWordList(seedStr.split(" "));
        AccountExtendedKey key = new AccountExtendedKey(seed, 0, false);

        Assert.assertEquals(expectedPub, key.getAccountPublic());
        Assert.assertEquals(expectedPriv, key.getAccountPrivate());
    }
}
