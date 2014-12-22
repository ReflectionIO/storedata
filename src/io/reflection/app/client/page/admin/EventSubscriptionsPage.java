//
//  EventSubscriptionsPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 11 Dec 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.admin;

import io.reflection.app.client.cell.StyledButtonCell;
import io.reflection.app.client.controller.EventSubscriptionController;
import io.reflection.app.client.controller.ServiceConstants;
import io.reflection.app.client.helper.FormattingHelper;
import io.reflection.app.client.page.Page;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.SimplePager;
import io.reflection.app.client.res.Images;
import io.reflection.app.datatypes.shared.EventSubscription;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author William Shakour (billy1380)
 *
 */
public class EventSubscriptionsPage extends Page {

	private static EventSubscriptionsPageUiBinder uiBinder = GWT.create(EventSubscriptionsPageUiBinder.class);

	interface EventSubscriptionsPageUiBinder extends UiBinder<Widget, EventSubscriptionsPage> {}

	@UiField(provided = true) CellTable<EventSubscription> eventSubscriptions = new CellTable<EventSubscription>(ServiceConstants.SHORT_STEP_VALUE,
			BootstrapGwtCellTable.INSTANCE);
	@UiField(provided = true) SimplePager simplePager = new SimplePager(false, false);

	public EventSubscriptionsPage() {
		initWidget(uiBinder.createAndBindUi(this));

		addColumns();

		eventSubscriptions.setLoadingIndicator(new Image(Images.INSTANCE.preloader()));
		eventSubscriptions.setEmptyTableWidget(new HTMLPanel("<h6>No Event Subscriptions</h6>"));
		EventSubscriptionController.get().addDataDisplay(eventSubscriptions);
		simplePager.setDisplay(eventSubscriptions);
	}

	private void addColumns() {
		TextColumn<EventSubscription> idColumn = new TextColumn<EventSubscription>() {

			@Override
			public String getValue(EventSubscription object) {
				return object.id.toString();
			}
		};
		eventSubscriptions.addColumn(idColumn, "Id");

		TextColumn<EventSubscription> createdColumn = new TextColumn<EventSubscription>() {

			@Override
			public String getValue(EventSubscription object) {
				return FormattingHelper.DATE_FORMAT_DD_MMM_YYYY_HH_MM.format(object.created);
			}
		};
		eventSubscriptions.addColumn(createdColumn, "Created");

		TextColumn<EventSubscription> userId = new TextColumn<EventSubscription>() {

			@Override
			public String getValue(EventSubscription object) {
				return object.user.id.toString();
			}
		};
		eventSubscriptions.addColumn(userId, "User Id");

		TextColumn<EventSubscription> eventId = new TextColumn<EventSubscription>() {

			@Override
			public String getValue(EventSubscription object) {
				return object.event.id.toString();
			}
		};
		eventSubscriptions.addColumn(eventId, "Event Id");

		TextColumn<EventSubscription> evesDroppingId = new TextColumn<EventSubscription>() {
			@Override
			public String getValue(EventSubscription object) {
				return object.eavesDropping == null ? object.eavesDropping.id.toString() : "None";
			}
		};
		eventSubscriptions.addColumn(evesDroppingId, "Eavesdropping Id");

		TextColumn<EventSubscription> email = new TextColumn<EventSubscription>() {
			@Override
			public String getValue(EventSubscription object) {
				return object.email.toString();
			}
		};
		eventSubscriptions.addColumn(email, "E-mail");

		TextColumn<EventSubscription> notificationCenter = new TextColumn<EventSubscription>() {
			@Override
			public String getValue(EventSubscription object) {
				return object.notificationCenter.toString();
			}
		};
		eventSubscriptions.addColumn(notificationCenter, "Notification Center");

		StyledButtonCell prototype = new StyledButtonCell("btn", "btn-xs", "btn-danger");
		Column<EventSubscription, String> deleteColumn = new Column<EventSubscription, String>(prototype) {

			@Override
			public String getValue(EventSubscription object) {
				return "Delete";
			}
		};
		deleteColumn.setFieldUpdater(new FieldUpdater<EventSubscription, String>() {

			@Override
			public void update(int index, EventSubscription object, String value) {
				EventSubscriptionController.get().deleteEventSubscriptions(object.id);
			}
		});
		eventSubscriptions.addColumn(deleteColumn);

	}

}
