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
import io.reflection.app.api.admin.shared.call.GetUsersCountRequest;
import io.reflection.app.api.admin.shared.call.GetUsersCountResponse;
import io.reflection.app.api.admin.shared.call.GetUsersRequest;
import io.reflection.app.api.admin.shared.call.GetUsersResponse;
import io.reflection.app.input.ValidationError;
import io.reflection.app.input.ValidationHelper;
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
}