//
//  LinkedAccountsPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 17 Dec 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.page;

import io.reflection.app.api.core.shared.call.DeleteLinkedAccountRequest;
import io.reflection.app.api.core.shared.call.DeleteLinkedAccountResponse;
import io.reflection.app.api.core.shared.call.GetLinkedAccountsRequest;
import io.reflection.app.api.core.shared.call.GetLinkedAccountsResponse;
import io.reflection.app.api.core.shared.call.LinkAccountRequest;
import io.reflection.app.api.core.shared.call.LinkAccountResponse;
import io.reflection.app.api.core.shared.call.UpdateLinkedAccountRequest;
import io.reflection.app.api.core.shared.call.UpdateLinkedAccountResponse;
import io.reflection.app.api.core.shared.call.event.DeleteLinkedAccountEventHandler;
import io.reflection.app.api.core.shared.call.event.GetLinkedAccountsEventHandler;
import io.reflection.app.api.core.shared.call.event.LinkAccountEventHandler;
import io.reflection.app.api.core.shared.call.event.UpdateLinkedAccountEventHandler;
import io.reflection.app.api.shared.datatypes.Session;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.controller.LinkedAccountController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.ServiceConstants;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.handler.EnterPressedEventHandler;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.handler.user.SessionEventHandler;
import io.reflection.app.client.helper.FormHelper;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.CircleProgressBar;
import io.reflection.app.client.part.datatypes.MyLinkedAccount;
import io.reflection.app.client.part.linkaccount.IosMacLinkAccountForm;
import io.reflection.app.client.part.linkaccount.LinkableAccountFields;
import io.reflection.app.client.res.Images;
import io.reflection.app.client.res.Styles;
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.User;

import com.google.gson.JsonObject;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.willshex.gson.json.service.shared.Error;
import com.willshex.gson.json.service.shared.StatusType;
import com.willshex.gson.json.shared.Convert;

/**
 * @author billy1380
 * 
 */
public class LinkedAccountsPage extends Page implements NavigationEventHandler, LinkAccountEventHandler, GetLinkedAccountsEventHandler,
		DeleteLinkedAccountEventHandler, UpdateLinkedAccountEventHandler, SessionEventHandler {

	public static final int ACTION_PARAMETER = 1;
	public static final int ACTION_PARAMETER_INDEX = 2;
	public static final String ADD_ACTION_PARAMETER_VALUE = "add";
	public static final String EDIT_ACTION_PARAMETER_VALUE = "edit";
	public static final String DELETE_ACTION_PARAMETER_VALUE = "delete";

	private static LinkedAccountsPageUiBinder uiBinder = GWT.create(LinkedAccountsPageUiBinder.class);

	interface LinkedAccountsPageUiBinder extends UiBinder<Widget, LinkedAccountsPage> {}

	@UiField(provided = true) CellTable<MyLinkedAccount> linkedAccountsTable = new CellTable<MyLinkedAccount>(ServiceConstants.STEP_VALUE,
			BootstrapGwtCellTable.INSTANCE);

	final String buttonAddHtml = "Link Account&nbsp;&nbsp;<img style=\"vertical-align: 1px;\" src=\""
			+ Images.INSTANCE.buttonLinkedAccount().getSafeUri().asString() + "\"/>";
	final String buttonEditHtml = "Save changes";

	private TextColumn<MyLinkedAccount> columnAccountName;
	private TextColumn<MyLinkedAccount> columnDateAdded;
	private Column<MyLinkedAccount, SafeHtml> columnEdit;
	private Column<MyLinkedAccount, SafeHtml> columnDelete;
	private Column<MyLinkedAccount, SafeHtml> columnExpand;

	// Add linked account elements
	@UiField HTMLPanel linkedAccountForm;
	@UiField InlineHyperlink mIosMacLink;
	@UiField InlineHyperlink mPlayLink;
	@UiField InlineHyperlink mAmazonLink;
	@UiField InlineHyperlink mWindowsPhoneLink;
	@UiField FormPanel mForm;
	@UiField HTMLPanel mToolbar;
	@UiField IosMacLinkAccountForm mIosMacForm;
	@UiField Button mLinkAccount;
	@UiField InlineHyperlink mMyAppsLink;
	@UiField InlineHyperlink mLinkedAccountsLink;
	@UiField Button addLinkedAccount;

	@UiField HTMLPanel linkedAccountsPanel;

	@UiField CircleProgressBar loader;

	private LinkableAccountFields mLinkableAccount;

	public LinkedAccountsPage() {
		initWidget(uiBinder.createAndBindUi(this));

		User user = SessionController.get().getLoggedInUser();

		if (user != null) {
			String userIdParameter = user.id.toString();

			createColumns(user.id.toString());
			mLinkedAccountsLink.setTargetHistoryToken(PageType.UsersPageType.asTargetHistoryToken(PageType.LinkedAccountsPageType.toString(userIdParameter)));
			mMyAppsLink.setTargetHistoryToken(PageType.UsersPageType.asTargetHistoryToken(PageType.MyAppsPageType.toString(userIdParameter, FilterController
					.get().asMyAppsFilterString())));

		}

		addLinkedAccount.setHTML("Link Another Account&nbsp;&nbsp;<img style=\"vertical-align: 1px;\" src=\""
				+ Images.INSTANCE.buttonLinkedAccount().getSafeUri().asString() + "\"/>");

		addSoonTag(mPlayLink);

		LinkedAccountController.get().addDataDisplay(linkedAccountsTable);
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
		register(EventController.get().addHandlerToSource(LinkAccountEventHandler.TYPE, LinkedAccountController.get(), this));
		register(EventController.get().addHandlerToSource(GetLinkedAccountsEventHandler.TYPE, LinkedAccountController.get(), this));
		register(EventController.get().addHandlerToSource(DeleteLinkedAccountEventHandler.TYPE, LinkedAccountController.get(), this));
		register(EventController.get().addHandlerToSource(UpdateLinkedAccountEventHandler.TYPE, LinkedAccountController.get(), this));
		register(EventController.get().addHandlerToSource(SessionEventHandler.TYPE, SessionController.get(), this));
	}

	/**
	 * Create and add the Linked Account Table columns and cells
	 */
	private void createColumns(final String userId) {

		columnAccountName = new TextColumn<MyLinkedAccount>() {
			@Override
			public String getValue(MyLinkedAccount object) {
				return (object.source != null) ? object.source.name : "-";
			}
		};
		linkedAccountsTable.addColumn(columnAccountName, "Account Name");

		columnDateAdded = new TextColumn<MyLinkedAccount>() {
			@Override
			public String getValue(MyLinkedAccount object) {
				DateTimeFormat dtf = DateTimeFormat.getFormat("dd/MM/yy");
				return (object.source.created != null) ? dtf.format(object.source.created, null) : "-";
			}
		};
		linkedAccountsTable.addColumn(columnDateAdded, "Date Added");

		columnEdit = new Column<MyLinkedAccount, SafeHtml>(new SafeHtmlCell()) {
			@Override
			public SafeHtml getValue(MyLinkedAccount object) {
				Anchor a = new Anchor();
				a.setHref(PageType.UsersPageType.asHref(
						PageType.LinkedAccountsPageType.toString(userId, EDIT_ACTION_PARAMETER_VALUE, object.dataAccount.id.toString())).asString());
				a.setText("Edit");
				a.setStyleName("invisible");
				SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
				safeHtmlBuilder.appendHtmlConstant(a.toString());
				return safeHtmlBuilder.toSafeHtml();
			}

		};
		linkedAccountsTable.addColumn(columnEdit, "");

		columnDelete = new Column<MyLinkedAccount, SafeHtml>(new SafeHtmlCell()) {
			@Override
			public SafeHtml getValue(MyLinkedAccount object) {
				Anchor a = new Anchor();
				a.setHref(PageType.UsersPageType.asHref(
						PageType.LinkedAccountsPageType.toString(userId, DELETE_ACTION_PARAMETER_VALUE, object.dataAccount.id.toString())).asString());
				a.setText("Delete");
				a.setStyleName("invisible");
				SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
				safeHtmlBuilder.appendHtmlConstant(a.toString());
				return safeHtmlBuilder.toSafeHtml();
			}

		};
		linkedAccountsTable.addColumn(columnDelete, "");

		columnExpand = new Column<MyLinkedAccount, SafeHtml>(new SafeHtmlCell()) {
			@Override
			public SafeHtml getValue(MyLinkedAccount object) {
				Anchor a = new Anchor();
				a.setStyleName(Styles.INSTANCE.reflection().linkedAccountPlus());
				SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
				safeHtmlBuilder.appendHtmlConstant(a.toString());
				return safeHtmlBuilder.toSafeHtml();
			}
		};
		linkedAccountsTable.addColumn(columnExpand, "");

	}

	/**
	 * Click on expand cell and manage the visibility of the edit and delete elements on the same row
	 * 
	 * @param event
	 */
	@UiHandler("linkedAccountsTable")
	void cellTableEvent(CellPreviewEvent<MyLinkedAccount> event) {
		if ("click".equals(event.getNativeEvent().getType()) && event.getColumn() == 4) {
			// add visibility hidden in cell
			Element expandElement = linkedAccountsTable.getRowElement(event.getIndex()).getCells().getItem(4).getElementsByTagName("a").getItem(0);
			Element editElement = linkedAccountsTable.getRowElement(event.getIndex()).getCells().getItem(2).getElementsByTagName("a").getItem(0);
			Element deleteElement = linkedAccountsTable.getRowElement(event.getIndex()).getCells().getItem(3).getElementsByTagName("a").getItem(0);

			if ("show".equals(editElement.getClassName())) {
				editElement.setClassName("invisible");
				deleteElement.setClassName("invisible");
				expandElement.removeClassName(Styles.INSTANCE.reflection().linkedAccountMinus());
				expandElement.addClassName(Styles.INSTANCE.reflection().linkedAccountPlus());
				// Use sprite for expander
			} else {
				editElement.setClassName("show");
				deleteElement.setClassName("show");
				expandElement.removeClassName(Styles.INSTANCE.reflection().linkedAccountPlus());
				expandElement.addClassName(Styles.INSTANCE.reflection().linkedAccountMinus());
			}
		}
	}

	@UiHandler("addLinkedAccount")
	void onAddLinkedAccountClicked(ClickEvent event) {
		User user = SessionController.get().getLoggedInUser();
		PageType.UsersPageType.show(PageType.LinkedAccountsPageType.toString(user.id.toString(), ADD_ACTION_PARAMETER_VALUE));
	}

	/**
	 * @param link
	 */
	private void addSoonTag(InlineHyperlink link) {
		SpanElement s = DOM.createSpan().cast();

		s.setInnerText("Coming Soon!");

		s.addClassName("label");
		s.addClassName("label-danger");

		s.getStyle().setMarginLeft(10.0, Unit.PX);

		link.getElement().appendChild(s);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) {
		mLinkableAccount = null;

		String actionParameter = current.getParameter(ACTION_PARAMETER);

		if (ADD_ACTION_PARAMETER_VALUE.equals(actionParameter)) {
			String typeParameter = current.getParameter(ACTION_PARAMETER_INDEX);

			linkedAccountsPanel.setVisible(false);
			linkedAccountForm.setVisible(true);
			mToolbar.setVisible(true);
			mLinkAccount.setHTML(buttonAddHtml);
			mForm.setVisible(false);
			mIosMacForm.resetForm();

			if (typeParameter == null) {
				mIosMacLink.setTargetHistoryToken(current.toString("iosmac"));
			}

			if ("iosmac".equals(typeParameter)) {
				mForm.setVisible(true);
				mLinkableAccount = mIosMacForm;

				mLinkableAccount.setOnEnterPressed(new EnterPressedEventHandler() {
					public void onEnterPressed() {
						mLinkAccount.click();
					}
				});

				mIosMacForm.setVisible(true);
				mLinkableAccount.getFirstToFocus().setFocus(true);
			} else {
				mIosMacForm.setVisible(false);
				mForm.setVisible(false);
			}

		} else if (EDIT_ACTION_PARAMETER_VALUE.equals(actionParameter)) {
			String idParameter = current.getParameter(ACTION_PARAMETER_INDEX);
			Long linkedAccountId = null;

			if (idParameter != null) {
				linkedAccountId = Long.valueOf(idParameter);
			}

			linkedAccountsPanel.setVisible(false);
			linkedAccountForm.setVisible(true);

			mToolbar.setVisible(false);
			mLinkAccount.setHTML(buttonEditHtml);
			mForm.setVisible(true);
			mIosMacForm.setAccountUsernameEnabled(false);
			mLinkableAccount = mIosMacForm;
			mLinkableAccount.setOnEnterPressed(new EnterPressedEventHandler() {
				public void onEnterPressed() {
					mLinkAccount.click();
				}
			});
			mLinkableAccount.getFirstToFocus().setFocus(true);

			if (linkedAccountId != null) {
				DataAccount linkedAccount = LinkedAccountController.get().getLinkedAccount(linkedAccountId);

				if (linkedAccount != null) {
					mIosMacForm.setAccountUsername(linkedAccount.username);
					JsonObject propertiesJson = Convert.toJsonObject(linkedAccount.properties);
					mIosMacForm.setVendorNumber(propertiesJson.get("vendors").getAsString());
				}
			}

		} else if (DELETE_ACTION_PARAMETER_VALUE.equals(actionParameter)) {
			String idParameter = current.getParameter(ACTION_PARAMETER_INDEX);
			Long linkedAccountId = null;

			if (idParameter != null) {
				linkedAccountId = Long.valueOf(idParameter);
			}

			if (linkedAccountId != null) {
				loader.setVisible(true);

				LinkedAccountController.get().deleteLinkedAccount(LinkedAccountController.get().getLinkedAccount(linkedAccountId));
			}

		} else {
			linkedAccountForm.setVisible(false);
			linkedAccountsPanel.setVisible(true);
			LinkedAccountController.get().showLinkedAccounts();
		}

	}

	@UiHandler("mLinkAccount")
	void onLinkAccount(ClickEvent event) {
		if (mLinkableAccount.validate()) {

			mLinkableAccount.setFormErrors();
			mLinkableAccount.setEnabled(false);
			mLinkAccount.setEnabled(false);
			loader.setVisible(true);

			if (EDIT_ACTION_PARAMETER_VALUE.equals(NavigationController.get().getStack().getParameter(ACTION_PARAMETER))) {
				LinkedAccountController.get().updateLinkedAccont(Long.valueOf(NavigationController.get().getStack().getParameter(ACTION_PARAMETER_INDEX)),
						mLinkableAccount.getPassword(), mLinkableAccount.getProperties());
			} else { // Add linked account
				LinkedAccountController.get().linkAccount(mLinkableAccount.getAccountSourceId(), mLinkableAccount.getUsername(),
						mLinkableAccount.getPassword(), mLinkableAccount.getProperties());
			}
		} else {
			mLinkableAccount.setFormErrors();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.LinkAccountEventHandler# linkAccountSuccess (io.reflection.app.api.core.shared.call.LinkAccountRequest,
	 * io.reflection.app.api.core.shared.call.LinkAccountResponse)
	 */
	@Override
	public void linkAccountSuccess(LinkAccountRequest input, LinkAccountResponse output) {
		loader.setVisible(false);

		mLinkableAccount.resetForm();
		mLinkableAccount.setEnabled(true);
		mLinkAccount.setEnabled(true);

		if (output.status == StatusType.StatusTypeSuccess) {
			User user = SessionController.get().getLoggedInUser();

			PageType.UsersPageType.show(PageType.LinkedAccountsPageType.toString(user.id.toString()));
		} else {
			showError(output.error);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.LinkAccountEventHandler# linkAccountFailure (io.reflection.app.api.core.shared.call.LinkAccountRequest,
	 * java.lang.Throwable)
	 */
	@Override
	public void linkAccountFailure(LinkAccountRequest input, Throwable caught) {
		loader.setVisible(false);
		mForm.setVisible(true);
		mLinkableAccount.resetForm();
		mLinkableAccount.setEnabled(true);
		mLinkAccount.setEnabled(true);
		showError(FormHelper.convertToError(caught));
	}

	private void showError(Error e) {

		mForm.setVisible(true);
		mToolbar.setVisible(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.DeleteLinkedAccountEventHandler#deleteLinkedAccountSuccess(io.reflection.app.api.core.shared.call.
	 * DeleteLinkedAccountRequest, io.reflection.app.api.core.shared.call.DeleteLinkedAccountResponse)
	 */
	@Override
	public void deleteLinkedAccountSuccess(DeleteLinkedAccountRequest input, DeleteLinkedAccountResponse output) {
		loader.setVisible(false);
		if (output.status == StatusType.StatusTypeSuccess) {
			User user = SessionController.get().getLoggedInUser();

			PageType.UsersPageType.show(PageType.LinkedAccountsPageType.toString(user.id.toString()));

		} else {
			showError(output.error);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.DeleteLinkedAccountEventHandler#deleteLinkedAccountFailure(io.reflection.app.api.core.shared.call.
	 * DeleteLinkedAccountRequest, java.lang.Throwable)
	 */
	@Override
	public void deleteLinkedAccountFailure(DeleteLinkedAccountRequest input, Throwable caught) {
		loader.setVisible(false);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.UpdateLinkedAccountEventHandler#updateLinkedAccountSuccess(io.reflection.app.api.core.shared.call.
	 * UpdateLinkedAccountRequest, io.reflection.app.api.core.shared.call.UpdateLinkedAccountResponse)
	 */
	@Override
	public void updateLinkedAccountSuccess(UpdateLinkedAccountRequest input, UpdateLinkedAccountResponse output) {
		loader.setVisible(false);
		mLinkableAccount.setEnabled(true);
		mLinkAccount.setEnabled(true);

		if (output.status == StatusType.StatusTypeSuccess) {
			mLinkableAccount.resetForm();
			User user = SessionController.get().getLoggedInUser();
			PageType.UsersPageType.show(PageType.LinkedAccountsPageType.toString(user.id.toString()));
		} else {
			showError(output.error);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.UpdateLinkedAccountEventHandler#updateLinkedAccountFailure(io.reflection.app.api.core.shared.call.
	 * UpdateLinkedAccountRequest, java.lang.Throwable)
	 */
	@Override
	public void updateLinkedAccountFailure(UpdateLinkedAccountRequest input, Throwable caught) {
		loader.setVisible(false);
		mLinkableAccount.setEnabled(true);
		mLinkAccount.setEnabled(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.GetLinkedAccountsEventHandler #getLinkedAccountsSuccess(io.reflection.app.api.core.shared.call.
	 * GetLinkedAccountsRequest, io.reflection.app.api.core.shared.call.GetLinkedAccountsResponse)
	 */
	@Override
	public void getLinkedAccountsSuccess(GetLinkedAccountsRequest input, GetLinkedAccountsResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {
			if (LinkedAccountController.get().getLinkedAccountsCount() == 0) {

			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.GetLinkedAccountsEventHandler #getLinkedAccountsFailure(io.reflection.app.api.core.shared.call.
	 * GetLinkedAccountsRequest, java.lang.Throwable)
	 */
	@Override
	public void getLinkedAccountsFailure(GetLinkedAccountsRequest input, Throwable caught) {
		if (LinkedAccountController.get().getLinkedAccounts() == null || LinkedAccountController.get().getLinkedAccounts().size() == 0) {

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.SessionEventHandler#userLoggedIn(io.reflection.app.datatypes.shared.User,
	 * io.reflection.app.api.shared.datatypes.Session)
	 */
	@Override
	public void userLoggedIn(User user, Session session) {
		// TODO Add My Apps Link

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.SessionEventHandler#userLoggedOut()
	 */
	@Override
	public void userLoggedOut() {
		LinkedAccountController.get().reset();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.SessionEventHandler#userLoginFailed(com.willshex.gson.json.service.shared.Error)
	 */
	@Override
	public void userLoginFailed(Error error) {

	}
}
