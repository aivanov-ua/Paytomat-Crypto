package com.paytomat.btc;

import com.google.gson.Gson;
import com.paytomat.btc.network.prod.BTCProdNetParams;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * created by Alex Ivanov on 2019-02-12.
 */
public class AddressGenerationTest {

    @Test
    public void testBTCGen() throws IOException {
        String fileData = readFile();
        Gson gson = new Gson();
        String[] data = fileData.split("\n");
        for (int i = 0; i < data.length; i++) {
            String json = data[i];
            if (json.equals("{") || json.equals("}")) continue;
            AddressModel testModel = gson.fromJson(json, AddressModel.class);
            System.out.println((i + 1) + " of " + data.length);
            PrivateKey pk = new PrivateKey(testModel.privateKey);
            KeyPair pair = new KeyPair(pk);
            Assert.assertEquals(testModel.publicKey, pair.publicKey.toString());
            Assert.assertEquals(testModel.address, pair.getAddress(BTCProdNetParams.getParams()).toString());
        }
    }

    private String readFile() throws IOException {
        InputStream input = getClass().getClassLoader().getResourceAsStream("set.txt");
        if (input == null) return "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        StringBuilder out = new StringBuilder();
        String line = reader.readLine();
        while (line != null) {
            out.append(line).append("\n");
            line = reader.readLine();
        }
        reader.close();
        return out.toString();
    }
}
