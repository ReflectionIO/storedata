//
//  NotifyUser.java
//  storedata
//
//  Created by William Shakour (billy1380) on 3 Mar 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.pipeline;

import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.Event;
import io.reflection.app.datatypes.shared.Notification;
import io.reflection.app.datatypes.shared.NotificationTypeType;
import io.reflection.app.datatypes.shared.User;
import io.reflection.app.helpers.NotificationHelper;
import io.reflection.app.service.dataaccount.DataAccountServiceProvider;
import io.reflection.app.service.event.EventServiceProvider;
import io.reflection.app.service.notification.NotificationServiceProvider;
import io.reflection.app.service.user.UserServiceProvider;
import io.reflection.app.shared.util.DataTypeHelper;

import java.util.HashMap;
import java.util.Map;

import com.google.appengine.tools.pipeline.Job2;
import com.google.appengine.tools.pipeline.Value;

/**
 * @author William Shakour (billy1380)
 *
 */
public class NotifyUser extends Job2<Void, Long, Long> {

	private static final long serialVersionUID = 7645918492687402533L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.appengine.tools.pipeline.Job2#run(java.lang.Object)
	 */
	@Override
	public Value<Void> run(Long dataAccountFetchId, Long dataAccountId) throws Exception {
		if (dataAccountFetchId != null) {
			DataAccount account = DataAccountServiceProvider.provide().getDataAccount(dataAccountId);

			if (account != null) {
				Event event = EventServiceProvider.provide().getCodeEvent(DataTypeHelper.NEW_USER_EVENT_CODE);
				User user = UserServiceProvider.provide().getDataAccountOwner(account);

				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("user", user);

				String body = NotificationHelper.inflate(parameters, event.longBody);

				Notification notification = (new Notification()).from("hello@reflection.io").user(user).event(event).body(body).subject(event.subject);
				Notification added = NotificationServiceProvider.provide().addNotification(notification);

				if (added.type != NotificationTypeType.NotificationTypeTypeInternal) {
					notification.type = NotificationTypeType.NotificationTypeTypeInternal;
					NotificationServiceProvider.provide().addNotification(notification);
				}
			}
		}

		return null;
	}

}
