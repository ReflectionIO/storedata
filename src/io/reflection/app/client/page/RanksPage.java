//
//  RanksPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 3 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.page;

import static io.reflection.app.client.controller.FilterController.CATEGORY_KEY;
import static io.reflection.app.client.controller.FilterController.COUNTRY_KEY;
import static io.reflection.app.client.controller.FilterController.DAILY_DATA_KEY;
import static io.reflection.app.client.controller.FilterController.END_DATE_KEY;
import static io.reflection.app.client.controller.FilterController.FREE_LIST_TYPE;
import static io.reflection.app.client.controller.FilterController.GROSSING_LIST_TYPE;
import static io.reflection.app.client.controller.FilterController.OVERALL_LIST_TYPE;
import static io.reflection.app.client.controller.FilterController.PAID_LIST_TYPE;
import static io.reflection.app.client.controller.FilterController.STORE_KEY;
import io.reflection.app.api.core.shared.call.GetAllTopItemsRequest;
import io.reflection.app.api.core.shared.call.GetAllTopItemsResponse;
import io.reflection.app.api.core.shared.call.event.GetAllTopItemsEventHandler;
import io.reflection.app.client.cell.AppRankCell;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.controller.FilterController.Filter;
import io.reflection.app.client.controller.ItemController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.RankController;
import io.reflection.app.client.controller.ServiceConstants;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.handler.FilterEventHandler;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.page.part.RankSidePanel;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.datatypes.RanksGroup;
import io.reflection.app.client.res.Images;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.shared.util.DataTypeHelper;
import io.reflection.app.shared.util.FormattingHelper;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class RanksPage extends Page implements FilterEventHandler, // SessionEventHandler, IsAuthorisedEventHandler,
		NavigationEventHandler, GetAllTopItemsEventHandler {

	private static RanksPageUiBinder uiBinder = GWT.create(RanksPageUiBinder.class);

	interface RanksPageUiBinder extends UiBinder<Widget, RanksPage> {}

	interface RanksPageStyle extends CssResource {
		String red();

		String green();

		String silver();

		String emptyTableContainer();

		String emptyTableHeading();
	}

	public static final int SELECTED_TAB_PARAMETER_INDEX = 0;
	public static final int VIEW_ALL_LENGTH_VALUE = Integer.MAX_VALUE;
	public static final String ALL_TEXT = "Overview / All";

	@UiField RanksPageStyle style;

	interface AllAdminCodeTemplate extends SafeHtmlTemplates {
		AllAdminCodeTemplate INSTANCE = GWT.create(AllAdminCodeTemplate.class);

		@Template(ALL_TEXT + " <span class=\"badge pull-right\">{0}</span>")
		SafeHtml code(Long code);
	}

	@UiField(provided = true) CellTable<RanksGroup> mRanks = new CellTable<RanksGroup>(ServiceConstants.STEP_VALUE, BootstrapGwtCellTable.INSTANCE);

	@UiField RankSidePanel mSidePanel;

	@UiField InlineHyperlink mAll;
	@UiField InlineHyperlink mFree;
	@UiField InlineHyperlink mGrossing;
	@UiField InlineHyperlink mPaid;

	@UiField LIElement mAllItem;
	@UiField LIElement mFreeItem;
	@UiField LIElement mGrossingItem;
	@UiField LIElement mPaidItem;

	@UiField HTMLPanel showMorePanel;
	@UiField Button viewAllBtn;
	@UiField InlineHyperlink redirect;

	private Column<RanksGroup, Rank> mGrossingColumn;
	private Column<RanksGroup, Rank> mFreeColumn;
	private Column<RanksGroup, Rank> mPaidColumn;

	private TextColumn<RanksGroup> mPriceColumn;
	private TextColumn<RanksGroup> mDownloadsColumn;
	private TextColumn<RanksGroup> mRevenueColumn;
	private Column<RanksGroup, SafeHtml> mIapColumn;

	private Map<String, LIElement> mTabs = new HashMap<String, LIElement>();

	private TextHeader mPaidHeader;
	private TextHeader mFreeHeader;
	private TextHeader mGrossingHeader;

	private TextHeader mDownloadsHeader;
	private TextHeader mRevenueHeader;
	private TextHeader mIapHeader;
	private TextHeader mPriceHeader;

	private String selectedTab = OVERALL_LIST_TYPE;

	private boolean showModelPredictions;

	public RanksPage() {
		initWidget(uiBinder.createAndBindUi(this));

		// set the overall tab title (this is because it is modified for admins to contain the gather code)
		mAll.setText(ALL_TEXT);

		showModelPredictions = SessionController.get().isLoggedInUserAdmin();

		createColumns();

		mTabs.put(OVERALL_LIST_TYPE, mAllItem);
		mTabs.put(FREE_LIST_TYPE, mFreeItem);
		mTabs.put(PAID_LIST_TYPE, mPaidItem);
		mTabs.put(GROSSING_LIST_TYPE, mGrossingItem);

		HTMLPanel emptyTableWidget = new HTMLPanel("<h6 class=" + style.emptyTableHeading() + ">No ranking data for filter!</h6>");
		emptyTableWidget.setStyleName(style.emptyTableContainer());
		mRanks.setEmptyTableWidget(emptyTableWidget);
		mRanks.setLoadingIndicator(new Image(Images.INSTANCE.preloader()));

		RankController.get().addDataDisplay(mRanks);
	}

	private void createColumns() {
		TextColumn<RanksGroup> position = new TextColumn<RanksGroup>() {

			@Override
			public String getValue(RanksGroup object) {
				return object.free.position.toString();
			}

		};

		AppRankCell appRankCell = new AppRankCell(showModelPredictions);

		mPaidColumn = new Column<RanksGroup, Rank>(appRankCell) {

			@Override
			public Rank getValue(RanksGroup object) {
				return object.paid;
			}
		};

		mFreeColumn = new Column<RanksGroup, Rank>(appRankCell) {

			@Override
			public Rank getValue(RanksGroup object) {
				return object.free;
			}

		};

		mGrossingColumn = new Column<RanksGroup, Rank>(appRankCell) {

			@Override
			public Rank getValue(RanksGroup object) {
				return object.grossing;
			}
		};

		mPriceColumn = new TextColumn<RanksGroup>() {

			@Override
			public String getValue(RanksGroup object) {
				Rank rank = rankForListType(object);
				return FormattingHelper.getPrice(rank.currency, rank.price.floatValue());
			}

		};
		mDownloadsColumn = new TextColumn<RanksGroup>() {

			@Override
			public String getValue(RanksGroup object) {
				Rank rank = rankForListType(object);
				return FormattingHelper.getIntegerFormattedNumber(rank.downloads);
			}

		};

		mRevenueColumn = new TextColumn<RanksGroup>() {

			@Override
			public String getValue(RanksGroup object) {
				Rank rank = rankForListType(object);
				return FormattingHelper.getCurrencySymbol(rank.currency) + " " + FormattingHelper.getIntegerFormattedNumber(rank.revenue);
			}

		};

		mIapColumn = new Column<RanksGroup, SafeHtml>(new SafeHtmlCell()) {

			private final String IAP_DONT_KNOW_HTML = "<span class=\"icon-help " + style.silver() + "\"></span>";
			private final String IAP_YES_HTML = "<span class=\"icon-ok " + style.green() + "\"></span>";
			private final String IAP_NO_HTML = "<span></span>";

			@Override
			public SafeHtml getValue(RanksGroup object) {
				return SafeHtmlUtils.fromSafeConstant(DataTypeHelper.itemIapState(ItemController.get().lookupItem(rankForListType(object).itemId),
						IAP_YES_HTML, IAP_NO_HTML, IAP_DONT_KNOW_HTML));
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

		mRanks.setWidth("100%", true);
		mRanks.setColumnWidth(position, 25.0, Unit.PCT);
		mRanks.setColumnWidth(mPaidColumn, 100.0, Unit.PCT);
		mRanks.setColumnWidth(mFreeColumn, 100.0, Unit.PCT);
		mRanks.setColumnWidth(mGrossingColumn, 100.0, Unit.PCT);
		mRanks.setColumnWidth(mPriceColumn, 60.0, Unit.PCT);
		mRanks.setColumnWidth(mDownloadsColumn, 60.0, Unit.PCT);
		mRanks.setColumnWidth(mRevenueColumn, 60.0, Unit.PCT);
		mRanks.setColumnWidth(mIapColumn, 20.0, Unit.PCT);

	}

	/**
	 * @param object
	 * @return
	 */
	protected Rank rankForListType(RanksGroup object) {
		Rank rank = object.grossing;

		if (FREE_LIST_TYPE.equals(selectedTab)) {
			rank = object.free;
		} else if (PAID_LIST_TYPE.equals(selectedTab)) {
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
		if (NavigationController.get().getCurrentPage() == PageType.RanksPageType) {
			boolean foundDailyData = false;
			if (name != null
					&& (COUNTRY_KEY.equals(name) || STORE_KEY.equals(name) || CATEGORY_KEY.equals(name) || END_DATE_KEY.equals(name) || (foundDailyData = DAILY_DATA_KEY
							.equals(name)))) {

				if (foundDailyData) {
					mRanks.redraw();
				} else {
					RankController.get().reset();
					RankController.get().fetchTopItems();
					showMorePanel.setVisible(false);
				}

				PageType.RanksPageType.show("view", selectedTab, FilterController.get().asRankFilterString());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.FilterEventHandler#filterParamsChanged(io.reflection.app.client.controller.FilterController.Filter, java.util.Map)
	 */
	@Override
	public void filterParamsChanged(Filter currentFilter, Map<String, ?> previousValues) {
		if (NavigationController.get().getCurrentPage() == PageType.RanksPageType) {

			boolean foundDailyData = false;

			if (previousValues.get(COUNTRY_KEY) != null || previousValues.get(STORE_KEY) != null || previousValues.get(CATEGORY_KEY) != null
					|| previousValues.get(END_DATE_KEY) != null || (foundDailyData = (previousValues.get(DAILY_DATA_KEY) != null))) {

				if (foundDailyData) {
					mRanks.redraw();
				} else {
					RankController.get().reset();
					RankController.get().fetchTopItems();
					showMorePanel.setVisible(false);
				}

				PageType.RanksPageType.show("view", selectedTab, FilterController.get().asRankFilterString());
			}
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
	// redirect.setVisible(true);
	// redirect.setTargetHistoryToken(PageType.LoginPageType.toString());
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
	// redirect.setVisible(false);
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

	private void refreshRanks() {

		if (OVERALL_LIST_TYPE.equals(selectedTab)) {
			removeAllColumns();
			addColumn(mPaidColumn, mPaidHeader);
			addColumn(mFreeColumn, mFreeHeader);
			addColumn(mGrossingColumn, mGrossingHeader);
		} else if (FREE_LIST_TYPE.equals(selectedTab)) {
			removeAllColumns();

			addColumn(mFreeColumn, mFreeHeader);

			addColumn(mPriceColumn, mPriceHeader);
			addColumn(mDownloadsColumn, mDownloadsHeader);
			addColumn(mRevenueColumn, mRevenueHeader);
			addColumn(mIapColumn, mIapHeader);
		} else if (PAID_LIST_TYPE.equals(selectedTab)) {
			removeAllColumns();

			addColumn(mPaidColumn, mPaidHeader);

			addColumn(mPriceColumn, mPriceHeader);
			addColumn(mDownloadsColumn, mDownloadsHeader);
			addColumn(mRevenueColumn, mRevenueHeader);
			addColumn(mIapColumn, mIapHeader);
		} else if (GROSSING_LIST_TYPE.equals(selectedTab)) {
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

		LIElement selected = mTabs.get(selectedTab);

		if (selected != null) {
			selected.addClassName("active");
		}
	}

	@UiHandler("viewAllBtn")
	void onViewAllButtonClicked(ClickEvent event) {
		if (((Button) event.getSource()).isEnabled()) {
			if (mRanks.getVisibleItemCount() == ServiceConstants.STEP_VALUE) {
				mRanks.setVisibleRange(0, VIEW_ALL_LENGTH_VALUE);
				viewAllBtn.setText("View Less Apps");
			} else {
				mRanks.setVisibleRange(0, ServiceConstants.STEP_VALUE);
				viewAllBtn.setText("View All Apps");
			}
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

			if (mRanks.getVisibleItemCount() > 0) {
				showMorePanel.setVisible(true);
			}

			boolean hasPermission = SessionController.get().loggedInUserHas(DataTypeHelper.PERMISSION_FULL_RANK_VIEW_CODE);

			if (hasPermission) {
				redirect.removeFromParent();
				viewAllBtn.getParent().getElement().appendChild(viewAllBtn.getElement());
			} else {
				viewAllBtn.removeFromParent();
				redirect.getParent().getElement().appendChild(redirect.getElement());
			}

			if (current.getAction() == null || !"view".equals(current.getAction())) {
				PageType.RanksPageType.show("view", OVERALL_LIST_TYPE, FilterController.get().asRankFilterString());
			} else {
				String currentFilter = FilterController.get().asRankFilterString();

				if (currentFilter != null && currentFilter.length() > 0) {
					mAll.setTargetHistoryToken(PageType.RanksPageType.asTargetHistoryToken("view", OVERALL_LIST_TYPE, currentFilter));
					mPaid.setTargetHistoryToken(PageType.RanksPageType.asTargetHistoryToken("view", PAID_LIST_TYPE, currentFilter));
					mFree.setTargetHistoryToken(PageType.RanksPageType.asTargetHistoryToken("view", FREE_LIST_TYPE, currentFilter));
					mGrossing.setTargetHistoryToken(PageType.RanksPageType.asTargetHistoryToken("view", GROSSING_LIST_TYPE, currentFilter));
				}

				selectedTab = current.getParameter(SELECTED_TAB_PARAMETER_INDEX);

				refreshTabs();
				refreshRanks();

				if (FREE_LIST_TYPE.equals(selectedTab) || PAID_LIST_TYPE.equals(selectedTab) || GROSSING_LIST_TYPE.equals(selectedTab)) {
					mSidePanel.setDataFilterVisible(false);
				} else {
					mSidePanel.setDataFilterVisible(true);
					selectedTab = OVERALL_LIST_TYPE;
				}

				mSidePanel.updateFromFilter();
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
	// redirect.setTargetHistoryToken(PageType.UpgradePageType.toString());
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
		register(EventController.get().addHandlerToSource(GetAllTopItemsEventHandler.TYPE, RankController.get(), this));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.core.shared.call.event.GetAllTopItemsEventHandler#getAllTopItemsSuccess(io.reflection.app.api.core.shared.call.GetAllTopItemsRequest
	 * , io.reflection.app.api.core.shared.call.GetAllTopItemsResponse)
	 */
	@Override
	public void getAllTopItemsSuccess(GetAllTopItemsRequest input, GetAllTopItemsResponse output) {
		if (output.status.equals(StatusType.StatusTypeSuccess)) {
			showMorePanel.setVisible(true);

			if (SessionController.get().isLoggedInUserAdmin()) {
				if (output.freeRanks != null && output.freeRanks.size() > 0 && output.freeRanks.get(0).code != null) {
					mAll.setHTML(AllAdminCodeTemplate.INSTANCE.code(output.freeRanks.get(0).code));
				} else if (output.paidRanks != null && output.paidRanks.size() > 0 && output.paidRanks.get(0).code != null) {
					mAll.setHTML(AllAdminCodeTemplate.INSTANCE.code(output.paidRanks.get(0).code));
				} else if (output.grossingRanks != null && output.grossingRanks.size() > 0 && output.grossingRanks.get(0).code != null) {
					mAll.setHTML(AllAdminCodeTemplate.INSTANCE.code(output.grossingRanks.get(0).code));
				} else {
					mAll.setText(ALL_TEXT);
				}
			}
		} else {
			RankController.get().updateRowCount(0, true);
			showMorePanel.setVisible(false);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.core.shared.call.event.GetAllTopItemsEventHandler#getAllTopItemsFailure(io.reflection.app.api.core.shared.call.GetAllTopItemsRequest
	 * , java.lang.Throwable)
	 */
	@Override
	public void getAllTopItemsFailure(GetAllTopItemsRequest input, Throwable caught) {
		RankController.get().updateRowCount(0, true);
		showMorePanel.setVisible(false);
	}
}
