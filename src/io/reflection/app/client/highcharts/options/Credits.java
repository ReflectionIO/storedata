//
//  Credits.java
//  storedata
//
//  Created by Stefano Capuzzi on 24 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.highcharts.options;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author Stefano Capuzzi
 *
 */
public class Credits extends Option<Credits> {

	public Credits setEnabled(boolean enabled) {
		return setOption("enabled", enabled);
	}

	public Credits setHref(String href) {
		return setOption("href", href);
	}

	public Credits setPosition(JavaScriptObject position) {
		return setOption("position", position);
	}

	public Credits setStyle(JavaScriptObject style) {
		return setOption("style", style);
	}

	public Credits setText(String text) {
		return setOption("text", text);
	}

}
