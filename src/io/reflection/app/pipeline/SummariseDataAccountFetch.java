//
//  SummariseDataAccountFetch.java
//  storedata
//
//  Created by William Shakour (billy1380) on 27 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.pipeline;

import static io.reflection.app.apple.SaleTransactionTypes.FREE_OR_PAID_APP_IPAD_IOS;
import static io.reflection.app.apple.SaleTransactionTypes.FREE_OR_PAID_APP_IPHONE_AND_IPOD_TOUCH_IOS;
import static io.reflection.app.apple.SaleTransactionTypes.FREE_OR_PAID_APP_UNIVERSAL_IOS;
import static io.reflection.app.apple.SaleTransactionTypes.INAPP_PURCHASE_PURCHASE_IOS;
import static io.reflection.app.apple.SaleTransactionTypes.INAPP_PURCHASE_SUBSCRIPTION_IOS;
import static io.reflection.app.apple.SaleTransactionTypes.UPDATE_IPAD_IOS;
import static io.reflection.app.apple.SaleTransactionTypes.UPDATE_IPHONE_AND_IPOD_TOUCH_IOS;
import static io.reflection.app.apple.SaleTransactionTypes.UPDATE_UNIVERSAL_IOS;
import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.datatypes.shared.FormType;
import io.reflection.app.datatypes.shared.Sale;
import io.reflection.app.service.sale.SaleServiceProvider;
import io.reflection.app.shared.util.DataTypeHelper;
import io.reflection.app.shared.util.PagerHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.tools.pipeline.ImmediateValue;
import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;
import com.spacehopperstudios.utility.StringUtils;

/**
 * @author William Shakour (billy1380)
 *
 */
public class SummariseDataAccountFetch extends Job1<Map<String, Double>, Long> {

	private static final long serialVersionUID = 7363371689981793909L;

	public static final String DOWNLOADS_LIST_PROPERTY = "downloads";
	public static final String REVENUE_LIST_PROPERTY = "revenue";

	public static final transient ImmediateValue<String> DOWNLOADS_LIST_PROPERTY_VALUE = immediate(DOWNLOADS_LIST_PROPERTY);
	public static final transient ImmediateValue<String> REVENUE_LIST_PROPERTY_VALUE = immediate(REVENUE_LIST_PROPERTY);
	
	private String name;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.appengine.tools.pipeline.Job1#run(java.lang.Object)
	 */
	@Override
	public Value<Map<String, Double>> run(Long dataAccountFeedId) throws Exception {
		Map<String, Double> apps = new HashMap<String, Double>();

		List<Sale> sales = SaleServiceProvider.provide().getDataAccountFetchSales(DataTypeHelper.createDataAccountFetch(dataAccountFeedId),
				PagerHelper.createInfinitePager());

		Double value;
		Collection<String> keys;
		for (Sale sale : sales) {
			keys = createKeys(sale, REVENUE_LIST_PROPERTY_VALUE.getValue());

			for (String key : keys) {
				value = apps.get(key);

				if (value == null) {
					value = Double.valueOf(0);
				}

				value = Double.valueOf(value.doubleValue() + revenue(sale).doubleValue());

				apps.put(key, value);
			}

			keys = createKeys(sale, DOWNLOADS_LIST_PROPERTY_VALUE.getValue());
			for (String key : keys) {
				value = apps.get(key);

				if (value == null) {
					value = Double.valueOf(0);
				}

				value = Double.valueOf(value.doubleValue() + downloads(sale).doubleValue());

				apps.put(key, value);
			}
		}

		return immediate(apps);
	}

	public static Collection<FormType> forms(Sale sale) {
		List<FormType> forms = new ArrayList<FormType>();

		if (FREE_OR_PAID_APP_UNIVERSAL_IOS.equals(sale.typeIdentifier) || UPDATE_UNIVERSAL_IOS.equals(sale.typeIdentifier)
				|| INAPP_PURCHASE_PURCHASE_IOS.equals(sale.typeIdentifier)) {
			forms.add(FormType.FormTypeOther);
			forms.add(FormType.FormTypeTablet);
		} else if (FREE_OR_PAID_APP_IPHONE_AND_IPOD_TOUCH_IOS.equals(sale.typeIdentifier) || UPDATE_IPHONE_AND_IPOD_TOUCH_IOS.equals(sale.typeIdentifier)) {
			forms.add(FormType.FormTypeOther);
		} else if (FREE_OR_PAID_APP_IPAD_IOS.equals(sale.typeIdentifier) || UPDATE_IPAD_IOS.equals(sale.typeIdentifier)) {
			forms.add(FormType.FormTypeTablet);
		}

		return forms;
	}

	public static Collection<String> createKeys(Sale sale, String type) throws DataAccessException {
		List<String> keys = new ArrayList<String>();

		for (FormType formType : forms(sale)) {
			keys.add(StringUtils.join(Arrays.<String> asList(sale.country, DataTypeHelper.IOS_STORE_A3, formType.toString(), type, getSaleItemId(sale)), "."));
		}

		return keys;
	}

	public static Double downloads(Sale sale) {
		return Double.valueOf(Math.abs(sale.units.floatValue()) * sale.customerPrice.floatValue());
	}

	public static Double revenue(Sale sale) {
		return Double.valueOf(sale.typeIdentifier.equals(FREE_OR_PAID_APP_IPHONE_AND_IPOD_TOUCH_IOS)
				|| sale.typeIdentifier.equals(FREE_OR_PAID_APP_UNIVERSAL_IOS) || sale.typeIdentifier.equals(FREE_OR_PAID_APP_IPAD_IOS) ? sale.units.intValue()
				: 0);
	}

	public static String getSaleItemId(Sale sale) throws DataAccessException {
		String itemId;
		if (sale.typeIdentifier.equals(INAPP_PURCHASE_PURCHASE_IOS) || sale.typeIdentifier.equals(INAPP_PURCHASE_SUBSCRIPTION_IOS)) {
			itemId = SaleServiceProvider.provide().getSkuItemId(sale.parentIdentifier);
		} else {
			itemId = sale.item.internalId;
		}

		return itemId;
	}
	
	public SummariseDataAccountFetch name(String value) {
		name = value;
		return this;
	}	
	
	/* (non-Javadoc)
	 * @see com.google.appengine.tools.pipeline.Job#getJobDisplayName()
	 */
	@Override
	public String getJobDisplayName() {
		return name;
	}

}
