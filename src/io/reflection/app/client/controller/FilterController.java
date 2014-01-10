//
//  FilterController.java
//  storedata
//
//  Created by William Shakour (billy1380) on 18 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.controller;

import io.reflection.app.client.handler.FilterEventHandler;
import io.reflection.app.datatypes.shared.Country;
import io.reflection.app.datatypes.shared.Store;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author billy1380
 * 
 */
public class FilterController {

	public static final String STORE_KEY = "store";
	public static final String COUNTRY_KEY = "country";
	private static final String LIST_TYPE_KEY = "list.type";
	private static final String START_DATE_KEY = "start.date";
	private static final String END_DATE_KEY = "end.date";

	private static FilterController mOne = null;

	private boolean mInTransaction = false;

	private Map<String, Object> mPreviousValues;
	private Map<String, Object> mCurrentValues = new HashMap<String, Object>();

	public static FilterController get() {
		if (mOne == null) {
			mOne = new FilterController();
		}

		return mOne;
	}

	public void setStore(String store) {
		if (store != null && !store.equals(mCurrentValues.get(STORE_KEY))) {
			String previousStore = (String) mCurrentValues.get(STORE_KEY);
			mCurrentValues.put(STORE_KEY, store);

			if (mInTransaction) {
				if (mPreviousValues == null) {
					mPreviousValues = new HashMap<String, Object>();
				}

				mPreviousValues.put(STORE_KEY, previousStore);
			} else {
				EventController.get().fireEventFromSource(
						new FilterEventHandler.ChangedFilterParameter<String>(STORE_KEY, (String) mCurrentValues.get(STORE_KEY), previousStore), this);
			}
		}
	}

	public void setCountry(String country) {
		if (country != null && !country.equals(mCurrentValues.get(COUNTRY_KEY))) {
			String previousCountry = (String) mCurrentValues.get(COUNTRY_KEY);
			mCurrentValues.put(COUNTRY_KEY, country);

			if (mInTransaction) {
				if (mPreviousValues == null) {
					mPreviousValues = new HashMap<String, Object>();
				}

				mPreviousValues.put(COUNTRY_KEY, previousCountry);
			} else {
				EventController.get().fireEventFromSource(
						new FilterEventHandler.ChangedFilterParameter<String>(COUNTRY_KEY, (String) mCurrentValues.get(COUNTRY_KEY), previousCountry), this);
			}
		}
	}

	public void setListType(String listType) {
		if (listType != null && !listType.equals(mCurrentValues.get(LIST_TYPE_KEY))) {
			String previousListType = (String) mCurrentValues.get(LIST_TYPE_KEY);
			mCurrentValues.put(LIST_TYPE_KEY, listType);

			if (mInTransaction) {
				if (mPreviousValues == null) {
					mPreviousValues = new HashMap<String, Object>();
				}

				mPreviousValues.put(LIST_TYPE_KEY, previousListType);
			} else {
				EventController.get().fireEventFromSource(
						new FilterEventHandler.ChangedFilterParameter<String>(LIST_TYPE_KEY, (String) mCurrentValues.get(LIST_TYPE_KEY), previousListType),
						this);
			}
		}
	}

	public Store getStore() {
		return StoreController.get().getStore((String) mCurrentValues.get(STORE_KEY));
	}

	public Country getCountry() {
		return CountryController.get().getCountry((String) mCurrentValues.get(COUNTRY_KEY));
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
		return (Date) mCurrentValues.get(START_DATE_KEY);
	}

	public Date getEndDate() {
		return (Date) mCurrentValues.get(END_DATE_KEY);
	}

	public void setStartDate(Date value) {
		if (value != null && !value.equals(mCurrentValues.get(START_DATE_KEY))) {
			Date previousStartDate = (Date) mCurrentValues.get(START_DATE_KEY);
			mCurrentValues.put(START_DATE_KEY, value);

			if (mInTransaction) {
				if (mPreviousValues == null) {
					mPreviousValues = new HashMap<String, Object>();
				}

				mPreviousValues.put(START_DATE_KEY, previousStartDate);
			} else {
				EventController.get()
						.fireEventFromSource(
								new FilterEventHandler.ChangedFilterParameter<Date>(START_DATE_KEY, (Date) mCurrentValues.get(START_DATE_KEY),
										previousStartDate), this);
			}
		}
	}

	public void setEndDate(Date value) {
		if (value != null && !value.equals(mCurrentValues.get(END_DATE_KEY))) {
			Date previousEndDate = (Date) mCurrentValues.get(END_DATE_KEY);
			mCurrentValues.put(END_DATE_KEY, value);

			if (mInTransaction) {
				if (mPreviousValues == null) {
					mPreviousValues = new HashMap<String, Object>();
				}

				mPreviousValues.put(END_DATE_KEY, previousEndDate);
			} else {
				EventController.get()
						.fireEventFromSource(
								new FilterEventHandler.ChangedFilterParameter<Date>(END_DATE_KEY, (Date) mCurrentValues.get(END_DATE_KEY),
										previousEndDate), this);
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
		mInTransaction = true;
	}

	/**
	 * 
	 */
	public void commit() {
		mInTransaction = false;

		if (mPreviousValues != null) {
			EventController.get().fireEventFromSource(new FilterEventHandler.ChangedFilterParameters(mCurrentValues, mPreviousValues), this);
		}

		mPreviousValues = null;
	}
}
