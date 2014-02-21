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
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.datatypes.shared.EmailFormatType;
import io.reflection.app.datatypes.shared.EmailTemplate;
import io.reflection.app.datatypes.shared.EmailTypeType;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;

import java.util.ArrayList;
import java.util.List;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.emailtemplate.IEmailTemplateService#getEmailTemplates(io.reflection.app.api.shared.datatypes.Pager)
	 */
	@Override
	public List<EmailTemplate> getEmailTemplates(Pager pager) throws DataAccessException {
		List<EmailTemplate> emailTemplates = new ArrayList<EmailTemplate>();

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection emailTemplateConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeEmailTemplate.toString());

		String getEmailTemplateIdsQuery = "SELECT * FROM `emailtemplate` WHERE `deleted`='n'";

		if (pager != null) {
			String sortByQuery = "id";

			if (pager.sortBy != null && ("type".equals(pager.sortBy) || "subject".equals(pager.sortBy))) {
				sortByQuery = pager.sortBy;
			}

			String sortDirectionQuery = "DESC";

			if (pager.sortDirection != null) {
				switch (pager.sortDirection) {
				case SortDirectionTypeAscending:
					sortDirectionQuery = "ASC";
					break;
				default:
					break;
				}
			}

			getEmailTemplateIdsQuery += String.format(" ORDER BY `%s` %s", sortByQuery, sortDirectionQuery);
		}

		if (pager.start != null && pager.count != null) {
			getEmailTemplateIdsQuery += String.format(" LIMIT %d, %d", pager.start.longValue(), pager.count.longValue());
		} else if (pager.count != null) {
			getEmailTemplateIdsQuery += String.format(" LIMIT %d", pager.count.longValue());
		}

		try {
			emailTemplateConnection.connect();
			emailTemplateConnection.executeQuery(getEmailTemplateIdsQuery);

			while (emailTemplateConnection.fetchNextRow()) {
				EmailTemplate emailTemplate = this.toEmailTemplate(emailTemplateConnection);

				if (emailTemplate != null) {
					emailTemplates.add(emailTemplate);
				}
			}
		} finally {
			if (emailTemplateConnection != null) {
				emailTemplateConnection.disconnect();
			}
		}

		return emailTemplates;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.emailtemplate.IEmailTemplateService#getEmailTemplatesCount()
	 */
	@Override
	public Long getEmailTemplatesCount() throws DataAccessException {
		Long emailTemplatesCount = Long.valueOf(0);

		Connection emailTemplateConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeEmailTemplate.toString());

		String getEmailTemplatesCountQuery = "SELECT count(1) AS `emailTemplatecount` FROM `emailtemplate` WHERE `deleted`='n'";

		try {
			emailTemplateConnection.connect();
			emailTemplateConnection.executeQuery(getEmailTemplatesCountQuery);

			if (emailTemplateConnection.fetchNextRow()) {
				emailTemplatesCount = emailTemplateConnection.getCurrentRowLong("emailTemplatecount");
			}
		} finally {
			if (emailTemplateConnection != null) {
				emailTemplateConnection.disconnect();
			}
		}

		return emailTemplatesCount;
	}

}