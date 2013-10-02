//  
//  Lookup.java
//  reflection.io
//
//  Created by William Shakour on September 17, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.lookup;

import io.reflection.app.api.lookup.shared.call.LookupApplicationRequest;
import io.reflection.app.api.lookup.shared.call.LookupApplicationResponse;
import io.reflection.app.input.ValidationError;
import io.reflection.app.input.ValidationHelper;
import io.reflection.app.service.application.ApplicationServiceProvider;
import io.reflection.app.shared.datatypes.Application;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.willshex.gson.json.service.server.ActionHandler;
import com.willshex.gson.json.service.server.InputValidationException;
import com.willshex.gson.json.service.shared.StatusType;

public final class Lookup extends ActionHandler {
	private static final Logger LOG = Logger.getLogger(Lookup.class.getName());

	public LookupApplicationResponse lookupApplication(LookupApplicationRequest input) {
		LOG.finer("Entering lookupApplication");
		LookupApplicationResponse output = new LookupApplicationResponse();
		try {
			if (input == null)
				throw new InputValidationException(ValidationError.LookupApplicationNeedsInternalOrExternalId.getCode(),
						ValidationError.LookupApplicationNeedsInternalOrExternalId.getMessage("LookupApplicationRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input");

			boolean isInternalIdsLookup = (input.internalIds != null && input.internalIds.size() > 0);
			boolean isExternalIdsLookup = (input.externalIds != null && input.externalIds.size() > 0);

			if (!isExternalIdsLookup && !isInternalIdsLookup)
				throw new InputValidationException(ValidationError.LookupApplicationNeedsInternalOrExternalId.getCode(),
						ValidationError.LookupApplicationNeedsInternalOrExternalId.getMessage("LookupApplicationRequest: input"));

			output.applications = new ArrayList<Application>();

			if (isInternalIdsLookup) {
				List<Application> lookup = ApplicationServiceProvider.provide().lookupInternalIdsApplication(input.internalIds, input.detail);

				if (lookup != null) {
					output.applications.addAll(lookup);
				}
			}

			if (isExternalIdsLookup) {
				List<Application> lookup = ApplicationServiceProvider.provide().lookupExternalIdsApplication(input.externalIds, input.detail);

				if (lookup != null) {
					output.applications.addAll(lookup);
				}
			}

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting lookupApplication");
		return output;
	}
}