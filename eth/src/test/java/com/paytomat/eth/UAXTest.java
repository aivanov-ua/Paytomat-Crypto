package com.paytomat.eth;

import com.google.gson.Gson;
import com.paytomat.eth.crypto.Credentials;
import com.paytomat.eth.crypto.ECKeyPair;
import com.paytomat.eth.crypto.Sign;
import com.paytomat.eth.utils.Numeric;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * created by Alex Ivanov on 3/25/19.
 */
public class UAXTest {

    @Test
    public void testUax() throws IOException {
        String fileData = FileUtil.readFile(getClass(), "uax.txt");
        Gson gson = new Gson();
        String[] data = fileData.split("\n");
        ECKeyPair keyPair = Credentials.create("ff6d6f869aa3bfe432a9aa5bad3232e8f20aae6914b38e1357c1a485d41b3f9b").getEcKeyPair();
        for (int i = 0; i < data.length; i++) {
            String json = data[i];
            if (json.equals("{") || json.equals("}")) continue;
            UAXModel testModel = gson.fromJson(json, UAXModel.class);
            System.out.println((i + 1) + " of " + data.length);
            byte[] messageBytes = Hex.decode(testModel.message.replace("0x", ""));
            Sign.SignatureData signature = Sign.signMessage(messageBytes, keyPair, false);
            Assert.assertEquals(testModel.v, signature.getV());
            Assert.assertEquals(testModel.s, Numeric.toHexString(signature.getS()));
            Assert.assertEquals(testModel.r, Numeric.toHexString(signature.getR()));
        }
    }

}
