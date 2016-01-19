//
//  DeveloperPage.java
//  storedata
//
//  Created by Jamie Gilman on 13 Jan 2016.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.SafeHtmlHeader;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Widget;

import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.cell.MiniDeveloperAppCell;
import io.reflection.app.client.component.LoadingBar;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.helper.AnimationHelper;
import io.reflection.app.client.helper.FormattingHelper;
import io.reflection.app.client.helper.TooltipHelper;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.res.Styles;
import io.reflection.app.client.res.Styles.ReflectionMainStyles;
import io.reflection.app.datatypes.shared.Item;

/**
 * @author Jamie Gilman
 *
 */
public class DeveloperPage extends Page implements NavigationEventHandler {

	private static class ExternalApp {

		public static final String UNKNOWN_VALUE = "-";

		public Item item;

		public String formattedReleaseDate;
		public Date currentVersionReleaseDate;
		public String formattedCurrentVersionReleaseDate;
		public String overallDownloads;
		public String overallPrice;
		public String overallRevenue;
		public String iaps;

		public ExternalApp(Item item, String formattedReleaseDate, Date currentVersionReleaseDate, String formattedCurrentVersionReleaseDate,
				String overallDownloads, String overallPrice, String overallRevenue, String iaps) {

			this.item = item;
			this.formattedReleaseDate = formattedReleaseDate;
			this.currentVersionReleaseDate = currentVersionReleaseDate;
			this.formattedCurrentVersionReleaseDate = formattedCurrentVersionReleaseDate;
			this.overallDownloads = overallDownloads;
			this.overallPrice = overallPrice;
			this.overallRevenue = overallRevenue;
			this.iaps = iaps;
		}
	}

	private static DeveloperPage INSTANCE = null;
	public List<ExternalApp> developerApps = new ArrayList<ExternalApp>();

	private static DeveloperPageUiBinder uiBinder = GWT.create(DeveloperPageUiBinder.class);
	private final ReflectionMainStyles style = Styles.STYLES_INSTANCE.reflectionMainStyle();
	private final LoadingBar loadingBar = new LoadingBar(false);
	private String developerSearchString = "";
	private final int searchLimit = 200;

	interface DeveloperPageUiBinder extends UiBinder<Widget, DeveloperPage> {}

	@UiField(provided = true) CellTable<ExternalApp> developerAppsTable = new CellTable<ExternalApp>(200, BootstrapGwtCellTable.INSTANCE);
	@UiField HeadingElement pageTitleDeveloperName;
	@UiField HeadingElement numberOfApps;
	@UiField Anchor appStoreLink;

	private final SafeHtmlHeader appDetailsHeader = new SafeHtmlHeader(SafeHtmlUtils.fromTrustedString("App Details " + AnimationHelper.getSorterSvg()));
	private final SafeHtmlHeader releaseDateHeader = new SafeHtmlHeader(SafeHtmlUtils.fromTrustedString("Released " + AnimationHelper.getSorterSvg()));
	private final SafeHtmlHeader currentVersionReleaseDateHeader = new SafeHtmlHeader(
			SafeHtmlUtils.fromTrustedString("Updated " + AnimationHelper.getSorterSvg()));
	private final SafeHtmlHeader priceHeader = new SafeHtmlHeader(SafeHtmlUtils.fromTrustedString("Price " + AnimationHelper.getSorterSvg()));
	private final SafeHtmlHeader downloadsHeader = new SafeHtmlHeader(SafeHtmlUtils.fromTrustedString("Downloads"));
	private final SafeHtmlHeader revenueHeader = new SafeHtmlHeader(SafeHtmlUtils.fromTrustedString("Revenue"));
	private final SafeHtmlHeader iapHeader = new SafeHtmlHeader(
			SafeHtmlUtils.fromTrustedString("<span class=\"js-tooltip\" data-tooltip=\"In App Purchases\">IAP</span>"));

	private Column<ExternalApp, Item> columnAppDetails;
	private Column<ExternalApp, SafeHtml> columnReleaseDate;
	private Column<ExternalApp, SafeHtml> columnCurrentVersionReleaseDate;
	private Column<ExternalApp, SafeHtml> columnPrice;
	private Column<ExternalApp, SafeHtml> columnDownloads;
	private Column<ExternalApp, SafeHtml> columnRevenue;
	private Column<ExternalApp, SafeHtml> columnIap;

	public DeveloperPage() {
		initWidget(uiBinder.createAndBindUi(this));
		INSTANCE = this;
		createColumns();
		developerAppsTable.setLoadingIndicator(AnimationHelper.getMyAppsLoadingIndicator(4));
		developerAppsTable.getTableLoadingSection().addClassName(style.tableBodyLoading());
		developerAppsTable.setRowCount(0, true);
	}

	@Override
	protected void onAttach() {
		super.onAttach();

		register(DefaultEventBus.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		// remove content
		pageTitleDeveloperName.setInnerText("");
		numberOfApps.setInnerText("");
		appStoreLink.setVisible(false);
		loadingBar.reset();
	}

	@Override
	public void navigationChanged(Stack previous, Stack current) {

		loadingBar.show("Finding apps...");

		developerSearchString = current.getAction();
		if (developerSearchString != null) {
			search(developerSearchString, searchLimit);
		}
		// removePageContent();
		// comingPage = current.getParameter(0);
		// previousFilter = current.getParameter(1);

		// revenueLink.setTargetHistoryToken(PageType.ItemPageType.asTargetHistoryToken(NavigationController.VIEW_ACTION_PARAMETER_VALUE, displayingAppId,
		// REVENUE_CHART_TYPE, comingPage, previousFilter));
	}

	/**
	 * @param sortAscending
	 */
	public void sortByAppDetails(final boolean sortAscending) {
		Collections.sort(developerApps, new Comparator<ExternalApp>() {

			@Override
			public int compare(ExternalApp o1, ExternalApp o2) {
				return (sortAscending ? o1.item.name.compareTo(o2.item.name) : o2.item.name.compareTo(o1.item.name));
			}
		});
	}

	/**
	 * @param sortAscending
	 */
	public void sortByPrice(final boolean sortAscending) {
		Collections.sort(developerApps, new Comparator<ExternalApp>() {

			@Override
			public int compare(ExternalApp o1, ExternalApp o2) {
				int res = 0;
				if (!o1.overallPrice.equals(o2.overallPrice)) {
					if (o1.overallPrice.equals("-")) {
						res = 1;
					} else if (o2.overallPrice.equals("-")) {
						res = -1;
					} else if (o1.overallPrice.equalsIgnoreCase("free")) {
						res = 1;
					} else if (o2.overallPrice.equalsIgnoreCase("free")) {
						res = -1;
					} else {
						res = (Float.parseFloat(o1.overallPrice.replaceAll(",|\\.|\\$|€|¥|£", "").trim()) < Float
								.parseFloat(o2.overallPrice.replaceAll(",|\\.|\\$|€|¥|£", "").trim()) ? 1 : -1);
					}
				}
				return (sortAscending ? res : -res);
			}
		});
	}

	/**
	 * @param sortAscending
	 */
	public void sortByReleaseDate(final boolean sortAscending) {
		Collections.sort(developerApps, new Comparator<ExternalApp>() {

			@Override
			public int compare(ExternalApp o1, ExternalApp o2) {
				return (sortAscending ? o1.item.added.compareTo(o2.item.added) : o2.item.added.compareTo(o1.item.added));
			}
		});
	}

	/**
	 * @param sortAscending
	 */
	public void sortByVersionReleaseDate(final boolean sortAscending) {
		Collections.sort(developerApps, new Comparator<ExternalApp>() {

			@Override
			public int compare(ExternalApp o1, ExternalApp o2) {
				return (sortAscending ? o1.currentVersionReleaseDate.compareTo(o2.currentVersionReleaseDate)
						: o2.currentVersionReleaseDate.compareTo(o1.currentVersionReleaseDate));
			}
		});
	}

	private void createColumns() {

		final ListHandler<ExternalApp> columnSortHandler = new ListHandler<ExternalApp>(developerApps) {
			/*
			 * (non-Javadoc)
			 *
			 * @see com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler#onColumnSort(com.google.gwt.user.cellview.client.ColumnSortEvent)
			 */
			@Override
			public void onColumnSort(ColumnSortEvent event) {
				super.onColumnSort(event);
				if (!columnAppDetails.isDefaultSortAscending()) {
					columnAppDetails.setDefaultSortAscending(true);
				}

				appDetailsHeader.setHeaderStyleNames(style.canBeSorted() + " " + style.mhxte6cIF());
				releaseDateHeader.setHeaderStyleNames(style.canBeSorted());
				currentVersionReleaseDateHeader.setHeaderStyleNames(style.canBeSorted());
				priceHeader.setHeaderStyleNames(style.canBeSorted());

				if (event.getColumn() == columnAppDetails) {
					sortByAppDetails(event.isSortAscending());
					appDetailsHeader.setHeaderStyleNames(
							style.canBeSorted() + " " + (event.isSortAscending() ? style.isAscending() : style.isDescending()) + " " + style.mhxte6cIF());
				} else if (event.getColumn() == columnPrice) {
					sortByPrice(event.isSortAscending());
					priceHeader.setHeaderStyleNames(style.canBeSorted() + " " + (event.isSortAscending() ? style.isAscending() : style.isDescending()));
				} else if (event.getColumn() == columnReleaseDate) {
					sortByReleaseDate(event.isSortAscending());
					releaseDateHeader.setHeaderStyleNames(style.canBeSorted() + " " + (event.isSortAscending() ? style.isAscending() : style.isDescending()));
				} else if (event.getColumn() == columnCurrentVersionReleaseDate) {
					sortByVersionReleaseDate(event.isSortAscending());
					currentVersionReleaseDateHeader
							.setHeaderStyleNames(style.canBeSorted() + " " + (event.isSortAscending() ? style.isAscending() : style.isDescending()));
				}
				developerAppsTable.setRowData(0, developerApps);

				TooltipHelper.updateHelperTooltip();
			}
		};

		developerAppsTable.addColumnSortHandler(columnSortHandler);

		final SafeHtml loaderInline = AnimationHelper.getLoaderInlineSafeHTML();

		// App Details Column
		columnAppDetails = new Column<ExternalApp, Item>(new MiniDeveloperAppCell()) {
			@Override
			public Item getValue(ExternalApp object) {
				return object.item;
			}
		};

		columnAppDetails.setCellStyleNames(style.mhxte6ciA() + " " + style.mhxte6cID());
		appDetailsHeader.setHeaderStyleNames(style.canBeSorted() + " " + style.mhxte6cIF());
		developerAppsTable.addColumn(columnAppDetails, appDetailsHeader);
		columnAppDetails.setSortable(true);

		// Release Date Column
		columnReleaseDate = new Column<ExternalApp, SafeHtml>(new SafeHtmlCell()) {
			@Override
			public SafeHtml getValue(ExternalApp object) {
				if (object.formattedReleaseDate != null) return SafeHtmlUtils.fromSafeConstant(object.formattedReleaseDate);
				else return loaderInline;
			}
		};
		columnReleaseDate.setCellStyleNames(style.mhxte6ciA());
		releaseDateHeader.setHeaderStyleNames(style.canBeSorted());
		developerAppsTable.addColumn(columnReleaseDate, releaseDateHeader);
		columnReleaseDate.setSortable(true);

		// Current Version Release Date Column
		columnCurrentVersionReleaseDate = new Column<ExternalApp, SafeHtml>(new SafeHtmlCell()) {
			@Override
			public SafeHtml getValue(ExternalApp object) {
				if (object.formattedCurrentVersionReleaseDate != null) return SafeHtmlUtils.fromSafeConstant(object.formattedCurrentVersionReleaseDate);
				else return loaderInline;
			}
		};
		columnCurrentVersionReleaseDate.setCellStyleNames(style.mhxte6ciA());
		currentVersionReleaseDateHeader.setHeaderStyleNames(style.canBeSorted());
		developerAppsTable.addColumn(columnCurrentVersionReleaseDate, currentVersionReleaseDateHeader);
		columnCurrentVersionReleaseDate.setSortable(true);

		// Price Column
		columnPrice = new Column<ExternalApp, SafeHtml>(new SafeHtmlCell()) {
			@Override
			public SafeHtml getValue(ExternalApp object) {
				if (object.overallPrice != null) return SafeHtmlUtils.fromSafeConstant(object.overallPrice);
				else return loaderInline;
			}
		};
		columnPrice.setCellStyleNames(style.mhxte6ciA());
		priceHeader.setHeaderStyleNames(style.canBeSorted());
		developerAppsTable.addColumn(columnPrice, priceHeader);
		columnPrice.setSortable(true);

		// Downloads Column
		columnDownloads = new Column<ExternalApp, SafeHtml>(new SafeHtmlCell()) {
			@Override
			public SafeHtml getValue(ExternalApp object) {
				return SafeHtmlUtils.fromTrustedString(
						"<span class=\"js-tooltip js-tooltip--info tooltip--info js-tooltip--right js-tooltip--right--no-pointer-padding\" data-tooltip=\"Coming Soon\"></span>");
			}
		};
		columnDownloads.setCellStyleNames(style.mhxte6ciA() + " " + style.columnHiddenMobile());
		downloadsHeader.setHeaderStyleNames(style.columnHiddenMobile());
		developerAppsTable.addColumn(columnDownloads, downloadsHeader);

		// Revenue Column
		columnRevenue = new Column<ExternalApp, SafeHtml>(new SafeHtmlCell()) {
			@Override
			public SafeHtml getValue(ExternalApp object) {
				return SafeHtmlUtils.fromTrustedString(
						"<span class=\"js-tooltip js-tooltip--info tooltip--info js-tooltip--right js-tooltip--right--no-pointer-padding\" data-tooltip=\"Coming Soon\"></span>");
			}
		};
		columnRevenue.setCellStyleNames(style.mhxte6ciA() + " " + style.columnHiddenMobile());
		revenueHeader.setHeaderStyleNames(style.columnHiddenMobile());
		developerAppsTable.addColumn(columnRevenue, revenueHeader);

		// IAP Column
		columnIap = new Column<ExternalApp, SafeHtml>(new SafeHtmlCell()) {

			@Override
			public SafeHtml getValue(ExternalApp object) {
				return SafeHtmlUtils.fromTrustedString(
						"<span class=\"js-tooltip js-tooltip--info tooltip--info js-tooltip--right js-tooltip--right--no-pointer-padding\" data-tooltip=\"Coming Soon\"></span>");
			}

		};
		columnIap.setCellStyleNames(style.mhxte6ciA() + " " + style.columnHiddenMobile());
		iapHeader.setHeaderStyleNames(style.columnHiddenMobile());
		developerAppsTable.addColumn(columnIap, iapHeader);

		developerAppsTable.addColumnStyleName(0, style.developerAppDetailsColumn());
		developerAppsTable.addColumnStyleName(1, style.developerAppReleaseDateColumn());
		developerAppsTable.addColumnStyleName(2, style.developerAppVersionReleaseDateColumn());
		developerAppsTable.addColumnStyleName(3, style.developerAppPriceColumn());
		developerAppsTable.addColumnStyleName(4, style.developerAppDownloadsColumn() + " " + style.columnHiddenMobile());
		developerAppsTable.addColumnStyleName(5, style.developerAppRevenueColumn() + " " + style.columnHiddenMobile());
		developerAppsTable.addColumnStyleName(6, style.developerAppIapColumn() + " " + style.columnHiddenMobile());
	}

	private void populateDeveloperAppsTable(JsonArray jarray) {
		developerApps.clear();
		// //if this creates a memory leak, instead clear the array
		// developerApps = new ArrayList<ExternalApp>(jarray.size());

		developerAppsTable.setRowCount(jarray.size(), true);

		final String developerNameFromFirstResult = jarray.get(0).getAsJsonObject().get("artistName").getAsString();
		if (developerNameFromFirstResult != null) {
			pageTitleDeveloperName.setInnerText(developerNameFromFirstResult);
		}

		final JsonElement appStoreLinkFromFirstResult = jarray.get(0).getAsJsonObject().get("artistViewUrl");
		if (appStoreLinkFromFirstResult != null) {
			appStoreLink.setHref(appStoreLinkFromFirstResult.getAsString());
			appStoreLink.setTarget("_blank");
			appStoreLink.setVisible(true);
		} else {
			appStoreLink.setVisible(false);
		}

		for (final JsonElement jsonElement : jarray) {
			final JsonObject returnedDataItem = jsonElement.getAsJsonObject();
			final Item appItem = new Item();
			String appPrice = "";
			Date appCurrentVersionReleaseDate = new Date();
			String formattedReleaseDate = "";
			String appCurrentVersionReleaseDateFormatted = "";
			if (returnedDataItem.get("artistName") != null) {
				appItem.creatorName(returnedDataItem.get("artistName").getAsString());
			}
			if (returnedDataItem.get("trackId").getAsString() != null) {
				appItem.internalId(returnedDataItem.get("trackId").getAsString());
			}
			if (returnedDataItem.get("trackName") != null) {
				appItem.name(returnedDataItem.get("trackName").getAsString());
			}
			if (returnedDataItem.get("artworkUrl100") != null) {
				appItem.smallImage(returnedDataItem.get("artworkUrl100").getAsString());
			}
			if (returnedDataItem.get("formattedPrice") != null) {
				appPrice = returnedDataItem.get("formattedPrice").getAsString();
			}
			final JsonElement releaseDate = returnedDataItem.get("releaseDate");
			if (releaseDate != null) {
				try {
					formattedReleaseDate = FormattingHelper.convertITunesDateToDefaultFormat(releaseDate.getAsString());
					final Date parsedDate = DateTimeFormat.getFormat("yyyy-MM-ddTHH:mm:ssZ").parse(releaseDate.getAsString());
					appItem.added = parsedDate;
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
			final JsonElement versionReleaseDate = returnedDataItem.get("currentVersionReleaseDate");
			if (versionReleaseDate != null) {
				try {
					appCurrentVersionReleaseDateFormatted = FormattingHelper.convertITunesDateToDefaultFormat(versionReleaseDate.getAsString());
					final Date parsedDate = DateTimeFormat.getFormat("yyyy-MM-ddTHH:mm:ssZ").parse(versionReleaseDate.getAsString());
					appCurrentVersionReleaseDate = parsedDate;
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
			if (returnedDataItem.get("averageUserRating") != null) {
				appItem.rating = returnedDataItem.get("averageUserRating").getAsFloat();
			}

			developerApps.add(new ExternalApp(appItem, formattedReleaseDate, appCurrentVersionReleaseDate, appCurrentVersionReleaseDateFormatted, "-", appPrice,
					"-", "-"));
		}

		appDetailsHeader.setHeaderStyleNames(style.canBeSorted() + " " + style.isAscending() + " " + style.mhxte6cIF());
		columnAppDetails.setDefaultSortAscending(false);

		String numberOfAppsOutput = String.valueOf(jarray.size());
		if (jarray.size() == searchLimit) {
			numberOfAppsOutput = String.valueOf((searchLimit - 1)) + "+";
		}
		numberOfApps.setInnerText("(" + numberOfAppsOutput + " apps in the UK store)");
		developerAppsTable.setRowData(0, developerApps);

		loadingBar.hide(true);
	}

	private native void search(String searchString, int searchLimit) /*-{
		$wnd.$('#ref-iTunesSearchDeveloperApps').remove();
		$wnd
				.$('body')
				.append(
						$wnd
								.$("<script>")
								.attr("id", "ref-iTunesSearchDeveloperApps")
								.attr(
										"src",
										"https://itunes.apple.com/gb/lookup?id="
												+ searchString
												+ "&entity=software&limit="
												+ searchLimit
												+ "&callback=handleDeveloperAppsSearch"));
	}-*/;

	public static void processDeveloperAppsSearchResponse(String response) {
		boolean isValidResult = false;
		final JsonElement jelement = new JsonParser().parse(response);
		if (jelement != null) {
			final JsonObject jobject = jelement.getAsJsonObject();
			if (jobject != null) {
				final JsonArray jarray = jobject.getAsJsonArray("results");
				if (jarray != null && jarray.size() > 0) {
					final JsonArray filteredArray = new JsonArray();
					for (int i = 0; i < jarray.size(); i++) {
						final JsonElement trackId = jarray.get(i).getAsJsonObject().get("trackId"); // make sure trackId exists (first result doesn't have one)
						if (trackId != null) {
							if (jarray.get(i).getAsJsonObject().get("artistId").getAsString() == INSTANCE.developerSearchString) {
								// for different accounts
								filteredArray.add(jarray.get(i));
							}
						}
					}
					if (filteredArray.size() > 0) {
						INSTANCE.populateDeveloperAppsTable(filteredArray);
						isValidResult = true;
					}
				}
			}
		}

		if (!isValidResult) {
			final String errorMessage = "No apps found...";
			INSTANCE.loadingBar.hide(errorMessage, false);
		}
	}

	public static native void exportDeveloperAppsSearchResponseHandler() /*-{
		$wnd.processDeveloperAppsSearchResponse = $entry(@io.reflection.app.client.page.DeveloperPage::processDeveloperAppsSearchResponse(Ljava/lang/String;));
	}-*/;
}