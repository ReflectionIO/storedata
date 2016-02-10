//
//  UserService.java
//  storedata
//
//  Created by William Shakour on October 8, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.service.user;

import static com.spacehopperstudios.utility.StringUtils.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.spacehopperstudios.utility.StringUtils;

import io.reflection.app.accountdatacollectors.ITunesConnectDownloadHelper;
import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.DataSource;
import io.reflection.app.datatypes.shared.Event;
import io.reflection.app.datatypes.shared.Notification;
import io.reflection.app.datatypes.shared.NotificationTypeType;
import io.reflection.app.datatypes.shared.Permission;
import io.reflection.app.datatypes.shared.Role;
import io.reflection.app.datatypes.shared.User;
import io.reflection.app.helpers.NotificationHelper;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;
import io.reflection.app.service.dataaccount.DataAccountServiceProvider;
import io.reflection.app.service.event.EventServiceProvider;
import io.reflection.app.service.notification.NotificationServiceProvider;
import io.reflection.app.shared.util.DataTypeHelper;

final class UserService implements IUserService {

	private static final String	SALT	= "salt.username.magic";

	private static final Logger	LOG		= Logger.getLogger(UserService.class.getName());

	@Override
	public String getName() {
		return ServiceType.ServiceTypeUser.toString();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.user.IUserService#getUser(java.lang.Long)
	 */
	@Override
	public User getUser(Long id) throws DataAccessException {
		User user = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection userConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeUser.toString());

		String getUserByIdQuery = String.format("SELECT * FROM `user` WHERE `deleted`='n' AND `id`=%d LIMIT 1", id.longValue());

		try {
			userConnection.connect();
			userConnection.executeQuery(getUserByIdQuery);

			if (userConnection.fetchNextRow()) {
				user = toUser(userConnection);
			}
		} finally {
			if (userConnection != null) {
				userConnection.disconnect();
			}
		}

		return user;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.user.IUserService#getRoleUserIds(io.reflection.app.datatypes.shared.Role)
	 */
	@Override
	public List<User> getRoleUsers(Role role) throws DataAccessException {
		List<User> users = new ArrayList<User>();

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection userConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeUser.toString());

		String getUserIdsQuery = String.format(
				"SELECT DISTINCT `userid` FROM `userrole` WHERE `roleid` = %d AND `deleted`='n' AND (`expires` > NOW() OR `expires` IS NULL)",
				role.id.longValue());

		try {
			userConnection.connect();
			userConnection.executeQuery(getUserIdsQuery);

			while (userConnection.fetchNextRow()) {
				Long userId = userConnection.getCurrentRowLong("userid");
				if (userId != null) {
					users.add(DataTypeHelper.createUser(userId));
				}
			}
		} finally {
			if (userConnection != null) {
				userConnection.disconnect();
			}
		}

		return users;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.user.IUserService#addUser(io.reflection.app.shared.datatypes.User)
	 */
	@Override
	public User addUser(User user) throws DataAccessException {
		User addedUser = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection userConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeUser.toString());

		// NOTE: salt is added then the password is sha1-ed
		String addUserQuery;
		if (user.password != null) {
			addUserQuery = String.format(
					"INSERT INTO `user` (`forename`, `surname`, `username`, `password`, `avatar`, `company`) VALUES ('%s', '%s', '%s', '%s', %s, '%s')",
					addslashes(user.forename), addslashes(user.surname), addslashes(user.username), sha1Hash(SALT + user.password), user.avatar == null ? "'"
							+ addslashes(StringUtils.md5Hash(user.username.trim().toLowerCase())) + "'" : "'" + addslashes(user.avatar) + "'",
					addslashes(user.company));
		} else {
			addUserQuery = String
					.format("INSERT INTO `user` (`forename`, `surname`, `username`, `avatar`, `company`) VALUES ('%s', '%s', '%s', %s, '%s')",
							addslashes(user.forename), addslashes(user.surname), addslashes(user.username),
							user.avatar == null ? "'" + addslashes(StringUtils.md5Hash(user.username.trim().toLowerCase())) + "'" : "'"
									+ addslashes(user.avatar) + "'",
							addslashes(user.company));
		}
		try {
			userConnection.connect();
			userConnection.executeQuery(addUserQuery);

			if (userConnection.getAffectedRowCount() > 0) {
				user.id = Long.valueOf(userConnection.getInsertedId());
				user.password = null;

				addedUser = getUser(user.id);
				addedUser.password = null;

			}
		} finally {

			if (userConnection != null) {
				userConnection.disconnect();
			}
		}

		return addedUser;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.user.IUserService#searchUsers(java.lang.String, io.reflection.app.api.shared.datatypes.Pager)
	 */
	@Override
	public List<User> searchUsers(String mask, Pager pager) throws DataAccessException {
		List<User> users = new ArrayList<User>();

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection userConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeUser.toString());

		String searchUsersQuery = String
				.format("SELECT * FROM `user` WHERE `deleted`='n' AND (`username` LIKE '%%%1$s%%' OR `forename` LIKE '%%%1$s%%' OR `surname` LIKE '%%%1$s%%' OR `company` LIKE '%%%1$s%%')",
						mask);

		if (pager != null) {
			String sortByQuery = "id";

			if (pager.sortBy != null
					&& ("username".equals(pager.sortBy) || "forename".equals(pager.sortBy) || "surname".equals(pager.sortBy) || "customerid"
							.equals(pager.sortBy))) {
				sortByQuery = pager.sortBy;
			}

			String sortDirectionQuery = "DESC";

			if (pager.sortDirection != null) {
				switch (pager.sortDirection) {
					case SortDirectionTypeAscending:
						sortDirectionQuery = "ASC";
						break;
					default:
						break;
				}
			}

			searchUsersQuery += String.format(" ORDER BY `%s` %s", sortByQuery, sortDirectionQuery);
		}

		if (pager.start != null && pager.count != null) {
			searchUsersQuery += String.format(" LIMIT %d, %d", pager.start.longValue(), pager.count.longValue());
		} else if (pager.count != null) {
			searchUsersQuery += String.format(" LIMIT %d", pager.count);
		}

		try {
			userConnection.connect();
			userConnection.executeQuery(searchUsersQuery);

			while (userConnection.fetchNextRow()) {
				User user = toUser(userConnection);

				if (user != null) {
					users.add(user);
				}
			}
		} finally {
			if (userConnection != null) {
				userConnection.disconnect();
			}
		}

		return users;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.user.IUserService#SearchUsersCount(java.lang.String)
	 */
	@Override
	public Long searchUsersCount(String mask) throws DataAccessException {
		Long usersCount = Long.valueOf(0);

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection userConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeUser.toString());

		String searchUsersCountQuery = String
				.format(
						"SELECT COUNT(1) AS `usercount` FROM `user`WHERE `deleted`='n' AND (`username` LIKE '%%%1$s%%' OR `forename` LIKE '%%%1$s%%' OR `surname` LIKE '%%%1$s%%' OR `company` LIKE '%%%1$s%%')",
						addslashes(mask));

		try {
			userConnection.connect();
			userConnection.executeQuery(searchUsersCountQuery);

			if (userConnection.fetchNextRow()) {
				usersCount = userConnection.getCurrentRowLong("usercount");
			}
		} finally {
			if (userConnection != null) {
				userConnection.disconnect();
			}
		}

		return usersCount;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.user.IUserService#updateUserPassword(io.reflection.app.shared.datatypes.User, java.lang.String)
	 */
	@Override
	public void updateUserPassword(User user, String newPassword) throws DataAccessException {
		updateUserPassword(user, newPassword, Boolean.TRUE);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.user.IUserService#updateUser(io.reflection.app.shared.datatypes.User)
	 */
	@Override
	public User updateUser(User user) throws DataAccessException {
		User updatedUser = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection userConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeUser.toString());

		String updateUserQuery = String.format("UPDATE `user` SET `forename`='%s', `surname`='%s', `username`='%s', `avatar`=%s, `company`='%s' WHERE `id`=%d",
				addslashes(user.forename), addslashes(user.surname), addslashes(user.username),
				user.avatar == null ? "'" + addslashes(StringUtils.md5Hash(user.username.trim().toLowerCase())) + "'" : "'" + addslashes(user.avatar) + "'",
				addslashes(user.company), user.id.longValue());
		try {
			userConnection.connect();
			userConnection.executeQuery(updateUserQuery);
		} finally {

			if (userConnection != null) {
				userConnection.disconnect();
			}
		}

		updatedUser = getUser(user.id);

		return updatedUser;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.user.IUserService#deleteUser(io.reflection.app.shared.datatypes.User)
	 */
	@Override
	public void deleteUser(User user) throws DataAccessException {
		Connection userConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeUser.toString());

		String deleteUserQuery = String.format("UPDATE `user` SET `deleted`='y' WHERE `id`=%d AND `deleted`='n'", user.id.longValue());
		try {
			userConnection.connect();
			userConnection.executeQuery(deleteUserQuery);
		} finally {
			if (userConnection != null) {
				userConnection.disconnect();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.user.IUserService#deleteUsers(java.util.Collection)
	 */
	@Override
	public void deleteUsers(Collection<User> users) throws DataAccessException {

		StringBuffer commaDelimitedUserIds = new StringBuffer();

		if (users != null && users.size() > 0) {
			for (User user : users) {
				if (commaDelimitedUserIds.length() != 0) {
					commaDelimitedUserIds.append("','");
				}

				commaDelimitedUserIds.append(user.id);
			}
		}

		if (commaDelimitedUserIds != null && commaDelimitedUserIds.length() != 0) {

			Connection userConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeUser.toString());

			String deleteUsersQuery = String.format("UPDATE `user` SET `deleted`='y' WHERE `id` IN ('%s') AND `deleted`='n'", commaDelimitedUserIds);
			try {
				userConnection.connect();
				userConnection.executeQuery(deleteUsersQuery);
			} finally {
				if (userConnection != null) {
					userConnection.disconnect();
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.user.IUserService#getLoginUser(java.lang.String, java.lang.String)
	 */
	@Override
	public User getLoginUser(String username, String password) throws DataAccessException {
		User user = null;

		Connection userConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeUser.toString());

		String getUserByUsernameAndPasswordQuery = String.format("SELECT * FROM `user` WHERE `deleted`='n' AND `username`='%s' AND `password`='%s' LIMIT 1",
				addslashes(username), sha1Hash(SALT + password));
		try {
			userConnection.connect();
			userConnection.executeQuery(getUserByUsernameAndPasswordQuery);

			if (userConnection.fetchNextRow()) {
				user = toUser(userConnection);

				if (user != null) {
					updateLoginTime(user);
				}
			}
		} finally {
			if (userConnection != null) {
				userConnection.disconnect();
			}
		}

		return user;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.user.IUserService#getUsers(io.reflection.app.api.shared.datatypes.Pager)
	 */
	@Override
	public List<User> getUsers(Pager pager) throws DataAccessException {
		List<User> users = new ArrayList<User>();

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection userConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeUser.toString());

		String getUserIdsQuery = "SELECT * FROM `user` WHERE `deleted`='n'";

		if (pager != null) {
			String sortByQuery = "id";

			if (pager.sortBy != null
					&& ("username".equals(pager.sortBy) || "forename".equals(pager.sortBy) || "surname".equals(pager.sortBy) || "customerid"
							.equals(pager.sortBy))) {
				sortByQuery = pager.sortBy;
			}

			String sortDirectionQuery = "DESC";

			if (pager.sortDirection != null) {
				switch (pager.sortDirection) {
					case SortDirectionTypeAscending:
						sortDirectionQuery = "ASC";
						break;
					default:
						break;
				}
			}

			getUserIdsQuery += String.format(" ORDER BY `%s` %s", sortByQuery, sortDirectionQuery);
		}

		if (pager.start != null && pager.count != null) {
			getUserIdsQuery += String.format(" LIMIT %d, %d", pager.start.longValue(), pager.count.longValue());
		} else if (pager.count != null) {
			getUserIdsQuery += String.format(" LIMIT %d", pager.count.longValue());
		}
		try {
			userConnection.connect();
			userConnection.executeQuery(getUserIdsQuery);

			while (userConnection.fetchNextRow()) {
				User user = toUser(userConnection);

				if (user != null) {
					users.add(user);
				}
			}
		} finally {
			if (userConnection != null) {
				userConnection.disconnect();
			}
		}

		return users;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.user.IUserService#getUsersCount()
	 */
	@Override
	public Long getUsersCount() throws DataAccessException {
		Long usersCount = Long.valueOf(0);

		Connection userConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeUser.toString());

		String getUsersCountQuery = "SELECT COUNT(1) AS `usercount` FROM `user` WHERE `deleted`='n'";

		try {
			userConnection.connect();
			userConnection.executeQuery(getUsersCountQuery);

			if (userConnection.fetchNextRow()) {
				usersCount = userConnection.getCurrentRowLong("usercount");
			}
		} finally {
			if (userConnection != null) {
				userConnection.disconnect();
			}
		}

		return usersCount;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.user.IUserService#updateLoginTime(io.reflection.app.shared.datatypes.User)
	 */
	@Override
	public void updateLoginTime(User user) throws DataAccessException {
		Connection userConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeUser.toString());

		String updateLoginTimeQuery = String.format("UPDATE `user` SET `lastloggedin`=NOW() WHERE `id`=%d", user.id.longValue());
		try {
			userConnection.connect();
			userConnection.executeQuery(updateLoginTimeQuery);
		} finally {
			if (userConnection != null) {
				userConnection.disconnect();
			}
		}
	}

	// /*
	// * (non-Javadoc)
	// *
	// * @see io.reflection.app.service.user.IUserService#getSessionUser(io.reflection.app.shared.datatypes.DataType)
	// */
	// @Override
	// public User getSessionUser(DataType session) {
	// User user = null;
	//
	// Connection sessionConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeSession.toString());
	//
	// String getSessionUserQuery = String.format("SELECT `userid` FROM `session` WHERE `id`=%d", session.id.longValue());
	//
	// try {
	// sessionConnection.connect();
	// sessionConnection.executeQuery(getSessionUserQuery);
	//
	// if (sessionConnection.fetchNextRow()) {
	// user = this.getUser(sessionConnection.getCurrentRowLong("userid"));
	// }
	// } finally {
	// if (sessionConnection != null) {
	// sessionConnection.disconnect();
	// }
	// }
	//
	// return user;
	// }

	/**
	 *
	 * @param connction
	 * @return
	 */
	private User toUser(Connection connection) throws DataAccessException {
		User user = new User();

		user.id = connection.getCurrentRowLong("id");
		user.created = connection.getCurrentRowDateTime("created");
		user.deleted = connection.getCurrentRowString("deleted");

		user.forename = stripslashes(connection.getCurrentRowString("forename"));
		user.surname = stripslashes(connection.getCurrentRowString("surname"));
		user.username = stripslashes(connection.getCurrentRowString("username"));
		user.lastLoggedIn = connection.getCurrentRowDateTime("lastloggedin");
		user.avatar = stripslashes(connection.getCurrentRowString("avatar"));
		user.company = stripslashes(connection.getCurrentRowString("company"));
		user.verified = connection.getCurrentRowString("verified");
		user.code = stripslashes(connection.getCurrentRowString("code"));

		return user;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.user.IUserService#getUsernameUser(java.lang.String)
	 */
	@Override
	public User getUsernameUser(String username) throws DataAccessException {
		User user = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection userConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeUser.toString());

		String getUserByUsernameQuery = String.format("SELECT * FROM `user` WHERE `deleted`='n' AND `username`='%s' LIMIT 1", username);

		try {
			userConnection.connect();
			userConnection.executeQuery(getUserByUsernameQuery);

			if (userConnection.fetchNextRow()) {
				user = toUser(userConnection);
			}
		} finally {
			if (userConnection != null) {
				userConnection.disconnect();
			}
		}

		return user;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.user.IUserService#assignRole(io.reflection.app.shared.datatypes.User, io.reflection.app.shared.datatypes.Role)
	 */
	@Override
	public void assignRole(User user, Role role) throws DataAccessException {
		String assignUserRoleQuery = String.format("INSERT INTO `userrole` (`userid`, `roleid`) VALUES (%d, %d)", user.id.longValue(), role.id.longValue());

		Connection roleConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeRole.toString());

		try {
			roleConnection.connect();
			roleConnection.executeQuery(assignUserRoleQuery);

			if (roleConnection.getAffectedRowCount() > 0) {
				if (LOG.isLoggable(Level.INFO)) {
					LOG.info(String.format("Role with roleid [%d] was added to user with userid [%d]", role.id.longValue(), user.id.longValue()));
				}

			} else {
				if (LOG.isLoggable(Level.WARNING)) {
					LOG.warning(String.format("Role with roleid [%d] was NOT added to user with userid [%d]", role.id.longValue(), user.id.longValue()));
				}
			}
		} finally {
			if (roleConnection != null) {
				roleConnection.disconnect();
			}
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.user.IUserService#assignRole(io.reflection.app.datatypes.shared.User, io.reflection.app.datatypes.shared.Role, int)
	 */
	@Override
	public void assignExpiringRole(User user, Role role, int expiringDays) throws DataAccessException {
		String assignUserRoleQuery = String.format("INSERT INTO `userrole` (`userid`, `roleid`, `expires`) VALUES (%d, %d, DATE_ADD(NOW(),INTERVAL %d DAY))",
				user.id.longValue(), role.id.longValue(), expiringDays);

		Connection roleConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeRole.toString());

		try {
			roleConnection.connect();
			roleConnection.executeQuery(assignUserRoleQuery);

			if (roleConnection.getAffectedRowCount() > 0) {
				if (LOG.isLoggable(Level.INFO)) {
					LOG.info(String.format("Role with roleid [%d] was added to user with userid [%d] expiring in [%d] days", role.id.longValue(),
							user.id.longValue(), expiringDays));
				}

			} else {
				if (LOG.isLoggable(Level.WARNING)) {
					LOG.warning(String.format("Role with roleid [%d] was NOT added to user with userid [%d]", role.id.longValue(), user.id.longValue()));
				}
			}
		} finally {
			if (roleConnection != null) {
				roleConnection.disconnect();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.user.IUserService#hasRole(io.reflection.app.shared.datatypes.User, io.reflection.app.shared.datatypes.Role)
	 */
	@Override
	public Boolean hasRole(User user, Role role) throws DataAccessException {
		return hasRole(user, role, false);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.user.IUserService#hasRole(io.reflection.app.datatypes.shared.User, io.reflection.app.datatypes.shared.Role, boolean)
	 */
	@Override
	public Boolean hasRole(User user, Role role, boolean includeExpired) throws DataAccessException {
		Boolean hasUserRole = Boolean.FALSE;

		String expiredPart = includeExpired ? "" : " AND (`expires` > NOW() OR `expires` IS NULL)";

		String hasUserRoleQuery = String.format("SELECT `id` FROM `userrole` WHERE `userid`=%d AND `roleid`=%d AND `deleted`='n'" + expiredPart + " LIMIT 1",
				user.id.longValue(), role.id.longValue());

		Connection roleConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeRole.toString());

		try {
			roleConnection.connect();
			roleConnection.executeQuery(hasUserRoleQuery);

			if (roleConnection.fetchNextRow()) {
				hasUserRole = Boolean.TRUE;
			}
		} finally {
			if (roleConnection != null) {
				roleConnection.disconnect();
			}
		}

		return hasUserRole;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.user.IUserService#assignPermission(io.reflection.app.shared.datatypes.User, io.reflection.app.shared.datatypes.Permission)
	 */
	@Override
	public void assignPermission(User user, Permission permission) throws DataAccessException {
		String assignUserPermissionQuery = String.format("INSERT INTO `userpermission` (`userid`, `permissionid`) VALUES (%d, %d)", user.id.longValue(),
				permission.id.longValue());

		Connection permissionConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypePermission.toString());

		try {
			permissionConnection.connect();
			permissionConnection.executeQuery(assignUserPermissionQuery);

			if (permissionConnection.getAffectedRowCount() > 0) {
				if (LOG.isLoggable(Level.INFO)) {
					LOG.info(String.format("Permission with permissionid [%d] was added to user with userid [%d]", permission.id.longValue(),
							user.id.longValue()));
				}
			} else {
				if (LOG.isLoggable(Level.WARNING)) {
					LOG.warning(String.format("Permission with permissionid [%d] was NOT added to user with userid [%d]", permission.id.longValue(),
							user.id.longValue()));
				}
			}
		} finally {
			if (permissionConnection != null) {
				permissionConnection.disconnect();
			}
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.user.IUserService#hasPermission(io.reflection.app.shared.datatypes.User, io.reflection.app.shared.datatypes.Permission)
	 */
	@Override
	public Boolean hasPermission(User user, Permission permission) throws DataAccessException {
		return hasPermission(user, permission, Boolean.FALSE);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.user.IUserService#hasPermission(io.reflection.app.datatypes.shared.User, io.reflection.app.datatypes.shared.Permission,
	 * java.lang.Boolean)
	 */
	@Override
	public Boolean hasPermission(User user, Permission permission, Boolean deleted) throws DataAccessException {
		Boolean hasUserPermission = Boolean.FALSE;

		String hasUserPermissionQuery = String.format("SELECT `id` FROM `userpermission` WHERE `userid`=%d AND `permissionid`=%d %s LIMIT 1",
				user.id.longValue(), permission.id.longValue(), deleted ? "" : "AND `deleted`='n'");

		Connection permissionConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypePermission.toString());

		try {
			permissionConnection.connect();
			permissionConnection.executeQuery(hasUserPermissionQuery);

			if (permissionConnection.fetchNextRow()) {
				hasUserPermission = Boolean.TRUE;
			}
		} finally {
			if (permissionConnection != null) {
				permissionConnection.disconnect();
			}
		}

		return hasUserPermission;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.user.IUserService#getRoles(io.reflection.app.shared.datatypes.User)
	 */
	@Override
	public List<Role> getUserRoles(User user) throws DataAccessException {
		return getUserRoles(user, false);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.user.IUserService#getRoles(io.reflection.app.datatypes.shared.User, boolean)
	 */
	@Override
	public List<Role> getUserRoles(User user, boolean includeExpired) throws DataAccessException {
		List<Role> roles = new ArrayList<Role>();

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection userConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeUser.toString());

		String expiredPart = includeExpired ? "" : " AND (`expires` > NOW() OR `expires` IS NULL) ORDER BY `expires` DESC";

		String getRoleIdsQuery = String.format("SELECT DISTINCT * FROM `userrole` WHERE `userid`=%d AND `deleted`='n'" + expiredPart, user.id.longValue());

		try {
			userConnection.connect();
			userConnection.executeQuery(getRoleIdsQuery);

			while (userConnection.fetchNextRow()) {
				Role role = new Role();

				role.id = userConnection.getCurrentRowLong("roleid");
				role.created = userConnection.getCurrentRowDateTime("created"); // Get created date of user role

				roles.add(role);
			}
		} finally {
			if (userConnection != null) {
				userConnection.disconnect();
			}
		}

		return roles;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.user.IUserService#getPermissions(io.reflection.app.shared.datatypes.User)
	 */
	@Override
	public List<Permission> getPermissions(User user) throws DataAccessException {
		List<Permission> permissions = new ArrayList<Permission>();

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection userConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeUser.toString());

		String getPermissionIdsQuery = String.format("SELECT DISTINCT `permissionid` FROM `userpermission` WHERE `userid`=%d AND `deleted`='n'",
				user.id.longValue());

		try {
			userConnection.connect();
			userConnection.executeQuery(getPermissionIdsQuery);

			while (userConnection.fetchNextRow()) {
				Permission permission = new Permission();

				permission.id = userConnection.getCurrentRowLong("permissionid");

				permissions.add(permission);
			}
		} finally {
			if (userConnection != null) {
				userConnection.disconnect();
			}
		}

		return permissions;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.user.IUserService#addDataAccount(io.reflection.app.datatypes.shared.User, io.reflection.app.datatypes.shared.DataSource,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public DataAccount addDataAccount(User user, DataSource dataSource, String username, String password, String vendorId, String developerName, String accountId) throws DataAccessException {

		DataAccount addedDataAccount = null;

		DataAccount toAdd = new DataAccount();
		toAdd.source = dataSource;
		toAdd.username = username;
		toAdd.password = password;
		toAdd.vendorId = vendorId;
		toAdd.developerName = developerName;
		toAdd.accountId = accountId;
		toAdd.properties = ITunesConnectDownloadHelper.createProperties(vendorId);

		addedDataAccount = DataAccountServiceProvider.provide().addDataAccount(toAdd);

		return addedDataAccount;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.user.IUserService#getDataAccounts(io.reflection.app.datatypes.shared.User, io.reflection.app.api.shared.datatypes.Pager)
	 */
	@Override
	public List<DataAccount> getDataAccounts(User user, Pager pager) throws DataAccessException {
		List<DataAccount> usersAccounts = DataAccountServiceProvider.provide().getDataAccountForUser(user.id);
		LOG.log(GaeLevel.DEBUG, String.format("%d Data accounts found for user", usersAccounts == null ? 0 : usersAccounts.size()));
		return usersAccounts;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.user.IUserService#getUsersDataAccounts(java.util.Collection)
	 */
	@Override
	public List<DataAccount> getUsersDataAccounts(Collection<User> users, Pager pager) throws DataAccessException {
		List<Long> accountIds = new ArrayList<Long>();

		StringBuffer commaDelimitedUserIds = new StringBuffer();

		if (users != null && users.size() > 0) {
			for (User user : users) {
				if (commaDelimitedUserIds.length() != 0) {
					commaDelimitedUserIds.append("','");
				}

				commaDelimitedUserIds.append(user.id);
			}
		}

		if (commaDelimitedUserIds != null && commaDelimitedUserIds.length() != 0) {

			String getDataAccountIdsQuery = String.format(
					"SELECT `dataaccountid` FROM `userdataaccount` WHERE `deleted`='n' AND `userid` IN ('%s') ORDER BY `%s` %s LIMIT %d, %d",
					commaDelimitedUserIds, pager.sortBy == null ? "id" : stripslashes(pager.sortBy),
					pager.sortDirection == SortDirectionType.SortDirectionTypeAscending ? "ASC" : "DESC", pager.start == null ? Pager.DEFAULT_START.longValue()
							: pager.start.longValue(),
					pager.count == null ? Pager.DEFAULT_COUNT.longValue() : pager.count.longValue());

			Connection userConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeUser.toString());

			try {
				userConnection.connect();
				userConnection.executeQuery(getDataAccountIdsQuery);

				while (userConnection.fetchNextRow()) {
					Long accountId = userConnection.getCurrentRowLong("dataaccountid");

					if (accountId != null) {
						accountIds.add(accountId);
					}
				}
			} finally {
				if (userConnection != null) {
					userConnection.disconnect();
				}
			}
		}

		return accountIds.size() == 0 ? new ArrayList<DataAccount>() : DataAccountServiceProvider.provide().getIdsDataAccounts(accountIds, pager);
	}

	/*
	 * (non-Javadoc)/
	 *
	 * @see io.reflection.app.service.user.IUserService#getDataAccountsCount(io.reflection.app.datatypes.shared.User)
	 */
	@Override
	public Long getDataAccountsCount(User user) throws DataAccessException {
		Long dataAccountsCount = Long.valueOf(0);

		String getDataAccountsCountQuery = String.format("SELECT count(1) AS `dataaccountscount` FROM `userdataaccount` WHERE `deleted`='n' AND `userid`=%d",
				user.id.longValue());

		Connection userConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeUser.toString());

		try {
			userConnection.connect();
			userConnection.executeQuery(getDataAccountsCountQuery);

			if (userConnection.fetchNextRow()) {
				dataAccountsCount = userConnection.getCurrentRowLong("dataaccountscount");
			}
		} finally {
			if (userConnection != null) {
				userConnection.disconnect();
			}
		}

		return dataAccountsCount;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.user.IUserService#getDataAccountOwner(io.reflection.app.datatypes.shared.DataAccount)
	 */
	@Override
	public User getDataAccountOwner(DataAccount dataAccount) throws DataAccessException {
		User owner = null;

		String getDataAccountOwnerQuery = String.format(
				"SELECT `userid` FROM `userdataaccount` WHERE `dataaccountid`=%d AND `deleted`='n' ORDER BY `id` ASC LIMIT 1", dataAccount.id.longValue());

		Connection userConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeUser.toString());

		try {
			userConnection.connect();
			userConnection.executeQuery(getDataAccountOwnerQuery);

			if (userConnection.fetchNextRow()) {
				Long userId = userConnection.getCurrentRowLong("userid");
				owner = getUser(userId);
			}

		} finally {
			if (userConnection != null) {
				userConnection.disconnect();
			}
		}

		return owner;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.user.IUserService#getDataAccountOwnerBatch(java.util.Collection)
	 */
	@Override
	public List<User> getDataAccountOwnerBatch(Collection<Long> dataAccountIds) throws DataAccessException {
		List<User> owners = new ArrayList<User>();

		StringBuffer commaDelimitedDataAccountIds = new StringBuffer();

		if (dataAccountIds != null && dataAccountIds.size() > 0) {
			for (Long dataAccountId : dataAccountIds) {
				if (commaDelimitedDataAccountIds.length() != 0) {
					commaDelimitedDataAccountIds.append("','");
				}

				commaDelimitedDataAccountIds.append(dataAccountId);
			}
		}

		if (commaDelimitedDataAccountIds != null && commaDelimitedDataAccountIds.length() != 0) {
			String getDataAccountOwnerQuery = String
					.format("SELECT `userid`, `dataaccountid` FROM `userdataaccount` WHERE `dataaccountid` IN ('%s') AND `deleted`='n' GROUP BY `dataaccountid` ORDER BY `id` ASC",
							commaDelimitedDataAccountIds);

			Connection userConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeUser.toString());

			try {
				userConnection.connect();
				userConnection.executeQuery(getDataAccountOwnerQuery);

				Long userId, dataAccountId;
				while (userConnection.fetchNextRow()) {
					userId = userConnection.getCurrentRowLong("userid");
					dataAccountId = userConnection.getCurrentRowLong("dataaccountid");
					User user = getUser(userId);

					if (user != null) {
						owners.add(user);

						DataAccount dataAccount = new DataAccount();
						dataAccount.id = dataAccountId;

						user.linkedAccounts = new ArrayList<DataAccount>();
						user.linkedAccounts.add(dataAccount);
					}
				}

			} finally {
				if (userConnection != null) {
					userConnection.disconnect();
				}
			}
		}

		return owners;
	}

	/**
	 * @param connection
	 * @param user
	 * @return
	 */
	private String getUserActionCode(Connection connection, User user) throws DataAccessException {
		String actionCode = null;

		String getUserActionCodeQuery = String.format("SELECT CAST(`code` AS CHAR) AS `actioncode` FROM `user` WHERE `deleted`='n' AND `id`=%d LIMIT 1",
				user.id.longValue());

		connection.executeQuery(getUserActionCodeQuery);

		if (connection.fetchNextRow()) {
			actionCode = connection.getCurrentRowString("actioncode");
		}

		return actionCode;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.user.IUserService#getActionCodeUser(java.lang.String)
	 */
	@Override
	public User getActionCodeUser(String code) throws DataAccessException {
		User user = null;

		String getActionCodeUserQuery = String.format("SELECT * FROM `user` WHERE `code`=CAST('%s' AS BINARY) AND `deleted`='n' LIMIT 1", addslashes(code));

		Connection userConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeUser.toString());

		try {
			userConnection.connect();
			userConnection.executeQuery(getActionCodeUserQuery);

			if (userConnection.fetchNextRow()) {
				user = toUser(userConnection);
			}

		} finally {
			if (userConnection != null) {
				userConnection.disconnect();
			}
		}

		return user;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.user.IUserService#markForReset(io.reflection.app.datatypes.shared.User)
	 */
	@Override
	public void markForReset(User user) throws DataAccessException {
		markForEmailAction(user, "resetpassword", DataTypeHelper.RESET_PASSWORD_EVENT_CODE);
	}

	private void markForEmailAction(User user, String pageAction, String eventCode) throws DataAccessException {
		String markForEmailActionQuery = String.format("UPDATE `user` SET `code`=CAST(UUID() AS BINARY) WHERE `deleted`='n' AND `id`=%d", user.id.longValue());

		Connection userConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeUser.toString());

		try {
			userConnection.connect();
			userConnection.executeQuery(markForEmailActionQuery);

			if (userConnection.getAffectedRowCount() > 0) {
				String code = getUserActionCode(userConnection, user);

				Map<String, Object> values = new HashMap<String, Object>();

				if (user.forename == null) {
					user = getUser(user.id);
				}

				values.put("user", user);
				values.put("link", String.format("#!%s/%d/%s", pageAction, user.id.longValue(), code));

				if (LOG.isLoggable(GaeLevel.DEBUG)) {
					LOG.fine(String.format("Sending action code url [%s] to [%s]", values.get("link"), user.username));
				}

				Event event = EventServiceProvider.provide().getCodeEvent(eventCode);

				String subject = NotificationHelper.inflate(values, event.subject);

				String body = NotificationHelper.inflate(values, event.longBody);

				Notification notification = (new Notification()).from("hello@reflection.io").user(user).event(event).body(body).subject(subject);
				Notification added = NotificationServiceProvider.provide().addNotification(notification);

				if (added.type != NotificationTypeType.NotificationTypeTypeInternal) {
					notification.type = NotificationTypeType.NotificationTypeTypeInternal;
					NotificationServiceProvider.provide().addNotification(notification);
				}
			}

		} finally {
			if (userConnection != null) {
				userConnection.disconnect();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.user.IUserService#updateUserPassword(io.reflection.app.datatypes.shared.User, java.lang.String, java.lang.Boolean)
	 */
	@Override
	public void updateUserPassword(User user, String newPassword, Boolean notify) throws DataAccessException {
		String updateUserPasswordQuery = String.format("UPDATE `user` SET `password`='%s', `code`=NULL WHERE `id`=%d", sha1Hash(SALT + newPassword),
				user.id.longValue());

		Connection userConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeUser.toString());

		try {
			userConnection.connect();
			userConnection.executeQuery(updateUserPasswordQuery);

			if (userConnection.getAffectedRowCount() > 0 && notify == Boolean.TRUE) {
				Map<String, Object> values = new HashMap<String, Object>();
				values.put("user", user);

				Event event = EventServiceProvider.provide().getCodeEvent(DataTypeHelper.CHANGE_PASSWORD_EVENT_CODE);
				String body = NotificationHelper.inflate(values, event.longBody);

				Notification notification = (new Notification()).from("hello@reflection.io").user(user).event(event).body(body).subject(event.subject);
				Notification added = NotificationServiceProvider.provide().addNotification(notification);

				if (added.type != NotificationTypeType.NotificationTypeTypeInternal) {
					notification.type = NotificationTypeType.NotificationTypeTypeInternal;
					NotificationServiceProvider.provide().addNotification(notification);
				}
			}
		} finally {
			if (userConnection != null) {
				userConnection.disconnect();
			}
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.user.IUserService#hasDataAccount(io.reflection.app.datatypes.shared.User, io.reflection.app.datatypes.shared.DataAccount)
	 */
	@Override
	public Boolean hasDataAccount(User user, DataAccount dataAccount) throws DataAccessException {
		return hasDataAccount(user, dataAccount, Boolean.FALSE);
	}

	/**
	 *
	 * @param user
	 * @param dataAccount
	 * @param deleted
	 *          If true, check deleted linked accounts as well
	 * @return
	 * @throws DataAccessException
	 */
	private Boolean hasDataAccount(User user, DataAccount dataAccount, Boolean deleted) throws DataAccessException {
		Boolean hasDataAccount = Boolean.FALSE;

		String hasDataAccountQuery = String.format("SELECT 1 FROM `userdataaccount` WHERE `dataaccountid`=%d AND `userid`=%d %s ORDER BY `id` ASC LIMIT 1",
				dataAccount.id.longValue(), user.id.longValue(), deleted ? "" : "AND `deleted`='n'");

		Connection userConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeUser.toString());

		try {
			userConnection.connect();
			userConnection.executeQuery(hasDataAccountQuery);

			if (userConnection.fetchNextRow()) {
				hasDataAccount = Boolean.TRUE;
			}

		} finally {
			if (userConnection != null) {
				userConnection.disconnect();
			}
		}

		return hasDataAccount;
	}

	/**
	 * Return true if the user has at least one active linked account
	 *
	 * @param user
	 * @return
	 * @throws DataAccessException
	 */
	@Override
	public Boolean hasDataAccounts(User user) throws DataAccessException {
		Boolean hasDataAccounts = Boolean.FALSE;

		String hasDataAccountQuery = String.format("SELECT 1 FROM `userdataaccount` WHERE `userid`=%d AND `deleted`='n' LIMIT 1", user.id.longValue());

		Connection userConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeUser.toString());

		try {
			userConnection.connect();
			userConnection.executeQuery(hasDataAccountQuery);

			if (userConnection.fetchNextRow()) {
				hasDataAccounts = Boolean.TRUE;
			}

		} finally {
			if (userConnection != null) {
				userConnection.disconnect();
			}
		}

		return hasDataAccounts;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.user.IUserService#restoreUserDeletedDataAccount(io.reflection.app.datatypes.shared.User, java.lang.String)
	 */
	@Override
	public void restoreUserDataAccount(User user, DataAccount dataAccount) throws DataAccessException {
		String restoreDataAccountQuery = String.format("UPDATE `userdataaccount` SET `deleted`='n' WHERE `dataaccountid`=%d AND `userid`=%d AND `deleted`='y'",
				dataAccount.id.longValue(), user.id.longValue());

		Connection userConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeUser.toString());

		try {
			userConnection.connect();
			userConnection.executeQuery(restoreDataAccountQuery);

			if (userConnection.getAffectedRowCount() > 0) {
				// added the user account successfully
			}
		} finally {
			if (userConnection != null) {
				userConnection.disconnect();
			}
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.user.IUserService#deleteDataAccount(io.reflection.app.datatypes.shared.User,
	 * io.reflection.app.datatypes.shared.DataAccount)
	 */
	@Override
	public void deleteUserDataAccount(User user, DataAccount dataAccount) throws DataAccessException {

		String deleteDataAccountQuery = String.format("UPDATE `userdataaccount` SET `deleted`='y' WHERE `dataaccountid`=%d AND `userid`=%d AND `deleted`='n'",
				dataAccount.id.longValue(), user.id.longValue());

		Connection userConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeUser.toString());

		try {
			userConnection.connect();
			userConnection.executeQuery(deleteDataAccountQuery);

			if (userConnection.getAffectedRowCount() > 0) {
				// log something
			}
		} finally {
			if (userConnection != null) {
				userConnection.disconnect();
			}
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.user.IUserService#deleteAllUsersDataAccount(io.reflection.app.datatypes.shared.DataAccount)
	 */
	@Override
	public void deleteAllUsersDataAccount(DataAccount dataAccount) throws DataAccessException {
		String deleteAllUsersDataAccountQuery = String.format("UPDATE `userdataaccount` SET `deleted`='y' WHERE `dataaccountid`=%d AND `deleted`='n'",
				dataAccount.id.longValue());

		Connection userConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeUser.toString());

		try {
			userConnection.connect();
			userConnection.executeQuery(deleteAllUsersDataAccountQuery);

		} finally {
			if (userConnection != null) {
				userConnection.disconnect();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.user.IUserService#deleteAllDataAccounts(io.reflection.app.datatypes.shared.User)
	 */
	@Override
	public void deleteAllDataAccounts(User user) throws DataAccessException {
		String deleteAllDataAccountsQuery = String
				.format("UPDATE `userdataaccount` SET `deleted`='y' WHERE `userid`=%d AND `deleted`='n'", user.id.longValue());

		Connection userConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeUser.toString());

		try {
			userConnection.connect();
			userConnection.executeQuery(deleteAllDataAccountsQuery);

			if (userConnection.getAffectedRowCount() > 0) {
				// log something
			}
		} finally {
			if (userConnection != null) {
				userConnection.disconnect();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.user.IUserService#deleteUsersAllDataAccounts(java.util.Collection)
	 */
	@Override
	public void deleteUsersAllDataAccounts(Collection<User> users) throws DataAccessException {

		StringBuffer commaDelimitedUserIds = new StringBuffer();

		if (users != null && users.size() > 0) {
			for (User user : users) {
				if (commaDelimitedUserIds.length() != 0) {
					commaDelimitedUserIds.append("','");
				}

				commaDelimitedUserIds.append(user.id);
			}
		}

		if (commaDelimitedUserIds != null && commaDelimitedUserIds.length() != 0) {

			String deleteAllUsersDataAccountQuery = String.format("UPDATE `userdataaccount` SET `deleted`='y' WHERE `userid` IN ('%s') AND `deleted`='n'",
					commaDelimitedUserIds);

			Connection userConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeUser.toString());

			try {
				userConnection.connect();
				userConnection.executeQuery(deleteAllUsersDataAccountQuery);

			} finally {
				if (userConnection != null) {
					userConnection.disconnect();
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.user.IUserService#deletePermission(io.reflection.app.datatypes.shared.User, io.reflection.app.datatypes.shared.Permission)
	 */
	@Override
	public void revokePermission(User user, Permission permission) throws DataAccessException {
		String deletePermissionQuery = String.format("UPDATE `userpermission` SET `deleted`='y' WHERE `permissionid`=%d AND `userid`=%d AND `deleted`='n'",
				permission.id.longValue(), user.id.longValue());

		Connection userConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeUser.toString());

		try {
			userConnection.connect();
			userConnection.executeQuery(deletePermissionQuery);

			if (userConnection.getAffectedRowCount() > 0) {
				// log something
			}
		} finally {
			if (userConnection != null) {
				userConnection.disconnect();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.user.IUserService#revokeAllPermissions(io.reflection.app.datatypes.shared.User)
	 */
	@Override
	public void revokeAllPermissions(User user) throws DataAccessException {
		String deletePermissionQuery = String.format("UPDATE `userpermission` SET `deleted`='y' WHERE `userid`=%d AND `deleted`='n'", user.id.longValue());

		Connection userConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeUser.toString());

		try {
			userConnection.connect();
			userConnection.executeQuery(deletePermissionQuery);

			if (userConnection.getAffectedRowCount() > 0) {
				// log something
			}
		} finally {
			if (userConnection != null) {
				userConnection.disconnect();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.user.IUserService#revokeUsersAllPermissions(java.util.Collection)
	 */
	@Override
	public void revokeUsersAllPermissions(Collection<User> users) throws DataAccessException {

		StringBuffer commaDelimitedUserIds = new StringBuffer();

		if (users != null && users.size() > 0) {
			for (User user : users) {
				if (commaDelimitedUserIds.length() != 0) {
					commaDelimitedUserIds.append("','");
				}

				commaDelimitedUserIds.append(user.id);
			}
		}

		if (commaDelimitedUserIds != null && commaDelimitedUserIds.length() != 0) {

			Connection userConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeUser.toString());

			String deletePermissionQuery = String.format("UPDATE `userpermission` SET `deleted`='y' WHERE `userid` IN ('%s') AND `deleted`='n'",
					commaDelimitedUserIds);
			try {
				userConnection.connect();
				userConnection.executeQuery(deletePermissionQuery);

			} finally {
				if (userConnection != null) {
					userConnection.disconnect();
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.user.IUserService#revokeRoles(io.reflection.app.datatypes.shared.User, io.reflection.app.datatypes.shared.Role)
	 */
	@Override
	public void revokeRole(User user, Role role) throws DataAccessException {
		String deleteRoleQuery = String.format("UPDATE `userrole` SET `expires`=NOW() WHERE `roleid`=%d AND `userid`=%d AND `deleted`='n'",
				role.id.longValue(), user.id.longValue());

		Connection userConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeUser.toString());

		try {
			userConnection.connect();
			userConnection.executeQuery(deleteRoleQuery);

			if (userConnection.getAffectedRowCount() > 0) {
				// log something
			}
		} finally {
			if (userConnection != null) {
				userConnection.disconnect();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.user.IUserService#revokeAllRoles(io.reflection.app.datatypes.shared.User)
	 */
	@Override
	public void revokeAllRoles(User user) throws DataAccessException {
		String deleteRoleQuery = String.format("UPDATE `userrole` SET `expires`=NOW() WHERE `userid`=%d AND `deleted`='n'", user.id.longValue());

		Connection userConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeUser.toString());

		try {
			userConnection.connect();
			userConnection.executeQuery(deleteRoleQuery);

			if (userConnection.getAffectedRowCount() > 0) {
				// log something
			}
		} finally {
			if (userConnection != null) {
				userConnection.disconnect();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.user.IUserService#revokeUsersAllRoles(java.util.Collection)
	 */
	@Override
	public void revokeUsersAllRoles(Collection<User> users) throws DataAccessException {

		StringBuffer commaDelimitedUserIds = new StringBuffer();

		if (users != null && users.size() > 0) {
			for (User user : users) {
				if (commaDelimitedUserIds.length() != 0) {
					commaDelimitedUserIds.append("','");
				}

				commaDelimitedUserIds.append(user.id);
			}
		}

		if (commaDelimitedUserIds != null && commaDelimitedUserIds.length() != 0) {

			Connection userConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeUser.toString());

			String deleteRoleQuery = String.format("UPDATE `userrole` SET `expires`=NOW() WHERE `userid` IN ('%s') AND `deleted`='n'", commaDelimitedUserIds);
			try {
				userConnection.connect();
				userConnection.executeQuery(deleteRoleQuery);

			} finally {
				if (userConnection != null) {
					userConnection.disconnect();
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.user.IUserService#addOrRestoreUserDataAccount(io.reflection.app.datatypes.shared.User,
	 * io.reflection.app.datatypes.shared.DataAccount)
	 */
	@Override
	public void addOrRestoreUserDataAccount(User user, DataAccount dataAccount) throws DataAccessException {
		if (!hasDataAccount(user, dataAccount, Boolean.TRUE)) {
			String addUserDataAccountQuery = String.format("INSERT INTO `userdataaccount` (`dataaccountid`,`userid`) VALUES (%d, %d)",
					dataAccount.id.longValue(), user.id.longValue());

			Connection userConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeUser.toString());

			try {
				userConnection.connect();
				userConnection.executeQuery(addUserDataAccountQuery);

				if (userConnection.getAffectedRowCount() > 0) {
					// added the user account successfully
				}
			} finally {
				if (userConnection != null) {
					userConnection.disconnect();
				}
			}
		} else {
			restoreUserDataAccount(user, dataAccount);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.user.IUserService#getDataAccountsIds(io.reflection.app.datatypes.shared.User,
	 * io.reflection.app.api.shared.datatypes.Pager)
	 */
	@Override
	public List<Long> getDataAccountsIds(User user, Pager pager) throws DataAccessException {
		List<Long> accountIds = new ArrayList<Long>();

		String getDataAccountIdsQuery = String.format(
				"SELECT `dataaccountid` FROM `userdataaccount` WHERE `deleted`='n' AND `userid`=%d ORDER BY `%s` %s LIMIT %d, %d", user.id.longValue(),
				pager.sortBy == null ? "id" : stripslashes(pager.sortBy), pager.sortDirection == SortDirectionType.SortDirectionTypeAscending ? "ASC" : "DESC",
				pager.start == null ? 0 : pager.start.longValue(), pager.count == null ? 25 : pager.count.longValue());

		Connection userConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeUser.toString());

		try {
			userConnection.connect();
			userConnection.executeQuery(getDataAccountIdsQuery);

			while (userConnection.fetchNextRow()) {
				Long accountId = userConnection.getCurrentRowLong("dataaccountid");

				if (accountId != null) {
					accountIds.add(accountId);
				}
			}
		} finally {
			if (userConnection != null) {
				userConnection.disconnect();
			}
		}

		return accountIds;
	}

}
