//
//  AnimationHelper.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 4 May 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.helper;

import com.google.gwt.dom.client.Element;

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

}
