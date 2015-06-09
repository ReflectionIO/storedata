//
//  Lang.java
//  storedata
//
//  Created by Stefano Capuzzi on 16 Feb 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.highcharts;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author Stefano Capuzzi
 *
 */
public class Lang {
	JavaScriptObject property;

	public JavaScriptObject getProperty() {
		if (property == null) {
			property = JavaScriptObject.createObject();
		}
		return property;
	}
	
	

}
