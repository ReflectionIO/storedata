//
//  EmailTemplatePage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 18 Feb 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.ServiceController;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.SimplePager;
import io.reflection.app.datatypes.shared.EmailTemplate;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class EmailTemplatePage extends Page implements NavigationEventHandler {

	private static EmailTemplatePageUiBinder uiBinder = GWT.create(EmailTemplatePageUiBinder.class);

	interface EmailTemplatePageUiBinder extends UiBinder<Widget, EmailTemplatePage> {}

	@UiField(provided = true) CellTable<EmailTemplate> emailTemplates = new CellTable<EmailTemplate>(ServiceController.SHORT_STEP_VALUE,
			BootstrapGwtCellTable.INSTANCE);
	@UiField(provided = true) SimplePager pager = new SimplePager(false, false);

	public EmailTemplatePage() {
		initWidget(uiBinder.createAndBindUi(this));

		addEmailTemplateColumns();
	}

	public void addEmailTemplateColumns() {
		TextColumn<EmailTemplate> idColumn = new TextColumn<EmailTemplate>() {

			@Override
			public String getValue(EmailTemplate object) {
				return object.id.toString();
			}

		};

		TextHeader idHeader = new TextHeader("Id");
		idHeader.setHeaderStyleNames("col-xs-1");
		emailTemplates.addColumn(idColumn, idHeader);

		TextColumn<EmailTemplate> subjectColumn = new TextColumn<EmailTemplate>() {

			@Override
			public String getValue(EmailTemplate object) {
				return object.subject;
			}

		};

		TextHeader subjectHeader = new TextHeader("Subject");
		subjectHeader.setHeaderStyleNames("col-xs-1");
		emailTemplates.addColumn(subjectColumn, subjectHeader);

		TextColumn<EmailTemplate> formatColumn = new TextColumn<EmailTemplate>() {

			@Override
			public String getValue(EmailTemplate object) {
				return object.format.toString();
			}

		};

		TextHeader formatHeader = new TextHeader("Format");
		formatHeader.setHeaderStyleNames("col-xs-1");
		emailTemplates.addColumn(formatColumn, formatHeader);

		TextColumn<EmailTemplate> typeColumn = new TextColumn<EmailTemplate>() {

			@Override
			public String getValue(EmailTemplate object) {
				return object.type.toString();
			}

		};

		TextHeader typeHeader = new TextHeader("Type");
		typeHeader.setHeaderStyleNames("col-xs-1");
		emailTemplates.addColumn(typeColumn, typeHeader);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack stack) {
		if ("emailtemplate".equals(stack.getPage()) && "view".equals(stack.getAction())) {}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		register(EventController.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));
	}

}
