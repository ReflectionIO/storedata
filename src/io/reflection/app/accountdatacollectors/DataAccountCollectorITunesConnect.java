//
//  DataAccountCollectorITunesConnect.java
//  storedata
//
//  Created by William Shakour (billy1380) on 2 Jan 2014.
//  Copyright © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyright © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.accountdatacollectors;

import static io.reflection.app.shared.util.DataTypeHelper.PERMISSION_HAS_LINKED_ACCOUNT_CODE;
import static io.reflection.app.shared.util.DataTypeHelper.SALES_GATHER_CREDENTIAL_ERROR_EVENT_CODE;
import static io.reflection.app.shared.util.DataTypeHelper.SALES_GATHER_GENERIC_ERROR_EVENT_CODE;
import io.reflection.app.api.shared.ApiError;
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.DataAccountFetch;
import io.reflection.app.datatypes.shared.DataAccountFetchStatusType;
import io.reflection.app.datatypes.shared.Event;
import io.reflection.app.datatypes.shared.Notification;
import io.reflection.app.datatypes.shared.Permission;
import io.reflection.app.datatypes.shared.User;
import io.reflection.app.helpers.ApiHelper;
import io.reflection.app.helpers.NotificationHelper;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.service.dataaccountfetch.DataAccountFetchServiceProvider;
import io.reflection.app.service.dataaccountfetch.IDataAccountFetchService;
import io.reflection.app.service.event.EventServiceProvider;
import io.reflection.app.service.notification.NotificationServiceProvider;
import io.reflection.app.service.permission.PermissionServiceProvider;
import io.reflection.app.service.user.UserServiceProvider;
import io.reflection.app.shared.util.PagerHelper;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import co.spchopr.persistentmap.PersistentMap;
import co.spchopr.persistentmap.PersistentMapFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.spacehopperstudios.utility.StringUtils;
import com.willshex.gson.json.service.server.InputValidationException;
import com.willshex.gson.json.service.server.ServiceException;
import com.willshex.gson.json.shared.Convert;

/**
 * @author billy1380
 * 
 */
public class DataAccountCollectorITunesConnect implements DataAccountCollector {

	private static final Logger LOG = Logger.getLogger(DataAccountCollectorITunesConnect.class.getName());
	private static final String GATHER_ERROR_KEY_PREFIX = "sales.gather.error";

	private static final long MAX_ALLOWED_ERRORS = 3L;

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
	public DataAccountFetch collect(DataAccount dataAccount, Date date) throws ServiceException {
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

		DataAccountFetchStatusType existingFetchWorseStatus = null;
		if (dataAccountFetch.status != DataAccountFetchStatusType.DataAccountFetchStatusTypeIngested) {
			if (error != null) {
				if (error.startsWith("There are no reports") || error.startsWith("There is no report")) {
					dataAccountFetch.status = DataAccountFetchStatusType.DataAccountFetchStatusTypeEmpty;
					success = true;
				} else {
					if (dataAccountFetch.id != null && dataAccountFetch.status != DataAccountFetchStatusType.DataAccountFetchStatusTypeError) {
						existingFetchWorseStatus = DataAccountFetchStatusType.DataAccountFetchStatusTypeError;
					} else {
						dataAccountFetch.status = DataAccountFetchStatusType.DataAccountFetchStatusTypeError;
					}
				}

				dataAccountFetch.data = error;

			} else if (cloudFileName != null) {
				dataAccountFetch.status = DataAccountFetchStatusType.DataAccountFetchStatusTypeGathered;
				dataAccountFetch.data = cloudFileName;
				success = true;
			} else {
				if (dataAccountFetch.id != null && dataAccountFetch.status == DataAccountFetchStatusType.DataAccountFetchStatusTypeGathered) {
					existingFetchWorseStatus = DataAccountFetchStatusType.DataAccountFetchStatusTypeEmpty;
				} else {
					dataAccountFetch.status = DataAccountFetchStatusType.DataAccountFetchStatusTypeEmpty;
					dataAccountFetch.data = "Internal: The report was empty but there was no error from itunes connect";
				}

				success = true;
			}

			// don't do anything if there is an existing fetch with a worse new status
			if (existingFetchWorseStatus == null) {
				if (dataAccountFetch.id == null) {
					dataAccountFetch = DataAccountFetchServiceProvider.provide().addDataAccountFetch(dataAccountFetch);
				} else {
					dataAccountFetch = DataAccountFetchServiceProvider.provide().updateDataAccountFetch(dataAccountFetch);
				}

				// Manage notifications in case of error
				PersistentMap persistentMap = PersistentMapFactory.createObjectify();

				if (!success) {
					// Recognise the event error type
					Event event = null;
					boolean informOwnerAndRevokePermission = false;
					if (error != null && error.contains("account or password was entered incorrectly")) {
						event = EventServiceProvider.provide().getCodeEvent(SALES_GATHER_CREDENTIAL_ERROR_EVENT_CODE);
						informOwnerAndRevokePermission = true;
					} else {
						event = EventServiceProvider.provide().getCodeEvent(SALES_GATHER_GENERIC_ERROR_EVENT_CODE);
					}

					String persistentKey = StringUtils.join(Arrays.asList(GATHER_ERROR_KEY_PREFIX, dataAccount.id.toString(), event.code), ".");

					if (!persistentMap.contains(persistentKey)) { // Last gather wasn't an error
						Map<String, Object> parameters = new HashMap<String, Object>();

						User dataAccountOwner = UserServiceProvider.provide().getDataAccountOwner(dataAccount);

						// in the unlikely event of an account not having a user
						// don't add it
						if (dataAccountOwner != null) {
							parameters.put("user", dataAccountOwner);
						}
						
						parameters.put("dataaccount", dataAccount);
						parameters.put("dataaccountfetch", dataAccountFetch);

						String body = NotificationHelper.inflate(parameters, event.longBody);
						String subject = NotificationHelper.inflate(parameters, event.subject);

						if (dataAccountOwner != null && informOwnerAndRevokePermission) {
							NotificationServiceProvider.provide().addNotification(
									(new Notification()).event(event).user(dataAccountOwner).body(body).subject(subject));

							// FIXME: this method (getDataAccountsIds) should have an active account variant
							List<Long> userDataAccountIds = UserServiceProvider.provide().getDataAccountsIds(dataAccountOwner,
									PagerHelper.createInfinitePager());

							boolean revoke = false;
							if (userDataAccountIds.size() <= 1) {
								revoke = true;
							} else {
								long accountsWithLessThanMaxErrors = userDataAccountIds.size();

								IDataAccountFetchService dataAccountFetchService = DataAccountFetchServiceProvider.provide();
								for (Long id : userDataAccountIds) {
									if (dataAccountFetchService.getFailedDataAccountFetchesCount((DataAccount) (new DataAccount()).id(id)).longValue() > MAX_ALLOWED_ERRORS) {
										accountsWithLessThanMaxErrors--;
									}
								}

								if (accountsWithLessThanMaxErrors == 0) {
									revoke = true;
								}
							}

							if (revoke) {
								// Revoke the has linked account permission
								Permission hla = PermissionServiceProvider.provide().getCodePermission(PERMISSION_HAS_LINKED_ACCOUNT_CODE);
								UserServiceProvider.provide().revokePermission(dataAccountOwner, hla);
							}
						}

						// Notify admin about the gather error
						User adminUser = UserServiceProvider.provide().getUsernameUser("chi@reflection.io");
						NotificationServiceProvider.provide().addNotification((new Notification()).event(event).user(adminUser).body(body).subject(subject));

						persistentMap.put(persistentKey, Integer.valueOf(1));
					} else {
						persistentMap.put(persistentKey, Integer.valueOf(((Integer) persistentMap.get(persistentKey)).intValue() + 1));
					}
				} else {
					persistentMap.delete(StringUtils.join(
							Arrays.asList(GATHER_ERROR_KEY_PREFIX, dataAccount.id.toString(), SALES_GATHER_CREDENTIAL_ERROR_EVENT_CODE), "."));
					persistentMap.delete(StringUtils.join(
							Arrays.asList(GATHER_ERROR_KEY_PREFIX, dataAccount.id.toString(), SALES_GATHER_GENERIC_ERROR_EVENT_CODE), "."));
				}
			} else {
				if (LOG.isLoggable(GaeLevel.INFO)) {
					LOG.info(String.format("Gather for data account [%s] and date [%s] skipped because of status [%s] is worse than [%]",
							dataAccount.id == null ? dataAccount.username : dataAccount.id.toString(), dateParameter, existingFetchWorseStatus,
							dataAccountFetch.status));
				}
			}

		} else {
			LOG.warning(String.format("Gather for data account [%s] and date [%s] skipped because of status [%s]",
					dataAccount.id == null ? dataAccount.username : dataAccount.id.toString(), dateParameter, dataAccountFetch.status));
		}

		return dataAccountFetch;
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
