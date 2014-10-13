//
//  DataAccountFetches.java
//  storedata
//
//  Created by Stefano Capuzzi (stefanocapuzzi) on 9 Oct 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.admin;

import io.reflection.app.client.cell.StyledButtonCell;
import io.reflection.app.client.controller.DataAccountFetchController;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.ServiceConstants;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.page.Page;
import io.reflection.app.client.page.PageType;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.SimplePager;
import io.reflection.app.client.res.Images;
import io.reflection.app.datatypes.shared.DataAccountFetch;
import io.reflection.app.datatypes.shared.DataAccountFetchStatusType;
import io.reflection.app.shared.util.FormattingHelper;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Stefano Capuzzi (stefanocapuzzi)
 * 
 */
public class DataAccountFetchesPage extends Page implements NavigationEventHandler {

	private static DataAccountFetchesPageUiBinder uiBinder = GWT.create(DataAccountFetchesPageUiBinder.class);

	interface DataAccountFetchesPageUiBinder extends UiBinder<Widget, DataAccountFetchesPage> {}

	@UiField(provided = true) CellTable<DataAccountFetch> dataAccountFetchTable = new CellTable<DataAccountFetch>(ServiceConstants.STEP_VALUE,
			BootstrapGwtCellTable.INSTANCE);
	@UiField(provided = true) SimplePager simplePager = new SimplePager(false, false);

	@UiField InlineHyperlink backLink;

	public DataAccountFetchesPage() {
		initWidget(uiBinder.createAndBindUi(this));

		addColumns();

		dataAccountFetchTable.setLoadingIndicator(new Image(Images.INSTANCE.preloader()));
		DataAccountFetchController.get().addDataDisplay(dataAccountFetchTable);
		simplePager.setDisplay(dataAccountFetchTable);
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

		final DateTimeFormat dtf = DateTimeFormat.getFormat(FormattingHelper.DATE_FORMAT_DD_MMM_YYYY);
		TextColumn<DataAccountFetch> dateColumn = new TextColumn<DataAccountFetch>() {

			@Override
			public String getValue(DataAccountFetch object) {
				return dtf.format(object.date);
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
					DataAccountFetchController.get().gather(object.id);
					break;
				case "ingest":
					DataAccountFetchController.get().ingest(object.id);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();
		register(EventController.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));
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
}
