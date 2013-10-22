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
import io.reflection.app.api.admin.shared.call.GetFeedFetchesRequest;
import io.reflection.app.api.admin.shared.call.GetFeedFetchesResponse;
import io.reflection.app.api.admin.shared.call.GetModelOutcomeRequest;
import io.reflection.app.api.admin.shared.call.GetModelOutcomeResponse;
import io.reflection.app.api.admin.shared.call.GetUsersCountRequest;
import io.reflection.app.api.admin.shared.call.GetUsersCountResponse;
import io.reflection.app.api.admin.shared.call.GetUsersRequest;
import io.reflection.app.api.admin.shared.call.GetUsersResponse;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.input.ValidationError;
import io.reflection.app.input.ValidationHelper;
import io.reflection.app.service.fetchfeed.FeedFetchServiceProvider;
import io.reflection.app.service.user.UserServiceProvider;

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

			if (input.listTypes == null)
				throw new InputValidationException(ValidationError.InvalidValueNull.getCode(),
						ValidationError.InvalidValueNull.getMessage("List: input.listType"));

			String validatedListType, listType;
			StringBuffer badListTypes = new StringBuffer();
			for (int i = 0; i < input.listTypes.size(); i++) {
				listType = input.listTypes.get(i);
				validatedListType = ValidationHelper.validateListType(listType, input.store);

				input.listTypes.remove(i);

				if (listType != null) {
					input.listTypes.add(i, validatedListType);
				} else {
					if (badListTypes.length() != 0) {
						badListTypes.append(",");
					}

					badListTypes.append(listType);
				}
			}

			if (input.listTypes.size() == 0)
				throw new InputValidationException(ValidationError.ListTypeNotFound.getCode(),
						ValidationError.ListTypeNotFound.getMessage("String: input.listTypes"));

			output.feedFetches = FeedFetchServiceProvider.provide().getFeedFetches(input.store, input.country, input.listTypes, input.pager);

			output.pager = input.pager;
			updatePager(output.pager, output.feedFetches,
					input.pager.totalCount == null ? FeedFetchServiceProvider.provide().getFeedFetchesCount(input.store, input.country, input.listTypes) : null);

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting getFeedFetches");
		return output;
	}
}