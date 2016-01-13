//
//  DeveloperPage.java
//  storedata
//
//  Created by Jamie Gilman on 13 Jan 2016.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SafeHtmlHeader;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

import io.reflection.app.client.cell.MiniAppCell;
import io.reflection.app.client.controller.ServiceConstants;
import io.reflection.app.client.helper.AnimationHelper;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.res.Styles;
import io.reflection.app.client.res.Styles.ReflectionMainStyles;
import io.reflection.app.datatypes.shared.Item;

/**
 * @author Jamie Gilman
 *
 */
public class DeveloperPage extends Page {

	private static class ExternalApp {

		public static final String UNKNOWN_VALUE = "-";

		public Item item;

		public String overallDownloads;
		public String overallPrice;
		public String overallRevenue;
		public String iaps;

		public ExternalApp(Item item, String overallDownloads, String overallPrice, String overallRevenue, String iaps) {

			this.item = item;
			this.overallDownloads = overallDownloads;
			this.overallPrice = overallPrice;
			this.overallRevenue = overallRevenue;
			this.iaps = iaps;

			// this.releaseDate = releaseDate - try using item.added;
		}
	}

	private static DeveloperPage INSTANCE = null;
	public List<ExternalApp> developerApps = new ArrayList<ExternalApp>();

	private static DeveloperPageUiBinder uiBinder = GWT.create(DeveloperPageUiBinder.class);
	private final ReflectionMainStyles style = Styles.STYLES_INSTANCE.reflectionMainStyle();

	interface DeveloperPageUiBinder extends UiBinder<Widget, DeveloperPage> {}

	@UiField(provided = true) CellTable<ExternalApp> developerAppsTable = new CellTable<ExternalApp>(ServiceConstants.STEP_VALUE,
			BootstrapGwtCellTable.INSTANCE);

	private final SafeHtmlHeader appDetailsHeader = new SafeHtmlHeader(SafeHtmlUtils.fromTrustedString("App Details " + AnimationHelper.getSorterSvg()));
	private final SafeHtmlHeader priceHeader = new SafeHtmlHeader(SafeHtmlUtils.fromTrustedString("Price " + AnimationHelper.getSorterSvg()));
	private final SafeHtmlHeader downloadsHeader = new SafeHtmlHeader(SafeHtmlUtils.fromTrustedString("Downloads " + AnimationHelper.getSorterSvg()));
	private final SafeHtmlHeader revenueHeader = new SafeHtmlHeader(SafeHtmlUtils.fromTrustedString("Revenue " + AnimationHelper.getSorterSvg()));
	private final SafeHtmlHeader iapHeader = new SafeHtmlHeader(
			SafeHtmlUtils.fromTrustedString("<span class=\"js-tooltip\" data-tooltip=\"In App Purchases\">IAP</span>"));

	private Column<ExternalApp, Item> columnAppDetails;
	private Column<ExternalApp, SafeHtml> columnPrice;
	private Column<ExternalApp, SafeHtml> columnDownloads;
	private Column<ExternalApp, SafeHtml> columnRevenue;
	private Column<ExternalApp, SafeHtml> columnIap;

	public DeveloperPage() {
		initWidget(uiBinder.createAndBindUi(this));
		INSTANCE = this;
		createColumns();
		search("ustwo");
	}

	private void createColumns() {

		final SafeHtml loaderInline = AnimationHelper.getLoaderInlineSafeHTML();

		columnAppDetails = new Column<ExternalApp, Item>(new MiniAppCell()) {
			@Override
			public Item getValue(ExternalApp object) {
				return object.item;
			}
		};

		columnAppDetails.setCellStyleNames(style.mhxte6ciA() + " " + style.mhxte6cID());
		appDetailsHeader.setHeaderStyleNames(style.canBeSorted() + " " + style.mhxte6cIF());
		developerAppsTable.addColumn(columnAppDetails, appDetailsHeader);
		// columnAppDetails.setSortable(true);

		columnPrice = new Column<ExternalApp, SafeHtml>(new SafeHtmlCell()) {
			@Override
			public SafeHtml getValue(ExternalApp object) {
				if (object.overallPrice != null) return SafeHtmlUtils.fromSafeConstant(object.overallPrice);
				else return loaderInline;
			}
		};
		columnPrice.setCellStyleNames(style.mhxte6ciA() + " " + style.columnHiddenMobile());
		priceHeader.setHeaderStyleNames(style.canBeSorted() + " " + style.columnHiddenMobile());
		developerAppsTable.addColumn(columnPrice, priceHeader);
		// columnPrice.setSortable(true);

		columnDownloads = new Column<ExternalApp, SafeHtml>(new SafeHtmlCell()) {
			@Override
			public SafeHtml getValue(ExternalApp object) {
				return SafeHtmlUtils.fromTrustedString(
						"<span class=\"js-tooltip js-tooltip--info tooltip--info js-tooltip--right js-tooltip--right--no-pointer-padding\" data-tooltip=\"Coming Soon\"></span>");
			}
		};
		columnDownloads.setCellStyleNames(style.mhxte6ciA());
		downloadsHeader.setHeaderStyleNames(style.canBeSorted());
		developerAppsTable.addColumn(columnDownloads, downloadsHeader);
		// columnDownloads.setSortable(true);

		columnRevenue = new Column<ExternalApp, SafeHtml>(new SafeHtmlCell()) {
			@Override
			public SafeHtml getValue(ExternalApp object) {
				return SafeHtmlUtils.fromTrustedString(
						"<span class=\"js-tooltip js-tooltip--info tooltip--info js-tooltip--right js-tooltip--right--no-pointer-padding\" data-tooltip=\"Coming Soon\"></span>");
			}
		};
		columnRevenue.setCellStyleNames(style.mhxte6ciA());
		revenueHeader.setHeaderStyleNames(style.canBeSorted());
		developerAppsTable.addColumn(columnRevenue, revenueHeader);
		columnRevenue.setSortable(true);

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

		developerAppsTable.addColumnStyleName(0, style.appDetailsColumn());
		developerAppsTable.addColumnStyleName(1, style.priceColumn() + " " + style.columnHiddenMobile());
		developerAppsTable.addColumnStyleName(2, style.downloadsColumn());
		developerAppsTable.addColumnStyleName(3, style.revenueColumn());
		developerAppsTable.addColumnStyleName(4, style.iapColumn() + " " + style.columnHiddenMobile());
	}

	private void populateDeveloperAppsTable(JsonArray jarray) {

		// Window.alert(String.valueOf(jarray.size()));
		for (final JsonElement jsonElement : jarray) {
			final JsonObject returnedDataItem = jsonElement.getAsJsonObject();
			final Item appItem = new Item();
			String appPrice = "";
			if (returnedDataItem.get("artistName") != null) {
				appItem.creatorName(returnedDataItem.get("artistName").getAsString());
			}
			if (returnedDataItem.get("trackName") != null) {
				appItem.name(returnedDataItem.get("trackName").getAsString());
			}
			if (returnedDataItem.get("artworkUrl100") != null) {
				appItem.smallImage(returnedDataItem.get("artworkUrl100").getAsString());
			}
			if (returnedDataItem.get("price") != null) {
				appPrice = returnedDataItem.get("price").getAsString();
			}

			developerApps.add(new ExternalApp(appItem, "-", appPrice, "-", "-"));
		}

		appDetailsHeader.setHeaderStyleNames(style.canBeSorted() + " " + style.isAscending() + " " + style.mhxte6cIF());
		columnAppDetails.setDefaultSortAscending(false);

		developerAppsTable.setRowData(0, developerApps);
	}

	private native void search(String searchString) /*-{
		$wnd.$('#ref-iTunesSearchDeveloperApps').remove();
		$wnd
				.$('body')
				.append(
						$wnd
								.$("<script>")
								.attr("id", "ref-iTunesSearchDeveloperApps")
								.attr(
										"src",
										"https://itunes.apple.com/search?term="
												+ searchString
												+ "&media=software&limit=17&callback=handleDeveloperAppsSearchFromHeader"));
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
						if (jarray.get(i).getAsJsonObject().get("artistName").toString().contains("ustwo")) { // this needs more checking for nulls
							filteredArray.add(jarray.get(i));
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
			Window.alert("No results");
		}
	}

	public static native void exportDeveloperAppsSearchResponseHandler() /*-{
																			$wnd.processDeveloperAppsSearchResponse = $entry(@io.reflection.app.client.page.DeveloperPage::processDeveloperAppsSearchResponse(Ljava/lang/String;));
																			}-*/;
}