//
//  ItunesReporterCollector.java
//  storedata
//
//  Created by mamin on 18 Jan 2016.
//  Copyright © 2016 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.accountdatacollectors;

import java.util.AbstractMap.SimpleEntry;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.willshex.gson.json.service.server.InputValidationException;
import com.willshex.gson.json.service.server.ServiceException;
import com.willshex.gson.json.shared.Convert;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.ApiError;
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.DataAccountFetch;
import io.reflection.app.datatypes.shared.DataAccountFetchStatusType;
import io.reflection.app.helpers.ApiHelper;
import io.reflection.app.helpers.AppleReporterHelper;
import io.reflection.app.helpers.AppleReporterHelper.AppleReporterException;
import io.reflection.app.helpers.DataAccountPropertiesHelper;
import io.reflection.app.service.dataaccount.DataAccountServiceProvider;
import io.reflection.app.service.dataaccountfetch.DataAccountFetchServiceProvider;

/**
 * @author mamin
 *
 */
public class ItunesReporterCollector implements DataAccountCollector {
	private transient static final Logger LOG = Logger.getLogger(ItunesReporterCollector.class.getName());

	/* (non-Javadoc)
	 * @see io.reflection.app.accountdatacollectors.DataAccountCollector#collect(io.reflection.app.datatypes.shared.DataAccount, java.util.Date)
	 */
	@Override
	public boolean collect(DataAccount dataAccount, Date date) throws DataAccessException, ServiceException {
		date = ApiHelper.removeTime(date);

		final String dateParameter = ITunesConnectDownloadHelper.DATE_FORMATTER.format(date);

		if (LOG.isLoggable(Level.INFO)) {
			LOG.info(String.format("Getting data from itunes connect for data account [%s] and date [%s]", dataAccount.id == null ? dataAccount.username
					: dataAccount.id.toString(), dateParameter));
		}

		DataAccountFetch dataAccountFetch = DataAccountFetchServiceProvider.provide().getDateDataAccountFetch(dataAccount, date);

		if (dataAccountFetch == null) {
			dataAccountFetch = new DataAccountFetch();

			dataAccountFetch.date = date;
			dataAccountFetch.linkedAccount = dataAccount;
		}

		boolean success = false;

		if (dataAccountFetch.status != DataAccountFetchStatusType.DataAccountFetchStatusTypeIngested) {
			String accountId = null;
			String vendorId = null;

			List<SimpleEntry<String, String>> accountAndVendorIdsFromProperties = DataAccountPropertiesHelper.getAccountAndVendorIdsFromProperties(dataAccount.properties);

			if (accountAndVendorIdsFromProperties != null && accountAndVendorIdsFromProperties.size() > 0) {
				// we only work with the first entry as the DB should now only store one Vendor ID per data account record
				SimpleEntry<String, String> firstEntry = accountAndVendorIdsFromProperties.get(0);

				accountId = firstEntry.getKey();
				vendorId = firstEntry.getValue();
			}

			if (accountId == null || vendorId == null || accountId.trim().length() == 0 || vendorId.trim().length() == 0) {
				// if there are account and vendor ids in the properties, it means they are invalid / empty. clear them and get them again from itunes
				if (accountAndVendorIdsFromProperties != null && accountAndVendorIdsFromProperties.size() > 0) {
					dataAccount.properties = DataAccountPropertiesHelper.clearAccountAndVendorIds(dataAccount.properties);
					DataAccountServiceProvider.provide().updateDataAccountProperties(dataAccount);
				}

				SimpleEntry<String, String> accountAndVendorId = getAccountAndVendorIdPairForFirstVendorId(dataAccount);
				if (accountAndVendorId != null) {
					accountId = accountAndVendorId.getKey();
					vendorId = accountAndVendorId.getValue();

					dataAccount.properties(DataAccountPropertiesHelper.addAccountIdAndVendorIdToProperties(accountId, vendorId, dataAccount.properties));
					DataAccountServiceProvider.provide().updateDataAccountProperties(dataAccount);
				}
			}

			if (accountId == null || vendorId == null || accountId.trim().length() == 0 || vendorId.trim().length() == 0) {
				LOG.log(Level.SEVERE, "Can't get a valid set of account id and vendor id for DataAccount id: " + dataAccount.id + " with username: " + dataAccount.username);
				return false;
			}

			// do the gather
		}

		return success;
	}

	/**
	 * @param dataAccount
	 * @return
	 */
	private SimpleEntry<String, String> getAccountAndVendorIdPairForFirstVendorId(DataAccount dataAccount) {
		List<String> vendorIdsFromProperties = DataAccountPropertiesHelper.getVendorIdsFromProperties(dataAccount.properties);
		if (vendorIdsFromProperties == null || vendorIdsFromProperties.size() == 0) return null;

		String primaryVendorId = vendorIdsFromProperties.get(0);

		String accountId = getAccountIdForVendorId(dataAccount, primaryVendorId);
		if (accountId == null) return null;

		return new SimpleEntry<>(accountId, primaryVendorId);
	}

	/**
	 * @param dataAccount
	 * @param primaryVendorId
	 * @return
	 */
	private String getAccountIdForVendorId(DataAccount dataAccount, String primaryVendorId) {
		Map<String, String> accounts = null;
		String username = dataAccount.username;
		String password = dataAccount.password;

		try {
			accounts = AppleReporterHelper.getAccounts(username, password);
		} catch (AppleReporterException e) {
			LOG.log(Level.WARNING, "Could not get accounts for data account id: " + dataAccount.id + ", username: " + username, e);
		}

		if (accounts == null || accounts.size() == 0) return null;

		for (String name : accounts.keySet()) {
			String accountId = accounts.get(name);

			try {
				List<String> vendors = AppleReporterHelper.getVendors(username, password, accountId);
				if (vendors == null || vendors.size() == 0) {
					continue;
				}

				for (String vendorId : vendors) {
					if (primaryVendorId.equals(vendorId)) return accountId;
				}
			} catch (InputValidationException | AppleReporterException e) {
				LOG.log(Level.WARNING, "Could not get the vendor id for account id: " + accountId + " of data account id: " + dataAccount.id + ", username: " + username, e);
				continue;
			}
		}

		LOG.log(Level.WARNING, "No accounts matching vendor id: " + primaryVendorId + " were found under data account id: " + dataAccount.id + ", username: " + username);
		return null;
	}

	/* (non-Javadoc)
	 * @see io.reflection.app.accountdatacollectors.DataAccountCollector#validateProperties(java.lang.String)
	 */
	@Override
	public void validateProperties(String properties) throws ServiceException {
		try {
			final JsonObject jsonProperties = Convert.toJsonObject(properties);

			JsonElement element = jsonProperties.get("vendors");

			if (element == null)
				throw new InputValidationException(ITunesValidationError.NullVendorsArray.getCode(), ITunesValidationError.NullVendorsArray.getMessage());

			final JsonArray vendors = element.getAsJsonArray();

			if (vendors == null)
				throw new InputValidationException(ITunesValidationError.VendorsNotArray.getCode(), ITunesValidationError.VendorsNotArray.getMessage());

			for (int i = 0; i < vendors.size(); i++) {
				element = vendors.get(i);

				if (element != null) {
					final String vendor = element.getAsString();

					if (vendor == null)
						throw new InputValidationException(ITunesValidationError.NullVendorId.getCode(), ITunesValidationError.NullVendorId.getMessage());

					// e.g. 80012345
					if (!vendor.matches("8[0-9]{7}"))
						throw new InputValidationException(ITunesValidationError.BadVendorIdFormat.getCode(),
								ITunesValidationError.BadVendorIdFormat.getMessage());
				}
			}

		} catch (final JsonParseException pe) {
			if (LOG.isLoggable(Level.WARNING)) {
				LOG.log(Level.WARNING, String.format("Error parsing properties [%s] properties", properties), pe);
			}

			throw new InputValidationException(ApiError.JsonParseException.getCode(), ApiError.JsonParseException.getMessage());
		}
	}
}
