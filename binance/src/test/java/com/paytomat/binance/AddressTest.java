package com.paytomat.binance;

import com.paytomat.btc.PrivateKey;
import com.paytomat.btc.PublicKey;
import com.paytomat.core.util.Bech32;
import com.paytomat.core.util.BytesUtil;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Assert;
import org.junit.Test;

/**
 * created by Alex Ivanov on 2019-05-09.
 */
public class AddressTest {

    private final String TEST_PK = "95949f757db1f57ca94a5dff23314accbe7abee89597bf6a3c7382c84d7eb832";
    private final String TEST_PUBK = "026a35920088d98c3888ca68c53dfc93f4564602606cbb87f0fe5ee533db38e502";
    private final String TEST_ADDRESS = "bnb1grpf0955h0ykzq3ar5nmum7y6gdfl6lxfn46h2";

    private final PrivateKey TEST_PRIV_KEY = new PrivateKey(Hex.decode(TEST_PK), true);
    private final PublicKey TEST_PUB_KEY = new PublicKey(Hex.decode(TEST_PUBK));

    @Test
    public void privKeyToPubKey() {
        Assert.assertEquals(TEST_PUBK, TEST_PRIV_KEY.getPublicKey().toString());
    }

    @Test
    public void testAddressFromPubK() {
        Assert.assertEquals(TEST_ADDRESS, new Address(TEST_PRIV_KEY).toString());
    }

    @Test
    public void testAddressFromPrivK() {
        Assert.assertEquals(TEST_ADDRESS, new Address(TEST_PUB_KEY).toString());
    }

    @Test
    public void testEncodeDecode() {
        Assert.assertTrue(Address.isValid(TEST_ADDRESS));
        Bech32.HrpAndData data = Bech32.bech32Decode(TEST_ADDRESS);
        Assert.assertEquals("080301090f051414170f04160200111d0314131b1c1b1e041a080d091f1a1f06", Hex.toHexString(data.getData()));
        Assert.assertEquals(TEST_ADDRESS, Bech32.bech32Encode(Constants.ADDRESS_PREFIX, data.getData()));
        byte[] convertedUp = BytesUtil.convertBits(data.getData(), 5, 8, false);
        Assert.assertEquals("40c2979694bbc961023d1d27be6fc4d21a9febe6", Hex.toHexString(convertedUp));
        byte[] convertedDown = BytesUtil.convertBits(convertedUp, 8, 5, true);
        Assert.assertEquals("080301090f051414170f04160200111d0314131b1c1b1e041a080d091f1a1f06", Hex.toHexString(convertedDown));
        Assert.assertEquals(TEST_ADDRESS, Bech32.bech32Encode(Constants.ADDRESS_PREFIX, convertedDown));
    }

    @Test
    public void testVerification() {
        Assert.assertTrue(Address.isValid("bnb1grpf0955h0ykzq3ar5nmum7y6gdfl6lxfn46h2"));
        Assert.assertTrue(Address.isValid("bnb1hlly02l6ahjsgxw9wlcswnlwdhg4xhx38yxpd5"));
        Assert.assertFalse(Address.isValid("pnb1grpf0955h0ykzq3ar5nmum7y6gdfl6lxfn46h2"));
        Assert.assertFalse(Address.isValid("bnb1grpf0955h0ykzq3ar5nmum7y6gdfl6lxfn46h3"));
    }

}
