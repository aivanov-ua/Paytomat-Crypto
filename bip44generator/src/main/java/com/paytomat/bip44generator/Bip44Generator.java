package com.paytomat.bip44generator;

import com.paytomat.core.util.HashUtil;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.text.Normalizer;
import java.util.Arrays;

import static com.paytomat.bip44generator.Constants.ENGLISH_WORD_LIST;

/**
 * created by Alex Ivanov on 2019-02-12.
 */
public class Bip44Generator {

    private static final String ALGORITHM = "HmacSHA512";
    private static final int REPETITIONS = 2048;
    private static final int BIP32_SEED_LENGTH = 64;
    private static final String BASE_SALT = "mnemonic";
    private static final String DEFAULT_CHARSET = "UTF-8";

    public static MasterSeed createRandomMasterSeed() {
        byte[] rawEntropy = new byte[32];
        new SecureRandom().nextBytes(rawEntropy);
        String[] wordList = rawEntropyToWords(rawEntropy);
        return generateSeedFromWordList(wordList);
    }

    public static boolean isValidWordList(String[] wordList) {
        if (wordList.length != 24) return false;
        for (String word : wordList) {
            if (getWordIndex(word) == -1) return false;
        }
        byte[] bytes = wordListToBytes(wordList);

        int checksumLength = 8;
        byte[] raw = new byte[bytes.length - 1];
        System.arraycopy(bytes, 0, raw, 0, raw.length);

        byte[] checksumHash = HashUtil.sha256(raw).getBytes();
        byte checksumByte = (byte) (((0xFF << (8 - checksumLength)) & 0xFF) & (0xFF & ((int) checksumHash[0])));

        // Verify that the checksum is valid
        byte c = bytes[bytes.length - 1];
        return checksumByte == c;
    }

    public static MasterSeed generateSeedFromWordList(String[] wordList) {
        StringBuilder sb = new StringBuilder();
        for (String s : wordList) {
            sb.append(s).append(' ');
        }
        String menemonic = sb.toString().trim();

        byte[] seed;
        try {
            byte[] saltBytes = Normalizer.normalize(BASE_SALT, Normalizer.Form.NFKD).getBytes(DEFAULT_CHARSET);
            seed = PBKDF.pbkdf2(ALGORITHM, menemonic.getBytes(DEFAULT_CHARSET), saltBytes, REPETITIONS, BIP32_SEED_LENGTH);
        } catch (UnsupportedEncodingException | GeneralSecurityException e) {
            throw new GeneratorException(e.getMessage());
        }
        return new MasterSeed(wordList, seed);
    }

    public static String[] rawEntropyToWords(byte[] rawEntropy) {
        int bitLength = rawEntropy.length * 8;
        if (bitLength != 256) {
            throw new GeneratorException("Raw entropy must be 256 bits and not " + bitLength);
        }

        // Calculate the checksum
        int checksumLength = 8;
        byte[] csHash = HashUtil.sha256(rawEntropy).getBytes();
        byte checksumByte = (byte) (((0xFF << (8 - checksumLength)) & 0xFF) & (0xFF & ((int) csHash[0])));

        // Append the checksum to the raw entropy
        byte[] buf = new byte[rawEntropy.length + 1];
        System.arraycopy(rawEntropy, 0, buf, 0, rawEntropy.length);
        buf[rawEntropy.length] = checksumByte;

        // Turn the array of bytes into a word list where each word represents 11 bits
        String[] words = new String[(bitLength + checksumLength) / 11];
        for (int i = 0; i < words.length; i++) {
            int wordIndex = integerFrom11Bits(buf, i * 11);
            words[i] = ENGLISH_WORD_LIST[wordIndex];
        }
        return words;
    }

    public static byte[] wordListToRawEntropy(String[] wordList) {
        // Get the bytes of the word list
        byte[] bytes = wordListToBytes(wordList);
        // Chop off the checksum byte
        return Arrays.copyOf(bytes, bytes.length - 1);
    }

    private static byte[] wordListToBytes(String[] wordList) {
        if (wordList.length != 24) {
            throw new RuntimeException("Word list must be 24 words and not " + wordList.length);
        }
        int bitLength = wordList.length * 11;
        byte[] buf = new byte[bitLength / 8 + ((bitLength % 8) > 0 ? 1 : 0)];
        for (int i = 0; i < wordList.length; i++) {
            String word = wordList[i];
            int wordIndex = getWordIndex(word);
            if (wordIndex == -1) {
                throw new RuntimeException("The word '" + word + "' is not valid");
            }
            integerTo11Bits(buf, i * 11, wordIndex);
        }
        return buf;
    }

    private static int integerFrom11Bits(byte[] buf, int bitIndex) {
        int value = 0;
        for (int i = 0; i < 11; i++) {
            if (isBitSet(buf, bitIndex + i)) {
                value = (value << 1) | 0x01;
            } else {
                value = (value << 1);
            }
        }
        return value;
    }

    private static void integerTo11Bits(byte[] buf, int bitIndex, int integer) {
        for (int i = 0; i < 11; i++) {
            if ((integer & 0x400) == 0x400) {
                setBit(buf, bitIndex + i);
            }
            integer = integer << 1;
        }
    }

    private static void setBit(byte[] buf, int bitIndex) {
        int value = ((int) buf[bitIndex / 8]) & 0xFF;
        value = value | (1 << (7 - (bitIndex % 8)));
        buf[bitIndex / 8] = (byte) value;
    }

    private static boolean isBitSet(byte[] buf, int bitIndex) {
        int val = ((int) buf[bitIndex / 8]) & 0xFF;
        val = val << (bitIndex % 8);
        val = val & 0x80;
        return val == 0x80;
    }

    private static int getWordIndex(String word) {
        for (int i = 0; i < ENGLISH_WORD_LIST.length; i++) {
            if (ENGLISH_WORD_LIST[i].equals(word)) {
                return i;
            }
        }
        return -1;
    }
}
