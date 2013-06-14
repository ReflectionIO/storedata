//
//  StringUtils.java
//  JsonSpacecloud
//
//  Created by William Shakour on Jun 14, 2012.
//  Copyright © 2012 Spacehopper Studios Ltd. All rights reserved.
//
package com.spacehopperstudios.utility;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author William Shakour
 * 
 */
public class StringUtils {

	private static final String[] ESCAPE_CHARS = { " ", "{", "}", ";", "/", "?", ":", "@", "&", "=", "+", "$", ",", "[", "]", "#", "!", "'", "(", ")", "*",
			"\"", "<", ">" };

	private static final String[] REPLACE_CHARS = { "%20", "%7B", "%7D", "%3B", "%2F", "%3F", "%3A", "%40", "%26", "%3D", "%2B", "%24", "%2C", "%5B", "%5D",
			"%23", "%21", "%27", "%28", "%29", "%2A", "%22", "%3C", "%3E" };

	public static String sanitise(String value) {
		return value;
		// value.replace("<", "").replace(">", "");
	}

	public static String stripslashes(String value) {
		return value.replace("\\", "");
	}

	public static String addslashes(String value) {
		return value;
	}

	public static String urldecode(String value) {
		StringBuffer replaced = new StringBuffer(value);

		int i, start;
		for (i = 0; i < REPLACE_CHARS.length; i++) {
			while ((start = replaced.indexOf(REPLACE_CHARS[i])) >= 0) {
				replaced.replace(start, start + REPLACE_CHARS[i].length(), ESCAPE_CHARS[i]);
			}
		}

		return replaced.toString();
	}

	public static String urlencode(String value) {
		StringBuffer replaced = new StringBuffer(value);

		int i, start;
		for (i = 0; i < ESCAPE_CHARS.length; i++) {
			while ((start = replaced.indexOf(ESCAPE_CHARS[i])) >= 0) {
				replaced.replace(start, start + ESCAPE_CHARS[i].length(), REPLACE_CHARS[i]);
			}
		}

		return replaced.toString();
	}

	public static String sha1Hash(String content) {
		byte[] bytesOfMessage = null;
		byte[] theDigest = null;

		try {
			bytesOfMessage = content.getBytes("UTF-8");

			MessageDigest md = MessageDigest.getInstance("SHA-1");
			theDigest = md.digest(bytesOfMessage);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return theDigest == null ? null : convertDigestToString(theDigest);
	}

	private static String convertDigestToString(byte[] digest) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < digest.length; i++) {
			result.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
		}

		return result.toString();
	}
}
