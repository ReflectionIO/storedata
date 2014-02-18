//  
//  EmailTemplateServiceProvider.java
//  reflection.io
//
//  Created by William Shakour on February 17, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.emailtemplate;

import io.reflection.app.service.ServiceType;

import com.spacehopperstudios.service.ServiceDiscovery;

final public class EmailTemplateServiceProvider {

	/**
	 * @return
	 */
	public static IEmailTemplateService provide() {
		IEmailTemplateService emailTemplateService = null;

		if ((emailTemplateService = (IEmailTemplateService) ServiceDiscovery.getService(ServiceType.ServiceTypeEmailTemplate.toString())) == null) {
			emailTemplateService = EmailTemplateServiceFactory.createNewEmailTemplateService();
			ServiceDiscovery.registerService(emailTemplateService);
		}

		return emailTemplateService;
	}

}