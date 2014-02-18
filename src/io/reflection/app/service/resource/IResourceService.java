//  
//  IResourceService.java
//  reflection.io
//
//  Created by William Shakour on February 17, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.resource;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.datatypes.shared.Resource;

import com.spacehopperstudios.service.IService;

public interface IResourceService extends IService {
	/**
	 * @param id
	 * @return
	 */
	public Resource getResource(Long id) throws DataAccessException;

	/**
	 * @param resource
	 * @return
	 */
	public Resource addResource(Resource resource) throws DataAccessException;

	/**
	 * @param resource
	 * @return
	 */
	public Resource updateResource(Resource resource) throws DataAccessException;

	/**
	 * @param resource
	 */
	public void deleteResource(Resource resource) throws DataAccessException;

}