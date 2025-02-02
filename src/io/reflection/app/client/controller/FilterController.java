//
//  FilterController.java
//  storedata
//
//  Created by William Shakour (billy1380) on 18 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.controller;

import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.handler.FilterEventHandler;
import io.reflection.app.client.helper.FilterHelper;
import io.reflection.app.client.page.PageType;
import io.reflection.app.client.part.datatypes.DateRange;
import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.datatypes.shared.Country;
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.Store;
import io.reflection.app.shared.util.DataTypeHelper;

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

	public static final String ITEM_FILTER_KEY = "itemfilter:";
	public static final String RANK_FILTER_KEY = "rankfilter:";
	public static final String MYAPP_FILTER_KEY = "myappfilter:";
	public static final String FEED_FILTER_KEY = "feedfilter:";
	public static final String DATA_ACCOUNT_FETCH_FILTER_KEY = "dataaccountfetchfilter:";

	public static final String LINKED_ACCOUNT_KEY = "linkedaccount";
	public static final String STORE_KEY = "store";
	public static final String COUNTRY_KEY = "country";
	public static final String LIST_TYPE_KEY = "listtype";
	public static final String START_DATE_KEY = "startdate";
	public static final String END_DATE_KEY = "enddate";
	public static final String CATEGORY_KEY = "category";
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

	private PageType pageTypeFilter;

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
				parsed.setEndTime(Long.valueOf(splitDecoded[5]));
				parsed.setChartType(splitDecoded[6]);
				parsed.setSummaryType(splitDecoded[7]);

			} else if ((splitDecoded = Stack.decode(RANK_FILTER_KEY, filter)) != null && splitDecoded.length == 5) {
				parsed = new Filter();
				parsed.setStoreA3Code(splitDecoded[0]);
				parsed.setCountryA2Code(splitDecoded[1]);
				parsed.setCategoryId(Long.valueOf(splitDecoded[2]));
				parsed.setEndTime(Long.valueOf(splitDecoded[3]));
				parsed.setDailyData(splitDecoded[4]);

			} else if ((splitDecoded = Stack.decode(MYAPP_FILTER_KEY, filter)) != null && splitDecoded.length == 5) {
				parsed = new Filter();
				parsed.setStoreA3Code(splitDecoded[0]);
				parsed.setCountryA2Code(splitDecoded[1]);
				parsed.setStartTime(Long.valueOf(splitDecoded[2]));
				parsed.setEndTime(Long.valueOf(splitDecoded[3]));
				parsed.setLinkedAccountId(Long.valueOf(splitDecoded[4]));

			} else if ((splitDecoded = Stack.decode(FEED_FILTER_KEY, filter)) != null && splitDecoded.length == 6) {
				parsed = new Filter();
				parsed.setListType(splitDecoded[0]);
				parsed.setStoreA3Code(splitDecoded[1]);
				parsed.setCountryA2Code(splitDecoded[2]);
				parsed.setCategoryId(Long.valueOf(splitDecoded[3]));
				parsed.setStartTime(Long.valueOf(splitDecoded[4]));
				parsed.setEndTime(Long.valueOf(splitDecoded[5]));

			} else if ((splitDecoded = Stack.decode(DATA_ACCOUNT_FETCH_FILTER_KEY, filter)) != null && splitDecoded.length == 2) {
				parsed = new Filter();
				parsed.setStartTime(Long.valueOf(splitDecoded[0]));
				parsed.setEndTime(Long.valueOf(splitDecoded[1]));
			}

			return parsed;
		}

		public Long getLinkedAccountId() {
			return (Long) get(LINKED_ACCOUNT_KEY);
		}

		public void setLinkedAccountId(Long value) {
			put(LINKED_ACCOUNT_KEY, value);
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
			return Stack.encode(RANK_FILTER_KEY, getStoreA3Code(), getCountryA2Code(), getCategoryId().toString(), getEndTime().toString(), getDailyData());
		}

		public String asItemFilterString() {
			return Stack.encode(ITEM_FILTER_KEY, getStoreA3Code(), getCountryA2Code(), getCategoryId().toString(), getListType(), getStartTime().toString(),
					getEndTime().toString(), getChartType(), getSummaryType());
		}

		public String asMyAppsFilterString() {
			return Stack.encode(MYAPP_FILTER_KEY, getStoreA3Code(), getCountryA2Code(), getStartTime().toString(), getEndTime().toString(),
					getLinkedAccountId().toString());
		}

		public String asFeedFilterString() {
			return Stack.encode(FEED_FILTER_KEY, getListType(), getStoreA3Code(), getCountryA2Code(), getCategoryId().toString(), getStartTime().toString(),
					getEndTime().toString());
		}

		public String asDataAccountFetchFilterString() {
			return Stack.encode(DATA_ACCOUNT_FETCH_FILTER_KEY, getStartTime().toString(), getEndTime().toString());
		}

		/**
		 * @param part
		 * @return
		 */
		public static boolean isFilter(String part) {
			return false;
		}

		public Filter copyFilter() {
			Filter filterCopy = new Filter();
			filterCopy.setCategoryId(getCategoryId());
			filterCopy.setChartType(getChartType());
			filterCopy.setCountryA2Code(getCountryA2Code());
			filterCopy.setDailyData(getDailyData());
			filterCopy.setEndTime(getEndTime());
			filterCopy.setLinkedAccountId(getLinkedAccountId());
			filterCopy.setListType(getListType());
			filterCopy.setStartTime(getStartTime());
			filterCopy.setStoreA3Code(getStoreA3Code());
			filterCopy.setSummaryType(getSummaryType());
			return filterCopy;
		}
	}

	private Map<PageType, Filter> filters = new HashMap<PageType, Filter>();
	private Map<String, Object> mPreviousValues;
	private Filter mCurrentFilter = new Filter();
	private Filter defaultFilter = new Filter();
	private Filter adminFilter = new Filter();

	private int mInTransaction = 0;

	private FilterController() {
		defaultFilter = mCurrentFilter;
		start();
		setLinkedAccount(Long.valueOf(0));
		setStore(DataTypeHelper.STORE_IPHONE_A3_CODE);
		setListType(OVERALL_LIST_TYPE);
		setCountry("gb");
		setEndDate(FilterHelper.getDaysAgo(FilterHelper.DEFAULT_LEADERBOARD_LAG_DAYS));
		setStartDate(FilterHelper.getDaysAgo(32));
		setCategory(Long.valueOf(15));
		setDailyData(REVENUE_DAILY_DATA_TYPE);
		setChartType(RANKING_CHART_TYPE);
		setSummaryType(TODAY_SUMMARY_TYPE);
		commit();

		defaultFilter = getDefaultFilter();
		adminFilter = getDefaultFilter();
		adminFilter.setStartTime(FilterHelper.getDaysAgo(30).getTime());

		filters.put(PageType.RanksPageType, getDefaultFilter());
		filters.put(PageType.MyAppsPageType, getDefaultFilter());
		filters.put(PageType.ItemPageType, getDefaultFilter());

		filters.put(PageType.FeedBrowserPageType, getAdminFilter());
		filters.put(PageType.DataAccountFetchesPageType, getAdminFilter());

	}

	public Filter getFilter() {
		return mCurrentFilter;
	}

	private Filter getDefaultFilter() {
		return defaultFilter.copyFilter();
	}

	private Filter getAdminFilter() {
		return adminFilter.copyFilter();
	}

	/**
	 * Set the proper filter page - Item Page filter will reflect the changes made in other pages, but not the opposite
	 *
	 * @param p
	 */
	public void setFilter(String stackValue) {
		if (stackValue != null) {
			String[] allParts = stackValue.split("/");
			if (allParts[0].equals("!ranks") && pageTypeFilter != PageType.RanksPageType) {
				pageTypeFilter = PageType.RanksPageType;
				mCurrentFilter = filters.get(pageTypeFilter);
			}
			if (allParts[0].equals("!users") && allParts.length > 1 && allParts[1].equals("myapps") && pageTypeFilter != PageType.MyAppsPageType) {
				pageTypeFilter = PageType.MyAppsPageType;
				mCurrentFilter = filters.get(pageTypeFilter);
			}
			if ((allParts[0].equals("!feedbrowser") || allParts[0].equals("!simplemodelrun")) && pageTypeFilter != PageType.FeedBrowserPageType) {
				pageTypeFilter = PageType.FeedBrowserPageType;
				mCurrentFilter = filters.get(pageTypeFilter);
			}
			if (allParts[0].equals("!dataaccountfetches") && pageTypeFilter != PageType.DataAccountFetchesPageType) {
				pageTypeFilter = PageType.DataAccountFetchesPageType;
				mCurrentFilter = filters.get(pageTypeFilter);
			}
			if (allParts[0].equals("!item") && pageTypeFilter != PageType.ItemPageType) { // Dump object reference and make item filter independent again
				pageTypeFilter = PageType.ItemPageType;
				mCurrentFilter = Filter.parse(allParts[5]);
			}
			filters.put(PageType.ItemPageType, mCurrentFilter);
		}

	}

	public void setLinkedAccount(Long linkedAccountId) {

		if (linkedAccountId != null && !linkedAccountId.equals(mCurrentFilter.getLinkedAccountId())) {
			Long previousLinkedAccount = mCurrentFilter.getLinkedAccountId();
			mCurrentFilter.setLinkedAccountId(linkedAccountId);

			if (mInTransaction == 0) {
				DefaultEventBus.get().fireEventFromSource(
						new FilterEventHandler.ChangedFilterParameter<Long>(LINKED_ACCOUNT_KEY, mCurrentFilter.getLinkedAccountId(), previousLinkedAccount),
						this);
			} else {
				if (mPreviousValues == null) {
					mPreviousValues = new HashMap<String, Object>();
				}

				mPreviousValues.put(LINKED_ACCOUNT_KEY, previousLinkedAccount);
			}
		}
	}

	/**
	 * @param string
	 */
	public void setDailyData(String dailyData) {
		if (dailyData != null && !dailyData.equals(mCurrentFilter.getDailyData())) {

			String previousDailyData = mCurrentFilter.getDailyData();
			mCurrentFilter.setDailyData(dailyData);

			if (mInTransaction == 0) {

				DefaultEventBus.get().fireEventFromSource(
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
				DefaultEventBus.get().fireEventFromSource(
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
				DefaultEventBus.get().fireEventFromSource(
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
				DefaultEventBus.get().fireEventFromSource(
						new FilterEventHandler.ChangedFilterParameter<String>(LIST_TYPE_KEY, mCurrentFilter.getListType(), previousListType), this);
			} else {
				if (mPreviousValues == null) {
					mPreviousValues = new HashMap<String, Object>();
				}

				mPreviousValues.put(LIST_TYPE_KEY, previousListType);
			}
		}
	}

	public DataAccount getLinkedAccount() {
		return LinkedAccountController.get().getLinkedAccount(Long.valueOf(mCurrentFilter.getLinkedAccountId()));
	}

	public Store getStore() {
		return StoreController.get().getStore(mCurrentFilter.getStoreA3Code());
	}

	public Country getCountry() {
		return CountryController.get().getCountry(mCurrentFilter.getCountryA2Code());
	}

	public List<String> getListTypes() {
		List<String> types = new ArrayList<String>();

		if (DataTypeHelper.STORE_IPHONE_A3_CODE.equals(mCurrentFilter.getStoreA3Code())) {
			if (PAID_LIST_TYPE.equals(mCurrentFilter.getListType()) || OVERALL_LIST_TYPE.equals(mCurrentFilter.getListType())) {
				types.add("toppaidapplications");
			}

			if (FREE_LIST_TYPE.equals(mCurrentFilter.getListType()) || OVERALL_LIST_TYPE.equals(mCurrentFilter.getListType())) {
				types.add("topfreeapplications");
			}

			types.add("topgrossingapplications");
		} else if (DataTypeHelper.STORE_IPAD_A3_CODE.equals(mCurrentFilter.getStoreA3Code())) {
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
		Date previousStartDate = null;

		if (value != null && (previousStartTime == null || !FilterHelper.equalDate(value, previousStartDate = new Date(previousStartTime.longValue())))) {
			mCurrentFilter.setStartTime(Long.valueOf(value.getTime()));

			if (mInTransaction == 0) {
				DefaultEventBus.get().fireEventFromSource(new FilterEventHandler.ChangedFilterParameter<Date>(START_DATE_KEY, value, previousStartDate), this);
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
		Date previousEndDate = null;

		if (value != null && (previousEndTime == null || !FilterHelper.equalDate(value, previousEndDate = new Date(previousEndTime.longValue())))) {
			mCurrentFilter.setEndTime(Long.valueOf(value.getTime()));

			if (mInTransaction == 0) {
				DefaultEventBus.get().fireEventFromSource(new FilterEventHandler.ChangedFilterParameter<Date>(END_DATE_KEY, value, previousEndDate), this);
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

		if (DataTypeHelper.STORE_IPHONE_A3_CODE.equals(mCurrentFilter.getStoreA3Code())) {
			types.add("toppaidapplications");
			types.add("topfreeapplications");
			types.add("topgrossingapplications");
		} else if (DataTypeHelper.STORE_IPAD_A3_CODE.equals(mCurrentFilter.getStoreA3Code())) {
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

	public void commit() {
		commit(true);
	}

	/**
	 *
	 */
	public void commit(boolean fireEvents) {
		mInTransaction--;

		if (mInTransaction == 0) {
			if (mPreviousValues != null) {
				if (fireEvents) {
					DefaultEventBus.get().fireEventFromSource(new FilterEventHandler.ChangedFilterParameters(mCurrentFilter, mPreviousValues), this);
				}

				mPreviousValues = null;
			}
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

				DefaultEventBus.get().fireEventFromSource(
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
				DefaultEventBus.get().fireEventFromSource(
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
				DefaultEventBus.get().fireEventFromSource(
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
		return filters.get(PageType.RanksPageType) == null ? "" : filters.get(PageType.RanksPageType).asRankFilterString();
	}

	public String asItemFilterString() {
		return filters.get(PageType.ItemPageType) == null ? "" : filters.get(PageType.ItemPageType).asItemFilterString();
	}

	public String asMyAppsFilterString() {
		return filters.get(PageType.MyAppsPageType) == null ? "" : filters.get(PageType.MyAppsPageType).asMyAppsFilterString();
	}

	public String asFeedFilterString() {
		return filters.get(PageType.FeedBrowserPageType) == null ? "" : filters.get(PageType.FeedBrowserPageType).asFeedFilterString();
	}

	public String asDataAccountFetchFilterString() {
		return filters.get(PageType.DataAccountFetchesPageType) == null ? "" : filters.get(PageType.DataAccountFetchesPageType)
				.asDataAccountFetchFilterString();
	}

	public boolean isFilterParam(String parameter) {
		return parameter != null && parameter.length() > 0 && (parameter.startsWith(RANK_FILTER_KEY) || parameter.startsWith(ITEM_FILTER_KEY));
	}

	/**
	 * @param part
	 */
	public void fromParameter(String part) {
		if (mPreviousValues == null) {
			Filter parameterFitler = Filter.parse(part);

			if (parameterFitler != null && parameterFitler.size() > 0) {
				start();

				setLinkedAccount(parameterFitler.getLinkedAccountId());
				setCategory(parameterFitler.getCategoryId());
				setChartType(parameterFitler.getChartType());
				setCountry(parameterFitler.getCountryA2Code());
				setDailyData(parameterFitler.getDailyData());
				setEndDate(parameterFitler.getEndTime() == null ? null : new Date(parameterFitler.getEndTime().longValue()));
				setListType(parameterFitler.getListType());
				setStartDate(parameterFitler.getStartTime() == null ? null : new Date(parameterFitler.getStartTime().longValue()));
				setStore(parameterFitler.getStoreA3Code());
				setSummaryType(parameterFitler.getSummaryType());

				commit();
			}
		}
	}

	public DateRange getDateRange() {
		return FilterHelper.createRange(getStartDate(), getEndDate());
	}

	public void reset() {
		pageTypeFilter = null;
		filters.put(PageType.RanksPageType, getDefaultFilter());
		filters.put(PageType.MyAppsPageType, getDefaultFilter());
		filters.put(PageType.ItemPageType, getDefaultFilter());
		filters.put(PageType.FeedBrowserPageType, getAdminFilter());
		filters.put(PageType.DataAccountFetchesPageType, getAdminFilter());
	}

}
