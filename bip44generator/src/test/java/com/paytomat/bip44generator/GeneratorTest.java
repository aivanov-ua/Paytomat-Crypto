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

    @Test
    public void testSeedOfDifferentLength() {
        String[] words12 = new String[]{"lab", "verify", "deposit", "style", "avoid", "throw", "bean", "claw", "dawn", "dutch", "climb", "mention"};
        MasterSeed masterSeed = Bip44Generator.generateSeedFromWordList(words12);
        Assert.assertEquals("cdd0e94c0766af9fdca70319b956521e62346fc876871cbcd70041a16b4dbcad0062dcd9169b903ab89531c53b29d618307b0c3adbdd8e8747bdc8218f3f63c3", Hex.toHexString(masterSeed.getBytes()));

        String[] words15 = new String[]{"stick", "attitude", "tool", "boost", "expect", "exchange", "winner", "tent", "sad", "broccoli", "pencil", "divide", "annual", "bubble", "pet"};
        masterSeed = Bip44Generator.generateSeedFromWordList(words15);
        Assert.assertEquals("66a916c662e359479395f2e3a758539b0fed34a532d203cac8c0d9ecffa2ac7ea877a547eb269a80327af8c27081be80c5e0693256033364b7c7b6c656f054f1", Hex.toHexString(masterSeed.getBytes()));

        String[] words18 = new String[]{"shift", "patrol", "flat", "panda", "text", "tired", "olympic", "inhale", "sadness", "void", "deer", "wagon", "rice", "collect", "valve", "guitar", "path", "ten"};
        masterSeed = Bip44Generator.generateSeedFromWordList(words18);
        Assert.assertEquals("ad6a5f0f1d7b7cfb9e211fefe523c11e36173edf365dfa7d24ef26b1f3b44cb5cb4d07fd29570cc9813eeb4bfec2d657a2cebd4f2c7c7a8924555b51672c86a1", Hex.toHexString(masterSeed.getBytes()));

        String[] words21 = new String[]{"trial", "sing", "pulp", "fall", "eternal", "song", "equip", "panic", "cart", "hire", "state", "market", "topic", "maid", "student", "finish", "shy", "salad", "pear", "elbow", "friend"};
        masterSeed = Bip44Generator.generateSeedFromWordList(words21);
        Assert.assertEquals("4e0455ac029c7ca243a918e82343672a78d7382e019ee7a469f47b2a90d983c3b1b3ba8858c4bf30082ab9d452f52e9cbbd78f981e8999f7d86e235dda87c26c", Hex.toHexString(masterSeed.getBytes()));

        String[] words24 = new String[]{"above", "hurry", "dumb", "bundle", "people", "portion", "dismiss", "timber", "win", "sorry", "item", "camera", "desert", "ridge", "extend", "weather", "picture", "exile", "rent", "enter", "warm", "cook", "time", "manual"};
        masterSeed = Bip44Generator.generateSeedFromWordList(words24);
        Assert.assertEquals("b0f7b01db263070430dca20d919197e0299af5d82216e75c69658e9fb19b3dc0e5bbba5fdd94e7c2d34fbece13205c24fdb88bbd4cd7f0456d59006ae8c048f3", Hex.toHexString(masterSeed.getBytes()));
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
