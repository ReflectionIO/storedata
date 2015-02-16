//
//  JavaScriptObjectHelper.java
//  storedata
//
//  Created by William Shakour (billy1380) on 28 Nov 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.helper;

import java.sql.Date;
import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsDate;

/**
 * @author William Shakour (billy1380)
 *
 */
public class JavaScriptObjectHelper {

	public static void setDateProperty(JavaScriptObject object, String propertyName, Date value) {
		setDateProperty(object, propertyName, JsDate.create(value.getTime()));
	}

	private static native void setDateProperty(JavaScriptObject object, String propertyName, JsDate value) /*-{
		object[propertyName] = value;
	}-*/;

	public static native void setStringProperty(JavaScriptObject object, String propertyName, String value) /*-{
		object[propertyName] = value;
	}-*/;

	public static native void setBooleanProperty(JavaScriptObject object, String propertyName, boolean value) /*-{
		object[propertyName] = value;
	}-*/;

	/**
	 * @param o
	 * @param properties
	 */
	@SuppressWarnings("unchecked")
	public static void copyToJavaScriptObject(Map<String, ?> map, JavaScriptObject object) {
		Object value;
		for (String key : map.keySet()) {
			value = map.get(key);
			if (value instanceof Map) {
				JavaScriptObject oo = JavaScriptObject.createObject();
				copyToJavaScriptObject((Map<String, ?>) value, oo);
				setObjectProperty(object, key, oo);
			} else if (value instanceof Boolean) {
				setBooleanProperty(object, key, ((Boolean) value).booleanValue());
			} else if (value instanceof String) {
				setStringProperty(object, key, (String) value);
			} else if (value instanceof Date) {
				setDateProperty(object, key, (Date) value);
			}

			// TODO: need numbers
		}
	}

	private static native void setObjectProperty(JavaScriptObject object, String propertyName, JavaScriptObject value) /*-{
		object[propertyName] = value;
	}-*/;

}
