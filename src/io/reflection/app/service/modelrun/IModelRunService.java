//  
//  IModelRunService.java
//  reflection.io
//
//  Created by William Shakour on October 23, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.service.modelrun;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.datatypes.shared.Country;
import io.reflection.app.datatypes.shared.FormType;
import io.reflection.app.datatypes.shared.ModelRun;
import io.reflection.app.datatypes.shared.Store;

import com.spacehopperstudios.service.IService;

public interface IModelRunService extends IService {
	/**
	 * @param id
	 * @return
	 */
	public ModelRun getModelRun(Long id) throws DataAccessException;

	/**
	 * @param modelRun
	 * @return
	 */
	public ModelRun addModelRun(ModelRun modelRun) throws DataAccessException;

	/**
	 * @param modelRun
	 * @return
	 */
	public ModelRun updateModelRun(ModelRun modelRun) throws DataAccessException;

	/**
	 * @param modelRun
	 */
	public void deleteModelRun(ModelRun modelRun) throws DataAccessException;

	/**
	 * @param country
	 * @param store
	 * @param listTypes
	 * @param code
	 * @return
	 */
	public ModelRun getGatherCodeModelRun(Country country, Store store, FormType form, String code) throws DataAccessException;

}