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
import io.reflection.app.datatypes.shared.User;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.gwt.crypto.bouncycastle.util.encoders.Base64;
import com.spacehopperstudios.utility.StringUtils;

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

	private String intended = null;

	private boolean loaded = false;

	public static class Stack {
		public static final String NEXT_KEY = "nextgoto:";
		public static final String PREVIOUS_KEY = "prevgoto:";

		private String allParts;
		private String[] mParts;

		private Stack previous;
		private Stack next;

		private Stack(String value) {
			if (value != null) {
				allParts = value;
				mParts = allParts.split("/");

				for (String part : mParts) {
					String[] parameters;
					if (next == null && (parameters = Stack.decode(NEXT_KEY, part)) != null) {
						next = new Stack(StringUtils.join(Arrays.asList(parameters), "/"));
					}

					if (previous == null && (parameters = Stack.decode(PREVIOUS_KEY, part)) != null) {
						previous = new Stack(StringUtils.join(Arrays.asList(parameters), "/"));
					}

					if (next != null && previous != null) break;
				}
			}
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

		@Override
		public String toString() {
			return allParts;
		}

		public String toString(int fromPart) {
			return mParts == null ? "" : StringUtils.join(Arrays.asList(mParts).subList(fromPart, mParts.length), "/");
		}

		public String asParameter() {
			return Stack.encode(null, allParts);
		}

		public String asNextParameter() {
			return Stack.encode(NEXT_KEY, allParts);
		}

		public String asPreviousParameter() {
			return Stack.encode(PREVIOUS_KEY, allParts);
		}

		/**
		 * @return
		 */
		public boolean hasNext() {
			return next != null;
		}

		/**
		 * @return
		 */
		public Stack getNext() {
			return next;
		}

		/**
		 * @return
		 */
		public boolean hasPrevious() {
			return previous != null;
		}

		/**
		 * @return
		 */
		public Stack getPrevious() {
			return previous;
		}

		public static String encode(String name, String... values) {
			String parameters = "";

			if (values != null && values.length > 0) {
				parameters = new String(Base64.encode(StringUtils.join(Arrays.asList(values), "/").getBytes()));
			}

			return (name == null || name.length() == 0) ? parameters : (name + parameters);
		}

		public static String[] decode(String name, String encoded) {
			String content;
			String decoded;
			String[] splitDecoded = null;

			if (encoded != null && encoded.length() > 0 && encoded.startsWith(name)) {
				content = encoded.substring(name.length());

				if (content != null && content.length() > 0) {
					decoded = new String(Base64.decode(content));

					if (decoded.length() > 0) {
						splitDecoded = decoded.split("/");
					}
				}
			}

			return splitDecoded;
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
			mPanel = new HTMLPanel("");
			mPanel.setStyleName("container-fluid");
			mPanel.getElement().setAttribute("style", "padding: 60px 0px 60px 0px;");
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
		Stack s = Stack.parse(value);

		if (intended != null && intended.equals(s.toString())) {
			intended = null;
		}

		addStack(s);
	}

	private void addStack(Stack value) {
		String page = value.getPage();

		if ("logout".equals(page)) {
			SessionController.get().logout();
		} else {
			PageType stackPage = PageType.fromString(page);

			if (stackPage == null || !stackPage.isNavigable()) {
				stackPage = PageType.HomePageType;
			} else if (PageType.UsersPageType == stackPage) {
				if (value.hasAction()) {
					stackPage = PageType.fromString(value.getAction());
				}
			}

			boolean doAttach = false;

			if (SessionController.get().isValidSession()) {
				if (loaded) {
					if (!stackPage.requiresLogin() || SessionController.get().isLoggedInUserAdmin()
							|| SessionController.get().isAuthorised(stackPage.getRequiredPermissions())) {
						doAttach = true;
					} else {
						if (!PageType.NotPermittedPageType.equals(mStack.getPage())) {
							PageType.NotPermittedPageType.show(value.asParameter(), mStack.asPreviousParameter());
						}
					}
				} else {
					setLastIntendedPage(value);
					stackPage = PageType.LoadingPageType;
					value = new Stack(stackPage.toString());
					doAttach = true;
					loaded = true;
				}
			} else {
				if (stackPage.requiresLogin()) {
					String loginParams = "";

					User user = SessionController.get().getLoggedInUser();
					if (user != null && user.username != null) {
						loginParams += user.username + "/";
					}

					loginParams += value.asNextParameter();

					PageType.LoginPageType.show(loginParams);
				} else {
					doAttach = true;
				}
			}

			if (doAttach) {
				Stack previous = mStack;
				mStack = value;

				attachPage(stackPage);
				EventController.get().fireEventFromSource(new NavigationEventHandler.ChangedEvent(previous, mStack), NavigationController.this);
			}
		}

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

	public PageType getCurrentPage() {
		return PageType.fromString(mStack.getPage());
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

	public void showIntendedPage() {
		if (intended == null) {
			addPage(PageType.HomePageType.toString());
		} else {
			addPage(intended);
		}
	}

	public void setLastIntendedPage(Stack value) {
		intended = value.toString();
	}

	public void showNext() {
		if (mStack.hasNext()) {
			PageType.fromString(mStack.getNext().getPage()).show(mStack.getNext().toString(1));
		} else {
			PageType.HomePageType.show();
		}
	}

	public void showPrevious() {
		if (mStack.hasPrevious()) {
			PageType.fromString(mStack.getPrevious().getPage()).show(mStack.getPrevious().toString(1));
		} else {
			PageType.HomePageType.show();
		}
	}

	/**
	 * 
	 */
	public void setNotLoaded() {
		loaded = false;
	}

}
