//
//  DOMHelper.java
//  storedata
//
//  Created by Stefano Capuzzi on 12 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.helper;

import io.reflection.app.client.res.Styles;
import io.reflection.app.client.res.Styles.ReflectionMainStyles;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LinkElement;
import com.google.gwt.dom.client.ScriptElement;

/**
 * @author Stefano Capuzzi
 *
 */
public class DOMHelper {

	public static ReflectionMainStyles style = Styles.STYLES_INSTANCE.reflectionMainStyle();

	/** Load CSS file from url */
	public static LinkElement getCssLinkFromUrl(String url) {
		LinkElement link = Document.get().createLinkElement();
		link.setRel("stylesheet");
		link.setType("text/css");
		link.setMedia("screen, projection");
		link.setHref(url);
		return link;
	}

	/** Load CSS file from url */
	public static ScriptElement getJSScriptFromUrl(String url) {
		ScriptElement script = Document.get().createScriptElement();
		script.setType("text/javascript");
		script.setSrc(url);
		return script;
	}

	/** Load CSS file from url */
	public static ScriptElement getJSScriptFromUrl(String url, String attribute) {
		ScriptElement script = Document.get().createScriptElement();
		script.setType("text/javascript");
		script.setSrc(url);
		script.setAttribute(attribute, "");
		return script;
	}

	public static Element getHtmlElement() {
		return Document.get().getElementsByTagName("html").getItem(0);
	}

	public static void toggleClassName(Element element, String className) {
		if (!element.hasClassName(className)) {
			element.addClassName(className);
		} else {
			element.removeClassName(className);
		}
	}

	public static void addClassName(Element element, String className) {
		if (!element.hasClassName(className)) {
			element.addClassName(className);
		}
	}

	public static void setScrollEnabled(boolean enabled) {
		if (enabled) {
			DOMHelper.getHtmlElement().removeClassName(style.noScroll());
			Document.get().getBody().removeClassName(style.noScroll());
		} else {
			addClassName(DOMHelper.getHtmlElement(), style.noScroll());
			addClassName(Document.get().getBody(), style.noScroll());
		}
	}

}
