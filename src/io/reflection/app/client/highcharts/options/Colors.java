//
//  Colors.java
//  storedata
//
//  Created by Stefano Capuzzi on 25 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.highcharts.options;

import com.google.gwt.core.client.JsArrayString;

/**
 * @author Stefano Capuzzi
 *
 */
public class Colors extends Option<Colors> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.highcharts.Option#getName()
	 */
	@Override
	public String getName() {
		return "colors";
	}

	public Colors setColors(JsArrayString colors) {
		return setOption("colors", colors);
	}

}
