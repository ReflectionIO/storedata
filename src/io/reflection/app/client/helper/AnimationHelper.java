//
//  AnimationHelper.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 4 May 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.helper;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

/**
 * @author Stefano Capuzzi (capuzzistefano)
 *
 */
public class AnimationHelper {

	public static native void nativeScrollTop(int position, int duration, String easing)/*-{
		$wnd.$('html, body').animate({
			scrollTop : position
		}, duration, easing);
	}-*/;

	public static native void nativeScrollTop(int position, String duration, String easing)/*-{
		$wnd.$('html, body').animate({
			scrollTop : position
		}, duration, easing);
	}-*/;

	public static native void nativeHide(Element elem)/*-{
		$wnd.$(elem).hide();
	}-*/;

	public static native void nativeSlideToggle(Element elem, int duration)/*-{
		$wnd.$(elem).slideToggle(duration);
	}-*/;

	public static native void nativeFadeIn(Element elem, int duration)/*-{
		$wnd.$(elem).fadeIn(duration);
	}-*/;

	public static native void nativeFadeOut(Element elem, int duration)/*-{
		$wnd.$(elem).fadeOut(duration);
	}-*/;

	public static SafeHtml getLoaderInlineSafeHTML() {
		return SafeHtmlUtils
				.fromSafeConstant("<svg class=\"loading-ellipsis\" version=\"1.1\" x=\"0px\" y=\"0px\" viewBox=\"0 0 24.2 6.6\" enable-background=\"new 0 0 24.2 6.6\" xml:space=\"preserve\"><circle class=\"dot-2\" fill=\"#E7E7EA\" cx=\"12.1\" cy=\"3.4\" r=\"3.2\"></circle><circle class=\"dot-1\" fill=\"#E7E7EA\" cx=\"3.2\" cy=\"3.2\" r=\"3.2\"></circle><circle class=\"dot-3\" fill=\"#E7E7EA\" cx=\"21\" cy=\"3.2\" r=\"3.2\"></circle></svg>");
	}

	public static Element getLoaderInlineElement() {
		Element loaderElem = Document.get().createElement("svg");
		loaderElem.addClassName("loading-ellipsis");
		loaderElem.setAttribute("version", "1.1");
		loaderElem.setAttribute("x", "0px");
		loaderElem.setAttribute("y", "0px");
		loaderElem.setAttribute("viewBox", "0 0 24.2 6.6");
		loaderElem.setAttribute("enable-background", "new 0 0 24.2 6.6");
		loaderElem.setAttribute("xml:space", "preserve");
		loaderElem.setInnerSafeHtml(getLoaderInlineSafeHTML());
		return loaderElem;
	}

}
