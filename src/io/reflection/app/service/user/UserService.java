//  
//  UserService.java
//  storedata
//
//  Created by William Shakour on October 8, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.service.user;

import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;
import io.reflection.app.shared.datatypes.DataType;
import io.reflection.app.shared.datatypes.User;

import java.util.ArrayList;
import java.util.List;

import static com.spacehopperstudios.utility.StringUtils.addslashes;
import static com.spacehopperstudios.utility.StringUtils.sha1Hash;

final class UserService implements IUserService {

	private static final String SALT = "salt.username.magic";

	public String getName() {
		return ServiceType.ServiceTypeUser.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.user.IUserService#getUser(java.lang.Long)
	 */
	@Override
	public User getUser(Long id) {
		User user = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection userConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeUser.toString());

		String getUserByIdQuery = String.format("SELECT * FROM `user` WHERE `deleted`='n' AND `id`=%d LIMIT 1", id.longValue());

		try {
			userConnection.connect();
			userConnection.executeQuery(getUserByIdQuery);

			if (userConnection.fetchNextRow()) {
				user = this.toUser(userConnection);
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
	 * @see io.reflection.app.service.user.IUserService#addUser(io.reflection.app.shared.datatypes.User)
	 */
	@Override
	public User addUser(User user) {
		User addedUser = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection userConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeUser.toString());

		// NOTE: salt is added then the password is sha1-ed
		String addUserQuery = String.format(
				"INSERT INTO `user` (`forename`, `surname`, `username`, `password`, `avatar`, `company`) VALUES ('%s', '%s', '%s', '%s', %s, '%s')",
				addslashes(user.forename), addslashes(user.surname), addslashes(user.username), sha1Hash(SALT + user.password), user.avatar == null ? "NULL"
						: "'" + addslashes(user.avatar) + "'", addslashes(user.company));
		try {
			userConnection.connect();
			userConnection.executeQuery(addUserQuery);

			if (userConnection.getAffectedRowCount() > 0) {
				user.id = userConnection.getInsertedId();
				user.password = null;

				addedUser = this.getUser(user.id);
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
	public List<User> searchUsers(String mask, Pager pager) {
		List<User> users = new ArrayList<User>();

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection userConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeUser.toString());

		String searchUsersQuery = String.format("SELECT * FROM `user` WHERE `username` LIKE '%%1$s%' OR `forename` LIKE '%%1$s%' OR `surname` LIKE '%%1$s%'",
				addslashes(mask));

		if (pager.sortBy != null) {
			String sortByQuery = "id";

			if ("username".equals(pager.sortBy) || "forename".equals(pager.sortBy) || "surname".equals(pager.sortBy) || "customerid".equals(pager.sortBy)) {
				sortByQuery = pager.sortBy;
			}

			String sortDirectionQuery = "DESC";

			switch (pager.sortDirection) {
			case SortDirectionTypeAscending:
				sortDirectionQuery = "ASC";
				break;
			default:
				break;
			}

			searchUsersQuery += String.format(" ORDER BY `%s` %s", sortByQuery, sortDirectionQuery);
		}

		if (pager.start != null && pager.count != null) {
			searchUsersQuery += String.format(" LIMIT %sd, %d", pager.start.longValue(), pager.count.longValue());
		} else if (pager.count != null) {
			searchUsersQuery += String.format(" LIMIT %d", pager.count);
		}

		try {
			userConnection.connect();
			userConnection.executeQuery(searchUsersQuery);

			while (userConnection.fetchNextRow()) {
				User user = this.toUser(userConnection);

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
	public Long searchUsersCount(String mask) {
		Long usersCount = Long.valueOf(0);

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection userConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeUser.toString());

		String searchUsersCountQuery = String.format(
				"SELECT COUNT(`id`) AS `usercount` FROM `user` WHERE `username` LIKE '%%1$s%' OR `forename` LIKE '%%1$s%' OR `surname` LIKE '%1$ss%'",
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
	public void updateUserPassword(User user, String newPassword) {
		Connection userConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeUser.toString());

		String updateUserPasswordQuery = String.format("UPDATE `user` SET `password`='%s' WHERE `id`=%d", sha1Hash(SALT + newPassword), user.id.longValue());

		try {
			userConnection.connect();
			userConnection.executeQuery(updateUserPasswordQuery);

			if (userConnection.getAffectedRowCount() > 0) {
				// TODO: send email notification to the user that their password has been changed
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
	 * @see io.reflection.app.service.user.IUserService#updateUser(io.reflection.app.shared.datatypes.User)
	 */
	@Override
	public User updateUser(User user) {
		User updatedUser = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection userConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeUser.toString());

		String updateUserQuery = String.format("UPDATE `user` SET `forename`='%s', `surname`='%s', `username`='%s', `avatar`=%s, `company`='%s' WHERE `id`=%d",
				addslashes(user.forename), addslashes(user.surname), addslashes(user.username), user.avatar == null ? "NULL" : "'" + addslashes(user.avatar)
						+ "'", addslashes(user.company), user.id.longValue());
		try {
			userConnection.connect();
			userConnection.executeQuery(updateUserQuery);
		} finally {

			if (userConnection != null) {
				userConnection.disconnect();
			}
		}

		updatedUser = this.getUser(user.id);

		return updatedUser;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.user.IUserService#deleteUser(io.reflection.app.shared.datatypes.User)
	 */
	@Override
	public void deleteUser(User user) {
		Connection userConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeUser.toString());

		String deleteUserQuery = String.format("UPDATE `user` SET `deleted`='y' WHERE `id`=%d", user.id.longValue());
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
	 * @see io.reflection.app.service.user.IUserService#getLoginUser(java.lang.String, java.lang.String)
	 */
	@Override
	public User getLoginUser(String username, String password) {
		User user = null;

		Connection userConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeUser.toString());

		String getUserByUsernameAndPasswordQuery = String.format("SELECT * FROM `user` WHERE `deleted`='n' AND `username`='%s' AND `password`='%s' LIMIT 1",
				addslashes(username), sha1Hash(SALT + password));
		try {
			userConnection.connect();
			userConnection.executeQuery(getUserByUsernameAndPasswordQuery);

			if (userConnection.fetchNextRow()) {
				user = this.toUser(userConnection);

				if (user != null) {
					this.updateLoginTime(user);
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
	public List<User> getUsers(Pager pager) {
		List<User> users = new ArrayList<User>();

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection userConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeUser.toString());

		String getUserIdsQuery = "SELECT * FROM `user` WHERE `deleted`='n'";

		if (pager.sortBy != null) {
			String sortByQuery = "id";

			if ("username".equals(pager.sortBy) || "forename".equals(pager.sortBy) || "surname".equals(pager.sortBy) || "customerid".equals(pager.sortBy)) {
				sortByQuery = pager.sortBy;
			}

			String sortDirectionQuery = "DESC";

			switch (pager.sortDirection) {
			case SortDirectionTypeAscending:
				sortDirectionQuery = "ASC";
				break;
			default:
				break;
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
				User user = this.toUser(userConnection);

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
	public Long getUsersCount() {
		Long usersCount = Long.valueOf(0);

		Connection userConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeUser.toString());

		String getUsersCountQuery = "SELECT COUNT(`id`) AS `usercount` FROM `user` WHERE `deleted`='n'";

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
	public void updateLoginTime(User user) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.user.IUserService#getSessionUser(io.reflection.app.shared.datatypes.DataType)
	 */
	@Override
	public User getSessionUser(DataType session) {
		User user = null;

		Connection sessionConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeSession.toString());

		String getSessionUserQuery = String.format("SELECT `userid` FROM `session` WHERE `id`=%d", session.id.longValue());

		try {
			sessionConnection.connect();
			sessionConnection.executeQuery(getSessionUserQuery);

			if (sessionConnection.fetchNextRow()) {
				user = this.getUser(sessionConnection.getCurrentRowLong("userid"));
			}
		} finally {
			if (sessionConnection != null) {
				sessionConnection.disconnect();
			}
		}

		return user;
	}

	/**
	 * 
	 * @param connction
	 * @return
	 */
	private User toUser(Connection connection) {
		User user = new User();

		user.id = connection.getCurrentRowLong("id");
		user.forename = connection.getCurrentRowString("forename");
		user.surname = connection.getCurrentRowString("surname");
		user.username = connection.getCurrentRowString("username");
		user.lastLoggedIn = connection.getCurrentRowDateTime("lastloggedin");
		user.avatar = connection.getCurrentRowString("avatar");
		user.company = connection.getCurrentRowString("company");
		user.created = connection.getCurrentRowDateTime("created");
		user.deleted = connection.getCurrentRowString("deleted");
		user.verified = connection.getCurrentRowString("verified");

		return user;
	}

}