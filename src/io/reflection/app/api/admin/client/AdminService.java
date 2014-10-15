//  
//  admin/AdminService.java
//  reflection.io
//
//  Created by William Shakour on October 9, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.client;

import io.reflection.app.api.admin.shared.call.AssignPermissionRequest;
import io.reflection.app.api.admin.shared.call.AssignPermissionResponse;
import io.reflection.app.api.admin.shared.call.AssignRoleRequest;
import io.reflection.app.api.admin.shared.call.AssignRoleResponse;
import io.reflection.app.api.admin.shared.call.GetDataAccountFetchesRequest;
import io.reflection.app.api.admin.shared.call.GetDataAccountFetchesResponse;
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

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.willshex.gson.json.service.client.HttpException;
import com.willshex.gson.json.service.client.JsonService;

public final class AdminService extends JsonService {
	public static final String AdminMethodGetUsers = "GetUsers";

	public Request getUsers(final GetUsersRequest input, final AsyncCallback<GetUsersResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(AdminMethodGetUsers, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					try {
						GetUsersResponse outputParameter = new GetUsersResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(AdminService.this, AdminMethodGetUsers, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(AdminService.this, AdminMethodGetUsers, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(AdminService.this, AdminMethodGetUsers, input, exception);
				}
			});
			onCallStart(AdminService.this, AdminMethodGetUsers, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(AdminService.this, AdminMethodGetUsers, input, exception);
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
					try {
						GetUsersCountResponse outputParameter = new GetUsersCountResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(AdminService.this, AdminMethodGetUsersCount, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(AdminService.this, AdminMethodGetUsersCount, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(AdminService.this, AdminMethodGetUsersCount, input, exception);
				}
			});
			onCallStart(AdminService.this, AdminMethodGetUsersCount, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(AdminService.this, AdminMethodGetUsersCount, input, exception);
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
					try {
						GetModelOutcomeResponse outputParameter = new GetModelOutcomeResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(AdminService.this, AdminMethodGetModelOutcome, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(AdminService.this, AdminMethodGetModelOutcome, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(AdminService.this, AdminMethodGetModelOutcome, input, exception);
				}
			});
			onCallStart(AdminService.this, AdminMethodGetModelOutcome, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(AdminService.this, AdminMethodGetModelOutcome, input, exception);
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
					try {
						GetFeedFetchesResponse outputParameter = new GetFeedFetchesResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(AdminService.this, AdminMethodGetFeedFetches, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(AdminService.this, AdminMethodGetFeedFetches, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(AdminService.this, AdminMethodGetFeedFetches, input, exception);
				}
			});
			onCallStart(AdminService.this, AdminMethodGetFeedFetches, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(AdminService.this, AdminMethodGetFeedFetches, input, exception);
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
					try {
						TriggerGatherResponse outputParameter = new TriggerGatherResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(AdminService.this, AdminMethodTriggerGather, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(AdminService.this, AdminMethodTriggerGather, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(AdminService.this, AdminMethodTriggerGather, input, exception);
				}
			});
			onCallStart(AdminService.this, AdminMethodTriggerGather, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(AdminService.this, AdminMethodTriggerGather, input, exception);
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
					try {
						TriggerIngestResponse outputParameter = new TriggerIngestResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(AdminService.this, AdminMethodTriggerIngest, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(AdminService.this, AdminMethodTriggerIngest, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(AdminService.this, AdminMethodTriggerIngest, input, exception);
				}
			});
			onCallStart(AdminService.this, AdminMethodTriggerIngest, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(AdminService.this, AdminMethodTriggerIngest, input, exception);
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
					try {
						TriggerModelResponse outputParameter = new TriggerModelResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(AdminService.this, AdminMethodTriggerModel, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(AdminService.this, AdminMethodTriggerModel, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(AdminService.this, AdminMethodTriggerModel, input, exception);
				}
			});
			onCallStart(AdminService.this, AdminMethodTriggerModel, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(AdminService.this, AdminMethodTriggerModel, input, exception);
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
					try {
						TriggerPredictResponse outputParameter = new TriggerPredictResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(AdminService.this, AdminMethodTriggerPredict, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(AdminService.this, AdminMethodTriggerPredict, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(AdminService.this, AdminMethodTriggerPredict, input, exception);
				}
			});
			onCallStart(AdminService.this, AdminMethodTriggerPredict, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(AdminService.this, AdminMethodTriggerPredict, input, exception);
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
					try {
						SetPasswordResponse outputParameter = new SetPasswordResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(AdminService.this, AdminMethodSetPassword, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(AdminService.this, AdminMethodSetPassword, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(AdminService.this, AdminMethodSetPassword, input, exception);
				}
			});
			onCallStart(AdminService.this, AdminMethodSetPassword, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(AdminService.this, AdminMethodSetPassword, input, exception);
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
					try {
						AssignRoleResponse outputParameter = new AssignRoleResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(AdminService.this, AdminMethodAssignRole, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(AdminService.this, AdminMethodAssignRole, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(AdminService.this, AdminMethodAssignRole, input, exception);
				}
			});
			onCallStart(AdminService.this, AdminMethodAssignRole, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(AdminService.this, AdminMethodAssignRole, input, exception);
		}
		return handle;
	}

	public static final String AdminMethodAssignPermission = "AssignPermission";

	public Request assignPermission(final AssignPermissionRequest input, final AsyncCallback<AssignPermissionResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(AdminMethodAssignPermission, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					try {
						AssignPermissionResponse outputParameter = new AssignPermissionResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(AdminService.this, AdminMethodAssignPermission, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(AdminService.this, AdminMethodAssignPermission, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(AdminService.this, AdminMethodAssignPermission, input, exception);
				}
			});
			onCallStart(AdminService.this, AdminMethodAssignPermission, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(AdminService.this, AdminMethodAssignPermission, input, exception);
		}
		return handle;
	}

	public static final String AdminMethodRevokeRole = "RevokeRole";

	public Request revokeRole(final RevokeRoleRequest input, final AsyncCallback<RevokeRoleResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(AdminMethodRevokeRole, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					try {
						RevokeRoleResponse outputParameter = new RevokeRoleResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(AdminService.this, AdminMethodRevokeRole, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(AdminService.this, AdminMethodRevokeRole, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(AdminService.this, AdminMethodRevokeRole, input, exception);
				}
			});
			onCallStart(AdminService.this, AdminMethodRevokeRole, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(AdminService.this, AdminMethodRevokeRole, input, exception);
		}
		return handle;
	}

	public static final String AdminMethodRevokePermission = "RevokePermission";

	public Request revokePermission(final RevokePermissionRequest input, final AsyncCallback<RevokePermissionResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(AdminMethodRevokePermission, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					try {
						RevokePermissionResponse outputParameter = new RevokePermissionResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(AdminService.this, AdminMethodRevokePermission, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(AdminService.this, AdminMethodRevokePermission, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(AdminService.this, AdminMethodRevokePermission, input, exception);
				}
			});
			onCallStart(AdminService.this, AdminMethodRevokePermission, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(AdminService.this, AdminMethodRevokePermission, input, exception);
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
					try {
						GetRolesResponse outputParameter = new GetRolesResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(AdminService.this, AdminMethodGetRoles, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(AdminService.this, AdminMethodGetRoles, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(AdminService.this, AdminMethodGetRoles, input, exception);
				}
			});
			onCallStart(AdminService.this, AdminMethodGetRoles, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(AdminService.this, AdminMethodGetRoles, input, exception);
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
					try {
						GetPermissionsResponse outputParameter = new GetPermissionsResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(AdminService.this, AdminMethodGetPermissions, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(AdminService.this, AdminMethodGetPermissions, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(AdminService.this, AdminMethodGetPermissions, input, exception);
				}
			});
			onCallStart(AdminService.this, AdminMethodGetPermissions, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(AdminService.this, AdminMethodGetPermissions, input, exception);
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
					try {
						GetEmailTemplatesResponse outputParameter = new GetEmailTemplatesResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(AdminService.this, AdminMethodGetEmailTemplates, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(AdminService.this, AdminMethodGetEmailTemplates, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(AdminService.this, AdminMethodGetEmailTemplates, input, exception);
				}
			});
			onCallStart(AdminService.this, AdminMethodGetEmailTemplates, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(AdminService.this, AdminMethodGetEmailTemplates, input, exception);
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
					try {
						SendEmailResponse outputParameter = new SendEmailResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(AdminService.this, AdminMethodSendEmail, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(AdminService.this, AdminMethodSendEmail, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(AdminService.this, AdminMethodSendEmail, input, exception);
				}
			});
			onCallStart(AdminService.this, AdminMethodSendEmail, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(AdminService.this, AdminMethodSendEmail, input, exception);
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
					try {
						GetItemsResponse outputParameter = new GetItemsResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(AdminService.this, AdminMethodGetItems, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(AdminService.this, AdminMethodGetItems, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(AdminService.this, AdminMethodGetItems, input, exception);
				}
			});
			onCallStart(AdminService.this, AdminMethodGetItems, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(AdminService.this, AdminMethodGetItems, input, exception);
		}
		return handle;
	}

	public static final String AdminMethodDeleteUser = "DeleteUser";

	public Request deleteUser(final DeleteUserRequest input, final AsyncCallback<DeleteUserResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(AdminMethodDeleteUser, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					try {
						DeleteUserResponse outputParameter = new DeleteUserResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(AdminService.this, AdminMethodDeleteUser, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(AdminService.this, AdminMethodDeleteUser, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(AdminService.this, AdminMethodDeleteUser, input, exception);
				}
			});
			onCallStart(AdminService.this, AdminMethodDeleteUser, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(AdminService.this, AdminMethodDeleteUser, input, exception);
		}
		return handle;
	}

	public static final String AdminMethodGetRolesAndPermissions = "GetRolesAndPermissions";

	public Request getRolesAndPermissions(final GetRolesAndPermissionsRequest input, final AsyncCallback<GetRolesAndPermissionsResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(AdminMethodGetRolesAndPermissions, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					try {
						GetRolesAndPermissionsResponse outputParameter = new GetRolesAndPermissionsResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(AdminService.this, AdminMethodGetRolesAndPermissions, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(AdminService.this, AdminMethodGetRolesAndPermissions, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(AdminService.this, AdminMethodGetRolesAndPermissions, input, exception);
				}
			});
			onCallStart(AdminService.this, AdminMethodGetRolesAndPermissions, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(AdminService.this, AdminMethodGetRolesAndPermissions, input, exception);
		}
		return handle;
	}

	public static final String AdminMethodUpdateEmailTemplate = "UpdateEmailTemplate";

	public Request updateEmailTemplate(final UpdateEmailTemplateRequest input, final AsyncCallback<UpdateEmailTemplateResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(AdminMethodUpdateEmailTemplate, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					try {
						UpdateEmailTemplateResponse outputParameter = new UpdateEmailTemplateResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(AdminService.this, AdminMethodUpdateEmailTemplate, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(AdminService.this, AdminMethodUpdateEmailTemplate, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(AdminService.this, AdminMethodUpdateEmailTemplate, input, exception);
				}
			});
			onCallStart(AdminService.this, AdminMethodUpdateEmailTemplate, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(AdminService.this, AdminMethodUpdateEmailTemplate, input, exception);
		}
		return handle;
	}

	public static final String AdminMethodGetDataAccounts = "GetDataAccounts";

	public Request getDataAccounts(final GetDataAccountsRequest input, final AsyncCallback<GetDataAccountsResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(AdminMethodGetDataAccounts, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					try {
						GetDataAccountsResponse outputParameter = new GetDataAccountsResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(AdminService.this, AdminMethodGetDataAccounts, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(AdminService.this, AdminMethodGetDataAccounts, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(AdminService.this, AdminMethodGetDataAccounts, input, exception);
				}
			});
			onCallStart(AdminService.this, AdminMethodGetDataAccounts, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(AdminService.this, AdminMethodGetDataAccounts, input, exception);
		}
		return handle;
	}

	public static final String AdminMethodGetDataAccountFetches = "GetDataAccountFetches";

	public Request getDataAccountFetches(final GetDataAccountFetchesRequest input, final AsyncCallback<GetDataAccountFetchesResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(AdminMethodGetDataAccountFetches, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					try {
						GetDataAccountFetchesResponse outputParameter = new GetDataAccountFetchesResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(AdminService.this, AdminMethodGetDataAccountFetches, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(AdminService.this, AdminMethodGetDataAccountFetches, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(AdminService.this, AdminMethodGetDataAccountFetches, input, exception);
				}
			});
			onCallStart(AdminService.this, AdminMethodGetDataAccountFetches, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(AdminService.this, AdminMethodGetDataAccountFetches, input, exception);
		}
		return handle;
	}

}