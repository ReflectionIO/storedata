//
//  NativeHighcharts.java
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
public class NativeHighcharts {

	/**
	 * Highcharts constructor
	 * 
	 * @param options
	 * @return
	 */
	static native JavaScriptObject nativeChart(JavaScriptObject options)/*-{
		return new $wnd.Highcharts.Chart(options);
	}-*/;

	/**
	 * Set global options before graph initialization
	 * 
	 * @param options
	 */
	static native void nativeSetOptions(JavaScriptObject options)/*-{
		$wnd.Highcharts.setOptions(options);
	}-*/;

}
