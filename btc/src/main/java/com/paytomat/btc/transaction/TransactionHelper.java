package com.paytomat.btc.transaction;

import com.paytomat.btc.Address;
import com.paytomat.btc.BitcoinException;
import com.paytomat.btc.Convertor;
import com.paytomat.btc.network.NetworkParamsFactory;
import com.paytomat.core.util.HashUtil;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERSequenceGenerator;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.ECDSASigner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import static com.paytomat.btc.BitcoinException.CODE_AMOUNT_TO_SEND_IS_LESS_THEN_ZERO;
import static com.paytomat.btc.BitcoinException.CODE_CHANGE_IS_LESS_THEN_ZERO;
import static com.paytomat.btc.BitcoinException.CODE_FEE_IS_LESS_THAN_ZERO;
import static com.paytomat.btc.BitcoinException.CODE_FEE_IS_TOO_BIG;
import static com.paytomat.btc.BitcoinException.CODE_GENERAL;
import static com.paytomat.btc.BitcoinException.CODE_INVALID_CHANGE_ADDRESS;
import static com.paytomat.btc.BitcoinException.CODE_NO_INPUT;
import static com.paytomat.btc.BitcoinException.CODE_UNSUPPORTED_SCRIPT;
import static com.paytomat.btc.BitcoinException.CODE_WRONG_PUB_KEY;

/**
 * created by Alex Ivanov on 7/19/18.
 */
public class TransactionHelper {

    private static final long MAX_ALLOWED_FEE = Convertor.parseValue("0.1", BigDecimal.valueOf(100_000_000));

    static final int SIGVERSION_BASE = 0;
    static final int SIGVERSION_WITNESS_V0 = 1;

    private static final ECDomainParameters EC_PARAMS;
    private static final BigInteger LARGEST_PRIVATE_KEY = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141", 16);//SECP256K1_N


    static {
        X9ECParameters params = SECNamedCurves.getByName("secp256k1");
        EC_PARAMS = new ECDomainParameters(params.getCurve(), params.getG(), params.getN(), params.getH());
    }

    static Transaction createTransaction(List<UnspentOutputInfo> unspentOutputs,
                                         String outputAddress,
                                         String changeAddress,
                                         final long amountToSend,
                                         final long feePerB,
                                         String tokenSymbol,
                                         TransactionType transactionType,
                                         long dust,
                                         int blockHeight,
                                         String blockHash,
                                         boolean isTestnet,
                                         SecureRandom secureRandom,
                                         byte[] previousBlockHash) throws BitcoinException {
        if (Address.fromString(outputAddress, tokenSymbol, isTestnet) == null) {
            throw new BitcoinException(BitcoinException.CODE_INVALID_OUTPUT_ADDRESS, "Output address is invalid", outputAddress);
        }
        if (feePerB < NetworkParamsFactory.getParams(tokenSymbol, isTestnet).getMinFeePerByte())
            throw new BitcoinException(CODE_FEE_IS_LESS_THAN_ZERO, "Incorrect fee", feePerB);

        BaseTxInfo baseTxInfo = calcBaseTxInfo(unspentOutputs, feePerB, amountToSend, dust, transactionType);

        Output[] outputs;

        Output outputTo = new Output(baseTxInfo.amountForRecipient, ScriptHelper.buildOutput(outputAddress, tokenSymbol, isTestnet, blockHeight, blockHash));
        if (baseTxInfo.change == 0) {
            outputs = new Output[]{outputTo};
        } else {
            if (outputAddress.equals(changeAddress)) {
                throw new BitcoinException(BitcoinException.CODE_MEANINGLESS_OPERATION, "Change address equals to recipient's address, it is likely an error.");
            }
            if (Address.fromString(changeAddress, tokenSymbol, isTestnet) == null) {
                throw new BitcoinException(CODE_INVALID_CHANGE_ADDRESS, "Change address is invalid", changeAddress);
            }
            outputs = new Output[]{
                    outputTo,
                    new Output(baseTxInfo.change, ScriptHelper.buildOutput(changeAddress, tokenSymbol, isTestnet, blockHeight, blockHash))
            };
        }

        List<UnspentOutputInfo> outputToSpend = baseTxInfo.outputsToSpend;
        Input[] unsignedInputs = new Input[outputToSpend.size()];
        for (int i = 0; i < unsignedInputs.length; i++) {
            UnspentOutputInfo utxo = outputToSpend.get(i);
            OutPoint outPoint = new OutPoint(utxo.txHash, utxo.outputIndex);
            unsignedInputs[i] = new Input(outPoint, null, 0xffffffff);
        }
        Transaction unsignedTx = new Transaction(tokenSymbol.equals("BCD") ? 12 : 1, unsignedInputs, outputs, 0);
        if (unsignedTx.getVersion() == 12) unsignedTx.setPreviousBlockHash(previousBlockHash);

        return sign(outputToSpend, unsignedTx, transactionType, secureRandom);
    }

    private static BaseTxInfo calcBaseTxInfo(List<UnspentOutputInfo> unspentOutputs, long feePerB, long amountToSend,
                                             long dust, TransactionType transactionType) throws BitcoinException {
        long fee = 0;
        long change = 0;
        long utxoValue = 0;
        List<UnspentOutputInfo> outputsToSpend = new ArrayList<>();
        if (amountToSend <= 0) {
            outputsToSpend.addAll(unspentOutputs);
            for (UnspentOutputInfo outputInfo : unspentOutputs) {
                utxoValue += outputInfo.value;
            }
            int txLen = getMaxTxSize(outputsToSpend.size(), 1, transactionType);
            fee = Convertor.calcMinimumFee(feePerB, txLen);
            amountToSend = utxoValue - fee;
        } else {
            for (UnspentOutputInfo outInfo : unspentOutputs) {
                outputsToSpend.add(outInfo);
                utxoValue += outInfo.value;
                long tempFee = 0;
                for (int i = 0; i < 3; i++) {
                    fee = tempFee;
                    change = utxoValue - fee - amountToSend;
                    if (change < dust) {
                        fee += change;
                        change = 0;
                    }
                    int txLen = getMaxTxSize(outputsToSpend.size(), change > 0 ? 2 : 1, transactionType);
                    tempFee = Convertor.calcMinimumFee(feePerB, txLen);
                    if (tempFee == fee) break;
                }
                fee = tempFee;
                if (utxoValue >= amountToSend + fee) break;
            }
            if (amountToSend > utxoValue - fee) {
                amountToSend = utxoValue - fee;
                change = 0;
            }

            if (outputsToSpend.isEmpty())
                throw new BitcoinException(CODE_NO_INPUT, "No utxo to spend");
            if (fee > MAX_ALLOWED_FEE)
                throw new BitcoinException(CODE_FEE_IS_TOO_BIG, "Fee is too big", fee);
            if (fee < 0)
                throw new BitcoinException(CODE_FEE_IS_LESS_THAN_ZERO, "Incorrect fee", fee);
            if (change < 0)
                throw new BitcoinException(CODE_CHANGE_IS_LESS_THEN_ZERO, "Incorrect change", change);
            if (amountToSend < 0)
                throw new BitcoinException(CODE_AMOUNT_TO_SEND_IS_LESS_THEN_ZERO, "Incorrect amount to send", amountToSend);
        }
        return new BaseTxInfo(fee, change, amountToSend, outputsToSpend);
    }

    public static int getMaxTxSize(int inputCount, int outputCount, TransactionType transactionType) {
        if (inputCount == 0 || outputCount == 0) {
            throw new BitcoinException(CODE_NO_INPUT, "No information about tx inputs provided");
        }
        if (transactionType == TransactionType.SEGWIT) {
            return 11 + inputCount * 91 + outputCount * 32;
        } else {
            return 10 + inputCount * 148 + outputCount * 34;
        }
    }

    private static Transaction sign(List<UnspentOutputInfo> unspentOutputs, Transaction unsignedTx, TransactionType txType, SecureRandom secureRandom) {
        int sigVersion = txType != TransactionType.SEGWIT ? SIGVERSION_BASE : SIGVERSION_WITNESS_V0;
        Input[] signedInputs = new Input[unsignedTx.getInputs().length];
        byte hashType = Script.SIGHASH_ALL;
        if (txType == TransactionType.BITCOIN_CASH) hashType |= Script.SIGHASH_FORKID;

        byte[][][] witnesses;
        if (sigVersion == SIGVERSION_BASE) {
            witnesses = new byte[0][][];
        } else {
            witnesses = new byte[signedInputs.length][][];
            for (int i = 0; i < witnesses.length; i++) {
                witnesses[i] = new byte[0][];
            }
        }
        for (int i = 0; i < signedInputs.length; i++) {
            UnspentOutputInfo utxo = unspentOutputs.get(i);
            long inputValue = utxo.value;
            BigInteger privateKey = utxo.keyPair.privateKey.getBigInteger();
            Script subScript = utxo.script;

            Script scriptSig;
            if (subScript.isPay2PublicKeyHash()) {
                byte[] signatureAndHashType = getSignatureAndHashType(unsignedTx, i, inputValue, privateKey, subScript.bytes, SIGVERSION_BASE, hashType, secureRandom);
                scriptSig = new Script(signatureAndHashType, utxo.keyPair.publicKey.getBytes());
            } else if (subScript.isPubkey()) {
                byte[] signatureAndHashType = getSignatureAndHashType(unsignedTx, i, inputValue, privateKey, subScript.bytes, SIGVERSION_BASE, hashType, secureRandom);
                scriptSig = new Script(ScriptHelper.convertDataToScript(signatureAndHashType));
            } else if (sigVersion != SIGVERSION_BASE) {
                Script.WitnessProgram wp;
                if (utxo.script.isPayToScriptHash()) {
                    if (utxo.keyPair.publicKey != null && utxo.keyPair.publicKey.getBytes().length > 33) {
                        throw new BitcoinException(CODE_WRONG_PUB_KEY, "Writing uncompressed public key into witness");
                    }
                    wp = new Script.WitnessProgram(0, HashUtil.sha256ripemd160(utxo.keyPair.publicKey.getBytes()));
                    scriptSig = new Script(ScriptHelper.convertDataToScript(wp.getBytes()));
                } else {
                    wp = utxo.script.getWitnessProgram();
                    scriptSig = new Script(new byte[0]);
                }
                byte[] actualSubScriptForWitness;
                if (wp != null) {
                    try {
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        if (wp.program.length == 20) {
                            os.write(Script.OP_DUP);
                            os.write(Script.OP_HASH160);
                            os.write(ScriptHelper.convertDataToScript(wp.program));
                            os.write(Script.OP_EQUALVERIFY);
                            os.write(Script.OP_CHECKSIG);
                        } else {
                            throw new BitcoinException(CODE_UNSUPPORTED_SCRIPT, "Unsupported scriptPubKey type: " + utxo.script);
                        }
                        os.close();
                        actualSubScriptForWitness = os.toByteArray();
                    } catch (IOException e) {
                        throw new BitcoinException(CODE_GENERAL, e.getLocalizedMessage());
                    }
                } else {
                    throw new BitcoinException(CODE_WRONG_PUB_KEY, "Unsupported scriptPubKey type: " + utxo.script);
                }
                byte[] signatureAndHashType = getSignatureAndHashType(unsignedTx, i, inputValue, privateKey, actualSubScriptForWitness, sigVersion, hashType, secureRandom);
                if (utxo.keyPair.publicKey == null) {
                    throw new BitcoinException(CODE_WRONG_PUB_KEY, "Writing null public key into witness");
                }
                if (utxo.keyPair.publicKey.getBytes().length > 33) {
                    throw new BitcoinException(CODE_WRONG_PUB_KEY, "Writing uncompressed public key into witness");
                }
                witnesses[i] = new byte[][]{signatureAndHashType, utxo.keyPair.publicKey.getBytes()};
            } else {
                throw new BitcoinException(CODE_UNSUPPORTED_SCRIPT, "Unsupported scriptPubKey type: " + utxo.script + " for base sig version");
            }
            signedInputs[i] = new Input(unsignedTx.getInputs()[i].outPoint, scriptSig, unsignedTx.getInputs()[i].sequence);
        }
        Transaction tx = new Transaction(unsignedTx.getVersion(),
                signedInputs,
                unsignedTx.getOutputs(),
                witnesses,
                unsignedTx.getLockTime());
        if (tx.getVersion() == 12) tx.setPreviousBlockHash(unsignedTx.getPreviousBlockHash());
        return tx;
    }

    private static byte[] getSignatureAndHashType(Transaction unsignedTx, int i, long inputValue,
                                                  BigInteger privateKey, byte[] subScript,
                                                  int sigVersion, byte hashType,
                                                  SecureRandom secureRandom) {
        byte[] hash = ScriptHelper.hashTransaction(i, subScript, unsignedTx, hashType, inputValue, sigVersion);
        byte[] signature = sign(privateKey, hash, secureRandom);
        byte[] signatureAndHashType = new byte[signature.length + 1];
        System.arraycopy(signature, 0, signatureAndHashType, 0, signature.length);
        signatureAndHashType[signatureAndHashType.length - 1] = hashType;
        return signatureAndHashType;
    }

    private static byte[] sign(BigInteger privateKey, byte[] input, SecureRandom secureRandom) {
        synchronized (EC_PARAMS) {
            ECDSASigner signer = new ECDSASigner();
            ECPrivateKeyParameters privateKeyParam = new ECPrivateKeyParameters(privateKey, EC_PARAMS);
            signer.init(true, new ParametersWithRandom(privateKeyParam, secureRandom));
            BigInteger[] sign = signer.generateSignature(input);
            BigInteger r = sign[0];
            BigInteger s = sign[1];
            BigInteger largestAllowedS = new BigInteger("7FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF5D576E7357A4501DDFE92F46681B20A0", 16);//SECP256K1_N_DIV_2
            if (s.compareTo(largestAllowedS) > 0) {
                //https://github.com/bitcoin/bips/blob/master/bip-0062.mediawiki#low-s-values-in-signatures
                s = LARGEST_PRIVATE_KEY.subtract(s);
            }
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(72);
                DERSequenceGenerator derGen = new DERSequenceGenerator(baos);
                derGen.addObject(new ASN1Integer(r));
                derGen.addObject(new ASN1Integer(s));
                derGen.close();
                return baos.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
