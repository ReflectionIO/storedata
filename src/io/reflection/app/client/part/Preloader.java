//
//  Preloader.java
//  storedata
//
//  Created by William Shakour (stefanocapuzzi) on 18 Jun 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part;

import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author stefanocapuzzi
 * 
 */
public class Preloader extends Composite implements HasWidgets {

	private static PreloaderUiBinder uiBinder = GWT.create(PreloaderUiBinder.class);

	interface PreloaderUiBinder extends UiBinder<Widget, Preloader> {}

	@UiField HTMLPanel wrapperPanel;
	@UiField HTMLPanel content;

	interface PreloaderStyle extends CssResource {
		String opaque();

		String transparent();
	}

	@UiField PreloaderStyle style;

	public Preloader() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void show() {
		show(Boolean.FALSE);
	}

	public void show(boolean transparent) {
		if (transparent) {
			content.addStyleName(style.transparent());
		} else {
			content.addStyleName(style.opaque());
		}
		content.setVisible(Boolean.TRUE);
	}

	public void hide() {
		content.setVisible(Boolean.FALSE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.HasWidgets#add(com.google.gwt.user.client.ui.Widget)
	 */
	@Override
	public void add(Widget w) {
		wrapperPanel.add(w);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.HasWidgets#clear()
	 */
	@Override
	public void clear() {
		wrapperPanel.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.HasWidgets#iterator()
	 */
	@Override
	public Iterator<Widget> iterator() {
		return wrapperPanel.iterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.HasWidgets#remove(com.google.gwt.user.client.ui.Widget)
	 */
	@Override
	public boolean remove(Widget w) {
		return wrapperPanel.remove(w);
	}

}
