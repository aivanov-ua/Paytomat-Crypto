package com.paytomat.btc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * created by Alex Ivanov on 2020-02-20.
 */
public class FileUtil {

    public static String readFile(String fileName, Class<?> clazz) throws IOException {
        InputStream input = clazz.getClassLoader().getResourceAsStream(fileName);
        if (input == null) return "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        StringBuilder out = new StringBuilder();
        String line = reader.readLine();
        while (line != null) {
            out.append(line).append("\n");
            line = reader.readLine();
        }
        reader.close();
        return out.toString();
    }
}
