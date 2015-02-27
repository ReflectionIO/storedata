//
//  CategoriesPage.java
//  storedata
//
//  Created by Stefano Capuzzi (stefanocapuzzi) on 1 Oct 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.admin;

import io.reflection.app.api.core.shared.call.GetCategoriesRequest;
import io.reflection.app.api.core.shared.call.GetCategoriesResponse;
import io.reflection.app.api.core.shared.call.event.GetCategoriesEventHandler;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.controller.CategoryController;
import io.reflection.app.client.controller.ServiceConstants;
import io.reflection.app.client.page.Page;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.SimplePager;
import io.reflection.app.client.res.Images;
import io.reflection.app.datatypes.shared.Category;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author Stefano Capuzzi (stefanocapuzzi)
 * 
 */
public class CategoriesPage extends Page implements GetCategoriesEventHandler {

	private static CategoriesPageUiBinder uiBinder = GWT.create(CategoriesPageUiBinder.class);

	interface CategoriesPageUiBinder extends UiBinder<Widget, CategoriesPage> {}

	@UiField(provided = true) CellTable<Category> tableCategories = new CellTable<Category>(ServiceConstants.STEP_VALUE, BootstrapGwtCellTable.INSTANCE);
	@UiField(provided = true) SimplePager simplePager = new SimplePager(false, false);

	public CategoriesPage() {
		initWidget(uiBinder.createAndBindUi(this));

		addCategoryColumns();

		tableCategories.setLoadingIndicator(new Image(Images.INSTANCE.preloader()));
		CategoryController.get().addDataDisplay(tableCategories);
		simplePager.setDisplay(tableCategories);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		register(DefaultEventBus.get().addHandlerToSource(GetCategoriesEventHandler.TYPE, CategoryController.get(), this));
	}

	private void addCategoryColumns() {

		TextColumn<Category> nameColumn = new TextColumn<Category>() {

			@Override
			public String getValue(Category object) {
				return object.name == null ? "-" : object.name;
			}

		};

		TextHeader nameHeader = new TextHeader("Name");
		nameHeader.setHeaderStyleNames("col-md-1");
		tableCategories.addColumn(nameColumn, nameHeader);

		TextColumn<Category> idColumn = new TextColumn<Category>() {

			@Override
			public String getValue(Category object) {
				return object.id == null || object.internalId == null ? "-" : object.id.toString() + " (" + object.internalId + ")";
			}

		};

		TextHeader idHeader = new TextHeader("ID (internal ID)");
		idHeader.setHeaderStyleNames("col-md-1");
		tableCategories.addColumn(idColumn, idHeader);

		TextColumn<Category> parentColumn = new TextColumn<Category>() {

			@Override
			public String getValue(Category object) {
				Category c = null;
				return object.parent == null || object.parent.id == null ? "-"
						: (c = CategoryController.get().getCategory(object.parent.id)) == null ? object.parent.id.toString() : c.name + " ("
								+ object.parent.id.toString() + ")";
			}

		};

		TextHeader parentHeader = new TextHeader("Parent");
		parentHeader.setHeaderStyleNames("col-md-1");
		tableCategories.addColumn(parentColumn, parentHeader);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.core.shared.call.event.GetCategoriesEventHandler#getCategoriesSuccess(io.reflection.app.api.core.shared.call.GetCategoriesRequest,
	 * io.reflection.app.api.core.shared.call.GetCategoriesResponse)
	 */
	@Override
	public void getCategoriesSuccess(GetCategoriesRequest input, GetCategoriesResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {
			if (CategoryController.get().getCategoriesCount() > output.pager.count) {
				simplePager.setVisible(true);
			} else {
				simplePager.setVisible(false);
			}
		} else {
			simplePager.setVisible(false);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.core.shared.call.event.GetCategoriesEventHandler#getCategoriesFailure(io.reflection.app.api.core.shared.call.GetCategoriesRequest,
	 * java.lang.Throwable)
	 */
	@Override
	public void getCategoriesFailure(GetCategoriesRequest input, Throwable caught) {
		simplePager.setVisible(false);
	}

}
