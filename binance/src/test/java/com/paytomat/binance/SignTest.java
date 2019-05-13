package com.paytomat.binance;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Assert;
import org.junit.Test;

/**
 * created by Alex Ivanov on 2019-05-09.
 */
public class SignTest {

    @Test
    public void testSignature1() {
        byte[] data = Hex.decode("123456812398bb222332010aa469b92e443b6a93f327e1bb162594bf511eb746");
        byte[] privateKey = Hex.decode("bbb456812398bb222332010aa469b92e443b6a93f327e1bbfff594bf511eb746");
        String expectedSignature = "a910d15f9686162ac9f181927daa0947e20e7d3603a08a17e1e5f03d4352700e5567a1c13ffdbabb88fa11cef35fdc6e427c7cb9bd0407dacc453faa40355c7e";
        byte[] signature = Signature.signMessage(data, privateKey);
        Assert.assertEquals(expectedSignature, Hex.toHexString(signature));
    }

    @Test
    public void testSignature2() {
        byte[] data = "  Hello!!!".getBytes();
        byte[] privateKey = Hex.decode("bbb456812398bb222332010aa469b92e443b6a93f327e1bbfff594bf511eb746");
        String expectedSignature = "70086d9e901d00d67dcdd17ed4c2c44711a9b9b6fa77a3b65da45efaa509e4511b8d5b64f6ca8ab69ff13636196b829ec704bedbea559fe46701f3e6e3eeb6a9";
        byte[] signature = Signature.signMessage(data, privateKey);
        Assert.assertEquals(expectedSignature, Hex.toHexString(signature));
    }

}
