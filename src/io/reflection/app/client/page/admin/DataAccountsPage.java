//
//  DataAccountsPage.java
//  storedata
//
//  Created by Stefano Capuzzi on 7 Oct 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.admin;

import static io.reflection.app.client.helper.FormattingHelper.DATE_FORMATTER_DD_MMM_YYYY;
import io.reflection.app.client.cell.StyledButtonCell;
import io.reflection.app.client.controller.DataAccountController;
import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.controller.ServiceConstants;
import io.reflection.app.client.page.Page;
import io.reflection.app.client.page.PageType;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.SimplePager;
import io.reflection.app.client.res.Images;
import io.reflection.app.client.res.Styles;
import io.reflection.app.datatypes.shared.DataAccount;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
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
public class DataAccountsPage extends Page {

	private static DataAccountsPageUiBinder uiBinder = GWT.create(DataAccountsPageUiBinder.class);

	interface DataAccountsPageUiBinder extends UiBinder<Widget, DataAccountsPage> {}

	interface DataAccountsPageStyle extends CssResource {
		String green();

	}

	@UiField DataAccountsPageStyle style;

	@UiField(provided = true) CellTable<DataAccount> dataAccountTable = new CellTable<DataAccount>(ServiceConstants.STEP_VALUE, BootstrapGwtCellTable.INSTANCE);
	@UiField(provided = true) SimplePager simplePager = new SimplePager(false, false);

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

		TextColumn<DataAccount> createdColumn = new TextColumn<DataAccount>() {

			@Override
			public String getValue(DataAccount object) {
				return DATE_FORMATTER_DD_MMM_YYYY.format(object.created);
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
				return object.active.equals("y") ? SafeHtmlUtils.fromSafeConstant("<span class=\""
						+ Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBefore() + " "
						+ Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBeforeCheck() + "\"></span>") : SafeHtmlUtils.EMPTY_SAFE_HTML;
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
						+ PageType.DataAccountFetchesPageType.asHref(id, FilterController.get().asDataAccountFetchFilterString()).asString() + "\" class=\""
						+ Styles.STYLES_INSTANCE.reflectionMainStyle().refButtonFunctionSmall() + "\">Fetches</a>");
			}

		};
		dataAccountTable.addColumn(fetchColumn);

		Column<DataAccount, String> linkToMeColumn = new Column<DataAccount, String>(new StyledButtonCell(Styles.STYLES_INSTANCE.reflectionMainStyle()
				.refButtonFunctionSmall())) {

			@Override
			public String getValue(DataAccount object) {
				return "share with me";
			}

		};
		dataAccountTable.addColumn(linkToMeColumn);

		FieldUpdater<DataAccount, String> onLinkToMe = new FieldUpdater<DataAccount, String>() {

			@Override
			public void update(int index, DataAccount object, String value) {
				DataAccountController.get().joinDataAccount(object);
			}
		};

		linkToMeColumn.setFieldUpdater(onLinkToMe);

	}
}
