//  
//  ForumService.java
//  reflection.io
//
//  Created by William Shakour on April 5, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.forum;

import static com.spacehopperstudios.utility.StringUtils.stripslashes;
import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.datatypes.shared.Forum;
import io.reflection.app.datatypes.shared.ForumTypeType;
import io.reflection.app.datatypes.shared.User;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;

import java.util.ArrayList;
import java.util.List;

final class ForumService implements IForumService {
	public String getName() {
		return ServiceType.ServiceTypeForum.toString();
	}

	@Override
	public Forum getForum(Long id) throws DataAccessException {
		Forum forum = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection forumConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeForum.toString());

		String getForumQuery = String.format("SELECT * FROM `forum` WHERE `deleted`='n' AND `id`='%d' LIMIT 1", id.longValue());
		try {
			forumConnection.connect();
			forumConnection.executeQuery(getForumQuery);

			if (forumConnection.fetchNextRow()) {
				forum = toForum(forumConnection);
			}
		} finally {
			if (forumConnection != null) {
				forumConnection.disconnect();
			}
		}
		return forum;
	}

	/**
	 * To forum
	 * 
	 * @param connection
	 * @return
	 */
	private Forum toForum(Connection connection) throws DataAccessException {
		Forum forum = new Forum();
		forum.id = connection.getCurrentRowLong("id");
		forum.created = connection.getCurrentRowDateTime("created");
		forum.deleted = connection.getCurrentRowString("deleted");

		forum.title = stripslashes(connection.getCurrentRowString("title"));
		forum.description = stripslashes(connection.getCurrentRowString("description"));

		forum.creator = new User();
		forum.creator.id = connection.getCurrentRowLong("creatorid");

		forum.type = ForumTypeType.fromString(connection.getCurrentRowString("type"));

		return forum;
	}

	@Override
	public Forum addForum(Forum forum) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Forum updateForum(Forum forum) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteForum(Forum forum) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.forum.IForumService#getForumsCount()
	 */
	@Override
	public Long getForumsCount() throws DataAccessException {
		Long forumCount = Long.valueOf(0);

		Connection forumConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeForum.toString());

		String getForumCountQuery = "SELECT COUNT(`id`) AS `forumcount` FROM `forum` WHERE `deleted`='n'";

		try {
			forumConnection.connect();
			forumConnection.executeQuery(getForumCountQuery);

			if (forumConnection.fetchNextRow()) {
				forumCount = forumConnection.getCurrentRowLong("forumcount");
			}
		} finally {
			if (forumConnection != null) {
				forumConnection.disconnect();
			}
		}

		return forumCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.forum.IForumService#getForums(io.reflection.app.api.shared.datatypes.Pager)
	 */
	@Override
	public List<Forum> getForums(Pager pager) throws DataAccessException {
		List<Forum> forums = new ArrayList<Forum>();

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection forumConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeForum.toString());

		String getForumIdsQuery = "SELECT * FROM `forum` WHERE `deleted`='n'";

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

			getForumIdsQuery += String.format(" ORDER BY `%s` %s", sortByQuery, sortDirectionQuery);
		}

		if (pager.start != null && pager.count != null) {
			getForumIdsQuery += String.format(" LIMIT %d, %d", pager.start.longValue(), pager.count.longValue());
		} else if (pager.count != null) {
			getForumIdsQuery += String.format(" LIMIT %d", pager.count.longValue());
		}
		try {
			forumConnection.connect();
			forumConnection.executeQuery(getForumIdsQuery);

			while (forumConnection.fetchNextRow()) {
				Forum forum = this.toForum(forumConnection);

				if (forum != null) {
					forums.add(forum);
				}
			}
		} finally {
			if (forumConnection != null) {
				forumConnection.disconnect();
			}
		}

		return forums;
	}

}