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
        byte[] signature = Signer.signMessage(data, privateKey);
        Assert.assertEquals(expectedSignature, Hex.toHexString(signature));
    }

    @Test
    public void testSignature2() {
        byte[] data = Hex.decode("7b226163636f756e745f6e756d626572223a2231222c22636861696e5f6964223a22626e62636861696e2d31303030222c226d656d6f223a22222c226d736773223a5b7b226964223a22423635363144434331303431333030353941374330384634384336343631304331463646393036342d3130222c226f7264657274797065223a322c227072696365223a3130303030303030302c227175616e74697479223a313230303030303030302c2273656e646572223a22626e63316b6574706d6e71736779637174786e7570723667636572707073306b6c797279687a36667a6c222c2273696465223a312c2273796d626f6c223a224254432d3543345f424e42222c2274696d65696e666f726365223a317d5d2c2273657175656e6365223a2239227d");
        byte[] privateKey = Hex.decode("30c5e838578a29e3e9273edddd753d6c9b38aca2446dd84bdfe2e5988b0da0a1");
        String expectedSignature = "9c0421217ef92d556a14e3f442b07c85f6fc706dfcd8a72d6b58f05f96e95aa226b10f7cf62ccf7c9d5d953fa2c9ae80a1eacaf0c779d0253f1a34afd17eef34";
        byte[] signature = Signer.signMessage(data, privateKey);
        Assert.assertEquals(expectedSignature, Hex.toHexString(signature));
    }

}
