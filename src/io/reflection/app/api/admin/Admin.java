//  
//  Admin.java
//  reflection.io
//
//  Created by William Shakour on October 9, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin;

import static io.reflection.app.api.PagerHelper.updatePager;
import io.reflection.app.api.admin.shared.call.AssignRoleRequest;
import io.reflection.app.api.admin.shared.call.AssignRoleResponse;
import io.reflection.app.api.admin.shared.call.GetFeedFetchesRequest;
import io.reflection.app.api.admin.shared.call.GetFeedFetchesResponse;
import io.reflection.app.api.admin.shared.call.GetModelOutcomeRequest;
import io.reflection.app.api.admin.shared.call.GetModelOutcomeResponse;
import io.reflection.app.api.admin.shared.call.GetPermissionsRequest;
import io.reflection.app.api.admin.shared.call.GetPermissionsResponse;
import io.reflection.app.api.admin.shared.call.GetRolesRequest;
import io.reflection.app.api.admin.shared.call.GetRolesResponse;
import io.reflection.app.api.admin.shared.call.GetUsersCountRequest;
import io.reflection.app.api.admin.shared.call.GetUsersCountResponse;
import io.reflection.app.api.admin.shared.call.GetUsersRequest;
import io.reflection.app.api.admin.shared.call.GetUsersResponse;
import io.reflection.app.api.admin.shared.call.SetPasswordRequest;
import io.reflection.app.api.admin.shared.call.SetPasswordResponse;
import io.reflection.app.api.admin.shared.call.TriggerGatherRequest;
import io.reflection.app.api.admin.shared.call.TriggerGatherResponse;
import io.reflection.app.api.admin.shared.call.TriggerIngestRequest;
import io.reflection.app.api.admin.shared.call.TriggerIngestResponse;
import io.reflection.app.api.admin.shared.call.TriggerModelRequest;
import io.reflection.app.api.admin.shared.call.TriggerModelResponse;
import io.reflection.app.api.admin.shared.call.TriggerPredictRequest;
import io.reflection.app.api.admin.shared.call.TriggerPredictResponse;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.collectors.Collector;
import io.reflection.app.collectors.CollectorFactory;
import io.reflection.app.ingestors.Ingestor;
import io.reflection.app.ingestors.IngestorFactory;
import io.reflection.app.input.ValidationError;
import io.reflection.app.input.ValidationHelper;
import io.reflection.app.modellers.Modeller;
import io.reflection.app.modellers.ModellerFactory;
import io.reflection.app.predictors.Predictor;
import io.reflection.app.predictors.PredictorFactory;
import io.reflection.app.service.fetchfeed.FeedFetchServiceProvider;
import io.reflection.app.service.permission.PermissionServiceProvider;
import io.reflection.app.service.role.RoleServiceProvider;
import io.reflection.app.service.user.UserServiceProvider;

import java.util.List;
import java.util.logging.Logger;

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
				throw new InputValidationException(ValidationError.InvalidValueNull.getCode(),
						ValidationError.InvalidValueNull.getMessage("GetUsersRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			input.pager = ValidationHelper.validatePager(input.pager, "input");

			output.users = UserServiceProvider.provide().getUsers(input.pager);
			output.pager = input.pager;
			updatePager(output.pager, output.users, input.pager.totalCount == null ? UserServiceProvider.provide().getUsersCount() : null);

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
				throw new InputValidationException(ValidationError.InvalidValueNull.getCode(),
						ValidationError.InvalidValueNull.getMessage("GetUsersCountRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

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
				throw new InputValidationException(ValidationError.InvalidValueNull.getCode(),
						ValidationError.InvalidValueNull.getMessage("GetFeedFetchesRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			input.pager = ValidationHelper.validatePager(input.pager, "input");

			if (input.pager.sortBy == null) {
				input.pager.sortBy = "date";
			}

			if (input.pager.sortDirection == null) {
				input.pager.sortDirection = SortDirectionType.SortDirectionTypeDescending;
			}

			input.country = ValidationHelper.validateCountry(input.country, "input");

			input.store = ValidationHelper.validateStore(input.store, "input");

			input.listTypes = ValidationHelper.validateListTypes(input.listTypes, input.store, "input");

			output.feedFetches = FeedFetchServiceProvider.provide().getFeedFetches(input.country, input.store, input.listTypes, input.pager);

			output.pager = input.pager;
			updatePager(output.pager, output.feedFetches,
					input.pager.totalCount == null ? FeedFetchServiceProvider.provide().getFeedFetchesCount(input.country, input.store, input.listTypes) : null);

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
				throw new InputValidationException(ValidationError.InvalidValueNull.getCode(),
						ValidationError.InvalidValueNull.getMessage("TriggerModelRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			input.country = ValidationHelper.validateCountry(input.country, "input");

			input.store = ValidationHelper.validateStore(input.store, "input");

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
				throw new InputValidationException(ValidationError.InvalidValueNull.getCode(),
						ValidationError.InvalidValueNull.getMessage("TriggerModelRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			input.country = ValidationHelper.validateCountry(input.country, "input");

			input.store = ValidationHelper.validateStore(input.store, "input");

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
				throw new InputValidationException(ValidationError.InvalidValueNull.getCode(),
						ValidationError.InvalidValueNull.getMessage("should contain a grossing list name List: input.listType"));

			Modeller model = ModellerFactory.getModellerForStore(input.store.a3Code);
			model.enqueue(input.country.a2Code, type, input.code);

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
				throw new InputValidationException(ValidationError.InvalidValueNull.getCode(),
						ValidationError.InvalidValueNull.getMessage("TriggerModelRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			input.country = ValidationHelper.validateCountry(input.country, "input");

			input.store = ValidationHelper.validateStore(input.store, "input");

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
				throw new InputValidationException(ValidationError.InvalidValueNull.getCode(),
						ValidationError.InvalidValueNull.getMessage("should contain a grossing list name List: input.listType"));

			Predictor predictor = PredictorFactory.getPredictorForStore(input.store.a3Code);

			predictor.enqueue(input.country.a2Code, type, input.code);

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
				throw new InputValidationException(ValidationError.InvalidValueNull.getCode(),
						ValidationError.InvalidValueNull.getMessage("SetPasswordRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input.accessCode");

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
				throw new InputValidationException(ValidationError.InvalidValueNull.getCode(),
						ValidationError.InvalidValueNull.getMessage("AssignRoleRequest: input"));
			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input.accessCode");

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

	public GetRolesResponse getRoles(GetRolesRequest input) {
		LOG.finer("Entering getRoles");
		GetRolesResponse output = new GetRolesResponse();
		try {
			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

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
			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

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
}