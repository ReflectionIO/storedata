//  
//  RoleService.java
//  reflection.io
//
//  Created by William Shakour on November 21, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.service.role;

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

		String getRoleQuery = String.format("select * from `role` where `deleted`='n' and `id`='%d' limit 1", id.longValue());
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

}