//
//  DataAccountGatherServlet.java
//  storedata
//
//  Created by William Shakour on 2 Jan 2014.
//  Copyright © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyright © 2014 reflection.io. All rights reserved.
//
package io.reflection.app;

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

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import com.willshex.gson.json.service.server.ServiceException;
import com.willshex.service.ContextAwareServlet;

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
		String appEngineQueue = REQUEST.get().getHeader("X-AppEngine-QueueName");

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, String.format("appEngineQueue is [%s]", appEngineQueue));
		}

		boolean isNotQueue = false;

		// bail out if we have not been called by app engine cron
		if (isNotQueue = (appEngineQueue == null || !"dataaccountgather".toLowerCase().equals(appEngineQueue.toLowerCase()))) {
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

		String accountIdParameter = REQUEST.get().getParameter("accountId");
		String dateParameter = REQUEST.get().getParameter("date");
		String notifyParameter = REQUEST.get().getParameter("notify");

		if (accountIdParameter != null) {
			try {
				DataAccount account = DataAccountServiceProvider.provide().getDataAccount(Long.valueOf(accountIdParameter));

				if (account != null) {
					Date date;

					if (dateParameter != null) {
						date = new Date(Long.parseLong(dateParameter));
					} else {
						date = new Date();
					}

					DataSource dataSource = DataSourceServiceProvider.provide().getDataSource(account.source.id);

					if (dataSource != null) {
						account.source = dataSource;

						DataAccountCollector collector = DataAccountCollectorFactory.getCollectorForSource(dataSource.a3Code);

						if (collector != null) {
							boolean status = collector.collect(account, date);

							if (status && notifyParameter != null && Boolean.parseBoolean(notifyParameter)) {
								Event event = EventServiceProvider.provide().getCodeEvent(DataTypeHelper.NEW_USER_EVENT_CODE);
								User user = UserServiceProvider.provide().getDataAccountOwner(account);

								Map<String, Object> parameters = new HashMap<String, Object>();
								parameters.put("user", user);

								String body = NotificationHelper.inflate(parameters, event.longBody);

								Notification notification = (new Notification()).from("hello@reflection.io").user(user).event(event).body(body)
										.subject(event.subject);
								Notification added = NotificationServiceProvider.provide().addNotification(notification);

								if (added.type != NotificationTypeType.NotificationTypeTypeInternal) {
									notification.type = NotificationTypeType.NotificationTypeTypeInternal;
									NotificationServiceProvider.provide().addNotification(notification);
								}
							}

						} else {
							if (LOG.isLoggable(GaeLevel.WARNING)) {
								LOG.log(GaeLevel.WARNING, "Could not find a collector for [%s]", dataSource.a3Code);
							}
						}
					} else {
						if (LOG.isLoggable(GaeLevel.WARNING)) {
							LOG.log(GaeLevel.WARNING, "Could not find a data source for id [%d]", account.source.id.longValue());
						}
					}

				}
			} catch (DataAccessException e) {
				LOG.log(GaeLevel.SEVERE, String.format("Database error occured while trying to import data with accountid [%s] and date [%s]",
						accountIdParameter, dateParameter), e);
			} catch (ServiceException e) {
				if (LOG.isLoggable(GaeLevel.INFO)) {
					LOG.log(GaeLevel.INFO, String.format("Service error occured while trying to import data with accountid [%s] and date [%s]",
							accountIdParameter, dateParameter), e);
				}
			}

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
