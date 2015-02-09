//
//  PageType.java
//  storedata
//
//  Created by William Shakour (billy1380) on 19 Feb 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import io.reflection.app.client.page.admin.CategoriesPage;
import io.reflection.app.client.page.admin.DataAccountFetchesPage;
import io.reflection.app.client.page.admin.DataAccountsPage;
import io.reflection.app.client.page.admin.EventPage;
import io.reflection.app.client.page.admin.EventSubscriptionsPage;
import io.reflection.app.client.page.admin.FeedBrowserPage;
import io.reflection.app.client.page.admin.ItemsPage;
import io.reflection.app.client.page.admin.PermissionsPage;
import io.reflection.app.client.page.admin.RolesPage;
import io.reflection.app.client.page.admin.SendNotificationPage;
import io.reflection.app.client.page.admin.SimpleModelRunsPage;
import io.reflection.app.client.page.admin.UsersPage;
import io.reflection.app.client.page.blog.EditPostPage;
import io.reflection.app.client.page.blog.PostAdminPage;
import io.reflection.app.client.page.blog.PostPage;
import io.reflection.app.client.page.blog.PostsPage;
import io.reflection.app.client.page.forum.AddTopicPage;
import io.reflection.app.client.page.forum.EditTopicPage;
import io.reflection.app.client.page.forum.ForumPage;
import io.reflection.app.client.page.forum.TopicPage;
import io.reflection.app.client.page.test.WidgetTestPage;
import io.reflection.app.datatypes.shared.Permission;
import io.reflection.app.shared.util.DataTypeHelper;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.History;
import com.spacehopperstudios.utility.StringUtils;

/**
 * @author billy1380
 * 
 */
public enum PageType {
	// navigable
	BlogAdminPageType("blogadmin", "MBL"),
	BlogEditPostPageType("blogedit", "BLE", "BLU"),
	BlogPostsPageType("blog", false),
	BlogPostPageType("blogpost", false),
	BlogTagPageType("blogtag", false),
	CategoriesPageType("categories", "MCA"),
	ChangeDetailsPageType("changedetails", true),
	ChangePasswordPageType("changepassword", true),
	DataAccountFetchesPageType("dataaccountfetches", "MDF"),
	DataAccountsPageType("dataaccounts", "MDA"),
	EventsPageType("events", DataTypeHelper.PERMISSION_MANAGE_EVENTS_CODE),
	FeedBrowserPageType("feedbrowser", "MFF"),
	ForgotPasswordPageType("forgotpassword", false),
	ForumEditTopicPageType("forumtopicedit", false),
	ForumPageType("forum", false),
	ForumThreadPageType("forumthread", false),
	ForumTopicPageType("forumtopic", false),
	HomePageType("home", false),
	ItemPageType("item", true),
	ItemsPageType("items", "MIT"),
	LoginPageType("login", false),
	LinkedAccountsPageType("linkedaccounts", true),
	LinkItunesPageType("linkitunes", true),
	MyAppsPageType("myapps", true),
	NotPermittedPageType("notpermitted", false),
	PermissionsPageType("permissions", "MPR"),
	RanksPageType("ranks", "HLA"),
	ReadyToStartPageType("readytostart", true),
	RegisterPageType("register", false),
	ResetPasswordPageType("resetpassword", false),
	RolesPageType("roles", "MRL"),
	SearchPageType("search", true),
	SimpleModelRunPageType("simplemodelrun", "MSM"),
	TermsPageType("terms", false),
	UpgradePageType("upgrade", true),
	UsersPageType("users", DataTypeHelper.PERMISSION_MANAGE_USERS_CODE),
	WidgetTestPage("test", false),
	NotificationsPageType("notifications", true),
	SendNotificationPageType("sendnotification", DataTypeHelper.PERMISSION_SEND_NOTIFICATIONS_CODE),
	EventSubscriptionsPageType("eventsubscriptions", DataTypeHelper.PERMISSION_MANAGE_EVENT_SUBSCRIPTIONS_CODE),
	EditEventSubscriptionPageType("editeventsubscription", DataTypeHelper.PERMISSION_MANAGE_EVENT_SUBSCRIPTIONS_CODE),

	// Non navigable
	LoadingPageType("loading"), ;

	private String value;
	private static Map<String, PageType> valueLookup = null;
	private HomePage defaultPage = null;
	private Map<String, Permission> requiredPermissions;
	private boolean navigable;
	private boolean requiresAuthentication;

	private PageType(String value, boolean requiresAuthentication) {
		this.value = value;
		this.navigable = true;
		this.requiresAuthentication = requiresAuthentication;
	}

	private PageType(String value) {
		this.value = value;
		this.navigable = false;
		this.requiresAuthentication = false;
	}

	private PageType(String value, String... requiredPermissionCode) {
		this.value = value;
		this.navigable = true;
		this.requiresAuthentication = true;

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

	public String toString(String... params) {
		String asString;
		String joinedParams = StringUtils.join(Arrays.asList(params), "/");

		if (joinedParams == null || joinedParams.length() == 0) {
			asString = toString();
		} else {
			asString = toString() + "/" + joinedParams;
		}

		return asString;
	}

	public SafeUri asHref() {
		return UriUtils.fromString("#!" + toString());
	}

	public SafeUri asHref(String... params) {
		return UriUtils.fromString("#!" + toString(params));
	}

	public String asTargetHistoryToken() {
		return "!" + toString();
	}

	public String asTargetHistoryToken(String... params) {
		return "!" + toString(params);
	}

	public static PageType fromString(String value) {
		value = stripExclamation(value);

		if (valueLookup == null) {
			valueLookup = new HashMap<String, PageType>();

			for (PageType currentPageType : PageType.values()) {
				valueLookup.put(currentPageType.value, currentPageType);
			}
		}

		return value == null ? null : valueLookup.get(value);
	}

	public boolean requiresLogin() {
		return requiresAuthentication;
	}

	public boolean isNavigable() {
		return navigable;
	}

	public Collection<Permission> getRequiredPermissions() {
		return requiredPermissions == null ? null : requiredPermissions.values();
	}

	public Collection<String> getRequiredPermissionCodes() {
		return requiredPermissions == null ? null : requiredPermissions.keySet();
	}

	public boolean equals(String value) {
		value = stripExclamation(value);

		return this.value.equals(value);
	}

	/**
	 * @return
	 */
	public Page create() {
		Page page = null;

		switch (this) {
		case SimpleModelRunPageType:
			page = new SimpleModelRunsPage();
			break;
		case DataAccountsPageType:
			page = new DataAccountsPage();
			break;
		case DataAccountFetchesPageType:
			page = new DataAccountFetchesPage();
			break;
		case CategoriesPageType:
			page = new CategoriesPage();
			break;
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
		case EventsPageType:
			page = new EventPage();
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
		case BlogAdminPageType:
			page = new PostAdminPage();
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
		case NotPermittedPageType:
			page = new NotPermittedPage();
			break;
		case WidgetTestPage:
			page = new WidgetTestPage();
			break;
		case ForumPageType:
			page = new ForumPage();
			break;
		case ForumThreadPageType:
			page = new TopicPage();
			break;
		case ForumTopicPageType:
			page = new AddTopicPage();
			break;
		case ForumEditTopicPageType:
			page = new EditTopicPage();
			break;
		case NotificationsPageType:
			page = new NotificationsPage();
			break;
		case EventSubscriptionsPageType:
			page = new EventSubscriptionsPage();
			break;
		case SendNotificationPageType:
			page = new SendNotificationPage();
			break;
		case HomePageType:
		default:
			if (defaultPage == null) {
				defaultPage = new HomePage();
				defaultPage.setPageType(this);
			}
			page = defaultPage;
			break;
		}

		if (page != defaultPage) {
			page.setPageType(this);
		}

		return page;
	}

	public void show() {
		if (!navigable) throw navigableError();

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				History.newItem(asTargetHistoryToken());
			}
		});
	}

	public void show(final String... params) {
		if (!navigable) throw navigableError();

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				History.newItem(asTargetHistoryToken(params));
			}
		});
	}

	private RuntimeException navigableError() {
		return new RuntimeException("Cannot show/redirect to page [" + asTargetHistoryToken()
				+ "] because it is not navigable. Should be added directly to DOM");
	}

	private static String stripExclamation(String value) {
		if (value != null && value.length() > 0 && value.charAt(0) == '!') {
			value = value.substring(1);
		}

		return value;
	}

}