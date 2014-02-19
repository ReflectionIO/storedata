//
//  FilterController.java
//  storedata
//
//  Created by William Shakour (billy1380) on 18 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.controller;

import io.reflection.app.client.handler.FilterEventHandler;
import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.datatypes.shared.Country;
import io.reflection.app.datatypes.shared.Store;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.datepicker.client.CalendarUtil;

/**
 * @author billy1380
 * 
 */
public class FilterController {

	public static final String STORE_KEY = "store";
	public static final String COUNTRY_KEY = "country";
	private static final String LIST_TYPE_KEY = "listtype";
	private static final String START_DATE_KEY = "startdate";
	private static final String END_DATE_KEY = "enddate";
	private static final String CATEGORY_KEY = "category";

	private static FilterController mOne = null;

	private int mInTransaction = 0;

	private Map<String, Object> mPreviousValues;
	private Map<String, Object> mCurrentValues = new HashMap<String, Object>();

	public static FilterController get() {
		if (mOne == null) {
			mOne = new FilterController();
		}

		return mOne;
	}

	private FilterController() {
		start();
		setStore(StoreController.IPHONE_A3_CODE);
		setListType("all");
		setCountry("us");
		setEndDate(new Date());
		Date startDate = getEndDate();
		CalendarUtil.addDaysToDate(startDate, -10);
		setStartDate(startDate);
		setCategory(Long.valueOf(24));
		commit();
	}

	public void setStore(String store) {
		if (store != null && !store.equals(mCurrentValues.get(STORE_KEY))) {
			String previousStore = (String) mCurrentValues.get(STORE_KEY);
			mCurrentValues.put(STORE_KEY, store);

			if (mInTransaction == 0) {
				EventController.get().fireEventFromSource(
						new FilterEventHandler.ChangedFilterParameter<String>(STORE_KEY, (String) mCurrentValues.get(STORE_KEY), previousStore), this);
			} else {
				if (mPreviousValues == null) {
					mPreviousValues = new HashMap<String, Object>();
				}

				mPreviousValues.put(STORE_KEY, previousStore);
			}
		}
	}

	public void setCountry(String country) {
		if (country != null && !country.equals(mCurrentValues.get(COUNTRY_KEY))) {
			String previousCountry = (String) mCurrentValues.get(COUNTRY_KEY);
			mCurrentValues.put(COUNTRY_KEY, country);

			if (mInTransaction == 0) {
				EventController.get().fireEventFromSource(
						new FilterEventHandler.ChangedFilterParameter<String>(COUNTRY_KEY, (String) mCurrentValues.get(COUNTRY_KEY), previousCountry), this);
			} else {
				if (mPreviousValues == null) {
					mPreviousValues = new HashMap<String, Object>();
				}

				mPreviousValues.put(COUNTRY_KEY, previousCountry);
			}
		}
	}

	public void setListType(String listType) {
		if (listType != null && !listType.equals(mCurrentValues.get(LIST_TYPE_KEY))) {
			String previousListType = (String) mCurrentValues.get(LIST_TYPE_KEY);
			mCurrentValues.put(LIST_TYPE_KEY, listType);

			if (mInTransaction == 0) {
				EventController.get().fireEventFromSource(
						new FilterEventHandler.ChangedFilterParameter<String>(LIST_TYPE_KEY, (String) mCurrentValues.get(LIST_TYPE_KEY), previousListType),
						this);
			} else {
				if (mPreviousValues == null) {
					mPreviousValues = new HashMap<String, Object>();
				}

				mPreviousValues.put(LIST_TYPE_KEY, previousListType);
			}
		}
	}

	public Store getStore() {
		return StoreController.get().getStore((String) mCurrentValues.get(STORE_KEY));
	}

	public Country getCountry() {
		return CountryController.get().getCountry((String) mCurrentValues.get(COUNTRY_KEY));
	}

	public String getCountryA2Code() {
		return (String) mCurrentValues.get(COUNTRY_KEY);
	}

	public List<String> getListTypes() {
		List<String> types = new ArrayList<String>();

		if (StoreController.IPHONE_A3_CODE.equals(mCurrentValues.get(STORE_KEY))) {
			if ("paid".equals(mCurrentValues.get(LIST_TYPE_KEY)) || "all".equals(mCurrentValues.get(LIST_TYPE_KEY))) {
				types.add("toppaidapplications");
			}

			if ("free".equals(mCurrentValues.get(LIST_TYPE_KEY)) || "all".equals(mCurrentValues.get(LIST_TYPE_KEY))) {
				types.add("topfreeapplications");
			}

			types.add("topgrossingapplications");
		} else if (StoreController.IPAD_A3_CODE.equals(mCurrentValues.get(STORE_KEY))) {
			if ("paid".equals(mCurrentValues.get(LIST_TYPE_KEY)) || "all".equals(mCurrentValues.get(LIST_TYPE_KEY))) {
				types.add("toppaidipadapplications");
			}

			if ("free".equals(mCurrentValues.get(LIST_TYPE_KEY)) || "all".equals(mCurrentValues.get(LIST_TYPE_KEY))) {
				types.add("topfreeipadapplications");
			}

			types.add("topgrossingipadapplications");
		}

		return types;
	}

	public Date getStartDate() {
		Long startTime = (Long) mCurrentValues.get(START_DATE_KEY);

		Date startDate = null;

		if (startTime != null) {
			startDate = new Date(startTime.longValue());
		}

		return startDate;
	}

	public Date getEndDate() {
		Long endTime = (Long) mCurrentValues.get(END_DATE_KEY);

		Date endDate = null;

		if (endTime != null) {
			endDate = new Date(endTime.longValue());
		}

		return endDate;
	}

	public void setStartDate(Date value) {
		Long previousStartTime = (Long) mCurrentValues.get(START_DATE_KEY);

		if (value != null && (previousStartTime == null || value.getTime() != previousStartTime.longValue())) {
			Date previousStartDate = null;

			if (previousStartTime != null) {
				previousStartDate = new Date(previousStartTime.longValue());
			}

			mCurrentValues.put(START_DATE_KEY, Long.valueOf(value.getTime()));

			if (mInTransaction == 0) {
				EventController.get().fireEventFromSource(
						new FilterEventHandler.ChangedFilterParameter<Date>(START_DATE_KEY, new Date(((Long) mCurrentValues.get(START_DATE_KEY)).longValue()),
								previousStartDate), this);
			} else {
				if (mPreviousValues == null) {
					mPreviousValues = new HashMap<String, Object>();
				}

				mPreviousValues.put(START_DATE_KEY, previousStartDate);
			}

		}
	}

	public void setEndDate(Date value) {
		Long previousEndTime = (Long) mCurrentValues.get(END_DATE_KEY);

		if (value != null && (previousEndTime == null || value.getTime() != previousEndTime.longValue())) {
			Date previousEndDate = null;

			if (previousEndTime != null) {
				previousEndDate = new Date(previousEndTime.longValue());
			}

			mCurrentValues.put(END_DATE_KEY, Long.valueOf(value.getTime()));

			if (mInTransaction == 0) {
				EventController.get().fireEventFromSource(
						new FilterEventHandler.ChangedFilterParameter<Date>(END_DATE_KEY, new Date(((Long) mCurrentValues.get(END_DATE_KEY)).longValue()),
								previousEndDate), this);
			} else {
				if (mPreviousValues == null) {
					mPreviousValues = new HashMap<String, Object>();
				}

				mPreviousValues.put(END_DATE_KEY, previousEndDate);
			}
		}
	}

	public List<String> getAllListTypes() {
		List<String> types = new ArrayList<String>();

		if (StoreController.IPHONE_A3_CODE.equals(mCurrentValues.get(STORE_KEY))) {
			types.add("toppaidapplications");
			types.add("topfreeapplications");
			types.add("topgrossingapplications");
		} else if (StoreController.IPAD_A3_CODE.equals(mCurrentValues.get(STORE_KEY))) {
			types.add("toppaidipadapplications");
			types.add("topfreeipadapplications");
			types.add("topgrossingipadapplications");
		}

		return types;
	}

	/**
	 * 
	 */
	public void start() {
		mInTransaction++;
	}

	/**
	 * 
	 */
	public void commit() {
		mInTransaction--;

		if (mInTransaction == 0) {
			if (mPreviousValues != null) {
				EventController.get().fireEventFromSource(new FilterEventHandler.ChangedFilterParameters(mCurrentValues, mPreviousValues), this);
			}

			mPreviousValues = null;
		}
	}

	/**
	 * @param value
	 */
	public void setCategory(Long value) {
		if (value != null && !value.equals(mCurrentValues.get(CATEGORY_KEY))) {
			Long previousCategoryId = (Long) mCurrentValues.get(CATEGORY_KEY);

			Category previousCategory = null;

			if (previousCategoryId != null) {
				previousCategory = new Category();
				previousCategory.id = previousCategoryId;
			}

			mCurrentValues.put(CATEGORY_KEY, value);

			if (mInTransaction == 0) {
				EventController.get().fireEventFromSource(
						new FilterEventHandler.ChangedFilterParameter<Category>(CATEGORY_KEY, (Category) mCurrentValues.get(CATEGORY_KEY), previousCategory),
						this);
			} else {
				if (mPreviousValues == null) {
					mPreviousValues = new HashMap<String, Object>();
				}

				mPreviousValues.put(CATEGORY_KEY, previousCategory);

			}
		}
	}

	/**
	 * @return
	 */
	public Category getCategory() {
		Long categoryId = (Long) mCurrentValues.get(CATEGORY_KEY);
		Category category = null;

		if (categoryId != null) {
			category = new Category();
			category.id = categoryId;
		}

		return category;
	}

	// /*
	// * (non-Javadoc)
	// *
	// * @see java.lang.Object#toString()
	// */
	// @Override
	// public String toString() {
	// String filter = "/";
	//
	// for (String key : mCurrentValues.keySet()) {
	// filter += mCurrentValues.get(key).toString() + "/";
	// }
	//
	// return filter;
	// }

	/**
	 * @return
	 */
	public String getStoreA3Code() {
		return (String) mCurrentValues.get(STORE_KEY);
	}

	// /**
	// * @param stack
	// * @return
	// */
	// public boolean fromStack(Stack stack) {
	// return false;
	// }

	public String toRankFilterString() {
		List<String> listTypes = getListTypes();
		String listType;

		if (listTypes.size() > 1) {
			listType = "all";
		} else {
			listType = listTypes.get(0);
		}

		return "/" + getStoreA3Code() + "/" + getCountryA2Code() + "/" + getCategory().id.toString() + "/" + listType + "/" + getEndTime();
	}

	/**
	 * @return
	 */
	public Long getEndTime() {
		return (Long) mCurrentValues.get(END_DATE_KEY);
	}

	public Long getStartTime() {
		return (Long) mCurrentValues.get(START_DATE_KEY);
	}

}
