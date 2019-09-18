package com.paytomat.btc.transaction;

import com.paytomat.btc.Address;
import com.paytomat.btc.BitcoinException;
import com.paytomat.btc.network.NetworkParams;
import com.paytomat.btc.network.NetworkParamsFactory;
import com.paytomat.core.util.ByteSerializer;
import com.paytomat.core.util.HashUtil;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static com.paytomat.btc.BitcoinException.CODE_INVALID_OUTPUT_ADDRESS;
import static com.paytomat.btc.BitcoinException.CODE_SIGN_FAILED;
import static com.paytomat.btc.transaction.Script.OP_16;
import static com.paytomat.btc.transaction.Script.OP_CHECKBLOCKATHEIGHT;
import static com.paytomat.btc.transaction.Script.OP_CHECKSIG;
import static com.paytomat.btc.transaction.Script.OP_CODESEPARATOR;
import static com.paytomat.btc.transaction.Script.OP_DUP;
import static com.paytomat.btc.transaction.Script.OP_EQUAL;
import static com.paytomat.btc.transaction.Script.OP_EQUALVERIFY;
import static com.paytomat.btc.transaction.Script.OP_FALSE;
import static com.paytomat.btc.transaction.Script.OP_HASH160;
import static com.paytomat.btc.transaction.Script.OP_PUSHDATA1;
import static com.paytomat.btc.transaction.Script.OP_PUSHDATA2;
import static com.paytomat.btc.transaction.Script.OP_PUSHDATA4;
import static com.paytomat.btc.transaction.Script.OP_TRUE;
import static com.paytomat.btc.transaction.Script.SIGHASH_MASK;
import static com.paytomat.btc.transaction.Script.SIGHASH_NONE;
import static com.paytomat.btc.transaction.Script.SIGHASH_SINGLE;
import static com.paytomat.btc.transaction.TransactionHelper.SIGVERSION_BASE;
import static com.paytomat.btc.transaction.TransactionHelper.SIGVERSION_WITNESS_V0;

/**
 * created by Alex Ivanov on 7/19/18.
 */
public class ScriptHelper {
    public static Script buildOutput(String addressStr, String tokenSymbol, boolean isTestNet, int blockHeight, String blockHash) throws BitcoinException {
        if (tokenSymbol.toUpperCase().equals("ZEN")) {
            return buildOutputZenCash(addressStr, blockHeight, blockHash, isTestNet);
        } else {
            return buildOutput(addressStr, tokenSymbol, isTestNet);
        }
    }

    static Script buildOutput(String addressStr, String tokenSymbol, boolean isTestNet) throws BitcoinException {
        try {
            NetworkParams params = NetworkParamsFactory.getParams(tokenSymbol, isTestNet);
            Address address = Address.fromString(addressStr, params);
            if (address == null)
                throw new BitcoinException(CODE_INVALID_OUTPUT_ADDRESS, "Ivalid address");
            if (address.isP2PKH()) {
                //P2PKH
                ByteArrayOutputStream baos = new ByteArrayOutputStream(25);
                baos.write(OP_DUP);
                baos.write(OP_HASH160);
                writeBytes(baos, address.getHash160());
                baos.write(OP_EQUALVERIFY);
                baos.write(OP_CHECKSIG);
                return new Script(baos.toByteArray());
            } else {
                //P2SH
                ByteArrayOutputStream baos = new ByteArrayOutputStream(25);
                baos.write(OP_HASH160);
                writeBytes(baos, address.getHash160());
                baos.write(OP_EQUAL);
                return new Script(baos.toByteArray());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Script buildOutputZenCash(String addressStr, int blockHeight, String blockHash, boolean isTestNet) throws BitcoinException {
        try {
            NetworkParams params = NetworkParamsFactory.getParams("ZEN", isTestNet);
            Address address = Address.fromString(addressStr, params);
            if (address == null) {
                throw new BitcoinException(CODE_INVALID_OUTPUT_ADDRESS, "Ivalid address");
            } else if (address.isP2PKH()) {
                //P2PKH
                byte[] subAddress = address.getHash160();
                byte[] blockHeightBytes = encodeBlockHeight(blockHeight);
                byte[] blockHashBytes = encodeBlockHash(blockHash);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                baos.write(OP_DUP);
                baos.write(OP_HASH160);
                writeBytes(baos, subAddress);
                baos.write(OP_EQUALVERIFY);
                baos.write(OP_CHECKSIG);
                writeBytes(baos, blockHashBytes);
                writeBytes(baos, blockHeightBytes);
                baos.write(OP_CHECKBLOCKATHEIGHT);
                return new ZenScript(baos.toByteArray());
            } else {
                //P2SH
                String addrHex = address.getBytesHex();
                String subAddressHex = addrHex.substring(4);
                byte[] subAddress = Hex.decode(subAddressHex);
                byte[] blockHeightBytes = encodeBlockHeight(blockHeight);
                byte[] blockHashBytes = encodeBlockHash(blockHash);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                baos.write(OP_HASH160);
                writeBytes(baos, subAddress);
                baos.write(OP_EQUAL);
                writeBytes(baos, blockHashBytes);
                writeBytes(baos, blockHeightBytes);
                baos.write(OP_CHECKBLOCKATHEIGHT);
                return new ZenScript(baos.toByteArray());

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] encodeBlockHeight(int blockHeight) {
        byte[] blockHeightBytes = ByteBuffer
                .allocate(4)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putInt(blockHeight)
                .array();
        if (blockHeightBytes[3] == 0x00) {
            byte[] temp = new byte[3];
            System.arraycopy(blockHeightBytes, 0, temp, 0, 3);
            blockHeightBytes = temp;
        }
        return blockHeightBytes;
    }

    private static byte[] encodeBlockHash(String blockHash) {
        return Arrays.reverse(Hex.decode(blockHash));
    }

    public static void writeBytes(ByteArrayOutputStream baos, byte[] data) throws IOException {
        if (data.length < OP_PUSHDATA1) {
            baos.write(data.length);
        } else if (data.length < 0xff) {
            baos.write(OP_PUSHDATA1);
            baos.write(data.length);
        } else if (data.length < 0xffff) {
            baos.write(OP_PUSHDATA2);
            baos.write(data.length & 0xff);
            baos.write((data.length >> 8) & 0xff);
        } else {
            baos.write(OP_PUSHDATA4);
            baos.write(data.length & 0xff);
            baos.write((data.length >> 8) & 0xff);
            baos.write((data.length >> 16) & 0xff);
            baos.write((data.length >>> 24) & 0xff);
        }
        baos.write(data);
    }

    static int getScriptTokenLengthAt(byte[] script, int pos) {
        int op = script[pos] & 0xff;
        if (op > OP_PUSHDATA4) {
            return 1;
        }
        if (op < OP_PUSHDATA1) {
            return 1 + op;
        }
        if (op == OP_PUSHDATA1) {
            return 2 + (script[pos + 1] & 0xff);
        }
        throw new BitcoinException(CODE_SIGN_FAILED, "No large data load implemented");
    }

    static byte[] hashTransaction(int inputIndex, byte[] subScript, Transaction tx, int hashType, long amount, int sigVersion) {
        boolean bitcoinCash = (hashType & Script.SIGHASH_FORKID) == Script.SIGHASH_FORKID;
        if (tx != null && (hashType & SIGHASH_MASK) == SIGHASH_SINGLE && inputIndex >= tx.getOutputs().length && sigVersion == SIGVERSION_BASE) {
            byte[] hash = new byte[32];
            hash[0] = 1;
            return hash;
        }
        if (!bitcoinCash && sigVersion == SIGVERSION_BASE) {
            subScript = findAndDelete(subScript, new byte[]{OP_CODESEPARATOR});
        }
        int inputsCount = tx == null ? 0 : tx.getInputs().length;
        Input[] unsignedInputs = new Input[inputsCount];
        for (int i = 0; i < inputsCount; i++) {
            Input txInput = tx.getInputs()[i];
            if (i == inputIndex) {
                unsignedInputs[i] = new Input(txInput.outPoint, new Script(subScript), txInput.sequence);
            } else {
                unsignedInputs[i] = new Input(txInput.outPoint, new Script(new byte[0]), txInput.sequence);
            }
        }
        Output[] outputs;
        if (sigVersion == SIGVERSION_BASE) {
            switch (hashType & SIGHASH_MASK) {
                case SIGHASH_NONE:
                    outputs = new Output[0];
                    for (int i = 0; i < inputsCount; i++) {
                        if (i != inputIndex) {
                            unsignedInputs[i] = new Input(unsignedInputs[i].outPoint, unsignedInputs[i].scriptSig, 0);
                        }
                    }
                    break;
                case SIGHASH_SINGLE:
                    outputs = new Output[inputIndex + 1];
                    for (int i = 0; i < inputIndex; i++) {
                        outputs[i] = new Output(-1, new Script(new byte[0]));
                    }
                    if (tx == null) {
                        throw new RuntimeException("Null TX in hashTransaction/SIGHASH_SINGLE");
                    }
                    outputs[inputIndex] = tx.getOutputs()[inputIndex];
                    for (int i = 0; i < inputsCount; i++) {
                        if (i != inputIndex) {
                            unsignedInputs[i] = new Input(unsignedInputs[i].outPoint, unsignedInputs[i].scriptSig, 0);
                        }
                    }
                    break;
                default:
                    outputs = tx == null ? new Output[0] : tx.getOutputs();
                    break;
            }

            if ((hashType & Script.SIGHASH_ANYONECANPAY) != 0) {
                unsignedInputs = new Input[]{unsignedInputs[inputIndex]};
            }
        } else {
            outputs = tx == null ? new Output[0] : tx.getOutputs();
        }
        Transaction unsignedTransaction = new Transaction(tx == null ? 1 : tx.getVersion(), unsignedInputs, outputs, tx == null ? 0 : tx.getLockTime());
        if (tx.getVersion() == 12)
            unsignedTransaction.setPreviousBlockHash(tx.getPreviousBlockHash());
        if (bitcoinCash || sigVersion == SIGVERSION_WITNESS_V0) {
            if (tx == null) {
                throw new RuntimeException("null tx");
            }
            return bip143Hash(inputIndex, unsignedTransaction, hashType, subScript, amount);
        } else {
            byte[] txUnsignedBytes = unsignedTransaction.getBytes(false);
            BitcoinOutputStream baos = new BitcoinOutputStream();
            try {
                baos.write(txUnsignedBytes);
                baos.writeInt32(hashType);
                baos.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return HashUtil.doubleSha256(baos.toByteArray()).getBytes();
        }
    }

    private static byte[] findAndDelete(byte[] script, byte[] scriptTokenToDelete) {
        for (int i = 0; i < script.length; ) {
            int tokenLength = getScriptTokenLengthAt(script, i);
            if (tokenLength == scriptTokenToDelete.length) {
                boolean equals = true;
                for (int j = 0; j < tokenLength; j++) {
                    if (script[i + j] != scriptTokenToDelete[j]) {
                        equals = false;
                        break;
                    }
                }
                if (equals) {
                    byte[] updatedScript = new byte[script.length - tokenLength];
                    System.arraycopy(script, 0, updatedScript, 0, i);
                    System.arraycopy(script, i + tokenLength, updatedScript, i, updatedScript.length - i);
                    script = updatedScript;
                    i -= tokenLength;
                }
            }
            i += tokenLength;
        }
        return script;
    }

    private static byte[] bip143Hash(int inputIndex, Transaction tx, int hashType, byte[] script, long amount) {
        boolean single = (hashType & SIGHASH_MASK) == Script.SIGHASH_SINGLE;
        boolean none = (hashType & SIGHASH_MASK) == Script.SIGHASH_NONE;
        BitcoinOutputStream baos = new BitcoinOutputStream();
        try {
//                    1. nVersion of the transaction (4-byte little endian)
            baos.writeInt32(tx.getVersion());
//                    2. hashPrevouts (32-byte hash)
            if ((hashType & Script.SIGHASH_ANYONECANPAY) == 0) {
                BitcoinOutputStream prevOuts = new BitcoinOutputStream();
                for (Input input : tx.getInputs()) {
                    prevOuts.write(Arrays.reverse(input.outPoint.hash));
                    prevOuts.writeInt32(input.outPoint.index);
                }
                prevOuts.close();
                baos.write(HashUtil.doubleSha256(prevOuts.toByteArray()).getBytes());
            } else {
                baos.write(new byte[32]);
            }
//                    3. hashSequence (32-byte hash)
            if ((hashType & Script.SIGHASH_ANYONECANPAY) == 0 && !single && !none) {
                BitcoinOutputStream sequences = new BitcoinOutputStream();
                for (Input input : tx.getInputs()) {
                    sequences.writeInt32(input.sequence);
                }
                sequences.close();
                baos.write(HashUtil.doubleSha256(sequences.toByteArray()).getBytes());
            } else {
                baos.write(new byte[32]);
            }
//                    4. outpoint (32-byte hash + 4-byte little endian)
            baos.write(Arrays.reverse(tx.getInputs()[inputIndex].outPoint.hash));
            baos.writeInt32(tx.getInputs()[inputIndex].outPoint.index);
//                    5. scriptCode of the input (serialized as scripts inside CTxOuts)
            baos.write(convertDataToScript(script));
//                    6. value of the output spent by this input (8-byte little endian)
            baos.writeInt64(amount);
//                    7. nSequence of the input (4-byte little endian)
            baos.writeInt32(tx.getInputs()[inputIndex].sequence);
//                    8. hashOutputs (32-byte hash)
            BitcoinOutputStream outputStream = new BitcoinOutputStream();
            if (!single && !none) {
                for (Output output : tx.getOutputs()) {
                    outputStream.writeInt64(output.value);
                    outputStream.write(convertDataToScript(output.script == null ? new byte[0] : output.script.bytes));
                }
                outputStream.close();
                baos.write(HashUtil.doubleSha256(outputStream.toByteArray()).getBytes());
            } else if (single && inputIndex < tx.getOutputs().length) {
                outputStream.writeInt64(tx.getOutputs()[inputIndex].value);
                outputStream.write(convertDataToScript(tx.getOutputs()[inputIndex].script == null ? new byte[0] : tx.getOutputs()[inputIndex].script.bytes));
                outputStream.close();
                baos.write(HashUtil.doubleSha256(outputStream.toByteArray()).getBytes());
            } else {
                baos.write(new byte[32]);
            }
//                    9. nLocktime of the transaction (4-byte little endian)
            baos.writeInt32(tx.getLockTime());
//                    10. sighash type of the signature (4-byte little endian)
            baos.writeInt32(hashType);
            return HashUtil.doubleSha256(baos.toByteArray()).getBytes();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static byte[] convertDataToScript(byte[] bytes) throws BitcoinException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length + 1);
        try {
            writeBytes(baos, bytes);
            baos.close();
        } catch (IOException e) {
            throw new BitcoinException(CODE_SIGN_FAILED, e.getLocalizedMessage());
        }
        return baos.toByteArray();
    }

    static int decodeOpN(int opcode) {
        if (opcode == OP_FALSE)
            return 0;
        if (opcode < OP_TRUE || opcode > OP_16) {
            throw new IllegalArgumentException("decodeOpN " + opcode);
        }
        return opcode - (OP_TRUE - 1);
    }

    public static Script createP2WPKHOutputScript(byte[] hash) {
        if (hash.length != Address.NUM_ADDRESS_HASH) throw new IllegalArgumentException();
        return new Script(ByteSerializer.create()
                .write((byte) 0)
                .write((byte) hash.length)
                .write(hash)
                .serialize());
    }
}
