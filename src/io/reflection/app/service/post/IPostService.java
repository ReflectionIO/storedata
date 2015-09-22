//  
//  IPostService.java
//  reflection.io
//
//  Created by William Shakour on March 16, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.post;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.datatypes.shared.Post;
import io.reflection.app.datatypes.shared.User;

import java.util.List;

import com.spacehopperstudios.service.IService;

public interface IPostService extends IService {
	/**
	 * @param id
	 * @return
	 * @throws DataAccessException
	 */
	public Post getPost(Long id) throws DataAccessException;

	/**
	 * @param post
	 * @return
	 * @throws DataAccessException
	 */
	public Post addPost(Post post) throws DataAccessException;

	/**
	 * @param post
	 * @return
	 * @throws DataAccessException
	 */
	public Post updatePost(Post post) throws DataAccessException;

	/**
	 * @param post
	 * @throws DataAccessException
	 */
	public void deletePost(Post post) throws DataAccessException;

	/**
	 * 
	 * @param user
	 *            , get all posts published by user
	 * @param showAll
	 *            , if true show even unpublished posts
	 * @param includeContents
	 * @param pager
	 * @return
	 * @throws DataAccessException
	 */
	public List<Post> getUserViewablePosts(User user, Boolean showAll, Boolean includeContents, Pager pager) throws DataAccessException;

	/**
	 * 
	 * @param showAll
	 * @param includeContents
	 * @param pager
	 * @return
	 * @throws DataAccessException
	 */
	public List<Post> getPosts(Boolean showAll, Boolean includeContents, Pager pager) throws DataAccessException;

	/**
	 * 
	 * @param user
	 * @param showAll
	 * @return
	 * @throws DataAccessException
	 */
	public Long getUserViewablePostsCount(User user, Boolean showAll) throws DataAccessException;

	/**
	 * 
	 * @param showAll
	 * @return
	 * @throws DataAccessException
	 */
	public Long getPostsCount(Boolean showAll) throws DataAccessException;

	/**
	 * 
	 * @param code
	 * @return
	 * @throws DataAccessException
	 */
	public Post getCodePost(String code) throws DataAccessException;

}