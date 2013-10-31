//  
//  IModelRunService.java
//  reflection.io
//
//  Created by William Shakour on October 23, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.service.modelrun;

import io.reflection.app.shared.datatypes.Country;
import io.reflection.app.shared.datatypes.FormType;
import io.reflection.app.shared.datatypes.ModelRun;
import io.reflection.app.shared.datatypes.Store;

import com.spacehopperstudios.service.IService;

public interface IModelRunService extends IService {
	/**
	 * @param id
	 * @return
	 */
	public ModelRun getModelRun(Long id);

	/**
	 * @param modelRun
	 * @return
	 */
	public ModelRun addModelRun(ModelRun modelRun);

	/**
	 * @param modelRun
	 * @return
	 */
	public ModelRun updateModelRun(ModelRun modelRun);

	/**
	 * @param modelRun
	 */
	public void deleteModelRun(ModelRun modelRun);

	/**
	 * @param country
	 * @param store
	 * @param listTypes
	 * @param code
	 * @return
	 */
	public ModelRun getGatherCodeModelRun(Country country, Store store, FormType form, String code);

}