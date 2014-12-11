//
//  NotificationsPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 8 Dec 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import static io.reflection.app.client.helper.FormattingHelper.DATE_FORMAT_DD_MMM_YYYY;
import io.reflection.app.api.core.shared.call.DeleteNotificationsRequest;
import io.reflection.app.api.core.shared.call.DeleteNotificationsResponse;
import io.reflection.app.api.core.shared.call.UpdateNotificationsRequest;
import io.reflection.app.api.core.shared.call.UpdateNotificationsResponse;
import io.reflection.app.api.core.shared.call.event.DeleteNotificationsEventHandler;
import io.reflection.app.api.core.shared.call.event.UpdateNotificationsEventHandler;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.NotificationController;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.page.part.MyAccountSidePanel;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.SimplePager;
import io.reflection.app.client.res.Images;
import io.reflection.app.client.res.Styles;
import io.reflection.app.datatypes.shared.Notification;
import io.reflection.app.datatypes.shared.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author William Shakour (billy1380)
 *
 */
public class NotificationsPage extends Page implements NavigationEventHandler, DeleteNotificationsEventHandler, UpdateNotificationsEventHandler {

	private static NotificationsPageUiBinder uiBinder = GWT.create(NotificationsPageUiBinder.class);

	interface NotificationsPageUiBinder extends UiBinder<Widget, NotificationsPage> {}

	@UiField MyAccountSidePanel myAccountSidePanel;
	@UiField(provided = true) CellTable<Notification> notificationsTable = new CellTable<Notification>(Pager.DEFAULT_COUNT.intValue(),
			BootstrapGwtCellTable.INSTANCE);
	@UiField(provided = true) SimplePager simplePager = new SimplePager(false, false);
	private User user;
	private TextColumn<Notification> columnCreated;
	private TextColumn<Notification> columnPriority;
	private TextColumn<Notification> columnSubject;
	private TextColumn<Notification> columnFrom;

	public NotificationsPage() {
		initWidget(uiBinder.createAndBindUi(this));

		Styles.INSTANCE.reflection().ensureInjected();

		createColumns();

		notificationsTable.setEmptyTableWidget((new HTMLPanel("<h6>No notifications <i class=\"icon-emo-happy\"></i></h6>")));
		notificationsTable.setLoadingIndicator(new Image(Images.INSTANCE.preloader()));

		NotificationController.get().addDataDisplay(notificationsTable);
		simplePager.setDisplay(notificationsTable);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		register(DefaultEventBus.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(DeleteNotificationsEventHandler.TYPE, NotificationController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(UpdateNotificationsEventHandler.TYPE, NotificationController.get(), this));
	}

	private void createColumns() {
		columnPriority = new TextColumn<Notification>() {
			@Override
			public String getValue(Notification object) {
				return object.priority.toString();
			}
		};
		notificationsTable.addColumn(columnPriority);

		columnCreated = new TextColumn<Notification>() {
			@Override
			public String getValue(Notification object) {
				return (object.created != null) ? DATE_FORMAT_DD_MMM_YYYY.format(object.created, null) : "-";
			}
		};
		notificationsTable.addColumn(columnCreated, "date");

		columnSubject = new TextColumn<Notification>() {
			@Override
			public String getValue(Notification object) {
				return object.subject;
			}
		};
		notificationsTable.addColumn(columnSubject, "subject");

		columnFrom = new TextColumn<Notification>() {
			@Override
			public String getValue(Notification object) {
				return object.from;
			}
		};
		notificationsTable.addColumn(columnFrom, "from");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) {
		myAccountSidePanel.setActive(getPageType());

		user = SessionController.get().getLoggedInUser();

		if (user != null) {
			myAccountSidePanel.setUser(user);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.UpdateNotificationsEventHandler#updateNotificationsSuccess(io.reflection.app.api.core.shared.call.
	 * UpdateNotificationsRequest, io.reflection.app.api.core.shared.call.UpdateNotificationsResponse)
	 */
	@Override
	public void updateNotificationsSuccess(UpdateNotificationsRequest input, UpdateNotificationsResponse output) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.UpdateNotificationsEventHandler#updateNotificationsFailure(io.reflection.app.api.core.shared.call.
	 * UpdateNotificationsRequest, java.lang.Throwable)
	 */
	@Override
	public void updateNotificationsFailure(UpdateNotificationsRequest input, Throwable caught) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.DeleteNotificationsEventHandler#deleteNotificationsSuccess(io.reflection.app.api.core.shared.call.
	 * DeleteNotificationsRequest, io.reflection.app.api.core.shared.call.DeleteNotificationsResponse)
	 */
	@Override
	public void deleteNotificationsSuccess(DeleteNotificationsRequest input, DeleteNotificationsResponse output) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.DeleteNotificationsEventHandler#deleteNotificationsFailure(io.reflection.app.api.core.shared.call.
	 * DeleteNotificationsRequest, java.lang.Throwable)
	 */
	@Override
	public void deleteNotificationsFailure(DeleteNotificationsRequest input, Throwable caught) {
		// TODO Auto-generated method stub

	}

}
