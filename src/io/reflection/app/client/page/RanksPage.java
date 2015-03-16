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
import static io.reflection.app.client.helper.FormattingHelper.WHOLE_NUMBER_FORMATTER;
import io.reflection.app.api.core.shared.call.GetAllTopItemsRequest;
import io.reflection.app.api.core.shared.call.GetAllTopItemsResponse;
import io.reflection.app.api.core.shared.call.event.GetAllTopItemsEventHandler;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.cell.AppRankCell;
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
import io.reflection.app.client.helper.FormattingHelper;
import io.reflection.app.client.page.part.RankSidePanel;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.datatypes.RanksGroup;
import io.reflection.app.client.res.Images;
import io.reflection.app.client.res.Styles;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.shared.util.DataTypeHelper;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Cursor;
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
import com.google.gwt.user.client.ui.InlineHTML;
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

		String emptyTableContainer();

		String emptyTableHeading();

	}

	public static final int SELECTED_TAB_PARAMETER_INDEX = 0;
	public static final int VIEW_ALL_LENGTH_VALUE = Integer.MAX_VALUE;
	public static final String ALL_TEXT = "Overview / All";
	public static final String COMING_FROM_PARAMETER = "leaderboard";

	@UiField RanksPageStyle style;

	interface AllAdminCodeTemplate extends SafeHtmlTemplates {
		AllAdminCodeTemplate INSTANCE = GWT.create(AllAdminCodeTemplate.class);

		@Template(ALL_TEXT
				+ " <span style=\"background-color: #474949;color: #fff;float: right;display: inline-block;padding: 0px 3px; margin-left: 20px;\">{0}</span>")
		SafeHtml code(Long code);
	}

	@UiField(provided = true) CellTable<RanksGroup> ranksTable = new CellTable<RanksGroup>(ServiceConstants.STEP_VALUE, BootstrapGwtCellTable.INSTANCE);

	@UiField RankSidePanel mSidePanel;

	@UiField InlineHyperlink allLink;
	@UiField SpanElement overviewAllText;
	@UiField InlineHyperlink freeLink;
	@UiField InlineHyperlink grossingLink;
	@UiField InlineHyperlink paidLink;

	@UiField LIElement allItem;
	@UiField LIElement freeItem;
	@UiField LIElement grossingItem;
	@UiField LIElement paidItem;

	@UiField Button viewAllBtn;
	@UiField SpanElement viewAllSpan;
	@UiField InlineHyperlink redirect;

	private TextColumn<RanksGroup> rankColumn;
	private Column<RanksGroup, Rank> grossingColumn;
	private Column<RanksGroup, Rank> freeColumn;
	private Column<RanksGroup, Rank> paidColumn;
	private TextColumn<RanksGroup> priceColumn;
	private TextColumn<RanksGroup> downloadsColumn;
	private TextColumn<RanksGroup> revenueColumn;
	private Column<RanksGroup, SafeHtml> iapColumn;
	private Column<RanksGroup, SafeHtml> comingSoonColumn;

	private Map<String, LIElement> tabs = new HashMap<String, LIElement>();

	private TextHeader rankHeader;
	private TextHeader paidHeader;
	private TextHeader freeHeader;
	private TextHeader grossingHeader;
	private TextHeader downloadsHeader;
	private TextHeader revenueHeader;
	private TextHeader iapHeader;
	private TextHeader priceHeader;

	private String selectedTab = OVERALL_LIST_TYPE;

	private boolean showAllPredictions;

	public RanksPage() {
		initWidget(uiBinder.createAndBindUi(this));

		// set the overall tab title (this is because it is modified for admins to contain the gather code)
		overviewAllText.setInnerText(ALL_TEXT);

		showAllPredictions = SessionController.get().isLoggedInUserAdmin();

		createColumns();

		tabs.put(OVERALL_LIST_TYPE, allItem);
		tabs.put(FREE_LIST_TYPE, freeItem);
		if (SessionController.get().isLoggedInUserAdmin()) {
			tabs.put(PAID_LIST_TYPE, paidItem);
			tabs.put(GROSSING_LIST_TYPE, grossingItem);
		}

		if (!SessionController.get().isLoggedInUserAdmin()) {
			InlineHTML comingSoon = new InlineHTML("&nbsp;&nbsp;&nbsp;-&nbsp;&nbsp;&nbsp;Coming soon");
			comingSoon.getElement().getStyle().setFontSize(14.0, Unit.PX);
			paidLink.setHTML(paidLink.getText() + comingSoon);
			paidLink.getElement().getStyle().setColor("lightgrey");
			paidLink.getElement().getStyle().setCursor(Cursor.DEFAULT);
			paidItem.getStyle().setCursor(Cursor.DEFAULT);
			grossingLink.setHTML(grossingLink.getText() + comingSoon);
			grossingLink.getElement().getStyle().setColor("lightgrey");
			grossingLink.getElement().getStyle().setCursor(Cursor.DEFAULT);
			grossingItem.getStyle().setCursor(Cursor.DEFAULT);
		}

		HTMLPanel emptyTableWidget = new HTMLPanel("<h6 class=" + style.emptyTableHeading() + ">No ranking data for filter!</h6>");
		emptyTableWidget.setStyleName(style.emptyTableContainer());
		ranksTable.setEmptyTableWidget(emptyTableWidget);

		ranksTable.setLoadingIndicator(new Image(Images.INSTANCE.preloader()));

		RankController.get().addDataDisplay(ranksTable);
	}

	private void createColumns() {
		rankColumn = new TextColumn<RanksGroup>() {

			@Override
			public String getValue(RanksGroup object) {
				return (object.free.position != null) ? object.free.position.toString() : "-";
			}

		};
		rankColumn.setCellStyleNames(Styles.STYLES_INSTANCE.reflectionMainStyle().mhxte6ciA() + " " + Styles.STYLES_INSTANCE.reflectionMainStyle().mhxte6cID());

		AppRankCell appRankCell = new AppRankCell(showAllPredictions);

		paidColumn = new Column<RanksGroup, Rank>(appRankCell) {

			@Override
			public Rank getValue(RanksGroup object) {
				return object.paid;
			}
		};
		paidColumn.setCellStyleNames(Styles.STYLES_INSTANCE.reflectionMainStyle().mhxte6ciA());

		freeColumn = new Column<RanksGroup, Rank>(appRankCell) {

			@Override
			public Rank getValue(RanksGroup object) {
				return object.free;
			}

		};
		freeColumn.setCellStyleNames(Styles.STYLES_INSTANCE.reflectionMainStyle().mhxte6ciA());

		grossingColumn = new Column<RanksGroup, Rank>(appRankCell) {

			@Override
			public Rank getValue(RanksGroup object) {
				return object.grossing;
			}
		};
		grossingColumn.setCellStyleNames(Styles.STYLES_INSTANCE.reflectionMainStyle().mhxte6ciA());

		priceColumn = new TextColumn<RanksGroup>() {

			@Override
			public String getValue(RanksGroup object) {
				Rank rank = rankForListType(object);
				return (rank.currency != null && rank.price != null) ? FormattingHelper.asPriceString(rank.currency, rank.price.floatValue()) : "-";
			}
		};
		priceColumn.setCellStyleNames(Styles.STYLES_INSTANCE.reflectionMainStyle().mhxte6ciA());

		downloadsColumn = new TextColumn<RanksGroup>() {

			@Override
			public String getValue(RanksGroup object) {
				Rank rank = rankForListType(object);
				return (rank.downloads != null) ? WHOLE_NUMBER_FORMATTER.format(rank.downloads) : "-";
			}

		};
		downloadsColumn.setCellStyleNames(Styles.STYLES_INSTANCE.reflectionMainStyle().mhxte6ciA());

		revenueColumn = new TextColumn<RanksGroup>() {

			@Override
			public String getValue(RanksGroup object) {
				Rank rank = rankForListType(object);
				return (rank.currency != null && rank.revenue != null) ? FormattingHelper.asWholeMoneyString(rank.currency, rank.revenue.floatValue()) : "-";
			}

		};
		revenueColumn.setCellStyleNames(Styles.STYLES_INSTANCE.reflectionMainStyle().mhxte6ciA());

		iapColumn = new Column<RanksGroup, SafeHtml>(new SafeHtmlCell()) {

			private final String IAP_DONT_KNOW_HTML = "<span class=\"" + Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBefore() + " "
					+ Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBeforeMinus() + "\"></span>";
			private final String IAP_YES_HTML = "<span class=\"" + Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBefore() + " "
					+ Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBeforeCheck() + "\"></span>";
			private final String IAP_NO_HTML = "<span></span>";

			@Override
			public SafeHtml getValue(RanksGroup object) {
				return SafeHtmlUtils.fromSafeConstant(DataTypeHelper.itemIapState(ItemController.get().lookupItem(rankForListType(object).itemId),
						IAP_YES_HTML, IAP_NO_HTML, IAP_DONT_KNOW_HTML));
			}

		};
		iapColumn.setCellStyleNames(Styles.STYLES_INSTANCE.reflectionMainStyle().mhxte6ciA());

		comingSoonColumn = new Column<RanksGroup, SafeHtml>(new SafeHtmlCell()) {

			@Override
			public SafeHtml getValue(RanksGroup object) {
				return SafeHtmlUtils.fromSafeConstant("<p>Coming Soon</p>");
			}

		};

		rankHeader = new TextHeader("Rank");
		rankHeader.setHeaderStyleNames(Styles.STYLES_INSTANCE.reflectionMainStyle().mhxte6cIF());
		paidHeader = new TextHeader("Paid");
		freeHeader = new TextHeader("Free");
		grossingHeader = new TextHeader("Grossing");
		priceHeader = new TextHeader("Price");
		downloadsHeader = new TextHeader("Downloads");
		revenueHeader = new TextHeader("Revenue");
		iapHeader = new TextHeader("IAP");

		ranksTable.setWidth("100%", true);
		ranksTable.setColumnWidth(comingSoonColumn, 100.0, Unit.PCT);
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
					ranksTable.redraw();
				} else {
					RankController.get().reset();
					RankController.get().fetchTopItems();
					setViewMoreVisible(false);
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
					ranksTable.redraw();
				} else {
					RankController.get().reset();
					RankController.get().fetchTopItems();
					setViewMoreVisible(false);
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
			ranksTable.setColumnWidth(rankColumn, 10.0, Unit.PCT);
			ranksTable.setColumnWidth(freeColumn, 30.0, Unit.PCT);
			ranksTable.setColumnWidth(paidColumn, 30.0, Unit.PCT);
			ranksTable.setColumnWidth(grossingColumn, 30.0, Unit.PCT);
			addColumn(rankColumn, rankHeader);
			addColumn(freeColumn, freeHeader);
			addColumn(paidColumn, paidHeader);
			addColumn(grossingColumn, grossingHeader);
		} else if (FREE_LIST_TYPE.equals(selectedTab)) {
			removeAllColumns();
			if (SessionController.get().isLoggedInUserAdmin()) {
				ranksTable.setColumnWidth(rankColumn, 10.0, Unit.PCT);
				ranksTable.setColumnWidth(freeColumn, 36.7, Unit.PCT);
				ranksTable.setColumnWidth(priceColumn, 13.6, Unit.PCT);
				ranksTable.setColumnWidth(downloadsColumn, 16.7, Unit.PCT);
				ranksTable.setColumnWidth(revenueColumn, 16.7, Unit.PCT);
				ranksTable.setColumnWidth(iapColumn, 6.3, Unit.PCT);
			} else {
				ranksTable.setColumnWidth(rankColumn, 10.0, Unit.PCT);
				ranksTable.setColumnWidth(freeColumn, 42.0, Unit.PCT);
				ranksTable.setColumnWidth(priceColumn, 19.0, Unit.PCT);
				ranksTable.setColumnWidth(downloadsColumn, 19.0, Unit.PCT);
				ranksTable.setColumnWidth(iapColumn, 10.0, Unit.PCT);
			}
			addColumn(rankColumn, rankHeader);
			addColumn(freeColumn, freeHeader);
			addColumn(priceColumn, priceHeader);
			addColumn(downloadsColumn, downloadsHeader);
			if (SessionController.get().isLoggedInUserAdmin()) {
				addColumn(revenueColumn, revenueHeader);
			}
			addColumn(iapColumn, iapHeader);
		} else if (PAID_LIST_TYPE.equals(selectedTab)) {
			if (SessionController.get().isLoggedInUserAdmin()) {
				removeAllColumns();
				ranksTable.setColumnWidth(rankColumn, 10.0, Unit.PCT);
				ranksTable.setColumnWidth(paidColumn, 36.7, Unit.PCT);
				ranksTable.setColumnWidth(priceColumn, 13.6, Unit.PCT);
				ranksTable.setColumnWidth(downloadsColumn, 16.7, Unit.PCT);
				ranksTable.setColumnWidth(revenueColumn, 16.7, Unit.PCT);
				ranksTable.setColumnWidth(iapColumn, 6.3, Unit.PCT);
				addColumn(rankColumn, rankHeader);
				addColumn(paidColumn, paidHeader);
				addColumn(priceColumn, priceHeader);
				addColumn(downloadsColumn, downloadsHeader);
				addColumn(revenueColumn, revenueHeader);
				addColumn(iapColumn, iapHeader);
			}
		} else if (GROSSING_LIST_TYPE.equals(selectedTab)) {
			if (SessionController.get().isLoggedInUserAdmin()) {
				removeAllColumns();
				ranksTable.setColumnWidth(rankColumn, 10.0, Unit.PCT);
				ranksTable.setColumnWidth(grossingColumn, 36.7, Unit.PCT);
				ranksTable.setColumnWidth(priceColumn, 13.6, Unit.PCT);
				ranksTable.setColumnWidth(downloadsColumn, 16.7, Unit.PCT);
				ranksTable.setColumnWidth(revenueColumn, 16.7, Unit.PCT);
				ranksTable.setColumnWidth(iapColumn, 6.3, Unit.PCT);
				addColumn(rankColumn, rankHeader);
				addColumn(grossingColumn, grossingHeader);
				addColumn(priceColumn, priceHeader);
				addColumn(downloadsColumn, downloadsHeader);
				addColumn(revenueColumn, revenueHeader);
				addColumn(iapColumn, iapHeader);
			}
		}

	}

	private void removeColumn(Column<RanksGroup, ?> column) {
		int currentIndex = ranksTable.getColumnIndex(column);

		if (currentIndex != -1) {
			ranksTable.removeColumn(column);
		}
	}

	private void removeAllColumns() {
		removeColumn(rankColumn);
		removeColumn(freeColumn);
		removeColumn(paidColumn);
		removeColumn(grossingColumn);
		removeColumn(priceColumn);
		removeColumn(revenueColumn);
		removeColumn(downloadsColumn);
		removeColumn(iapColumn);
	}

	private void addColumn(Column<RanksGroup, ?> column, TextHeader header) {
		ranksTable.addColumn(column, header);
	}

	private void refreshTabs() {
		for (String key : tabs.keySet()) {
			tabs.get(key).removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isActive());
		}

		LIElement selected = tabs.get(selectedTab);

		if (selected != null) {
			selected.addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isActive());
		}
	}

	@UiHandler("viewAllBtn")
	void onViewAllButtonClicked(ClickEvent event) {
		if (((Button) event.getSource()).isEnabled()) {
			if (ranksTable.getVisibleItemCount() == ServiceConstants.STEP_VALUE) {
				ranksTable.setVisibleRange(0, VIEW_ALL_LENGTH_VALUE);
				viewAllSpan.setInnerText("View Less Apps");
			} else {
				ranksTable.setVisibleRange(0, ServiceConstants.STEP_VALUE);
				viewAllSpan.setInnerText("View All Apps");
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

			if (ranksTable.getVisibleItemCount() > 0) {
				setViewMoreVisible(true);
			}

			boolean hasPermission = SessionController.get().loggedInUserHas(DataTypeHelper.PERMISSION_FULL_RANK_VIEW_CODE);

			if (hasPermission) {
				redirect.removeFromParent();
				viewAllBtn.getParent().getElement().appendChild(viewAllBtn.getElement());
			} else {
				viewAllBtn.removeFromParent();
				redirect.getParent().getElement().appendChild(redirect.getElement());
			}

			if (current.getAction() == null || !NavigationController.VIEW_ACTION_PARAMETER_VALUE.equals(current.getAction())) {
				PageType.RanksPageType.show(NavigationController.VIEW_ACTION_PARAMETER_VALUE, OVERALL_LIST_TYPE, FilterController.get().asRankFilterString());
			} else {
				String currentFilter = FilterController.get().asRankFilterString();

				if (currentFilter != null && currentFilter.length() > 0) {
					allLink.setTargetHistoryToken(PageType.RanksPageType.asTargetHistoryToken(NavigationController.VIEW_ACTION_PARAMETER_VALUE,
							OVERALL_LIST_TYPE, currentFilter));
					freeLink.setTargetHistoryToken(PageType.RanksPageType.asTargetHistoryToken(NavigationController.VIEW_ACTION_PARAMETER_VALUE,
							FREE_LIST_TYPE, currentFilter));
					if (SessionController.get().isLoggedInUserAdmin()) {
						paidLink.setTargetHistoryToken(PageType.RanksPageType.asTargetHistoryToken(NavigationController.VIEW_ACTION_PARAMETER_VALUE,
								PAID_LIST_TYPE, currentFilter));
						grossingLink.setTargetHistoryToken(PageType.RanksPageType.asTargetHistoryToken(NavigationController.VIEW_ACTION_PARAMETER_VALUE,
								GROSSING_LIST_TYPE, currentFilter));
					} else {
						paidLink.setTargetHistoryToken(current.toString());
						grossingLink.setTargetHistoryToken(current.toString());
					}
				}

				selectedTab = current.getParameter(SELECTED_TAB_PARAMETER_INDEX);

				refreshTabs();
				refreshRanks();

				if (SessionController.get().isLoggedInUserAdmin()) {
					if (FREE_LIST_TYPE.equals(selectedTab) || PAID_LIST_TYPE.equals(selectedTab) || GROSSING_LIST_TYPE.equals(selectedTab)) {
						mSidePanel.setDataFilterVisible(false);
					} else {
						mSidePanel.setDataFilterVisible(true);
						selectedTab = OVERALL_LIST_TYPE;
					}
				}

				mSidePanel.updateFromFilter();
			}
		}
	}

	private void setViewMoreVisible(boolean visible) {
		if (viewAllBtn.isAttached()) {
			viewAllBtn.setVisible(visible);
		}
		if (redirect.isAttached()) {
			redirect.setVisible(visible);
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

		register(DefaultEventBus.get().addHandlerToSource(FilterEventHandler.TYPE, FilterController.get(), this));
		// register(EventController.get().addHandlerToSource(SessionEventHandler.TYPE, SessionController.get(), this));
		// register(EventController.get().addHandlerToSource(IsAuthorisedEventHandler.TYPE, SessionController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(GetAllTopItemsEventHandler.TYPE, RankController.get(), this));

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
		if (output.status.equals(StatusType.StatusTypeSuccess) && output.freeRanks != null) {
			setViewMoreVisible(true);

			if (SessionController.get().isLoggedInUserAdmin()) {
				if (output.freeRanks != null && output.freeRanks.size() > 0 && output.freeRanks.get(0).code != null) {
					overviewAllText.setInnerSafeHtml(AllAdminCodeTemplate.INSTANCE.code(output.freeRanks.get(0).code));
				} else if (output.paidRanks != null && output.paidRanks.size() > 0 && output.paidRanks.get(0).code != null) {
					overviewAllText.setInnerSafeHtml(AllAdminCodeTemplate.INSTANCE.code(output.paidRanks.get(0).code));
				} else if (output.grossingRanks != null && output.grossingRanks.size() > 0 && output.grossingRanks.get(0).code != null) {
					overviewAllText.setInnerSafeHtml(AllAdminCodeTemplate.INSTANCE.code(output.grossingRanks.get(0).code));
				} else {
					overviewAllText.setInnerText(ALL_TEXT);
				}
			}
		} else {
			setViewMoreVisible(false);
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
		setViewMoreVisible(false);
	}
}
