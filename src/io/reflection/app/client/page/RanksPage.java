//
//  RanksPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 3 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.page;

import io.reflection.app.api.admin.shared.call.event.IsAuthorisedEventHandler;
import io.reflection.app.api.core.shared.call.IsAuthorisedRequest;
import io.reflection.app.api.core.shared.call.IsAuthorisedResponse;
import io.reflection.app.api.shared.datatypes.Session;
import io.reflection.app.client.cell.MiniAppCell;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.controller.RankController;
import io.reflection.app.client.controller.ServiceController;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.handler.FilterEventHandler;
import io.reflection.app.client.handler.RanksEventHandler;
import io.reflection.app.client.handler.user.SessionEventHandler;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.Breadcrumbs;
import io.reflection.app.client.part.PageSizePager;
import io.reflection.app.client.part.RankFilter;
import io.reflection.app.client.part.datatypes.RanksGroup;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Permission;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.datatypes.shared.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.Error;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class RanksPage extends Composite implements RanksEventHandler, FilterEventHandler, SessionEventHandler, IsAuthorisedEventHandler {

	private static RanksPageUiBinder uiBinder = GWT.create(RanksPageUiBinder.class);

	interface RanksPageUiBinder extends UiBinder<Widget, RanksPage> {}

	@UiField(provided = true) CellTable<RanksGroup> mRanks = new CellTable<RanksGroup>(ServiceController.STEP_VALUE, BootstrapGwtCellTable.INSTANCE);
	@UiField(provided = true) PageSizePager mPager = new PageSizePager(ServiceController.STEP_VALUE);

	@UiField RankFilter mFilter;

	@UiField Breadcrumbs mBreadcrumbs;
	@UiField InlineHyperlink mRedirect;

	String mListType = ALL_LIST_TYPE;

	@UiField InlineHyperlink mAll;
	@UiField InlineHyperlink mFree;
	@UiField InlineHyperlink mGrossing;
	@UiField InlineHyperlink mPaid;

	@UiField LIElement mAllItem;
	@UiField LIElement mFreeItem;
	@UiField LIElement mGrossingItem;
	@UiField LIElement mPaidItem;

	private Column<RanksGroup, Item> mGrossingColumn;
	private Column<RanksGroup, Item> mFreeColumn;
	private Column<RanksGroup, Item> mPaidColumn;

	private static final String ALL_LIST_TYPE = "all";
	private static final String FREE_LIST_TYPE = "free";
	private static final String PAID_LIST_TYPE = "paid";
	private static final String GROSSING_LIST_TYPE = "grossing";

	private Map<String, LIElement> mTabs = new HashMap<String, LIElement>();

	public RanksPage() {
		initWidget(uiBinder.createAndBindUi(this));

		createColumns();

		mTabs.put(ALL_LIST_TYPE, mAllItem);
		mTabs.put(FREE_LIST_TYPE, mFreeItem);
		mTabs.put(PAID_LIST_TYPE, mPaidItem);
		mTabs.put(GROSSING_LIST_TYPE, mGrossingItem);

		EventController.get().addHandlerToSource(RanksEventHandler.TYPE, RankController.get(), this);
		EventController.get().addHandlerToSource(FilterEventHandler.TYPE, FilterController.get(), this);
		EventController.get().addHandlerToSource(SessionEventHandler.TYPE, SessionController.get(), this);
		EventController.get().addHandlerToSource(IsAuthorisedEventHandler.TYPE, SessionController.get(), this);

		RankController.get().addDataDisplay(mRanks);
		mPager.setDisplay(mRanks);

		refreshBreadcrumbs();

		refreshTabs();
	}

	private void createColumns() {
		TextColumn<RanksGroup> position = new TextColumn<RanksGroup>() {

			@Override
			public String getValue(RanksGroup object) {
				return object.free.position.toString();
			}

		};

		mPaidColumn = new Column<RanksGroup, Item>(new MiniAppCell()) {

			@Override
			public Item getValue(RanksGroup object) {
				return RankController.get().lookupItem(object.paid.itemId);
			}
		};

		mFreeColumn = new Column<RanksGroup, Item>(new MiniAppCell()) {

			@Override
			public Item getValue(RanksGroup object) {
				return RankController.get().lookupItem(object.free.itemId);
			}
		};

		mGrossingColumn = new Column<RanksGroup, Item>(new MiniAppCell()) {

			@Override
			public Item getValue(RanksGroup object) {
				return RankController.get().lookupItem(object.grossing.itemId);
			}
		};

		TextHeader rankHeader = new TextHeader("Rank");
		rankHeader.setHeaderStyleNames("col-md-1");
		mRanks.addColumn(position, rankHeader);

		TextHeader paidHeader = new TextHeader("Paid");
		paidHeader.setHeaderStyleNames("col-md-3");
		mRanks.addColumn(mPaidColumn, paidHeader);

		TextHeader freeHeader = new TextHeader("Free");
		freeHeader.setHeaderStyleNames("col-md-3");
		mRanks.addColumn(mFreeColumn, freeHeader);

		TextHeader grossingHeader = new TextHeader("Grossing");
		grossingHeader.setHeaderStyleNames("col-md-3");
		mRanks.addColumn(mGrossingColumn, grossingHeader);
	}

	/**
	 * 
	 */
	private void refreshBreadcrumbs() {
		mBreadcrumbs.clear();
		mBreadcrumbs.push(mFilter.getStore(), mFilter.getCountry(), mListType.substring(0, 1).toUpperCase() + mListType.substring(1), mFilter.getDisplayDate(),
				"Ranks");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.RanksEventHandler#receivedRanks(java.lang.String, java.util.List)
	 */
	@Override
	public void receivedRanks(String listType, List<Rank> ranks) {
		mPager.setLoading(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.RanksEventHandler#fetchingRanks()
	 */
	@Override
	public void fetchingRanks() {
		mPager.setLoading(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.FilterEventHandler#filterParamChanged(java.lang.String, java.lang.Object, java.lang.Object)
	 */
	@Override
	public <T> void filterParamChanged(String name, T currentValue, T previousValue) {
		refreshBreadcrumbs();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.FilterEventHandler#filterParamsChanged(java.util.Map, java.util.Map)
	 */
	@Override
	public void filterParamsChanged(Map<String, ?> currentValues, Map<String, ?> previousValues) {
		refreshBreadcrumbs();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.SessionEventHandler#userLoggedIn(io.reflection.app.datatypes.shared.User,
	 * io.reflection.app.api.shared.datatypes.Session)
	 */
	@Override
	public void userLoggedIn(User user, Session session) {
		RankController.get().reset();

		List<Permission> permissions = new ArrayList<Permission>();

		Permission p = new Permission();
		// p.code = "FRV";
		p.id = Long.valueOf(1);
		permissions.add(p);

		SessionController.get().fetchAuthorisation(null, permissions);

		mRedirect.setTargetHistoryToken("upgrade");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.SessionEventHandler#userLoggedOut()
	 */
	@Override
	public void userLoggedOut() {
		RankController.get().reset();
		mPager.setVisible(false);
		mRedirect.setVisible(true);
		mRedirect.setTargetHistoryToken("login");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.SessionEventHandler#userLoginFailed(com.willshex.gson.json.service.shared.Error)
	 */
	@Override
	public void userLoginFailed(Error error) {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.admin.shared.call.event.IsAuthorisedEventHandler#isAuthorisedSuccess(io.reflection.app.api.core.shared.call.IsAuthorisedRequest,
	 * io.reflection.app.api.core.shared.call.IsAuthorisedResponse)
	 */
	@Override
	public void isAuthorisedSuccess(IsAuthorisedRequest input, IsAuthorisedResponse output) {
		if (output.status == StatusType.StatusTypeSuccess && output.authorised == Boolean.TRUE) {
			if (input.permissions != null) {
				for (Permission p : input.permissions) {
					if (p.id != null && p.id != null) {
						mPager.setVisible(true);
						mRedirect.setVisible(false);
						break;
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.admin.shared.call.event.IsAuthorisedEventHandler#isAuthorisedFailure(io.reflection.app.api.core.shared.call.IsAuthorisedRequest,
	 * java.lang.Throwable)
	 */
	@Override
	public void isAuthorisedFailure(IsAuthorisedRequest input, Throwable caught) {}

	@UiHandler({ "mAll", "mFree", "mGrossing", "mPaid" })
	void onClicked(ClickEvent e) {
		boolean changed = false;

		if (e.getSource() == mAll && !ALL_LIST_TYPE.equals(mListType)) {
			mListType = ALL_LIST_TYPE;
			changed = true;
		} else if (e.getSource() == mFree && !FREE_LIST_TYPE.equals(mListType)) {
			mListType = FREE_LIST_TYPE;
			changed = true;
		} else if (e.getSource() == mPaid && !PAID_LIST_TYPE.equals(mListType)) {
			mListType = PAID_LIST_TYPE;
			changed = true;
		} else if (e.getSource() == mGrossing && !GROSSING_LIST_TYPE.equals(mListType)) {
			mListType = GROSSING_LIST_TYPE;
			changed = true;
		}

		if (changed) {
			refreshBreadcrumbs();

			refreshTabs();

			refreshRanks();
		}
	}

	private void refreshRanks() {

		if (ALL_LIST_TYPE.equals(mListType)) {
			addColumn(mPaidColumn, "Paid", 1);
			addColumn(mFreeColumn, "Free", 2);
			addColumn(mGrossingColumn, "Grossing", 3);
		} else if (FREE_LIST_TYPE.equals(mListType)) {
			removeColumn(mPaidColumn);
			removeColumn(mGrossingColumn);
			addColumn(mFreeColumn, "Free", 1);
		} else if (PAID_LIST_TYPE.equals(mListType)) {
			addColumn(mPaidColumn, "Paid", 1);
			removeColumn(mFreeColumn);
			removeColumn(mGrossingColumn);
		} else if (GROSSING_LIST_TYPE.equals(mListType)) {
			removeColumn(mPaidColumn);
			removeColumn(mFreeColumn);
			addColumn(mGrossingColumn, "Grossing", 1);
		}

	}

	private void removeColumn(Column<RanksGroup, ?> column) {
		int currentIndex = mRanks.getColumnIndex(column);

		if (currentIndex != -1) {
			mRanks.removeColumn(column);
		}
	}

	private void addColumn(Column<RanksGroup, ?> column, String header, int index) {
		int currentIndex = mRanks.getColumnIndex(column);

		if (currentIndex == -1) {
			mRanks.insertColumn(1, column, new TextHeader(header));
		}
	}

	private void refreshTabs() {
		for (String key : mTabs.keySet()) {
			mTabs.get(key).removeClassName("active");
		}

		mTabs.get(mListType).addClassName("active");
	}
}
