//
//  Overview.java
//  storedata
//
//  Created by Stefano Capuzzi on 18 Feb 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part.myapps;

import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.res.Images;

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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author stefanocapuzzi
 * 
 */
public class Overview extends Composite {

	private static OverviewUiBinder uiBinder = GWT.create(OverviewUiBinder.class);

	interface OverviewUiBinder extends UiBinder<Widget, Overview> {}

	@UiField(provided = true) CellTable<FakeData> mApps = new CellTable<Overview.FakeData>(10, BootstrapGwtCellTable.INSTANCE);

	Images images = GWT.create(Images.class);

	private static List<FakeData> fakeData;

	private static class FakeData {
		private Integer rank;
		private ImageResource image;
		private String price;
		private Long downloads;
		private Double revenue;
		private ImageResource iap;

		public FakeData(Integer rank, ImageResource image, String price, Long downloads, Double revenue, ImageResource iap) {

			this.rank = rank;
			this.image = image;
			this.price = price;
			this.downloads = downloads;
			this.revenue = revenue;
			this.iap = iap;
		}
	}

	public Overview() {
		initWidget(uiBinder.createAndBindUi(this));

		fakeData = Arrays.asList(new FakeData(new Integer("22"), images.app1(), new String("Free"), new Long("108"), new Double(213), images.greenTick()),
				new FakeData(new Integer("22"), images.app2(), new String("Free"), new Long("108"), new Double(213), images.greenTick()), new FakeData(
						new Integer("22"), images.app3(), new String("Free"), new Long("108"), new Double(213), images.greenTick()), new FakeData(new Integer(
						"22"), images.app1(), new String("Free"), new Long("108"), new Double(213), images.greenTick()),
				new FakeData(new Integer("22"), images.app2(), new String("Free"), new Long("108"), new Double(213), images.greenTick()), new FakeData(
						new Integer("22"), images.app3(), new String("Free"), new Long("108"), new Double(213), images.greenTick()), new FakeData(new Integer(
						"22"), images.app1(), new String("Free"), new Long("108"), new Double(213), images.greenTick()),
				new FakeData(new Integer("22"), images.app2(), new String("Free"), new Long("108"), new Double(213), images.greenTick()), new FakeData(
						new Integer("22"), images.app3(), new String("Free"), new Long("108"), new Double(213), images.greenTick()), new FakeData(new Integer(
						"22"), images.app1(), new String("Free"), new Long("108"), new Double(213), images.greenTick()),
				new FakeData(new Integer("22"), images.app2(), new String("Free"), new Long("108"), new Double(213), images.greenTick()), new FakeData(
						new Integer("22"), images.app3(), new String("Free"), new Long("108"), new Double(213), images.greenTick()));

		TextColumn<FakeData> columnRank = new TextColumn<FakeData>() {
			@Override
			public String getValue(FakeData object) {
				return object.rank.toString();
			}
		};
		mApps.addColumn(columnRank, "Rank");

		ImageResourceCell imgCell = new ImageResourceCell();
		Column<FakeData, ImageResource> columnAppDetails = new Column<FakeData, ImageResource>(imgCell) {

			@Override
			public ImageResource getValue(FakeData object) {

				return object.image;
			}
		};
		mApps.addColumn(columnAppDetails, "App Details");

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
