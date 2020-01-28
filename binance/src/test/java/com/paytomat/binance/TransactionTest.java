package com.paytomat.binance;

import com.paytomat.btc.PrivateKey;
import com.paytomat.core.util.HashUtil;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Assert;
import org.junit.Test;

/**
 * created by Alex Ivanov on 2019-05-14.
 */
public class TransactionTest {

    @Test
    public void testTx1() {
        String memo = "Hello";
        long source = 1;
        byte[] data = null;
        TransactionAssembler assembler = new TransactionAssembler("Binance-Chain-Tigris", 4844, 10L);
        SignData signData = assembler.getSignData(memo, source, data, "bnb1y0fy2q0k4ueudh8kv4x3k67arc8ccvnkkhr40r", "bnb16vgtc7lvjldcm6m3h675g8vpwnum42h3t2qpkm", 100000, "BNB");

        Assert.assertEquals("{\"account_number\":\"4844\",\"chain_id\":\"Binance-Chain-Tigris\",\"data\":null,\"memo\":\"Hello\",\"msgs\":[{\"inputs\":[{\"address\":\"bnb1y0fy2q0k4ueudh8kv4x3k67arc8ccvnkkhr40r\",\"coins\":[{\"amount\":100000,\"denom\":\"BNB\"}]}],\"outputs\":[{\"address\":\"bnb16vgtc7lvjldcm6m3h675g8vpwnum42h3t2qpkm\",\"coins\":[{\"amount\":100000,\"denom\":\"BNB\"}]}]}],\"sequence\":\"10\",\"source\":\"1\"}", signData.toString());
        Assert.assertEquals("7b226163636f756e745f6e756d626572223a2234383434222c22636861696e5f6964223a2242696e616e63652d436861696e2d546967726973222c2264617461223a6e756c6c2c226d656d6f223a2248656c6c6f222c226d736773223a5b7b22696e70757473223a5b7b2261646472657373223a22626e6231793066793271306b347565756468386b763478336b3637617263386363766e6b6b6872343072222c22636f696e73223a5b7b22616d6f756e74223a3130303030302c2264656e6f6d223a22424e42227d5d7d5d2c226f757470757473223a5b7b2261646472657373223a22626e62313676677463376c766a6c64636d366d336836373567387670776e756d34326833743271706b6d222c22636f696e73223a5b7b22616d6f756e74223a3130303030302c2264656e6f6d223a22424e42227d5d7d5d7d5d2c2273657175656e6365223a223130222c22736f75726365223a2231227d", signData.toHex());

        Assert.assertEquals("fff4d5ecf3dd7f2909a0da5730fe99908b7dbe954f85ba7b1f8c324bd876b081", HashUtil.sha256(signData.toByteArray()).toString());

        PrivateKey pk = new PrivateKey(Hex.decode("835c418a8898bb665052010aa469b92e443b6a93f327e1bb162594bf511eb746"), true);
        byte[] signature = assembler.sign(signData, pk);
        Assert.assertEquals("8f7c94622fd970f2ea75c2396b5735c4457cd4b64686c160c47a66eddbc9df036ecbab8cef110cb2ad141d61baa21c4dfe8d397ecd043f5b51939d169e0aa84a", Hex.toHexString(signature));

        byte[] encodedMsg = assembler.encodeTransferMessage(signData);
        byte[] encodedSign = assembler.encodeSignature(signature, pk);

        byte[] encodedTx = assembler.encodeTx(memo, source, null, encodedMsg, encodedSign);
        Assert.assertEquals("ca01f0625dee0a4a2a2c87fa0a210a1423d24501f6af33c6dcf6654d1b6bdd1e0f8c327612090a03424e4210a08d0612210a14d310bc7bec97db8deb71bebd441d8174f9baaaf112090a03424e4210a08d06126f0a26eb5ae987210209410fcf16a8cc2867e33f56c36678705ce8083b1cd6cdee7932a26788f3ae2d12408f7c94622fd970f2ea75c2396b5735c4457cd4b64686c160c47a66eddbc9df036ecbab8cef110cb2ad141d61baa21c4dfe8d397ecd043f5b51939d169e0aa84a18ec25200a1a0548656c6c6f2001",
                Hex.toHexString(encodedTx));
    }

    @Test
    public void testTx2() {
        String memo = "Hello";
        long source = 1;
        byte[] data = null;
        TransactionAssembler assembler = new TransactionAssembler("Binance-Chain-Tigris", 4844, 11L);
        PrivateKey pk = new PrivateKey(Hex.decode("835c418a8898bb665052010aa469b92e443b6a93f327e1bb162594bf511eb746"), true);
        byte[] encodedTx = assembler.assemble(memo, source, data, "bnb1y0fy2q0k4ueudh8kv4x3k67arc8ccvnkkhr40r", "bnb16vgtc7lvjldcm6m3h675g8vpwnum42h3t2qpkm", 100_000, "BNB", pk);
        Assert.assertEquals("ca01f0625dee0a4a2a2c87fa0a210a1423d24501f6af33c6dcf6654d1b6bdd1e0f8c327612090a03424e4210a08d0612210a14d310bc7bec97db8deb71bebd441d8174f9baaaf112090a03424e4210a08d06126f0a26eb5ae987210209410fcf16a8cc2867e33f56c36678705ce8083b1cd6cdee7932a26788f3ae2d1240245b6239c8e51e244e7a4f7f8fab85fe81a3859a7fe6c0d9e373b4c23072072506804ea8a2222b1c631fc1d5f6b3618346c593d8c0fddfddd623b6eb9296c5ff18ec25200b1a0548656c6c6f2001",
                Hex.toHexString(encodedTx));
    }

    @Test
    public void testTx3() {
        String memo = "100243966";
        long source = 1;
        byte[] data = null;
        TransactionAssembler assembler = new TransactionAssembler("Binance-Chain-Tigris", 15555, 0L);
        PrivateKey pk = new PrivateKey(Hex.decode("835c418a8898bb665052010aa469b92e443b6a93f327e1bb162594bf511eb746"), true);
        byte[] encodedTx = assembler.assemble(memo, source, data, "bnb197sq8969w79985ugqg5jvjl8sqm6mjdd3ny7gn", "bnb136ns6lfw4zs5hg4n85vdthaad7hq5m4gtkgf23", 100437500, "BNB", pk);
        Assert.assertEquals("d001f0625dee0a4c2a2c87fa0a220a142fa0039745778a53d3880229264be78037adc9ad120a0a03424e4210fc9bf22f12220a148ea70d7d2ea8a14ba2b33d18d5dfbd6fae0a6ea8120a0a03424e4210fc9bf22f126f0a26eb5ae987210209410fcf16a8cc2867e33f56c36678705ce8083b1cd6cdee7932a26788f3ae2d1240891b97f6e40f0d1142effce2f81648d9b45aa9662654539efee5c2d194e6d7d810a2f3db7ecad11507940797d3caa35cb09e156d547913deff67074968738ce118c37920001a093130303234333936362001",
                Hex.toHexString(encodedTx));
    }
}
