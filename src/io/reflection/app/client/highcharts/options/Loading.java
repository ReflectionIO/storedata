//
//  Loading.java
//  storedata
//
//  Created by Stefano Capuzzi on 25 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.highcharts.options;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author Stefano Capuzzi
 *
 */
public class Loading extends Option<Loading> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.highcharts.Option#getName()
	 */
	@Override
	public String getName() {
		return "loading";
	}

	public Loading setHideDuration(int hideDuration) {
		return setOption("hideDuration", hideDuration);
	}

	public Loading setLabelStyle(JavaScriptObject labelStyle) {
		return setOption("labelStyle", labelStyle);
	}

	public Loading setShowDuration(int showDuration) {
		return setOption("showDuration", showDuration);
	}

	public Loading setStyle(JavaScriptObject style) {
		return setOption("style", style);
	}

}
