package com.paytomat.bip44generator;

import com.google.gson.Gson;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * created by Alex Ivanov on 2019-02-12.
 */
public class GeneratorTest {

    @Test
    public void testGeneration() throws IOException {
        String fileData = readFile(getClass(), "set.txt");
        Gson gson = new Gson();
        String[] data = fileData.split("\n");
        for (int i = 0; i < data.length; i++) {
            String json = data[i];
            if (json.equals("{") || json.equals("}")) continue;
            GeneratorModel testModel = gson.fromJson(json, GeneratorModel.class);
            System.out.println((i + 1) + " of " + data.length);

            HDPath path = HDPath.valueOf(testModel.getPath());
            MasterSeed seed = Bip44Generator.generateSeedFromWordList(testModel.getMnemonic().split(" "));
            HDNode node = HDNode.fromSeed(seed.getBytes());
            node = node.createChildNode(path);

            Assert.assertEquals(testModel.getPrivateKey(), Hex.toHexString(node.getPrivateKey()));
        }
    }

    public String readFile(Class javaClass, String fileName) throws IOException {
        InputStream input = javaClass.getClassLoader().getResourceAsStream(fileName);
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
