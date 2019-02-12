package com.paytomat.btc.transaction;

import com.paytomat.btc.BitcoinException;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.List;

/**
 * created by Alex Ivanov on 7/18/18.
 */
public class Transaction {

    private final int version;
    private final Input[] inputs;
    private final Output[] outputs;
    private final byte[][][] scriptWitnesses;
    private final int lockTime;
    private byte[] previousBlockHash = null;

    public Transaction(Input[] inputs, Output[] outputs, int lockTime) {
        this(1, inputs, outputs, lockTime);
    }

    public Transaction(int version, Input[] inputs, Output[] outputs, int lockTime) {
        this(version, inputs, outputs, new byte[0][][], lockTime);
    }

    public Transaction(int version, Input[] inputs, Output[] outputs, byte[][][] scriptWitnesses, int lockTime) {
        this.version = version;
        this.inputs = inputs;
        this.outputs = outputs;
        this.scriptWitnesses = scriptWitnesses;
        this.lockTime = lockTime;
    }

    public void setPreviousBlockHash(byte[] previousBlockHash) {
        this.previousBlockHash = previousBlockHash;
    }

    public byte[] getPreviousBlockHash() {
        return previousBlockHash;
    }

    public int getVersion() {
        return version;
    }

    public Input[] getInputs() {
        return inputs;
    }

    public Output[] getOutputs() {
        return outputs;
    }

    public int getLockTime() {
        return lockTime;
    }

    public byte[] getBytes(boolean withWitness) {
        if (withWitness && scriptWitnesses.length == 0) {
            withWitness = false;
        }
        BitcoinOutputStream bos = new BitcoinOutputStream();
        try {
            bos.writeInt32(version);
            if (withWitness) {
                bos.write(0);
                bos.write(1);
            } else if (version == 12) {
                bos.write(previousBlockHash);
            }
            bos.writeVarInt(inputs.length);
            for (Input input : inputs) {
                bos.write(Arrays.reverse(input.outPoint.hash));
                bos.writeInt32(input.outPoint.index);
                int scriptLen = input.scriptSig == null ? 0 : input.scriptSig.bytes.length;
                bos.writeVarInt(scriptLen);
                if (scriptLen > 0) {
                    bos.write(input.scriptSig.bytes);
                }
                bos.writeInt32(input.sequence);
            }
            bos.writeVarInt(outputs.length);
            for (Output output : outputs) {
                bos.writeInt64(output.value);
                int scriptLen = output.script == null ? 0 : output.script.bytes.length;
                bos.writeVarInt(scriptLen);
                if (scriptLen > 0) {
                    bos.write(output.script.bytes);
                }
            }
            if (withWitness) {
                for (byte[][] witness : scriptWitnesses) {
                    bos.writeVarInt(witness.length);
                    for (byte[] stackEntry : witness) {
                        bos.writeVarInt(stackEntry.length);
                        bos.write(stackEntry);
                    }
                }
            }
            bos.writeInt32(lockTime);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BitcoinException(BitcoinException.CODE_BAD_FORMAT, "Cannot serialize tx");
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bos.toByteArray();
    }

    public String toString() {
        return Hex.toHexString(getBytes(true));
    }

    public static class Builder {
        private List<UnspentOutputInfo> unspentOutputInfoList;
        private String outputAddress, changeAddress, tokenSymbol;
        private long ammountToSend, feePerB, dust;
        private TransactionType transactionType;
        private boolean isTestnet = false;
        private int blockHeight = 0;
        private String blockHash = "";
        private byte[] previousBlockHash = null;

        public Builder(String tokenSymbol) {
            this.tokenSymbol = tokenSymbol;
        }

        public Builder withUnspentInfo(List<UnspentOutputInfo> unspentOutputInfoList) {
            this.unspentOutputInfoList = unspentOutputInfoList;
            return this;
        }

        public Builder withOutputAddress(String outputAddress) {
            this.outputAddress = outputAddress;
            return this;
        }

        public Builder withChangeAddress(String changeAddress) {
            this.changeAddress = changeAddress;
            return this;
        }

        public Builder withAmount(long amountSatoshi) {
            this.ammountToSend = amountSatoshi;
            return this;
        }

        public Builder withFeePerB(long feePerB) {
            this.feePerB = feePerB;
            return this;
        }

        public Builder withDust(long dust) {
            this.dust = dust;
            return this;
        }

        public Builder withTransactionType(TransactionType txType) {
            this.transactionType = txType;
            return this;
        }

        public Builder withTestNetwork(boolean isTestnet) {
            this.isTestnet = isTestnet;
            return this;
        }

        public Builder withBlockHeight(int blockHeight) {
            this.blockHeight = blockHeight;
            return this;
        }

        public Builder withBlockHash(String blockHash) {
            this.blockHash = blockHash;
            return this;
        }

        public Builder withPreviousBlockHash(byte[] previousBlockHash) {
            this.previousBlockHash = previousBlockHash;
            return this;
        }

        public Transaction build(SecureRandom secureRandom) {
            if (unspentOutputInfoList == null || outputAddress == null || changeAddress == null ||
                    tokenSymbol == null || ammountToSend == 0) {
                throw new BitcoinException(BitcoinException.CODE_BAD_FORMAT, "Cannot create tx");
            }

            return TransactionHelper.createTransaction(unspentOutputInfoList,
                    outputAddress,
                    changeAddress,
                    ammountToSend,
                    feePerB,
                    tokenSymbol,
                    transactionType,
                    dust,
                    blockHeight,
                    blockHash,
                    isTestnet,
                    secureRandom,
                    previousBlockHash);
        }
    }
}
