//
//  DOMHelper.java
//  storedata
//
//  Created by Stefano Capuzzi on 12 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.helper;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LinkElement;
import com.google.gwt.dom.client.ScriptElement;

/**
 * @author Stefano Capuzzi
 *
 */
public class DOMHelper {

	/**
	 * Attach element to head
	 */
	public static native void appendToHead(JavaScriptObject object) /*-{
		$doc.getElementsByTagName("head")[0].appendChild(object);
	}-*/;

	/**
	 * Detach element to head
	 */
	public static native void removeFromHead(JavaScriptObject object) /*-{
		$doc.getElementsByTagName("head")[0].removeChild(object);
	}-*/;

	/**
	 * Attach element to body
	 */
	public static native void appendToBody(JavaScriptObject object) /*-{
		$doc.getElementsByTagName("body")[0].appendChild(object);
	}-*/;

	/**
	 * Detach element to body
	 */
	public static native void removeFromBody(JavaScriptObject object) /*-{
		$doc.getElementsByTagName("body")[0].removeChild(object);
	}-*/;

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

}
