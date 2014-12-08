//
//  Page.java
//  storedata
//
//  Created by William Shakour (billy1380) on 11 Feb 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;

/**
 * @author billy1380
 * 
 */
public class Page extends Composite {

	private List<HandlerRegistration> handlers = new ArrayList<HandlerRegistration>();
	private PageType pageType = null;

	protected void register(HandlerRegistration registration) {
		handlers.add(registration);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		Document.get().setTitle(getTitle());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onDetach()
	 */
	@Override
	protected void onDetach() {
		super.onDetach();

		for (HandlerRegistration handler : handlers) {
			handler.removeHandler();
		}
	}

	public String getTitle() {
		return "Reflection.io";
	}

	protected PageType getPageType() {
		return pageType;
	}

	public void setPageType(PageType type) {
		if (pageType == null) {
			pageType = type;
		} else throw new RuntimeException("Page type is alread " + pageType.toString());
	}
}
