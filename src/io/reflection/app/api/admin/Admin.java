//  
//  Admin.java
//  reflection.io
//
//  Created by William Shakour on October 9, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin;

import static io.reflection.app.shared.util.PagerHelper.updatePager;
import io.reflection.app.api.ValidationHelper;
import io.reflection.app.api.admin.shared.call.AddEventRequest;
import io.reflection.app.api.admin.shared.call.AddEventResponse;
import io.reflection.app.api.admin.shared.call.AddEventSubscriptionRequest;
import io.reflection.app.api.admin.shared.call.AddEventSubscriptionResponse;
import io.reflection.app.api.admin.shared.call.AssignPermissionRequest;
import io.reflection.app.api.admin.shared.call.AssignPermissionResponse;
import io.reflection.app.api.admin.shared.call.AssignRoleRequest;
import io.reflection.app.api.admin.shared.call.AssignRoleResponse;
import io.reflection.app.api.admin.shared.call.DeleteEventSubscriptionsRequest;
import io.reflection.app.api.admin.shared.call.DeleteEventSubscriptionsResponse;
import io.reflection.app.api.admin.shared.call.DeleteUserRequest;
import io.reflection.app.api.admin.shared.call.DeleteUserResponse;
import io.reflection.app.api.admin.shared.call.DeleteUsersRequest;
import io.reflection.app.api.admin.shared.call.DeleteUsersResponse;
import io.reflection.app.api.admin.shared.call.GetCalibrationSummaryRequest;
import io.reflection.app.api.admin.shared.call.GetCalibrationSummaryResponse;
import io.reflection.app.api.admin.shared.call.GetDataAccountFetchesRequest;
import io.reflection.app.api.admin.shared.call.GetDataAccountFetchesResponse;
import io.reflection.app.api.admin.shared.call.GetDataAccountsRequest;
import io.reflection.app.api.admin.shared.call.GetDataAccountsResponse;
import io.reflection.app.api.admin.shared.call.GetEventSubscriptionsRequest;
import io.reflection.app.api.admin.shared.call.GetEventSubscriptionsResponse;
import io.reflection.app.api.admin.shared.call.GetEventsRequest;
import io.reflection.app.api.admin.shared.call.GetEventsResponse;
import io.reflection.app.api.admin.shared.call.GetFeedFetchesRequest;
import io.reflection.app.api.admin.shared.call.GetFeedFetchesResponse;
import io.reflection.app.api.admin.shared.call.GetItemsRequest;
import io.reflection.app.api.admin.shared.call.GetItemsResponse;
import io.reflection.app.api.admin.shared.call.GetModelOutcomeRequest;
import io.reflection.app.api.admin.shared.call.GetModelOutcomeResponse;
import io.reflection.app.api.admin.shared.call.GetPermissionsRequest;
import io.reflection.app.api.admin.shared.call.GetPermissionsResponse;
import io.reflection.app.api.admin.shared.call.GetRolesAndPermissionsRequest;
import io.reflection.app.api.admin.shared.call.GetRolesAndPermissionsResponse;
import io.reflection.app.api.admin.shared.call.GetRolesRequest;
import io.reflection.app.api.admin.shared.call.GetRolesResponse;
import io.reflection.app.api.admin.shared.call.GetSimpleModelRunsRequest;
import io.reflection.app.api.admin.shared.call.GetSimpleModelRunsResponse;
import io.reflection.app.api.admin.shared.call.GetUsersCountRequest;
import io.reflection.app.api.admin.shared.call.GetUsersCountResponse;
import io.reflection.app.api.admin.shared.call.GetUsersRequest;
import io.reflection.app.api.admin.shared.call.GetUsersResponse;
import io.reflection.app.api.admin.shared.call.JoinDataAccountRequest;
import io.reflection.app.api.admin.shared.call.JoinDataAccountResponse;
import io.reflection.app.api.admin.shared.call.RevokePermissionRequest;
import io.reflection.app.api.admin.shared.call.RevokePermissionResponse;
import io.reflection.app.api.admin.shared.call.RevokeRoleRequest;
import io.reflection.app.api.admin.shared.call.RevokeRoleResponse;
import io.reflection.app.api.admin.shared.call.SendNotificationRequest;
import io.reflection.app.api.admin.shared.call.SendNotificationResponse;
import io.reflection.app.api.admin.shared.call.SetPasswordRequest;
import io.reflection.app.api.admin.shared.call.SetPasswordResponse;
import io.reflection.app.api.admin.shared.call.TriggerDataAccountFetchIngestRequest;
import io.reflection.app.api.admin.shared.call.TriggerDataAccountFetchIngestResponse;
import io.reflection.app.api.admin.shared.call.TriggerDataAccountGatherRequest;
import io.reflection.app.api.admin.shared.call.TriggerDataAccountGatherResponse;
import io.reflection.app.api.admin.shared.call.TriggerGatherRequest;
import io.reflection.app.api.admin.shared.call.TriggerGatherResponse;
import io.reflection.app.api.admin.shared.call.TriggerIngestRequest;
import io.reflection.app.api.admin.shared.call.TriggerIngestResponse;
import io.reflection.app.api.admin.shared.call.TriggerModelRequest;
import io.reflection.app.api.admin.shared.call.TriggerModelResponse;
import io.reflection.app.api.admin.shared.call.TriggerPredictRequest;
import io.reflection.app.api.admin.shared.call.TriggerPredictResponse;
import io.reflection.app.api.admin.shared.call.UpdateEventRequest;
import io.reflection.app.api.admin.shared.call.UpdateEventResponse;
import io.reflection.app.api.shared.ApiError;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.collectors.Collector;
import io.reflection.app.collectors.CollectorFactory;
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.DataSource;
import io.reflection.app.datatypes.shared.EventPriorityType;
import io.reflection.app.datatypes.shared.FeedFetch;
import io.reflection.app.datatypes.shared.ListPropertyType;
import io.reflection.app.datatypes.shared.Notification;
import io.reflection.app.datatypes.shared.NotificationTypeType;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.datatypes.shared.Role;
import io.reflection.app.datatypes.shared.SimpleModelRun;
import io.reflection.app.datatypes.shared.User;
import io.reflection.app.helpers.NotificationHelper;
import io.reflection.app.ingestors.Ingestor;
import io.reflection.app.ingestors.IngestorFactory;
import io.reflection.app.modellers.CalibrationSummaryHelper;
import io.reflection.app.modellers.Modeller;
import io.reflection.app.modellers.ModellerFactory;
import io.reflection.app.predictors.Predictor;
import io.reflection.app.predictors.PredictorFactory;
import io.reflection.app.service.category.CategoryServiceProvider;
import io.reflection.app.service.country.CountryServiceProvider;
import io.reflection.app.service.dataaccount.DataAccountServiceProvider;
import io.reflection.app.service.dataaccountfetch.DataAccountFetchServiceProvider;
import io.reflection.app.service.datasource.DataSourceServiceProvider;
import io.reflection.app.service.event.EventServiceProvider;
import io.reflection.app.service.feedfetch.FeedFetchServiceProvider;
import io.reflection.app.service.item.ItemServiceProvider;
import io.reflection.app.service.notification.NotificationServiceProvider;
import io.reflection.app.service.permission.PermissionServiceProvider;
import io.reflection.app.service.role.RoleServiceProvider;
import io.reflection.app.service.simplemodelrun.SimpleModelRunServiceProvider;
import io.reflection.app.service.user.IUserService;
import io.reflection.app.service.user.UserServiceProvider;
import io.reflection.app.shared.util.DataTypeHelper;
import io.reflection.app.shared.util.PagerHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.google.gwt.dev.util.collect.HashSet;
import com.willshex.gson.json.service.server.ActionHandler;
import com.willshex.gson.json.service.server.InputValidationException;
import com.willshex.gson.json.service.shared.StatusType;

public final class Admin extends ActionHandler {
	private static final Logger LOG = Logger.getLogger(Admin.class.getName());

	public GetUsersResponse getUsers(GetUsersRequest input) {
		LOG.finer("Entering getUsers");
		GetUsersResponse output = new GetUsersResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("GetUsersRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.adminRole());

			input.pager = ValidationHelper.validatePager(input.pager, "input");

			boolean isQuery = false;
			try {
				input.query = ValidationHelper.validateQuery(input.query, "input");
				isQuery = true;
			} catch (InputValidationException ex) {}

			output.users = isQuery ? UserServiceProvider.provide().searchUsers(input.query, input.pager) : UserServiceProvider.provide().getUsers(input.pager);
			output.pager = input.pager;
			if (isQuery) {
				updatePager(output.pager, output.users, input.pager.totalCount == null ? UserServiceProvider.provide().searchUsersCount(input.query) : null);
			} else {
				updatePager(output.pager, output.users, input.pager.totalCount == null ? UserServiceProvider.provide().getUsersCount() : null);
			}

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting getUsers");
		return output;
	}

	public GetUsersCountResponse getUsersCount(GetUsersCountRequest input) {
		LOG.finer("Entering getUsersCount");
		GetUsersCountResponse output = new GetUsersCountResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("GetUsersCountRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.adminRole());

			output.count = UserServiceProvider.provide().getUsersCount();

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting getUsersCount");
		return output;
	}

	public GetModelOutcomeResponse getModelOutcome(GetModelOutcomeRequest input) {
		LOG.finer("Entering getModelOutcome");
		GetModelOutcomeResponse output = new GetModelOutcomeResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("GetModelOutcomeRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.adminRole());

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting getModelOutcome");
		return output;
	}

	public GetFeedFetchesResponse getFeedFetches(GetFeedFetchesRequest input) {
		LOG.finer("Entering getFeedFetches");
		GetFeedFetchesResponse output = new GetFeedFetchesResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("GetFeedFetchesRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.adminRole());

			input.pager = ValidationHelper.validatePager(input.pager, "input.pager");

			if (input.pager.sortBy == null) {
				input.pager.sortBy = "date";
			}

			if (input.pager.sortDirection == null) {
				input.pager.sortDirection = SortDirectionType.SortDirectionTypeDescending;
			}

			input.country = ValidationHelper.validateCountry(input.country, "input.country");

			input.store = ValidationHelper.validateStore(input.store, "input.store");

			input.category = ValidationHelper.validateCategory(input.category, "input.category");

			input.listTypes = ValidationHelper.validateListTypes(input.listTypes, input.store, "input.listTypes");

			output.feedFetches = FeedFetchServiceProvider.provide().getFeedFetches(input.country, input.store, input.category, input.listTypes, input.pager);

			output.pager = input.pager;
			updatePager(
					output.pager,
					output.feedFetches,
					input.pager.totalCount == null ? FeedFetchServiceProvider.provide().getFeedFetchesCount(input.country, input.store, input.category,
							input.listTypes) : null);

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting getFeedFetches");
		return output;
	}

	public TriggerGatherResponse triggerGather(TriggerGatherRequest input) {
		LOG.finer("Entering triggerGather");
		TriggerGatherResponse output = new TriggerGatherResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("TriggerGatherRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.adminRole());

			input.country = ValidationHelper.validateCountry(input.country, "input");

			input.store = ValidationHelper.validateStore(input.store, "input");

			if (input.category != null) {
				input.category = ValidationHelper.validateCategory(input.category, "input.category");
			} else {
				input.category = CategoryServiceProvider.provide().getAllCategory(input.store);
			}

			input.listTypes = ValidationHelper.validateListTypes(input.listTypes, input.store, "input");

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting triggerGather");
		return output;
	}

	public TriggerIngestResponse triggerIngest(TriggerIngestRequest input) {
		LOG.finer("Entering triggerIngest");
		TriggerIngestResponse output = new TriggerIngestResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("TriggerModelRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.adminRole());

			input.country = ValidationHelper.validateCountry(input.country, "input");

			input.store = ValidationHelper.validateStore(input.store, "input");

			if (input.category != null) {
				input.category = ValidationHelper.validateCategory(input.category, "input.category");
			} else {
				input.category = CategoryServiceProvider.provide().getAllCategory(input.store);
			}

			input.listTypes = ValidationHelper.validateListTypes(input.listTypes, input.store, "input");

			Ingestor i = IngestorFactory.getIngestorForStore(input.store.a3Code);

			for (String listType : input.listTypes) {
				List<Long> ids = FeedFetchServiceProvider.provide().getIngestableFeedFetchIds(input.country, input.store, listType, input.code);
				i.enqueue(ids);
			}

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting triggerIngest");
		return output;
	}

	public TriggerModelResponse triggerModel(TriggerModelRequest input) {
		LOG.finer("Entering triggerModel");
		TriggerModelResponse output = new TriggerModelResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("TriggerModelRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.adminRole());

			if (input.modelType == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("ModelTypeType: input.modelType"));

			Modeller modeller = null;

			switch (input.modelType) {
			case ModelTypeTypeCorrelation:
				input.country = ValidationHelper.validateCountry(input.country, "input");

				input.store = ValidationHelper.validateStore(input.store, "input");

				if (input.category != null) {
					input.category = ValidationHelper.validateCategory(input.category, "input.category");
				} else {
					input.category = CategoryServiceProvider.provide().getAllCategory(input.store);
				}

				input.listTypes = ValidationHelper.validateListTypes(input.listTypes, input.store, "input");

				Collector collector = CollectorFactory.getCollectorForStore(input.store.a3Code);

				String type = null;
				for (String listType : input.listTypes) {
					if (collector.isGrossing(listType)) {
						type = listType;
						break;
					}
				}

				if (type == null)
					throw new InputValidationException(ApiError.InvalidValueNull.getCode(),
							ApiError.InvalidValueNull.getMessage("should contain a grossing list name List: input.listType"));

				modeller = ModellerFactory.getModellerForStore(input.store.a3Code);
				modeller.enqueue(input.modelType, input.country.a2Code, input.category, type, input.code);

				break;
			case ModelTypeTypeSimple:
				input.feedFetch = ValidationHelper.validateFeedFetch(input.feedFetch, "input.feedFetch");

				modeller = ModellerFactory.getModellerForStore(input.feedFetch.store);
				modeller.enqueue(input.feedFetch);

				break;
			}

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting triggerModel");
		return output;
	}

	public TriggerPredictResponse triggerPredict(TriggerPredictRequest input) {
		LOG.finer("Entering triggerPredict");
		TriggerPredictResponse output = new TriggerPredictResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("TriggerModelRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.adminRole());

			if (input.modelType == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("ModelTypeType: input.modelType"));

			switch (input.modelType) {
			case ModelTypeTypeCorrelation:
				input.country = ValidationHelper.validateCountry(input.country, "input");

				input.store = ValidationHelper.validateStore(input.store, "input");

				if (input.category != null) {
					input.category = ValidationHelper.validateCategory(input.category, "input.category");
				} else {
					input.category = CategoryServiceProvider.provide().getAllCategory(input.store);
				}

				input.listTypes = ValidationHelper.validateListTypes(input.listTypes, input.store, "input");

				Collector collector = CollectorFactory.getCollectorForStore(input.store.a3Code);
				Predictor predictor = null;

				String type = null;
				for (String listType : input.listTypes) {
					if (collector.isGrossing(listType)) {
						type = listType;
						break;
					}
				}

				if (type == null)
					throw new InputValidationException(ApiError.InvalidValueNull.getCode(),
							ApiError.InvalidValueNull.getMessage("should contain a grossing list name List: input.listType"));

				predictor = PredictorFactory.getPredictorForStore(input.store.a3Code);
				predictor.enqueue(input.country.a2Code, type, input.code);

				break;
			case ModelTypeTypeSimple:
				input.simpleModelRun = ValidationHelper.validateSimpleModelRun(input.simpleModelRun, "input.simpleModelRun");

				if (input.simpleModelRun.feedFetch.store == null) {
					input.simpleModelRun.feedFetch = FeedFetchServiceProvider.provide().getFeedFetch(input.simpleModelRun.feedFetch.id);
				}

				predictor = PredictorFactory.getPredictorForStore(input.simpleModelRun.feedFetch.store);
				predictor.enqueue(input.simpleModelRun);

				break;
			}

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting triggerPredict");
		return output;
	}

	public SetPasswordResponse setPassword(SetPasswordRequest input) {
		LOG.finer("Entering setPassword");
		SetPasswordResponse output = new SetPasswordResponse();

		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("SetPasswordRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input.accessCode");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.adminRole());

			input.user = ValidationHelper.validateExistingUser(input.user, "input.user");

			input.password = ValidationHelper.validatePassword(input.password, "input.password");

			UserServiceProvider.provide().updateUserPassword(input.user, input.password);

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}

		LOG.finer("Exiting setPassword");
		return output;
	}

	public AssignRoleResponse assignRole(AssignRoleRequest input) {
		LOG.finer("Entering assignRole");
		AssignRoleResponse output = new AssignRoleResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("AssignRoleRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input.accessCode");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.adminRole());

			input.user = ValidationHelper.validateExistingUser(input.user, "input.user");

			input.role = ValidationHelper.validateRole(input.role, "input.role");

			if (!UserServiceProvider.provide().hasRole(input.user, input.role).booleanValue()) {
				UserServiceProvider.provide().assignRole(input.user, input.role);
			}

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting assignRole");
		return output;
	}

	public AssignPermissionResponse assignPermission(AssignPermissionRequest input) {
		LOG.finer("Entering assignPermission");
		AssignPermissionResponse output = new AssignPermissionResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("AssignRoleRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input.accessCode");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.adminRole());

			input.user = ValidationHelper.validateExistingUser(input.user, "input.user");

			input.permission = ValidationHelper.validatePermission(input.permission, "input.permission");

			if (!UserServiceProvider.provide().hasPermission(input.user, input.permission).booleanValue()) {
				UserServiceProvider.provide().assignPermission(input.user, input.permission);
			}

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting assignPermission");
		return output;
	}

	public RevokeRoleResponse revokeRole(RevokeRoleRequest input) {
		LOG.finer("Entering revokeRole");
		RevokeRoleResponse output = new RevokeRoleResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("RevokeRoleRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input.accessCode");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.adminRole());

			input.user = ValidationHelper.validateExistingUser(input.user, "input.user");

			input.role = ValidationHelper.validateRole(input.role, "input.role");

			if (UserServiceProvider.provide().hasRole(input.user, input.role).booleanValue()) {
				UserServiceProvider.provide().revokeRole(input.user, input.role);
			}

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting revokeRole");
		return output;
	}

	public RevokePermissionResponse revokePermission(RevokePermissionRequest input) {
		LOG.finer("Entering revokePermission");
		RevokePermissionResponse output = new RevokePermissionResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("AssignRoleRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input.accessCode");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.adminRole());

			input.user = ValidationHelper.validateExistingUser(input.user, "input.user");

			input.permission = ValidationHelper.validatePermission(input.permission, "input.permission");

			if (UserServiceProvider.provide().hasPermission(input.user, input.permission).booleanValue()) {
				UserServiceProvider.provide().revokePermission(input.user, input.permission);
			}

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting revokePermission");
		return output;
	}

	public GetRolesResponse getRoles(GetRolesRequest input) {
		LOG.finer("Entering getRoles");
		GetRolesResponse output = new GetRolesResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("GetRolesRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.adminRole());

			input.pager = ValidationHelper.validatePager(input.pager, "input");

			output.roles = RoleServiceProvider.provide().getRoles(input.pager);
			output.pager = input.pager;
			updatePager(output.pager, output.roles, input.pager.totalCount == null ? RoleServiceProvider.provide().getRolesCount() : null);

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting getRoles");
		return output;
	}

	public GetPermissionsResponse getPermissions(GetPermissionsRequest input) {
		LOG.finer("Entering getPermissions");
		GetPermissionsResponse output = new GetPermissionsResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("GetPermissionsRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.adminRole());

			input.pager = ValidationHelper.validatePager(input.pager, "input");

			output.permissions = PermissionServiceProvider.provide().getPermissions(input.pager);
			output.pager = input.pager;
			updatePager(output.pager, output.permissions, input.pager.totalCount == null ? PermissionServiceProvider.provide().getPermissionsCount() : null);

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting getPermissions");
		return output;
	}

	public GetEventsResponse getEvents(GetEventsRequest input) {
		LOG.finer("Entering getEvents");
		GetEventsResponse output = new GetEventsResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("GetEventsRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.adminRole());

			input.pager = ValidationHelper.validatePager(input.pager, "input");

			boolean isQuery = false;
			try {
				input.query = ValidationHelper.validateQuery(input.query, "input");
				isQuery = true;
			} catch (InputValidationException ex) {}

			output.events = isQuery ? EventServiceProvider.provide().searchEvents(input.query, input.pager) : EventServiceProvider.provide().getEvents(
					input.pager);

			output.pager = input.pager;

			updatePager(output.pager, output.events, null);

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}

		LOG.finer("Exiting getEvents");
		return output;
	}

	public SendNotificationResponse sendNotification(SendNotificationRequest input) {
		LOG.finer("Entering sendNotification");
		SendNotificationResponse output = new SendNotificationResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("SendNotificationRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.adminRole());

			ValidationHelper.validateNewNotification(input.notification, "input.notification");

			NotificationServiceProvider.provide().addNotification(input.notification);

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting sendNotification");
		return output;
	}

	public GetItemsResponse getItems(GetItemsRequest input) {
		LOG.finer("Entering getItems");
		GetItemsResponse output = new GetItemsResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("GetItemsRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.adminRole());

			input.pager = ValidationHelper.validatePager(input.pager, "input");

			boolean isQuery = false;
			try {
				input.query = ValidationHelper.validateQuery(input.query, "input");
				isQuery = true;
			} catch (InputValidationException ex) {}

			output.items = isQuery ? ItemServiceProvider.provide().searchItems(input.query, input.pager) : ItemServiceProvider.provide().getItems(input.pager);
			output.pager = input.pager;
			if (isQuery) {
				updatePager(output.pager, output.items, input.pager.totalCount == null ? ItemServiceProvider.provide().searchItemsCount(input.query) : null);
			} else {
				updatePager(output.pager, output.items, input.pager.totalCount == null ? ItemServiceProvider.provide().getItemsCount() : null);
			}

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}

		LOG.finer("Exiting getItems");
		return output;
	}

	public DeleteUserResponse deleteUser(DeleteUserRequest input) {
		LOG.finer("Entering deleteUser");
		DeleteUserResponse output = new DeleteUserResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("DeleteUserRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input.accessCode");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.adminRole());

			input.user = ValidationHelper.validateExistingUser(input.user, "input.user");

			List<DataAccount> linkedAccounts = new ArrayList<DataAccount>();
			linkedAccounts = UserServiceProvider.provide().getDataAccounts(input.user, PagerHelper.createInfinitePager());
			for (DataAccount la : linkedAccounts) {
				if (input.user.id.longValue() == UserServiceProvider.provide().getDataAccountOwner(la).id) { // User is the owner of the linked account
					la.active = DataTypeHelper.INACTIVE_VALUE;
					DataAccountServiceProvider.provide().updateDataAccount(la);
				}
			}

			UserServiceProvider.provide().deleteAllDataAccounts(input.user);

			UserServiceProvider.provide().revokeAllPermissions(input.user);

			UserServiceProvider.provide().revokeAllRoles(input.user);

			UserServiceProvider.provide().deleteUser(input.user);

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting deleteUser");
		return output;
	}

	public DeleteUsersResponse deleteUsers(DeleteUsersRequest input) {
		LOG.finer("Entering deleteUsers");
		DeleteUsersResponse output = new DeleteUsersResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("DeleteUsersRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input.accessCode");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.adminRole());

			if (input.allTestUsers) {
				Role testRole = RoleServiceProvider.provide().getCodeRole(DataTypeHelper.ROLE_TEST_CODE);
				input.users = UserServiceProvider.provide().getRoleUsers(testRole);
			}

			if (input.users != null && input.users.size() > 0) {

				List<DataAccount> linkedAccounts = UserServiceProvider.provide().getUsersDataAccounts(input.users, PagerHelper.createInfinitePager());
				List<Long> userIds = new ArrayList<Long>();
				for (User user : input.users) {
					userIds.add(user.id);
				}
				for (DataAccount la : linkedAccounts) {
					if (userIds.contains(UserServiceProvider.provide().getDataAccountOwner(la).id)) { // One of the users to delete is the owner
						la.active = DataTypeHelper.INACTIVE_VALUE;
						DataAccountServiceProvider.provide().updateDataAccount(la);
					}
				}

				UserServiceProvider.provide().deleteUsersAllDataAccounts(input.users);

				UserServiceProvider.provide().revokeUsersAllPermissions(input.users);

				UserServiceProvider.provide().revokeUsersAllRoles(input.users);

				UserServiceProvider.provide().deleteUsers(input.users);

			}

			output.status = StatusType.StatusTypeSuccess;

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting deleteUsers");
		return output;
	}

	public GetRolesAndPermissionsResponse getRolesAndPermissions(GetRolesAndPermissionsRequest input) {
		LOG.finer("Entering getRolesAndPermissions");
		GetRolesAndPermissionsResponse output = new GetRolesAndPermissionsResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(),
						ApiError.InvalidValueNull.getMessage("GetRolesAndPermissionsResponse: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input.accessCode");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.adminRole());

			input.user = ValidationHelper.validateExistingUser(input.user, "input.user");

			if (input.permissionsOnly != Boolean.TRUE) {
				output.roles = UserServiceProvider.provide().getRoles(input.user);

				if (output.roles != null) {
					if (input.idsOnly == Boolean.FALSE) {
						RoleServiceProvider.provide().inflateRoles(output.roles);
					}

					for (Role role : output.roles) {
						role.permissions = RoleServiceProvider.provide().getPermissions(role);

						if (role.permissions != null && input.idsOnly == Boolean.FALSE) {
							PermissionServiceProvider.provide().inflatePermissions(role.permissions);
						}
					}
				}
			}

			if (input.rolesOnly != Boolean.TRUE) {
				output.permissions = UserServiceProvider.provide().getPermissions(input.user);

				if (output.permissions != null && input.idsOnly == Boolean.FALSE) {
					PermissionServiceProvider.provide().inflatePermissions(output.permissions);
				}
			}

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting getRolesAndPermissions");
		return output;
	}

	public UpdateEventResponse updateEvent(UpdateEventRequest input) {
		LOG.finer("Entering updateEvent");
		UpdateEventResponse output = new UpdateEventResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("UpdateEventRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input.accessCode");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			if (input.event == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("input.event"));

			ValidationHelper.validateExistingEvent(input.event, "input.event");

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.adminRole());

			EventServiceProvider.provide().updateEvent(input.event);

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting updateEvent");
		return output;
	}

	public AddEventResponse addEvent(AddEventRequest input) {
		LOG.finer("Entering addEvent");
		AddEventResponse output = new AddEventResponse();
		try {
			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting addEvent");
		return output;
	}

	public GetDataAccountsResponse getDataAccounts(GetDataAccountsRequest input) {
		LOG.finer("Entering getDataAccounts");
		GetDataAccountsResponse output = new GetDataAccountsResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("GetDataAccountsRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input.accessCode");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.adminRole());

			output.dataAccounts = DataAccountServiceProvider.provide().getDataAccounts(input.pager);
			// Delete password fields
			for (DataAccount dataAccount : output.dataAccounts) {
				dataAccount.password = null;
			}

			// Retrieve and link Sources
			List<Long> dataSourceIdList = new ArrayList<Long>();
			Map<Long, DataSource> dataSourceLookup = new HashMap<Long, DataSource>();
			for (DataAccount dataAccount : output.dataAccounts) {
				if (dataAccount.source.id != null) {
					dataSourceIdList.add(dataAccount.source.id);
				}
			}
			if (dataSourceIdList != null && dataSourceIdList.size() > 0) {
				List<DataSource> dataSourceList = DataSourceServiceProvider.provide().getDataSourceBatch(dataSourceIdList);
				for (DataSource dataSource : dataSourceList) {
					dataSourceLookup.put(dataSource.id, dataSource);
				}
			}

			// Retrieve and link Owners
			List<Long> dataAccountIdList = new ArrayList<Long>();
			Map<Long, User> userLookup = new HashMap<Long, User>();
			for (DataAccount dataAccount : output.dataAccounts) {
				if (dataAccount.id != null) {
					dataAccountIdList.add(dataAccount.id);
				}
			}
			if (dataAccountIdList != null && dataAccountIdList.size() > 0) {
				List<User> userList = UserServiceProvider.provide().getDataAccountOwnerBatch(dataAccountIdList);
				for (User owner : userList) {
					if (owner.linkedAccounts.get(0).id != null) userLookup.put(owner.linkedAccounts.get(0).id, owner);
				}
			}

			// Add DataAccount informations
			for (DataAccount dataAccount : output.dataAccounts) {
				dataAccount.source = dataSourceLookup.get(dataAccount.source.id);
				dataAccount.user = userLookup.get(dataAccount.id);
				dataAccount.fetches = DataAccountFetchServiceProvider.provide().getFailedDataAccountFetches(dataAccount, PagerHelper.createInfinitePager());
			}

			output.pager = input.pager;
			updatePager(output.pager, output.dataAccounts, input.pager.totalCount == null ? DataAccountServiceProvider.provide().getDataAccountsCount() : null);

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting getDataAccounts");
		return output;
	}

	public GetDataAccountFetchesResponse getDataAccountFetches(GetDataAccountFetchesRequest input) {
		LOG.finer("Entering getDataAccountFetches");
		GetDataAccountFetchesResponse output = new GetDataAccountFetchesResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(),
						ApiError.InvalidValueNull.getMessage("GetDataAccountFetchesRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input.accessCode");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.adminRole());

			if (input.end == null) {
				input.end = DateTime.now(DateTimeZone.UTC).toDate();
			}

			if (input.start == null) {
				DateTime start = new DateTime(input.end.getTime(), DateTimeZone.UTC);
				start.minusDays(30);
				input.start = start.toDate();
			}

			if (input.dataAccount != null && input.dataAccount.id != null) {
				output.dataAccountFetches = DataAccountFetchServiceProvider.provide().getDataAccountFetches(input.dataAccount, input.start, input.end,
						input.pager);
			} else {
				output.dataAccountFetches = DataAccountFetchServiceProvider.provide().getDataAccountFetches(input.start, input.end, input.pager);
			}

			output.pager = input.pager;
			if (input.dataAccount != null && input.dataAccount.id != null) {
				updatePager(output.pager, output.dataAccountFetches, input.pager.totalCount == null ? DataAccountFetchServiceProvider.provide()
						.getDataAccountFetchesCount(input.dataAccount, input.start, input.end) : null);
			} else {
				updatePager(output.pager, output.dataAccountFetches, input.pager.totalCount == null ? DataAccountFetchServiceProvider.provide()
						.getDataAccountFetchesCount(input.start, input.end) : null);
			}

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting getDataAccountFetches");
		return output;
	}

	public TriggerDataAccountGatherResponse triggerDataAccountGather(TriggerDataAccountGatherRequest input) {
		LOG.finer("Entering triggerDataAccountGather");
		TriggerDataAccountGatherResponse output = new TriggerDataAccountGatherResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(),
						ApiError.InvalidValueNull.getMessage("TriggerDataAccountGatherRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.adminRole());

			if (input.from == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("Date: input.from"));

			if (input.days == null || input.days.intValue() < 1) {
				input.days = Integer.valueOf(1);
			}

			input.dataAccount = ValidationHelper.validateDataAccount(input.dataAccount, "input.dataAccount");

			if (input.days.intValue() == 1) {
				DataAccountServiceProvider.provide().triggerSingleDateDataAccountFetch(input.dataAccount, input.from);
			} else {
				DataAccountServiceProvider.provide().triggerMultipleDateDataAccountFetch(input.dataAccount, input.from, input.days);
			}

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting triggerDataAccountGather");
		return output;
	}

	public TriggerDataAccountFetchIngestResponse triggerDataAccountFetchIngest(TriggerDataAccountFetchIngestRequest input) {
		LOG.finer("Entering triggerDataAccountFetchIngest");
		TriggerDataAccountFetchIngestResponse output = new TriggerDataAccountFetchIngestResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(),
						ApiError.InvalidValueNull.getMessage("TriggerDataAccountFetchIngestRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.adminRole());

			input.fetch = ValidationHelper.validateDataAccountFetch(input.fetch, "input.fetch");

			DataAccountFetchServiceProvider.provide().triggerDataAccountFetchIngest(input.fetch);

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting triggerDataAccountFetchIngest");
		return output;
	}

	public JoinDataAccountResponse joinDataAccount(JoinDataAccountRequest input) {
		LOG.finer("Entering joinDataAccount");
		JoinDataAccountResponse output = new JoinDataAccountResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("JoinDataAccountRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.adminRole());

			input.dataAccount = ValidationHelper.validateDataAccount(input.dataAccount, "input.dataAccount");

			IUserService userService = UserServiceProvider.provide();
			userService.addOrRestoreUserDataAccount(input.session.user, input.dataAccount);

			User adminUser = userService.getUser(input.session.user.id);
			User ownerUser = userService.getDataAccountOwner(input.dataAccount);

			DataSource dataSource = DataSourceServiceProvider.provide().getDataSource(input.dataAccount.source.id);

			User listeningUser = UserServiceProvider.provide().getUsernameUser("chi@reflection.io");

			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("listener", listeningUser);
			parameters.put("admin", adminUser);
			parameters.put("owner", ownerUser);
			parameters.put("data", input.dataAccount);
			parameters.put("source", dataSource);

			String body = NotificationHelper
					.inflate(
							parameters,
							"Hi ${listener.forename},\n\nThis is to let you know that the admin user [${admin.id} - ${admin.forename} ${admin.surname}] has added the data account [${data.id}] for the data source [${source.name}] and the username [${data.username}]. This data account belongs to the user [${owner.id} - ${owner.forename} ${owner.surname}].\n\nReflection");

			Notification notification = (new Notification()).from("hello@reflection.io").user(listeningUser).body(body)
					.priority(EventPriorityType.EventPriorityTypeCritical).subject("An admin has linked to a user's account");
			Notification added = NotificationServiceProvider.provide().addNotification(notification);

			if (added.type != NotificationTypeType.NotificationTypeTypeInternal) {
				notification.type = NotificationTypeType.NotificationTypeTypeInternal;
				NotificationServiceProvider.provide().addNotification(notification);
			}

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting joinDataAccount");
		return output;
	}

	public GetSimpleModelRunsResponse getSimpleModelRuns(GetSimpleModelRunsRequest input) {
		LOG.finer("Entering getSimpleModelRuns");
		GetSimpleModelRunsResponse output = new GetSimpleModelRunsResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("JoinDataAccountRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.adminRole());

			if (input.end == null) {
				input.end = DateTime.now(DateTimeZone.UTC).toDate();
			}

			if (input.start == null) {
				DateTime start = new DateTime(input.end.getTime(), DateTimeZone.UTC);
				start.minusDays(30);
				input.start = start.toDate();
			}

			input.country = ValidationHelper.validateCountry(input.country, "input");

			input.store = ValidationHelper.validateStore(input.store, "input");

			input.listType = ValidationHelper.validateListType(input.listType, input.store);
			List<String> listTypes = new ArrayList<String>();
			listTypes.add(input.listType);

			if (input.category != null) {
				input.category = ValidationHelper.validateCategory(input.category, "input.category");
			} else {
				input.category = CategoryServiceProvider.provide().getAllCategory(input.store);
			}

			input.pager = ValidationHelper.validatePager(input.pager, "input");

			if (input.pager.sortBy == null) {
				input.pager.sortBy = "feedfetchid";
			}

			if (input.pager.sortDirection == null) {
				input.pager.sortDirection = SortDirectionType.SortDirectionTypeDescending;
			}

			output.pager = input.pager;

			List<FeedFetch> feedFetchList = FeedFetchServiceProvider.provide().getDatesFeedFetches(input.country, input.store, input.category, listTypes,
					input.start, input.end);

			if (feedFetchList != null && feedFetchList.size() > 0) {
				// Create feedfetchId : feedFetch HashMap
				Map<Long, FeedFetch> feedFetchIdLookup = new HashMap<Long, FeedFetch>();
				for (FeedFetch feedFetch : feedFetchList) {
					if (feedFetchIdLookup.get(feedFetch.id) == null) {
						feedFetchIdLookup.put(feedFetch.id, feedFetch);
					}
				}

				List<SimpleModelRun> simpleModelRunList = SimpleModelRunServiceProvider.provide().getFeedFetchesSimpleModelRuns(feedFetchIdLookup.keySet(),
						input.pager);

				// Add feedFetch object to SimpleModelRuns
				for (SimpleModelRun simpleModelRun : simpleModelRunList) {
					if (feedFetchIdLookup.get(simpleModelRun.feedFetch.id) != null) {
						simpleModelRun.feedFetch = feedFetchIdLookup.get(simpleModelRun.feedFetch.id);
					}
				}

				updatePager(output.pager, output.simpleModelRuns, input.pager.totalCount == null ? SimpleModelRunServiceProvider.provide()
						.getFeedFetchesSimpleModelRunsCount(feedFetchIdLookup.keySet()) : null);

				output.simpleModelRuns = simpleModelRunList;
			} else {
				updatePager(output.pager, null, Long.valueOf(0));
			}

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting getSimpleModelRuns");
		return output;
	}

	public GetEventSubscriptionsResponse getEventSubscriptions(GetEventSubscriptionsRequest input) {
		LOG.finer("Entering getEventSubscriptions");
		GetEventSubscriptionsResponse output = new GetEventSubscriptionsResponse();
		try {
			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting getEventSubscriptions");
		return output;
	}

	public AddEventSubscriptionResponse addEventSubscription(AddEventSubscriptionRequest input) {
		LOG.finer("Entering addEventSubscription");
		AddEventSubscriptionResponse output = new AddEventSubscriptionResponse();
		try {
			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting addEventSubscription");
		return output;
	}

	public DeleteEventSubscriptionsResponse deleteEventSubscriptions(DeleteEventSubscriptionsRequest input) {
		LOG.finer("Entering deleteEventSubscriptions");
		DeleteEventSubscriptionsResponse output = new DeleteEventSubscriptionsResponse();
		try {
			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting deleteEventSubscriptions");
		return output;
	}

	public GetCalibrationSummaryResponse getCalibrationSummary(GetCalibrationSummaryRequest input) {
		LOG.finer("Entering getCalibrationSummary");
		GetCalibrationSummaryResponse output = new GetCalibrationSummaryResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("JoinDataAccountRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.adminRole());

			input.feedFetch = ValidationHelper.validateFeedFetch(input.feedFetch, "input.feedfetch");

			output.calibrationSummary = CalibrationSummaryHelper.read(input.feedFetch);

			Set<String> itemIds = new HashSet<String>();
			if (output.calibrationSummary != null) {
				if (output.calibrationSummary.hits != null) {
					for (Rank rank : output.calibrationSummary.hits) {
						itemIds.add(rank.itemId);
					}
				}

				if (output.calibrationSummary.misses != null) {
					for (Rank rank : output.calibrationSummary.misses) {
						itemIds.add(rank.itemId);
					}
				}
			}

			output.items = ItemServiceProvider.provide().getInternalIdItemBatch(itemIds);

			// we don't send the store because of iphone/ipad stores - we use the list type to decide
			// output.store = StoreServiceProvider.provide().getA3CodeStore(input.feedFetch.store);

			output.country = CountryServiceProvider.provide().getA2CodeCountry(input.feedFetch.country);
			output.category = CategoryServiceProvider.provide().getCategory(input.feedFetch.category.id);

			Collector collector = CollectorFactory.getCollectorForStore(input.feedFetch.store);

			output.listType = collector.getListType(input.feedFetch.type);
			output.listProperty = (collector.isGrossing(input.feedFetch.type) ? ListPropertyType.ListPropertyTypeRevenue
					: ListPropertyType.ListPropertyTypeDownloads);

			output.form = ModellerFactory.getModellerForStore(input.feedFetch.store).getForm(input.feedFetch.type);

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}

		LOG.finer("Exiting getCalibrationSummary");
		return output;
	}
}
