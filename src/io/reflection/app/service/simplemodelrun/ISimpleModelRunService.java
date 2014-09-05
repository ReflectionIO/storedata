//  
//  ISimpleModelRunService.java
//  reflection.io
//
//  Created by William Shakour on September 5, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.simplemodelrun;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.datatypes.shared.SimpleModelRun;

import com.spacehopperstudios.service.IService;

public interface ISimpleModelRunService extends IService {
	/**
	 * @param id
	 * @return
	 */
	public SimpleModelRun getSimpleModelRun(Long id) throws DataAccessException;

	/**
	 * @param simpleModelRun
	 * @return
	 */
	public SimpleModelRun addSimpleModelRun(SimpleModelRun simpleModelRun) throws DataAccessException;

	/**
	 * @param simpleModelRun
	 * @return
	 */
	public SimpleModelRun updateSimpleModelRun(SimpleModelRun simpleModelRun) throws DataAccessException;

	/**
	 * @param simpleModelRun
	 */
	public void deleteSimpleModelRun(SimpleModelRun simpleModelRun) throws DataAccessException;

}