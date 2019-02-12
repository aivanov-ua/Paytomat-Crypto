package com.paytomat.eos;

import com.google.gson.Gson;
import com.payomat.eos.Eos;
import com.payomat.eos.EosTransactionHelper;
import com.payomat.eos.PrivateKey;
import com.payomat.eos.PublicKey;
import com.payomat.eos.signature.Signature;
import com.payomat.eos.transaction.EosAction;
import com.payomat.eos.transaction.EosAuthority;
import com.payomat.eos.transaction.EosExtentionType;
import com.payomat.eos.transaction.EosKeyWeight;
import com.payomat.eos.transaction.EosPackedTransaction;
import com.payomat.eos.transaction.EosPermissionLevelWeight;
import com.payomat.eos.transaction.EosTransaction;
import com.payomat.eos.transaction.EosTransactionCompression;
import com.payomat.eos.transaction.EosWaitWeight;
import com.payomat.eos.transaction.actiondata.NewAccountActionData;
import com.payomat.eos.transaction.actiondata.TransactionActionData;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.Assert.assertEquals;

/**
 * created by Alex Ivanov on 10/16/18.
 */
public class EosTransactionTest {

    @Test
    public void testEosSignSingle() {
        PrivateKey pk = new PrivateKey("5JHfJNMtxGvg45F17UHC1xkdYR2BmKY2nrXrGPm12aNKEoXBfw3");
        String message = "rqxoj1L2oBNTBGFVsF63h5fbf7DQNydUa";
        Signature signature = Eos.signTransactionRaw(message.getBytes(), pk);
        assertEquals("1f4c51137e1524d87f5530009b9e26713cec2bec5ae8505edc11e5d2c53884d5304c42e81baaf8d9ae35318d6b573814dde255d901d012407d0cbfc444cadd7543",
                signature.toHex());
        assertEquals("SIG_K1_K5EsVrZpFHd5wJJhpqzomzaGobipR9UaprokdoA8jia4RpTrEF8KAVSUFnTwMsNzmpc5RSimq6S4S9RjsccUFXTnhBoM2b",
                signature.toString());
    }


    @Test
    public void testEosSignature() throws IOException {
        String fileData = readFile(getClass(), "eos_sign.txt");
        Gson gson = new Gson();
        String[] data = fileData.split("\n");
        for (int i = 0; i < data.length; i++) {
            String json = data[i];
            if (json.equals("{") || json.equals("}")) continue;
            EosSignatureModel testModel = gson.fromJson(json, EosSignatureModel.class);
            System.out.println((i + 1) + " of " + data.length);
            PrivateKey pk = new PrivateKey(testModel.getPrivateKey());
            Signature signature = Eos.signTransactionRaw(testModel.getData().getBytes(), pk);
            assertEquals(testModel.getSig(), signature.toString());
        }
    }

    @Test
    public void testEosTransactionSingle() {
        PrivateKey privateKey = new PrivateKey("5JHfJNMtxGvg45F17UHC1xkdYR2BmKY2nrXrGPm12aNKEoXBfw3");

        String tx = EosTransactionHelper.createRawTx(privateKey,
                "paytomatios3",
                "paytomatios",
                45.0033,
                Eos.CURRENCY_NAME,
                "",
                "eosio.token",
                Eos.CURRENCY_PRECISION,
                Eos.CHAIN_ID_HEX_TESTNET,
                1530823785000L,
                (short) 17900,
                (int) 4109894760L);

        EosPackedTransaction expectedSerializedTx = new EosPackedTransaction(
                new String[]{"SIG_K1_Khs2mwzBDgGPN2nEKrNf9pHcoxmhQHBzK2Vh42T2HxfA9ixjCNdizaxtzcZ74jRMqBYMiXkCoG5F7PYfpk1mADYdy6ksec"},
                EosTransactionCompression.NONE,
                "",
                "69843e5bec456804f8f4000000000100a6823403ea3055000000572d3ccdcd01303075d9489abda900000000a8ed323221303075d9489abda9003075d9489abda9f1dd06000000000004454f53000000000000");

        assertEquals(expectedSerializedTx.toJson(), tx);
    }

    @Test
    public void testEosTransaction() {
        PrivateKey privateKey = new PrivateKey("5K3e7MCue5NZpUHpp3zyB7jDg7vfukDSf9jn81qqPcH2WSjgwQg");

        EosAction action = EosAction.transfer("eosio.token",
                new TransactionActionData("eqzosgwqmg3a", "a5nbbgne33jn", 10, "EOS", Eos.CURRENCY_PRECISION, ""));

        EosAction[] actions = new EosAction[1];
        actions[0] = action;

        EosTransaction tx = new EosTransaction(privateKey,
                "038f4b0fc8ff18a4f0842a8f0564611f6e96e8535901dd45e43ac8691a1c4dca",
                1539159884000L,
                (short) 64604,
                (int) 30239881L,
                0,
                (byte) 0,
                0,
                new EosAction[0],
                actions,
                new EosExtentionType[0]);
        String expectedPackedTrx = "4cb7bd5b5cfc896ccd01000000000100a6823403ea3055000000572d3ccdcd0160069396334cbf5500000000a8ed32322160069396334cbf5530df186ab2736631a08601000000000004454f53000000000000";
        assertEquals(expectedPackedTrx, tx.getPackedTx().getPackedTrx());
    }

    @Test
    public void testEosTransactionSingleWithMemo() {
        PrivateKey privateKey = new PrivateKey("5K3e7MCue5NZpUHpp3zyB7jDg7vfukDSf9jn81qqPcH2WSjgwQg");

        String tx = EosTransactionHelper.createRawTx(privateKey,
                "paytomatios",
                "paytomatios3",
                256.0,
                "EOS",
                "lalalahohoho",
                "eosio.token",
                Eos.CURRENCY_PRECISION,
                Eos.CHAIN_ID_HEX_TESTNET,
                1530824570000L,
                (short) 19431,
                77559832);

        EosPackedTransaction expectedSerializedTx = new EosPackedTransaction(
                new String[]{"SIG_K1_JzaQ9rPiawJ5T8P7UyDETV6W4TH5U7kCgAHJSbfYsRjZ1RKt8xmGEQYe6NKfHgiU6V7GiqZhAjkwgn8T5i6RkUdbYjXnud"},
                EosTransactionCompression.NONE,
                "",
                "7a873e5be74b18789f04000000000100a6823403ea3055000000572d3ccdcd01003075d9489abda900000000a8ed32322d003075d9489abda9303075d9489abda9001027000000000004454f53000000000c6c616c616c61686f686f686f00");

        assertEquals(expectedSerializedTx.toJson(), tx);
    }

    @Test
    public void testEosTransactionSubToken() {
        PrivateKey privateKey = new PrivateKey("5JHfJNMtxGvg45F17UHC1xkdYR2BmKY2nrXrGPm12aNKEoXBfw3");

        String tx = EosTransactionHelper.createRawTx(privateKey,
                "paytomatios3",
                "paytomatios",
                45.0033,
                "YAROS",
                "",
                "yaroslavtokn",
                (byte) 4,
                Eos.CHAIN_ID_HEX_TESTNET,
                1530823785000L,
                (short) 17900,
                (int) 4109894760L);

        EosPackedTransaction expectedSerializedTx = new EosPackedTransaction(
                new String[]{"SIG_K1_K8kJkaPqvVntdh1XBx5uc95oJCqDjKTtYAT5ktguRwde9NWBUunQtdCcR9USeZra6jpGBvskbz4XakS9rC92qi9xBn7eYg"},
                EosTransactionCompression.NONE,
                "",
                "69843e5bec456804f8f400000000013021cddb444caff1000000572d3ccdcd01303075d9489abda900000000a8ed323221303075d9489abda9003075d9489abda9f1dd060000000000045941524f5300000000");

        assertEquals(expectedSerializedTx.toJson(), tx);
    }

    @Test
    public void testNewAccountAction() {
        PrivateKey privateKey = new PrivateKey("5K3e7MCue5NZpUHpp3zyB7jDg7vfukDSf9jn81qqPcH2WSjgwQg");

        EosKeyWeight keyWeight = new EosKeyWeight(new PublicKey("EOS5fJbmRusJQnfEH1NSSPLANwwEeBz7vwPV2pkMQBgi5zZkaDcRw"), (short) 1);
        EosKeyWeight[] keys = new EosKeyWeight[1];
        keys[0] = keyWeight;
        EosAuthority authority = new EosAuthority(1,
                keys,
                new EosPermissionLevelWeight[0],
                new EosWaitWeight[0]);

        EosAction action = EosAction.createNewAccount(new NewAccountActionData(
                "eqzosgwqmg3a",
                "a5nbbgne33jn",
                authority,
                authority
        ));

        EosAction[] actions = new EosAction[1];
        actions[0] = action;

        EosTransaction tx = new EosTransaction(privateKey,
                "038f4b0fc8ff18a4f0842a8f0564611f6e96e8535901dd45e43ac8691a1c4dca",
                1539159884000L,
                (short) 64604,
                (int) 30239881L,
                0,
                (byte) 0,
                0,
                new EosAction[0],
                actions,
                new EosExtentionType[0]);
        String expectedPackedTrx = "4cb7bd5b5cfc896ccd0100000000010000000000ea305500409e9a2264b89a0160069396334cbf5500000000a8ed32326660069396334cbf5530df186ab27366310100000001000265c1cc49739f8eb7365367a8ccb0b2c5dc51eacf6132a505b1b37373ce7fde11010000000100000001000265c1cc49739f8eb7365367a8ccb0b2c5dc51eacf6132a505b1b37373ce7fde110100000000";
        assertEquals(expectedPackedTrx, tx.getPackedTx().getPackedTrx());
    }

    private String readFile(Class javaClass, String fileName) throws IOException {
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
