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
import io.reflection.app.client.controller.ItemController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.RankController;
import io.reflection.app.client.controller.ServiceController;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.handler.FilterEventHandler;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.handler.RanksEventHandler;
import io.reflection.app.client.handler.user.SessionEventHandler;
import io.reflection.app.client.part.AlertBox;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.Breadcrumbs;
import io.reflection.app.client.part.PageSizePager;
import io.reflection.app.client.part.RankSidePanel;
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
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.Error;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class RanksPage extends Page implements RanksEventHandler, FilterEventHandler, SessionEventHandler, IsAuthorisedEventHandler, NavigationEventHandler {

	private static RanksPageUiBinder uiBinder = GWT.create(RanksPageUiBinder.class);

	interface RanksPageUiBinder extends UiBinder<Widget, RanksPage> {}

	@UiField AlertBox mAlertBox;

	@UiField(provided = true) CellTable<RanksGroup> mRanks = new CellTable<RanksGroup>(ServiceController.STEP_VALUE, BootstrapGwtCellTable.INSTANCE);
	@UiField(provided = true) PageSizePager mPager = new PageSizePager(ServiceController.STEP_VALUE);

	@UiField RankSidePanel mSidePanel;

	@UiField Breadcrumbs mBreadcrumbs;
	@UiField InlineHyperlink mRedirect;

	private String mListType = ALL_LIST_TYPE;

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
	private TextColumn<RanksGroup> mPriceColumn;
	private TextColumn<RanksGroup> mDownloadsColumn;
	private TextColumn<RanksGroup> mRevenueColumn;
	private TextColumn<RanksGroup> mIapColumn;

	private static final String ALL_LIST_TYPE = "all";
	private static final String FREE_LIST_TYPE = "free";
	private static final String PAID_LIST_TYPE = "paid";
	private static final String GROSSING_LIST_TYPE = "grossing";

	private Map<String, LIElement> mTabs = new HashMap<String, LIElement>();
	private TextHeader mPriceHeader;
	private TextHeader mPaidHeader;
	private TextHeader mFreeHeader;
	private TextHeader mGrossingHeader;
	private TextHeader mDownloadsHeader;
	private TextHeader mRevenueHeader;
	private TextHeader mIapHeader;

	public RanksPage() {
		initWidget(uiBinder.createAndBindUi(this));

		createColumns();

		mTabs.put(ALL_LIST_TYPE, mAllItem);
		mTabs.put(FREE_LIST_TYPE, mFreeItem);
		mTabs.put(PAID_LIST_TYPE, mPaidItem);
		mTabs.put(GROSSING_LIST_TYPE, mGrossingItem);

		RankController.get().addDataDisplay(mRanks);
		mPager.setDisplay(mRanks);
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
				return ItemController.get().lookupItem(object.paid.itemId);
			}
		};

		mFreeColumn = new Column<RanksGroup, Item>(new MiniAppCell()) {

			@Override
			public Item getValue(RanksGroup object) {
				return ItemController.get().lookupItem(object.free.itemId);
			}
		};

		mGrossingColumn = new Column<RanksGroup, Item>(new MiniAppCell()) {

			@Override
			public Item getValue(RanksGroup object) {
				return ItemController.get().lookupItem(object.grossing.itemId);
			}
		};

		mPriceColumn = new TextColumn<RanksGroup>() {

			@Override
			public String getValue(RanksGroup object) {
				return rankForListType(object).price.toString();
			}

		};
		mDownloadsColumn = new TextColumn<RanksGroup>() {

			@Override
			public String getValue(RanksGroup object) {
				return rankForListType(object).downloads.toString();
			}

		};
		mRevenueColumn = new TextColumn<RanksGroup>() {

			@Override
			public String getValue(RanksGroup object) {
				return rankForListType(object).revenue.toString();
			}

		};
		mIapColumn = new TextColumn<RanksGroup>() {

			@Override
			public String getValue(RanksGroup object) {
				String jsonProperties = ItemController.get().lookupItem(rankForListType(object).itemId).properties;

				return jsonProperties == null ? "?" : jsonProperties;
			}

		};

		TextHeader rankHeader = new TextHeader("Rank");
		rankHeader.setHeaderStyleNames("col-xs-1");
		mRanks.addColumn(position, rankHeader);

		mPaidHeader = new TextHeader("Paid");
		mRanks.addColumn(mPaidColumn, mPaidHeader);

		mFreeHeader = new TextHeader("Free");
		mRanks.addColumn(mFreeColumn, mFreeHeader);

		mGrossingHeader = new TextHeader("Grossing");
		mRanks.addColumn(mGrossingColumn, mGrossingHeader);

//		mPriceHeader = new TextHeader("Price");
//		mRanks.addColumn(mPriceColumn, mPriceHeader);
//
//		mDownloadsHeader = new TextHeader("Downloads");
//		mRanks.addColumn(mDownloadsColumn, mDownloadsHeader);
//
//		mRevenueHeader = new TextHeader("Revenue");
//		mRanks.addColumn(mRevenueColumn, mRevenueHeader);
//
//		mIapHeader = new TextHeader("IAP");
//		mRanks.addColumn(mIapColumn, mIapHeader);

	}

	/**
	 * @param object
	 * @return
	 */
	protected Rank rankForListType(RanksGroup object) {
		Rank rank = object.grossing;

		if (FREE_LIST_TYPE.equals(mListType)) {
			rank = object.free;
		} else if (PAID_LIST_TYPE.equals(mListType)) {
			rank = object.paid;
		}

		return rank;
	}

	/**
	 * 
	 */
	private void refreshBreadcrumbs() {
		mBreadcrumbs.clear();
		mBreadcrumbs.push(mSidePanel.getStore(), mSidePanel.getCountry(), mListType.substring(0, 1).toUpperCase() + mListType.substring(1),
				mSidePanel.getDisplayDate(), "Ranks");
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
		RankController.get().reset();

		refreshBreadcrumbs();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.FilterEventHandler#filterParamsChanged(java.util.Map, java.util.Map)
	 */
	@Override
	public void filterParamsChanged(Map<String, ?> currentValues, Map<String, ?> previousValues) {
		RankController.get().reset();

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

		checkPermissions();
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
			addColumn(mGrossingColumn, mGrossingHeader, 1);
			addColumn(mFreeColumn, mFreeHeader, 1);
			addColumn(mPaidColumn, mPaidHeader, 1);
			removeColumn(mPriceColumn);
			removeColumn(mDownloadsColumn);
			removeColumn(mRevenueColumn);
			removeColumn(mIapColumn);
		} else if (FREE_LIST_TYPE.equals(mListType)) {
			removeColumn(mPaidColumn);
			removeColumn(mGrossingColumn);
			addColumn(mFreeColumn, mFreeHeader, 1);

			addColumn(mPriceColumn, mPriceHeader, 2);
			addColumn(mDownloadsColumn, mDownloadsHeader, 3);
			addColumn(mRevenueColumn, mRevenueHeader, 4);
			addColumn(mIapColumn, mIapHeader, 5);
		} else if (PAID_LIST_TYPE.equals(mListType)) {
			removeColumn(mFreeColumn);
			removeColumn(mGrossingColumn);
			addColumn(mPaidColumn, mPaidHeader, 1);

			addColumn(mPriceColumn, mPriceHeader, 2);
			addColumn(mDownloadsColumn, mDownloadsHeader, 3);
			addColumn(mRevenueColumn, mRevenueHeader, 4);
			addColumn(mIapColumn, mIapHeader, 5);
		} else if (GROSSING_LIST_TYPE.equals(mListType)) {
			removeColumn(mPaidColumn);
			removeColumn(mFreeColumn);
			addColumn(mGrossingColumn, mGrossingHeader, 1);

			addColumn(mPriceColumn, mPriceHeader, 2);
			addColumn(mDownloadsColumn, mDownloadsHeader, 3);
			addColumn(mRevenueColumn, mRevenueHeader, 4);
			addColumn(mIapColumn, mIapHeader, 5);
		}

	}

	private void removeColumn(Column<RanksGroup, ?> column) {
		// int currentIndex = mRanks.getColumnIndex(column);
		//
		// if (currentIndex != -1) {
		// mRanks.removeColumn(column);
		// }
	}

	private void addColumn(Column<RanksGroup, ?> column, TextHeader header, int index) {
		// int currentIndex = mRanks.getColumnIndex(column);
		//
		// if (currentIndex == -1) {
		// mRanks.insertColumn(index, column, header);
		// }
	}

	private void refreshTabs() {
		for (String key : mTabs.keySet()) {
			mTabs.get(key).removeClassName("active");
		}

		mTabs.get(mListType).addClassName("active");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack stack) {

		if ("ranks".equals(stack.getPage())) {
			checkPermissions();
		}

	}

	private void checkPermissions() {
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
	 * @see com.google.gwt.user.client.ui.Composite#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();
		
		register(EventController.get().addHandlerToSource(RanksEventHandler.TYPE, RankController.get(), this));
		register(EventController.get().addHandlerToSource(FilterEventHandler.TYPE, FilterController.get(), this));
		register(EventController.get().addHandlerToSource(SessionEventHandler.TYPE, SessionController.get(), this));
		register(EventController.get().addHandlerToSource(IsAuthorisedEventHandler.TYPE, SessionController.get(), this));
		register(EventController.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));
		
		refreshBreadcrumbs();

		refreshTabs();

	}

}
