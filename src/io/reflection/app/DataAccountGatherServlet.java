//
//  DataAccountGatherServlet.java
//  storedata
//
//  Created by William Shakour on 2 Jan 2014.
//  Copyright © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyright © 2014 reflection.io. All rights reserved.
//
package io.reflection.app;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import com.willshex.gson.json.service.server.ServiceException;
import com.willshex.service.ContextAwareServlet;

import io.reflection.app.accountdatacollectors.DataAccountCollector;
import io.reflection.app.accountdatacollectors.DataAccountCollectorFactory;
import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.DataSource;
import io.reflection.app.datatypes.shared.Event;
import io.reflection.app.datatypes.shared.Notification;
import io.reflection.app.datatypes.shared.NotificationTypeType;
import io.reflection.app.datatypes.shared.User;
import io.reflection.app.helpers.NotificationHelper;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.service.dataaccount.DataAccountServiceProvider;
import io.reflection.app.service.datasource.DataSourceServiceProvider;
import io.reflection.app.service.event.EventServiceProvider;
import io.reflection.app.service.notification.NotificationServiceProvider;
import io.reflection.app.service.user.UserServiceProvider;
import io.reflection.app.shared.util.DataTypeHelper;

/**
 * @author William Shakour
 *
 */
@SuppressWarnings("serial")
public class DataAccountGatherServlet extends ContextAwareServlet {
	private static final Logger LOG = Logger.getLogger(DataAccountGatherServlet.class.getName());

	/*
	 * (non-Javadoc)
	 *
	 * @see com.willshex.service.ContextAwareServlet#doGet()
	 */
	@Override
	protected void doGet() throws ServletException, IOException {
		final String appEngineQueue = REQUEST.get().getHeader("X-AppEngine-QueueName");

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, String.format("appEngineQueue is [%s]", appEngineQueue));
		}

		boolean isNotQueue = false;

		// bail out if we have not been called by app engine cron or the deferred queue
		if (isNotQueue = appEngineQueue == null || (!"deferred".toLowerCase().equals(appEngineQueue.toLowerCase())
				&& !"dataaccountgather".toLowerCase().equals(appEngineQueue.toLowerCase()))) {
			RESPONSE.get().setStatus(401);
			RESPONSE.get().getOutputStream().print("failure");
			LOG.log(Level.WARNING, "Attempt to run script directly, this is not permitted");
			return;
		}

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			if (!isNotQueue) {
				LOG.log(GaeLevel.DEBUG, String.format("Servelet is being called from [%s] queue", appEngineQueue));
			}
		}

		final String accountIdParameter = REQUEST.get().getParameter("accountId");
		final String dateParameter = REQUEST.get().getParameter("date");
		final String notifyParameter = REQUEST.get().getParameter("notify");

		if (accountIdParameter == null) {
			LOG.log(Level.WARNING, String.format("Account id parameter is null"));

			return;
		}

		try {
			final DataAccount account = DataAccountServiceProvider.provide().getDataAccount(Long.valueOf(accountIdParameter));

			if (account == null) {
				if (LOG.isLoggable(Level.WARNING)) {
					LOG.log(Level.WARNING, "Could not find the data account for [%d]", accountIdParameter);
				}

				return;
			}

			Date date;
			if (dateParameter != null) {
				date = new Date(Long.parseLong(dateParameter));
				LOG.log(GaeLevel.DEBUG, String.format("Parsed the date parameter %s and got date %s", dateParameter, date));
			} else {
				date = new Date();
				LOG.log(GaeLevel.DEBUG, String.format("Date parameter was null so setting the date to today"));
			}

			final DataSource dataSource = DataSourceServiceProvider.provide().getDataSource(account.source.id);

			if (dataSource == null) {
				if (LOG.isLoggable(Level.WARNING)) {
					LOG.log(Level.WARNING, "Could not find a datasource for [%s]", account.source.id);
				}

				return;
			}

			account.source = dataSource;

			final DataAccountCollector collector = DataAccountCollectorFactory.getCollectorForSource(dataSource.a3Code);

			if (collector == null) {
				if (LOG.isLoggable(Level.WARNING)) {
					LOG.log(Level.WARNING, "Could not find a collector for [%s]", dataSource.a3Code);
				}

				return;
			}

			final boolean status = collector.collect(account, date);

			if (status && notifyParameter != null && Boolean.parseBoolean(notifyParameter)) {
				final Event event = EventServiceProvider.provide().getCodeEvent(DataTypeHelper.NEW_USER_EVENT_CODE);
				final User user = UserServiceProvider.provide().getDataAccountOwner(account);

				final Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("user", user);

				final String body = NotificationHelper.inflate(parameters, event.longBody);

				final Notification notification = new Notification().from("hello@reflection.io").user(user).event(event).body(body)
						.subject(event.subject);
				final Notification added = NotificationServiceProvider.provide().addNotification(notification);

				if (added.type != NotificationTypeType.NotificationTypeTypeInternal) {
					notification.type = NotificationTypeType.NotificationTypeTypeInternal;
					NotificationServiceProvider.provide().addNotification(notification);
				}
			}

		} catch (final DataAccessException e) {
			LOG.log(Level.SEVERE, String.format("Database error occured while trying to import data with accountid [%s] and date [%s]",
					accountIdParameter, dateParameter), e);
		} catch (final ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.willshex.service.ContextAwareServlet#doPost()
	 */
	@Override
	protected void doPost() throws ServletException, IOException {
		doGet();
	}

}
