//
//  Option.java
//  storedata
//
//  Created by Stefano Capuzzi on 18 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.highcharts.options;

import io.reflection.app.client.helper.JavaScriptObjectHelper;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author Stefano Capuzzi
 *
 */
public abstract class Option<T> {

	private JavaScriptObject option = JavaScriptObject.createObject();

	@SuppressWarnings("unchecked")
	public T setOption(String name, String property) {
		JavaScriptObjectHelper.setStringProperty(option, name, property);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setOption(String name, int property) {
		JavaScriptObjectHelper.setIntegerProperty(option, name, property);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setOption(String name, double property) {
		JavaScriptObjectHelper.setDoubleProperty(option, name, property);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setOption(String name, boolean property) {
		JavaScriptObjectHelper.setBooleanProperty(option, name, property);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setOption(String name, JavaScriptObject property) {
		JavaScriptObjectHelper.setObjectProperty(option, name, property);
		return (T) this;
	}

	public abstract String getName();

	public JavaScriptObject getProperty() {
		return option;
	}
}
