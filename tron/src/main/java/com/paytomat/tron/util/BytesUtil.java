package com.paytomat.tron.util;

/**
 * created by Alex Ivanov on 2019-01-08.
 */
public class BytesUtil {

    /**
     * Append arrays
     *
     * @param arrays merge target
     * @return arrays combined in given order
     */
    public static byte[] append(byte[]... arrays) {
        int size = 0;
        for (byte[] array : arrays) size += array.length;

        byte[] result = new byte[size];
        int offset = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }


}
