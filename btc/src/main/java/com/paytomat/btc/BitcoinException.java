package com.paytomat.btc;

/**
 * created by Alex Ivanov on 2019-02-12.
 */
public class BitcoinException extends RuntimeException {

    public static final int CODE_SIGN_FAILED = 1000;
    public static final int CODE_NO_INPUT = 1001;
    public static final int CODE_INVALID_OUTPUT_ADDRESS = 1002;
    public static final int CODE_MEANINGLESS_OPERATION = 1003;
    public static final int CODE_INVALID_CHANGE_ADDRESS = 1004;
    public static final int CODE_UNSUPPORTED_SCRIPT = 1005;
    public static final int CODE_WRONG_PUB_KEY = 1006;
    public static final int CODE_FEE_IS_TOO_BIG = 1007;
    public static final int CODE_FEE_IS_LESS_THAN_ZERO = 1008;
    public static final int CODE_CHANGE_IS_LESS_THEN_ZERO = 1009;
    public static final int CODE_AMOUNT_TO_SEND_IS_LESS_THEN_ZERO = 1010;
    public static final int CODE_BAD_FORMAT = 1011;
    public static final int CODE_WRONG_TYPE = 1012;
    public static final int CODE_UNSUPPORTED = 1013;

    public static final int CODE_GENERAL = 1999;

    public final int errorCode;
    @SuppressWarnings({"WeakerAccess", "unused"})
    public final Object extraInformation;

    public BitcoinException(int errorCode, String detailMessage, Object extraInformation) {
        super(detailMessage);
        this.errorCode = errorCode;
        this.extraInformation = extraInformation;
    }

    public BitcoinException(int errorCode, String detailMessage) {
        this(errorCode, detailMessage, null);
    }

}
