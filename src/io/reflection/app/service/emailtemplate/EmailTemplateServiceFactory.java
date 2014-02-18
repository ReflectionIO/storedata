//  
//  EmailTemplateServiceFactory.java
//  reflection.io
//
//  Created by William Shakour on February 17, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.emailtemplate;

final class EmailTemplateServiceFactory {

	/**
	 * @return
	 */
	public static IEmailTemplateService createNewEmailTemplateService() {
		return new EmailTemplateService();
	}

}