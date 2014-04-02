//
//  FilterController.java
//  storedata
//
//  Created by William Shakour (billy1380) on 18 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.controller;

import io.reflection.app.client.controller.NavigationController.Stack;
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

	public static final String ITEM_FILTER_KEY = "itemfilter:";
	public static final String RANK_FILTER_KEY = "rankfilter:";

	public static final String STORE_KEY = "store";
	public static final String COUNTRY_KEY = "country";
	public static final String LIST_TYPE_KEY = "listtype";
	private static final String START_DATE_KEY = "startdate";
	private static final String END_DATE_KEY = "enddate";
	private static final String CATEGORY_KEY = "category";
	public static final String DAILY_DATA_KEY = "dailydata";
	public static final String CHART_TYPE_KEY = "charttype";
	public static final String SUMMARY_TYPE_KEY = "summarytype";

	public static final String OVERALL_LIST_TYPE = "all";
	public static final String FREE_LIST_TYPE = "free";
	public static final String PAID_LIST_TYPE = "paid";
	public static final String GROSSING_LIST_TYPE = "grossing";

	public static final String REVENUE_DAILY_DATA_TYPE = "revenue";
	public static final String DOWNLOADS_DAILY_DATA_TYPE = "downloads";

	public static final String REVENUE_CHART_TYPE = "revenue";
	public static final String DOWNLOADS_CHART_TYPE = "downloads";
	public static final String RANKING_CHART_TYPE = "ranking";

	public static final String TODAY_SUMMARY_TYPE = "today";
	public static final String MONTH_SUMMARY_TYPE = "month";

	private static FilterController mOne = null;

	public static FilterController get() {
		if (mOne == null) {
			mOne = new FilterController();
		}

		return mOne;
	}

	@SuppressWarnings("serial")
	public static class Filter extends HashMap<String, Object> {
		public static Filter parse(String filter) {
			Filter parsed = null;

			String[] splitDecoded;

			if ((splitDecoded = Stack.decode(ITEM_FILTER_KEY, filter)) != null && splitDecoded.length == 8) {
				parsed = new Filter();

				parsed.setStoreA3Code(splitDecoded[0]);
				parsed.setCountryA2Code(splitDecoded[1]);
				parsed.setCategoryId(Long.valueOf(splitDecoded[2]));
				parsed.setListType(splitDecoded[3]);
				parsed.setStartTime(Long.valueOf(splitDecoded[4]));
				parsed.setEndTime(Long.valueOf(splitDecoded[5]).longValue());
				parsed.setChartType(splitDecoded[6]);
				parsed.setSummaryType(splitDecoded[7]);
			} else if ((splitDecoded = Stack.decode(RANK_FILTER_KEY, filter)) != null && splitDecoded.length == 6) {
				parsed = new Filter();

				parsed.setStoreA3Code(splitDecoded[0]);
				parsed.setCountryA2Code(splitDecoded[1]);
				parsed.setCategoryId(Long.valueOf(splitDecoded[2]));
				parsed.setListType(splitDecoded[3]);
				parsed.setEndTime(Long.valueOf(splitDecoded[4]));
				parsed.setDailyData(splitDecoded[5]);
			}

			return parsed;
		}

		public String getStoreA3Code() {
			return (String) get(STORE_KEY);
		}

		public void setStoreA3Code(String value) {
			put(STORE_KEY, value);
		}

		public String getCountryA2Code() {
			return (String) get(COUNTRY_KEY);
		}

		public void setCountryA2Code(String value) {
			put(COUNTRY_KEY, value);
		}

		public Long getCategoryId() {
			return (Long) get(CATEGORY_KEY);
		}

		public void setCategoryId(Long value) {
			put(CATEGORY_KEY, value);
		}

		public String getListType() {
			return (String) get(LIST_TYPE_KEY);
		}

		public void setListType(String value) {
			put(LIST_TYPE_KEY, value);
		}

		public Long getEndTime() {
			return (Long) get(END_DATE_KEY);
		}

		public void setEndTime(Long value) {
			put(END_DATE_KEY, value);
		}

		public Long getStartTime() {
			return (Long) get(START_DATE_KEY);
		}

		public void setStartTime(Long value) {
			put(START_DATE_KEY, value);
		}

		public String getChartType() {
			return (String) get(CHART_TYPE_KEY);
		}

		public void setChartType(String value) {
			put(CHART_TYPE_KEY, value);
		}

		public String getDailyData() {
			return (String) get(DAILY_DATA_KEY);
		}

		public void setDailyData(String value) {
			put(DAILY_DATA_KEY, value);
		}

		public String getSummaryType() {
			return (String) get(SUMMARY_TYPE_KEY);
		}

		public void setSummaryType(String value) {
			put(SUMMARY_TYPE_KEY, value);
		}

		public String asRankFilterString() {
			return Stack.encode(RANK_FILTER_KEY, getStoreA3Code(), getCountryA2Code(), getCategoryId().toString(), getListType(), getEndTime().toString(),
					getDailyData());
		}

		public String asItemFilterString() {
			return Stack.encode(ITEM_FILTER_KEY, getStoreA3Code(), getCountryA2Code(), getCategoryId().toString(), getListType(), getStartTime().toString(),
					getEndTime().toString(), getChartType(), getSummaryType());
		}
	}

	private Map<String, Object> mPreviousValues;
	private Filter mCurrentFilter = new Filter();

	private int mInTransaction = 0;

	private FilterController() {
		start();
		setStore(StoreController.IPHONE_A3_CODE);
		setListType(OVERALL_LIST_TYPE);
		setCountry("us");
		setEndDate(new Date());
		Date startDate = getEndDate();
		CalendarUtil.addDaysToDate(startDate, -30);
		setStartDate(startDate);
		setCategory(Long.valueOf(24));
		setDailyData(REVENUE_DAILY_DATA_TYPE);
		setChartType(RANKING_CHART_TYPE);
		setSummaryType(TODAY_SUMMARY_TYPE);
		commit();
	}

	public Filter getFilter() {
		return mCurrentFilter;
	}

	/**
	 * @param string
	 */
	public void setDailyData(String dailyData) {
		if (dailyData != null && !dailyData.equals(mCurrentFilter.getDailyData())) {

			String previousDailyData = mCurrentFilter.getDailyData();
			mCurrentFilter.setDailyData(dailyData);

			if (mInTransaction == 0) {

				EventController.get().fireEventFromSource(
						new FilterEventHandler.ChangedFilterParameter<String>(DAILY_DATA_KEY, mCurrentFilter.getDailyData(), previousDailyData), this);
			} else {
				if (mPreviousValues == null) {
					mPreviousValues = new HashMap<String, Object>();
				}

				mPreviousValues.put(DAILY_DATA_KEY, previousDailyData);
			}
		}

	}

	public void setStore(String store) {
		if (store != null && !store.equals(mCurrentFilter.getStoreA3Code())) {
			String previousStore = mCurrentFilter.getStoreA3Code();
			mCurrentFilter.setStoreA3Code(store);

			if (mInTransaction == 0) {
				EventController.get().fireEventFromSource(
						new FilterEventHandler.ChangedFilterParameter<String>(STORE_KEY, mCurrentFilter.getStoreA3Code(), previousStore), this);
			} else {
				if (mPreviousValues == null) {
					mPreviousValues = new HashMap<String, Object>();
				}

				mPreviousValues.put(STORE_KEY, previousStore);
			}
		}
	}

	public void setCountry(String country) {
		if (country != null && !country.equals(mCurrentFilter.getCountryA2Code())) {
			String previousCountry = mCurrentFilter.getCountryA2Code();
			mCurrentFilter.setCountryA2Code(country);

			if (mInTransaction == 0) {
				EventController.get().fireEventFromSource(
						new FilterEventHandler.ChangedFilterParameter<String>(COUNTRY_KEY, mCurrentFilter.getCountryA2Code(), previousCountry), this);
			} else {
				if (mPreviousValues == null) {
					mPreviousValues = new HashMap<String, Object>();
				}

				mPreviousValues.put(COUNTRY_KEY, previousCountry);
			}
		}
	}

	public void setListType(String listType) {
		if (listType != null && !listType.equals(mCurrentFilter.getListType())) {
			String previousListType = mCurrentFilter.getListType();
			mCurrentFilter.setListType(listType);

			if (mInTransaction == 0) {
				EventController.get().fireEventFromSource(
						new FilterEventHandler.ChangedFilterParameter<String>(LIST_TYPE_KEY, mCurrentFilter.getListType(), previousListType), this);
			} else {
				if (mPreviousValues == null) {
					mPreviousValues = new HashMap<String, Object>();
				}

				mPreviousValues.put(LIST_TYPE_KEY, previousListType);
			}
		}
	}

	public Store getStore() {
		return StoreController.get().getStore(mCurrentFilter.getStoreA3Code());
	}

	public Country getCountry() {
		return CountryController.get().getCountry(mCurrentFilter.getCountryA2Code());
	}

	public List<String> getListTypes() {
		List<String> types = new ArrayList<String>();

		if (StoreController.IPHONE_A3_CODE.equals(mCurrentFilter.getStoreA3Code())) {
			if (PAID_LIST_TYPE.equals(mCurrentFilter.getListType()) || OVERALL_LIST_TYPE.equals(mCurrentFilter.getListType())) {
				types.add("toppaidapplications");
			}

			if (FREE_LIST_TYPE.equals(mCurrentFilter.getListType()) || OVERALL_LIST_TYPE.equals(mCurrentFilter.getListType())) {
				types.add("topfreeapplications");
			}

			types.add("topgrossingapplications");
		} else if (StoreController.IPAD_A3_CODE.equals(mCurrentFilter.getStoreA3Code())) {
			if (PAID_LIST_TYPE.equals(mCurrentFilter.getListType()) || OVERALL_LIST_TYPE.equals(mCurrentFilter.getListType())) {
				types.add("toppaidipadapplications");
			}

			if (FREE_LIST_TYPE.equals(mCurrentFilter.getListType()) || OVERALL_LIST_TYPE.equals(mCurrentFilter.getListType())) {
				types.add("topfreeipadapplications");
			}

			types.add("topgrossingipadapplications");
		}

		return types;
	}

	public Date getStartDate() {
		Long startTime = mCurrentFilter.getStartTime();

		Date startDate = null;

		if (startTime != null) {
			startDate = new Date(startTime.longValue());
		}

		return startDate;
	}

	public Date getEndDate() {
		Long endTime = mCurrentFilter.getEndTime();

		Date endDate = null;

		if (endTime != null) {
			endDate = new Date(endTime.longValue());
		}

		return endDate;
	}

	public void setStartDate(Date value) {
		Long previousStartTime = mCurrentFilter.getStartTime();

		if (value != null && (previousStartTime == null || value.getTime() != previousStartTime.longValue())) {
			Date previousStartDate = null;

			if (previousStartTime != null) {
				previousStartDate = new Date(previousStartTime.longValue());
			}

			mCurrentFilter.setStartTime(Long.valueOf(value.getTime()));

			if (mInTransaction == 0) {
				EventController.get().fireEventFromSource(
						new FilterEventHandler.ChangedFilterParameter<Date>(START_DATE_KEY, new Date(mCurrentFilter.getStartTime().longValue()),
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
		Long previousEndTime = mCurrentFilter.getEndTime();

		if (value != null && (previousEndTime == null || value.getTime() != previousEndTime.longValue())) {
			Date previousEndDate = null;

			if (previousEndTime != null) {
				previousEndDate = new Date(previousEndTime.longValue());
			}

			mCurrentFilter.setEndTime(Long.valueOf(value.getTime()));

			if (mInTransaction == 0) {
				EventController.get().fireEventFromSource(
						new FilterEventHandler.ChangedFilterParameter<Date>(END_DATE_KEY, new Date(mCurrentFilter.getEndTime().longValue()), previousEndDate),
						this);
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

		if (StoreController.IPHONE_A3_CODE.equals(mCurrentFilter.getStoreA3Code())) {
			types.add("toppaidapplications");
			types.add("topfreeapplications");
			types.add("topgrossingapplications");
		} else if (StoreController.IPAD_A3_CODE.equals(mCurrentFilter.getStoreA3Code())) {
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
				EventController.get().fireEventFromSource(new FilterEventHandler.ChangedFilterParameters(mCurrentFilter, mPreviousValues), this);
			}

			mPreviousValues = null;
		}
	}

	/**
	 * @param value
	 */
	public void setCategory(Long value) {
		if (value != null && !value.equals(mCurrentFilter.getCategoryId())) {
			Long previousCategoryId = mCurrentFilter.getCategoryId();

			Category previousCategory = null;

			if (previousCategoryId != null) {
				previousCategory = new Category();
				previousCategory.id = previousCategoryId;
			}

			mCurrentFilter.setCategoryId(value);

			if (mInTransaction == 0) {
				Category currentCategory = getCategory();

				EventController.get().fireEventFromSource(
						new FilterEventHandler.ChangedFilterParameter<Category>(CATEGORY_KEY, currentCategory, previousCategory), this);
			} else {
				if (mPreviousValues == null) {
					mPreviousValues = new HashMap<String, Object>();
				}

				mPreviousValues.put(CATEGORY_KEY, previousCategory);

			}
		}
	}

	public Category getCategory() {
		Long categoryId = mCurrentFilter.getCategoryId();
		Category category = null;

		if (categoryId != null) {
			category = new Category();
			category.id = categoryId;
		}

		return category;
	}

	public void setChartType(String value) {
		if (value != null && !value.equals(mCurrentFilter.getChartType())) {
			String previousListType = mCurrentFilter.getChartType();
			mCurrentFilter.setChartType(value);

			if (mInTransaction == 0) {
				EventController.get().fireEventFromSource(
						new FilterEventHandler.ChangedFilterParameter<String>(CHART_TYPE_KEY, mCurrentFilter.getChartType(), previousListType), this);
			} else {
				if (mPreviousValues == null) {
					mPreviousValues = new HashMap<String, Object>();
				}

				mPreviousValues.put(CHART_TYPE_KEY, previousListType);
			}
		}
	}

	public void setSummaryType(String value) {
		if (value != null && !value.equals(mCurrentFilter.getSummaryType())) {
			String previousListType = mCurrentFilter.getSummaryType();
			mCurrentFilter.setSummaryType(value);

			if (mInTransaction == 0) {
				EventController.get().fireEventFromSource(
						new FilterEventHandler.ChangedFilterParameter<String>(SUMMARY_TYPE_KEY, mCurrentFilter.getSummaryType(), previousListType), this);
			} else {
				if (mPreviousValues == null) {
					mPreviousValues = new HashMap<String, Object>();
				}

				mPreviousValues.put(SUMMARY_TYPE_KEY, previousListType);
			}
		}
	}

	public String asRankFilterString() {
		return mCurrentFilter == null ? "" : mCurrentFilter.asRankFilterString();
	}

	public String asItemFilterString() {
		return mCurrentFilter == null ? "" : mCurrentFilter.asItemFilterString();
	}

	public boolean isFilterParam(String parameter) {
		return parameter != null && parameter.length() > 0 && (parameter.startsWith(RANK_FILTER_KEY) || parameter.startsWith(ITEM_FILTER_KEY));
	}
}
