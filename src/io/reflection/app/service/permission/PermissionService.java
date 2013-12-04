//  
//  PermissionService.java
//  reflection.io
//
//  Created by William Shakour on November 21, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.service.permission;

import static com.spacehopperstudios.utility.StringUtils.addslashes;
import static com.spacehopperstudios.utility.StringUtils.stripslashes;
import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;
import io.reflection.app.shared.datatypes.Permission;

final class PermissionService implements IPermissionService {
	public String getName() {
		return ServiceType.ServiceTypePermission.toString();
	}

	@Override
	public Permission getPermission(Long id) throws DataAccessException {
		Permission permission = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection permissionConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypePermission.toString());

		String getPermissionQuery = String.format("SELECT * FROM `permission` WHERE `deleted`='n' AND `id`=%d LIMIT 1", id.longValue());
		try {
			permissionConnection.connect();
			permissionConnection.executeQuery(getPermissionQuery);

			if (permissionConnection.fetchNextRow()) {
				permission = toPermission(permissionConnection);
			}
		} finally {
			if (permissionConnection != null) {
				permissionConnection.disconnect();
			}
		}

		return permission;
	}

	/**
	 * To permission
	 * 
	 * @param connection
	 * @return
	 * @throws DataAccessException
	 */
	private Permission toPermission(Connection connection) throws DataAccessException {
		Permission permission = new Permission();

		permission.id = connection.getCurrentRowLong("id");
		permission.created = connection.getCurrentRowDateTime("created");
		permission.deleted = connection.getCurrentRowString("deleted");

		permission.code = stripslashes(connection.getCurrentRowString("code"));
		permission.description = stripslashes(connection.getCurrentRowString("description"));
		permission.name = stripslashes(connection.getCurrentRowString("name"));

		return permission;
	}

	@Override
	public Permission addPermission(Permission permission) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Permission updatePermission(Permission permission) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deletePermission(Permission permission) {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.permission.IPermissionService#getNamedPermission(java.lang.String)
	 */
	@Override
	public Permission getNamedPermission(String name) throws DataAccessException {
		Permission permission = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection permissionConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypePermission.toString());

		String getPermissionQuery = String.format("SELECT * FROM `permission` WHERE `deleted`='n' AND `name`='%s' LIMIT 1", addslashes(name));
		try {
			permissionConnection.connect();
			permissionConnection.executeQuery(getPermissionQuery);

			if (permissionConnection.fetchNextRow()) {
				permission = toPermission(permissionConnection);
			}
		} finally {
			if (permissionConnection != null) {
				permissionConnection.disconnect();
			}
		}

		return permission;
	}

}