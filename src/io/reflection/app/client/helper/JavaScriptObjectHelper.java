//
//  JavaScriptObjectHelper.java
//  storedata
//
//  Created by William Shakour (billy1380) on 28 Nov 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.helper;

import java.sql.Date;

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
	
}
