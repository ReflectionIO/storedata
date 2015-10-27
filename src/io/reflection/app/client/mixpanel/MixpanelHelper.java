//
//  MixpanelApiHelper.java
//  storedata
//
//  Created by William Shakour (billy1380) on 28 Nov 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.mixpanel;

import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.helper.JavaScriptObjectHelper;
import io.reflection.app.client.page.PageType;
import io.reflection.app.datatypes.shared.Permission;
import io.reflection.app.datatypes.shared.Role;
import io.reflection.app.datatypes.shared.User;
import io.reflection.app.shared.util.DataTypeHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Window;

/**
 * 
 * @author Stefano Capuzzi (capuzzistefano)
 *
 */
public class MixpanelHelper {

	public enum Event {
		SIGNUP_SUCCESS("Signup Success"),
		SIGNUP_FAILURE("Signup Failure"),
		LOGIN("Login"),
		LOGOUT("Logout"),
		NAVIGATION("Navigation"),
		GO_TO_SIGNUP("Go To Signup Page"), // source_clicked
		OPEN_LINK_ACCOUNT_POPUP("Open Link Account Popup"), // source_clicked
		GO_TO_LINK_ACCOUNT_PROCESS("Go To Link Account Process Page"), // TODO tracked only when coming from registration process
		LINK_ACCOUNT_SUCCESS("Link Account Success"),
		LINK_ACCOUNT_FAILURE("Link Account Failure"); // TODO delete linked account

		private final String event;

		private Event(final String event) {
			this.event = event;
		}

		@Override
		public String toString() {
			return event;
		}
	}

	public static void init() {
		if (Window.Location.getHostName().equals("www.reflection.io")) {
			MixpanelApi.get().init("69afe8ba753ea33015dbd4cdbf11d1c8"); // Live
		} else if (Window.Location.getHostName().equals("dev.reflection.io")) {
			MixpanelApi.get().init("de7297c03772ca384bba5483b63f5e45"); // Dev
		} else { // Window.Location.getHostName().equals("127.0.0.1")
			MixpanelApi.get().init("400e244ec1aab9ad548fe51024506310"); // Debug
		}
	}

	/**
	 * @param stack
	 */
	public static void trackNavigation(Stack stack) {
		trackNavigation(stack, null);
	}

	public static void trackNavigation(Stack stack, Map<String, ?> mixpanelProp) {
		Map<String, Object> properties = new HashMap<String, Object>();
		if (mixpanelProp != null) {
			properties.putAll(mixpanelProp);
		}
		PageType stackPage = PageType.fromString(stack.getPage());
		if ("".equals(stack.getPage())) {
			stackPage = PageType.HomePageType;
		} else if (stackPage == null || PageType.Error404PageType.equals(stackPage)) {
			stackPage = PageType.Error404PageType;
		} else if (!stackPage.isNavigable()) {
			stackPage = PageType.HomePageType;
		} else if (PageType.UsersPageType == stackPage) {
			if (stack.hasAction()) {
				stackPage = PageType.fromString(stack.getAction());
			}
		}

		trackNavigation(stackPage.toString(), mixpanelProp);
	}

	public static void trackNavigation(String page, Map<String, ?> mixpanelProp) {
		Map<String, Object> properties = new HashMap<String, Object>();
		if (mixpanelProp != null) {
			properties.putAll(mixpanelProp);
		}
		properties.put("page", page);
		track(Event.NAVIGATION, properties);
	}

	/**
	 * Track signed up user - be sure this is called only once per user
	 * 
	 * @param signedUpUser
	 */
	public static void trackSignUpSuccess(User signedUpUser) {
		MixpanelApi.get().alias(signedUpUser.username); // Bond previously anonymous user

		JavaScriptObject peopleSetProp = JavaScriptObject.createObject();
		JavaScriptObjectHelper.setStringProperty(peopleSetProp, "$username", signedUpUser.username);
		JavaScriptObjectHelper.setStringProperty(peopleSetProp, "$email", signedUpUser.username);
		JavaScriptObjectHelper.setStringProperty(peopleSetProp, "$first_name", signedUpUser.forename);
		JavaScriptObjectHelper.setStringProperty(peopleSetProp, "$last_name", signedUpUser.surname);
		JavaScriptObjectHelper.setDateProperty(peopleSetProp, "$created", signedUpUser.created);
		JavaScriptObjectHelper.setStringProperty(peopleSetProp, "Company", signedUpUser.company);
		MixpanelApi.get().peopleSet(peopleSetProp);

		// TODO on change details call alias to update the email as id
		track(Event.SIGNUP_SUCCESS);
	}

	public static void trackLogin() {
		User user;
		if ((user = SessionController.get().getLoggedInUser()) != null) {
			MixpanelApi.get().identify(user.username); // Assign or identify user with a unique ID
			// Remember user data after the login (and the logout)
			JavaScriptObject registerProp = JavaScriptObject.createObject();
			JavaScriptObjectHelper.setStringProperty(registerProp, "username", user.username);
			JavaScriptObjectHelper.setStringProperty(registerProp, "first_name", user.forename);
			JavaScriptObjectHelper.setStringProperty(registerProp, "last_name", user.surname);
			JavaScriptObjectHelper.setStringProperty(registerProp, "company", user.company);
			JavaScriptObjectHelper.setBooleanProperty(registerProp, "loggedin", true);
			MixpanelApi.get().register(registerProp);

			// TODO set properties on login to store already registered users
			JavaScriptObject peopleSetProp = JavaScriptObject.createObject();
			JavaScriptObjectHelper.setStringProperty(peopleSetProp, "$username", user.username);
			JavaScriptObjectHelper.setStringProperty(peopleSetProp, "$email", user.username);
			JavaScriptObjectHelper.setStringProperty(peopleSetProp, "$first_name", user.forename);
			JavaScriptObjectHelper.setStringProperty(peopleSetProp, "$last_name", user.surname);
			JavaScriptObjectHelper.setDateProperty(peopleSetProp, "$created", user.created);
			JavaScriptObjectHelper.setStringProperty(peopleSetProp, "Company", user.company);
			MixpanelApi.get().peopleSet(peopleSetProp);
			track(Event.LOGIN);
		}
	}

	public static void registerRoleAndPermissions(Role role, List<Permission> permissions) {
		JavaScriptObject registerProp = JavaScriptObject.createObject();
		JavaScriptObjectHelper.setStringProperty(registerProp, "role", role.code);
		boolean hasLinkedAccount = false; // For now just register the Has Linked Account permission
		if (permissions != null) {
			for (Permission p : permissions) {
				if (DataTypeHelper.PERMISSION_HAS_LINKED_ACCOUNT_CODE.equals(p.code)) {
					hasLinkedAccount = true;
				}
			}
		}
		JavaScriptObjectHelper.setBooleanProperty(registerProp, "hasLinkedAccount", hasLinkedAccount);
		MixpanelApi.get().register(registerProp);
	}

	public static void trackLogout() {
		track(Event.LOGOUT); // Tracked to registered user
		resetRegisteredUser(); // Switch to anonymous user
	}

	public static void track(Event eventName, Map<String, ?> mixpanelProp) {
		MixpanelApi.get().track(eventName.toString(), JavaScriptObjectHelper.getAsJSObject(mixpanelProp));
	}

	public static void track(Event eventName) {
		MixpanelApi.get().track(eventName.toString());
	}

	public static void trackClicked(Event eventName, String clickedSource) {
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("source_clicked", clickedSource);
		track(eventName, properties);
	}

	private static void resetRegisteredUser() {
		MixpanelApi.get().unregister("username");
		MixpanelApi.get().unregister("first_name");
		MixpanelApi.get().unregister("last_name");
		MixpanelApi.get().unregister("company");
		MixpanelApi.get().unregister("loggedin");
		MixpanelApi.get().unregister("role");
		MixpanelApi.get().unregister("hasLinkedAccount");
		MixpanelApi.get().resetUserIdentity();
	}

}
