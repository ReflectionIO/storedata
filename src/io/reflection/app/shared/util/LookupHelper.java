//
//  LookupHelper.java
//  storedata
//
//  Created by William Shakour (billy1380) on 19 May 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.shared.util;

import io.reflection.app.datatypes.shared.DataType;
import io.reflection.app.datatypes.shared.Forum;
import io.reflection.app.datatypes.shared.Post;
import io.reflection.app.datatypes.shared.Topic;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author billy1380
 * 
 */
public class LookupHelper {

	public static <T extends DataType> Map<Long, T> makeLookup(Collection<T> collection) {
		Map<Long, T> lookup = new HashMap<Long, T>();

		for (T t : collection) {
			lookup.put(t.id, t);
		}

		return lookup;
	}

	private static final String ALLOWED_CHARS = "abcdefghijklmnopqrstuvwxyz_";
	private static final char REPLACEMENT_CHAR = '_';
	private static final int MAX_CODE_LENGTH = 45;

	public static String codify(String string) {
		StringBuffer codified = new StringBuffer();

		if (string != null && string.length() > 0) {
			string = string.toLowerCase();

			int size = Math.min(string.length(), MAX_CODE_LENGTH);
			char c;
			for (int i = 0; i < size; i++) {
				c = string.charAt(i);

				if (ALLOWED_CHARS.contains(Character.toString(c))) {
					codified.append(c);
				} else {
					codified.append(REPLACEMENT_CHAR);
				}
			}
		}

		return codified.toString();
	}

	public static String reference(Post object) {
		String lookup = object.code;

		if (object.id != null && (lookup == null || lookup.length() == 0)) {
			lookup = object.id.toString();
		}

		return lookup == null || lookup.length() == 0 ? codify(object.title) : lookup;
	}

	public static String reference(Forum object) {
		String lookup = object.code;

		if (object.id != null && (lookup == null || lookup.length() == 0)) {
			lookup = object.id.toString();
		}

		return lookup == null || lookup.length() == 0 ? codify(object.title) : lookup;
	}

	public static String reference(Topic object) {
		String lookup = object.code;

		if (object.id != null && (lookup == null || lookup.length() == 0)) {
			lookup = object.id.toString();
		}

		return lookup == null || lookup.length() == 0 ? codify(object.title) : lookup;
	}
}
