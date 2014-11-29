//
//  ValueAppender.java
//  storedata
//
//  Created by William Shakour (billy1380) on 28 Aug 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.archivablekeyvalue.peristence;

import java.util.Map;

/**
 * @author William Shakour (billy1380)
 * 
 */
public interface ValueAppender<T> {
	String getNewValue(String key, String currentValue, T value, Map<String, Object> other);
}
