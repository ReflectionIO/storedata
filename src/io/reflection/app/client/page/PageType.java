//
//  PageType.java
//  storedata
//
//  Created by William Shakour (billy1380) on 19 Feb 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.History;
import com.spacehopperstudios.utility.StringUtils;

/**
 * @author billy1380
 * 
 */
public enum PageType {
	RanksPageType("ranks"),
	FeedBrowserPageType("feedbrowser"),
	UsersPageType("users"),
	LoginPageType("login"),
	RegisterPageType("register"),
	ChangePasswordPageType("changepassword"),
	RolesPageType("roles"),
	PermissionsPageType("permissions"),
	ChangeDetailsPageType("changedetails"),
	UpgradePageType("upgrade"),
	LinkedAccountsPageType("linkedaccounts"),
	SearchPageType("search"),
	ItemPageType("item"),
	HomePageType("home"),
	LinkItunesPageType("linkitunes"),
	ReadyToStartPageType("readytostart"),
	MyAppsOverviewPageType("myappsoverview"),
	EmailTemplatesPageType("emailtemplates"),
	ForgotPasswordPageType("forgotpassword"),
	ResetPasswordPageType("resetpassword"),
	ItemsPageType("items"), ;

	private String value;
	private static Map<String, PageType> valueLookup = null;
	private HomePage defaultPage = null;

	public String toString() {
		return value;
	}

	private PageType(String value) {
		this.value = value;
	}

	public static PageType fromString(String value) {
		if (valueLookup == null) {
			valueLookup = new HashMap<String, PageType>();

			for (PageType currentPageType : PageType.values()) {
				valueLookup.put(currentPageType.value, currentPageType);
			}
		}

		return valueLookup.get(value);
	}

	public boolean equals(String value) {
		return this.value.equals(value);
	}

	/**
	 * @return
	 */
	public Page create() {
		Page page = null;

		switch (this) {
		case RanksPageType:
			page = new RanksPage();
			break;
		case FeedBrowserPageType:
			page = new FeedBrowserPage();
			break;
		case UsersPageType:
			page = new UsersPage();
			break;
		case LoginPageType:
			page = new LoginPage();
			break;
		case RegisterPageType:
			page = new RegisterPage();
			break;
		case ChangePasswordPageType:
			page = new ChangePasswordPage();
			break;
		case RolesPageType:
			page = new RolesPage();
			break;
		case PermissionsPageType:
			page = new PermissionsPage();
			break;
		case ChangeDetailsPageType:
			page = new ChangeDetailsPage();
			break;
		case UpgradePageType:
			page = new UpgradePage();
			break;
		case LinkedAccountsPageType:
			page = new LinkedAccountsPage();
			break;
		case SearchPageType:
			page = new SearchPage();
			break;
		case ItemPageType:
			page = new ItemPage();
			break;
		case LinkItunesPageType:
			page = new LinkItunesPage();
			break;
		case ReadyToStartPageType:
			page = new ReadyToStartPage();
			break;
		case MyAppsOverviewPageType:
			page = new MyAppsOverviewPage();
			break;
		case EmailTemplatesPageType:
			page = new EmailTemplatePage();
			break;
		case ForgotPasswordPageType:
			page = new ForgotPasswordPage();
			break;
		case ResetPasswordPageType:
			page = new ResetPasswordPage();
			break;
		case ItemsPageType:
			page = new ItemsPage();
			break;
		case HomePageType:
		default:
			if (defaultPage == null) {
				defaultPage = new HomePage();
			}
			page = defaultPage;
			break;
		}

		return page;
	}

	public void show() {
		History.newItem(toString());
	}

	public void show(String... params) {
		String joinedParams = StringUtils.join(Arrays.asList(params), "/");

		History.newItem(toString() + "/" + joinedParams);
	}
}