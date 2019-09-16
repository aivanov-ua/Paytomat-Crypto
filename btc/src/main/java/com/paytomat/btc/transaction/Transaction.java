package com.paytomat.btc.transaction;

import com.paytomat.btc.BitcoinException;
import com.paytomat.core.util.ByteSerializer;
import com.paytomat.core.util.VarInt32;

import org.bouncycastle.util.encoders.Hex;

import java.security.SecureRandom;
import java.util.List;

/**
 * created by Alex Ivanov on 7/18/18.
 */
public class Transaction extends Message {

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
        ByteSerializer serializer = ByteSerializer.create();
        serializer.writeLE(version);
        if (withWitness) {
            serializer.write((byte) 0)
                    .write((byte) 1);
        } else if (version == 12) {
            serializer.write(previousBlockHash);
        }
        serializer.write(inputs, true)
                .write(outputs, true);
        if (withWitness) {
            for (byte[][] witness : scriptWitnesses) {
                serializer.writeVarInt32(witness.length);
                for (byte[] stackEntry : witness) {
                    serializer.writeVarInt32(stackEntry.length);
                    serializer.write(stackEntry);
                }
            }
        }
        serializer.writeLE(lockTime);
        return serializer.serialize();
    }

    private boolean hasWitnesses() {
        return scriptWitnesses != null && scriptWitnesses.length > 0;
    }

    /**
     * Gets the transaction weight as defined in BIP141.
     */
    public int getWeight() {
        if (hasWitnesses()) {
            final int baseSize = getBytes(false).length;
            final int totalSize = getBytes(true).length;
            return baseSize * 3 + totalSize;
        } else {
            return getMessageSize() * 4;
        }
    }

    /**
     * Gets the virtual transaction size as defined in BIP141.
     */
    public int getVsize() {
        if (!hasWitnesses()) return getMessageSize();
        return (getWeight() + 3) / 4; // round up
    }

    @Override
    public byte[] serialize() {
        return getBytes(hasWitnesses());
    }

    @Override
    public int getMessageSize() {
        int inputsSize = 0;
        for (Input input : inputs) {
            inputsSize += input.getMessageSize();
        }
        int outputSize = 0;
        for (Output output : outputs) {
            outputSize += output.getMessageSize();
        }
        int witnessesSize = 0;
        if (hasWitnesses()) {
            for (byte[][] witness : scriptWitnesses) {
                witnessesSize += VarInt32.size(witness.length);
                for (byte[] stackEntry : witness) {
                    witnessesSize += VarInt32.size(stackEntry.length);
                    witnessesSize += stackEntry.length;
                }
            }
        }


        return 4 + (hasWitnesses() ? 2 : 0) + (version == 12 ? previousBlockHash.length : 0) + VarInt32.size(inputs.length) + inputsSize + VarInt32.size(outputs.length) + outputSize + witnessesSize + 4; //Version(4) + optionalWitnessHeader(2?) + optionalBlockHash(size) + input count + inputs data + outputs size + output data +  witnesses +  lockTime(4)
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
