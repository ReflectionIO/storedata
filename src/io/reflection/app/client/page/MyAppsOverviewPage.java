//
//  MyAppsOverviewPage.java
//  storedata
//
//  Created by William Shakour (stefanocapuzzi) on 7 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import io.reflection.app.client.cell.MiniAppCell;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.BootstrapGwtDatePicker;
import io.reflection.app.client.res.Images;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.User;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author stefanocapuzzi
 * 
 */
public class MyAppsOverviewPage extends Page {

	private static MyAppsOverviewPageUiBinder uiBinder = GWT.create(MyAppsOverviewPageUiBinder.class);

	interface MyAppsOverviewPageUiBinder extends UiBinder<Widget, MyAppsOverviewPage> {}

	@UiField(provided = true) CellTable<FakeData> mApps = new CellTable<MyAppsOverviewPage.FakeData>(10, BootstrapGwtCellTable.INSTANCE);

	@UiField InlineHyperlink mLinkedAccountsLink;
	@UiField InlineHyperlink mMyAppsLink;

	Images images = GWT.create(Images.class);

	private static List<FakeData> fakeData;

	private static class FakeData {
		private Integer rank;
		private Item appDetails;
		private String price;
		private Long downloads;
		private Double revenue;
		private ImageResource iap;

		public FakeData(Integer rank, Item appDetails, String price, Long downloads, Double revenue, ImageResource iap) {

			this.rank = rank;
			this.appDetails = appDetails;
			this.price = price;
			this.downloads = downloads;
			this.revenue = revenue;
			this.iap = iap;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		User user = SessionController.get().getLoggedInUser();
		
		if (user != null) {
			mLinkedAccountsLink.setTargetHistoryToken("users/linkedaccounts/" + user.id.toString());
			mMyAppsLink.setTargetHistoryToken("users/myapps/" + user.id.toString());
		}
	}

	public MyAppsOverviewPage() {
		initWidget(uiBinder.createAndBindUi(this));

		BootstrapGwtDatePicker.INSTANCE.styles().ensureInjected();

		Item item = new Item();
		item.creatorName = "Mojang";
		item.name = "Minecraft";
		item.smallImage = "http://a1592.phobos.apple.com/us/r30/Purple/v4/94/98/2f/94982fe2-4cec-8a02-fbf6-6fe0851276e2/mzl.nlynfkyw.53x53-50.png";
		item.type = "free";
		item.externalId = "1";

		fakeData = Arrays.asList(new FakeData(new Integer("22"), item, new String("Free"), new Long("108"), new Double(213), images.greenTick()), new FakeData(
				new Integer("22"), item, new String("Free"), new Long("108"), new Double(213), images.greenTick()), new FakeData(new Integer("22"), item,
				new String("Free"), new Long("108"), new Double(213), images.greenTick()), new FakeData(new Integer("22"), item, new String("Free"), new Long(
				"108"), new Double(213), images.greenTick()), new FakeData(new Integer("22"), item, new String("Free"), new Long("108"), new Double(213),
				images.greenTick()), new FakeData(new Integer("22"), item, new String("Free"), new Long("108"), new Double(213), images.greenTick()),
				new FakeData(new Integer("22"), item, new String("Free"), new Long("108"), new Double(213), images.greenTick()), new FakeData(
						new Integer("22"), item, new String("Free"), new Long("108"), new Double(213), images.greenTick()), new FakeData(new Integer("22"),
						item, new String("Free"), new Long("108"), new Double(213), images.greenTick()), new FakeData(new Integer("22"), item, new String(
						"Free"), new Long("108"), new Double(213), images.greenTick()), new FakeData(new Integer("22"), item, new String("Free"), new Long(
						"108"), new Double(213), images.greenTick()), new FakeData(new Integer("22"), item, new String("Free"), new Long("108"),
						new Double(213), images.greenTick()));

		TextColumn<FakeData> columnRank = new TextColumn<FakeData>() {
			@Override
			public String getValue(FakeData object) {
				return object.rank.toString();
			}
		};
		mApps.addColumn(columnRank, "Rank");

		Column<FakeData, Item> PaidColumn = new Column<FakeData, Item>(new MiniAppCell()) {
			@Override
			public Item getValue(FakeData object) {
				return object.appDetails;
			}
		};
		mApps.addColumn(PaidColumn, "App Details");

		TextColumn<FakeData> columnPrice = new TextColumn<FakeData>() {
			@Override
			public String getValue(FakeData object) {
				return object.price.toString();
			}
		};
		mApps.addColumn(columnPrice, "Price");

		TextColumn<FakeData> columnDownloads = new TextColumn<FakeData>() {
			@Override
			public String getValue(FakeData object) {
				return object.downloads.toString();
			}
		};
		mApps.addColumn(columnDownloads, "Downlaods");

		TextColumn<FakeData> columnRevenue = new TextColumn<FakeData>() {
			@Override
			public String getValue(FakeData object) {
				return "$ " + object.revenue.toString();
			}
		};
		mApps.addColumn(columnRevenue, "Revenue");

		ImageResourceCell imgIapCell = new ImageResourceCell();
		Column<FakeData, ImageResource> columnIap = new Column<FakeData, ImageResource>(imgIapCell) {

			@Override
			public ImageResource getValue(FakeData object) {
				return object.iap;
			}
		};
		mApps.addColumn(columnIap, "IAP");

		mApps.setRowData(fakeData);

		/*
		 * ListDataProvider<Rank> te = new ListDataProvider<Rank>(); te.setList(listToWrap);
		 */
	}

}
