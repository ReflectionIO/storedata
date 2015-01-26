//
//  DOMHelper.java
//  storedata
//
//  Created by Stefano Capuzzi on 12 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.helper;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LinkElement;
import com.google.gwt.dom.client.ScriptElement;

/**
 * @author Stefano Capuzzi
 *
 */
public class DOMHelper {

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

}
