package com.paytomat.nem.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Static class that contains string utility functions.
 */
public class StringUtils {

	public static List<String> split(final String src, final int partSize) {
		List<String> parts = new ArrayList<>();
		int len = src.length();
		for (int i = 0; i < len; i += partSize) {
			parts.add(src.substring(i, Math.min(len, i + partSize)));
		}
		return parts;
	}
}
