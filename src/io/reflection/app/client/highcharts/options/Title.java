//
//  Title.java
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
public class Title extends Option<Title> {

	public Title setAlign(String alignment) {
		return setOption("align", alignment);
	}

	public Title setFloating(boolean floating) {
		return setOption("floating", floating);
	}

	public Title setMargin(int margin) {
		return setOption("margin", margin);
	}

	public Title setStyle(JavaScriptObject style) {
		return setOption("style", style);
	}

	public Title setText(String text) {
		return setOption("text", text);
	}

	public Title setUseHTML(boolean useHTML) {
		return setOption("useHTML", useHTML);
	}

	public Title setVerticalALign(String verticalAlign) {
		return setOption("verticalAlign", verticalAlign);
	}

	public Title setX(int xPosition) {
		return setOption("x", xPosition);
	}

	public Title setY(int yPosition) {
		return setOption("y", yPosition);
	}

}
