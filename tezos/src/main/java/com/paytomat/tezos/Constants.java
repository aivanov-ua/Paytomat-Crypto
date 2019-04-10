package com.paytomat.tezos;

/**
 * created by Alex Ivanov on 2019-04-08.
 */
public interface Constants {

    byte[] SECRET_KEY_PREFIX = new byte[]{43, (byte) 246, 78, 7}; //edsk
    byte[] PUBLIC_KEY_PREFIX = new byte[]{13, 15, 37, (byte) 217}; //edpk
    byte[] WATTERMARK = new byte[]{3}; //03
    byte[] SIGN_PREFIX = new byte[]{9, (byte) 245, (byte) 205, (byte) 134, 18}; //edsig
    byte[] ADDRESS_PREFIX = new byte[]{6, (byte) 161, (byte) 159}; //tz1

}