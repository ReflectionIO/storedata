//
//  CalibrationSummaryPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 8 Mar 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.admin;

//import io.reflection.app.api.admin.shared.call.GetCalibrationSummaryRequest;
//import io.reflection.app.api.admin.shared.call.GetCalibrationSummaryResponse;
//import io.reflection.app.api.admin.shared.call.event.GetCalibrationSummaryEventHandler;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.cell.AppRankCell;
import io.reflection.app.client.controller.CalibrationSummaryController;
import io.reflection.app.client.controller.CategoryController;
import io.reflection.app.client.controller.CountryController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.helper.ColorHelper;
import io.reflection.app.client.helper.FormattingHelper;
import io.reflection.app.client.highcharts.Chart;
import io.reflection.app.client.highcharts.ChartHelper;
import io.reflection.app.client.highcharts.ChartHelper.XDataType;
import io.reflection.app.client.highcharts.ChartHelper.YDataType;
import io.reflection.app.client.page.Page;
import io.reflection.app.client.part.BootstrapGwtCellList;
import io.reflection.app.client.part.Breadcrumbs;
import io.reflection.app.client.res.Styles;
import io.reflection.app.datatypes.shared.CalibrationSummary;
import io.reflection.app.datatypes.shared.FormType;
import io.reflection.app.datatypes.shared.ListPropertyType;
import io.reflection.app.datatypes.shared.ListTypeType;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.datatypes.shared.SimpleModelRun;
import io.reflection.app.shared.util.DataTypeHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.spacehopperstudios.utility.StringUtils;

/**
 * @author William Shakour (billy1380)
 *
 */
public class CalibrationSummaryPage extends Page implements NavigationEventHandler {

	private static CalibrationSummaryPagePageUiBinder uiBinder = GWT.create(CalibrationSummaryPagePageUiBinder.class);

	interface CalibrationSummaryPagePageUiBinder extends UiBinder<Widget, CalibrationSummaryPage> {}

	private static final int FEED_FETCH_ID_PARAMETER_INDEX = 0;

	@UiField CheckBox showMisses;
	@UiField CheckBox showTop;
	@UiField CheckBox showTail;
	@UiField Breadcrumbs breadcrumbs;

	private AppRankCell prototype = new AppRankCell().useFilter(false);
	@UiField(provided = true) CellList<Rank> hitsCellList = new CellList<Rank>(prototype, BootstrapGwtCellList.INSTANCE);
	@UiField(provided = true) CellList<Rank> missesCellList = new CellList<Rank>(prototype, BootstrapGwtCellList.INSTANCE);

	// @UiField GChart chart;
	@UiField(provided = true) Chart chart = new Chart(XDataType.RankingXAxisDataType, YDataType.DownloadsYAxisDataType);

	@UiField Element title;
	@UiField Element description;

	private CalibrationSummary summary;
	private ListTypeType summaryListType;
	private ListPropertyType summaryListProperty;
	private FormType summaryFormType;
	private String feedFetchParam;
	private ListDataProvider<Rank> hitsProvider = new ListDataProvider<Rank>();
	private ListDataProvider<Rank> missesProvider = new ListDataProvider<Rank>();
	private String currency;

	@UiHandler({ "showMisses", "showTop", "showTail" })
	void showMissesValueChanged(ValueChangeEvent<Boolean> event) {
		updateCheckBoxes();
	}

	private void updateCheckBoxes() {
		if (showTail.getValue().booleanValue()) {
			chart.setXAxisMax(200);
		} else {
			chart.resetXAxisMax();
		}
		if (showTop.getValue().booleanValue()) {
			chart.setXAxisMin(0);
		} else {
			chart.resetXAxisMin();
		}
		chart.setSeriesVisible("misses", showMisses.getValue().booleanValue());
	}

	public CalibrationSummaryPage() {
		initWidget(uiBinder.createAndBindUi(this));

		// setupChart(chart);
		hitsCellList.setPageSize(Integer.MAX_VALUE);
		missesCellList.setPageSize(Integer.MAX_VALUE);

		hitsCellList.setEmptyListWidget(new HTMLPanel("No items found"));
		missesCellList.setEmptyListWidget(new HTMLPanel("No items found"));

		missesCellList.getRowContainer().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().mhxte6ciA());
		hitsCellList.getRowContainer().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().mhxte6ciA());

		hitsProvider.addDataDisplay(hitsCellList);
		missesProvider.addDataDisplay(missesCellList);

	}

	private void drawChartData() {

		if (summary != null) {

			if (summary.simpleModelRun != null) {
				int start = (showTop.getValue() == Boolean.TRUE ? 1 : summary.hits.get(0).position.intValue());
				int end = (showTail.getValue() == Boolean.TRUE ? 200 : summary.hits.get(summary.hits.size() - 1).position.intValue());

				List<Rank> predictionsDummyRanks = new ArrayList<Rank>();
				Rank dummyRank;
				for (int i = start; i <= end; i++) {
					double prediction = prediction(summary.simpleModelRun, i);
					// calibrated.addPoint(i, prediction);
					dummyRank = new Rank();
					dummyRank.position = i;
					dummyRank.downloads = (int) prediction;
					dummyRank.revenue = (float) prediction;
					predictionsDummyRanks.add(dummyRank);
				}
				chart.drawData(predictionsDummyRanks, "prediction", ChartHelper.TYPE_SPLINE, ColorHelper.getReflectionPurple(), false, false);
			}

			if (summary.misses != null) {
				chart.drawData(summary.misses, "misses", ChartHelper.TYPE_SCATTER, ColorHelper.getReflectionPurple(), false, false);
				chart.setSeriesVisible("misses", false);
			}

			if (summary.hits != null) {
				chart.drawData(summary.hits, "hits", ChartHelper.TYPE_SCATTER, ColorHelper.getReflectionPurple(), false, false);
			}
		}

	}

	/**
	 * @param simpleModelRun
	 * @param possition
	 * @return
	 */
	private double prediction(SimpleModelRun simpleModelRun, int possition) {
		return (double) (summary.simpleModelRun.b.doubleValue() * Math.pow(possition, -summary.simpleModelRun.a.doubleValue()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		// register(DefaultEventBus.get().addHandlerToSource(GetCalibrationSummaryEventHandler.TYPE, CalibrationSummaryController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));

	}

	private void refreshBreadcrumbs() {
		breadcrumbs.clear();

		if (summary == null) {
			breadcrumbs.push("...");
		} else {
			String storeName = null;

			if (DataTypeHelper.IOS_STORE_A3.equalsIgnoreCase(summary.feedFetch.store)) {
				storeName = (summaryFormType == FormType.FormTypeTablet ? DataTypeHelper.STORE_IPAD_NAME : DataTypeHelper.STORE_IPHONE_NAME);
			}

			breadcrumbs.push(storeName, CountryController.get().getCountry(summary.feedFetch.country).name,
					CategoryController.get().getCategory(summary.feedFetch.category.id).name, StringUtils.upperCaseFirstLetter(summaryListType.toString()));
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
		if (current.getAction() != null) {
			if (NavigationController.VIEW_ACTION_PARAMETER_VALUE.equals(current.getAction())) {
				feedFetchParam = current.getParameter(FEED_FETCH_ID_PARAMETER_INDEX);

				if (feedFetchParam != null) {

					Long feedFetchId = Long.valueOf(feedFetchParam);
					chart.setLoading(true);
					showMisses.setValue(Boolean.FALSE);
					showTop.setValue(Boolean.FALSE);
					showTail.setValue(Boolean.FALSE);
					show(CalibrationSummaryController.get().getCalibrationSummary(feedFetchId));
				}
			}
		}
	}

	// /*
	// * (non-Javadoc)
	// *
	// * @see
	// * io.reflection.app.api.admin.shared.call.event.GetCalibrationSummaryEventHandler#getCalibrationSummarySuccess(io.reflection.app.api.admin.shared.call.
	// * GetCalibrationSummaryRequest, io.reflection.app.api.admin.shared.call.GetCalibrationSummaryResponse)
	// */
	// @Override
	// public void getCalibrationSummarySuccess(GetCalibrationSummaryRequest input, GetCalibrationSummaryResponse output) {
	// if (output == null || output.status == StatusType.StatusTypeFailure || output.calibrationSummary == null) {
	// showFailed();
	// } else {
	// summaryListType = output.listType;
	// summaryListProperty = output.listProperty;
	// chart.setYDataType((ListPropertyType.ListPropertyTypeDownloads.equals(summaryListProperty)) ? YDataType.DownloadsYAxisDataType
	// : YDataType.RevenueYAxisDataType);
	//
	// show(output.calibrationSummary);
	// }
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see
	// * io.reflection.app.api.admin.shared.call.event.GetCalibrationSummaryEventHandler#getCalibrationSummaryFailure(io.reflection.app.api.admin.shared.call.
	// * GetCalibrationSummaryRequest, java.lang.Throwable)
	// */
	// @Override
	// public void getCalibrationSummaryFailure(GetCalibrationSummaryRequest input, Throwable caught) {
	// showFailed();
	// }

	// private void showFailed() {
	// this.summary = null;
	//
	// refreshBreadcrumbs();
	//
	// title.setInnerHTML("Not found");
	// description.setInnerHTML("Calibration summary of feed fetch " + feedFetchParam + " was not found.");
	//
	// hitsProvider.setList(Collections.<Rank> emptyList());
	// missesProvider.setList(Collections.<Rank> emptyList());
	//
	// drawChartData();
	//
	// showMisses.setEnabled(true);
	// showTop.setEnabled(true);
	// showTail.setEnabled(true);
	// }

	private void show(CalibrationSummary summary) {
		this.summary = summary;

		refreshBreadcrumbs();

		if (summary == null) {
			title.setInnerHTML("Loading...");
			description.setInnerHTML("Loading calibration summary of feed fetch " + feedFetchParam + "...");

			hitsProvider.setList(Collections.<Rank> emptyList());
			missesProvider.setList(Collections.<Rank> emptyList());

			showMisses.setEnabled(false);
			showTop.setEnabled(false);
			showTail.setEnabled(false);
		} else {
			String status = " <span class=\"label label-info\">PARTIAL</span>";
			boolean partial = true;

			if (summary.simpleModelRun == null) {
				status = " <span class=\"label label-danger\">FAILED</span>";
				partial = false;
			} else if (summary.hits != null && summary.hits.size() > 2) {
				status = " <span class=\"label label-success\">SUCCEEDED</span>";
				partial = false;
			}

			title.setInnerHTML("Feed fetch (" + summary.feedFetch.code.toString() + ") on "
					+ DateTimeFormat.getFormat(FormattingHelper.DATE_FORMAT_DD_MMM_YYYY + " a").format(summary.feedFetch.date) + status);

			currency = io.reflection.app.shared.util.FormattingHelper.getCountryCurrency(summary.feedFetch.country.toUpperCase());
			prototype.currency(currency);
			chart.setCurrency(FormattingHelper.getCurrencySymbol(currency));

			hitsProvider.setList(summary.hits == null ? Collections.<Rank> emptyList() : summary.hits);
			missesProvider.setList(summary.misses == null ? Collections.<Rank> emptyList() : summary.misses);

			description.setInnerHTML(StringUtils.upperCaseFirstLetter(summaryListType.toString())
					+ " list was calibrated with sales summaries from <strong>"
					+ FormattingHelper.DATE_FORMATTER_DD_MMM_YYYY.format(summary.simpleModelRun == null ? summary.salesSummaryDate
							: summary.simpleModelRun.summaryDate)
					+ "</strong>"
					+ (partial ? ", with partial success" : (summary.simpleModelRun == null ? ", but failed" : ", sucessfully"))
					+ ". Matched "
					+ hitCount()
					+ " ranking applications out of a total of "
					+ totalCount()
					+ " ("
					+ missCount()
					+ " misses) based on "
					+ summaryListProperty.toString()
					+ "."
					+ (summary.simpleModelRun == null ? "" : ("<div class=\"" + Styles.STYLES_INSTANCE.reflectionMainStyle().headingStyleHeadingSix()
							+ "\">Predictions</div>" + simpleModelRun1And200() + "<div class=\""
							+ Styles.STYLES_INSTANCE.reflectionMainStyle().headingStyleHeadingSix() + "\">Simple model run fitted with co-efficients</div>"
							+ simpleModelRunCoefficients() + (partial ? "" : "Additional information:" + simpleModelRunAdditionalInfo()))));

			drawChartData();

			showMisses.setEnabled(true);
			showTop.setEnabled(true);
			showTail.setEnabled(true);
		}
	}

	/**
	 * @return
	 */
	private String simpleModelRunAdditionalInfo() {
		return "<ul><li>adjusted r<sup>2</sup> = " + summary.simpleModelRun.adjustedRSquared.doubleValue() + "</li><li>standard error = "
				+ summary.simpleModelRun.aStandardError.doubleValue() + "</li><li>sum of squares = "
				+ summary.simpleModelRun.regressionSumSquares.doubleValue() + "</li><li>a standard error = "
				+ summary.simpleModelRun.aStandardError.doubleValue() + "</li><li>b standard error = " + summary.simpleModelRun.bStandardError.doubleValue()
				+ "</li></ul>";
	}

	private String simpleModelRun1And200() {
		String top = "None";
		String bottom = "None";

		if (summaryListProperty == ListPropertyType.ListPropertyTypeDownloads) {
			top = Integer.toString((int) prediction(summary.simpleModelRun, 1));
			bottom = Integer.toString((int) prediction(summary.simpleModelRun, 200));
		} else if (summaryListProperty == ListPropertyType.ListPropertyTypeRevenue) {
			top = FormattingHelper.asWholeMoneyString(currency, (float) prediction(summary.simpleModelRun, 1));
			bottom = FormattingHelper.asWholeMoneyString(currency, (float) prediction(summary.simpleModelRun, 200));
		}

		String made = (summaryListProperty == ListPropertyType.ListPropertyTypeRevenue ? "earned" : "had");
		String units = (summaryListProperty == ListPropertyType.ListPropertyTypeRevenue ? "" : " downloads");

		return "Item at rank number 1 should have " + made + " " + top + units + ", while the one at rank 200 should have " + made + " " + bottom + units + ".";
	}

	/**
	 * @return
	 */
	private String simpleModelRunCoefficients() {
		return "<ul><li>a = " + summary.simpleModelRun.a.doubleValue() + "</li><li>b = " + summary.simpleModelRun.b.doubleValue() + "</li></ul>";
	}

	private String hitCount() {
		return Integer.toString(summary.hits == null ? 0 : summary.hits.size());
	}

	private String totalCount() {
		return Integer.toString((summary.hits == null ? 0 : summary.hits.size()) + (summary.misses == null ? 0 : summary.misses.size()));
	}

	private String missCount() {
		return Integer.toString(summary.misses == null ? 0 : summary.misses.size());
	}
}
