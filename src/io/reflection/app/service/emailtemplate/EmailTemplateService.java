//  
//  EmailTemplateService.java
//  reflection.io
//
//  Created by William Shakour on February 17, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.emailtemplate;

import static com.spacehopperstudios.utility.StringUtils.stripslashes;
import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.datatypes.shared.EmailFormatType;
import io.reflection.app.datatypes.shared.EmailTemplate;
import io.reflection.app.datatypes.shared.EmailTypeType;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;

final class EmailTemplateService implements IEmailTemplateService {
	public String getName() {
		return ServiceType.ServiceTypeEmailTemplate.toString();
	}

	@Override
	public EmailTemplate getEmailTemplate(Long id) throws DataAccessException {
		EmailTemplate emailTemplate = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection emailTemplateConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeEmailTemplate.toString());

		String getEmailTemplateQuery = String.format("SELECT * FROM `emailtemplate` WHERE `deleted`='n' AND `id`=%d LIMIT 1", id.longValue());
		try {
			emailTemplateConnection.connect();
			emailTemplateConnection.executeQuery(getEmailTemplateQuery);

			if (emailTemplateConnection.fetchNextRow()) {
				emailTemplate = toEmailTemplate(emailTemplateConnection);
			}
		} finally {
			if (emailTemplateConnection != null) {
				emailTemplateConnection.disconnect();
			}
		}
		return emailTemplate;
	}

	/**
	 * To emailTemplate
	 * 
	 * @param connection
	 * @return
	 */
	private EmailTemplate toEmailTemplate(Connection connection) throws DataAccessException {
		EmailTemplate emailTemplate = new EmailTemplate();
		
		emailTemplate.id = connection.getCurrentRowLong("id");
		emailTemplate.created = connection.getCurrentRowDateTime("created");
		emailTemplate.deleted = connection.getCurrentRowString("deleted");
		
		emailTemplate.body = stripslashes(connection.getCurrentRowString("body"));
		emailTemplate.format = EmailFormatType.fromString(connection.getCurrentRowString("format"));
		emailTemplate.from = stripslashes(connection.getCurrentRowString("from"));
		emailTemplate.subject = stripslashes(connection.getCurrentRowString("subject"));
		emailTemplate.type = EmailTypeType.fromString(connection.getCurrentRowString("type"));
		
		return emailTemplate;
	}

	@Override
	public EmailTemplate addEmailTemplate(EmailTemplate emailTemplate) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	@Override
	public EmailTemplate updateEmailTemplate(EmailTemplate emailTemplate) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteEmailTemplate(EmailTemplate emailTemplate) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

}