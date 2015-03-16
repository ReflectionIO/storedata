//
//  CalibrationSummaryPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 8 Mar 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.admin;

import io.reflection.app.api.admin.shared.call.GetCalibrationSummaryRequest;
import io.reflection.app.api.admin.shared.call.GetCalibrationSummaryResponse;
import io.reflection.app.api.admin.shared.call.event.GetCalibrationSummaryEventHandler;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.cell.AppRankCell;
import io.reflection.app.client.controller.CalibrationSummaryController;
import io.reflection.app.client.controller.CategoryController;
import io.reflection.app.client.controller.CountryController;
import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.helper.FormattingHelper;
import io.reflection.app.client.page.Page;
import io.reflection.app.client.page.part.ItemChart;
import io.reflection.app.client.page.part.ItemChart.Colour;
import io.reflection.app.client.page.part.RankHover;
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

import java.util.Collections;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.googlecode.gchart.client.GChart;
import com.googlecode.gchart.client.GChart.AnnotationLocation;
import com.googlecode.gchart.client.GChart.Curve;
import com.googlecode.gchart.client.GChart.SymbolType;
import com.spacehopperstudios.utility.StringUtils;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author William Shakour (billy1380)
 *
 */
public class CalibrationSummaryPage extends Page implements NavigationEventHandler, GetCalibrationSummaryEventHandler {

	private static CalibrationSummaryPagePageUiBinder uiBinder = GWT.create(CalibrationSummaryPagePageUiBinder.class);

	interface CalibrationSummaryPagePageUiBinder extends UiBinder<Widget, CalibrationSummaryPage> {}

	private static final int FEED_FETCH_ID_PARAMETER_INDEX = 0;
	public static final String VIEW_ACTION_NAME = "view";

	@UiField CheckBox showMisses;
	@UiField CheckBox showTop;
	@UiField CheckBox showTail;
	@UiField Breadcrumbs breadcrumbs;

	private AppRankCell prototype = new AppRankCell(true).useFilter(false);
	@UiField(provided = true) CellList<Rank> hits = new CellList<Rank>(prototype, BootstrapGwtCellList.INSTANCE);
	@UiField(provided = true) CellList<Rank> misses = new CellList<Rank>(prototype, BootstrapGwtCellList.INSTANCE);

	@UiField GChart chart;

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

	private Timer resizeTimer;
	private static final int CHART_HEIGHT = 350;

	private ResizeHandler resizeHandler = new ResizeHandler() {

		@Override
		public void onResize(ResizeEvent event) {
			resizeTimer.cancel();
			resizeTimer.schedule(250);
		}
	};

	@UiHandler({ "showMisses", "showTop", "showTail" })
	void showMissesValueChanged(ValueChangeEvent<Boolean> event) {
		drawChartData(chart);
	}

	public CalibrationSummaryPage() {
		initWidget(uiBinder.createAndBindUi(this));

		hits.setPageSize(Integer.MAX_VALUE);
		misses.setPageSize(Integer.MAX_VALUE);

		hits.setEmptyListWidget(new HTMLPanel("No items found"));
		misses.setEmptyListWidget(new HTMLPanel("No items found"));

		hitsProvider.addDataDisplay(hits);
		missesProvider.addDataDisplay(misses);

		resizeTimer = new Timer() {
			@Override
			public void run() {
				resizeChart(chart);
			}
		};

		setupChart(chart);
	}

	private void setupChart(GChart gc) {
		gc.setBorderStyle("none");

		// setBackgroundColor("repeating-linear-gradient( 180deg, #FAFAFA, #FAFAFA 50px, #FFFFFF 50px, #FFFFFF 100px)");

		// configure x-axis
		gc.getXAxis().setTickLength(0);

		gc.getYAxis().setHasGridlines(true);
		gc.getYAxis().setTickLength(0);

		gc.getYAxis().setTicksPerGridline(1);
		gc.setGridColor("#EFF2F5");

		gc.getYAxis().setAxisVisible(false);
		gc.getXAxis().setAxisVisible(false);

		gc.getXAxis().setTickLabelFontColor("gray");
		gc.getYAxis().setTickLabelFontColor("gray");

		gc.getXAxis().setTickLabelPadding(20);
		gc.getYAxis().setTickLabelPadding(10);

		gc.addCurve(0);
		setupChartCurve(gc.getCurve(0), Colour.GreenColour);

		gc.addCurve(1);
		setupChartCurve(gc.getCurve(1), Colour.PinkColour);

		gc.addCurve(2);
		setupChartCurveLine(gc.getCurve(2), Colour.PurpleColour);

		gc.getYAxis().setOutOfBoundsMultiplier(.1);

	}

	private void setupChartCurveLine(Curve c, Colour col) {
		c.getSymbol().setHoverSelectionWidth(1);
		c.getSymbol().setHoverSelectionBackgroundColor("#929292");
		c.getSymbol().setHoverSelectionBorderWidth(0);
		c.getSymbol().setHoverSelectionFillThickness(1);

		c.getSymbol().setSymbolType(SymbolType.LINE);
		c.getSymbol().setHeight(0);
		c.getSymbol().setWidth(0);
		c.getSymbol().setFillThickness(1);

		// use a vertical line for the selection cursor
		c.getSymbol().setHoverSelectionSymbolType(SymbolType.XGRIDLINE);
		// with annotation on top of this line (above chart)
		c.getSymbol().setHoverLocation(AnnotationLocation.NORTHEAST);

		c.getSymbol().setHoverYShift(15);
		c.getSymbol().setHoverXShift(-62);

		RankHover hoverWidget = new RankHover();
		hoverWidget.setCssColor(col.getColour());
		hoverWidget.setXAxisDataType(ItemChart.XAxisDataType.RankingXAxisDataType);

		c.getSymbol().setHoverWidget(hoverWidget);

		// tall brush so it touches independent of mouse y position
		c.getSymbol().setBrushSize(25, 700);
		// so only point-to-mouse x-distance matters for hit testing
		c.getSymbol().setDistanceMetric(1, 0);

	}

	private void setupChartCurve(Curve c, Colour col) {
		c.getSymbol().setBorderWidth(0);
		c.getSymbol().setWidth(8);
		c.getSymbol().setHeight(8);

		c.getSymbol().setImageURL(col.getImageUrl());

		c.getSymbol().setHoverSelectionWidth(1);
		c.getSymbol().setHoverSelectionBackgroundColor("#929292");
		c.getSymbol().setHoverSelectionBorderWidth(0);
		c.getSymbol().setHoverSelectionFillThickness(1);

		// use a vertical line for the selection cursor
		c.getSymbol().setHoverSelectionSymbolType(SymbolType.XGRIDLINE);
		// with annotation on top of this line (above chart)
		c.getSymbol().setHoverLocation(AnnotationLocation.NORTHEAST);

		c.getSymbol().setHoverYShift(15);
		c.getSymbol().setHoverXShift(-62);

		RankHover hoverWidget = new RankHover();
		hoverWidget.setCssColor(col.getColour());
		hoverWidget.setXAxisDataType(ItemChart.XAxisDataType.RankingXAxisDataType);

		c.getSymbol().setHoverWidget(hoverWidget);

		// tall brush so it touches independent of mouse y position
		c.getSymbol().setBrushSize(25, 700);
		// so only point-to-mouse x-distance matters for hit testing
		c.getSymbol().setDistanceMetric(1, 0);
	}

	private void drawChartData(GChart gc) {
		Curve hits = gc.getCurve(0);
		Curve misses = gc.getCurve(1);
		Curve calibrated = gc.getCurve(2);

		if (hits != null) {
			hits.clearPoints();
		}

		if (misses != null) {
			misses.clearPoints();
		}

		if (calibrated != null) {
			calibrated.clearPoints();
		}

		if (summary != null) {
			boolean isRevenue = false;
			if (summary.hits != null) {
				for (Rank rank : summary.hits) {
					hits.addPoint(rank.position.doubleValue(),
							(isRevenue = (rank.downloads == null)) ? rank.revenue.doubleValue() : rank.downloads.doubleValue());
				}
			}

			if (showMisses.getValue() == Boolean.TRUE && summary.misses != null) {
				for (Rank rank : summary.misses) {
					misses.addPoint(0, (isRevenue = (rank.downloads == null)) ? rank.revenue.doubleValue() : rank.downloads.doubleValue());
				}
			}

			((RankHover) hits.getSymbol().getHoverWidget()).setYAxisDataType(isRevenue ? ItemChart.YAxisDataType.RevenueYAxisDataType
					: ItemChart.YAxisDataType.DownloadsYAxisDataType);
			((RankHover) hits.getSymbol().getHoverWidget()).setCurrency(io.reflection.app.shared.util.FormattingHelper
					.getCountryCurrency(summary.feedFetch.country.toUpperCase()));

			((RankHover) misses.getSymbol().getHoverWidget()).setYAxisDataType(isRevenue ? ItemChart.YAxisDataType.RevenueYAxisDataType
					: ItemChart.YAxisDataType.DownloadsYAxisDataType);
			((RankHover) misses.getSymbol().getHoverWidget()).setCurrency(currency);

			if (summary.simpleModelRun != null) {
				int start = (showTop.getValue() == Boolean.TRUE ? 1 : summary.hits.get(0).position.intValue());
				int end = (showTail.getValue() == Boolean.TRUE ? 200 : summary.hits.get(summary.hits.size() - 1).position.intValue());

				for (int i = start; i <= end; i++) {
					calibrated.addPoint(i, prediction(summary.simpleModelRun, i));
				}
			}

		}

		setChartLoading(gc, false);
	}

	/**
	 * @param simpleModelRun
	 * @param possition
	 * @return
	 */
	private double prediction(SimpleModelRun simpleModelRun, int possition) {
		return (double) (summary.simpleModelRun.b.doubleValue() * Math.pow(possition, -summary.simpleModelRun.a.doubleValue()));
	}

	private void setChartLoading(GChart gc, boolean loading) {
		Curve curve = gc.getCurve(0);

		if (curve != null) {
			curve.setVisible(!loading);

			if (loading) {
				curve.clearPoints();

				gc.getXAxis().setAxisMax(FilterController.get().getEndDate().getTime());
				gc.getXAxis().setAxisMin(FilterController.get().getStartDate().getTime());

				gc.getYAxis().setAxisMax(1);
				gc.getYAxis().setAxisMin(8);
				gc.getYAxis().setTickCount(8);
			}

			gc.update();
		}
	}

	private void resizeChart(GChart gc) {
		gc.setChartSize((int) (gc.getElement().getParentElement().getClientWidth() - 170), CHART_HEIGHT);
		gc.update();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		register(DefaultEventBus.get().addHandlerToSource(GetCalibrationSummaryEventHandler.TYPE, CalibrationSummaryController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));

		resizeTimer.cancel();
		resizeTimer.schedule(250);

		register(Window.addResizeHandler(resizeHandler));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onDetach()
	 */
	@Override
	protected void onDetach() {
		super.onDetach();

		resizeTimer.cancel();
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
			if (VIEW_ACTION_NAME.equals(current.getAction())) {
				feedFetchParam = current.getParameter(FEED_FETCH_ID_PARAMETER_INDEX);

				if (feedFetchParam != null) {
					Long feedFetchId = Long.valueOf(feedFetchParam);

					show(CalibrationSummaryController.get().getCalibrationSummary(feedFetchId));
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.admin.shared.call.event.GetCalibrationSummaryEventHandler#getCalibrationSummarySuccess(io.reflection.app.api.admin.shared.call.
	 * GetCalibrationSummaryRequest, io.reflection.app.api.admin.shared.call.GetCalibrationSummaryResponse)
	 */
	@Override
	public void getCalibrationSummarySuccess(GetCalibrationSummaryRequest input, GetCalibrationSummaryResponse output) {
		if (output == null || output.status == StatusType.StatusTypeFailure || output.calibrationSummary == null) {
			showFailed();
		} else {
			summaryListType = output.listType;
			summaryListProperty = output.listProperty;

			show(output.calibrationSummary);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.admin.shared.call.event.GetCalibrationSummaryEventHandler#getCalibrationSummaryFailure(io.reflection.app.api.admin.shared.call.
	 * GetCalibrationSummaryRequest, java.lang.Throwable)
	 */
	@Override
	public void getCalibrationSummaryFailure(GetCalibrationSummaryRequest input, Throwable caught) {
		showFailed();
	}

	private void showFailed() {
		this.summary = null;

		refreshBreadcrumbs();

		title.setInnerHTML("Not found");
		description.setInnerHTML("Calibration summary of feed fetch " + feedFetchParam + " was not found.");

		hitsProvider.setList(Collections.<Rank> emptyList());
		missesProvider.setList(Collections.<Rank> emptyList());

		drawChartData(chart);

		showMisses.setEnabled(true);
		showTop.setEnabled(true);
		showTail.setEnabled(true);
	}

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

			drawChartData(chart);

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
