package com.paytomat.btc;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.paytomat.btc.model.UtxoModel;
import com.paytomat.btc.transaction.Transaction;
import com.paytomat.btc.transaction.TransactionHelper;
import com.paytomat.btc.transaction.TransactionType;
import com.paytomat.btc.transaction.UnspentOutputInfo;

import org.bouncycastle.crypto.signers.ECDSASigner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * created by Alex Ivanov on 2019-09-13.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(TransactionHelper.class)
public class TransactionTest {

    @Test
    public void testTransaction() throws Exception {
        System.out.println("Legacy tx: 1 input 2 outputs");
        ECDSASigner signer = PowerMockito.mock(ECDSASigner.class);
        BigInteger r = new BigInteger("67547841274129697924326056856374171085011849519820321056172646824859723204626");
        BigInteger s = new BigInteger("20908024467244276561715692900737565326060532585583171831057889097262712454173");
        PowerMockito.when(signer.generateSignature(Mockito.any())).thenReturn(new BigInteger[]{r, s});
        PowerMockito.whenNew(ECDSASigner.class).withNoArguments().thenReturn(signer);

        List<UnspentOutputInfo> utxo = Collections.singletonList(new UnspentOutputInfo.Builder()
                .withPrivateKeyWif("Kx8MdEZMPxRn7EHbHByVtbKTBKhAX4ZQMZVgxEeFRvSwdFqow5wN")
                .withTxHash("fea98492c10f7f8b5dfd05bfd66aa218ce552274082f7619140738357eae6969")
                .withScript("18NZjGTLNNY6uUHVqiWGdZXsFENt4MVrCt", "BTC", false)
                .withValue("0.0008778")
                .withOutputIndex(1)
                .build());

        Transaction transaction = new Transaction.Builder("BTC")
                .withUnspentInfo(utxo)
                .withOutputAddress("1Mg7z4drfwCqUxRm3XPSm8uqpq31WHUZwp")
                .withChangeAddress("1NRiY5XjiGJeC1iNbxDvxsP2gb8MMX5Xw7")
                .withAmount(50000)
                .withFeePerB(67)
                .withTransactionType(TransactionType.LEGACY)
                .withDust(567L)
                .withTestNetwork(false)
                .build(SecureRandom.getInstanceStrong());

        logTx(transaction);

        Assert.assertEquals("01000000016969ae7e3538071419762f08742255ce18a26ad6bf05fd5d8b7f0fc19284a9fe010000006b4830450221009556b92b325413d7537c98dd0bf60fd6e4a7bb8c942a31b4c028551556e1881202202e3985c186b8b11a09aea4d9750b1774c27383bc03f2ad96e0c285d104637c1d01210273cd03971dbf4a42672ea01e58371f79db6bb20f1cd727fac7980e668da0dca4ffffffff0250c30000000000001976a914e2c891688037efe0f3076cb7781dced4a853a87888ac6e580000000000001976a914eb074c0210ef4ce2f76f2aa467bdf328a1b6bc0688ac00000000",
                transaction.toString());
    }

    @Test
    public void testTransactionSegwit() throws Exception {
        System.out.println("========================================================================");
        System.out.println("SegWit tx: 1 input 2 outputs");
        List<UnspentOutputInfo> utxo = Collections.singletonList(new UnspentOutputInfo.Builder()
                .withPrivateKeyWif("Kx8MdEZMPxRn7EHbHByVtbKTBKhAX4ZQMZVgxEeFRvSwdFqow5wN")
                .withTxHash("fea98492c10f7f8b5dfd05bfd66aa218ce552274082f7619140738357eae6969")
                .withScript("3MutiCB1JpyZXbzvZEf5qzSG6xZBR9KQBg", "BTC", false)
                .withValue("0.0008778")
                .withOutputIndex(1)
                .build());

        Transaction transaction = new Transaction.Builder("BTC")
                .withUnspentInfo(utxo)
                .withOutputAddress("31phkQAk7MuCjJ69dhXN6mSnMsWd8ZQJH3")
                .withChangeAddress("3MutiCB1JpyZXbzvZEf5qzSG6xZBR9KQBg")
                .withAmount(50000)
                .withFeePerB(67)
                .withTransactionType(TransactionType.SEGWIT)
                .withDust(567L)
                .withTestNetwork(false)
                .build(SecureRandom.getInstanceStrong());

        logTx(transaction);
    }

    @Test
    public void testWithSegwitUtxoTransaction() throws Exception {
        System.out.println("========================================================================");
        System.out.println("Legacy tx: 9 inputs 1 output");
        ECDSASigner signer = PowerMockito.mock(ECDSASigner.class);
        BigInteger r = new BigInteger("35689355527339649799767181021700919902486260453631980353154844733650569289765");
        BigInteger s = new BigInteger("64782987907364111867239834696577282619718820600144809970597851687280754387214");
        PowerMockito.when(signer.generateSignature(Mockito.any())).thenReturn(new BigInteger[]{r, s});
        PowerMockito.whenNew(ECDSASigner.class).withNoArguments().thenReturn(signer);


        String tokenSymbol = "BTC";
        List<UnspentOutputInfo> utxo = Arrays.asList(
                new UnspentOutputInfo.Builder()
                        .withPrivateKeyWif("KwddBVHSf2rHBu7YpM2VxjQkmwRd1zdVZkSsVGrHRYcmGApziPx6")
                        .withTxHash("73c340781b0a0d0a74a534b683ab6e09a69a912bd2a5af97665f52198b5fa260")
                        .withScript("16Gr6JWb2iiRjriZo1biRHTZtR692wqFUm", tokenSymbol, false)
                        .withValue("0.00068368")
                        .withOutputIndex(1)
                        .build(),
                new UnspentOutputInfo.Builder()
                        .withPrivateKeyWif("L3jKfxoDHZvAiEoTWS6MNLKdQM6ihb6WhZnuYPdsqbD7akPszXHQ")
                        .withTxHash("6cc6c8cb375a9887c90d0fb2dfaa253dabd74f46679bc8d4fe5c742b928d7d26")
                        .withScript("1LJC1UX5HgboVNgxSoSuuhXhWvEQrVYoVZ", tokenSymbol, false)
                        .withValue("0.0002")
                        .withOutputIndex(0)
                        .build(),
                new UnspentOutputInfo.Builder()
                        .withPrivateKeyWif("KxY9G3nUNi1t8tXSg5Xcq3vYaKntxjSxmomjSxPLphUHXjDbF6GQ")
                        .withTxHash("33f932dc2b4fbe82ff0831e88b76ebfe471befaaeb8f3f54fa1bef6401f94098")
                        .withScript("1ByxK8UG75snMyqN6erqjLzWL4jigcerFn", tokenSymbol, false)
                        .withValue("0.00015028")
                        .withOutputIndex(1)
                        .build(),
                new UnspentOutputInfo.Builder()
                        .withPrivateKeyWif("L36AVf57MBk8W5xrMxkEqcXFXZkU1YCSv3jkDAss96f6wdh8G7cH")
                        .withTxHash("ed6e5b2a7d6b4321f1521e3a096943a9fc8edbd33e1c275dbd0da9b805bffe36")
                        .withScript("1Mg7z4drfwCqUxRm3XPSm8uqpq31WHUZwp", tokenSymbol, false)
                        .withValue("0.0001")
                        .withOutputIndex(0)
                        .build(),
                new UnspentOutputInfo.Builder()
                        .withPrivateKeyWif("KySLPUr6Z2fXAbBaWFFFpHYfvMsSpjkqWnm27fnVATiLuGGUDwjE")
                        .withTxHash("2d80d76b656986fbd6f298c827381cdfee8eac2e3a9592b59761dbf38e59826d")
                        .withScript("1AedbgWQXwTmKhgn26yfhKzGo2xKC97Xrx", tokenSymbol, false)
                        .withValue("0.0001")
                        .withOutputIndex(1)
                        .build(),
                new UnspentOutputInfo.Builder()
                        .withPrivateKeyWif("KzKTqhh3qnN4NWFXwQ8P4SNXm5u1D5RX2Mnp7PpTjyW6h3UbeXX3")
                        .withTxHash("0afac5694799b576bd5a93f834eca40080214e286f9f00e78708b0371258027b")
                        .withScript("1Bi6hMBrfpuhDs8WjEqXRem8E8JfHdSxTT", tokenSymbol, false)
                        .withValue("0.0001")
                        .withOutputIndex(0)
                        .build(),
                new UnspentOutputInfo.Builder()
                        .withPrivateKeyWif("L3KYit5HD2Y4sGWfHuNZVc85pFc8NCe1BiyZMso81XAx7AVF8KLg")
                        .withTxHash("77152a0a8750729591305a4e518cc69dd5ec28b0297f8c7a4f1b09bd631bdacd")
                        .withScript("14oNkRbDFM2wqSsat4ZTKhuVjSVq1mtb5K", tokenSymbol, false)
                        .withValue("0.0001")
                        .withOutputIndex(0)
                        .build(),
                new UnspentOutputInfo.Builder()
                        .withPrivateKeyWif("KznQkm6J33vpa8PkrS1KmuDCNZPfGR5DLNkturnGQsW6ZpAyZbqv")
                        .withTxHash("9e8d8958199e1ea39b63cf9d2b1d9526989890ffa7fe35962964b2aa5375e1cd")
                        .withScript("1FGJX9a1vDE416yfjpcrr9Jb2jKn3soq3w", tokenSymbol, false)
                        .withValue("0.0001")
                        .withOutputIndex(0)
                        .build(),
                new UnspentOutputInfo.Builder()
                        .withPrivateKeyWif("L1bGqBPubQp6MZEKYsqm2oFgqxz4uKXtCt2P5U2VCERho2vXnPvh")
                        .withTxHash("0b6475ca80dde2b289c769f285d40276f1139b47d12d9cd0bff4e724387c14e9")
                        .withScript("17AtAAxxisi2csnNfbDJevbS4YvgPDM2sK", tokenSymbol, false)
                        .withValue("0.00000863")
                        .withOutputIndex(1)
                        .build());

        Transaction transaction = new Transaction.Builder(tokenSymbol)
                .withUnspentInfo(utxo)
                .withOutputAddress("1BHhSHhg6owPj7uLzNEVJb3aKfJZL2nYoB")
                .withChangeAddress("1E8bY5DG3jqqUDT5XFkEt9ar75dcwsujBW")
                .withAmount(154259)
                .withFeePerB(67)
                .withTransactionType(TransactionType.LEGACY)
                .withDust(567L)
                .withTestNetwork(false)
                .build(SecureRandom.getInstanceStrong());

        logTx(transaction);

        Assert.assertEquals("010000000960a25f8b19525f6697afa5d22b919aa6096eab83b634a5740a0d0a1b7840c373010000006a47304402204ee7759a5c5c4e71164de54433a104d494215287147906c887786389acc86025022070c620d3f0e2e1d3275462039044c2fd717ae1c517d0c6d1481f0c25dbacd833012102003cb5bffd8b932d2783ec2fe24063d6fbfa4047bfdb4efbca81cf99abfbc06dffffffff267d8d922b745cfed4c89b67464fd7ab3d25aadfb20f0dc987985a37cbc8c66c000000006a47304402204ee7759a5c5c4e71164de54433a104d494215287147906c887786389acc86025022070c620d3f0e2e1d3275462039044c2fd717ae1c517d0c6d1481f0c25dbacd83301210342c8973355b24dc5e40f2fb8bb16986d4a814b9fde03587636ee9ec004cd831cffffffff9840f90164ef1bfa543f8febaaef1b47feeb768be83108ff82be4f2bdc32f933010000006a47304402204ee7759a5c5c4e71164de54433a104d494215287147906c887786389acc86025022070c620d3f0e2e1d3275462039044c2fd717ae1c517d0c6d1481f0c25dbacd833012102c14d5a1970314bf596611ed8be419092f855ac0684c3ea04b2fe1e4bc6840a11ffffffff36febf05b8a90dbd5d271c3ed3db8efca94369093a1e52f121436b7d2a5b6eed000000006a47304402204ee7759a5c5c4e71164de54433a104d494215287147906c887786389acc86025022070c620d3f0e2e1d3275462039044c2fd717ae1c517d0c6d1481f0c25dbacd833012102ba55b619a611849d2bb532fe811fa589269a38a1e73ecc0e66e03ef123760be9ffffffff6d82598ef3db6197b592953a2eac8eeedf1c3827c898f2d6fb8669656bd7802d010000006a47304402204ee7759a5c5c4e71164de54433a104d494215287147906c887786389acc86025022070c620d3f0e2e1d3275462039044c2fd717ae1c517d0c6d1481f0c25dbacd8330121025ec40633a8d814d6e295e819f9348a5717005d1c43b0dfd0fe621bc7691ef275ffffffff7b02581237b00887e7009f6f284e218000a4ec34f8935abd76b5994769c5fa0a000000006a47304402204ee7759a5c5c4e71164de54433a104d494215287147906c887786389acc86025022070c620d3f0e2e1d3275462039044c2fd717ae1c517d0c6d1481f0c25dbacd833012102fba6bb64dcdafb85d9fa7bc0b4e77a5e7daef9bab3ed114dffd57c3ccc2798a1ffffffffcdda1b63bd091b4f7a8c7f29b028ecd59dc68c514e5a3091957250870a2a1577000000006a47304402204ee7759a5c5c4e71164de54433a104d494215287147906c887786389acc86025022070c620d3f0e2e1d3275462039044c2fd717ae1c517d0c6d1481f0c25dbacd833012103e79eaee2321e4df91537bd70a448c068156bffc78c3a60d3da7b439d25331e9affffffffcde17553aab264299635fea7ff90989826951d2b9dcf639ba31e9e1958898d9e000000006a47304402204ee7759a5c5c4e71164de54433a104d494215287147906c887786389acc86025022070c620d3f0e2e1d3275462039044c2fd717ae1c517d0c6d1481f0c25dbacd83301210288fdbc6b7aa0d37f41351b6965c6b1e0782c23867951ce7d8416fc235add54e1ffffffffe9147c3824e7f4bfd09c2dd1479b13f17602d485f269c789b2e2dd80ca75640b010000006a47304402204ee7759a5c5c4e71164de54433a104d494215287147906c887786389acc86025022070c620d3f0e2e1d3275462039044c2fd717ae1c517d0c6d1481f0c25dbacd833012103453bf66de8ad3dcab16f22334139bc5035605ebd6f0f25bd63e0847aeb78e4caffffffff0173f20000000000001976a91470d9aae13176f0e55deb405715853855d8d065f388ac00000000",
                transaction.toString());
    }

    private void logTx(Transaction tx) {
        System.out.println("Signed raw tx size = " + tx.getMessageSize());
        System.out.println("Signed tx vsize = " + tx.getVsize());
    }

    @Test
    public void testMultiInputHorizenTx() throws IOException, NoSuchAlgorithmException {
        String file = FileUtil.readFile("horizen_utxo.json", getClass());

        Type type = new TypeToken<Map<String, UtxoModel>>() {
        }.getType();
        Map<String, UtxoModel> utxoList = new Gson().fromJson(file, type);

        List<UnspentOutputInfo> formedUtxo = new ArrayList<>(utxoList.size());
        for (UtxoModel utxo : utxoList.values()) {
            formedUtxo.add(new UnspentOutputInfo.Builder()
                    .withPrivateKeyWif("L1bGqBPubQp6MZEKYsqm2oFgqxz4uKXtCt2P5U2VCERho2vXnPvh")
                    .withTxHash(utxo.txHex)
                    .withZenScript(utxo.subScriptHex)
                    .withValue(utxo.value)
                    .withOutputIndex((int) utxo.outputIndex)
                    .build());
        }

        Transaction transaction = new Transaction.Builder("ZEN")
                .withUnspentInfo(formedUtxo)
                .withOutputAddress("znpCfC3GpBfr93SHzAm74KfiYsTJBbDrs5r")
                .withChangeAddress("zna4KRrpTCT6aqt95Axoxst7pjveBC1n4sS")
                .withAmount(Convertor.parseValue("0.01452371", BigDecimal.TEN.pow(8)))
                .withFeePerB(10000)
                .withTransactionType(TransactionType.HORIZEN)
                .withDust(546L)
                .withTestNetwork(false)
                .build(SecureRandom.getInstanceStrong());

        System.out.println(transaction);
    }

}
