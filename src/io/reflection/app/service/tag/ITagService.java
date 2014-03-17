//  
//  ITagService.java
//  reflection.io
//
//  Created by William Shakour on March 16, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.tag;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.datatypes.shared.Tag;

import com.spacehopperstudios.service.IService;

public interface ITagService extends IService {
	/**
	 * @param id
	 * @return
	 * @throws DataAccessException
	 */
	public Tag getTag(Long id) throws DataAccessException;

	/**
	 * @param tag
	 * @return
	 * @throws DataAccessException
	 */
	public Tag addTag(Tag tag) throws DataAccessException;

	/**
	 * @param tag
	 * @return
	 * @throws DataAccessException
	 */
	public Tag updateTag(Tag tag) throws DataAccessException;

	/**
	 * @param tag
	 * @throws DataAccessException
	 */
	public void deleteTag(Tag tag) throws DataAccessException;

}