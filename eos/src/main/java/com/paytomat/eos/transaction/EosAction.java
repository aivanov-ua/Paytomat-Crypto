package com.paytomat.eos.transaction;

import com.paytomat.eos.transaction.actiondata.BuyRamBytesActionData;
import com.paytomat.eos.transaction.actiondata.BuyRamEosActionData;
import com.paytomat.eos.transaction.actiondata.DelegateBwActionData;
import com.paytomat.eos.transaction.actiondata.NewAccountActionData;
import com.paytomat.eos.transaction.actiondata.RegProxyActionData;
import com.paytomat.eos.transaction.actiondata.SellRamBytesActionData;
import com.paytomat.eos.transaction.actiondata.TransactionActionData;
import com.paytomat.eos.transaction.actiondata.UndelegateBwActionData;
import com.paytomat.eos.transaction.actiondata.VoteProducerActionData;
import com.paytomat.core.util.ByteSerializer;
import com.paytomat.core.util.Serializable;

import static com.paytomat.eos.Eos.encodeName;

/**
 * created by Alex Ivanov on 2019-02-12.
 */
public class EosAction implements Serializable {

    private static final String TRANSFER = "transfer";
    private static final String BUY_RAM_BYTES = "buyrambytes";
    private static final String SELL_RAM = "sellram";
    private static final String DELEGATE_BW = "delegatebw";
    private static final String UNDELEGATE_BW = "undelegatebw";
    private static final String VOTE_PRODUCER = "voteproducer";
    private static final String NEW_ACCOUNT = "newaccount";
    private static final String REG_PROXY = "regproxy";

    private final String account;
    private final String name;
    private final EosActionAuthorization[] authorization;
    private final byte[] data;

    private static EosActionAuthorization[] provideAuthorization(String... from) {
        EosActionAuthorization[] authorizationArray = new EosActionAuthorization[from.length];
        for (int i = 0; i < from.length; i++) {
            authorizationArray[i] = new EosActionAuthorization(from[i], EosActionAuthorizationPermission.ACTIVE);
        }
        return authorizationArray;
    }

    public static EosAction transfer(String tokenAccount, TransactionActionData actionData) {
        return new EosAction(tokenAccount,
                TRANSFER,
                provideAuthorization(actionData.getAuthorization()),
                actionData.serialize());
    }

    public static EosAction buyRamBytes(BuyRamBytesActionData actionData) {
        return new EosAction("eosio",
                BUY_RAM_BYTES,
                provideAuthorization(actionData.getAuthorization()),
                actionData.serialize());
    }

    public static EosAction buyRamEos(BuyRamEosActionData actionData) {
        return new EosAction("eosio",
                "buyram",
                provideAuthorization(actionData.getAuthorization()),
                actionData.serialize());
    }

    public static EosAction sellRamBytes(SellRamBytesActionData actionData) {
        return new EosAction("eosio",
                SELL_RAM,
                provideAuthorization(actionData.getAuthorization()),
                actionData.serialize());
    }

    public static EosAction delegateBw(DelegateBwActionData actionData) {
        return new EosAction("eosio",
                DELEGATE_BW,
                provideAuthorization(actionData.getAuthorization()),
                actionData.serialize());
    }

    public static EosAction undelegateBw(UndelegateBwActionData actionData) {
        return new EosAction("eosio",
                UNDELEGATE_BW,
                provideAuthorization(actionData.getAuthorization()),
                actionData.serialize());
    }

    public static EosAction voteProducer(VoteProducerActionData actionData) {
        return new EosAction("eosio",
                VOTE_PRODUCER,
                provideAuthorization(actionData.getAuthorization()),
                actionData.serialize());
    }

    public static EosAction createNewAccount(NewAccountActionData actionData) {
        return new EosAction("eosio",
                NEW_ACCOUNT,
                provideAuthorization(actionData.getAuthorization()),
                actionData.serialize());
    }

    public static EosAction registerProxy(RegProxyActionData actionData) {
        return new EosAction("eosio",
                REG_PROXY,
                provideAuthorization(actionData.getAuthorization()),
                actionData.serialize());
    }

    private EosAction(String account,
                      String name,
                      EosActionAuthorization[] authorization,
                      byte[] data) {
        this.account = account;
        this.name = name;
        this.authorization = authorization;
        this.data = data;
    }

    @Override
    public byte[] serialize() {
        return new ByteSerializer()
                .write(encodeName(account))
                .write(encodeName(name))
                .write(authorization, true)
                .writeVarInt32(data.length)
                .write(data)
                .serialize();
    }
}
