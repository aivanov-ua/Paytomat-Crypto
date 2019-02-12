package com.paytomat.btc.transaction;

import org.bouncycastle.util.encoders.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.paytomat.btc.transaction.ScriptHelper.writeBytes;

/**
 * created by Alex Ivanov on 7/18/18.
 */
public class Script {

    static final byte OP_FALSE = 0;
    static final byte OP_TRUE = 0x51;
    static final byte OP_16 = 0x60;
    public static final byte OP_DUP = 0x76;//Duplicates the top stack item.
    public static final byte OP_HASH160 = (byte) 0xA9;//The input is hashed twice: first with SHA-256 and then with RIPEMD-160.
    public static final byte OP_EQUAL = (byte) 0x87;//Returns 1 if the inputs are exactly equal, 0 otherwise.
    public static final byte OP_EQUALVERIFY = (byte) 0x88;//Same as OP_EQUAL, but runs OP_VERIFY afterward.
    static final byte OP_CODESEPARATOR = (byte) 0xAB;
    public static final byte OP_CHECKSIG = (byte) 0xAC;//The entire transaction's outputs, inputs, and script (from the most recently-executed OP_CODESEPARATOR to the end) are hashed. The signature used by OP_CHECKSIG must be a valid signature for this hash and public key. If it is, 1 is returned, 0 otherwise.
    static final byte OP_PUSHDATA1 = 0x4c;
    static final byte OP_PUSHDATA2 = 0x4d;
    static final byte OP_PUSHDATA4 = 0x4e;
    public static final byte OP_CHECKBLOCKATHEIGHT = (byte) 0xb4;

    static final byte SIGHASH_ALL = 1;
    static final byte SIGHASH_NONE = 2;
    static final byte SIGHASH_SINGLE = 3;
    static final byte SIGHASH_FORKID = 0x40;
    static final int SIGHASH_ANYONECANPAY = 0x80;
    static final int SIGHASH_MASK = 0x1f;

    public final byte[] bytes;

    public Script(byte[] bytes) {
        this.bytes = bytes;
    }

    public Script(byte[] data1, byte[] data2) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(data1.length + data2.length + 2);
        try {
            writeBytes(baos, data1);
            writeBytes(baos, data2);
            baos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        bytes = baos.toByteArray();
    }

    boolean isPay2PublicKeyHash() {
        return bytes.length == 25 &&
                bytes[0] == Script.OP_DUP &&
                bytes[1] == Script.OP_HASH160 &&
                bytes[2] == 20;
    }

    boolean isPubkey() {
        return bytes.length > 2 &&
                ScriptHelper.getScriptTokenLengthAt(bytes, 0) == bytes.length - 1 &&
                bytes[bytes.length - 1] == Script.OP_CHECKSIG;
    }

    boolean isPayToScriptHash() {
        return bytes.length == 23 &&
                bytes[0] == OP_HASH160 &&
                bytes[1] == 0x14 &&
                bytes[22] == OP_EQUAL;
    }

    WitnessProgram getWitnessProgram() {
        if (bytes.length < 4 || bytes.length > 42) {
            return null;
        }
        int versionByte = bytes[0] & 0xFF;
        if (versionByte != 0 && (versionByte < Script.OP_TRUE || versionByte > Script.OP_16)) {
            return null;
        }
        int witnessProgramLen = bytes[1] & 0xff;
        if (witnessProgramLen == bytes.length - 2) {
            byte[] witnessProgram = new byte[witnessProgramLen];
            System.arraycopy(bytes, 2, witnessProgram, 0, witnessProgram.length);
            return new WitnessProgram(ScriptHelper.decodeOpN(versionByte), witnessProgram);
        }
        return null;
    }

    @Override
    public String toString() {
        return Hex.toHexString(bytes);
    }

    static class WitnessProgram {
        final int version;
        final byte[] program;

        public WitnessProgram(int version, byte[] witnessProgram) {
            this.version = version;
            this.program = witnessProgram;
        }

        @Override
        public String toString() {
            return "WitnessProgram{" +
                    "version=" + version +
                    ", program=" + Hex.toHexString(program) +
                    '}';
        }

        public byte[] getBytes() {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            os.write(version == 0 ? 0 : (version + 0x50));
            try {
                ScriptHelper.writeBytes(os, program);
                os.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return os.toByteArray();
        }

        public boolean isWitnessKeyHashType() {
            return program.length == 20;
        }

        public boolean isWitnessSha256Type() {
            return program.length == 32;
        }
    }

}
