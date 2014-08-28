//
//  KeyValueArchiveManager.java
//  storedata
//
//  Created by William Shakour (billy1380) on 27 Aug 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.archivablekeyvalue.peristence.objectify;

import static io.reflection.app.objectify.PersistenceService.ofy;
import io.reflection.app.archivablekeyvalue.peristence.ValueAppender;

import java.util.HashMap;
import java.util.Map;

import com.googlecode.objectify.VoidWork;

/**
 * @author billy1380
 * 
 */
public class KeyValueArchiveManager {

	private static KeyValueArchiveManager one;

	public static KeyValueArchiveManager get() {
		if (one == null) {
			one = new KeyValueArchiveManager();
		}

		return one;
	}

	private Map<String, ValueAppender<?>> appenders = new HashMap<String, ValueAppender<?>>();
	
	@SuppressWarnings("rawtypes")
	private ValueAppender defaultAppender = new ValueAppender() {

		@Override
		public String getNewValue(String currentValue, Object value) {
			return currentValue + value.toString();
		}
	};

	/**
	 * Gets the slice for a key
	 * 
	 * @param key
	 * @return
	 */
	public ArchivableKeyValue getArchiveKeyValue(String key) {
		return ofy().load().type(ArchivableKeyValue.class).id(key).now();
	}

	public void clear(final String key) {
		ofy().transact(new VoidWork() {
			public void vrun() {
				ArchivableKeyValue akv = ofy().cache(false).load().type(ArchivableKeyValue.class).id(key).now();
				akv.value = "";
				ofy().save().entity(akv);
			}
		});
	}

	public <T> void appendToValue(final String key, final T value) {
		@SuppressWarnings("unchecked")
		final ValueAppender<T> appender = (ValueAppender<T>) appenders.get(value.getClass().getName());

		ofy().transact(new VoidWork() {
			@SuppressWarnings("unchecked")
			public void vrun() {
				ArchivableKeyValue akv = ofy().cache(false).load().type(ArchivableKeyValue.class).id(key).now();
				akv.value = (appender == null ? defaultAppender : appender).getNewValue(akv.value, value);
				ofy().save().entity(akv);
			}
		});
	}

	/**
	 * Define a class that controls how a certain type clazz is appended to the existing value
	 * 
	 * @param clazz
	 * @param appender
	 */
	public <T> void setAppender(Class<T> clazz, ValueAppender<T> appender) {
		if (appender == null) {
			appenders.remove(clazz.getName());
		} else {
			appenders.put(clazz.getName(), appender);
		}
	}
}
