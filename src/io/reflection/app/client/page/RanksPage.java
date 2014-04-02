//
//  RanksPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 3 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.page;

import static io.reflection.app.client.controller.FilterController.DAILY_DATA_KEY;
import static io.reflection.app.client.controller.FilterController.FREE_LIST_TYPE;
import static io.reflection.app.client.controller.FilterController.GROSSING_LIST_TYPE;
import static io.reflection.app.client.controller.FilterController.LIST_TYPE_KEY;
import static io.reflection.app.client.controller.FilterController.OVERALL_LIST_TYPE;
import static io.reflection.app.client.controller.FilterController.PAID_LIST_TYPE;
import io.reflection.app.client.cell.AppRankCell;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.controller.FilterController.Filter;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.RankController;
import io.reflection.app.client.controller.ServiceConstants;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.handler.FilterEventHandler;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.PageSizePager;
import io.reflection.app.client.part.RankSidePanel;
import io.reflection.app.client.part.datatypes.RanksGroup;
import io.reflection.app.client.res.Images;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.shared.util.FormattingHelper;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class RanksPage extends Page implements FilterEventHandler, // SessionEventHandler, IsAuthorisedEventHandler,
		NavigationEventHandler {

	private static RanksPageUiBinder uiBinder = GWT.create(RanksPageUiBinder.class);

	interface RanksPageUiBinder extends UiBinder<Widget, RanksPage> {}

	@UiField(provided = true) CellTable<RanksGroup> mRanks = new CellTable<RanksGroup>(ServiceConstants.STEP_VALUE, BootstrapGwtCellTable.INSTANCE);
	@UiField(provided = true) PageSizePager mPager = new PageSizePager(ServiceConstants.STEP_VALUE);

	@UiField RankSidePanel mSidePanel;

	@UiField InlineHyperlink mRedirect;

	@UiField InlineHyperlink mAll;
	@UiField InlineHyperlink mFree;
	@UiField InlineHyperlink mGrossing;
	@UiField InlineHyperlink mPaid;

	@UiField LIElement mAllItem;
	@UiField LIElement mFreeItem;
	@UiField LIElement mGrossingItem;
	@UiField LIElement mPaidItem;

	private Column<RanksGroup, Rank> mGrossingColumn;
	private Column<RanksGroup, Rank> mFreeColumn;
	private Column<RanksGroup, Rank> mPaidColumn;

	private TextColumn<RanksGroup> mPriceColumn;
	private TextColumn<RanksGroup> mDownloadsColumn;
	private TextColumn<RanksGroup> mRevenueColumn;
	private Column<RanksGroup, ImageResource> mIapColumn;

	private Map<String, LIElement> mTabs = new HashMap<String, LIElement>();

	private TextHeader mPaidHeader;
	private TextHeader mFreeHeader;
	private TextHeader mGrossingHeader;

	private TextHeader mDownloadsHeader;
	private TextHeader mRevenueHeader;
	private TextHeader mIapHeader;
	private TextHeader mPriceHeader;

	public RanksPage() {
		initWidget(uiBinder.createAndBindUi(this));

		createColumns();

		mTabs.put(OVERALL_LIST_TYPE, mAllItem);
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

		mPaidColumn = new Column<RanksGroup, Rank>(new AppRankCell()) {

			@Override
			public Rank getValue(RanksGroup object) {
				return object.paid;
			}
		};

		mFreeColumn = new Column<RanksGroup, Rank>(new AppRankCell()) {

			@Override
			public Rank getValue(RanksGroup object) {
				return object.free;
			}

		};

		mGrossingColumn = new Column<RanksGroup, Rank>(new AppRankCell()) {

			@Override
			public Rank getValue(RanksGroup object) {
				return object.grossing;
			}
		};

		mPriceColumn = new TextColumn<RanksGroup>() {

			@Override
			public String getValue(RanksGroup object) {
				Rank rank = rankForListType(object);
				return rank.price.floatValue() == 0.0f ? "Free" : FormattingHelper.getCurrencySymbol(rank.currency) + " " + rank.price.toString();
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

		mIapColumn = new Column<RanksGroup, ImageResource>(new ImageResourceCell()) {

			@Override
			public ImageResource getValue(RanksGroup object) {
				// String jsonProperties = ItemController.get().lookupItem(rankForListType(object).itemId).properties;
				// String imageName = jsonProperties == null ? "?" : jsonProperties;

				return Images.INSTANCE.greenTick();
			}

		};

		TextHeader rankHeader = new TextHeader("Rank");
		rankHeader.setHeaderStyleNames("col-xs-1");
		mRanks.addColumn(position, rankHeader);

		mPaidHeader = new TextHeader("Paid");

		mFreeHeader = new TextHeader("Free");

		mGrossingHeader = new TextHeader("Grossing");

		mPriceHeader = new TextHeader("Price");

		mDownloadsHeader = new TextHeader("Downloads");

		mRevenueHeader = new TextHeader("Revenue");

		mIapHeader = new TextHeader("IAP");

	}

	/**
	 * @param object
	 * @return
	 */
	protected Rank rankForListType(RanksGroup object) {
		Rank rank = object.grossing;

		String listType = FilterController.get().getFilter().getListType();

		if (FREE_LIST_TYPE.equals(listType)) {
			rank = object.free;
		} else if (PAID_LIST_TYPE.equals(listType)) {
			rank = object.paid;
		}

		return rank;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.FilterEventHandler#filterParamChanged(java.lang.String, java.lang.Object, java.lang.Object)
	 */
	@Override
	public <T> void filterParamChanged(String name, T currentValue, T previousValue) {
		boolean foundDailyData = false;
		if (name != null && !LIST_TYPE_KEY.equals(name) && !(foundDailyData = DAILY_DATA_KEY.equals(name))) {
			RankController.get().reset();
		}

		PageType.RanksPageType.show("view", FilterController.get().asRankFilterString());

		if (foundDailyData) {
			mRanks.redraw();
		}

		if (currentValue.toString().equals("paid") || currentValue.toString().equals("free") || currentValue.toString().equals("grossing")) {
			mSidePanel.setDataFilterVisible(false);

		} else if (currentValue.toString().equals("all")) {
			mSidePanel.setDataFilterVisible(true);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.FilterEventHandler#filterParamsChanged(io.reflection.app.client.controller.FilterController.Filter, java.util.Map)
	 */
	@Override
	public void filterParamsChanged(Filter currentFilter, Map<String, ?> previousValues) {
		boolean foundResetFilterValues = false, foundDailyData = false;

		for (String name : currentFilter.keySet()) {
			if (!LIST_TYPE_KEY.equals(name) && !(foundDailyData = DAILY_DATA_KEY.equals(name))) {
				foundResetFilterValues = true;
				break;
			}
		}

		if (foundResetFilterValues) {
			RankController.get().reset();
		} else if (foundDailyData) {
			PageType.RanksPageType.show("view", FilterController.get().asRankFilterString());
			mRanks.redraw();
		}

	}

	// /*
	// * (non-Javadoc)
	// *
	// * @see io.reflection.app.client.handler.user.SessionEventHandler#userLoggedIn(io.reflection.app.datatypes.shared.User,
	// * io.reflection.app.api.shared.datatypes.Session)
	// */
	// @Override
	// public void userLoggedIn(User user, Session session) {
	// RankController.get().reset();
	//
	// // checkPermissions();
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see io.reflection.app.client.handler.user.SessionEventHandler#userLoggedOut()
	// */
	// @Override
	// public void userLoggedOut() {
	// RankController.get().reset();
	// mPager.setVisible(false);
	// mRedirect.setVisible(true);
	// mRedirect.setTargetHistoryToken(PageType.LoginPageType.toString());
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see io.reflection.app.client.handler.user.SessionEventHandler#userLoginFailed(com.willshex.gson.json.service.shared.Error)
	// */
	// @Override
	// public void userLoginFailed(Error error) {}
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see
	// * io.reflection.app.api.admin.shared.call.event.IsAuthorisedEventHandler#isAuthorisedSuccess(io.reflection.app.api.core.shared.call.IsAuthorisedRequest,
	// * io.reflection.app.api.core.shared.call.IsAuthorisedResponse)
	// */
	// @Override
	// public void isAuthorisedSuccess(IsAuthorisedRequest input, IsAuthorisedResponse output) {
	// if (output.status == StatusType.StatusTypeSuccess && output.authorised == Boolean.TRUE) {
	// if (input.permissions != null) {
	// for (Permission p : input.permissions) {
	// if (p.id != null && p.id != null) {
	// mPager.setVisible(true);
	// mRedirect.setVisible(false);
	// }
	// }
	// }
	// }
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see
	// * io.reflection.app.api.admin.shared.call.event.IsAuthorisedEventHandler#isAuthorisedFailure(io.reflection.app.api.core.shared.call.IsAuthorisedRequest,
	// * java.lang.Throwable)
	// */
	// @Override
	// public void isAuthorisedFailure(IsAuthorisedRequest input, Throwable caught) {}

	@UiHandler({ "mAll", "mFree", "mGrossing", "mPaid" })
	void onClicked(ClickEvent e) {
		boolean changed = false;

		String listType = FilterController.get().getFilter().getListType();

		if (e.getSource() == mAll && !OVERALL_LIST_TYPE.equals(listType)) {
			FilterController.get().setListType(OVERALL_LIST_TYPE);
			changed = true;
		} else if (e.getSource() == mFree && !FREE_LIST_TYPE.equals(listType)) {
			FilterController.get().setListType(FREE_LIST_TYPE);
			changed = true;
		} else if (e.getSource() == mPaid && !PAID_LIST_TYPE.equals(listType)) {
			FilterController.get().setListType(PAID_LIST_TYPE);
			changed = true;
		} else if (e.getSource() == mGrossing && !GROSSING_LIST_TYPE.equals(listType)) {
			FilterController.get().setListType(GROSSING_LIST_TYPE);
			changed = true;
		}

		if (changed) {
			refreshTabs();
			refreshRanks();
		}
	}

	private void refreshRanks() {

		String listType = FilterController.get().getFilter().getListType();

		if (OVERALL_LIST_TYPE.equals(listType)) {
			removeAllColumns();
			addColumn(mPaidColumn, mPaidHeader);
			addColumn(mFreeColumn, mFreeHeader);
			addColumn(mGrossingColumn, mGrossingHeader);
		} else if (FREE_LIST_TYPE.equals(listType)) {
			removeAllColumns();

			addColumn(mFreeColumn, mFreeHeader);

			addColumn(mPriceColumn, mPriceHeader);
			addColumn(mDownloadsColumn, mDownloadsHeader);
			addColumn(mRevenueColumn, mRevenueHeader);
			addColumn(mIapColumn, mIapHeader);
		} else if (PAID_LIST_TYPE.equals(listType)) {
			removeAllColumns();

			addColumn(mPaidColumn, mPaidHeader);

			addColumn(mPriceColumn, mPriceHeader);
			addColumn(mDownloadsColumn, mDownloadsHeader);
			addColumn(mRevenueColumn, mRevenueHeader);
			addColumn(mIapColumn, mIapHeader);
		} else if (GROSSING_LIST_TYPE.equals(listType)) {
			removeAllColumns();

			addColumn(mGrossingColumn, mGrossingHeader);

			addColumn(mPriceColumn, mPriceHeader);
			addColumn(mDownloadsColumn, mDownloadsHeader);
			addColumn(mRevenueColumn, mRevenueHeader);
			addColumn(mIapColumn, mIapHeader);
		}

	}

	private void removeColumn(Column<RanksGroup, ?> column) {
		int currentIndex = mRanks.getColumnIndex(column);

		if (currentIndex != -1) {
			mRanks.removeColumn(column);
		}
	}

	private void removeAllColumns() {
		removeColumn(mFreeColumn);
		removeColumn(mPaidColumn);
		removeColumn(mGrossingColumn);

		removeColumn(mPriceColumn);
		removeColumn(mRevenueColumn);
		removeColumn(mDownloadsColumn);
		removeColumn(mIapColumn);
	}

	private void addColumn(Column<RanksGroup, ?> column, TextHeader header) {
		mRanks.addColumn(column, header);
	}

	private void refreshTabs() {
		for (String key : mTabs.keySet()) {
			mTabs.get(key).removeClassName("active");
		}

		LIElement selected = mTabs.get(FilterController.get().getFilter().getListType());

		if (selected != null) {
			selected.addClassName("active");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) {

		if (PageType.RanksPageType.equals(current.getPage())) {
			// checkPermissions();

			if (current.getAction() == null || !"view".equals(current.getAction())) {
				PageType.RanksPageType.show("view", FilterController.get().asRankFilterString());
			} else {
				Filter currentFilter = Filter.parse(FilterController.get().asRankFilterString());

				if (currentFilter != null) {
					currentFilter.setListType(OVERALL_LIST_TYPE);
					mAll.setTargetHistoryToken(PageType.RanksPageType.asTargetHistoryToken("view", currentFilter.asRankFilterString()));

					currentFilter.setListType(PAID_LIST_TYPE);
					mPaid.setTargetHistoryToken(PageType.RanksPageType.asTargetHistoryToken("view", currentFilter.asRankFilterString()));

					currentFilter.setListType(FREE_LIST_TYPE);
					mFree.setTargetHistoryToken(PageType.RanksPageType.asTargetHistoryToken("view", currentFilter.asRankFilterString()));

					currentFilter.setListType(GROSSING_LIST_TYPE);
					mGrossing.setTargetHistoryToken(PageType.RanksPageType.asTargetHistoryToken("view", currentFilter.asRankFilterString()));
				}

				refreshTabs();

				refreshRanks();
			}
		}

	}

	// private void checkPermissions() {
	// List<Permission> permissions = new ArrayList<Permission>();
	//
	// Permission p = new Permission();
	// // p.code = "FRV";
	// p.id = Long.valueOf(1);
	// permissions.add(p);
	//
	// SessionController.get().fetchAuthorisation(null, permissions);
	//
	// mRedirect.setTargetHistoryToken(PageType.UpgradePageType.toString());
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		register(EventController.get().addHandlerToSource(FilterEventHandler.TYPE, FilterController.get(), this));
		// register(EventController.get().addHandlerToSource(SessionEventHandler.TYPE, SessionController.get(), this));
		// register(EventController.get().addHandlerToSource(IsAuthorisedEventHandler.TYPE, SessionController.get(), this));
		register(EventController.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));

		boolean hasPermission = SessionController.get().loggedInUserHas(SessionController.FULL_RANK_VIEW_PERMISSION_ID);

		mPager.setVisible(hasPermission);
		mRedirect.setVisible(!hasPermission);
	}
}
