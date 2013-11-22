//  
//  PermissionService.java
//  reflection.io
//
//  Created by William Shakour on November 21, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.service.permission;

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
	public Permission getPermission(Long id) {
		Permission permission = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection permissionConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypePermission.toString());

		String getPermissionQuery = String.format("select * from `permission` where `deleted`='n' and `id`='%d' limit 1", id.longValue());
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
	 */
	private Permission toPermission(Connection connection) {
		Permission permission = new Permission();
		permission.id = connection.getCurrentRowLong("id");
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

}