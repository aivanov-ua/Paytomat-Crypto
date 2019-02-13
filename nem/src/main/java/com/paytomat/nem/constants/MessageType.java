package com.paytomat.nem.constants;

/**
 * created by Alex Ivanov on 6/1/18.
 */
public class MessageType {

    public static MessageType NOT_ENCRYPTED  = new MessageType(1);
    public static MessageType ENCRYPTED  = new MessageType(2);

    public final int messageType;

    private MessageType(int type) {
        this.messageType = type;
    }
}
