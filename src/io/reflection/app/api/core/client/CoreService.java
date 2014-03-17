//  
//  core/CoreService.java
//  storedata
//
//  Created by William Shakour on October 2, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.core.client;

import io.reflection.app.api.core.shared.call.ChangePasswordRequest;
import io.reflection.app.api.core.shared.call.ChangePasswordResponse;
import io.reflection.app.api.core.shared.call.ChangeUserDetailsRequest;
import io.reflection.app.api.core.shared.call.ChangeUserDetailsResponse;
import io.reflection.app.api.core.shared.call.CheckUsernameRequest;
import io.reflection.app.api.core.shared.call.CheckUsernameResponse;
import io.reflection.app.api.core.shared.call.ForgotPasswordRequest;
import io.reflection.app.api.core.shared.call.ForgotPasswordResponse;
import io.reflection.app.api.core.shared.call.GetAllTopItemsRequest;
import io.reflection.app.api.core.shared.call.GetAllTopItemsResponse;
import io.reflection.app.api.core.shared.call.GetCategoriesRequest;
import io.reflection.app.api.core.shared.call.GetCategoriesResponse;
import io.reflection.app.api.core.shared.call.GetCountriesRequest;
import io.reflection.app.api.core.shared.call.GetCountriesResponse;
import io.reflection.app.api.core.shared.call.GetItemRanksRequest;
import io.reflection.app.api.core.shared.call.GetItemRanksResponse;
import io.reflection.app.api.core.shared.call.GetLinkedAccountItemsRequest;
import io.reflection.app.api.core.shared.call.GetLinkedAccountItemsResponse;
import io.reflection.app.api.core.shared.call.GetLinkedAccountsRequest;
import io.reflection.app.api.core.shared.call.GetLinkedAccountsResponse;
import io.reflection.app.api.core.shared.call.GetRolesAndPermissionsRequest;
import io.reflection.app.api.core.shared.call.GetRolesAndPermissionsResponse;
import io.reflection.app.api.core.shared.call.GetStoresRequest;
import io.reflection.app.api.core.shared.call.GetStoresResponse;
import io.reflection.app.api.core.shared.call.GetTopItemsRequest;
import io.reflection.app.api.core.shared.call.GetTopItemsResponse;
import io.reflection.app.api.core.shared.call.GetUserDetailsRequest;
import io.reflection.app.api.core.shared.call.GetUserDetailsResponse;
import io.reflection.app.api.core.shared.call.IsAuthorisedRequest;
import io.reflection.app.api.core.shared.call.IsAuthorisedResponse;
import io.reflection.app.api.core.shared.call.LinkAccountRequest;
import io.reflection.app.api.core.shared.call.LinkAccountResponse;
import io.reflection.app.api.core.shared.call.LoginRequest;
import io.reflection.app.api.core.shared.call.LoginResponse;
import io.reflection.app.api.core.shared.call.LogoutRequest;
import io.reflection.app.api.core.shared.call.LogoutResponse;
import io.reflection.app.api.core.shared.call.RegisterUserRequest;
import io.reflection.app.api.core.shared.call.RegisterUserResponse;
import io.reflection.app.api.core.shared.call.SearchForItemRequest;
import io.reflection.app.api.core.shared.call.SearchForItemResponse;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.willshex.gson.json.service.client.JsonService;

public final class CoreService extends JsonService {
	public static final String CoreMethodGetCountries = "GetCountries";

	public Request getCountries(final GetCountriesRequest input, final AsyncCallback<GetCountriesResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(CoreMethodGetCountries, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					GetCountriesResponse outputParameter = new GetCountriesResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
					onCallSuccess(CoreService.this, CoreMethodGetCountries, input, outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodGetCountries, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodGetCountries, input, handle);
		} catch (RequestException e) {
			output.onFailure(e);
			onCallFailure(CoreService.this, CoreMethodGetCountries, input, e);
		}
		return handle;
	}

	public static final String CoreMethodGetStores = "GetStores";

	public Request getStores(final GetStoresRequest input, final AsyncCallback<GetStoresResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(CoreMethodGetStores, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					GetStoresResponse outputParameter = new GetStoresResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
					onCallSuccess(CoreService.this, CoreMethodGetStores, input, outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodGetStores, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodGetStores, input, handle);
		} catch (RequestException e) {
			output.onFailure(e);
			onCallFailure(CoreService.this, CoreMethodGetStores, input, e);
		}
		return handle;
	}

	public static final String CoreMethodGetTopItems = "GetTopItems";

	public Request getTopItems(final GetTopItemsRequest input, final AsyncCallback<GetTopItemsResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(CoreMethodGetTopItems, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					GetTopItemsResponse outputParameter = new GetTopItemsResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
					onCallSuccess(CoreService.this, CoreMethodGetTopItems, input, outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodGetTopItems, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodGetTopItems, input, handle);
		} catch (RequestException e) {
			output.onFailure(e);
			onCallFailure(CoreService.this, CoreMethodGetTopItems, input, e);
		}
		return handle;
	}

	public static final String CoreMethodGetAllTopItems = "GetAllTopItems";

	public Request getAllTopItems(final GetAllTopItemsRequest input, final AsyncCallback<GetAllTopItemsResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(CoreMethodGetAllTopItems, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					GetAllTopItemsResponse outputParameter = new GetAllTopItemsResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
					onCallSuccess(CoreService.this, CoreMethodGetAllTopItems, input, outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodGetAllTopItems, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodGetAllTopItems, input, handle);
		} catch (RequestException e) {
			output.onFailure(e);
			onCallFailure(CoreService.this, CoreMethodGetAllTopItems, input, e);
		}
		return handle;
	}

	public static final String CoreMethodGetItemRanks = "GetItemRanks";

	public Request getItemRanks(final GetItemRanksRequest input, final AsyncCallback<GetItemRanksResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(CoreMethodGetItemRanks, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					GetItemRanksResponse outputParameter = new GetItemRanksResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
					onCallSuccess(CoreService.this, CoreMethodGetItemRanks, input, outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodGetItemRanks, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodGetItemRanks, input, handle);
		} catch (RequestException e) {
			output.onFailure(e);
			onCallFailure(CoreService.this, CoreMethodGetItemRanks, input, e);
		}
		return handle;
	}

	public static final String CoreMethodRegisterUser = "RegisterUser";

	public Request registerUser(final RegisterUserRequest input, final AsyncCallback<RegisterUserResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(CoreMethodRegisterUser, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					RegisterUserResponse outputParameter = new RegisterUserResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
					onCallSuccess(CoreService.this, CoreMethodRegisterUser, input, outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodRegisterUser, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodRegisterUser, input, handle);
		} catch (RequestException e) {
			output.onFailure(e);
			onCallFailure(CoreService.this, CoreMethodRegisterUser, input, e);
		}
		return handle;
	}

	public static final String CoreMethodLogin = "Login";

	public Request login(final LoginRequest input, final AsyncCallback<LoginResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(CoreMethodLogin, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					LoginResponse outputParameter = new LoginResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
					onCallSuccess(CoreService.this, CoreMethodLogin, input, outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodLogin, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodLogin, input, handle);
		} catch (RequestException e) {
			output.onFailure(e);
			onCallFailure(CoreService.this, CoreMethodLogin, input, e);
		}
		return handle;
	}

	public static final String CoreMethodLogout = "Logout";

	public Request logout(final LogoutRequest input, final AsyncCallback<LogoutResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(CoreMethodLogout, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					LogoutResponse outputParameter = new LogoutResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
					onCallSuccess(CoreService.this, CoreMethodLogout, input, outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodLogout, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodLogout, input, handle);
		} catch (RequestException e) {
			output.onFailure(e);
			onCallFailure(CoreService.this, CoreMethodLogout, input, e);
		}
		return handle;
	}

	public static final String CoreMethodChangePassword = "ChangePassword";

	public Request changePassword(final ChangePasswordRequest input, final AsyncCallback<ChangePasswordResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(CoreMethodChangePassword, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					ChangePasswordResponse outputParameter = new ChangePasswordResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
					onCallSuccess(CoreService.this, CoreMethodChangePassword, input, outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodChangePassword, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodChangePassword, input, handle);
		} catch (RequestException e) {
			output.onFailure(e);
			onCallFailure(CoreService.this, CoreMethodChangePassword, input, e);
		}
		return handle;
	}

	public static final String CoreMethodChangeUserDetails = "ChangeUserDetails";

	public Request changeUserDetails(final ChangeUserDetailsRequest input, final AsyncCallback<ChangeUserDetailsResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(CoreMethodChangeUserDetails, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					ChangeUserDetailsResponse outputParameter = new ChangeUserDetailsResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
					onCallSuccess(CoreService.this, CoreMethodChangeUserDetails, input, outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodChangeUserDetails, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodChangeUserDetails, input, handle);
		} catch (RequestException e) {
			output.onFailure(e);
			onCallFailure(CoreService.this, CoreMethodChangeUserDetails, input, e);
		}
		return handle;
	}

	public static final String CoreMethodCheckUsername = "CheckUsername";

	public Request checkUsername(final CheckUsernameRequest input, final AsyncCallback<CheckUsernameResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(CoreMethodCheckUsername, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					CheckUsernameResponse outputParameter = new CheckUsernameResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
					onCallSuccess(CoreService.this, CoreMethodCheckUsername, input, outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodCheckUsername, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodCheckUsername, input, handle);
		} catch (RequestException e) {
			output.onFailure(e);
			onCallFailure(CoreService.this, CoreMethodCheckUsername, input, e);
		}
		return handle;
	}

	public static final String CoreMethodGetRolesAndPermissions = "GetRolesAndPermissions";

	public Request getRolesAndPermissions(final GetRolesAndPermissionsRequest input, final AsyncCallback<GetRolesAndPermissionsResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(CoreMethodGetRolesAndPermissions, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					GetRolesAndPermissionsResponse outputParameter = new GetRolesAndPermissionsResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
					onCallSuccess(CoreService.this, CoreMethodGetRolesAndPermissions, input, outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodGetRolesAndPermissions, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodGetRolesAndPermissions, input, handle);
		} catch (RequestException e) {
			output.onFailure(e);
			onCallFailure(CoreService.this, CoreMethodGetRolesAndPermissions, input, e);
		}
		return handle;
	}

	public static final String CoreMethodGetLinkedAccounts = "GetLinkedAccounts";

	public Request getLinkedAccounts(final GetLinkedAccountsRequest input, final AsyncCallback<GetLinkedAccountsResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(CoreMethodGetLinkedAccounts, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					GetLinkedAccountsResponse outputParameter = new GetLinkedAccountsResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
					onCallSuccess(CoreService.this, CoreMethodGetLinkedAccounts, input, outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodGetLinkedAccounts, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodGetLinkedAccounts, input, handle);
		} catch (RequestException e) {
			output.onFailure(e);
			onCallFailure(CoreService.this, CoreMethodGetLinkedAccounts, input, e);
		}
		return handle;
	}

	public static final String CoreMethodGetLinkedAccountItems = "GetLinkedAccountItems";

	public Request getLinkedAccountItems(final GetLinkedAccountItemsRequest input, final AsyncCallback<GetLinkedAccountItemsResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(CoreMethodGetLinkedAccountItems, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					GetLinkedAccountItemsResponse outputParameter = new GetLinkedAccountItemsResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
					onCallSuccess(CoreService.this, CoreMethodGetLinkedAccountItems, input, outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodGetLinkedAccountItems, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodGetLinkedAccountItems, input, handle);
		} catch (RequestException e) {
			output.onFailure(e);
			onCallFailure(CoreService.this, CoreMethodGetLinkedAccountItems, input, e);
		}
		return handle;
	}

	public static final String CoreMethodLinkAccount = "LinkAccount";

	public Request linkAccount(final LinkAccountRequest input, final AsyncCallback<LinkAccountResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(CoreMethodLinkAccount, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					LinkAccountResponse outputParameter = new LinkAccountResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
					onCallSuccess(CoreService.this, CoreMethodLinkAccount, input, outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodLinkAccount, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodLinkAccount, input, handle);
		} catch (RequestException e) {
			output.onFailure(e);
			onCallFailure(CoreService.this, CoreMethodLinkAccount, input, e);
		}
		return handle;
	}

	public static final String CoreMethodIsAuthorised = "IsAuthorised";

	public Request isAuthorised(final IsAuthorisedRequest input, final AsyncCallback<IsAuthorisedResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(CoreMethodIsAuthorised, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					IsAuthorisedResponse outputParameter = new IsAuthorisedResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
					onCallSuccess(CoreService.this, CoreMethodIsAuthorised, input, outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodIsAuthorised, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodIsAuthorised, input, handle);
		} catch (RequestException e) {
			output.onFailure(e);
			onCallFailure(CoreService.this, CoreMethodIsAuthorised, input, e);
		}
		return handle;
	}

	public static final String CoreMethodSearchForItem = "SearchForItem";

	public Request searchForItem(final SearchForItemRequest input, final AsyncCallback<SearchForItemResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(CoreMethodSearchForItem, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					SearchForItemResponse outputParameter = new SearchForItemResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
					onCallSuccess(CoreService.this, CoreMethodSearchForItem, input, outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodSearchForItem, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodSearchForItem, input, handle);
		} catch (RequestException e) {
			output.onFailure(e);
			onCallFailure(CoreService.this, CoreMethodSearchForItem, input, e);
		}
		return handle;
	}

	public static final String CoreMethodForgotPassword = "ForgotPassword";

	public Request forgotPassword(final ForgotPasswordRequest input, final AsyncCallback<ForgotPasswordResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(CoreMethodForgotPassword, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					ForgotPasswordResponse outputParameter = new ForgotPasswordResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
					onCallSuccess(CoreService.this, CoreMethodForgotPassword, input, outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodForgotPassword, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodForgotPassword, input, handle);
		} catch (RequestException e) {
			output.onFailure(e);
			onCallFailure(CoreService.this, CoreMethodForgotPassword, input, e);
		}
		return handle;
	}

	public static final String CoreMethodGetUserDetails = "GetUserDetails";

	public Request getUserDetails(final GetUserDetailsRequest input, final AsyncCallback<GetUserDetailsResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(CoreMethodGetUserDetails, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					GetUserDetailsResponse outputParameter = new GetUserDetailsResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
					onCallSuccess(CoreService.this, CoreMethodGetUserDetails, input, outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodGetUserDetails, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodGetUserDetails, input, handle);
		} catch (RequestException e) {
			output.onFailure(e);
			onCallFailure(CoreService.this, CoreMethodGetUserDetails, input, e);
		}
		return handle;
	}

	public static final String CoreMethodGetCategories = "GetCategories";

	public Request getCategories(final GetCategoriesRequest input, final AsyncCallback<GetCategoriesResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(CoreMethodGetCategories, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					GetCategoriesResponse outputParameter = new GetCategoriesResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
					onCallSuccess(CoreService.this, CoreMethodGetCategories, input, outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodGetCategories, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodGetCategories, input, handle);
		} catch (RequestException e) {
			output.onFailure(e);
			onCallFailure(CoreService.this, CoreMethodGetCategories, input, e);
		}
		return handle;
	}
}