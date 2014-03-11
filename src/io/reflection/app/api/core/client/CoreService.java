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

	public void getCountries(GetCountriesRequest input, final AsyncCallback<GetCountriesResponse> output) {
		try {
			sendRequest(CoreMethodGetCountries, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					GetCountriesResponse outputParameter = new GetCountriesResponse();
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

	public static final String CoreMethodGetStores = "GetStores";

	public void getStores(GetStoresRequest input, final AsyncCallback<GetStoresResponse> output) {
		try {
			sendRequest(CoreMethodGetStores, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					GetStoresResponse outputParameter = new GetStoresResponse();
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

	public static final String CoreMethodGetTopItems = "GetTopItems";

	public void getTopItems(GetTopItemsRequest input, final AsyncCallback<GetTopItemsResponse> output) {
		try {
			sendRequest(CoreMethodGetTopItems, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					GetTopItemsResponse outputParameter = new GetTopItemsResponse();
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

	public static final String CoreMethodGetAllTopItems = "GetAllTopItems";

	public void getAllTopItems(GetAllTopItemsRequest input, final AsyncCallback<GetAllTopItemsResponse> output) {
		try {
			sendRequest(CoreMethodGetAllTopItems, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					GetAllTopItemsResponse outputParameter = new GetAllTopItemsResponse();
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

	public static final String CoreMethodGetItemRanks = "GetItemRanks";

	public void getItemRanks(GetItemRanksRequest input, final AsyncCallback<GetItemRanksResponse> output) {
		try {
			sendRequest(CoreMethodGetItemRanks, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					GetItemRanksResponse outputParameter = new GetItemRanksResponse();
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

	public static final String CoreMethodRegisterUser = "RegisterUser";

	public void registerUser(RegisterUserRequest input, final AsyncCallback<RegisterUserResponse> output) {
		try {
			sendRequest(CoreMethodRegisterUser, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					RegisterUserResponse outputParameter = new RegisterUserResponse();
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

	public static final String CoreMethodLogin = "Login";

	public void login(LoginRequest input, final AsyncCallback<LoginResponse> output) {
		try {
			sendRequest(CoreMethodLogin, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					LoginResponse outputParameter = new LoginResponse();
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

	public static final String CoreMethodLogout = "Logout";

	public void logout(LogoutRequest input, final AsyncCallback<LogoutResponse> output) {
		try {
			sendRequest(CoreMethodLogout, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					LogoutResponse outputParameter = new LogoutResponse();
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

	public static final String CoreMethodChangePassword = "ChangePassword";

	public void changePassword(ChangePasswordRequest input, final AsyncCallback<ChangePasswordResponse> output) {
		try {
			sendRequest(CoreMethodChangePassword, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					ChangePasswordResponse outputParameter = new ChangePasswordResponse();
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

	public static final String CoreMethodChangeUserDetails = "ChangeUserDetails";

	public void changeUserDetails(ChangeUserDetailsRequest input, final AsyncCallback<ChangeUserDetailsResponse> output) {
		try {
			sendRequest(CoreMethodChangeUserDetails, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					ChangeUserDetailsResponse outputParameter = new ChangeUserDetailsResponse();
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

	public static final String CoreMethodCheckUsername = "CheckUsername";

	public void checkUsername(CheckUsernameRequest input, final AsyncCallback<CheckUsernameResponse> output) {
		try {
			sendRequest(CoreMethodCheckUsername, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					CheckUsernameResponse outputParameter = new CheckUsernameResponse();
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

	public static final String CoreMethodGetRolesAndPermissions = "GetRolesAndPermissions";

	public void getRolesAndPermissions(GetRolesAndPermissionsRequest input, final AsyncCallback<GetRolesAndPermissionsResponse> output) {
		try {
			sendRequest(CoreMethodGetRolesAndPermissions, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					GetRolesAndPermissionsResponse outputParameter = new GetRolesAndPermissionsResponse();
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

	public static final String CoreMethodGetLinkedAccounts = "GetLinkedAccounts";

	public void getLinkedAccounts(GetLinkedAccountsRequest input, final AsyncCallback<GetLinkedAccountsResponse> output) {
		try {
			sendRequest(CoreMethodGetLinkedAccounts, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					GetLinkedAccountsResponse outputParameter = new GetLinkedAccountsResponse();
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

	public static final String CoreMethodGetLinkedAccountItems = "GetLinkedAccountItems";

	public void getLinkedAccountItems(GetLinkedAccountItemsRequest input, final AsyncCallback<GetLinkedAccountItemsResponse> output) {
		try {
			sendRequest(CoreMethodGetLinkedAccountItems, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					GetLinkedAccountItemsResponse outputParameter = new GetLinkedAccountItemsResponse();
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

	public static final String CoreMethodLinkAccount = "LinkAccount";

	public void linkAccount(LinkAccountRequest input, final AsyncCallback<LinkAccountResponse> output) {
		try {
			sendRequest(CoreMethodLinkAccount, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					LinkAccountResponse outputParameter = new LinkAccountResponse();
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

	public static final String CoreMethodIsAuthorised = "IsAuthorised";

	public void isAuthorised(IsAuthorisedRequest input, final AsyncCallback<IsAuthorisedResponse> output) {
		try {
			sendRequest(CoreMethodIsAuthorised, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					IsAuthorisedResponse outputParameter = new IsAuthorisedResponse();
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

	public static final String CoreMethodSearchForItem = "SearchForItem";

	public void searchForItem(SearchForItemRequest input, final AsyncCallback<SearchForItemResponse> output) {
		try {
			sendRequest(CoreMethodSearchForItem, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					SearchForItemResponse outputParameter = new SearchForItemResponse();
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

	public static final String CoreMethodForgotPassword = "ForgotPassword";

	public void forgotPassword(ForgotPasswordRequest input, final AsyncCallback<ForgotPasswordResponse> output) {
		try {
			sendRequest(CoreMethodForgotPassword, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					ForgotPasswordResponse outputParameter = new ForgotPasswordResponse();
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

	public static final String CoreMethodGetUserDetails = "GetUserDetails";

	public void getUserDetails(GetUserDetailsRequest input, final AsyncCallback<GetUserDetailsResponse> output) {
		try {
			sendRequest(CoreMethodGetUserDetails, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					GetUserDetailsResponse outputParameter = new GetUserDetailsResponse();
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

	public static final String CoreMethodGetCategories = "GetCategories";

	public void getCategories(GetCategoriesRequest input, final AsyncCallback<GetCategoriesResponse> output) {
		try {
			sendRequest(CoreMethodGetCategories, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					GetCategoriesResponse outputParameter = new GetCategoriesResponse();
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