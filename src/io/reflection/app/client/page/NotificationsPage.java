//
//  NotificationsPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 8 Dec 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import io.reflection.app.api.core.shared.call.DeleteNotificationsRequest;
import io.reflection.app.api.core.shared.call.DeleteNotificationsResponse;
import io.reflection.app.api.core.shared.call.UpdateNotificationsRequest;
import io.reflection.app.api.core.shared.call.UpdateNotificationsResponse;
import io.reflection.app.api.core.shared.call.event.DeleteNotificationsEventHandler;
import io.reflection.app.api.core.shared.call.event.UpdateNotificationsEventHandler;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.NotificationController;
import io.reflection.app.client.controller.ServiceConstants;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.helper.MarkdownHelper;
import io.reflection.app.client.page.part.MyAccountSidePanel;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.ExpandableCellTableBuilder;
import io.reflection.app.client.part.ExpandableCellTableBuilder.ExpandMultiSelectionModel;
import io.reflection.app.client.part.ExpandableCellTableBuilder.PlaceHolderColumn;
import io.reflection.app.client.part.SimplePager;
import io.reflection.app.client.res.Images;
import io.reflection.app.client.res.Styles;
import io.reflection.app.datatypes.shared.Notification;
import io.reflection.app.datatypes.shared.NotificationStatusType;
import io.reflection.app.datatypes.shared.User;
import io.reflection.app.shared.util.FormattingHelper;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;

/**
 * @author William Shakour (billy1380)
 *
 */
public class NotificationsPage extends Page implements NavigationEventHandler, DeleteNotificationsEventHandler, UpdateNotificationsEventHandler {

	private static NotificationsPageUiBinder uiBinder = GWT.create(NotificationsPageUiBinder.class);

	interface NotificationsPageUiBinder extends UiBinder<Widget, NotificationsPage> {}

	@UiField MyAccountSidePanel myAccountSidePanel;
	@UiField(provided = true) CellTable<Notification> notificationsTable = new CellTable<Notification>(ServiceConstants.SHORT_STEP.intValue(),
			BootstrapGwtCellTable.INSTANCE);
	@UiField(provided = true) SimplePager simplePager = new SimplePager(false, false);
	private User user;
	private TextColumn<Notification> columnCreated;
	private Column<Notification, SafeHtml> columnPriority;
	private Column<Notification, SafeHtml> columnSubject;
	private Column<Notification, SafeHtml> columnBody;
	private TextColumn<Notification> columnFrom;

	public NotificationsPage() {
		initWidget(uiBinder.createAndBindUi(this));

		Styles.INSTANCE.reflection().ensureInjected();

		createColumns();

		notificationsTable.setEmptyTableWidget((new HTMLPanel("<h6>No notifications <i class=\"icon-emo-happy\"></i></h6>")));
		notificationsTable.setLoadingIndicator(new Image(Images.INSTANCE.preloader()));

		NotificationController.get().addDataDisplay(notificationsTable);
		simplePager.setDisplay(notificationsTable);

		PlaceHolderColumn<Notification, SafeHtml> placeholder = new PlaceHolderColumn<Notification, SafeHtml>(new SafeHtmlCell()) {
			@Override
			public SafeHtml getValue(Notification object) {
				return SafeHtmlUtils.fromSafeConstant(getSelected() ? "<i class=\"glyphicon glyphicon-chevron-down\"></i>"
						: "<i class=\"glyphicon glyphicon-chevron-right\"></i>");
			}
		};
		placeholder.setCellStyleNames("text-muted");

		notificationsTable.setTableBuilder(new ExpandableCellTableBuilder<Notification, SafeHtml>(notificationsTable, columnBody, placeholder));
		notificationsTable.getSelectionModel().addSelectionChangeHandler(new Handler() {

			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				@SuppressWarnings("unchecked")
				Notification value = ((ExpandMultiSelectionModel<Notification>) notificationsTable.getSelectionModel()).getLastSelection();

				if (value != null && value.status != NotificationStatusType.NotificationStatusTypeRead) {
					value.status = NotificationStatusType.NotificationStatusTypeRead;
					NotificationController.get().updateNotifications(value);
					notificationsTable.redrawRow(notificationsTable.getVisibleItems().indexOf(value));
				}
			}
		});
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
		columnBody = new Column<Notification, SafeHtml>(new SafeHtmlCell()) {

			@Override
			public SafeHtml getValue(Notification object) {
				return SafeHtmlUtils.fromTrustedString(MarkdownHelper.process(object.body));
			}
		};
		notificationsTable.addColumn(columnBody);

		columnPriority = new Column<Notification, SafeHtml>(new SafeHtmlCell()) {
			@Override
			public SafeHtml getValue(Notification object) {
				SafeHtml priority = null;

				switch (object.priority) {
				case EventPriorityTypeCritical:
					priority = SafeHtmlUtils.fromSafeConstant("<i class=\"glyphicon glyphicon-exclamation-sign\" style=\"color:#ff496a\"></i>");
					break;
				case EventPriorityTypeDebug:
					priority = SafeHtmlUtils.fromSafeConstant("<i class=\"glyphicon glyphicon-minus\" style=\"color:#eee\"></i>");
					break;
				case EventPriorityTypeHigh:
					priority = SafeHtmlUtils.fromSafeConstant("<i class=\"glyphicon glyphicon-bell\" style=\"color:#f8c765\"></i>");
					break;
				case EventPriorityTypeLow:
					priority = SafeHtmlUtils.fromSafeConstant("<i class=\"glyphicon glyphicon-arrow-down\" style=\"color:#ddd\"></i>");
					break;
				case EventPriorityTypeNormal:
					priority = SafeHtmlUtils.fromSafeConstant("<i class=\"glyphicon glyphicon-bell\" style=\"color:#ccc\"></i>");
					break;
				}
				return priority;
			}
		};
		notificationsTable.addColumn(columnPriority);

		columnCreated = new TextColumn<Notification>() {
			@Override
			public String getValue(Notification object) {
				return object.created != null ? FormattingHelper.getTimeSince(object.created) : "-";
			}
		};
		notificationsTable.addColumn(columnCreated);

		columnSubject = new Column<Notification, SafeHtml>(new SafeHtmlCell()) {
			@Override
			public SafeHtml getValue(Notification object) {
				SafeHtmlBuilder builder = new SafeHtmlBuilder();
				if (object.status == NotificationStatusType.NotificationStatusTypeSent) {
					builder.appendHtmlConstant("<strong>");
					builder.appendEscaped(object.subject);
					builder.appendHtmlConstant("</strong>");
				} else {
					builder.appendEscaped(object.subject);
				}

				return builder.toSafeHtml();
			}
		};
		notificationsTable.addColumn(columnSubject, "Subject");

		columnFrom = new TextColumn<Notification>() {
			@Override
			public String getValue(Notification object) {
				return object.from;
			}
		};
		notificationsTable.addColumn(columnFrom, "From");
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
	public void updateNotificationsSuccess(UpdateNotificationsRequest input, UpdateNotificationsResponse output) {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.UpdateNotificationsEventHandler#updateNotificationsFailure(io.reflection.app.api.core.shared.call.
	 * UpdateNotificationsRequest, java.lang.Throwable)
	 */
	@Override
	public void updateNotificationsFailure(UpdateNotificationsRequest input, Throwable caught) {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.DeleteNotificationsEventHandler#deleteNotificationsSuccess(io.reflection.app.api.core.shared.call.
	 * DeleteNotificationsRequest, io.reflection.app.api.core.shared.call.DeleteNotificationsResponse)
	 */
	@Override
	public void deleteNotificationsSuccess(DeleteNotificationsRequest input, DeleteNotificationsResponse output) {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.DeleteNotificationsEventHandler#deleteNotificationsFailure(io.reflection.app.api.core.shared.call.
	 * DeleteNotificationsRequest, java.lang.Throwable)
	 */
	@Override
	public void deleteNotificationsFailure(DeleteNotificationsRequest input, Throwable caught) {}

}
