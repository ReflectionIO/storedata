//  
//  ResourceService.java
//  reflection.io
//
//  Created by William Shakour on February 17, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.resource;

import static com.spacehopperstudios.utility.StringUtils.stripslashes;
import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.datatypes.shared.Resource;
import io.reflection.app.datatypes.shared.ResourceTypeType;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;

final class ResourceService implements IResourceService {
	public String getName() {
		return ServiceType.ServiceTypeResource.toString();
	}

	@Override
	public Resource getResource(Long id) throws DataAccessException {
		Resource resource = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection resourceConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeResource.toString());

		String getResourceQuery = String.format("SELECT * FROM `resource` WHERE `deleted`='n' and `id`=%d LIMIT 1", id.longValue());
		try {
			resourceConnection.connect();
			resourceConnection.executeQuery(getResourceQuery);

			if (resourceConnection.fetchNextRow()) {
				resource = toResource(resourceConnection);
			}
		} finally {
			if (resourceConnection != null) {
				resourceConnection.disconnect();
			}
		}
		return resource;
	}

	/**
	 * To resource
	 * 
	 * @param connection
	 * @return
	 */
	private Resource toResource(Connection connection) throws DataAccessException {
		Resource resource = new Resource();

		resource.id = connection.getCurrentRowLong("id");
		resource.created = connection.getCurrentRowDateTime("created");
		resource.deleted = connection.getCurrentRowString("deleted");

		resource.name = connection.getCurrentRowString("name");
		resource.properties = stripslashes(connection.getCurrentRowString("properties"));
		resource.type = ResourceTypeType.fromString(connection.getCurrentRowString("type"));

		return resource;
	}

	@Override
	public Resource addResource(Resource resource) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Resource updateResource(Resource resource) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteResource(Resource resource) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

}