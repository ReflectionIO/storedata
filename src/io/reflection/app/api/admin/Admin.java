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
import io.reflection.app.api.PagerHelper;
import io.reflection.app.api.ValidationHelper;
import io.reflection.app.api.admin.shared.call.AssignPermissionRequest;
import io.reflection.app.api.admin.shared.call.AssignPermissionResponse;
import io.reflection.app.api.admin.shared.call.AssignRoleRequest;
import io.reflection.app.api.admin.shared.call.AssignRoleResponse;
import io.reflection.app.api.admin.shared.call.GetDataAccountsRequest;
import io.reflection.app.api.admin.shared.call.GetDataAccountsResponse;
import io.reflection.app.api.admin.shared.call.GetEmailTemplatesRequest;
import io.reflection.app.api.admin.shared.call.GetEmailTemplatesResponse;
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
import io.reflection.app.api.admin.shared.call.GetUsersCountRequest;
import io.reflection.app.api.admin.shared.call.GetUsersCountResponse;
import io.reflection.app.api.admin.shared.call.GetUsersRequest;
import io.reflection.app.api.admin.shared.call.GetUsersResponse;
import io.reflection.app.api.admin.shared.call.RevokePermissionRequest;
import io.reflection.app.api.admin.shared.call.RevokePermissionResponse;
import io.reflection.app.api.admin.shared.call.RevokeRoleRequest;
import io.reflection.app.api.admin.shared.call.RevokeRoleResponse;
import io.reflection.app.api.admin.shared.call.SendEmailRequest;
import io.reflection.app.api.admin.shared.call.SendEmailResponse;
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
import io.reflection.app.api.admin.shared.call.UpdateEmailTemplateRequest;
import io.reflection.app.api.admin.shared.call.UpdateEmailTemplateResponse;
import io.reflection.app.api.blog.shared.call.DeleteUserRequest;
import io.reflection.app.api.blog.shared.call.DeleteUserResponse;
import io.reflection.app.api.shared.ApiError;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.collectors.Collector;
import io.reflection.app.collectors.CollectorFactory;
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.DataSource;
import io.reflection.app.datatypes.shared.Role;
import io.reflection.app.ingestors.Ingestor;
import io.reflection.app.ingestors.IngestorFactory;
import io.reflection.app.modellers.Modeller;
import io.reflection.app.modellers.ModellerFactory;
import io.reflection.app.predictors.Predictor;
import io.reflection.app.predictors.PredictorFactory;
import io.reflection.app.service.category.CategoryServiceProvider;
import io.reflection.app.service.dataaccount.DataAccountServiceProvider;
import io.reflection.app.service.dataaccountfetch.DataAccountFetchServiceProvider;
import io.reflection.app.service.datasource.DataSourceServiceProvider;
import io.reflection.app.service.emailtemplate.EmailTemplateServiceProvider;
import io.reflection.app.service.feedfetch.FeedFetchServiceProvider;
import io.reflection.app.service.item.ItemServiceProvider;
import io.reflection.app.service.permission.PermissionServiceProvider;
import io.reflection.app.service.role.RoleServiceProvider;
import io.reflection.app.service.user.UserServiceProvider;
import io.reflection.app.shared.util.DataTypeHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("GetUsersRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.createRole(DataTypeHelper.ROLE_ADMIN_ID));

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
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("GetUsersCountRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.createRole(DataTypeHelper.ROLE_ADMIN_ID));

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

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.createRole(DataTypeHelper.ROLE_ADMIN_ID));

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

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.createRole(DataTypeHelper.ROLE_ADMIN_ID));

			input.pager = ValidationHelper.validatePager(input.pager, "input");

			if (input.pager.sortBy == null) {
				input.pager.sortBy = "date";
			}

			if (input.pager.sortDirection == null) {
				input.pager.sortDirection = SortDirectionType.SortDirectionTypeDescending;
			}

			input.country = ValidationHelper.validateCountry(input.country, "input");

			input.store = ValidationHelper.validateStore(input.store, "input");

			input.listTypes = ValidationHelper.validateListTypes(input.listTypes, input.store, "input.listTypes");

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
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("TriggerGatherRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.createRole(DataTypeHelper.ROLE_ADMIN_ID));

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

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.createRole(DataTypeHelper.ROLE_ADMIN_ID));

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

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.createRole(DataTypeHelper.ROLE_ADMIN_ID));

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

				modeller = ModellerFactory.getModellerForStore(input.store.a3Code);
				modeller.enqueue(input.feedFetch);

				break;
			default:
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("ModelTypeType: input.modelType"));
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

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.createRole(DataTypeHelper.ROLE_ADMIN_ID));

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

				predictor = PredictorFactory.getPredictorForStore(input.store.a3Code);
				predictor.enqueue(input.simpleModelRun);

				break;
			default:
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("ModelTypeType: input.modelType"));
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

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.createRole(DataTypeHelper.ROLE_ADMIN_ID));

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

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.createRole(DataTypeHelper.ROLE_ADMIN_ID));

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

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.createRole(DataTypeHelper.ROLE_ADMIN_ID));

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

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.createRole(DataTypeHelper.ROLE_ADMIN_ID));

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

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.createRole(DataTypeHelper.ROLE_ADMIN_ID));

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

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.createRole(DataTypeHelper.ROLE_ADMIN_ID));

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

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.createRole(DataTypeHelper.ROLE_ADMIN_ID));

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

	public GetEmailTemplatesResponse getEmailTemplates(GetEmailTemplatesRequest input) {
		LOG.finer("Entering getEmailTemplates");
		GetEmailTemplatesResponse output = new GetEmailTemplatesResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("GetEmailTemplatesRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.createRole(DataTypeHelper.ROLE_ADMIN_ID));

			input.pager = ValidationHelper.validatePager(input.pager, "input");

			output.templates = EmailTemplateServiceProvider.provide().getEmailTemplates(input.pager);
			output.pager = input.pager;
			updatePager(output.pager, output.templates, input.pager.totalCount == null ? EmailTemplateServiceProvider.provide().getEmailTemplatesCount() : null);

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}

		LOG.finer("Exiting getEmailTemplates");
		return output;
	}

	public SendEmailResponse sendEmail(SendEmailRequest input) {
		LOG.finer("Entering sendEmail");
		SendEmailResponse output = new SendEmailResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("SendEmailRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.createRole(DataTypeHelper.ROLE_ADMIN_ID));

			input.toAddress = ValidationHelper.validateEmail(input.toAddress, true, "input.toAddress");

			if (input.toAddress == null) {
				input.toUser = ValidationHelper.validateExistingUser(input.toUser, "input.toUser");
			}

			input.template = ValidationHelper.validateExistingEmailTemplate(input.template, "input.template");

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting sendEmail");
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

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.createRole(DataTypeHelper.ROLE_ADMIN_ID));

			input.pager = ValidationHelper.validatePager(input.pager, "input");

			output.items = ItemServiceProvider.provide().getItems(input.pager);
			output.pager = input.pager;
			updatePager(output.pager, output.items, input.pager.totalCount == null ? ItemServiceProvider.provide().getItemsCount() : null);

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

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.createRole(DataTypeHelper.ROLE_ADMIN_ID));

			input.user = ValidationHelper.validateExistingUser(input.user, "input.user");

			List<DataAccount> linkedAccounts = new ArrayList<DataAccount>();
			linkedAccounts = UserServiceProvider.provide().getDataAccounts(input.user, PagerHelper.infinitePager());
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

	public GetRolesAndPermissionsResponse getRolesAndPermissions(GetRolesAndPermissionsRequest input) {
		LOG.finer("Entering getRolesAndPermissions");
		GetRolesAndPermissionsResponse output = new GetRolesAndPermissionsResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(),
						ApiError.InvalidValueNull.getMessage("GetRolesAndPermissionsResponse: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input.accessCode");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.createRole(DataTypeHelper.ROLE_ADMIN_ID));

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

	public UpdateEmailTemplateResponse updateEmailTemplate(UpdateEmailTemplateRequest input) {
		LOG.finer("Entering updateEmailTemplate");
		UpdateEmailTemplateResponse output = new UpdateEmailTemplateResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(),
						ApiError.InvalidValueNull.getMessage("UpdateEmailTemplateRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input.accessCode");

			output.session = input.session = ValidationHelper.validateAndExtendSession(input.session, "input.session");

			if (input.emailTemplate == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("input.emailTemplate"));

			ValidationHelper.validateExistingEmailTemplate(input.emailTemplate, "input.emailTemplate");

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.createRole(DataTypeHelper.ROLE_ADMIN_ID));

			EmailTemplateServiceProvider.provide().updateEmailTemplate(input.emailTemplate);

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting updateEmailTemplate");
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

			ValidationHelper.validateAuthorised(input.session.user, DataTypeHelper.createRole(DataTypeHelper.ROLE_ADMIN_ID));

			output.dataAccounts = DataAccountServiceProvider.provide().getDataAccounts(input.pager);
			// Delete password fields
			for (DataAccount dataAccount : output.dataAccounts) {
				dataAccount.password = null;
			}

			// Retrieve Sources
			Map<Long, DataSource> dataSourcesLookup = new HashMap<Long, DataSource>();
			for (DataAccount dataAccount : output.dataAccounts) {
				if (dataAccount.source.id != null) {
					dataSourcesLookup.put(dataAccount.source.id, null);
				}
			}
			if (dataSourcesLookup != null && dataSourcesLookup.size() > 0) {
				List<DataSource> dataSourcesList = DataSourceServiceProvider.provide().getDataSourceBatch(dataSourcesLookup.keySet());
				for (DataSource dataSource : dataSourcesList) {
					dataSourcesLookup.put(dataSource.id, dataSource);
				}
			}
			// Add needed informations
			for (DataAccount dataAccount : output.dataAccounts) {
				dataAccount.source = dataSourcesLookup.get(dataAccount.source.id);
				dataAccount.user = UserServiceProvider.provide().getDataAccountOwner(dataAccount); // Get Data Account owner
				dataAccount.dataAccountFetches = DataAccountFetchServiceProvider.provide()
						.getFailedDataAccountFetches(dataAccount, PagerHelper.infinitePager()); // Get failed Fetched
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
}
