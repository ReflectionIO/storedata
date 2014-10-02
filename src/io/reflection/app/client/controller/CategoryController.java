//
//  CategoryController.java
//  storedata
//
//  Created by Stefano Capuzzi (stefanocapuzzi) on 1 Oct 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.controller;

import io.reflection.app.api.core.client.CoreService;
import io.reflection.app.api.core.shared.call.GetCategoriesRequest;
import io.reflection.app.api.core.shared.call.GetCategoriesResponse;
import io.reflection.app.api.core.shared.call.event.GetCategoriesEventHandler.GetCategoriesFailure;
import io.reflection.app.api.core.shared.call.event.GetCategoriesEventHandler.GetCategoriesSuccess;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.datatypes.shared.Category;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author Stefano Capuzzi (stefanocapuzzi)
 * 
 */
public class CategoryController extends AsyncDataProvider<Category> implements ServiceConstants {

	private static CategoryController mOne = null;
	private List<Category> categoryList = new ArrayList<Category>();
	private long count = -1;
	private Pager pager;

	public static CategoryController get() {
		if (mOne == null) {
			mOne = new CategoryController();
		}

		return mOne;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.view.client.AbstractDataProvider#onRangeChanged(com.google.gwt.view.client.HasData)
	 */
	@Override
	protected void onRangeChanged(HasData<Category> display) {
		Range r = display.getVisibleRange();

		int start = r.getStart();
		int end = start + r.getLength();

		if (end > categoryList.size()) {
			fetchCategories();
		} else {
			updateRowData(start, categoryList.subList(start, end));
		}
	}

	/**
	 * Get Categories for a certain Store
	 */
	private void fetchCategories() {
		CoreService service = ServiceCreator.createCoreService();

		final GetCategoriesRequest input = new GetCategoriesRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();

		if (pager == null) {
			pager = new Pager();
			pager.count = STEP;
			pager.start = Long.valueOf(0);
			pager.sortDirection = SortDirectionType.SortDirectionTypeDescending;
		}
		input.pager = pager;

		input.store = StoreController.get().getStore("ios");

		service.getCategories(input, new AsyncCallback<GetCategoriesResponse>() {

			@Override
			public void onSuccess(GetCategoriesResponse output) {

				if (output.status == StatusType.StatusTypeSuccess) {
					if (output.categories != null) {
						categoryList.addAll(output.categories);
					}

					if (output.pager != null) {
						pager = output.pager;

						if (pager.totalCount != null) {
							count = pager.totalCount.longValue();
						}
					}

					updateRowCount((int) count, true);
					updateRowData(
							input.pager.start.intValue(),
							categoryList.subList(input.pager.start.intValue(),
									Math.min(input.pager.start.intValue() + input.pager.count.intValue(), pager.totalCount.intValue())));
				}

				EventController.get().fireEventFromSource(new GetCategoriesSuccess(input, output), CategoryController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				EventController.get().fireEventFromSource(new GetCategoriesFailure(input, caught), CategoryController.this);
			}

		});
	}

	public List<Category> getCategories() {
		return categoryList;
	}

	public long getCategoriesCount() {
		return count;
	}

}
