//  
//  TagService.java
//  reflection.io
//
//  Created by William Shakour on March 16, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.tag;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.datatypes.shared.Tag;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;

final class TagService implements ITagService {
	public String getName() {
		return ServiceType.ServiceTypeTag.toString();
	}

	@Override
	public Tag getTag(Long id) throws DataAccessException {
		Tag tag = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection tagConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeTag.toString());

		String getTagQuery = String.format("select * from `tag` where `deleted`='n' and `id`='%d' limit 1", id.longValue());
		try {
			tagConnection.connect();
			tagConnection.executeQuery(getTagQuery);

			if (tagConnection.fetchNextRow()) {
				tag = toTag(tagConnection);
			}
		} finally {
			if (tagConnection != null) {
				tagConnection.disconnect();
			}
		}
		return tag;
	}

	/**
	 * To tag
	 * 
	 * @param connection
	 * @return
	 * @throws DataAccessException
	 */
	private Tag toTag(Connection connection) throws DataAccessException {
		Tag tag = new Tag();
		tag.id = connection.getCurrentRowLong("id");
		return tag;
	}

	@Override
	public Tag addTag(Tag tag) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Tag updateTag(Tag tag) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteTag(Tag tag) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

}