//  
//  PostService.java
//  reflection.io
//
//  Created by William Shakour on March 16, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.post;

import static com.spacehopperstudios.utility.StringUtils.addslashes;
import static com.spacehopperstudios.utility.StringUtils.join;
import static com.spacehopperstudios.utility.StringUtils.stripslashes;
import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.datatypes.shared.Post;
import io.reflection.app.datatypes.shared.User;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;
import io.reflection.app.shared.util.TagHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

final class PostService implements IPostService {

	private static final Logger LOG = Logger.getLogger(PostService.class.getName());

	public String getName() {
		return ServiceType.ServiceTypePost.toString();
	}

	@Override
	public Post getPost(Long id) throws DataAccessException {
		Post post = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection postConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypePost.toString());

		String getPostQuery = String.format("SELECT * FROM `post` WHERE `deleted`='n' AND `id`=%d LIMIT 1", id.longValue());
		try {
			postConnection.connect();
			postConnection.executeQuery(getPostQuery);

			if (postConnection.fetchNextRow()) {
				post = toPost(postConnection);
			}
		} finally {
			if (postConnection != null) {
				postConnection.disconnect();
			}
		}

		return post;
	}

	private Post toPost(Connection connection) throws DataAccessException {
		return toPost(connection, true);
	}

	/**
	 * To post
	 * 
	 * @param connection
	 * @param includeContents
	 * @throws DataAccessException
	 * @return
	 */
	private Post toPost(Connection connection, boolean includeContents) throws DataAccessException {
		Post post = new Post();
		post.id = connection.getCurrentRowLong("id");
		post.created = connection.getCurrentRowDateTime("created");
		post.deleted = connection.getCurrentRowString("deleted");

		post.tags = TagHelper.convertToTagList(stripslashes(connection.getCurrentRowString("tags")));

		if (includeContents) {
			post.content = stripslashes(connection.getCurrentRowString("content"));
		}

		post.description = stripslashes(connection.getCurrentRowString("description"));
		post.published = connection.getCurrentRowDateTime("published");
		post.title = stripslashes(connection.getCurrentRowString("title"));

		post.author = new User();
		post.author.id = connection.getCurrentRowLong("authorid");

		Integer value = connection.getCurrentRowInteger("visible");
		post.visible = (value == null ? null : (value.intValue() > 0 ? Boolean.TRUE : Boolean.FALSE));

		value = connection.getCurrentRowInteger("commentsenabled");
		post.commentsEnabled = (value == null ? null : (value.intValue() > 0 ? Boolean.TRUE : Boolean.FALSE));

		return post;
	}

	@Override
	public Post addPost(Post post) throws DataAccessException {
		Post addedPost = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection postConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypePost.toString());

		String addPostQuery = String
				.format("INSERT INTO `post` (`authorid`,`title`,`description`,`content`,`published`,`visible`,`tags`,`commentsenabled`) VALUES (%d,'%s','%s','%s',%s,%d,%s,%d)",
						post.author.id.longValue(), addslashes(post.title), addslashes(post.description), addslashes(post.content),
						post.published == null ? "NULL" : String.format("FROM_UNIXTIME(%d)", post.published.getTime() / 1000), post.visible == null ? 0
								: (post.visible.booleanValue() ? 1 : 0), post.tags == null ? "NULL" : "'" + addslashes(join(post.tags)) + "'",
						post.commentsEnabled == null ? 0 : (post.commentsEnabled.booleanValue() ? 1 : 0));
		try {
			postConnection.connect();
			postConnection.executeQuery(addPostQuery);

			if (postConnection.getAffectedRowCount() > 0) {
				post.id = postConnection.getInsertedId();
				addedPost = post;
			}
		} finally {
			if (postConnection != null) {
				postConnection.disconnect();
			}
		}

		return addedPost;
	}

	@Override
	public Post updatePost(Post post) throws DataAccessException {
		Post updatedPost = null;
		boolean changed = false;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection postConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypePost.toString());

		String updatePostQuery = String
				.format("UPDATE `post` SET `title`='%s',`description`='%s',`content`='%s',`published`=%s,`visible`=%d,`tags`=%s,`commentsenabled`=%d WHERE `id`=%d AND `deleted`='n'",
						addslashes(post.title), addslashes(post.description), addslashes(post.content),
						post.published == null ? "NULL" : String.format("FROM_UNIXTIME(%d)", post.published.getTime() / 1000), post.visible == null ? 0
								: (post.visible.booleanValue() ? 1 : 0), post.tags == null ? "NULL" : "'" + addslashes(join(post.tags)) + "'",
						post.commentsEnabled == null ? 0 : (post.commentsEnabled.booleanValue() ? 1 : 0), post.id.longValue());
		try {
			postConnection.connect();
			postConnection.executeQuery(updatePostQuery);

			if (postConnection.getAffectedRowCount() > 0) {
				changed = true;
			}
		} finally {
			if (postConnection != null) {
				postConnection.disconnect();
			}
		}

		if (changed) {
			updatedPost = getPost(post.id);
		} else {
			updatedPost = post;
		}

		return updatedPost;
	}

	@Override
	public void deletePost(Post post) throws DataAccessException {
		String deletePostQuery = String.format("UPDATE `post` SET `deleted`='y' WHERE `id`=%d", post.id.longValue());

		Connection postConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypePost.toString());

		try {
			postConnection.connect();
			postConnection.executeQuery(deletePostQuery);

			if (postConnection.getAffectedRowCount() > 0) {
				if (LOG.isLoggable(GaeLevel.INFO)) {
					LOG.info(String.format("Post with id [%d] was deleted", post.id.longValue()));
				}
			}
		} finally {
			if (postConnection != null) {
				postConnection.disconnect();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.post.IPostService#getPosts(java.lang.Boolean, java.lang.Boolean, io.reflection.app.api.shared.datatypes.Pager)
	 */
	@Override
	public List<Post> getUserViewablePosts(User user, Boolean showAll, Boolean includeContents, Pager pager) throws DataAccessException {
		List<Post> posts = new ArrayList<Post>();

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection postConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypePost.toString());

		String getPostsQuery = null;

		if (includeContents == Boolean.TRUE) {
			getPostsQuery = "SELECT *";
		} else {
			// no content column
			getPostsQuery = "SELECT `id`,`created`,`authorid`, `published`,`title`,`description`,`visible`,`tags`,`commentsenabled`,`deleted`";
		}

		getPostsQuery += " FROM `post` WHERE (`authorid`=" + user.id.longValue() + " AND `deleted`='n') OR `deleted`='n'";

		// If showAll == true, get unpublished posts
		if (showAll == null || !showAll.booleanValue()) {
			getPostsQuery += " AND `visible`>0";
		}

		if (pager != null) {
			String sortByQuery = "id";

			if (pager.sortBy != null && ("created".equals(pager.sortBy) || "published".equals(pager.sortBy))) {
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

			getPostsQuery += String.format(" ORDER BY `%s` %s", sortByQuery, sortDirectionQuery);
		}

		if (pager.start != null && pager.count != null) {
			getPostsQuery += String.format(" LIMIT %d, %d", pager.start.longValue(), pager.count.longValue());
		} else if (pager.count != null) {
			getPostsQuery += String.format(" LIMIT %d", pager.count.longValue());
		}

		try {
			postConnection.connect();
			postConnection.executeQuery(getPostsQuery);

			while (postConnection.fetchNextRow()) {
				Post post = toPost(postConnection, includeContents == null ? true : includeContents.booleanValue());

				if (post != null) {
					posts.add(post);
				}
			}
		} finally {
			if (postConnection != null) {
				postConnection.disconnect();
			}
		}

		return posts;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.post.IPostService#getTitlePost(java.lang.String)
	 */
	@Override
	public Post getTitlePost(String title) throws DataAccessException {
		Post post = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection postConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypePost.toString());

		String getTitlePostQuery = String.format("SELECT * FROM `post` WHERE `deleted`='n' AND `title`='%s' LIMIT 1", addslashes(title));

		try {
			postConnection.connect();
			postConnection.executeQuery(getTitlePostQuery);

			if (postConnection.fetchNextRow()) {
				post = toPost(postConnection);
			}
		} finally {
			if (postConnection != null) {
				postConnection.disconnect();
			}
		}

		return post;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.post.IPostService#getPostsCount(java.lang.Boolean)
	 */
	@Override
	public Long getUserViewablePostsCount(User user, Boolean showAll) throws DataAccessException {
		Long postsCount = Long.valueOf(0);

		Connection postConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypePost.toString());

		String getPostsCountQuery = "SELECT count(1) AS `postcount` FROM `post` WHERE (`authorid`=" + user.id.longValue()
				+ " AND `deleted`='n') OR `deleted`='n'";

		if (showAll == null || !showAll.booleanValue()) {
			getPostsCountQuery += " AND `visible`>0";
		}

		try {
			postConnection.connect();
			postConnection.executeQuery(getPostsCountQuery);

			if (postConnection.fetchNextRow()) {
				postsCount = postConnection.getCurrentRowLong("postcount");
			}
		} finally {
			if (postConnection != null) {
				postConnection.disconnect();
			}
		}

		return postsCount;
	}
}