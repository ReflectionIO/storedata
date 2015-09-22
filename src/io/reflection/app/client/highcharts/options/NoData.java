//
//  NoData.java
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
public class NoData extends Option<NoData> {

	public NoData setAttr(JavaScriptObject attr) {
		return setOption("attr", attr);
	}

	public NoData setPosition(JavaScriptObject position) {
		return setOption("position", position);
	}

	public NoData setStyle(JavaScriptObject style) {
		return setOption("style", style);
	}

}
