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
import io.reflection.app.api.core.shared.call.DeleteLinkedAccountRequest;
import io.reflection.app.api.core.shared.call.DeleteLinkedAccountResponse;
import io.reflection.app.api.core.shared.call.DeleteNotificationsRequest;
import io.reflection.app.api.core.shared.call.DeleteNotificationsResponse;
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
import io.reflection.app.api.core.shared.call.GetItemSalesRanksRequest;
import io.reflection.app.api.core.shared.call.GetItemSalesRanksResponse;
import io.reflection.app.api.core.shared.call.GetItemSalesRequest;
import io.reflection.app.api.core.shared.call.GetItemSalesResponse;
import io.reflection.app.api.core.shared.call.GetLinkedAccountItemRequest;
import io.reflection.app.api.core.shared.call.GetLinkedAccountItemResponse;
import io.reflection.app.api.core.shared.call.GetLinkedAccountItemsRequest;
import io.reflection.app.api.core.shared.call.GetLinkedAccountItemsResponse;
import io.reflection.app.api.core.shared.call.GetLinkedAccountsRequest;
import io.reflection.app.api.core.shared.call.GetLinkedAccountsResponse;
import io.reflection.app.api.core.shared.call.GetNotificationsRequest;
import io.reflection.app.api.core.shared.call.GetNotificationsResponse;
import io.reflection.app.api.core.shared.call.GetRolesAndPermissionsRequest;
import io.reflection.app.api.core.shared.call.GetRolesAndPermissionsResponse;
import io.reflection.app.api.core.shared.call.GetSalesRanksRequest;
import io.reflection.app.api.core.shared.call.GetSalesRanksResponse;
import io.reflection.app.api.core.shared.call.GetSalesRequest;
import io.reflection.app.api.core.shared.call.GetSalesResponse;
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
import io.reflection.app.api.core.shared.call.UpdateLinkedAccountRequest;
import io.reflection.app.api.core.shared.call.UpdateLinkedAccountResponse;
import io.reflection.app.api.core.shared.call.UpdateNotificationsRequest;
import io.reflection.app.api.core.shared.call.UpdateNotificationsResponse;
import io.reflection.app.client.helper.ApiCallHelper;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.willshex.gson.json.service.client.HttpException;
import com.willshex.gson.json.service.client.JsonService;

public final class CoreService extends JsonService {
	public static final String CoreMethodGetCountries = "GetCountries";

	public Request getCountries(final GetCountriesRequest input, final AsyncCallback<GetCountriesResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(CoreMethodGetCountries, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					try {
						GetCountriesResponse outputParameter = new GetCountriesResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(CoreService.this, CoreMethodGetCountries, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(CoreService.this, CoreMethodGetCountries, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodGetCountries, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodGetCountries, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(CoreService.this, CoreMethodGetCountries, input, exception);
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
					try {
						GetStoresResponse outputParameter = new GetStoresResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(CoreService.this, CoreMethodGetStores, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(CoreService.this, CoreMethodGetStores, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodGetStores, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodGetStores, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(CoreService.this, CoreMethodGetStores, input, exception);
		}
		return handle;
	}

	public static final String CoreMethodGetTopItems = "GetTopItems";

	public Request getTopItems(final GetTopItemsRequest input, final AsyncCallback<GetTopItemsResponse> output) {
		Request handle = null;
		try {
			input.on = ApiCallHelper.getUTCDate(input.on);
			handle = sendRequest(CoreMethodGetTopItems, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					try {
						GetTopItemsResponse outputParameter = new GetTopItemsResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(CoreService.this, CoreMethodGetTopItems, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(CoreService.this, CoreMethodGetTopItems, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodGetTopItems, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodGetTopItems, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(CoreService.this, CoreMethodGetTopItems, input, exception);
		}
		return handle;
	}

	public static final String CoreMethodGetAllTopItems = "GetAllTopItems";

	public Request getAllTopItems(final GetAllTopItemsRequest input, final AsyncCallback<GetAllTopItemsResponse> output) {
		Request handle = null;
		try {
			input.on = ApiCallHelper.getUTCDate(input.on);
			handle = sendRequest(CoreMethodGetAllTopItems, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					try {
						GetAllTopItemsResponse outputParameter = new GetAllTopItemsResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(CoreService.this, CoreMethodGetAllTopItems, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(CoreService.this, CoreMethodGetAllTopItems, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodGetAllTopItems, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodGetAllTopItems, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(CoreService.this, CoreMethodGetAllTopItems, input, exception);
		}
		return handle;
	}

	public static final String CoreMethodGetItemRanks = "GetItemRanks";

	public Request getItemRanks(final GetItemRanksRequest input, final AsyncCallback<GetItemRanksResponse> output) {
		Request handle = null;
		try {
			input.start = ApiCallHelper.getUTCDate(input.start);
			input.end = ApiCallHelper.getUTCDate(input.end);

			handle = sendRequest(CoreMethodGetItemRanks, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					try {
						GetItemRanksResponse outputParameter = new GetItemRanksResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(CoreService.this, CoreMethodGetItemRanks, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(CoreService.this, CoreMethodGetItemRanks, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodGetItemRanks, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodGetItemRanks, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(CoreService.this, CoreMethodGetItemRanks, input, exception);
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
					try {
						RegisterUserResponse outputParameter = new RegisterUserResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(CoreService.this, CoreMethodRegisterUser, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(CoreService.this, CoreMethodRegisterUser, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodRegisterUser, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodRegisterUser, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(CoreService.this, CoreMethodRegisterUser, input, exception);
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
					try {
						LoginResponse outputParameter = new LoginResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(CoreService.this, CoreMethodLogin, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(CoreService.this, CoreMethodLogin, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodLogin, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodLogin, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(CoreService.this, CoreMethodLogin, input, exception);
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
					try {
						LogoutResponse outputParameter = new LogoutResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(CoreService.this, CoreMethodLogout, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(CoreService.this, CoreMethodLogout, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodLogout, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodLogout, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(CoreService.this, CoreMethodLogout, input, exception);
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
					try {
						ChangePasswordResponse outputParameter = new ChangePasswordResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(CoreService.this, CoreMethodChangePassword, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(CoreService.this, CoreMethodChangePassword, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodChangePassword, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodChangePassword, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(CoreService.this, CoreMethodChangePassword, input, exception);
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
					try {
						ChangeUserDetailsResponse outputParameter = new ChangeUserDetailsResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(CoreService.this, CoreMethodChangeUserDetails, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(CoreService.this, CoreMethodChangeUserDetails, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodChangeUserDetails, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodChangeUserDetails, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(CoreService.this, CoreMethodChangeUserDetails, input, exception);
		}
		return handle;
	}

	public static final String CoreMethodUpdateLinkedAccount = "UpdateLinkedAccount";

	public Request updateLinkedAccount(final UpdateLinkedAccountRequest input, final AsyncCallback<UpdateLinkedAccountResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(CoreMethodUpdateLinkedAccount, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					try {
						UpdateLinkedAccountResponse outputParameter = new UpdateLinkedAccountResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(CoreService.this, CoreMethodUpdateLinkedAccount, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(CoreService.this, CoreMethodUpdateLinkedAccount, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodUpdateLinkedAccount, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodUpdateLinkedAccount, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(CoreService.this, CoreMethodUpdateLinkedAccount, input, exception);
		}
		return handle;
	}

	public static final String CoreMethodDeleteLinkedAccount = "DeleteLinkedAccount";

	public Request deleteLinkedAccount(final DeleteLinkedAccountRequest input, final AsyncCallback<DeleteLinkedAccountResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(CoreMethodDeleteLinkedAccount, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					try {
						DeleteLinkedAccountResponse outputParameter = new DeleteLinkedAccountResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(CoreService.this, CoreMethodDeleteLinkedAccount, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(CoreService.this, CoreMethodDeleteLinkedAccount, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodDeleteLinkedAccount, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodDeleteLinkedAccount, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(CoreService.this, CoreMethodDeleteLinkedAccount, input, exception);
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
					try {
						CheckUsernameResponse outputParameter = new CheckUsernameResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(CoreService.this, CoreMethodCheckUsername, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(CoreService.this, CoreMethodCheckUsername, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodCheckUsername, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodCheckUsername, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(CoreService.this, CoreMethodCheckUsername, input, exception);
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
					try {
						GetRolesAndPermissionsResponse outputParameter = new GetRolesAndPermissionsResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(CoreService.this, CoreMethodGetRolesAndPermissions, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(CoreService.this, CoreMethodGetRolesAndPermissions, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodGetRolesAndPermissions, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodGetRolesAndPermissions, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(CoreService.this, CoreMethodGetRolesAndPermissions, input, exception);
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
					try {
						GetLinkedAccountsResponse outputParameter = new GetLinkedAccountsResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(CoreService.this, CoreMethodGetLinkedAccounts, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(CoreService.this, CoreMethodGetLinkedAccounts, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodGetLinkedAccounts, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodGetLinkedAccounts, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(CoreService.this, CoreMethodGetLinkedAccounts, input, exception);
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
					try {
						GetLinkedAccountItemsResponse outputParameter = new GetLinkedAccountItemsResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(CoreService.this, CoreMethodGetLinkedAccountItems, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(CoreService.this, CoreMethodGetLinkedAccountItems, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodGetLinkedAccountItems, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodGetLinkedAccountItems, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(CoreService.this, CoreMethodGetLinkedAccountItems, input, exception);
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
					try {
						LinkAccountResponse outputParameter = new LinkAccountResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(CoreService.this, CoreMethodLinkAccount, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(CoreService.this, CoreMethodLinkAccount, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodLinkAccount, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodLinkAccount, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(CoreService.this, CoreMethodLinkAccount, input, exception);
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
					try {
						IsAuthorisedResponse outputParameter = new IsAuthorisedResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(CoreService.this, CoreMethodIsAuthorised, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(CoreService.this, CoreMethodIsAuthorised, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodIsAuthorised, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodIsAuthorised, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(CoreService.this, CoreMethodIsAuthorised, input, exception);
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
					try {
						SearchForItemResponse outputParameter = new SearchForItemResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(CoreService.this, CoreMethodSearchForItem, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(CoreService.this, CoreMethodSearchForItem, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodSearchForItem, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodSearchForItem, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(CoreService.this, CoreMethodSearchForItem, input, exception);
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
					try {
						ForgotPasswordResponse outputParameter = new ForgotPasswordResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(CoreService.this, CoreMethodForgotPassword, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(CoreService.this, CoreMethodForgotPassword, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodForgotPassword, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodForgotPassword, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(CoreService.this, CoreMethodForgotPassword, input, exception);
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
					try {
						GetUserDetailsResponse outputParameter = new GetUserDetailsResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(CoreService.this, CoreMethodGetUserDetails, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(CoreService.this, CoreMethodGetUserDetails, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodGetUserDetails, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodGetUserDetails, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(CoreService.this, CoreMethodGetUserDetails, input, exception);
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
					try {
						GetCategoriesResponse outputParameter = new GetCategoriesResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(CoreService.this, CoreMethodGetCategories, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(CoreService.this, CoreMethodGetCategories, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodGetCategories, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodGetCategories, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(CoreService.this, CoreMethodGetCategories, input, exception);
		}
		return handle;
	}

	public static final String CoreMethodGetSales = "GetSales";

	public Request getSales(final GetSalesRequest input, final AsyncCallback<GetSalesResponse> output) {
		Request handle = null;
		try {
			input.start = ApiCallHelper.getUTCDate(input.start);
			input.end = ApiCallHelper.getUTCDate(input.end);

			handle = sendRequest(CoreMethodGetSales, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					try {
						GetSalesResponse outputParameter = new GetSalesResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(CoreService.this, CoreMethodGetSales, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(CoreService.this, CoreMethodGetSales, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodGetSales, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodGetSales, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(CoreService.this, CoreMethodGetSales, input, exception);
		}
		return handle;
	}

	public static final String CoreMethodGetItemSales = "GetItemSales";

	public Request getItemSales(final GetItemSalesRequest input, final AsyncCallback<GetItemSalesResponse> output) {
		Request handle = null;
		try {
			input.start = ApiCallHelper.getUTCDate(input.start);
			input.end = ApiCallHelper.getUTCDate(input.end);

			handle = sendRequest(CoreMethodGetItemSales, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					try {
						GetItemSalesResponse outputParameter = new GetItemSalesResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(CoreService.this, CoreMethodGetItemSales, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(CoreService.this, CoreMethodGetItemSales, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodGetItemSales, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodGetItemSales, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(CoreService.this, CoreMethodGetItemSales, input, exception);
		}
		return handle;
	}

	public static final String CoreMethodGetSalesRanks = "GetSalesRanks";

	public Request getSalesRanks(final GetSalesRanksRequest input, final AsyncCallback<GetSalesRanksResponse> output) {
		Request handle = null;
		try {
			input.start = ApiCallHelper.getUTCDate(input.start);
			input.end = ApiCallHelper.getUTCDate(input.end);

			handle = sendRequest(CoreMethodGetSalesRanks, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					try {
						GetSalesRanksResponse outputParameter = new GetSalesRanksResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(CoreService.this, CoreMethodGetSalesRanks, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(CoreService.this, CoreMethodGetSalesRanks, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodGetSalesRanks, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodGetSalesRanks, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(CoreService.this, CoreMethodGetSalesRanks, input, exception);
		}
		return handle;
	}

	public static final String CoreMethodGetItemSalesRanks = "GetItemSalesRanks";

	public Request getItemSalesRanks(final GetItemSalesRanksRequest input, final AsyncCallback<GetItemSalesRanksResponse> output) {
		Request handle = null;
		try {
			input.start = ApiCallHelper.getUTCDate(input.start);
			input.end = ApiCallHelper.getUTCDate(input.end);

			handle = sendRequest(CoreMethodGetItemSalesRanks, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					try {
						GetItemSalesRanksResponse outputParameter = new GetItemSalesRanksResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(CoreService.this, CoreMethodGetItemSalesRanks, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(CoreService.this, CoreMethodGetItemSalesRanks, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodGetItemSalesRanks, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodGetItemSalesRanks, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(CoreService.this, CoreMethodGetItemSalesRanks, input, exception);
		}
		return handle;
	}

	public static final String CoreMethodGetLinkedAccountItem = "GetLinkedAccountItem";

	public Request getLinkedAccountItem(final GetLinkedAccountItemRequest input, final AsyncCallback<GetLinkedAccountItemResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(CoreMethodGetLinkedAccountItem, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					try {
						GetLinkedAccountItemResponse outputParameter = new GetLinkedAccountItemResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(CoreService.this, CoreMethodGetLinkedAccountItem, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(CoreService.this, CoreMethodGetLinkedAccountItem, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodGetLinkedAccountItem, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodGetLinkedAccountItem, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(CoreService.this, CoreMethodGetLinkedAccountItem, input, exception);
		}
		return handle;
	}

	public static final String CoreMethodGetNotifications = "GetNotifications";

	public Request getNotifications(final GetNotificationsRequest input, final AsyncCallback<GetNotificationsResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(CoreMethodGetNotifications, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					try {
						GetNotificationsResponse outputParameter = new GetNotificationsResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(CoreService.this, CoreMethodGetNotifications, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(CoreService.this, CoreMethodGetNotifications, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodGetNotifications, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodGetNotifications, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(CoreService.this, CoreMethodGetNotifications, input, exception);
		}
		return handle;
	}

	public static final String CoreMethodDeleteNotifications = "DeleteNotifications";

	public Request deleteNotifications(final DeleteNotificationsRequest input, final AsyncCallback<DeleteNotificationsResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(CoreMethodDeleteNotifications, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					try {
						DeleteNotificationsResponse outputParameter = new DeleteNotificationsResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(CoreService.this, CoreMethodDeleteNotifications, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(CoreService.this, CoreMethodDeleteNotifications, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodDeleteNotifications, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodDeleteNotifications, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(CoreService.this, CoreMethodDeleteNotifications, input, exception);
		}
		return handle;
	}

	public static final String CoreMethodUpdateNotifications = "UpdateNotifications";

	public Request updateNotifications(final UpdateNotificationsRequest input, final AsyncCallback<UpdateNotificationsResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(CoreMethodUpdateNotifications, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					try {
						UpdateNotificationsResponse outputParameter = new UpdateNotificationsResponse();
						parseResponse(response, outputParameter);
						output.onSuccess(outputParameter);
						onCallSuccess(CoreService.this, CoreMethodUpdateNotifications, input, outputParameter);
					} catch (JSONException | HttpException exception) {
						output.onFailure(exception);
						onCallFailure(CoreService.this, CoreMethodUpdateNotifications, input, exception);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(CoreService.this, CoreMethodUpdateNotifications, input, exception);
				}
			});
			onCallStart(CoreService.this, CoreMethodUpdateNotifications, input, handle);
		} catch (RequestException exception) {
			output.onFailure(exception);
			onCallFailure(CoreService.this, CoreMethodUpdateNotifications, input, exception);
		}
		return handle;
	}
}