//
//  Legend.java
//  storedata
//
//  Created by Stefano Capuzzi on 25 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.highcharts.options;

import io.reflection.app.client.helper.JavaScriptObjectHelper;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author Stefano Capuzzi
 *
 */
public class Legend extends Option<Legend> {

	private JavaScriptObject navigation;
	private JavaScriptObject title;

	public Legend setAlign(String alignment) {
		return setOption("align", alignment);
	}

	public Legend setBackgroundColor(String rgb) {
		return setOption("backgroundColor", rgb);
	}

	public Legend setBorderColor(String rgb) {
		return setOption("borderColor", rgb);
	}

	public Legend setBorderRadius(int radius) {
		return setOption("borderRadius", radius);
	}

	public Legend setBorderWidth(int width) {
		return setOption("borderWidth", width);
	}

	public Legend setEnabled(boolean enabled) {
		return setOption("enabled", enabled);
	}

	public Legend setFloating(boolean floating) {
		return setOption("floating", floating);
	}

	public Legend setItemDistance(int itemDistance) {
		return setOption("itemDistance", itemDistance);
	}

	public Legend setItemHiddenStyle(JavaScriptObject style) {
		return setOption("itemHiddenStyle", style);
	}

	public Legend setHoverHiddenStyle(JavaScriptObject style) {
		return setOption("itemHoverStyle", style);
	}

	public Legend setItemMarginBottom(int itemMarginBottom) {
		return setOption("itemMarginBottom", itemMarginBottom);
	}

	public Legend setItemMarginTop(int itemMarginTop) {
		return setOption("itemMarginTop", itemMarginTop);
	}

	public Legend setItemStyle(JavaScriptObject style) {
		return setOption("itemStyle", style);
	}

	public Legend setItemWidth(int itemWidth) {
		return setOption("itemWidth", itemWidth);
	}

	public Legend setLabelFormat(String labelFormat) {
		return setOption("labelFormat", labelFormat);
	}

	public Legend setLabelFormatter(JavaScriptObject formatterFunction) {
		return setOption("labelFormatter", formatterFunction);
	}

	public Legend setLayout(String layout) {
		return setOption("layout", layout);
	}

	@Deprecated
	public Legend setLineHeight(int lineHeight) {
		return setOption("lineHeight", lineHeight);
	}

	public Legend setMargin(int margin) {
		return setOption("margin", margin);
	}

	public Legend setMaxHeight(int maxHeight) {
		return setOption("maxHeight", maxHeight);
	}

	public Legend setNavigationActiveColor(String color) {
		if (navigation == null) {
			navigation = JavaScriptObject.createObject();
			setOption("navigation", navigation);
		}
		JavaScriptObjectHelper.setStringProperty(navigation, "activeColor", color);
		return this;
	}

	public Legend setNavigationAnimation(boolean animate) {
		if (navigation == null) {
			navigation = JavaScriptObject.createObject();
			setOption("navigation", navigation);
		}
		JavaScriptObjectHelper.setBooleanProperty(navigation, "animation", animate);
		return this;
	}

	public Legend setNavigationAnimation(JavaScriptObject animation) {
		if (navigation == null) {
			navigation = JavaScriptObject.createObject();
			setOption("navigation", navigation);
		}
		JavaScriptObjectHelper.setObjectProperty(navigation, "animation", animation);
		return this;
	}

	public Legend setNavigationArrowSize(int arrowSize) {
		if (navigation == null) {
			navigation = JavaScriptObject.createObject();
			setOption("navigation", navigation);
		}
		JavaScriptObjectHelper.setIntegerProperty(navigation, "arrowSize", arrowSize);
		return this;
	}

	public Legend setNavigationInactiveColor(String color) {
		if (navigation == null) {
			navigation = JavaScriptObject.createObject();
			setOption("navigation", navigation);
		}
		JavaScriptObjectHelper.setStringProperty(navigation, "inactiveColor", color);
		return this;
	}

	public Legend setNavigationStyle(JavaScriptObject style) {
		if (navigation == null) {
			navigation = JavaScriptObject.createObject();
			setOption("navigation", navigation);
		}
		JavaScriptObjectHelper.setObjectProperty(navigation, "style", style);
		return this;
	}

	public Legend setPadding(int padding) {
		return setOption("padding", padding);
	}

	public Legend setReversed(boolean reversed) {
		return setOption("reversed", reversed);
	}

	public Legend setRtl(boolean rtl) {
		return setOption("rtl", rtl);
	}

	public Legend setShadow(boolean shadow) {
		return setOption("shadow", shadow);
	}

	public Legend setShadow(JavaScriptObject shadow) {
		return setOption("shadow", shadow);
	}

	@Deprecated
	public Legend setStyle(JavaScriptObject style) {
		return setOption("style", style);
	}

	public Legend setSymbolHeight(int symbolHeight) {
		return setOption("symbolHeight", symbolHeight);
	}

	public Legend setSymbolPadding(int symbolPadding) {
		return setOption("symbolPadding", symbolPadding);
	}

	public Legend setSymbolRadius(int symbolRadius) {
		return setOption("symbolRadius", symbolRadius);
	}

	public Legend setSymbolWidth(int symbolWidth) {
		return setOption("symbolWidth", symbolWidth);
	}

	public Legend setTitleStyle(JavaScriptObject style) {
		if (title == null) {
			title = JavaScriptObject.createObject();
			setOption("title", title);
		}
		JavaScriptObjectHelper.setObjectProperty(title, "style", style);
		return this;
	}

	public Legend setTitleText(String text) {
		if (title == null) {
			title = JavaScriptObject.createObject();
			setOption("title", title);
		}
		JavaScriptObjectHelper.setStringProperty(title, "text", text);
		return this;
	}

	public Legend setUseHTML(boolean useHTML) {
		return setOption("useHTML", useHTML);
	}

	public Legend setVerticalALign(String verticalAlign) {
		return setOption("verticalAlign", verticalAlign);
	}

	public Legend setWidth(int width) {
		return setOption("width", width);
	}

	public Legend setX(int xPosition) {
		return setOption("x", xPosition);
	}

	public Legend setY(int yPosition) {
		return setOption("y", yPosition);
	}
}
