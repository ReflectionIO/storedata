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

	public void getUsers(GetUsersRequest input, final AsyncCallback<GetUsersResponse> output) {
		try {
			sendRequest(AdminMethodGetUsers, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					GetUsersResponse outputParameter = new GetUsersResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
				}
			});
		} catch (RequestException e) {
			output.onFailure(e);
		}
	}

	public static final String AdminMethodGetUsersCount = "GetUsersCount";

	public void getUsersCount(GetUsersCountRequest input, final AsyncCallback<GetUsersCountResponse> output) {
		try {
			sendRequest(AdminMethodGetUsersCount, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					GetUsersCountResponse outputParameter = new GetUsersCountResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
				}
			});
		} catch (RequestException e) {
			output.onFailure(e);
		}
	}

	public static final String AdminMethodGetModelOutcome = "GetModelOutcome";

	public void getModelOutcome(GetModelOutcomeRequest input, final AsyncCallback<GetModelOutcomeResponse> output) {
		try {
			sendRequest(AdminMethodGetModelOutcome, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					GetModelOutcomeResponse outputParameter = new GetModelOutcomeResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
				}
			});
		} catch (RequestException e) {
			output.onFailure(e);
		}
	}

	public static final String AdminMethodGetFeedFetches = "GetFeedFetches";

	public void getFeedFetches(GetFeedFetchesRequest input, final AsyncCallback<GetFeedFetchesResponse> output) {
		try {
			sendRequest(AdminMethodGetFeedFetches, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					GetFeedFetchesResponse outputParameter = new GetFeedFetchesResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
				}
			});
		} catch (RequestException e) {
			output.onFailure(e);
		}
	}

	public static final String AdminMethodTriggerGather = "TriggerGather";

	public void triggerGather(TriggerGatherRequest input, final AsyncCallback<TriggerGatherResponse> output) {
		try {
			sendRequest(AdminMethodTriggerGather, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					TriggerGatherResponse outputParameter = new TriggerGatherResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
				}
			});
		} catch (RequestException e) {
			output.onFailure(e);
		}
	}

	public static final String AdminMethodTriggerIngest = "TriggerIngest";

	public void triggerIngest(TriggerIngestRequest input, final AsyncCallback<TriggerIngestResponse> output) {
		try {
			sendRequest(AdminMethodTriggerIngest, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					TriggerIngestResponse outputParameter = new TriggerIngestResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
				}
			});
		} catch (RequestException e) {
			output.onFailure(e);
		}
	}

	public static final String AdminMethodTriggerModel = "TriggerModel";

	public void triggerModel(TriggerModelRequest input, final AsyncCallback<TriggerModelResponse> output) {
		try {
			sendRequest(AdminMethodTriggerModel, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					TriggerModelResponse outputParameter = new TriggerModelResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
				}
			});
		} catch (RequestException e) {
			output.onFailure(e);
		}
	}

	public static final String AdminMethodTriggerPredict = "TriggerPredict";

	public void triggerPredict(TriggerPredictRequest input, final AsyncCallback<TriggerPredictResponse> output) {
		try {
			sendRequest(AdminMethodTriggerPredict, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					TriggerPredictResponse outputParameter = new TriggerPredictResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
				}
			});
		} catch (RequestException e) {
			output.onFailure(e);
		}
	}

	public static final String AdminMethodSetPassword = "SetPassword";

	public void setPassword(SetPasswordRequest input, final AsyncCallback<SetPasswordResponse> output) {
		try {
			sendRequest(AdminMethodSetPassword, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					SetPasswordResponse outputParameter = new SetPasswordResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
				}
			});
		} catch (RequestException e) {
			output.onFailure(e);
		}
	}

	public static final String AdminMethodAssignRole = "AssignRole";

	public void assignRole(AssignRoleRequest input, final AsyncCallback<AssignRoleResponse> output) {
		try {
			sendRequest(AdminMethodAssignRole, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					AssignRoleResponse outputParameter = new AssignRoleResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
				}
			});
		} catch (RequestException e) {
			output.onFailure(e);
		}
	}

	public static final String AdminMethodGetRoles = "GetRoles";

	public void getRoles(GetRolesRequest input, final AsyncCallback<GetRolesResponse> output) {
		try {
			sendRequest(AdminMethodGetRoles, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					GetRolesResponse outputParameter = new GetRolesResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
				}
			});
		} catch (RequestException e) {
			output.onFailure(e);
		}
	}

	public static final String AdminMethodGetPermissions = "GetPermissions";

	public void getPermissions(GetPermissionsRequest input, final AsyncCallback<GetPermissionsResponse> output) {
		try {
			sendRequest(AdminMethodGetPermissions, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					GetPermissionsResponse outputParameter = new GetPermissionsResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
				}
			});
		} catch (RequestException e) {
			output.onFailure(e);
		}
	}

	public static final String AdminMethodGetEmailTemplates = "GetEmailTemplates";

	public void getEmailTemplates(GetEmailTemplatesRequest input, final AsyncCallback<GetEmailTemplatesResponse> output) {
		try {
			sendRequest(AdminMethodGetEmailTemplates, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					GetEmailTemplatesResponse outputParameter = new GetEmailTemplatesResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
				}
			});
		} catch (RequestException e) {
			output.onFailure(e);
		}
	}

	public static final String AdminMethodSendEmail = "SendEmail";

	public void sendEmail(SendEmailRequest input, final AsyncCallback<SendEmailResponse> output) {
		try {
			sendRequest(AdminMethodSendEmail, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					SendEmailResponse outputParameter = new SendEmailResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
				}
			});
		} catch (RequestException e) {
			output.onFailure(e);
		}
	}

	public static final String AdminMethodGetItems = "GetItems";

	public void getItems(GetItemsRequest input, final AsyncCallback<GetItemsResponse> output) {
		try {
			sendRequest(AdminMethodGetItems, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					GetItemsResponse outputParameter = new GetItemsResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
				}
			});
		} catch (RequestException e) {
			output.onFailure(e);
		}
	}

}