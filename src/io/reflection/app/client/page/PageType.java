//
//  PageType.java
//  storedata
//
//  Created by William Shakour (billy1380) on 19 Feb 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import io.reflection.app.client.page.admin.EmailTemplatePage;
import io.reflection.app.client.page.admin.FeedBrowserPage;
import io.reflection.app.client.page.admin.ItemsPage;
import io.reflection.app.client.page.admin.PermissionsPage;
import io.reflection.app.client.page.admin.RolesPage;
import io.reflection.app.client.page.admin.UsersPage;
import io.reflection.app.client.page.blog.AdminPage;
import io.reflection.app.client.page.blog.EditPostPage;
import io.reflection.app.client.page.blog.PostPage;
import io.reflection.app.client.page.blog.PostsPage;
import io.reflection.app.datatypes.shared.Permission;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.History;
import com.spacehopperstudios.utility.StringUtils;

/**
 * @author billy1380
 * 
 */
public enum PageType {
	// navigable
	RanksPageType("ranks", true),
	FeedBrowserPageType("feedbrowser", true, "MFB"),
	UsersPageType("users", true, "MUS"),
	LoginPageType("login", false),
	RegisterPageType("register", false),
	ChangePasswordPageType("changepassword", true),
	RolesPageType("roles", true, "MRL"),
	PermissionsPageType("permissions", true, "MPR"),
	ChangeDetailsPageType("changedetails", true),
	UpgradePageType("upgrade", false),
	LinkedAccountsPageType("linkedaccounts", true),
	SearchPageType("search", true),
	ItemPageType("item", true),
	HomePageType("home", false),
	LinkItunesPageType("linkitunes", true),
	ReadyToStartPageType("readytostart", true),
	MyAppsPageType("myapps", true),
	EmailTemplatesPageType("emailtemplates", true, "MET"),
	ForgotPasswordPageType("forgotpassword", false),
	ResetPasswordPageType("resetpassword", false),
	ItemsPageType("items", true, "MIT"),
	PolicyPageType("policy", false),
	TermsPageType("terms", false),
	BlogAdminPageType("blogadmin", true, "MBL"),
	BlogPostsPageType("blog", false),
	BlogPostPageType("blogpost", false),
	BlogEditPostPageType("blogedit", true, "BLE", "BLU"),

	// Non navigable
	LoadingPageType("loading", false), ;

	private String value;
	private static Map<String, PageType> valueLookup = null;
	private HomePage defaultPage = null;
	private Map<String, Permission> requiredPermissions;
	private boolean navigable;
	private boolean requiresAuthentication;

	private PageType(String value, boolean showable) {
		this.value = value;
		this.navigable = showable;
		requiredPermissions = null;
	}

	private PageType(String value, boolean requiresAuthentication, String... requiredPermissionCode) {
		this.value = value;
		this.navigable = true;
		this.requiresAuthentication = requiresAuthentication;

		if (requiredPermissionCode != null && requiredPermissionCode.length > 0) {
			requiredPermissions = new HashMap<String, Permission>();

			Permission p;
			for (String code : requiredPermissionCode) {
				p = new Permission();
				p.code = code;
				requiredPermissions.put(code, p);
			}
		}
	}

	public String toString() {
		return value;
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

	public boolean requiresLogin() {
		return requiresAuthentication;
	}

	public Collection<Permission> getRequiredPermissions() {
		return requiredPermissions == null ? null : requiredPermissions.values();
	}

	public Collection<String> getRequiredPermissionCodes() {
		return requiredPermissions == null ? null : requiredPermissions.keySet();
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
		case MyAppsPageType:
			page = new MyAppsPage();
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
		case TermsPageType:
			page = new TermsPage();
			break;
		case PolicyPageType:
			page = new PolicyPage();
			break;
		case BlogAdminPageType:
			page = new AdminPage();
			break;
		case BlogEditPostPageType:
			page = new EditPostPage();
			break;
		case BlogPostPageType:
			page = new PostPage();
			break;
		case BlogPostsPageType:
			page = new PostsPage();
			break;
		case LoadingPageType:
			page = new LoadingPage();
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
		if (!navigable) throw showableError();

		History.newItem(toString());
	}

	public void show(String... params) {
		if (!navigable) throw showableError();

		String joinedParams = StringUtils.join(Arrays.asList(params), "/");
		History.newItem(toString() + "/" + joinedParams);
	}

	private RuntimeException showableError() {
		return new RuntimeException("Cannot show/redirect to page [" + toString() + "] because it is not showable. Should be added directly to DOM");
	}

}