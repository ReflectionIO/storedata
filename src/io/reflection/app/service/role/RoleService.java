//  
//  RoleService.java
//  reflection.io
//
//  Created by William Shakour on November 21, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.service.role;

import static com.spacehopperstudios.utility.StringUtils.addslashes;
import static com.spacehopperstudios.utility.StringUtils.stripslashes;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;
import io.reflection.app.shared.datatypes.Role;

final class RoleService implements IRoleService {

	public String getName() {
		return ServiceType.ServiceTypeRole.toString();
	}

	@Override
	public Role getRole(Long id) {
		Role role = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection roleConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeRole.toString());

		String getRoleQuery = String.format("SELECT * FROM `role` WHERE `deleted`='n' AND `id`=%d LIMIT 1", id.longValue());
		try {
			roleConnection.connect();
			roleConnection.executeQuery(getRoleQuery);

			if (roleConnection.fetchNextRow()) {
				role = toRole(roleConnection);
			}
		} finally {
			if (roleConnection != null) {
				roleConnection.disconnect();
			}
		}
		return role;
	}

	/**
	 * To role
	 * 
	 * @param connection
	 * @return
	 */
	private Role toRole(Connection connection) {
		Role role = new Role();

		role.id = connection.getCurrentRowLong("id");
		role.created = connection.getCurrentRowDateTime("created");
		role.deleted = connection.getCurrentRowString("deleted");

		role.code = stripslashes(connection.getCurrentRowString("code"));
		role.description = stripslashes(connection.getCurrentRowString("description"));
		role.name = stripslashes(connection.getCurrentRowString("name"));

		return role;
	}

	@Override
	public Role addRole(Role role) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Role updateRole(Role role) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteRole(Role role) {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.role.IRoleService#getNamedRole(java.lang.String)
	 */
	@Override
	public Role getNamedRole(String name) {
		Role role = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection roleConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeRole.toString());

		String getRoleQuery = String.format("SELECT * FROM `role` WHERE `deleted`='n' AND `name`='%s' LIMIT 1", addslashes(name));

		try {
			roleConnection.connect();
			roleConnection.executeQuery(getRoleQuery);

			if (roleConnection.fetchNextRow()) {
				role = toRole(roleConnection);
			}
		} finally {
			if (roleConnection != null) {
				roleConnection.disconnect();
			}
		}
		return role;
	}


}