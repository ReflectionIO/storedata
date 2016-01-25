//
//  AnimationHelper.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 4 May 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.helper;

import io.reflection.app.client.part.LoadingIndicator;
import io.reflection.app.client.res.Styles;

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

	public static native void nativeImgErrorPlaceholder(Element img)/*-{
		$wnd.$(img).error(function() {
			$wnd.$(this).hide();
		}).attr("src", "/images/placeholder_app_icon_2x.png");
	}-*/;

	public static String getSorterSvg() {
		return "<svg version=\"1.1\" x=\"0px\" y=\"0px\" viewBox=\"0 0 7 10\" enable-background=\"new 0 0 7 10\" xml:space=\"preserve\" class=\"sort-svg\"><path class=\"ascending\" d=\"M0.4,4.1h6.1c0.1,0,0.2,0,0.3-0.1C7,3.9,7,3.8,7,3.7c0-0.1,0-0.2-0.1-0.3L3.8,0.1C3.7,0,3.6,0,3.5,0C3.4,0,3.3,0,3.2,0.1L0.1,3.3C0,3.4,0,3.5,0,3.7C0,3.8,0,3.9,0.1,4C0.2,4.1,0.3,4.1,0.4,4.1z\"></path><path class=\"descending\" d=\"M6.6,5.9H0.4c-0.1,0-0.2,0-0.3,0.1C0,6.1,0,6.2,0,6.3c0,0.1,0,0.2,0.1,0.3l3.1,3.2C3.3,10,3.4,10,3.5,10c0.1,0,0.2,0,0.3-0.1l3.1-3.2C7,6.6,7,6.5,7,6.3C7,6.2,7,6.1,6.9,6C6.8,5.9,6.7,5.9,6.6,5.9z\"></path></svg>";
	}

	public static SafeHtml getLoaderInlineSafeHTML() {
		return SafeHtmlUtils
				.fromSafeConstant("<svg class=\"loading-ellipsis\" version=\"1.1\" x=\"0px\" y=\"0px\" viewBox=\"0 0 24.2 6.6\" enable-background=\"new 0 0 24.2 6.6\" xml:space=\"preserve\" style=\"opacity: 1;\"><circle class=\"dot-2\" fill=\"#dedee0\" cx=\"12.1\" cy=\"3.4\" r=\"3.2\" style=\"opacity: 1;\"></circle><circle class=\"dot-1\" fill=\"#dedee0\" cx=\"3.2\" cy=\"3.2\" r=\"3.2\" style=\"opacity: 1;\"></circle><circle class=\"dot-3\" fill=\"#dedee0\" cx=\"21\" cy=\"3.2\" r=\"3.2\" style=\"opacity: 1;\"></circle></svg>");
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

	public static LoadingIndicator getMyAppsLoadingIndicator(int rowNumber) {
		LoadingIndicator loadingIndicator = new LoadingIndicator(rowNumber, 5);
		// loadingIndicator.setColStyleName(0, Styles.STYLES_INSTANCE.reflectionMainStyle().rankColumn());
		loadingIndicator.setColStyleName(0, Styles.STYLES_INSTANCE.reflectionMainStyle().appDetailsColumn());
		loadingIndicator.setColStyleName(1, Styles.STYLES_INSTANCE.reflectionMainStyle().priceColumn() + " "
				+ Styles.STYLES_INSTANCE.reflectionMainStyle().columnHiddenMobile());
		loadingIndicator.setColStyleName(2, Styles.STYLES_INSTANCE.reflectionMainStyle().downloadsColumn());
		loadingIndicator.setColStyleName(3, Styles.STYLES_INSTANCE.reflectionMainStyle().revenueColumn());
		loadingIndicator.setColStyleName(4, Styles.STYLES_INSTANCE.reflectionMainStyle().iapColumn() + " "
				+ Styles.STYLES_INSTANCE.reflectionMainStyle().columnHiddenMobile());

		loadingIndicator.setColumnCellsStyleName(1, Styles.STYLES_INSTANCE.reflectionMainStyle().columnHiddenMobile());
		loadingIndicator.setColumnCellsStyleName(4, Styles.STYLES_INSTANCE.reflectionMainStyle().columnHiddenMobile());

		// loadingIndicator.setColumnCellsSafeHtml(0, getLoaderInlineSafeHTML());
		loadingIndicator.setColumnCellsSafeHtml(
				0,
				SafeHtmlUtils.fromSafeConstant("<img class=\"img-rounded\" src=\"images/leaderboard-app-icon-placeholder.png\"> "
						+ getLoaderInlineSafeHTML().asString()));
		loadingIndicator.setColumnCellsSafeHtml(1, getLoaderInlineSafeHTML());
		loadingIndicator.setColumnCellsSafeHtml(2, getLoaderInlineSafeHTML());
		loadingIndicator.setColumnCellsSafeHtml(3, getLoaderInlineSafeHTML());
		loadingIndicator.setColumnCellsSafeHtml(4, getLoaderInlineSafeHTML());

		return loadingIndicator;
	}

	public static LoadingIndicator getLeaderboardAllLoadingIndicator(int rowNumber) {
		LoadingIndicator loadingIndicator = new LoadingIndicator(rowNumber, 4);
		loadingIndicator.setColStyle(0, "width: 10%;");
		loadingIndicator.setColStyle(1, "width: 30%;");
		loadingIndicator.setColStyle(2, "width: 30%;");
		loadingIndicator.setColStyle(3, "width: 30%;");

		loadingIndicator.setColumnCellsSafeHtml(0, getLoaderInlineSafeHTML());
		loadingIndicator.setColumnCellsSafeHtml(
				1,
				SafeHtmlUtils.fromSafeConstant("<img class=\"img-rounded\" src=\"images/leaderboard-app-icon-placeholder.png\"> "
						+ getLoaderInlineSafeHTML().asString()));
		loadingIndicator.setColumnCellsSafeHtml(
				2,
				SafeHtmlUtils.fromSafeConstant("<img class=\"img-rounded\" src=\"images/leaderboard-app-icon-placeholder.png\"> "
						+ getLoaderInlineSafeHTML().asString()));
		loadingIndicator.setColumnCellsSafeHtml(
				3,
				SafeHtmlUtils.fromSafeConstant("<img class=\"img-rounded\" src=\"images/leaderboard-app-icon-placeholder.png\"> "
						+ getLoaderInlineSafeHTML().asString()));

		return loadingIndicator;
	}

	public static LoadingIndicator getLeaderboardListLoadingIndicator(int rowNumber, boolean isFree) {
		LoadingIndicator loadingIndicator = new LoadingIndicator(rowNumber, 5);
		loadingIndicator.setColStyle(0, "width: 10%;");
		loadingIndicator.setColStyle(1, "width: 42%;");
		loadingIndicator.setColStyle(2, "width: 19%;");
		loadingIndicator.setColStyle(3, "width: 19%;");
		loadingIndicator.setColStyle(4, "width: 10%;");
		loadingIndicator.setColStyleName((isFree ? 2 : 4), Styles.STYLES_INSTANCE.reflectionMainStyle().columnHiddenMobile());

		loadingIndicator.setColumnCellsStyleName((isFree ? 2 : 4), Styles.STYLES_INSTANCE.reflectionMainStyle().columnHiddenMobile());

		loadingIndicator.setColumnCellsSafeHtml(0, getLoaderInlineSafeHTML());
		loadingIndicator.setColumnCellsSafeHtml(
				1,
				SafeHtmlUtils.fromSafeConstant("<img class=\"img-rounded\" src=\"images/leaderboard-app-icon-placeholder.png\"> "
						+ getLoaderInlineSafeHTML().asString()));
		loadingIndicator.setColumnCellsSafeHtml(2, getLoaderInlineSafeHTML());
		loadingIndicator.setColumnCellsSafeHtml(3, getLoaderInlineSafeHTML());
		loadingIndicator.setColumnCellsSafeHtml(4, getLoaderInlineSafeHTML());

		return loadingIndicator;
	}

	public static LoadingIndicator getAppRevenueLoadingIndicator(int rowNumber) {
		LoadingIndicator loadingIndicator = new LoadingIndicator(rowNumber, 3);
		loadingIndicator.setColStyle(0, "width: 25%;");
		loadingIndicator.setColStyle(1, "width: 25%;");
		loadingIndicator.setColStyle(2, "width: 50%;");

		loadingIndicator.setColStyleName(0, Styles.STYLES_INSTANCE.reflectionMainStyle().dateValue());
		loadingIndicator.setColStyleName(1, Styles.STYLES_INSTANCE.reflectionMainStyle().revenueValue());
		loadingIndicator.setColStyleName(2, Styles.STYLES_INSTANCE.reflectionMainStyle().revenuePercentage());

		loadingIndicator.setColumnCellsSafeHtml(0, getLoaderInlineSafeHTML());
		loadingIndicator.setColumnCellsSafeHtml(1, getLoaderInlineSafeHTML());
		loadingIndicator.setColumnCellsSafeHtml(2, getLoaderInlineSafeHTML());

		return loadingIndicator;
	}

	public static LoadingIndicator getLinkedAccountsIndicator(int rowNumber) {
		LoadingIndicator loadingIndicator = new LoadingIndicator(rowNumber, 5);

		loadingIndicator.setColumnCellsSafeHtml(0, getLoaderInlineSafeHTML());
		loadingIndicator.setColumnCellsSafeHtml(1, getLoaderInlineSafeHTML());
		loadingIndicator.setColumnCellsSafeHtml(2, getLoaderInlineSafeHTML());
		loadingIndicator.setColumnCellsSafeHtml(3, getLoaderInlineSafeHTML());
		loadingIndicator.setColumnCellsSafeHtml(4, getLoaderInlineSafeHTML());

		return loadingIndicator;
	}

}
