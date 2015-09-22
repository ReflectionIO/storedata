//
//  ConfirmationDialog.java
//  storedata
//
//  Created by Stefano Capuzzi on 25 May 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.component;

import io.reflection.app.client.helper.DOMHelper;
import io.reflection.app.client.res.Styles;

import com.google.gwt.user.client.ui.PopupPanel;

/**
 * @author stefanocapuzzi
 * 
 */
public class PopupDialog extends PopupPanel {

	Long parameter = null;

	public PopupDialog() {
		super(false, true);

		setGlassEnabled(true);
		getGlassElement().getStyle().setBackgroundColor("rgba(41, 41, 43, 0.7)");
		getGlassElement().getStyle().setZIndex(10000);
		getElement().getStyle().setZIndex(10001);

	}

	public void setParameter(Long typeParameter) {
		parameter = typeParameter;
	}

	public Long getParameter() {
		return parameter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.PopupPanel#center()
	 */
	@Override
	public void center() {
		super.center();

		DOMHelper.setScrollEnabled(false);

		getElement().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isShowing());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.PopupPanel#hide()
	 */
	@Override
	public void hide() {
		getElement().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isShowing());

		super.hide();
		parameter = null;
		DOMHelper.setScrollEnabled(true);
	}

}
