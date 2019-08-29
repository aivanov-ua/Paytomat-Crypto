package com.paytomat.btc.transaction;

import com.paytomat.btc.BitcoinException;
import com.paytomat.btc.Convertor;
import com.paytomat.btc.KeyPair;
import com.paytomat.btc.PrivateKey;

import org.bouncycastle.util.encoders.Hex;

import java.math.BigDecimal;

import static com.paytomat.btc.BitcoinException.CODE_NO_INPUT;

/**
 * created by Alex Ivanov on 7/19/18.
 */
public class UnspentOutputInfo {

    public final KeyPair keyPair;
    public final byte[] txHash;
    public final Script script;
    public final long value;
    public final int outputIndex;

    private UnspentOutputInfo(KeyPair keyPair, byte[] txHash, Script script, long value, int outputIndex) {
        this.keyPair = keyPair;
        this.txHash = txHash;
        this.script = script;
        this.value = value;
        this.outputIndex = outputIndex;
    }

    public static class Builder {

        private KeyPair keyPair;
        private byte[] txHash;
        private Script script;
        private long value;
        private int outputIndex;

        public Builder withKeyPair(KeyPair keyPair) {
            this.keyPair = keyPair;
            return this;
        }

        public Builder withPrivateKey(PrivateKey privateKey) {
            return withKeyPair(new KeyPair(privateKey));
        }

        public Builder withPrivateKeyWif(String privateKeyWif) {
            return withPrivateKey(new PrivateKey(privateKeyWif));
        }

        public Builder withPrivateKeyHex(String privateKeyHex, boolean isCompressed) {
            return withPrivateKey(new PrivateKey(privateKeyHex, isCompressed));
        }

        public Builder withPrivateKeyBytes(byte[] privateKeyBytes) {
            return withPrivateKey(new PrivateKey(privateKeyBytes, true));
        }

        public Builder withTxHash(byte[] txHash) {
            this.txHash = txHash;
            return this;
        }

        public Builder withTxHash(String txHashStr) {
            return withTxHash(Hex.decode(txHashStr));
        }

        public Builder withScript(String script) {
            return withScript(new Script(Hex.decode(script)));
        }

        public Builder withZenScript(String script) {
            return withScript(new ZenScript(Hex.decode(script)));
        }

        public Builder withScript(Script script) {
            this.script = script;
            return this;
        }

        public Builder withScript(String scriptStr, String tokenSymbol, boolean isTestNet) {
            return withScript(ScriptHelper.buildOutput(scriptStr, tokenSymbol, isTestNet));
        }

        public Builder withValue(long valueSatoshi) {
            this.value = valueSatoshi;
            return this;
        }

        public Builder withValue(String value) {
            return withValue(Convertor.parseValue(value, BigDecimal.valueOf(100_000_000)));
        }

        public Builder withOutputIndex(int outputIndex) {
            this.outputIndex = outputIndex;
            return this;
        }

        public UnspentOutputInfo build() {
            if (value == 0 || script == null || txHash == null || keyPair == null) {
                throw new BitcoinException(CODE_NO_INPUT, "Cannot build unspent output");
            }
            return new UnspentOutputInfo(keyPair, txHash, script, value, outputIndex);
        }
    }
}
