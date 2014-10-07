//
//  EmailTemplatePage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 18 Feb 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.admin;

import io.reflection.app.api.admin.shared.call.GetEmailTemplatesRequest;
import io.reflection.app.api.admin.shared.call.GetEmailTemplatesResponse;
import io.reflection.app.api.admin.shared.call.UpdateEmailTemplateRequest;
import io.reflection.app.api.admin.shared.call.UpdateEmailTemplateResponse;
import io.reflection.app.api.admin.shared.call.event.GetEmailTemplatesEventHandler;
import io.reflection.app.api.admin.shared.call.event.UpdateEmailTemplateEventHandler;
import io.reflection.app.client.controller.EmailTemplateController;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.ServiceConstants;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.helper.FormHelper;
import io.reflection.app.client.page.Page;
import io.reflection.app.client.page.PageType;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.Preloader;
import io.reflection.app.client.part.SimplePager;
import io.reflection.app.client.part.text.MarkdownEditor;
import io.reflection.app.client.res.Images;
import io.reflection.app.datatypes.shared.EmailFormatType;
import io.reflection.app.datatypes.shared.EmailTemplate;
import io.reflection.app.helpers.EmailHelper;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class EmailTemplatePage extends Page implements NavigationEventHandler, GetEmailTemplatesEventHandler, UpdateEmailTemplateEventHandler {

	public static final int TEMPLATE_EMAIL_ID_PARAMETER = 0;
	public static final String FORMAT_PLAIN_TEXT_VALUE = "plainText";
	public static final String FORMAT_HTML_VALUE = "html";
	public static final int FORMAT_PLAIN_TEXT_INDEX = 0;
	public static final int FORMAT_HTML_INDEX = 1;

	private static EmailTemplatePageUiBinder uiBinder = GWT.create(EmailTemplatePageUiBinder.class);

	interface EmailTemplatePageUiBinder extends UiBinder<Widget, EmailTemplatePage> {}

	@UiField(provided = true) CellTable<EmailTemplate> emailTemplates = new CellTable<EmailTemplate>(ServiceConstants.SHORT_STEP_VALUE,
			BootstrapGwtCellTable.INSTANCE);
	@UiField(provided = true) SimplePager simplePager = new SimplePager(false, false);

	private EmailTemplate emailTemplate;

	@UiField HTMLPanel templateTablePanel;
	@UiField HTMLPanel editTemplatePanel;
	@UiField TextBox fromTextBox;
	@UiField HTMLPanel fromGroup;
	@UiField HTMLPanel fromNote;
	private String fromError = null;
	@UiField TextBox subjectTextBox;
	@UiField HTMLPanel subjectGroup;
	@UiField HTMLPanel subjectNote;
	private String subjectError = null;
	@UiField MarkdownEditor templateHtmlEditor;
	@UiField TextArea templatePlainTextEditor;
	@UiField HTMLPanel bodyGroup;
	@UiField HTMLPanel bodyNote;
	private String bodyError = null;
	private TextArea textArea; // Assign the current used TextArea to this
	@UiField Button addForenameBtn;
	@UiField Button addSurnameBtn;
	@UiField Button addUsernameBtn;
	@UiField Button addCompanyBtn;
	@UiField Button addLinkBtn;
	@UiField ListBox formatListBox;
	@UiField Button buttonUpdate;
	@UiField Preloader preloader;

	public EmailTemplatePage() {
		initWidget(uiBinder.createAndBindUi(this));

		addColumns();

		emailTemplates.setLoadingIndicator(new Image(Images.INSTANCE.preloader()));
		EmailTemplateController.get().addDataDisplay(emailTemplates);
		simplePager.setDisplay(emailTemplates);
	}

	public void addColumns() {
		TextColumn<EmailTemplate> idColumn = new TextColumn<EmailTemplate>() {

			@Override
			public String getValue(EmailTemplate object) {
				return object.id.toString();
			}

		};

		emailTemplates.addColumn(idColumn, "Id");

		TextColumn<EmailTemplate> fromColumn = new TextColumn<EmailTemplate>() {

			@Override
			public String getValue(EmailTemplate object) {
				return object.from;
			}

		};
		emailTemplates.addColumn(fromColumn, "From");

		TextColumn<EmailTemplate> subjectColumn = new TextColumn<EmailTemplate>() {

			@Override
			public String getValue(EmailTemplate object) {
				return object.subject;
			}

		};

		emailTemplates.addColumn(subjectColumn, "Subject");

		TextColumn<EmailTemplate> formatColumn = new TextColumn<EmailTemplate>() {

			@Override
			public String getValue(EmailTemplate object) {
				return object.format.toString();
			}

		};

		emailTemplates.addColumn(formatColumn, "Format");

		TextColumn<EmailTemplate> typeColumn = new TextColumn<EmailTemplate>() {

			@Override
			public String getValue(EmailTemplate object) {
				return object.type.toString();
			}

		};

		emailTemplates.addColumn(typeColumn, "Type");

		Column<EmailTemplate, SafeHtml> editColumn = new Column<EmailTemplate, SafeHtml>(new SafeHtmlCell()) {

			@Override
			public SafeHtml getValue(EmailTemplate object) {
				String id = object.id.toString();
				return SafeHtmlUtils.fromTrustedString("<a href=\""
						+ PageType.EmailTemplatesPageType.asHref(NavigationController.EDIT_ACTION_PARAMETER_VALUE, id).asString() + "\">Edit</a>");
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
		String actionParameter = current.getAction();
		String typeParameter = current.getParameter(TEMPLATE_EMAIL_ID_PARAMETER);
		if (isValidEditStack(actionParameter, typeParameter)) {
			clearErrors();
			emailTemplate = EmailTemplateController.get().getEmailTemplate(Long.valueOf(typeParameter));
			templateTablePanel.setVisible(false);
			editTemplatePanel.setVisible(true);
			fromTextBox.setText(emailTemplate.from);
			subjectTextBox.setText(emailTemplate.subject);
			if (emailTemplate.format == EmailFormatType.EmailFormatTypePlainText) {
				templateHtmlEditor.setVisible(false);
				templatePlainTextEditor.setVisible(true);
				textArea = templatePlainTextEditor;
				formatListBox.setItemSelected(FORMAT_PLAIN_TEXT_INDEX, true);
			} else {
				templatePlainTextEditor.setVisible(false);
				templateHtmlEditor.setVisible(true);
				textArea = templateHtmlEditor.getTextArea();
				formatListBox.setItemSelected(FORMAT_HTML_INDEX, true);
			}
			textArea.setText(emailTemplate.body);

		} else {
			editTemplatePanel.setVisible(false);
			templateTablePanel.setVisible(true);
			PageType.EmailTemplatesPageType.show();
		}
	}

	private boolean isValidEditStack(String actionParameter, String typeParameter) {
		return (actionParameter != null && typeParameter != null && NavigationController.EDIT_ACTION_PARAMETER_VALUE.equals(actionParameter)
				&& typeParameter.matches("[0-9]+") && EmailTemplateController.get().getEmailTemplate(Long.valueOf(typeParameter)) != null) ? true : false;
	}

	@UiHandler("addForenameBtn")
	void onAddForenameClicked(ClickEvent event) {
		appendCode(EmailHelper.USER_FORENAME_CODE);
	}

	@UiHandler("addSurnameBtn")
	void onAddSurnameClicked(ClickEvent event) {
		appendCode(EmailHelper.USER_SURNAME_CODE);
	}

	@UiHandler("addUsernameBtn")
	void onAddUsernameClicked(ClickEvent event) {
		appendCode(EmailHelper.USER_USERNAME_CODE);
	}

	@UiHandler("addCompanyBtn")
	void onAddCompanyClicked(ClickEvent event) {
		appendCode(EmailHelper.USER_COMPANY_CODE);
	}

	@UiHandler("addLinkBtn")
	void onAddLinkClicked(ClickEvent event) {
		appendCode(EmailHelper.LINK_CODE);
	}

	/**
	 * Append embedded email code at the end of the current TextArea text
	 * 
	 * @param code
	 */
	private void appendCode(String code) {
		textArea.setText(textArea.getText() + code);
		textArea.setCursorPos(textArea.getText().length());
	}

	@UiHandler("formatListBox")
	void onFormatSelected(ChangeEvent event) {
		if (FORMAT_PLAIN_TEXT_VALUE.equals(formatListBox.getValue(formatListBox.getSelectedIndex()))) {
			templateHtmlEditor.setVisible(false);
			templatePlainTextEditor.setVisible(true);
			templatePlainTextEditor.setText(textArea.getText());
			textArea = templatePlainTextEditor;
		} else {
			templatePlainTextEditor.setVisible(false);
			templateHtmlEditor.setVisible(true);
			templateHtmlEditor.getTextArea().setText(textArea.getText());
			textArea = templateHtmlEditor.getTextArea();
		}
	}

	@UiHandler("buttonUpdate")
	void onUpdateClicked(ClickEvent event) {
		if (validate()) {
			clearErrors();
			preloader.show();
			emailTemplate.from = fromTextBox.getText();
			emailTemplate.subject = subjectTextBox.getText();
			emailTemplate.id = Long.valueOf(NavigationController.get().getStack().getParameter(TEMPLATE_EMAIL_ID_PARAMETER));
			emailTemplate.body = textArea.getText();
			emailTemplate.format = (formatListBox.getValue(formatListBox.getSelectedIndex()).equals(FORMAT_PLAIN_TEXT_VALUE) ? EmailFormatType.EmailFormatTypePlainText
					: EmailFormatType.EmailFormatTypeHtml);
			EmailTemplateController.get().updateEmailTemplate(emailTemplate);
		} else {
			if (fromError != null) {
				FormHelper.showNote(true, fromGroup, fromNote, fromError);
			} else {
				FormHelper.hideNote(fromGroup, fromNote);
			}
			if (subjectError != null) {
				FormHelper.showNote(true, subjectGroup, subjectNote, subjectError);
			} else {
				FormHelper.hideNote(subjectGroup, subjectNote);
			}
			if (bodyError != null) {
				FormHelper.showNote(true, bodyGroup, bodyNote, bodyError);
			} else {
				FormHelper.hideNote(bodyGroup, bodyNote);
			}
		}
	}

	private boolean validate() {

		boolean validated = true;
		// Retrieve fields to validate
		String from = fromTextBox.getText();
		String subject = subjectTextBox.getText();
		String body = textArea.getText();

		// Check fields constraints
		if (from == null || from.length() == 0) {
			fromError = "Cannot be empty";
			validated = false;
		} else if (!FormHelper.isValidEmail(from)) {
			fromError = "Invalid email address";
			validated = false;
		} else {
			fromError = null;
			validated = validated && true;
		}

		if (subject == null || subject.length() == 0) {
			subjectError = "Cannot be empty";
			validated = false;
		} else {
			subjectError = null;
			validated = validated && true;
		}

		if (body == null || body.length() == 0) {
			bodyError = "Cannot be empty";
			validated = false;
		} else {
			bodyError = null;
			validated = validated && true;
		}

		return validated;
	}

	private void clearErrors() {
		FormHelper.hideNote(fromGroup, fromNote);
		FormHelper.hideNote(subjectGroup, subjectNote);
		FormHelper.hideNote(bodyGroup, bodyNote);
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
		register(EventController.get().addHandlerToSource(GetEmailTemplatesEventHandler.TYPE, EmailTemplateController.get(), this));
		register(EventController.get().addHandlerToSource(UpdateEmailTemplateEventHandler.TYPE, EmailTemplateController.get(), this));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.admin.shared.call.event.GetEmailTemplatesEventHandler#getEmailTemplatesSuccess(io.reflection.app.api.admin.shared.call.
	 * GetEmailTemplatesRequest, io.reflection.app.api.admin.shared.call.GetEmailTemplatesResponse)
	 */
	@Override
	public void getEmailTemplatesSuccess(GetEmailTemplatesRequest input, GetEmailTemplatesResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {
			if (EmailTemplateController.get().getEmailTemplatesCount() > output.pager.count) {
				simplePager.setVisible(true);
			} else {
				simplePager.setVisible(false);
			}
		} else {
			simplePager.setVisible(false);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.admin.shared.call.event.GetEmailTemplatesEventHandler#getEmailTemplatesFailure(io.reflection.app.api.admin.shared.call.
	 * GetEmailTemplatesRequest, java.lang.Throwable)
	 */
	@Override
	public void getEmailTemplatesFailure(GetEmailTemplatesRequest input, Throwable caught) {
		simplePager.setVisible(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.admin.shared.call.event.UpdateEmailTemplateEventHandler#updateEmailTemplateSuccess(io.reflection.app.api.admin.shared.call.
	 * UpdateEmailTemplateRequest, io.reflection.app.api.admin.shared.call.UpdateEmailTemplateResponse)
	 */
	@Override
	public void updateEmailTemplateSuccess(UpdateEmailTemplateRequest input, UpdateEmailTemplateResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {
			PageType.EmailTemplatesPageType.show();
		}
		preloader.hide();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.admin.shared.call.event.UpdateEmailTemplateEventHandler#updateEmailTemplateFailure(io.reflection.app.api.admin.shared.call.
	 * UpdateEmailTemplateRequest, java.lang.Throwable)
	 */
	@Override
	public void updateEmailTemplateFailure(UpdateEmailTemplateRequest input, Throwable caught) {
		preloader.hide();
	}

}
