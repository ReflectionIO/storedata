//
//  DataAccountCollectorITunesConnect.java
//  storedata
//
//  Created by William Shakour (billy1380) on 2 Jan 2014.
//  Copyright © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyright © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.accountdatacollectors;

import io.reflection.app.api.shared.ApiError;
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.DataAccountFetch;
import io.reflection.app.datatypes.shared.DataAccountFetchStatusType;
import io.reflection.app.datatypes.shared.Event;
import io.reflection.app.datatypes.shared.Notification;
import io.reflection.app.datatypes.shared.User;
import io.reflection.app.helpers.ApiHelper;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.service.dataaccountfetch.DataAccountFetchServiceProvider;
import io.reflection.app.service.event.EventServiceProvider;
import io.reflection.app.service.notification.NotificationServiceProvider;
import io.reflection.app.service.user.UserServiceProvider;
import io.reflection.app.shared.util.DataTypeHelper;

import java.util.Date;
import java.util.logging.Logger;

import co.spchopr.persistentmap.PersistentMapFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.willshex.gson.json.service.server.InputValidationException;
import com.willshex.gson.json.service.server.ServiceException;
import com.willshex.gson.json.shared.Convert;

/**
 * @author billy1380
 * 
 */
public class DataAccountCollectorITunesConnect implements DataAccountCollector {

	private static final Logger LOG = Logger.getLogger(DataAccountCollectorITunesConnect.class.getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.accountdatacollectors.DataAccountCollector#validateProperties(java.lang.String)
	 */
	@Override
	public void validateProperties(String properties) throws ServiceException {
		try {
			JsonObject jsonProperties = Convert.toJsonObject(properties);

			JsonElement element = jsonProperties.get("vendors");

			if (element == null)
				throw new InputValidationException(ITunesValidationError.NullVendorsArray.getCode(), ITunesValidationError.NullVendorsArray.getMessage());

			JsonArray vendors = element.getAsJsonArray();

			if (vendors == null)
				throw new InputValidationException(ITunesValidationError.VendorsNotArray.getCode(), ITunesValidationError.VendorsNotArray.getMessage());

			for (int i = 0; i < vendors.size(); i++) {
				element = vendors.get(i);

				if (element != null) {
					String vendor = element.getAsString();

					if (vendor == null)
						throw new InputValidationException(ITunesValidationError.NullVendorId.getCode(), ITunesValidationError.NullVendorId.getMessage());

					// e.g. 80012345
					if (!vendor.matches("8[0-9]{7}"))
						throw new InputValidationException(ITunesValidationError.BadVendorIdFormat.getCode(),
								ITunesValidationError.BadVendorIdFormat.getMessage());
				}
			}

		} catch (JsonParseException pe) {
			if (LOG.isLoggable(GaeLevel.WARNING)) {
				LOG.log(GaeLevel.WARNING, String.format("Error parsing properties [%s] properties", properties), pe);
			}

			throw new InputValidationException(ApiError.JsonParseException.getCode(), ApiError.JsonParseException.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.accountdatacollectors.DataAccountCollector#collect(io.reflection.app.shared.datatypes.DataAccount, java.util.Date)
	 */
	@Override
	public boolean collect(DataAccount dataAccount, Date date) throws ServiceException {
		date = ApiHelper.removeTime(date);

		String dateParameter = ITunesConnectDownloadHelper.DATE_FORMATTER.format(date);

		if (LOG.isLoggable(GaeLevel.INFO)) {
			LOG.info(String.format("Getting data from itunes connect for data account [%s] and date [%s]", dataAccount.id == null ? dataAccount.username
					: dataAccount.id.toString(), dateParameter));
		}

		boolean success = false;
		String cloudFileName = null, error = null;
		try {
			cloudFileName = ITunesConnectDownloadHelper.getITunesSalesFile(dataAccount.username, dataAccount.password,
					ITunesConnectDownloadHelper.getVendorId(dataAccount.properties), dateParameter, System.getProperty(ACCOUNT_DATA_BUCKET_KEY),
					dataAccount.id.toString());
		} catch (Exception ex) {
			error = ex.getMessage();
		}

		DataAccountFetch dataAccountFetch = DataAccountFetchServiceProvider.provide().getDateDataAccountFetch(dataAccount, date);

		if (dataAccountFetch == null) {
			dataAccountFetch = new DataAccountFetch();

			dataAccountFetch.date = date;
			dataAccountFetch.linkedAccount = dataAccount;
		}

		if (dataAccountFetch.status != DataAccountFetchStatusType.DataAccountFetchStatusTypeIngested) {
			if (error != null) {
				if (error.startsWith("There are no reports") || error.startsWith("There is no report")) {
					dataAccountFetch.status = DataAccountFetchStatusType.DataAccountFetchStatusTypeEmpty;
					success = true;
				} else {
					dataAccountFetch.status = DataAccountFetchStatusType.DataAccountFetchStatusTypeError;
				}

				dataAccountFetch.data = error;

			} else if (cloudFileName != null) {
				dataAccountFetch.status = DataAccountFetchStatusType.DataAccountFetchStatusTypeGathered;
				dataAccountFetch.data = cloudFileName;
				success = true;
			} else {
				dataAccountFetch.status = DataAccountFetchStatusType.DataAccountFetchStatusTypeEmpty;
				dataAccountFetch.data = "Internal: The report was empty but there was no error from itunes connect";
				success = true;
			}

			if (dataAccountFetch.id == null) {
				dataAccountFetch = DataAccountFetchServiceProvider.provide().addDataAccountFetch(dataAccountFetch);
			} else {
				dataAccountFetch = DataAccountFetchServiceProvider.provide().updateDataAccountFetch(dataAccountFetch);
			}

			if (dataAccountFetch != null && dataAccountFetch.status == DataAccountFetchStatusType.DataAccountFetchStatusTypeGathered) {
				// once the data is collected
				DataAccountFetchServiceProvider.provide().triggerDataAccountFetchIngest(dataAccountFetch);
			}

			if (error != null) {
				if (PersistentMapFactory.createObjectify().get(dataAccount.id.toString()) == null) {
					if (error.equals(DataTypeHelper.SALES_GATHER_CREDENTIAL_ERROR_EVENT_CODE)) {
						PersistentMapFactory.createObjectify().put(dataAccount.id.toString(),
								DataTypeHelper.SALES_GATHER_CREDENTIAL_ERROR_EVENT_CODE + ":first");
					} else {
						PersistentMapFactory.createObjectify().put(dataAccount.id.toString(), error);
					}
				} else if (PersistentMapFactory.createObjectify().get(dataAccount.id.toString())
						.equals(DataTypeHelper.SALES_GATHER_CREDENTIAL_ERROR_EVENT_CODE + ":first")) {
					// User dataAccountOwner = UserServiceProvider.provide().getDataAccountOwner(dataAccount);
					// Store in datastore the last fetch result for this data account
					// if (PersistentMapFactory.createObjectify().get(dataAccount.id.toString() == nu)
					// .equals("Error :Your AppleConnect account or password was entered incorrectly.")) {}
					// Error :Your AppleConnect account or password was entered incorrectly.
				} else {

				}
				User adminUser = UserServiceProvider.provide().getUsernameUser("chi@reflection.io");
				Event event = null;
				if (error.equals("Error :Your AppleConnect account or password was entered incorrectly.")) {
					// event = EventServiceProvider.provide().getCodeEvent(DataTypeHelper.SALES_GATHER_CREDENTIAL_ERROR_EVENT_CODE);
				} else {
					// event = EventServiceProvider.provide().getCodeEvent(DataTypeHelper.SALES_GATHER_GENERIC_ERROR_EVENT_CODE);
				}
				// Notify admin about the gather error
				Notification notificationAdmin = (new Notification()).event(event).user(adminUser);
				NotificationServiceProvider.provide().addNotification(notificationAdmin);
			} else {
				PersistentMapFactory.createObjectify().put(dataAccount.id.toString(), null);
			}

		} else {
			LOG.warning(String.format("Gather for data account [%s] and date [%s] skipped because of status [%s]",
					dataAccount.id == null ? dataAccount.username : dataAccount.id.toString(), dateParameter, dataAccountFetch.status));
		}

		return success;
	}
	// /**
	// * THIS CODE HAS TO BE REMOVED AFTER THE CLOSED BETA
	// *
	// * @throws DataAccessException
	// *
	// */
	// private void BETA_GrantAccess(DataAccount dataAccount) throws DataAccessException {
	// User owner = UserServiceProvider.provide().getDataAccountOwner(dataAccount);
	//
	// Role betaUser = new Role();
	// betaUser.id = new Long(5);
	//
	// if (!UserServiceProvider.provide().hasRole(owner, betaUser)) {
	// UserServiceProvider.provide().assignRole(owner, betaUser);
	// }
	// }
}
