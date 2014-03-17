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
import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.datatypes.shared.Post;
import io.reflection.app.datatypes.shared.Tag;
import io.reflection.app.datatypes.shared.User;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;

import java.util.ArrayList;
import java.util.List;

final class PostService implements IPostService {
	public String getName() {
		return ServiceType.ServiceTypePost.toString();
	}

	@Override
	public Post getPost(Long id) throws DataAccessException {
		Post post = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection postConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypePost.toString());

		String getPostQuery = String.format("SELECT * FROM `post` WHERE `deleted`='n' AND `id`='%d' LIMIT 1", id.longValue());
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

		post.tags = getTags(post.id);

		if (includeContents) {
			post.content = connection.getCurrentRowString("content");
		}

		post.description = connection.getCurrentRowString("description");
		post.published = connection.getCurrentRowDateTime("published");
		post.title = connection.getCurrentRowString("title");

		post.author = new User();
		post.author.id = connection.getCurrentRowLong("authorid");

		Integer value = connection.getCurrentRowInteger("visible");
		post.visible = (value == null ? null : (value.intValue() > 0 ? Boolean.TRUE : Boolean.FALSE));

		return post;
	}

	/**
	 * @param id
	 * @return
	 */
	private List<Tag> getTags(Long id) {
		return null;
	}

	@Override
	public Post addPost(Post post) throws DataAccessException {
		Post addedPost = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection postConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypePost.toString());

		String addPostQuery = String.format(
				"INSERT INTO `post` (`authorid`, `title`, `description`, `content`, `published`, `visible`) VALUES (%d, '%s', '%s', '%s', %s, %d)",
				post.author.id.longValue(), addslashes(post.title), addslashes(post.description), addslashes(post.content), post.published == null ? "NULL"
						: String.format("FROM_UNIXTIME(%d)", post.published.getTime()), post.visible == null ? 0 : (post.visible.booleanValue() ? 1 : 0));
		try {
			postConnection.connect();
			postConnection.executeQuery(addPostQuery);

			if (postConnection.getAffectedRowCount() > 0) {
				post.id = postConnection.getInsertedId();
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
		throw new UnsupportedOperationException();
	}

	@Override
	public void deletePost(Post post) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.post.IPostService#getPosts(java.lang.Boolean, java.lang.Boolean, io.reflection.app.api.shared.datatypes.Pager)
	 */
	@Override
	public List<Post> getPosts(Boolean onlyVisible, Boolean includeContents, Pager pager) throws DataAccessException {
		List<Post> posts = new ArrayList<Post>();

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection postConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypePost.toString());

		String getPostsQuery = null;

		if (includeContents == Boolean.TRUE) {
			getPostsQuery = "SELECT *";
		} else {
			// no content column
			getPostsQuery = "SELECT `id`,`created`,`authorid`, `published`,`title`,`description`,`version`,`visible`,`deleted`";
		}

		getPostsQuery += " FROM `post` WHERE `deleted`='n'";
		
		if (onlyVisible == null || onlyVisible.booleanValue()) {
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
	 * @see io.reflection.app.service.post.IPostService#assignTags(io.reflection.app.datatypes.shared.Post, java.util.List)
	 */
	@Override
	public void assignTags(Post post, List<Tag> tags) throws DataAccessException {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.post.IPostService#getTitlePost(java.lang.String)
	 */
	@Override
	public Post getTitlePost(String title) throws DataAccessException {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.post.IPostService#getPostsCount(java.lang.Boolean)
	 */
	@Override
	public Long getPostsCount(Boolean onlyVisible) throws DataAccessException {
		Long postsCount = Long.valueOf(0);

		Connection postConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypePost.toString());

		String getPostsCountQuery = "SELECT count(1) AS `postcount` FROM `post` WHERE `deleted`='n'";

		if (onlyVisible == null || onlyVisible.booleanValue()) {
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