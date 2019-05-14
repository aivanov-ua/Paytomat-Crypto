package com.paytomat.binance;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.google.protobuf.ByteString;
import com.paytomat.binance.protos.Binance;

import org.bouncycastle.util.encoders.Hex;

/**
 * created by Alex Ivanov on 2019-05-14.
 */
public class SignData {

    @SerializedName("account_number")
    private final String accountNumber;
    @SerializedName("chain_id")
    private final String chainId;
    private final byte[] data = null;
    private final String memo;
    private final SendOrder[] msgs;
    private final String sequence;
    private final String source;

    public SignData(String chainId, String accountNumber, String sequence, String memo, String source, String from, String to, long amount, String denom) {
        this.chainId = chainId;
        this.accountNumber = accountNumber;
        this.sequence = sequence;
        this.memo = memo;
        this.source = source;

        Token token = new Token(amount, denom);
        Destination input = new Destination(from, token);
        Destination output = new Destination(to, token);

        this.msgs = new SendOrder[]{new SendOrder(input, output)};
    }

    public SendOrder[] getMsgs() {
        return msgs;
    }

    @Override
    public String toString() {
        return new GsonBuilder().serializeNulls().create().toJson(this);
    }

    public byte[] toByteArray() {
        return toString().getBytes();
    }

    public String toHex() {
        return Hex.toHexString(toByteArray());
    }

    class Token {
        private final long amount;
        private final String denom;

        public Token(long amount, String denom) {
            this.amount = amount;
            this.denom = denom;
        }

        public Binance.SendOrder.Token toProto() {
            return Binance.SendOrder.Token.newBuilder()
                    .setAmount(amount)
                    .setDenom(denom)
                    .build();
        }
    }

    class Destination {

        private final String address;
        private final Token[] coins;

        public Destination(String address, Token token) {
            this.address = address;
            this.coins = new Token[]{token};
        }

        public Binance.SendOrder.Input toInputProto() {
            Binance.SendOrder.Input.Builder builder = Binance.SendOrder.Input.newBuilder()
                    .setAddress(ByteString.copyFrom(new Address(address).getBytes()));
            for (Token coin : coins) {
                builder.addCoins(coin.toProto());
            }
            return builder.build();
        }

        public Binance.SendOrder.Output toOutputProto() {
            Binance.SendOrder.Output.Builder builder = Binance.SendOrder.Output.newBuilder()
                    .setAddress(ByteString.copyFrom(new Address(address).getBytes()));
            for (Token coin : coins) {
                builder.addCoins(coin.toProto());
            }
            return builder.build();
        }
    }

    class SendOrder {
        private final Destination[] inputs;
        private final Destination[] outputs;

        public SendOrder(Destination input, Destination output) {
            this.inputs = new Destination[]{input};
            this.outputs = new Destination[]{output};
        }

        public Binance.SendOrder toProto() {
            Binance.SendOrder.Builder builder = Binance.SendOrder.newBuilder();
            for (Destination input : inputs) {
                builder.addInputs(input.toInputProto());
            }
            for (Destination output : outputs) {
                builder.addOutputs(output.toOutputProto());
            }
            return builder.build();
        }
    }
}
