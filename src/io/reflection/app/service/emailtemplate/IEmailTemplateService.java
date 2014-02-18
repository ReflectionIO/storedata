//  
//  IEmailTemplateService.java
//  reflection.io
//
//  Created by William Shakour on February 17, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.emailtemplate;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.datatypes.shared.EmailTemplate;

import com.spacehopperstudios.service.IService;

public interface IEmailTemplateService extends IService {
	/**
	 * @param id
	 * @return
	 */
	public EmailTemplate getEmailTemplate(Long id) throws DataAccessException;

	/**
	 * @param emailTemplate
	 * @return
	 */
	public EmailTemplate addEmailTemplate(EmailTemplate emailTemplate) throws DataAccessException;

	/**
	 * @param emailTemplate
	 * @return
	 */
	public EmailTemplate updateEmailTemplate(EmailTemplate emailTemplate) throws DataAccessException;

	/**
	 * @param emailTemplate
	 */
	public void deleteEmailTemplate(EmailTemplate emailTemplate) throws DataAccessException;

}