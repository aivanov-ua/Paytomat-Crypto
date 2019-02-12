package com.paytomat.btc.transaction;

/**
 * created by Alex Ivanov on 7/19/18.
 */
public class Input {

    public final OutPoint outPoint;
    public final Script scriptSig;
    public final int sequence;

    public Input(OutPoint outPoint, Script scriptSig, int sequence) {
        this.outPoint = outPoint;
        this.scriptSig = scriptSig;
        this.sequence = sequence;
    }

    @Override
    public String toString() {
        return "Input{" +
                "outPoint=" + outPoint +
                ", scriptSig=" + scriptSig +
                ", sequence=" + sequence +
                '}';
    }
}