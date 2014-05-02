//
//  EmailTemplatePage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 18 Feb 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.admin;

import io.reflection.app.client.controller.EmailTemplateController;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.ServiceConstants;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.page.Page;
import io.reflection.app.client.page.PageType;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.SimplePager;
import io.reflection.app.datatypes.shared.EmailTemplate;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
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

	@UiField(provided = true) CellTable<EmailTemplate> emailTemplates = new CellTable<EmailTemplate>(ServiceConstants.SHORT_STEP_VALUE,
			BootstrapGwtCellTable.INSTANCE);
	@UiField(provided = true) SimplePager pager = new SimplePager(false, false);

	public EmailTemplatePage() {
		initWidget(uiBinder.createAndBindUi(this));

		addColumns();

		EmailTemplateController.get().addDataDisplay(emailTemplates);
		pager.setDisplay(emailTemplates);
	}

	public void addColumns() {
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

		SafeHtmlCell prototype = new SafeHtmlCell();
		Column<EmailTemplate, SafeHtml> editColumn = new Column<EmailTemplate, SafeHtml>(prototype) {

			@Override
			public SafeHtml getValue(EmailTemplate object) {
				String s = object.id.toString();

				return SafeHtmlUtils.fromTrustedString("<a href=\"" + PageType.EmailTemplatesPageType.asHref("change", s).asString()
						+ "\" class=\"btn btn-xs btn-default\">Edit</a>");
			}
		};
		emailTemplates.addColumn(editColumn);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) {
		if (PageType.EmailTemplatesPageType.equals(current.getPage()) && "view".equals(current.getAction())) {

		}
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
