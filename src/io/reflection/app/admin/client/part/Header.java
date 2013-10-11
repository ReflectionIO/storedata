//
//  Header.java
//  storedata
//
//  Created by William Shakour (billy1380) on 3 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.part;

import io.reflection.app.admin.client.controller.EventController;
import io.reflection.app.admin.client.controller.NavigationController;
import io.reflection.app.admin.client.controller.NavigationController.Stack;
import io.reflection.app.admin.client.controller.UserController;
import io.reflection.app.admin.client.event.NavigationChanged;
import io.reflection.app.admin.client.event.ReceivedCount;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class Header extends Composite implements ReceivedCount.Handler, NavigationChanged.Handler {

	private static HeaderUiBinder uiBinder = GWT.create(HeaderUiBinder.class);

	interface HeaderUiBinder extends UiBinder<Widget, Header> {}

	@UiField InlineHyperlink mRanksLink;
	@UiField LIElement mRanksItem;

	@UiField InlineHyperlink mFeedBrowserLink;
	@UiField LIElement mFeedBrowserItem;

	@UiField InlineHyperlink mUsersLink;
	@UiField LIElement mUsersItem;

	@UiField SpanElement mTotalUsers;

	public Header() {
		initWidget(uiBinder.createAndBindUi(this));
		mRanksItem.addClassName("active");
		
		EventController.get().addHandlerToSource(ReceivedCount.TYPE, UserController.get(), this);
		
		if (UserController.get().getUsersCount() >= 0) {
			mTotalUsers.setInnerText(Long.toString(UserController.get().getUsersCount()));
		} else {
			UserController.get().fetchUsersCount();
		}
		
		EventController.get().addHandlerToSource(NavigationChanged.TYPE, NavigationController.get(), this);
	}

	@UiHandler("mRanksLink")
	public void onRanksLinkClicked(ClickEvent event) {
		NavigationController.get().addRanksPage();
	}

	@UiHandler("mFeedBrowserLink")
	public void onFeedBrowserLinkClicked(ClickEvent event) {
		NavigationController.get().addFeedBrowserPage();

	}

	@UiHandler("mUsersLink")
	public void onUsersLinkClicked(ClickEvent event) {
		NavigationController.get().addUsersPage();
	}

	private void activateFeedBrowser() {
		mFeedBrowserItem.addClassName("active");
		mRanksItem.removeClassName("active");
		mUsersItem.removeClassName("active");
	}

	private void activateUsers() {
		mFeedBrowserItem.removeClassName("active");
		mRanksItem.removeClassName("active");
		mUsersItem.addClassName("active");
	}

	private void activateRanks() {
		mRanksItem.addClassName("active");
		mFeedBrowserItem.removeClassName("active");
		mUsersItem.removeClassName("active");
	}

	/* (non-Javadoc)
	 * @see io.reflection.app.admin.client.event.NavigationChanged.Handler#navigationChanged(io.reflection.app.admin.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack stack) {
		if ("ranks".equals(stack.getPage())) {
			activateRanks();
		} else if ("feedbrowser".equals(stack.getPage())) {
			activateFeedBrowser();
		} else if("users".equals(stack.getPage())) {
			activateUsers();
		}
	}

	/* (non-Javadoc)
	 * @see io.reflection.app.admin.client.event.ReceivedCount.Handler#receivedCount(java.lang.Long)
	 */
	@Override
	public void receivedCount(Long count) {
		mTotalUsers.setInnerText(count.toString());
		
	}

}
