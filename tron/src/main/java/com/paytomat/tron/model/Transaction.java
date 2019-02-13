package com.paytomat.tron.model;

import com.paytomat.tron.Address;
import com.paytomat.tron.PrivateKey;
import com.paytomat.tron.Signature;

import org.bouncycastle.util.encoders.Hex;

import java.util.ArrayList;
import java.util.List;

/**
 * created by Alex Ivanov on 2019-01-08.
 */
public class Transaction {

    private List<String> signature = new ArrayList<>();
    private final String txID;
    //raw_data
    private final RawData rawData;

    public Transaction(String signature, String txID, RawData rawData) {
        this.signature.add(signature);
        this.txID = txID;
        this.rawData = rawData;
    }

    public boolean validateFromAddress(Address address) {
        for (Contract contract : rawData.getContracts()) {
            if (address.toHex().equals(contract.getFrom())) return true;
        }
        return false;
    }

    public boolean validateToAddress(Address address) {
        for (Contract contract : rawData.getContracts()) {
            if (address.toHex().equals(contract.getTo())) return true;
        }
        return false;
    }

    public boolean sign(PrivateKey privateKey) {
        if (!validateFromAddress(new Address(privateKey, false))) return false;
        this.signature = new ArrayList<>();
        this.signature.add(new Signature(Hex.decode(txID), privateKey).sign().toHex());
        return true;
    }
}
