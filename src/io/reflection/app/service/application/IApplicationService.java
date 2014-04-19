//  
//  IApplicationService.java
//  reflection.io
//
//  Created by William Shakour on September 17, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.service.application;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.lookup.shared.datatypes.LookupDetailType;
import io.reflection.app.datatypes.shared.Application;
import io.reflection.app.datatypes.shared.Store;

import java.util.Collection;
import java.util.List;

import com.spacehopperstudios.service.IService;

public interface IApplicationService extends IService {
	/**
	 * @param id
	 * @return
	 */
	public Application getApplication(Long id) throws DataAccessException;

	/**
	 * @param application
	 * @return
	 */
	public Application addApplication(Application application) throws DataAccessException;

	/**
	 * @param application
	 * @return
	 */
	public Application updateApplication(Application application) throws DataAccessException;

	/**
	 * @param application
	 */
	public void deleteApplication(Application application) throws DataAccessException;
	/**
	 * @param internalIds
	 * @param detail
	 * @return
	 */
	public List<Application> lookupInternalIdsApplication(Collection<String> internalIds, LookupDetailType detail) throws DataAccessException;

	/**
	 * @param externalIds
	 * @param detail
	 * @return
	 */
	public List<Application> lookupExternalIdsApplication(Collection<String> externalIds, LookupDetailType detail) throws DataAccessException;

	/**
	 * 
	 * @param application
	 * @param usesIap
	 */
	public void setApplicationIap(Application application, Boolean usesIap) throws DataAccessException;

	/**
	 * @return
	 */
	public List<String> getStoreIapNaApplicationIds(Store store) throws DataAccessException;

}