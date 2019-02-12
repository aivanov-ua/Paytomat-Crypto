package com.payomat.eos;

/**
 * created by Alex Ivanov on 2019-02-11.
 */
public class EosTransactionException extends RuntimeException {

    public static final int CODE_WRONG_SIGNATURE_INPUT = 1000;
    public static final int CODE_WRONG_HASH_SIZE = 1001;
    public static final int CODE_CANNOT_RECOVER_PUB_KEY = 1002;
    public static final int CODE_TRANSACTION_SERIALIZATION_ERROR = 1003;
    public static final int CODE_AMOUNT_TOO_SMALL = 1004;
    public static final int CODE_INVALID_SYMBOL = 1005;
    public static final int CODE_NOT_IMPLEMENTED = 1006;
    public static final int CODE_WRONG_PUBLIC_KEY = 1007;
    public static final int CODE_INVALID_PRODUCERS_AMOUNT = 1008;

    private final int errorCode;

    public EosTransactionException(String message, int code) {
        super("SignatureException" + message);
        this.errorCode = code;
    }

    public int getErrorCode() {
        return errorCode;
    }
}