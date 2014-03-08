//
//  NavigationController.java
//  storedata
//
//  Created by William Shakour (billy1380) on 3 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.controller;

import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.page.Page;
import io.reflection.app.client.page.PageType;
import io.reflection.app.client.part.Footer;
import io.reflection.app.client.part.Header;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class NavigationController implements ValueChangeHandler<String> {
	private static NavigationController mOne = null;

	private HTMLPanel mPanel = null;

	private Map<String, Page> pages = new HashMap<String, Page>();

	private Header mHeader = null;
	private Footer mFooter = null;

	private Stack mStack;

	public static class Stack {
		private String[] mParts;

		private Stack(String value) {
			mParts = value.split("/");
		}

		public String getPage() {
			return mParts.length > 0 ? mParts[0] : null;
		}

		public String getAction() {
			return mParts.length > 1 ? mParts[1] : null;
		}

		public String getParameter(int index) {
			return mParts.length > (2 + index) ? mParts[2 + index] : null;
		}

		public static Stack parse(String value) {
			return new Stack(value);
		}

		/**
		 * @return
		 */
		public boolean hasAction() {
			return getAction() != null;
		}

		public boolean hasPage() {
			return getPage() != null;
		}
	}

	public static NavigationController get() {
		if (mOne == null) {
			mOne = new NavigationController();
		}

		return mOne;
	}

	/**
	 * @return
	 */
	public Widget getPageHolderPanel() {

		if (mPanel == null) {
			mPanel = new HTMLPanel("<!-- pages go here -->");
			mPanel.setStyleName("container-fluid");
			mPanel.getElement().setAttribute("style", "padding: 70px 0px 40px 0px;");
		}

		return mPanel;
	}

	private void attachPage(PageType type) {
		Page page = null;

		if ((page = pages.get(type.toString())) == null) {
			pages.put(type.toString(), page = type.create());
		}

		if (!page.isAttached()) {
			mPanel.clear();
			mPanel.add(page);
		} else {}
	}

	/**
	 * @param value
	 */
	public void addPage(String value) {

		if (value == null || value.length() == 0) {
			value = PageType.HomePageType.toString();
		}

		mStack = Stack.parse(value);

		if (PageType.UsersPageType.equals(mStack.getPage())) {
			if (mStack.getAction() == null) {
				attachPage(PageType.UsersPageType);
			} else if ("assignrole".equals(mStack.getAction())) {
				String userId = mStack.getParameter(0);
				String roleName = mStack.getParameter(1);

				// TODO: this should not really be here (and the navigation controller should probably not be responsible for actions)
				if (userId != null) {
					if (roleName.equalsIgnoreCase("admin")) {
						UserController.get().makeAdmin(Long.valueOf(userId));
					} else if (roleName.equals("beta")) {
						UserController.get().makeBeta(Long.valueOf(userId));
					}
				}

				PageType.UsersPageType.show();

				return;
			} else {
				attachPage(PageType.fromString(mStack.getAction()));
			}
		} else if ("logout".equals(mStack.getPage())) {
			SessionController.get().logout();
			PageType.LoginPageType.show();
			return;
		} else {
			attachPage(PageType.fromString(mStack.getPage()));
		}

		EventController.get().fireEventFromSource(new NavigationEventHandler.ChangedEvent(mStack), NavigationController.this);
	}

	/**
	 * @return
	 */
	public Widget getHeader() {
		if (mHeader == null) {
			mHeader = new Header();
		}
		return mHeader;
	}

	/**
	 * @return
	 */
	public Widget getFooter() {
		if (mFooter == null) {
			mFooter = new Footer();
		}
		return mFooter;
	}

	public String getCurrentPage() {
		return mStack.getPage();
	}

	public Stack getStack() {
		return mStack;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.event.logical.shared.ValueChangeHandler#onValueChange(com.google.gwt.event.logical.shared.ValueChangeEvent)
	 */
	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		addPage(event.getValue());
	}
}
