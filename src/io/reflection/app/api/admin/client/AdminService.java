//  
//  admin/AdminService.java
//  reflection.io
//
//  Created by William Shakour on October 9, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.client;

import io.reflection.app.api.admin.shared.call.AssignRoleRequest;
import io.reflection.app.api.admin.shared.call.AssignRoleResponse;
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
import io.reflection.app.api.admin.shared.call.GetRolesRequest;
import io.reflection.app.api.admin.shared.call.GetRolesResponse;
import io.reflection.app.api.admin.shared.call.GetUsersCountRequest;
import io.reflection.app.api.admin.shared.call.GetUsersCountResponse;
import io.reflection.app.api.admin.shared.call.GetUsersRequest;
import io.reflection.app.api.admin.shared.call.GetUsersResponse;
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

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.willshex.gson.json.service.client.JsonService;

public final class AdminService extends JsonService {
	public static final String AdminMethodGetUsers = "GetUsers";

	public Request getUsers(final GetUsersRequest input, final AsyncCallback<GetUsersResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(AdminMethodGetUsers, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					GetUsersResponse outputParameter = new GetUsersResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
					onCallSuccess(AdminService.this, AdminMethodGetUsers, input, outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(AdminService.this, AdminMethodGetUsers, input, exception);
				}
			});
			onCallStart(AdminService.this, AdminMethodGetUsers, input, handle);
		} catch (RequestException e) {
			output.onFailure(e);
			onCallFailure(AdminService.this, AdminMethodGetUsers, input, e);
		}
		return handle;
	}

	public static final String AdminMethodGetUsersCount = "GetUsersCount";

	public Request getUsersCount(final GetUsersCountRequest input, final AsyncCallback<GetUsersCountResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(AdminMethodGetUsersCount, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					GetUsersCountResponse outputParameter = new GetUsersCountResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
					onCallSuccess(AdminService.this, AdminMethodGetUsersCount, input, outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(AdminService.this, AdminMethodGetUsersCount, input, exception);
				}
			});
			onCallStart(AdminService.this, AdminMethodGetUsersCount, input, handle);
		} catch (RequestException e) {
			output.onFailure(e);
			onCallFailure(AdminService.this, AdminMethodGetUsersCount, input, e);
		}
		return handle;
	}

	public static final String AdminMethodGetModelOutcome = "GetModelOutcome";

	public Request getModelOutcome(final GetModelOutcomeRequest input, final AsyncCallback<GetModelOutcomeResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(AdminMethodGetModelOutcome, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					GetModelOutcomeResponse outputParameter = new GetModelOutcomeResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
					onCallSuccess(AdminService.this, AdminMethodGetModelOutcome, input, outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(AdminService.this, AdminMethodGetModelOutcome, input, exception);
				}
			});
			onCallStart(AdminService.this, AdminMethodGetModelOutcome, input, handle);
		} catch (RequestException e) {
			output.onFailure(e);
			onCallFailure(AdminService.this, AdminMethodGetModelOutcome, input, e);
		}
		return handle;
	}

	public static final String AdminMethodGetFeedFetches = "GetFeedFetches";

	public Request getFeedFetches(final GetFeedFetchesRequest input, final AsyncCallback<GetFeedFetchesResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(AdminMethodGetFeedFetches, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					GetFeedFetchesResponse outputParameter = new GetFeedFetchesResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
					onCallSuccess(AdminService.this, AdminMethodGetFeedFetches, input, outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(AdminService.this, AdminMethodGetFeedFetches, input, exception);
				}
			});
			onCallStart(AdminService.this, AdminMethodGetFeedFetches, input, handle);
		} catch (RequestException e) {
			output.onFailure(e);
			onCallFailure(AdminService.this, AdminMethodGetFeedFetches, input, e);
		}
		return handle;
	}

	public static final String AdminMethodTriggerGather = "TriggerGather";

	public Request triggerGather(final TriggerGatherRequest input, final AsyncCallback<TriggerGatherResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(AdminMethodTriggerGather, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					TriggerGatherResponse outputParameter = new TriggerGatherResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
					onCallSuccess(AdminService.this, AdminMethodTriggerGather, input, outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(AdminService.this, AdminMethodTriggerGather, input, exception);
				}
			});
			onCallStart(AdminService.this, AdminMethodTriggerGather, input, handle);
		} catch (RequestException e) {
			output.onFailure(e);
			onCallFailure(AdminService.this, AdminMethodTriggerGather, input, e);
		}
		return handle;
	}

	public static final String AdminMethodTriggerIngest = "TriggerIngest";

	public Request triggerIngest(final TriggerIngestRequest input, final AsyncCallback<TriggerIngestResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(AdminMethodTriggerIngest, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					TriggerIngestResponse outputParameter = new TriggerIngestResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
					onCallSuccess(AdminService.this, AdminMethodTriggerIngest, input, outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(AdminService.this, AdminMethodTriggerIngest, input, exception);
				}
			});
			onCallStart(AdminService.this, AdminMethodTriggerIngest, input, handle);
		} catch (RequestException e) {
			output.onFailure(e);
			onCallFailure(AdminService.this, AdminMethodTriggerIngest, input, e);
		}
		return handle;
	}

	public static final String AdminMethodTriggerModel = "TriggerModel";

	public Request triggerModel(final TriggerModelRequest input, final AsyncCallback<TriggerModelResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(AdminMethodTriggerModel, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					TriggerModelResponse outputParameter = new TriggerModelResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
					onCallSuccess(AdminService.this, AdminMethodTriggerModel, input, outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(AdminService.this, AdminMethodTriggerModel, input, exception);
				}
			});
			onCallStart(AdminService.this, AdminMethodTriggerModel, input, handle);
		} catch (RequestException e) {
			output.onFailure(e);
			onCallFailure(AdminService.this, AdminMethodTriggerModel, input, e);
		}
		return handle;
	}

	public static final String AdminMethodTriggerPredict = "TriggerPredict";

	public Request triggerPredict(final TriggerPredictRequest input, final AsyncCallback<TriggerPredictResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(AdminMethodTriggerPredict, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					TriggerPredictResponse outputParameter = new TriggerPredictResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
					onCallSuccess(AdminService.this, AdminMethodTriggerPredict, input, outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(AdminService.this, AdminMethodTriggerPredict, input, exception);
				}
			});
			onCallStart(AdminService.this, AdminMethodTriggerPredict, input, handle);
		} catch (RequestException e) {
			output.onFailure(e);
			onCallFailure(AdminService.this, AdminMethodTriggerPredict, input, e);
		}
		return handle;
	}

	public static final String AdminMethodSetPassword = "SetPassword";

	public Request setPassword(final SetPasswordRequest input, final AsyncCallback<SetPasswordResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(AdminMethodSetPassword, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					SetPasswordResponse outputParameter = new SetPasswordResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
					onCallSuccess(AdminService.this, AdminMethodSetPassword, input, outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(AdminService.this, AdminMethodSetPassword, input, exception);
				}
			});
			onCallStart(AdminService.this, AdminMethodSetPassword, input, handle);
		} catch (RequestException e) {
			output.onFailure(e);
			onCallFailure(AdminService.this, AdminMethodSetPassword, input, e);
		}
		return handle;
	}

	public static final String AdminMethodAssignRole = "AssignRole";

	public Request assignRole(final AssignRoleRequest input, final AsyncCallback<AssignRoleResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(AdminMethodAssignRole, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					AssignRoleResponse outputParameter = new AssignRoleResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
					onCallSuccess(AdminService.this, AdminMethodAssignRole, input, outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(AdminService.this, AdminMethodAssignRole, input, exception);
				}
			});
			onCallStart(AdminService.this, AdminMethodAssignRole, input, handle);
		} catch (RequestException e) {
			output.onFailure(e);
			onCallFailure(AdminService.this, AdminMethodAssignRole, input, e);
		}
		return handle;
	}

	public static final String AdminMethodGetRoles = "GetRoles";

	public Request getRoles(final GetRolesRequest input, final AsyncCallback<GetRolesResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(AdminMethodGetRoles, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					GetRolesResponse outputParameter = new GetRolesResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
					onCallSuccess(AdminService.this, AdminMethodGetRoles, input, outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(AdminService.this, AdminMethodGetRoles, input, exception);
				}
			});
			onCallStart(AdminService.this, AdminMethodGetRoles, input, handle);
		} catch (RequestException e) {
			output.onFailure(e);
			onCallFailure(AdminService.this, AdminMethodGetRoles, input, e);
		}
		return handle;
	}

	public static final String AdminMethodGetPermissions = "GetPermissions";

	public Request getPermissions(final GetPermissionsRequest input, final AsyncCallback<GetPermissionsResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(AdminMethodGetPermissions, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					GetPermissionsResponse outputParameter = new GetPermissionsResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
					onCallSuccess(AdminService.this, AdminMethodGetPermissions, input, outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(AdminService.this, AdminMethodGetPermissions, input, exception);
				}
			});
			onCallStart(AdminService.this, AdminMethodGetPermissions, input, handle);
		} catch (RequestException e) {
			output.onFailure(e);
			onCallFailure(AdminService.this, AdminMethodGetPermissions, input, e);
		}
		return handle;
	}

	public static final String AdminMethodGetEmailTemplates = "GetEmailTemplates";

	public Request getEmailTemplates(final GetEmailTemplatesRequest input, final AsyncCallback<GetEmailTemplatesResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(AdminMethodGetEmailTemplates, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					GetEmailTemplatesResponse outputParameter = new GetEmailTemplatesResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
					onCallSuccess(AdminService.this, AdminMethodGetEmailTemplates, input, outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(AdminService.this, AdminMethodGetEmailTemplates, input, exception);
				}
			});
			onCallStart(AdminService.this, AdminMethodGetEmailTemplates, input, handle);
		} catch (RequestException e) {
			output.onFailure(e);
			onCallFailure(AdminService.this, AdminMethodGetEmailTemplates, input, e);
		}
		return handle;
	}

	public static final String AdminMethodSendEmail = "SendEmail";

	public Request sendEmail(final SendEmailRequest input, final AsyncCallback<SendEmailResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(AdminMethodSendEmail, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					SendEmailResponse outputParameter = new SendEmailResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
					onCallSuccess(AdminService.this, AdminMethodSendEmail, input, outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(AdminService.this, AdminMethodSendEmail, input, exception);
				}
			});
			onCallStart(AdminService.this, AdminMethodSendEmail, input, handle);
		} catch (RequestException e) {
			output.onFailure(e);
			onCallFailure(AdminService.this, AdminMethodSendEmail, input, e);
		}
		return handle;
	}

	public static final String AdminMethodGetItems = "GetItems";

	public Request getItems(final GetItemsRequest input, final AsyncCallback<GetItemsResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(AdminMethodGetItems, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					GetItemsResponse outputParameter = new GetItemsResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
					onCallSuccess(AdminService.this, AdminMethodGetItems, input, outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(AdminService.this, AdminMethodGetItems, input, exception);
				}
			});
			onCallStart(AdminService.this, AdminMethodGetItems, input, handle);
		} catch (RequestException e) {
			output.onFailure(e);
			onCallFailure(AdminService.this, AdminMethodGetItems, input, e);
		}
		return handle;
	}
}