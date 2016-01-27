//
//  SalesReportHelper.java
//  storedata
//
//  Created by mamin on 27 Jan 2016.
//  Copyright © 2016 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.helpers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.IOUtils;

import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Sale;

/**
 * @author mamin
 *
 */
public class SalesReportHelper {
	private transient static final Logger	LOG														= Logger.getLogger(SalesReportHelper.class.getName());

	private static final int							PROVIDER_INDEX								= 0;																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																							// seems
	private static final int							PROVIDER_COUNTRY_INDEX				= 1;																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																							// seems
	private static final int							SKU_INDEX											= 2;
	private static final int							DEVELOPER_INDEX								= 3;
	private static final int							TITLE_INDEX										= 4;
	private static final int							VERSION_INDEX									= 5;
	private static final int							PRODUCT_TYPE_IDENTIFIER_INDEX	= 6;
	private static final int							UNITS_INDEX										= 7;
	private static final int							DEVELOPER_PROCEEDS_INDEX			= 8;
	private static final int							BEGIN_DATE_INDEX							= 9;
	private static final int							END_DATE_INDEX								= 10;
	private static final int							CUSTOMER_CURRENCY_INDEX				= 11;
	private static final int							COUNTRY_CODE_INDEX						= 12;
	private static final int							CURRENCY_OF_PROCEEDS_INDEX		= 13;
	private static final int							APPLE_IDENTIFIER_INDEX				= 14;
	private static final int							CUSTOMER_PRICE_INDEX					= 15;
	private static final int							PROMO_CODE_INDEX							= 16;
	private static final int							PARENT_IDENTIFIER_INDEX				= 17;
	private static final int							SUBSCRIPTION_INDEX						= 18;
	private static final int							PERIOD_INDEX									= 19;
	private static final int							CATEGORY_INDEX								= 20;
	private static final int							CMB_INDEX											= 21;
	private static final int							DEVICE_INDEX									= 22;
	private static final int							SUPPORTED_PLATFORMS_INDEX			= 23;

	public static List<Sale> convertReportToSales(byte[] compressedReport, DataAccount dataAccount) {
		try {
			List<String> reportLines = IOUtils.readLines(new GZIPInputStream(new ByteArrayInputStream(compressedReport)));
			return convertReportToSales(reportLines.subList(1, reportLines.size()), dataAccount);
		} catch (IOException e) {
			LOG.log(Level.WARNING, "Could not read the contents of the compressed report. The report is " + (compressedReport == null ? "null" : compressedReport.length + " bytes long"), e);
		}

		return null;
	}

	public static List<Sale> convertReportToSales(List<String> reportLines, DataAccount dataAccount) {
		ArrayList<Sale> sales = new ArrayList<>(reportLines.size());

		for (String line : reportLines) {
			String[] split = line.split("\\t");

			Sale sale = new Sale();

			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);

			sale.item = new Item();
			sale.item.internalId = split[APPLE_IDENTIFIER_INDEX];

			sale.account = dataAccount;

			sale.sku = split[SKU_INDEX];
			sale.developer = split[DEVELOPER_INDEX];
			sale.title = split[TITLE_INDEX];
			sale.version = split[VERSION_INDEX];
			sale.typeIdentifier = split[PRODUCT_TYPE_IDENTIFIER_INDEX];
			sale.units = Integer.parseInt(split[UNITS_INDEX]);

			Float proceeds = null;
			try {
				proceeds = Float.valueOf(split[DEVELOPER_PROCEEDS_INDEX]);
			} catch (NumberFormatException e) {
				LOG.log(Level.WARNING, String.format("Exception occured while obtaining DEVELOPER PROCEEDS"), e);
			}

			sale.proceeds = proceeds;

			try {
				sale.begin = sdf.parse(split[BEGIN_DATE_INDEX]);
				sale.end = sdf.parse(split[END_DATE_INDEX]);
			} catch (ParseException e) {
				LOG.log(Level.WARNING, String.format("Exception occured while obtaining Sales begin / end date(s)"), e);
			}

			sale.customerCurrency = split[CUSTOMER_CURRENCY_INDEX];
			sale.country = split[COUNTRY_CODE_INDEX].toLowerCase();
			sale.currency = split[CURRENCY_OF_PROCEEDS_INDEX];

			sale.item = new Item();
			sale.item.internalId = split[APPLE_IDENTIFIER_INDEX];

			Float customerPrice = null;
			try {
				customerPrice = Float.valueOf(split[CUSTOMER_PRICE_INDEX]);
			} catch (NumberFormatException e) {
				LOG.log(Level.WARNING, String.format("Exception occured while obtaining CUSTOMER_PRICE"), e);
			}

			sale.customerPrice = customerPrice;
			sale.promoCode = split[PROMO_CODE_INDEX];
			sale.parentIdentifier = split[PARENT_IDENTIFIER_INDEX];
			sale.subscription = split[SUBSCRIPTION_INDEX];
			sale.period = split[PERIOD_INDEX];
			sale.category = split[CATEGORY_INDEX];

			if (split.length > DEVICE_INDEX) {
				String device = split[DEVICE_INDEX];
				sale.device = device;

				if ("1F".equals(sale.typeIdentifier)) {
					if ("iPad".equals(device)) {
						sale.typeIdentifier("1T");
					} else if ("iPhone".equals(device) || "iPod touch".equals(device)) {
						sale.typeIdentifier("1");
					}
				} else if ("3F".equals(sale.typeIdentifier)) {
					if ("iPad".equals(device)) {
						sale.typeIdentifier("3T");
					} else if ("iPhone".equals(device) || "iPod touch".equals(device)) {
						sale.typeIdentifier("3");
					}
				} else if ("7F".equals(sale.typeIdentifier)) {
					if ("iPad".equals(device)) {
						sale.typeIdentifier("7T");
					} else if ("iPhone".equals(device) || "iPod touch".equals(device)) {
						sale.typeIdentifier("7");
					}
				}
			}

			sales.add(sale);
		}

		return sales;
	}
}
