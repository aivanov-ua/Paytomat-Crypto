package com.paytomat.tron.model;

import java.util.Collections;
import java.util.List;

/**
 * created by Alex Ivanov on 2019-01-09.
 */
public class RawData {
    //contract
    private final List<Contract> contracts;
    //ref_block_bytes
    private final String refBlockBytes;
    //ref_block_hash
    private final String refBlockHash;
    private final long expiration;
    private final long timestamp;

    public RawData(Contract contract, String refBlockBytes, String refBlockHash, long expiration, long timestamp) {
        this.contracts = Collections.singletonList(contract);
        this.refBlockBytes = refBlockBytes;
        this.refBlockHash = refBlockHash;
        this.expiration = expiration;
        this.timestamp = timestamp;
    }

    public List<Contract> getContracts() {
        return contracts;
    }
}
