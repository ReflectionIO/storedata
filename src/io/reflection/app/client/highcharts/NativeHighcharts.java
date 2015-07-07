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
	public static native JavaScriptObject nativeChart(JavaScriptObject options)/*-{
		return new $wnd.Highcharts.Chart(options);
	}-*/;

	public static native String nativeDateFormat(String format, double millisec, boolean capitalize)/*-{
		return $wnd.Highcharts.dateFormat(format, millisec, capitalize);
	}-*/;

	public static native String nativeNumberFormat(double number, int decimals, String decimalPoint, String thousandsSeparator)/*-{
		return $wnd.Highcharts.numberFormat(number, decimals, decimalPoint,
				thousandsSeparator);
	}-*/;

	/**
	 * Set global options before graph initialization
	 * 
	 * @param options
	 */
	public static native void nativeSetOptions(JavaScriptObject options)/*-{
		$wnd.Highcharts.setOptions(options);
	}-*/;

	public static native void nativeSetUTC(JavaScriptObject options, boolean isUTC)/*-{
		$wnd.Highcharts.setOptions({
			global : {
				useUTC : isUTC
			}
		});
	}-*/;

}
