//  
//  CoreJsonServlet.java
//  storedata
//
//  Created by William Shakour on 04 July 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.api.core;

import io.reflection.app.api.core.shared.call.ChangePasswordRequest;
import io.reflection.app.api.core.shared.call.ChangeUserDetailsRequest;
import io.reflection.app.api.core.shared.call.CheckUsernameRequest;
import io.reflection.app.api.core.shared.call.GetAllTopItemsRequest;
import io.reflection.app.api.core.shared.call.GetCountriesRequest;
import io.reflection.app.api.core.shared.call.GetItemRanksRequest;
import io.reflection.app.api.core.shared.call.GetLinkedAccountItemsRequest;
import io.reflection.app.api.core.shared.call.GetLinkedAccountsRequest;
import io.reflection.app.api.core.shared.call.GetRolesAndPermissionsRequest;
import io.reflection.app.api.core.shared.call.GetStoresRequest;
import io.reflection.app.api.core.shared.call.GetTopItemsRequest;
import io.reflection.app.api.core.shared.call.IsAuthorisedRequest;
import io.reflection.app.api.core.shared.call.LinkAccountRequest;
import io.reflection.app.api.core.shared.call.LoginRequest;
import io.reflection.app.api.core.shared.call.LogoutRequest;
import io.reflection.app.api.core.shared.call.RegisterUserRequest;

import com.google.gson.JsonObject;
import com.willshex.gson.json.service.server.JsonServlet;

@SuppressWarnings("serial")
public final class CoreJsonServlet extends JsonServlet {
	@Override
	protected String processAction(String action, JsonObject request) {
		String output = "null";
		Core service = new Core();

		if ("GetCountries".equals(action)) {
			GetCountriesRequest input = new GetCountriesRequest();
			input.fromJson(request);
			output = service.getCountries(input).toString();
		} else if ("GetStores".equals(action)) {
			GetStoresRequest input = new GetStoresRequest();
			input.fromJson(request);
			output = service.getStores(input).toString();
		} else if ("GetTopItems".equals(action)) {
			GetTopItemsRequest input = new GetTopItemsRequest();
			input.fromJson(request);
			output = service.getTopItems(input).toString();
		} else if ("GetAllTopItems".equals(action)) {
			GetAllTopItemsRequest input = new GetAllTopItemsRequest();
			input.fromJson(request);
			output = service.getAllTopItems(input).toString();
		} else if ("GetItemRanks".equals(action)) {
			GetItemRanksRequest input = new GetItemRanksRequest();
			input.fromJson(request);
			output = service.getItemRanks(input).toString();
		} else if ("RegisterUser".equals(action)) {
			RegisterUserRequest input = new RegisterUserRequest();
			input.fromJson(request);
			output = service.registerUser(input).toString();
		} else if ("Login".equals(action)) {
			LoginRequest input = new LoginRequest();
			input.fromJson(request);
			output = service.login(input).toString();
		} else if ("Logout".equals(action)) {
			LogoutRequest input = new LogoutRequest();
			input.fromJson(request);
			output = service.logout(input).toString();
		} else if ("ChangePassword".equals(action)) {
			ChangePasswordRequest input = new ChangePasswordRequest();
			input.fromJson(request);
			output = service.changePassword(input).toString();
		} else if ("ChangeUserDetails".equals(action)) {
			ChangeUserDetailsRequest input = new ChangeUserDetailsRequest();
			input.fromJson(request);
			output = service.changeUserDetails(input).toString();
		} else if ("CheckUsername".equals(action)) {
			CheckUsernameRequest input = new CheckUsernameRequest();
			input.fromJson(request);
			output = service.checkUsername(input).toString();
		} else if ("GetRolesAndPermissions".equals(action)) {
			GetRolesAndPermissionsRequest input = new GetRolesAndPermissionsRequest();
			input.fromJson(request);
			output = service.getRolesAndPermissions(input).toString();
		} else if ("GetLinkedAccounts".equals(action)) {
			GetLinkedAccountsRequest input = new GetLinkedAccountsRequest();
			input.fromJson(request);
			output = service.getLinkedAccounts(input).toString();
		} else if ("GetLinkedAccountItems".equals(action)) {
			GetLinkedAccountItemsRequest input = new GetLinkedAccountItemsRequest();
			input.fromJson(request);
			output = service.getLinkedAccountItems(input).toString();
		} else if ("LinkAccount".equals(action)) {
			LinkAccountRequest input = new LinkAccountRequest();
			input.fromJson(request);
			output = service.linkAccount(input).toString();
		} else if ("IsAuthorised".equals(action)) {
			IsAuthorisedRequest input = new IsAuthorisedRequest();
			input.fromJson(request);
			output = service.isAuthorised(input).toString();
		}
		return output;
	}
}