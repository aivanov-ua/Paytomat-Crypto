package com.paytomat.bip44generator;

import java.util.Arrays;

public class MasterSeed {
    private final byte[] rawEntropy;
    private final byte[] masterSeed;
    private String[] wordList;

    public MasterSeed(byte[] bip39RawEntropy, byte[] bip32MasterSeed) {
        rawEntropy = bip39RawEntropy;
        masterSeed = bip32MasterSeed;
    }

    public MasterSeed(String[] wordList, byte[] masterSeed) {
        this.rawEntropy = Bip44Generator.wordListToRawEntropy(wordList);
        this.masterSeed = masterSeed;
        this.wordList = wordList;
    }

    public String[] getWordList() {
        if (wordList == null) {
            wordList = Bip44Generator.rawEntropyToWords(rawEntropy);
        }
        return wordList;
    }

    public byte[] getBytes() {
        return Arrays.copyOf(masterSeed,  masterSeed.length);
    }

}