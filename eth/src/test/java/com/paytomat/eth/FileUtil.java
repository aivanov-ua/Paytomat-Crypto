package com.paytomat.eth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * created by Alex Ivanov on 3/25/19.
 */
class FileUtil {

    static String readFile(Class clazz, String fileName) throws IOException {
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
