package com.paytomat.eth;

import com.google.gson.Gson;
import com.paytomat.eth.crypto.Credentials;
import com.paytomat.eth.utils.Numeric;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * created by Alex Ivanov on 2019-02-12.
 */
public class AddressGenerationTest {

    @Test
    public void testEthGen() throws IOException {
        String fileData = FileUtil.readFile(getClass(), "set.txt");
        Gson gson = new Gson();
        String[] data = fileData.split("\n");
        for (int i = 0; i < data.length; i++) {
            String json = data[i];
            if (json.equals("{") || json.equals("}")) continue;
            AddressModel testModel = gson.fromJson(json, AddressModel.class);
            System.out.println((i + 1) + " of " + data.length);
            Credentials credentials = Credentials.create(testModel.privateKey);
            Assert.assertEquals(testModel.publicKey, Numeric.toHexStringWithPrefixZeroPadded(credentials.getEcKeyPair().getPublicKey(), 128));
            Assert.assertEquals(testModel.address, credentials.getAddress());
        }
    }
}
