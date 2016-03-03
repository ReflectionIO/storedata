//
//  ItunesReporterCollector.java
//  storedata
//
//  Created by mamin on 18 Jan 2016.
//  Copyright © 2016 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.accountdatacollectors;

import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.DataAccountFetch;
import io.reflection.app.datatypes.shared.DataAccountFetchStatusType;
import io.reflection.app.helpers.ApiHelper;
import io.reflection.app.helpers.AppleReporterHelper;
import io.reflection.app.helpers.AppleReporterHelper.AppleReporterException;
import io.reflection.app.helpers.AppleReporterHelper.DateType;
import io.reflection.app.helpers.AppleReporterHelper.ITunesReporterError;
import io.reflection.app.helpers.DataAccountPropertiesHelper;
import io.reflection.app.helpers.GoogleCloudClientHelper;
import io.reflection.app.helpers.NotificationHelper;
import io.reflection.app.helpers.SqlQueryHelper;
import io.reflection.app.service.dataaccount.DataAccountServiceProvider;
import io.reflection.app.service.dataaccountfetch.DataAccountFetchServiceProvider;
import io.reflection.app.shared.util.PagerHelper;

/**
 * @author mamin
 *
 */
public class ITunesReporterCollector implements DataAccountCollector {
	private transient static final Logger		LOG				= Logger.getLogger(ITunesReporterCollector.class.getName());
	private static ITunesReporterCollector	INSTANCE	= null;

	private ITunesReporterCollector() {
	}

	/* (non-Javadoc)
	 * @see io.reflection.app.accountdatacollectors.DataAccountCollector#collect(io.reflection.app.datatypes.shared.DataAccount, java.util.Date)
	 */
	@Override
	public boolean collect(DataAccount dataAccount, Date date) throws DataAccessException, ServiceException {
		date = ApiHelper.removeTime(date);

		// // if the date requested is before iTunes Reporter started, then process it via the old iTunes Autoingest data collector.
		// if (AppleReporterHelper.isDateBeforeItunesReporterStarted(date)) {
		// LOG.info(String.format("%s is before the iTunes Report was active so using the older collector for it", date));
		//
		// return new DataAccountCollectorITunesConnect().collect(dataAccount, date);
		// }

		if (LOG.isLoggable(Level.INFO)) {
			LOG.info(String.format("Getting data from itunes connect for data account [%s] and date [%s]", dataAccount.id == null ? dataAccount.username
					: dataAccount.id.toString(), date));
		}

		DataAccountFetch dataAccountFetch = DataAccountFetchServiceProvider.provide().getDateDataAccountFetch(dataAccount, date);

		if (dataAccountFetch == null) {
			dataAccountFetch = new DataAccountFetch();

			dataAccountFetch.date = date;
			dataAccountFetch.linkedAccount = dataAccount;
		}

		if (dataAccountFetch.status != DataAccountFetchStatusType.DataAccountFetchStatusTypeIngested) {

			String accountId = dataAccount.accountId;
			String vendorId = dataAccount.vendorId;

			// If there accountid or vendor id is not set in the data account, get it from the properties or do a lookup
			if (accountId == null || vendorId == null || accountId.trim().length() == 0 || vendorId.trim().length() == 0) {
				SimpleEntry<String, String> accountAndVendorId = getPrimaryAccountAndVendorIdsFromProperties(dataAccount);

				if (accountAndVendorId != null) {
					accountId = accountAndVendorId.getKey();
					vendorId = accountAndVendorId.getValue();

					// if we were able to get the accountid and vendorid, update the data account
					if (accountId != null && vendorId != null && accountId.trim().length() > 0 && vendorId.trim().length() > 0) {
						dataAccount.accountId(accountId).vendorId(vendorId);
						DataAccountServiceProvider.provide().updateDataAccount(dataAccount, false);
					}
				}
			}

			// If we still don't have an account and vendor id pair, fail
			if (accountId == null || vendorId == null || accountId.trim().length() == 0 || vendorId.trim().length() == 0) {
				LOG.log(Level.SEVERE, "Can't get a valid set of account id and vendor id for DataAccount id: " + dataAccount.id + " with username: " + dataAccount.username);
				return false;
			}

			// do the gather
			try {
				byte[] gzippedReportData = AppleReporterHelper.getReport(dataAccount.username, dataAccount.password, accountId, vendorId, DateType.DAILY, date);
				String fileName = "S_D_A_" + accountId + "_V_" + vendorId + SqlQueryHelper.getSqlDateFormat().format(date) + ".txt.gz";

				try {
					String bucketName = System.getProperty(ACCOUNT_DATA_BUCKET_KEY);
					String bucketPath = dataAccount.id.toString() + "/" + fileName;

					String gcsPath = GoogleCloudClientHelper.uploadFileToGoogleCloudStorage(bucketName, bucketPath, gzippedReportData);
					dataAccountFetch.status(DataAccountFetchStatusType.DataAccountFetchStatusTypeGathered);
					dataAccountFetch.data(gcsPath);
					dataAccountFetch = DataAccountFetchServiceProvider.provide().updateDataAccountFetch(dataAccountFetch);

					DataAccountFetchServiceProvider.provide().triggerDataAccountFetchIngest(dataAccountFetch);

					// ================== IF ALL GOES WELL, WE EXIT THIS METHOD HERE ========================== //
					return true;
				} catch (IOException e) {
					throw new AppleReporterException(-1, "There was an error uploading the file to Google Cloud Storage", e);
				}
			} catch (AppleReporterException e) {
				ITunesReporterError error = ITunesReporterError.getByCode(e.getErrorCode());

				if (error == null) {
					dataAccountFetch.status(DataAccountFetchStatusType.DataAccountFetchStatusTypeError);
					dataAccountFetch.data(e.getErrorCode() + ":" + e.getMessage());
					dataAccountFetch = DataAccountFetchServiceProvider.provide().updateDataAccountFetch(dataAccountFetch);

					LOG.log(Level.WARNING, "Exception occured while trying to get iTunes Report via the Reporter", e);
					return false;
				}

				if (error == ITunesReporterError.CODE_213) {
					// report not available as there were no sales
					LOG.log(Level.INFO, "There were no sales for data account id: " + dataAccount.id + " with username: " + dataAccount.username + " on " + date);

					dataAccountFetch.status(DataAccountFetchStatusType.DataAccountFetchStatusTypeEmpty);
					dataAccountFetch.data(e.getErrorCode() + ":" + e.getMessage());
					dataAccountFetch = DataAccountFetchServiceProvider.provide().updateDataAccountFetch(dataAccountFetch);

					// ================== IF THERE ARE NO SALES AND HENCE AN EMPTY REPORT, WE EXIT THIS METHOD HERE ========================== //
					return true;
				}

				if (error == ITunesReporterError.CODE_210) {
					LOG.log(Level.WARNING, "The report is not ready yet for data account id: " + dataAccount.id + " with username: " + dataAccount.username + " on " + date);
				} else if (error == ITunesReporterError.CODE_107 || error == ITunesReporterError.CODE_108) {
					dataAccount.active = "n";
					DataAccountServiceProvider.provide().updateDataAccount(dataAccount, false);

					List<DataAccount> alternativeAccounts = getActiveDataAccountsWithVendorId(vendorId);
					DataAccount replacementAccount = null;
					for (DataAccount alternativeAccount : alternativeAccounts) {
						if (alternativeAccount.id.equals(dataAccount.id)) {
							continue;
						}

						replacementAccount = alternativeAccount;
					}

					if (replacementAccount != null) {
						DataAccountServiceProvider.provide().triggerDataAccountFetch(replacementAccount);
					}

					String emailBody = "Username / password is incorrect for data account id: " + dataAccount.id + " with username: " + dataAccount.username + " on " + date +
							".\nDisabling fetching from this account. Replacement account queued to fetch sales data - id: " + replacementAccount == null ? "None"
									: (replacementAccount.id + " with username: " + replacementAccount.username);

					LOG.log(Level.WARNING, emailBody);

					NotificationHelper.sendEmail("hello@reflection.io (Reflection)", "support@reflection.io", "Reflection", "Disabling account due to invalid credentials", emailBody, false);
				} else {
					dataAccountFetch.status(DataAccountFetchStatusType.DataAccountFetchStatusTypeError);
					dataAccountFetch.data(e.getErrorCode() + ":" + e.getMessage());
					dataAccountFetch = DataAccountFetchServiceProvider.provide().updateDataAccountFetch(dataAccountFetch);

					LOG.log(Level.WARNING, "The was an exception while trying to get the sales report for data account id: " + dataAccount.id + " with username: " + dataAccount.username + " on " + date);
				}

				// ================== LOG ALL REASONS OF FAILURE HERE ========================== //
				dataAccountFetch.status(DataAccountFetchStatusType.DataAccountFetchStatusTypeError);
				dataAccountFetch.data(e.getErrorCode() + ":" + e.getMessage());
				dataAccountFetch = DataAccountFetchServiceProvider.provide().updateDataAccountFetch(dataAccountFetch);
			}
		}

		return false;
	}

	/**
	 * @param dataAccount
	 * @return
	 * @throws DataAccessException
	 */
	public SimpleEntry<String, String> getPrimaryAccountAndVendorIdsFromProperties(DataAccount dataAccount) throws DataAccessException {
		String accountId = null;
		String vendorId = null;

		List<SimpleEntry<String, String>> accountAndVendorIdsFromProperties = DataAccountPropertiesHelper.getAccountAndVendorIdsFromProperties(dataAccount.properties);

		// First we load the account id and vendor id (previously identified) to use to download the report
		if (accountAndVendorIdsFromProperties != null && accountAndVendorIdsFromProperties.size() > 0) {
			// we only work with the first entry as the DB should now only store one Vendor ID per data account record
			SimpleEntry<String, String> firstEntry = accountAndVendorIdsFromProperties.get(0);

			accountId = firstEntry.getKey();
			vendorId = firstEntry.getValue();
		}

		// If an ID pair was not already identified and stored, then look up the pair based on the first vendor Id provided
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

		if (accountId == null || vendorId == null) return null;

		return new SimpleEntry<String, String>(accountId, vendorId);
	}

	/**
	 * @param vendorId
	 * @return
	 * @throws DataAccessException
	 */
	private List<DataAccount> getActiveDataAccountsWithVendorId(String requiredVendorId) throws DataAccessException {
		if (requiredVendorId == null) return new ArrayList<DataAccount>(0);

		final Pager pager = PagerHelper.createInfinitePager();
		final List<DataAccount> dataAccounts = DataAccountServiceProvider.provide().getActiveDataAccounts(pager);

		ArrayList<DataAccount> matchedDataAccounts = new ArrayList<>();

		for (final DataAccount dataAccount : dataAccounts) {
			String vendorId = DataAccountPropertiesHelper.getPrimaryVendorId(dataAccount.properties);
			if (requiredVendorId.equals(vendorId)) {
				matchedDataAccounts.add(dataAccount);
			}
		}

		return matchedDataAccounts;
	}

	/**
	 * @param dataAccount
	 * @return
	 */
	public SimpleEntry<String, String> getAccountAndVendorIdPairForFirstVendorId(DataAccount dataAccount) {
		String primaryVendorId = DataAccountPropertiesHelper.getPrimaryVendorId(dataAccount.properties);
		if (primaryVendorId == null) return null;

		String accountId = null;
		try {
			accountId = AppleReporterHelper.getAccountIdForVendorId(dataAccount.username, dataAccount.password, primaryVendorId);
		} catch (AppleReporterException e) {
			LOG.log(Level.WARNING, "Could not get the vendor id for account id: " + accountId + " of data account id: " + dataAccount.id + ", username: " + dataAccount.username, e);
		}

		if (accountId == null) return null;

		return new SimpleEntry<>(accountId, primaryVendorId);
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

	/**
	 *
	 */
	public static ITunesReporterCollector getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ITunesReporterCollector();
		}

		return INSTANCE;
	}
}
