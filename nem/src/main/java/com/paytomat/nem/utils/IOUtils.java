package com.paytomat.nem.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * created by Alex Ivanov on 2019-02-13.
 */
public class IOUtils {

    public static void closeSilently(final Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
