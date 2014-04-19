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
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.datatypes.shared.Permission;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

		toPermission(connection, permission);

		return permission;
	}

	/**
	 * @param connection
	 * @param permission
	 * @throws DataAccessException
	 */
	private void toPermission(Connection connection, Permission permission) throws DataAccessException {
		permission.created = connection.getCurrentRowDateTime("created");
		permission.deleted = connection.getCurrentRowString("deleted");

		permission.code = stripslashes(connection.getCurrentRowString("code"));
		permission.description = stripslashes(connection.getCurrentRowString("description"));
		permission.name = stripslashes(connection.getCurrentRowString("name"));
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.permission.IPermissionService#getPermissions(io.reflection.app.api.shared.datatypes.Pager)
	 */
	@Override
	public List<Permission> getPermissions(Pager pager) throws DataAccessException {
		List<Permission> permissions = new ArrayList<Permission>();

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection permissionConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypePermission.toString());

		String getPermissionIdsQuery = "SELECT * FROM `permission` WHERE `deleted`='n'";

		if (pager != null) {
			String sortByQuery = "id";

			if (pager.sortBy != null && ("code".equals(pager.sortBy) || "name".equals(pager.sortBy))) {
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

			getPermissionIdsQuery += String.format(" ORDER BY `%s` %s", sortByQuery, sortDirectionQuery);
		}

		if (pager.start != null && pager.count != null) {
			getPermissionIdsQuery += String.format(" LIMIT %d, %d", pager.start.longValue(), pager.count.longValue());
		} else if (pager.count != null) {
			getPermissionIdsQuery += String.format(" LIMIT %d", pager.count.longValue());
		}
		
		try {
			permissionConnection.connect();
			permissionConnection.executeQuery(getPermissionIdsQuery);

			while (permissionConnection.fetchNextRow()) {
				Permission permission = this.toPermission(permissionConnection);

				if (permission != null) {
					permissions.add(permission);
				}
			}
		} finally {
			if (permissionConnection != null) {
				permissionConnection.disconnect();
			}
		}

		return permissions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.permission.IPermissionService#getPermissionsCount()
	 */
	@Override
	public Long getPermissionsCount() throws DataAccessException {
		Long permissionsCount = Long.valueOf(0);

		Connection permissionConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypePermission.toString());

		String getPermissionsCountQuery = "SELECT count(1) AS `permissioncount` FROM `permission` WHERE `deleted`='n'";

		try {
			permissionConnection.connect();
			permissionConnection.executeQuery(getPermissionsCountQuery);

			if (permissionConnection.fetchNextRow()) {
				permissionsCount = permissionConnection.getCurrentRowLong("permissioncount");
			}
		} finally {
			if (permissionConnection != null) {
				permissionConnection.disconnect();
			}
		}

		return permissionsCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.permission.IPermissionService#getIdPermissionsBatch(java.util.Collection)
	 */
	@Override
	public List<Permission> getIdPermissionsBatch(Collection<Long> permissionIds) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.permission.IPermissionService#inflatePermissions(java.util.Collection)
	 */
	@Override
	public void inflatePermissions(Collection<Permission> permissions) throws DataAccessException {
		if (permissions != null && permissions.size() > 0) {
			Map<Long, Permission> lookup = new HashMap<Long, Permission>();

			StringBuffer getPermissionsQuery = new StringBuffer("SELECT * FROM `permission` WHERE `id`");

			if (permissions.size() == 1) {
				getPermissionsQuery.append("=");
				
				Permission permission = permissions.iterator().next();
				
				getPermissionsQuery.append(permission.id);

				lookup.put(permission.id, permission);
			} else {
				boolean first = true;

				for (Permission permission : permissions) {
					if (!first) {
						getPermissionsQuery.append(",");
					} else {
						getPermissionsQuery.append(" IN (");
						first = false;
					}

					getPermissionsQuery.append(permission.id.toString());

					lookup.put(permission.id, permission);
				}

				getPermissionsQuery.append(")");
			}

			getPermissionsQuery.append(" AND `deleted`='n'");

			IDatabaseService databaseService = DatabaseServiceProvider.provide();
			Connection permissionConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypePermission.toString());

			try {
				permissionConnection.connect();
				permissionConnection.executeQuery(getPermissionsQuery.toString());

				if (permissionConnection.fetchNextRow()) {
					Permission permission = lookup.get(permissionConnection.getCurrentRowLong("id"));

					toPermission(permissionConnection, permission);
				}
			} finally {
				if (permissionConnection != null) {
					permissionConnection.disconnect();
				}
			}
		}

	}

}