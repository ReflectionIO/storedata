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
import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.datatypes.shared.Permission;
import io.reflection.app.datatypes.shared.Role;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;
import io.reflection.app.service.permission.PermissionServiceProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class RoleService implements IRoleService {

	public String getName() {
		return ServiceType.ServiceTypeRole.toString();
	}

	@Override
	public Role getRole(Long id) throws DataAccessException {
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
	 * @throws DataAccessException
	 */
	private Role toRole(Connection connection) throws DataAccessException {
		Role role = new Role();

		role.id = connection.getCurrentRowLong("id");

		toRole(connection, role);

		return role;
	}

	/**
	 * @param connection
	 * @param role
	 * @throws DataAccessException
	 */
	private void toRole(Connection connection, Role role) throws DataAccessException {
		role.created = connection.getCurrentRowDateTime("created");
		role.deleted = connection.getCurrentRowString("deleted");

		role.code = stripslashes(connection.getCurrentRowString("code"));
		role.description = stripslashes(connection.getCurrentRowString("description"));
		role.name = stripslashes(connection.getCurrentRowString("name"));
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
	public Role getNamedRole(String name) throws DataAccessException {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.role.IRoleService#getRoles(io.reflection.app.api.shared.datatypes.Pager)
	 */
	@Override
	public List<Role> getRoles(Pager pager) throws DataAccessException {
		List<Role> roles = new ArrayList<Role>();

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection roleConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeRole.toString());

		String getRoleIdsQuery = "SELECT * FROM `role` WHERE `deleted`='n'";

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

			getRoleIdsQuery += String.format(" ORDER BY `%s` %s", sortByQuery, sortDirectionQuery);
		}

		if (pager.start != null && pager.count != null) {
			getRoleIdsQuery += String.format(" LIMIT %d, %d", pager.start.longValue(), pager.count.longValue());
		} else if (pager.count != null) {
			getRoleIdsQuery += String.format(" LIMIT %d", pager.count.longValue());
		}
		try {
			roleConnection.connect();
			roleConnection.executeQuery(getRoleIdsQuery);

			while (roleConnection.fetchNextRow()) {
				Role role = this.toRole(roleConnection);

				if (role != null) {
					roles.add(role);
				}
			}
		} finally {
			if (roleConnection != null) {
				roleConnection.disconnect();
			}
		}

		return roles;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.role.IRoleService#getRolesCount()
	 */
	@Override
	public Long getRolesCount() throws DataAccessException {
		Long rolesCount = Long.valueOf(0);

		Connection roleConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeRole.toString());

		String getRolesCountQuery = "SELECT COUNT(`id`) AS `rolecount` FROM `role` WHERE `deleted`='n'";

		try {
			roleConnection.connect();
			roleConnection.executeQuery(getRolesCountQuery);

			if (roleConnection.fetchNextRow()) {
				rolesCount = roleConnection.getCurrentRowLong("rolecount");
			}
		} finally {
			if (roleConnection != null) {
				roleConnection.disconnect();
			}
		}

		return rolesCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.role.IRoleService#getIdRoles(java.util.List)
	 */
	@Override
	public List<Role> getIdRoles(List<Long> roleIds) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.role.IRoleService#inflateRoles(java.util.List)
	 */
	@Override
	public void inflateRoles(List<Role> roles) throws DataAccessException {
		if (roles != null && roles.size() > 0) {
			Map<Long, Role> lookup = new HashMap<Long, Role>();

			StringBuffer getRolesQuery = new StringBuffer("SELECT * FROM `role` WHERE `id`");

			if (roles.size() == 1) {
				getRolesQuery.append("=");
				getRolesQuery.append(roles.get(0).id);

				lookup.put(roles.get(0).id, roles.get(0));
			} else {
				boolean first = true;

				for (Role role : roles) {
					if (!first) {
						getRolesQuery.append(",");
					} else {
						getRolesQuery.append(" IN (");
						first = false;
					}

					getRolesQuery.append(role.id.toString());

					lookup.put(role.id, role);
				}

				getRolesQuery.append(")");
			}

			getRolesQuery.append(" AND `deleted`='n'");

			IDatabaseService databaseService = DatabaseServiceProvider.provide();
			Connection roleConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeRole.toString());

			try {
				roleConnection.connect();
				roleConnection.executeQuery(getRolesQuery.toString());

				if (roleConnection.fetchNextRow()) {
					Role role = lookup.get(roleConnection.getCurrentRowLong("id"));

					toRole(roleConnection, role);
				}
			} finally {
				if (roleConnection != null) {
					roleConnection.disconnect();
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.role.IRoleService#getRolePermissions(io.reflection.app.datatypes.shared.Role)
	 */
	@Override
	public List<Permission> getPermissions(Role role) throws DataAccessException {
		List<Permission> rolePermissions = new ArrayList<Permission>();

		String getRolePermissionsQuery = String.format("SELECT `id` FROM `rolepermission` WHERE `roleid`=%d", role.id.longValue());

		Connection roleConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeRole.toString());

		try {
			roleConnection.connect();
			roleConnection.executeQuery(getRolePermissionsQuery);

			while (roleConnection.fetchNextRow()) {
				Long id = roleConnection.getCurrentRowLong("id");

				Permission p = PermissionServiceProvider.provide().getPermission(id);

				if (p != null) {
					rolePermissions.add(p);
				}
			}
		} finally {
			if (roleConnection != null) {
				roleConnection.disconnect();
			}
		}

		return rolePermissions;
	}

}