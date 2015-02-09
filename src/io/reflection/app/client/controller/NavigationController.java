//
//  NavigationController.java
//  storedata
//
//  Created by William Shakour (billy1380) on 3 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.controller;

import static io.reflection.app.client.controller.FilterController.OVERALL_LIST_TYPE;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.helper.MixPanelApiHelper;
import io.reflection.app.client.mixpanel.MixPanelApi;
import io.reflection.app.client.page.HomePage;
import io.reflection.app.client.page.Page;
import io.reflection.app.client.page.PageType;
import io.reflection.app.client.part.Footer;
import io.reflection.app.client.part.Header;
import io.reflection.app.datatypes.shared.User;
import io.reflection.app.shared.util.DataTypeHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
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

	public static final String ADD_ACTION_PARAMETER_VALUE = "add";
	public static final String EDIT_ACTION_PARAMETER_VALUE = "edit";
	public static final String DELETE_ACTION_PARAMETER_VALUE = "delete";
	public static final String VIEW_ACTION_PARAMETER_VALUE = "view";

	private static NavigationController one = null;

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
					} else if (previous == null && (parameters = Stack.decode(PREVIOUS_KEY, part)) != null) {
						previous = new Stack(StringUtils.join(Arrays.asList(parameters), "/"));
					} else {
						FilterController.get().fromParameter(part);
					}
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

		public String toString(String... param) {
			return toString() + "/" + StringUtils.join(Arrays.asList(param), "/");
		}

		public String toString(int fromPart, String... param) {
			return toString(fromPart) + "/" + StringUtils.join(Arrays.asList(param), "/");
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

		public int getParameterCount() {
			int count = mParts.length - 2;
			return count > 0 ? count : 0;
		}
	}

	public static NavigationController get() {
		if (one == null) {
			one = new NavigationController();
		}

		return one;
	}

	private NavigationController() {
		MixPanelApi.get().init("400e244ec1aab9ad548fe51024506310");
	}

	/**
	 * @return
	 */
	public Widget getPageHolderPanel() {
		if (mPanel == null) {
			mPanel = new HTMLPanel("");
			mPanel.setStyleName("container-fluid");
			mPanel.getElement().setAttribute("style", "padding: 60px 0px 39px 0px; min-width: 275px;");
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
		}
	}

	/**
	 * @param value
	 */
	public void addPage(String value) {
		FilterController.get().setFilter(value);

		Stack s = Stack.parse(value);

		if (intended != null && intended.equals(s.toString())) {
			intended = null;
		}

		addStack(s);
	}

	private void addStack(Stack value) {
		MixPanelApiHelper.trackNavigation(value);

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
				// If beta user with no linked accounts, always redirect to linkitunes page (show only post because of the 'waths this' link in the form)
				if (!SessionController.get().loggedInUserHas(DataTypeHelper.PERMISSION_HAS_LINKED_ACCOUNT_CODE)
						&& SessionController.get().loggedInUserIs(DataTypeHelper.ROLE_FIRST_CLOSED_BETA_CODE) && stackPage != PageType.BlogPostPageType
						&& stackPage != PageType.BlogPostsPageType && stackPage != PageType.BlogEditPostPageType && stackPage != PageType.BlogTagPageType) {
					stackPage = PageType.LinkItunesPageType;
					value = new Stack(stackPage.toString());
					doAttach = true;
					loaded = true;
					PageType.LinkItunesPageType.show();
				} else {
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
				}
			} else {
				if (stackPage.requiresLogin()) {
					String loginParams = "";

					User user = SessionController.get().getLoggedInUser();
					if (user != null && user.username != null) {
						loginParams += user.username + "/";
					}

					loginParams += value.asNextParameter();

					SessionController.get().makeSessionInvalid();

					PageType.LoginPageType.show(loginParams);
				} else {
					doAttach = true;
				}
			}

			if (doAttach) {
				final Stack previous = mStack;
				mStack = value;

				final PageType currentPage = stackPage;

				if (currentPage == PageType.HomePageType) {
					HomePage.applyHomePageTweeks();
				}

				// So in the web.bindery SimpleEventBus, it records the state of
				// firingDepth i.e. if eventA calls eventB call eventC, we'd be
				// 3 levels deep at
				// the time that eventC is called. When new handlers are added
				// it checks to see if we are at level 0. If not, it queues the
				// addition of the
				// new handler, wrapping it in a deferred run statement. So none
				// of the new handlers in the onAttached are run in this case.
				// This means in NavigationController.addStack, when the
				// ChangedEvent is fired... it misses all the enqueued handlers
				// of the new page.
				// This is why we wrap the firing of ChangedEvent.

				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						attachPage(currentPage);

						Scheduler.get().scheduleDeferred(new ScheduledCommand() {
							@Override
							public void execute() {
								DefaultEventBus.get().fireEventFromSource(new NavigationEventHandler.ChangedEvent(previous, mStack), NavigationController.this);
							}
						});
					}
				});

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
		PageType p = null;
		if (mStack != null) {
			p = PageType.fromString(mStack.getPage());
		}
		return p;
	}

	public Stack getStack() {
		return mStack;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.event.logical.shared.ValueChangeHandler#onValueChange( com.google.gwt.event.logical.shared.ValueChangeEvent)
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
			if (SessionController.get().getLoggedInUser() != null) {
				PageType.RanksPageType.show(VIEW_ACTION_PARAMETER_VALUE, OVERALL_LIST_TYPE, FilterController.get().asRankFilterString());
			} else {
				PageType.HomePageType.show();
			}
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

	/**
	 * Purges all pages that contain or rely on user data
	 */
	public void purgeUserPages() {
		List<String> pagesToRemove = new ArrayList<String>();

		for (String pageType : pages.keySet()) {
			if (PageType.fromString(pageType).requiresLogin()) {
				pagesToRemove.add(pageType);
			}
		}

		for (String pageType : pagesToRemove) {
			pages.remove(pageType);
		}
	}

	/**
	 * Purges all pages
	 */
	public void purgeAllPages() {
		pages.clear();
	}

}
