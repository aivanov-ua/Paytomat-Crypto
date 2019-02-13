package com.paytomat.eos;

import com.paytomat.eos.transaction.EosAction;
import com.paytomat.eos.transaction.EosAsset;
import com.paytomat.eos.transaction.EosAuthority;
import com.paytomat.eos.transaction.EosExtentionType;
import com.paytomat.eos.transaction.EosKeyWeight;
import com.paytomat.eos.transaction.EosPermissionLevelWeight;
import com.paytomat.eos.transaction.EosTransaction;
import com.paytomat.eos.transaction.EosWaitWeight;
import com.paytomat.eos.transaction.actiondata.BuyRamBytesActionData;
import com.paytomat.eos.transaction.actiondata.BuyRamEosActionData;
import com.paytomat.eos.transaction.actiondata.DelegateBwActionData;
import com.paytomat.eos.transaction.actiondata.NewAccountActionData;
import com.paytomat.eos.transaction.actiondata.RegProxyActionData;
import com.paytomat.eos.transaction.actiondata.SellRamBytesActionData;
import com.paytomat.eos.transaction.actiondata.TransactionActionData;
import com.paytomat.eos.transaction.actiondata.UndelegateBwActionData;
import com.paytomat.eos.transaction.actiondata.VoteProducerActionData;

import java.util.Calendar;

import static com.paytomat.eos.Eos.CHAIN_ID_HEX_PRODNET;
import static com.paytomat.eos.Eos.CHAIN_ID_HEX_TESTNET;
import static com.paytomat.eos.Eos.CURRENCY_NAME;
import static com.paytomat.eos.EosTransactionException.CODE_AMOUNT_TOO_SMALL;
import static com.paytomat.eos.EosTransactionException.CODE_INVALID_PRODUCERS_AMOUNT;

/**
 * created by Alex Ivanov on 2019-02-12.
 */
public class EosTransactionHelper {

    //TX
    public static String createRawTx(PrivateKey pk, String from, String to, double amount,
                                     String tokenSymbol, String memo, String tokenAccount,
                                     byte tokenPrecision, boolean isTestNet, long currentTimeMillis,
                                     short refBlockNum, int refBlockPrefix) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTimeMillis);
        calendar.add(Calendar.MINUTE, 5);
        long expiration = calendar.getTimeInMillis();
        return createRawTx(pk, from, to, amount, tokenSymbol, memo, tokenAccount, tokenPrecision,
                isTestNet ? CHAIN_ID_HEX_TESTNET : CHAIN_ID_HEX_PRODNET, expiration, refBlockNum, refBlockPrefix);
    }

    public static String createRawTx(PrivateKey pk, String from, String to, double amount,
                                     String currencyName, String memo, String tokenAccount,
                                     byte tokenPrecision, String chainIdHex, long expirationDate,
                                     short refBlockNum, int refBlockPrefix) {
        if (amount <= 0) {
            throw new EosTransactionException("Amount is too small", CODE_AMOUNT_TOO_SMALL);
        }

        TransactionActionData actionData = new TransactionActionData(from,
                to,
                amount,
                currencyName,
                tokenPrecision,
                memo);

        EosAction txAction = EosAction.transfer(tokenAccount, actionData);
        EosAction[] actionArray = new EosAction[1];
        actionArray[0] = txAction;

        return new EosTransaction(pk,
                chainIdHex,
                expirationDate,
                refBlockNum,
                refBlockPrefix,
                0,
                (byte) 0,
                0,
                new EosAction[0],
                actionArray,
                new EosExtentionType[0]).toSignedJson();
    }

    //RESOURCES
    public static String applyResourceChange(PrivateKey pk, String from,
                                             int ramBytesDiff, double cpuStakeDiff, double bandwidthStakeDiff,
                                             boolean isTestNet, long currentTimeMillis,
                                             short refBlockNum, int refBlockPrefix) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTimeMillis);
        calendar.add(Calendar.MINUTE, 5);
        long expiration = calendar.getTimeInMillis();
        return applyResourceChange(pk, from, ramBytesDiff, cpuStakeDiff, bandwidthStakeDiff,
                isTestNet ? CHAIN_ID_HEX_TESTNET : CHAIN_ID_HEX_PRODNET, expiration, refBlockNum, refBlockPrefix);
    }

    private static String applyResourceChange(PrivateKey pk, String from,
                                              int ramBytesDiff, double cpuStakeDiff, double bandwidthStakeDiff,
                                              String chainIdHex, long expirationDate,
                                              short refBlockNum, int refBlockPrefix) {
        if (ramBytesDiff == 0 && cpuStakeDiff == 0 && bandwidthStakeDiff == 0) return "";

        EosAction ramChangeAction = null;
        if (ramBytesDiff != 0) {
            boolean buy = ramBytesDiff > 0;
            ramBytesDiff = Math.abs(ramBytesDiff);
            if (buy) {
                ramChangeAction = EosAction.buyRamBytes(new BuyRamBytesActionData(from, from, ramBytesDiff));
            } else {
                ramChangeAction = EosAction.sellRamBytes(new SellRamBytesActionData(from, ramBytesDiff));
            }
        }

        EosAction bwChangeAction = null;
        EosAction cpuChangeAction = null;
        if (bandwidthStakeDiff != 0) {
            if (Math.signum(bandwidthStakeDiff) == Math.signum(cpuStakeDiff)) {
                bwChangeAction = createBwAction(from, CURRENCY_NAME, cpuStakeDiff, bandwidthStakeDiff, bandwidthStakeDiff > 0);
            } else {
                bwChangeAction = createBwAction(from, CURRENCY_NAME, 0, bandwidthStakeDiff, bandwidthStakeDiff > 0);
                if (cpuStakeDiff != 0)
                    cpuChangeAction = createBwAction(from, CURRENCY_NAME, cpuStakeDiff, 0, cpuStakeDiff > 0);
            }
        } else if (cpuStakeDiff != 0) {
            cpuChangeAction = createBwAction(from, CURRENCY_NAME, cpuStakeDiff, 0, cpuStakeDiff > 0);
        }


        EosAction[] actionArray = new EosAction[(ramChangeAction == null ? 0 : 1) + (bwChangeAction == null ? 0 : 1) + (cpuChangeAction == null ? 0 : 1)];
        int index = 0;
        if (ramChangeAction != null) actionArray[index++] = ramChangeAction;
        if (bwChangeAction != null) actionArray[index++] = bwChangeAction;
        if (cpuChangeAction != null) actionArray[index] = cpuChangeAction;

        return new EosTransaction(pk,
                chainIdHex,
                expirationDate,
                refBlockNum,
                refBlockPrefix,
                0,
                (byte) 0,
                0,
                new EosAction[0],
                actionArray,
                new EosExtentionType[0]).toSignedJson();
    }

    public static String buyRamForEos(PrivateKey privateKey, String from, boolean isTestNet,
                                      long currentTimeMillis, short refBlockNum, int refBlockPrefix,
                                      double eosRamStake) {
        if (eosRamStake <= 0) {
            throw new EosTransactionException("Amount is too small", CODE_AMOUNT_TOO_SMALL);
        }

        EosAction txAction = EosAction.buyRamEos(new BuyRamEosActionData(from, from,
                new EosAsset(eosRamStake, CURRENCY_NAME, Eos.CURRENCY_PRECISION)));

        EosAction[] actionArray = new EosAction[1];
        actionArray[0] = txAction;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTimeMillis);
        calendar.add(Calendar.MINUTE, 5);
        long expiration = calendar.getTimeInMillis();

        return new EosTransaction(privateKey,
                isTestNet ? CHAIN_ID_HEX_TESTNET : CHAIN_ID_HEX_PRODNET,
                expiration,
                refBlockNum,
                refBlockPrefix,
                0,
                (byte) 0,
                0,
                new EosAction[0],
                actionArray,
                new EosExtentionType[0]).toSignedJson();
    }

    private static EosAction createBwAction(String from, String currencyName, double cpuStakeDiff,
                                            double bandwidthStakeDiff, boolean delegate) {
        cpuStakeDiff = Math.abs(cpuStakeDiff);
        bandwidthStakeDiff = Math.abs(bandwidthStakeDiff);
        if (delegate) return EosAction.delegateBw(new DelegateBwActionData(from,
                from,
                new EosAsset(bandwidthStakeDiff, currencyName, Eos.CURRENCY_PRECISION),
                new EosAsset(cpuStakeDiff, currencyName, Eos.CURRENCY_PRECISION),
                false));
        else return EosAction.undelegateBw(new UndelegateBwActionData(from,
                from,
                new EosAsset(bandwidthStakeDiff, currencyName, Eos.CURRENCY_PRECISION),
                new EosAsset(cpuStakeDiff, currencyName, Eos.CURRENCY_PRECISION)));
    }

    //NEW ACCOUNT
    public static EosTransaction createNewAccount(PrivateKey pk, String creator,
                                                  String accountName, String ownerPubKey, String activePubKey,
                                                  int ramBytes, double cpuStake, double bandwidthStake,
                                                  boolean transferResources, long currentTimeMillis, String chainIdHex,
                                                  short refBlockNum, int refBlockPrefix) {

        EosKeyWeight keyWeightOwner = new EosKeyWeight(new PublicKey(ownerPubKey), (short) 1);
        EosKeyWeight[] keysOwner = new EosKeyWeight[1];
        keysOwner[0] = keyWeightOwner;
        EosKeyWeight keyWeightActive = new EosKeyWeight(new PublicKey(activePubKey), (short) 1);
        EosKeyWeight[] keysActive = new EosKeyWeight[1];
        keysActive[0] = keyWeightActive;

        NewAccountActionData newAccountActionData = new NewAccountActionData(creator, accountName,
                new EosAuthority(1, keysOwner, new EosPermissionLevelWeight[0], new EosWaitWeight[0]),
                new EosAuthority(1, keysActive, new EosPermissionLevelWeight[0], new EosWaitWeight[0]));
        BuyRamBytesActionData buyRamActionData = new BuyRamBytesActionData(creator, accountName, ramBytes);
        DelegateBwActionData delegateBwActionData = new DelegateBwActionData(creator, accountName,
                new EosAsset(cpuStake, CURRENCY_NAME, Eos.CURRENCY_PRECISION),
                new EosAsset(bandwidthStake, CURRENCY_NAME, Eos.CURRENCY_PRECISION),
                transferResources);


        EosAction[] actionArray = new EosAction[3];
        actionArray[0] = EosAction.createNewAccount(newAccountActionData);
        actionArray[1] = EosAction.buyRamBytes(buyRamActionData);
        actionArray[2] = EosAction.delegateBw(delegateBwActionData);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTimeMillis);
        calendar.add(Calendar.MINUTE, 5);
        long expiration = calendar.getTimeInMillis();

        return new EosTransaction(pk,
                chainIdHex,
                expiration,
                refBlockNum,
                refBlockPrefix,
                0,
                (byte) 0,
                0,
                new EosAction[0],
                actionArray,
                new EosExtentionType[0]);
    }

    //REG PROXY
    public static EosTransaction registerProxy(PrivateKey privateKey, String proxy, boolean setAsProxy,
                                               long currentTimeMillis, String chainIdHex,
                                               short refBlockNum, int refBlockPrefix) {
        EosAction[] actions = new EosAction[1];
        actions[0] = EosAction.registerProxy(new RegProxyActionData(proxy, setAsProxy));

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTimeMillis);
        calendar.add(Calendar.MINUTE, 5);
        long expiration = calendar.getTimeInMillis();

        return new EosTransaction(privateKey,
                chainIdHex,
                expiration,
                refBlockNum,
                refBlockPrefix,
                0,
                (byte) 0,
                0,
                new EosAction[0],
                actions,
                new EosExtentionType[0]);
    }

    //VOTE PRODUCER
    public static EosTransaction voteProducer(PrivateKey pk, String voter, String proxy, String[] producers,
                                              long currentTimeMillis, String chainIdHex,
                                              short refBlockNum, int refBlockPrefix) {
        if (producers.length > 30) {
            throw new EosTransactionException("Producers count must be in [0,30]", CODE_INVALID_PRODUCERS_AMOUNT);
        }

        EosAction[] actionArray = new EosAction[1];
        actionArray[0] = EosAction.voteProducer(new VoteProducerActionData(voter, proxy, producers));

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTimeMillis);
        calendar.add(Calendar.MINUTE, 5);
        long expiration = calendar.getTimeInMillis();

        return new EosTransaction(pk,
                chainIdHex,
                expiration,
                refBlockNum,
                refBlockPrefix,
                0,
                (byte) 0,
                0,
                new EosAction[0],
                actionArray,
                new EosExtentionType[0]);
    }

}
