//
//  DataAccountsPage.java
//  storedata
//
//  Created by Stefano Capuzzi on 7 Oct 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.admin;

import io.reflection.app.api.admin.shared.call.JoinDataAccountRequest;
import io.reflection.app.api.admin.shared.call.JoinDataAccountResponse;
import io.reflection.app.api.admin.shared.call.event.JoinDataAccountEventHandler;
import io.reflection.app.client.cell.StyledButtonCell;
import io.reflection.app.client.controller.DataAccountController;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.controller.ServiceConstants;
import io.reflection.app.client.page.Page;
import io.reflection.app.client.page.PageType;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.Preloader;
import io.reflection.app.client.part.SimplePager;
import io.reflection.app.client.res.Images;
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.shared.util.FormattingHelper;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Stefano Capuzzi
 * 
 */
public class DataAccountsPage extends Page implements JoinDataAccountEventHandler {

	private static DataAccountsPageUiBinder uiBinder = GWT.create(DataAccountsPageUiBinder.class);

	interface DataAccountsPageUiBinder extends UiBinder<Widget, DataAccountsPage> {}

	interface DataAccountsPageStyle extends CssResource {
		String green();

	}

	@UiField DataAccountsPageStyle style;

	@UiField(provided = true) CellTable<DataAccount> dataAccountTable = new CellTable<DataAccount>(ServiceConstants.STEP_VALUE, BootstrapGwtCellTable.INSTANCE);
	@UiField(provided = true) SimplePager simplePager = new SimplePager(false, false);

	@UiField Preloader preloader;

	public DataAccountsPage() {
		initWidget(uiBinder.createAndBindUi(this));

		addColumns();

		dataAccountTable.setLoadingIndicator(new Image(Images.INSTANCE.preloader()));
		DataAccountController.get().addDataDisplay(dataAccountTable);
		simplePager.setDisplay(dataAccountTable);
	}

	public void addColumns() {

		TextColumn<DataAccount> idColumn = new TextColumn<DataAccount>() {

			@Override
			public String getValue(DataAccount object) {
				return object.id.toString();
			}

		};
		dataAccountTable.addColumn(idColumn, "id");

		final DateTimeFormat dtf = DateTimeFormat.getFormat(FormattingHelper.DATE_FORMAT_DD_MMM_YYYY);
		TextColumn<DataAccount> createdColumn = new TextColumn<DataAccount>() {

			@Override
			public String getValue(DataAccount object) {
				return dtf.format(object.created);
			}

		};
		dataAccountTable.addColumn(createdColumn, "Created");

		TextColumn<DataAccount> userUsernameColumn = new TextColumn<DataAccount>() {

			@Override
			public String getValue(DataAccount object) {
				return object.user != null && object.user.username != null ? object.user.username : "-";
			}

		};
		dataAccountTable.addColumn(userUsernameColumn, "User username");

		TextColumn<DataAccount> usernameColumn = new TextColumn<DataAccount>() {

			@Override
			public String getValue(DataAccount object) {
				return object.username;
			}

		};
		dataAccountTable.addColumn(usernameColumn, "Account username");

		TextColumn<DataAccount> sourceColumn = new TextColumn<DataAccount>() {

			@Override
			public String getValue(DataAccount object) {
				return object.source.name != null ? object.source.name : "-";
			}

		};
		dataAccountTable.addColumn(sourceColumn, "Source");

		Column<DataAccount, SafeHtml> activeColumn = new Column<DataAccount, SafeHtml>(new SafeHtmlCell()) {

			@Override
			public SafeHtml getValue(DataAccount object) {
				return object.active.equals("y") ? SafeHtmlUtils.fromSafeConstant("<span class=\"icon-ok " + style.green() + "\"></span>")
						: SafeHtmlUtils.EMPTY_SAFE_HTML;
			}

		};
		dataAccountTable.addColumn(activeColumn, "Active");

		TextColumn<DataAccount> fetchErrorsColumn = new TextColumn<DataAccount>() {

			@Override
			public String getValue(DataAccount object) {
				return object.fetches != null ? String.valueOf(object.fetches.size()) : "";
			}

		};
		dataAccountTable.addColumn(fetchErrorsColumn, "Fetch errors");

		Column<DataAccount, SafeHtml> fetchColumn = new Column<DataAccount, SafeHtml>(new SafeHtmlCell()) {

			@Override
			public SafeHtml getValue(DataAccount object) {
				String id = object.id.toString();
				return SafeHtmlUtils.fromTrustedString("<a href=\""
						+ PageType.DataAccountFetchesPageType.asHref(id, FilterController.get().asDataAccountFetchFilterString()).asString()
						+ "\" class=\"btn btn-xs btn-default\">Fetches</a>");
			}

		};
		dataAccountTable.addColumn(fetchColumn);

		Column<DataAccount, String> linkToMeColumn = new Column<DataAccount, String>(new StyledButtonCell("btn", "btn-xs", "btn-warning")) {

			@Override
			public String getValue(DataAccount object) {
				return "share with me";
			}

		};
		dataAccountTable.addColumn(linkToMeColumn);

		FieldUpdater<DataAccount, String> onLinkToMe = new FieldUpdater<DataAccount, String>() {

			@Override
			public void update(int index, DataAccount object, String value) {
				preloader.show();
				DataAccountController.get().joinDataAccount(object);
			}
		};

		linkToMeColumn.setFieldUpdater(onLinkToMe);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();
		register(EventController.get().addHandlerToSource(JoinDataAccountEventHandler.TYPE, DataAccountController.get(), this));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.admin.shared.call.event.JoinDataAccountEventHandler#joinDataAccountSuccess(io.reflection.app.api.admin.shared.call.
	 * JoinDataAccountRequest, io.reflection.app.api.admin.shared.call.JoinDataAccountResponse)
	 */
	@Override
	public void joinDataAccountSuccess(JoinDataAccountRequest input, JoinDataAccountResponse output) {
		preloader.hide();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.admin.shared.call.event.JoinDataAccountEventHandler#joinDataAccountFailure(io.reflection.app.api.admin.shared.call.
	 * JoinDataAccountRequest, java.lang.Throwable)
	 */
	@Override
	public void joinDataAccountFailure(JoinDataAccountRequest input, Throwable caught) {
		preloader.hide();
	}
}
