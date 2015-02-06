//
//  SubSubtitle.java
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
public class Subtitle extends Option<Subtitle> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.highcharts.Option#getName()
	 */
	@Override
	public String getName() {
		return "subSubtitle";
	}

	public Subtitle setAlign(String alignment) {
		return setOption("align", alignment);
	}

	public Subtitle setFloating(boolean floating) {
		return setOption("floating", floating);
	}

	public Subtitle setStyle(JavaScriptObject style) {
		return setOption("style", style);
	}

	public Subtitle setText(String text) {
		return setOption("text", text);
	}

	public Subtitle setUseHTML(boolean useHTML) {
		return setOption("useHTML", useHTML);
	}

	public Subtitle setVerticalALign(String verticalAlign) {
		return setOption("verticalAlign", verticalAlign);
	}

	public Subtitle setX(int xPosition) {
		return setOption("x", xPosition);
	}

	public Subtitle setY(int yPosition) {
		return setOption("y", yPosition);
	}

}
