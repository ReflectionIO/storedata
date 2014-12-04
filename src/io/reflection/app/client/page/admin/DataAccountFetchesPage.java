//
//  DataAccountFetches.java
//  storedata
//
//  Created by Stefano Capuzzi (stefanocapuzzi) on 9 Oct 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.admin;

import static io.reflection.app.client.helper.FormattingHelper.DATE_FORMAT_DD_MMM_YYYY;
import io.reflection.app.api.admin.shared.call.GetDataAccountFetchesRequest;
import io.reflection.app.api.admin.shared.call.GetDataAccountFetchesResponse;
import io.reflection.app.api.admin.shared.call.TriggerDataAccountFetchIngestRequest;
import io.reflection.app.api.admin.shared.call.TriggerDataAccountFetchIngestResponse;
import io.reflection.app.api.admin.shared.call.TriggerDataAccountGatherRequest;
import io.reflection.app.api.admin.shared.call.TriggerDataAccountGatherResponse;
import io.reflection.app.api.admin.shared.call.event.GetDataAccountFetchesEventHandler;
import io.reflection.app.api.admin.shared.call.event.TriggerDataAccountFetchIngestEventHandler;
import io.reflection.app.api.admin.shared.call.event.TriggerDataAccountGatherEventHandler;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.cell.StyledButtonCell;
import io.reflection.app.client.controller.DataAccountFetchController;
import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.controller.FilterController.Filter;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.ServiceConstants;
import io.reflection.app.client.handler.FilterEventHandler;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.helper.FilterHelper;
import io.reflection.app.client.page.Page;
import io.reflection.app.client.page.PageType;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.BootstrapGwtDatePicker;
import io.reflection.app.client.part.DateSelector;
import io.reflection.app.client.part.Preloader;
import io.reflection.app.client.part.SimplePager;
import io.reflection.app.client.part.datatypes.DateRange;
import io.reflection.app.client.res.Images;
import io.reflection.app.datatypes.shared.DataAccountFetch;
import io.reflection.app.datatypes.shared.DataAccountFetchStatusType;

import java.util.Map;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author Stefano Capuzzi (stefanocapuzzi)
 * 
 */
public class DataAccountFetchesPage extends Page implements NavigationEventHandler, FilterEventHandler, GetDataAccountFetchesEventHandler,
		TriggerDataAccountGatherEventHandler, TriggerDataAccountFetchIngestEventHandler {

	private static DataAccountFetchesPageUiBinder uiBinder = GWT.create(DataAccountFetchesPageUiBinder.class);

	interface DataAccountFetchesPageUiBinder extends UiBinder<Widget, DataAccountFetchesPage> {}

	@UiField(provided = true) CellTable<DataAccountFetch> dataAccountFetchTable = new CellTable<DataAccountFetch>(ServiceConstants.STEP_VALUE,
			BootstrapGwtCellTable.INSTANCE);
	@UiField(provided = true) SimplePager simplePager = new SimplePager(false, false);

	@UiField DateSelector dateSelector;
	@UiField InlineHyperlink backLink;
	@UiField Preloader preloader;

	public DataAccountFetchesPage() {
		initWidget(uiBinder.createAndBindUi(this));

		BootstrapGwtDatePicker.INSTANCE.styles().ensureInjected();

		addColumns();

		dataAccountFetchTable.setLoadingIndicator(new Image(Images.INSTANCE.preloader()));
		dataAccountFetchTable.setEmptyTableWidget(new HTMLPanel("<h6>No Data Account Fetches</h6>"));
		DataAccountFetchController.get().addDataDisplay(dataAccountFetchTable);
		simplePager.setDisplay(dataAccountFetchTable);

		dateSelector.addFixedRanges(FilterHelper.getAdminDateRanges());

		updateFromFilter();

	}

	private void addColumns() {

		TextColumn<DataAccountFetch> idColumn = new TextColumn<DataAccountFetch>() {

			@Override
			public String getValue(DataAccountFetch object) {
				return object.id.toString();
			}

		};
		dataAccountFetchTable.addColumn(idColumn, "Id");

		TextColumn<DataAccountFetch> dataAccountIdColumn = new TextColumn<DataAccountFetch>() {

			@Override
			public String getValue(DataAccountFetch object) {
				return object.linkedAccount.id.toString();
			}

		};
		dataAccountFetchTable.addColumn(dataAccountIdColumn, "D.A. id");

		TextColumn<DataAccountFetch> dateColumn = new TextColumn<DataAccountFetch>() {

			@Override
			public String getValue(DataAccountFetch object) {
				return DATE_FORMAT_DD_MMM_YYYY.format(object.date);
			}

		};
		dataAccountFetchTable.addColumn(dateColumn, "Date");

		TextColumn<DataAccountFetch> statusColumn = new TextColumn<DataAccountFetch>() {

			@Override
			public String getValue(DataAccountFetch object) {
				return object.status.toString();
			}

		};
		dataAccountFetchTable.addColumn(statusColumn, "Status");

		TextColumn<DataAccountFetch> dataColumn = new TextColumn<DataAccountFetch>() {

			@Override
			public String getValue(DataAccountFetch object) {
				return object.data;
			}

		};
		dataAccountFetchTable.addColumn(dataColumn, "Data");

		FieldUpdater<DataAccountFetch, String> onClick = new FieldUpdater<DataAccountFetch, String>() {

			@Override
			public void update(int index, DataAccountFetch object, String value) {
				switch (value) {
				case "gather":
					preloader.show();
					DataAccountFetchController.get().gather(object.linkedAccount, object.date);
					break;
				case "ingest":
					preloader.show();
					DataAccountFetchController.get().ingest(object);
					break;
				}
			}
		};

		Column<DataAccountFetch, String> operationColumn = new Column<DataAccountFetch, String>(new StyledButtonCell("btn", "btn-xs", "btn-default")) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see com.google.gwt.user.cellview.client.Column#render(com.google.gwt.cell.client.Cell.Context, java.lang.Object,
			 * com.google.gwt.safehtml.shared.SafeHtmlBuilder)
			 */
			@Override
			public void render(Context context, DataAccountFetch object, SafeHtmlBuilder sb) {
				if (object.status != null
						&& (object.status == DataAccountFetchStatusType.DataAccountFetchStatusTypeError || object.status == DataAccountFetchStatusType.DataAccountFetchStatusTypeGathered)) {
					super.render(context, object, sb);
				}

			}

			@Override
			public String getValue(DataAccountFetch object) {
				String value = "";
				if (object.status != null) {
					if (object.status == DataAccountFetchStatusType.DataAccountFetchStatusTypeError) {
						return "gather";
					} else if (object.status == DataAccountFetchStatusType.DataAccountFetchStatusTypeGathered) { return "ingest"; }
				}
				return value;

			}

		};
		dataAccountFetchTable.addColumn(operationColumn);

		operationColumn.setFieldUpdater(onClick);

	}

	public void updateFromFilter() {
		FilterController fc = FilterController.get();

		DateRange range = new DateRange();
		range.setFrom(fc.getStartDate());
		range.setTo(fc.getEndDate());
		dateSelector.setValue(range);
	}

	@UiHandler("dateSelector")
	void onDateRangeValueChanged(ValueChangeEvent<DateRange> event) {
		FilterController fc = FilterController.get();
		fc.start();
		fc.setEndDate(event.getValue().getTo());
		fc.setStartDate(event.getValue().getFrom());
		fc.commit();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();
		register(DefaultEventBus.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(FilterEventHandler.TYPE, FilterController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(GetDataAccountFetchesEventHandler.TYPE, DataAccountFetchController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(TriggerDataAccountGatherEventHandler.TYPE, DataAccountFetchController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(TriggerDataAccountFetchIngestEventHandler.TYPE, DataAccountFetchController.get(), this));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) {
		if (PageType.DataAccountFetchesPageType.equals(current.getPage())) {
			updateFromFilter();

			Long feedFetchDataAccountId = DataAccountFetchController.get().getDataAccountId();
			if (current.getAction() != null && current.getAction().matches("[0-9]+")) { // Get for a specific Data Account
				backLink.setVisible(true);
				if (feedFetchDataAccountId == null
						|| (feedFetchDataAccountId != null && feedFetchDataAccountId.longValue() != Long.valueOf(current.getAction()).longValue())) {
					// Data Account changed
					DataAccountFetchController.get().reset();
					DataAccountFetchController.get().setDataAccountId(Long.valueOf(current.getAction()));
					simplePager.setPageStart(0);
					DataAccountFetchController.get().fetchDataAccountFetches();
				}
			} else { // Get for every data account
				backLink.setVisible(false);
				if (feedFetchDataAccountId != null || (feedFetchDataAccountId == null && !DataAccountFetchController.get().dataAccountFetchFetched())) {
					DataAccountFetchController.get().reset();
					simplePager.setPageStart(0);
					DataAccountFetchController.get().fetchDataAccountFetches();
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.FilterEventHandler#filterParamChanged(java.lang.String, java.lang.Object, java.lang.Object)
	 */
	@Override
	public <T> void filterParamChanged(String name, T currentValue, T previousValue) {
		if (PageType.DataAccountFetchesPageType.equals(NavigationController.get().getCurrentPage())) {
			String dataAccountId = NavigationController.get().getStack().getAction();
			DataAccountFetchController.get().reset();
			simplePager.setPageStart(0);
			if (dataAccountId != null && dataAccountId.matches("[0-9]+")) {
				PageType.DataAccountFetchesPageType.show(dataAccountId, FilterController.get().asDataAccountFetchFilterString());
			} else {
				PageType.DataAccountFetchesPageType.show(FilterController.get().asDataAccountFetchFilterString());
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
		if (PageType.DataAccountFetchesPageType.equals(NavigationController.get().getCurrentPage())) {
			String dataAccountId = NavigationController.get().getStack().getAction();
			DataAccountFetchController.get().reset();
			simplePager.setPageStart(0);
			if (dataAccountId != null && dataAccountId.matches("[0-9]+")) {
				PageType.DataAccountFetchesPageType.show(dataAccountId, FilterController.get().asDataAccountFetchFilterString());
			} else {
				PageType.DataAccountFetchesPageType.show(FilterController.get().asDataAccountFetchFilterString());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.admin.shared.call.event.GetDataAccountFetchesEventHandler#getDataAccountFetchesSuccess(io.reflection.app.api.admin.shared.call.
	 * GetDataAccountFetchesRequest, io.reflection.app.api.admin.shared.call.GetDataAccountFetchesResponse)
	 */
	@Override
	public void getDataAccountFetchesSuccess(GetDataAccountFetchesRequest input, GetDataAccountFetchesResponse output) {
		if (output.status.equals(StatusType.StatusTypeSuccess)) {

		} else {
			DataAccountFetchController.get().updateRowCount(0, true);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.admin.shared.call.event.GetDataAccountFetchesEventHandler#getDataAccountFetchesFailure(io.reflection.app.api.admin.shared.call.
	 * GetDataAccountFetchesRequest, java.lang.Throwable)
	 */
	@Override
	public void getDataAccountFetchesFailure(GetDataAccountFetchesRequest input, Throwable caught) {
		DataAccountFetchController.get().updateRowCount(0, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.admin.shared.call.event.TriggerDataAccountFetchIngestEventHandler#triggerDataAccountFetchIngestSuccess(io.reflection.app.api.admin
	 * .shared.call.TriggerDataAccountFetchIngestRequest, io.reflection.app.api.admin.shared.call.TriggerDataAccountFetchIngestResponse)
	 */
	@Override
	public void triggerDataAccountFetchIngestSuccess(TriggerDataAccountFetchIngestRequest input, TriggerDataAccountFetchIngestResponse output) {
		preloader.hide();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.admin.shared.call.event.TriggerDataAccountFetchIngestEventHandler#triggerDataAccountFetchIngestFailure(io.reflection.app.api.admin
	 * .shared.call.TriggerDataAccountFetchIngestRequest, java.lang.Throwable)
	 */
	@Override
	public void triggerDataAccountFetchIngestFailure(TriggerDataAccountFetchIngestRequest input, Throwable caught) {
		preloader.hide();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.admin.shared.call.event.TriggerDataAccountGatherEventHandler#triggerDataAccountGatherSuccess(io.reflection.app.api.admin.shared
	 * .call.TriggerDataAccountGatherRequest, io.reflection.app.api.admin.shared.call.TriggerDataAccountGatherResponse)
	 */
	@Override
	public void triggerDataAccountGatherSuccess(TriggerDataAccountGatherRequest input, TriggerDataAccountGatherResponse output) {
		preloader.hide();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.admin.shared.call.event.TriggerDataAccountGatherEventHandler#triggerDataAccountGatherFailure(io.reflection.app.api.admin.shared
	 * .call.TriggerDataAccountGatherRequest, java.lang.Throwable)
	 */
	@Override
	public void triggerDataAccountGatherFailure(TriggerDataAccountGatherRequest input, Throwable caught) {
		preloader.hide();
	}
}
