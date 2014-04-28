//  
//  IForumService.java
//  reflection.io
//
//  Created by William Shakour on April 5, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.forum;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.datatypes.shared.Forum;

import java.util.List;

import com.spacehopperstudios.service.IService;

public interface IForumService extends IService {
	/**
	 * @param id
	 * @return
	 */
	public Forum getForum(Long id) throws DataAccessException;

	/**
	 * @param forum
	 * @return
	 */
	public Forum addForum(Forum forum) throws DataAccessException;

	/**
	 * @param forum
	 * @return
	 */
	public Forum updateForum(Forum forum) throws DataAccessException;

	/**
	 * @param forum
	 */
	public void deleteForum(Forum forum) throws DataAccessException;

	/**
	 * @return
	 */
	public Long getForumsCount() throws DataAccessException;

	/**
	 * @param pager
	 * @return
	 */
	public List<Forum> getForums(Pager pager) throws DataAccessException;

	/**
	 * 
	 * @param forum
	 * @return
	 * @throws DataAccessException
	 */
	public Forum addTopic(Forum forum) throws DataAccessException;

	/**
	 * 
	 * @param forum
	 * @return
	 * @throws DataAccessException
	 */
	public Forum removeTopic(Forum forum) throws DataAccessException;
}