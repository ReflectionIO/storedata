//  
//  IApplicationService.java
//  reflection.io
//
//  Created by William Shakour on September 17, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.service.application;

import java.util.List;

import io.reflection.app.api.lookup.datatypes.LookupDetailType;
import io.reflection.app.datatypes.Application;

import com.spacehopperstudios.service.IService;

public interface IApplicationService extends IService {
	/**
	 * @param id
	 * @return
	 */
	public Application getApplication(Long id);

	/**
	 * @param application
	 * @return
	 */
	public Application addApplication(Application application);

	/**
	 * @param application
	 * @return
	 */
	public Application updateApplication(Application application);

	/**
	 * @param application
	 */
	public void deleteApplication(Application application);

	/**
	 * @param internalIds
	 * @param detail
	 * @return
	 */
	public List<Application> lookupInternalIdsApplication(List<String> internalIds, LookupDetailType detail);

	/**
	 * @param externalIds
	 * @param detail
	 * @return
	 */
	public List<Application> lookupExternalIdsApplication(List<String> externalIds, LookupDetailType detail);

	/**
	 * 
	 * @param application
	 * @param usesIap
	 */
	public void setApplicationIap(Application application, Boolean usesIap);

}