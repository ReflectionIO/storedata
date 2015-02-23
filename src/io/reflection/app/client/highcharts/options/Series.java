//
//  Series.java
//  storedata
//
//  Created by Stefano Capuzzi on 31 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.highcharts.options;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

/**
 * @author Stefano Capuzzi
 *
 */
public class Series extends Option<Series> {

	JsArray<JavaScriptObject> seriesArray;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.highcharts.options.Option#getName()
	 */
	@Override
	public String getName() {
		return "series";
	}

}
