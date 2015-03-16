//
//  LinkedAccountsPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 17 Dec 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.page;

import static io.reflection.app.client.helper.FormattingHelper.DATE_FORMATTER_DD_MMM_YYYY;
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
import io.reflection.app.api.shared.ApiError;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.component.FormButton;
import io.reflection.app.client.controller.LinkedAccountController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.handler.EnterPressedEventHandler;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.helper.FormHelper;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.ConfirmationDialog;
import io.reflection.app.client.part.Preloader;
import io.reflection.app.client.part.linkaccount.IosMacLinkAccountForm;
import io.reflection.app.client.part.linkaccount.LinkableAccountFields;
import io.reflection.app.client.part.linkaccount.LinkedAccountsEmptyTable;
import io.reflection.app.client.res.Images;
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.User;

import com.google.gson.JsonObject;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
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
		DeleteLinkedAccountEventHandler, UpdateLinkedAccountEventHandler {

	public static final int ACTION_PARAMETER = 1;
	public static final int ACTION_PARAMETER_INDEX = 2;

	private static LinkedAccountsPageUiBinder uiBinder = GWT.create(LinkedAccountsPageUiBinder.class);

	interface LinkedAccountsPageUiBinder extends UiBinder<Widget, LinkedAccountsPage> {}

	@UiField(provided = true) CellTable<DataAccount> linkedAccountsTable = new CellTable<DataAccount>(Integer.MAX_VALUE, BootstrapGwtCellTable.INSTANCE);
	// @UiField SimplePager simplePager;

	final String buttonAddHtml = "Link Account";
	final String buttonEditHtml = "Save changes";

	private TextColumn<DataAccount> columnAccountName;
	private TextColumn<DataAccount> columnAccountType;
	private TextColumn<DataAccount> columnDateAdded;
	private Column<DataAccount, SafeHtml> columnEdit;
	private Column<DataAccount, SafeHtml> columnDelete;
	// private Column<DataAccount, SafeHtml> columnExpand;
	private FormButton linkAccountBtn;
	// Add linked account elements
	@UiField HTMLPanel linkedAccountForm;

	// @UiField HTMLPanel mToolbar;
	// @UiField InlineHyperlink mIosMacLink;
	// @UiField InlineHyperlink mPlayLink;
	// @UiField InlineHyperlink mAmazonLink;
	// @UiField InlineHyperlink mWindowsPhoneLink;

	@UiField InlineHyperlink backLink;

	@UiField FormPanel form;

	@UiField IosMacLinkAccountForm iosMacForm;
	@UiField Button addLinkedAccount;

	@UiField HTMLPanel linkedAccountsPanel;

	private User user = SessionController.get().getLoggedInUser();

	private LinkableAccountFields linkableAccount;

	private ConfirmationDialog confirmationDialog;

	@UiField Preloader preloaderTable;
	@UiField Preloader preloaderForm;

	public LinkedAccountsPage() {
		initWidget(uiBinder.createAndBindUi(this));

		createColumns();

		// addSoonTag(mPlayLink);

		linkedAccountsTable.setEmptyTableWidget(new LinkedAccountsEmptyTable());
		LinkedAccountController.get().addDataDisplay(linkedAccountsTable);
		linkedAccountsTable.setLoadingIndicator(new Image(Images.INSTANCE.preloader()));
		// simplePager.setDisplay(linkedAccountsTable);

		// preloaderForm.show();
		// preloaderTable.show();

		linkAccountBtn = iosMacForm.getButton();
		linkAccountBtn.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (linkableAccount.validate()) {

					linkableAccount.setFormErrors();
					// mLinkableAccount.setEnabled(false);
					// mLinkAccount.setEnabled(false);
					preloaderForm.show();
					if (NavigationController.EDIT_ACTION_PARAMETER_VALUE.equals(NavigationController.get().getStack().getParameter(ACTION_PARAMETER))) {
						preloaderForm.show();
						LinkedAccountController.get().updateLinkedAccont(
								Long.valueOf(NavigationController.get().getStack().getParameter(ACTION_PARAMETER_INDEX)), linkableAccount.getPassword(),
								linkableAccount.getProperties());
					} else { // Add linked account
						LinkedAccountController.get().linkAccount(linkableAccount.getAccountSourceId(), linkableAccount.getUsername(),
								linkableAccount.getPassword(), linkableAccount.getProperties());
					}
				} else {
					linkableAccount.setFormErrors();
				}
			}
		});

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		register(DefaultEventBus.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(LinkAccountEventHandler.TYPE, LinkedAccountController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(GetLinkedAccountsEventHandler.TYPE, LinkedAccountController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(DeleteLinkedAccountEventHandler.TYPE, LinkedAccountController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(UpdateLinkedAccountEventHandler.TYPE, LinkedAccountController.get(), this));
	}

	/**
	 * Create and add the Linked Account Table columns and cells
	 */
	private void createColumns() {

		columnAccountName = new TextColumn<DataAccount>() {
			@Override
			public String getValue(DataAccount object) {
				return (object.source != null) ? object.username : "-";
			}
		};
		linkedAccountsTable.addColumn(columnAccountName, "Account Name");

		columnAccountType = new TextColumn<DataAccount>() {
			@Override
			public String getValue(DataAccount object) {
				return (object.source != null) ? object.source.name : "-";
			}
		};
		linkedAccountsTable.addColumn(columnAccountType, "Account Type");

		columnDateAdded = new TextColumn<DataAccount>() {
			@Override
			public String getValue(DataAccount object) {
				return (object.source.created != null) ? DATE_FORMATTER_DD_MMM_YYYY.format(object.created, null) : "-";
			}
		};
		linkedAccountsTable.addColumn(columnDateAdded, "Date Added");

		columnEdit = new Column<DataAccount, SafeHtml>(new SafeHtmlCell()) {
			@Override
			public SafeHtml getValue(DataAccount object) {
				String id = object.id.toString();
				return SafeHtmlUtils.fromTrustedString("<a href=\""
						+ PageType.UsersPageType.asHref(
								PageType.LinkedAccountsPageType.toString(user.id.toString(), NavigationController.EDIT_ACTION_PARAMETER_VALUE, id)).asString()
						+ "\">Edit</a>");
			}

		};
		linkedAccountsTable.addColumn(columnEdit);

		columnDelete = new Column<DataAccount, SafeHtml>(new SafeHtmlCell()) {
			@Override
			public SafeHtml getValue(DataAccount object) {
				String id = object.id.toString();
				return SafeHtmlUtils.fromTrustedString("<a href=\""
						+ PageType.UsersPageType.asHref(
								PageType.LinkedAccountsPageType.toString(user.id.toString(), NavigationController.DELETE_ACTION_PARAMETER_VALUE, id))
								.asString() + "\"><span class=\"icon-cancel-1 delete\"/></a>");
			}

		};

		linkedAccountsTable.addColumn(columnDelete);

		// columnExpand = new Column<DataAccount, SafeHtml>(new SafeHtmlCell()) {
		// @Override
		// public SafeHtml getValue(DataAccount object) {
		// Anchor a = new Anchor();
		// a.setStyleName(Styles.INSTANCE.reflection().linkedAccountPlus());
		// SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
		// safeHtmlBuilder.appendHtmlConstant(a.toString());
		// return safeHtmlBuilder.toSafeHtml();
		// }
		// };
		// linkedAccountsTable.addColumn(columnExpand, "");

	}

	/**
	 * Click on expand cell and manage the visibility of the edit and delete elements on the same row
	 * 
	 * @param event
	 */
	@UiHandler("linkedAccountsTable")
	void cellTableEvent(CellPreviewEvent<DataAccount> event) {

		// int offset = linkedAccountsTable.getVisibleRange().getStart();
		// Element editElement = linkedAccountsTable.getRowElement(event.getIndex() - offset).getCells().getItem(2).getElementsByTagName("a").getItem(0);
		// Element deleteElement = linkedAccountsTable.getRowElement(event.getIndex() - offset).getCells().getItem(3).getElementsByTagName("a").getItem(0);
		// if (BrowserEvents.MOUSEOVER.equals(event.getNativeEvent().getType())) {
		// // add visibility hidden in cell
		// // Element expandElement = linkedAccountsTable.getRowElement(event.getIndex()).getCells().getItem(4).getElementsByTagName("a").getItem(0);
		// editElement.setClassName("show");
		// deleteElement.setClassName("show");
		// } else if (BrowserEvents.MOUSEOUT.equals(event.getNativeEvent().getType())) {
		// editElement.setClassName("invisible");
		// deleteElement.setClassName("invisible");
		// }

		// TODO Use selection model to show Edit and Delete links if plus and minus images will be added

		// expandElement.removeClassName(Styles.INSTANCE.reflection().linkedAccountMinus());
		// expandElement.addClassName(Styles.INSTANCE.reflection().linkedAccountPlus());
		// Use sprite for expander

		// expandElement.removeClassName(Styles.INSTANCE.reflection().linkedAccountPlus());
		// expandElement.addClassName(Styles.INSTANCE.reflection().linkedAccountMinus());

	}

	@UiHandler("addLinkedAccount")
	void onAddLinkedAccountClicked(ClickEvent event) {
		PageType.UsersPageType.show(PageType.LinkedAccountsPageType.toString(user.id.toString(), NavigationController.ADD_ACTION_PARAMETER_VALUE));
	}

	/**
	 * @param link
	 */
	// private void addSoonTag(InlineHyperlink link) {
	// SpanElement s = DOM.createSpan().cast();
	//
	// s.setInnerText("Coming Soon!");
	//
	// s.addClassName("label");
	// s.addClassName("label-danger");
	//
	// s.getStyle().setMarginLeft(10.0, Unit.PX);
	//
	// link.getElement().appendChild(s);
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) {

		if (LinkedAccountController.get().hasLinkedAccounts()) {
			addLinkedAccount.setVisible(true);
		} else {
			addLinkedAccount.setVisible(false);
		}

		user = SessionController.get().getLoggedInUser();

		if (user != null) {

			backLink.setTargetHistoryToken(PageType.UsersPageType.asTargetHistoryToken(PageType.LinkedAccountsPageType.toString(), user.id.toString()));
		}

		linkableAccount = null;

		String actionParameter = current.getParameter(ACTION_PARAMETER);
		String typeParameter = current.getParameter(ACTION_PARAMETER_INDEX);

		if (NavigationController.ADD_ACTION_PARAMETER_VALUE.equals(actionParameter)) {
			linkedAccountsPanel.setVisible(false);
			linkedAccountForm.setVisible(true);
			// mToolbar.setVisible(true);
			linkAccountBtn.setHTML(buttonAddHtml);
			form.setVisible(false);
			iosMacForm.resetForm();

			// if (typeParameter == null) {
			// mIosMacLink.setTargetHistoryToken(current.toString("iosmac"));
			// }

			// if ("iosmac".equals(typeParameter)) {
			form.setVisible(true);
			linkableAccount = iosMacForm;

			linkableAccount.setOnEnterPressed(new EnterPressedEventHandler() {
				public void onEnterPressed() {
					linkAccountBtn.click();
				}
			});

			iosMacForm.setVisible(true);
			linkableAccount.getFirstToFocus().setFocus(true);
			// } else {
			// mForm.setVisible(false);
			// }

		} else if (isValidEditStack(actionParameter, typeParameter)) {
			iosMacForm.resetForm();
			linkedAccountsPanel.setVisible(false);
			linkedAccountForm.setVisible(true);

			// mToolbar.setVisible(false);
			linkAccountBtn.setHTML(buttonEditHtml);
			form.setVisible(true);
			iosMacForm.setAccountUsernameEnabled(false);
			linkableAccount = iosMacForm;
			linkableAccount.setOnEnterPressed(new EnterPressedEventHandler() {
				public void onEnterPressed() {
					linkAccountBtn.click();
				}
			});
			linkableAccount.getFirstToFocus().setFocus(true);

			DataAccount linkedAccount = LinkedAccountController.get().getLinkedAccount(Long.valueOf(typeParameter));
			iosMacForm.setAccountUsername(linkedAccount.username);
			JsonObject propertiesJson = Convert.toJsonObject(linkedAccount.properties);
			iosMacForm.setVendorNumber(propertiesJson.get("vendors").getAsString());

		} else if (isValidDeleteStack(actionParameter, typeParameter)) {
			confirmationDialog = new ConfirmationDialog("Delete linked account", "Are you sure you want to remove this linked account?");
			confirmationDialog.center();
			confirmationDialog.setParameter(Long.valueOf(typeParameter));
			confirmationDialog.getCancelButton().addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					if (user != null) {
						PageType.UsersPageType.show(PageType.LinkedAccountsPageType.toString(user.id.toString()));
					}
					confirmationDialog.reset();
				}
			});

			confirmationDialog.getDeleteButton().addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					preloaderTable.show();
					LinkedAccountController.get().deleteLinkedAccount(LinkedAccountController.get().getLinkedAccount(confirmationDialog.getParameter()));
					confirmationDialog.reset();
				}
			});

		} else {
			linkedAccountForm.setVisible(false);
			linkedAccountsPanel.setVisible(true);
			if (user != null) {
				PageType.UsersPageType.show(PageType.LinkedAccountsPageType.toString(user.id.toString()));
			}
			if (confirmationDialog != null) {
				confirmationDialog.reset();
			}
		}

	}

	private boolean isValidEditStack(String actionParameter, String typeParameter) {
		return (NavigationController.EDIT_ACTION_PARAMETER_VALUE.equals(actionParameter) && typeParameter != null && typeParameter.matches("[0-9]+") && LinkedAccountController
				.get().getLinkedAccount(Long.valueOf(typeParameter)) != null) ? true : false;
	}

	private boolean isValidDeleteStack(String actionParameter, String typeParameter) {
		return (NavigationController.DELETE_ACTION_PARAMETER_VALUE.equals(actionParameter) && typeParameter != null && typeParameter.matches("[0-9]+") && LinkedAccountController
				.get().getLinkedAccount(Long.valueOf(typeParameter)) != null) ? true : false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.LinkAccountEventHandler# linkAccountSuccess (io.reflection.app.api.core.shared.call.LinkAccountRequest,
	 * io.reflection.app.api.core.shared.call.LinkAccountResponse)
	 */
	@Override
	public void linkAccountSuccess(LinkAccountRequest input, LinkAccountResponse output) {
		preloaderForm.hide();
		// mLinkableAccount.setEnabled(true);

		if (output.status == StatusType.StatusTypeSuccess) {
			linkableAccount.resetForm();
			PageType.UsersPageType.show(PageType.LinkedAccountsPageType.toString(user.id.toString()));
		} else if (output.error != null) {
			if (output.error.code == ApiError.InvalidDataAccountCredentials.getCode()) {
				linkableAccount.setUsernameError("iTunes Connect username or password entered incorrectly");
				linkableAccount.setPasswordError("iTunes Connect username or password entered incorrectly");
				linkableAccount.setFormErrors();
			} else if (output.error.code == ApiError.InvalidDataAccountVendor.getCode()) {
				iosMacForm.setVendorError("iTunes Connect vendor number entered incorrectly");
				linkableAccount.setFormErrors();
			}
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
		preloaderForm.hide();
		form.setVisible(true);
		linkableAccount.resetForm();
		// mLinkableAccount.setEnabled(true);
		// mLinkAccount.setEnabled(true);
		showError(FormHelper.convertToError(caught));
	}

	private void showError(Error e) {

		form.setVisible(true);
		// mToolbar.setVisible(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.DeleteLinkedAccountEventHandler#deleteLinkedAccountSuccess(io.reflection.app.api.core.shared.call.
	 * DeleteLinkedAccountRequest, io.reflection.app.api.core.shared.call.DeleteLinkedAccountResponse)
	 */
	@Override
	public void deleteLinkedAccountSuccess(DeleteLinkedAccountRequest input, DeleteLinkedAccountResponse output) {
		preloaderTable.hide();
		if (output.status == StatusType.StatusTypeSuccess) {
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
		preloaderTable.hide();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.UpdateLinkedAccountEventHandler#updateLinkedAccountSuccess(io.reflection.app.api.core.shared.call.
	 * UpdateLinkedAccountRequest, io.reflection.app.api.core.shared.call.UpdateLinkedAccountResponse)
	 */
	@Override
	public void updateLinkedAccountSuccess(UpdateLinkedAccountRequest input, UpdateLinkedAccountResponse output) {
		preloaderForm.hide();
		// mLinkableAccount.setEnabled(true);
		// mLinkAccount.setEnabled(true);

		if (output.status == StatusType.StatusTypeSuccess) {
			linkableAccount.resetForm();
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
		preloaderForm.hide();
		// mLinkableAccount.setEnabled(true);
		// mLinkAccount.setEnabled(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.GetLinkedAccountsEventHandler #getLinkedAccountsSuccess(io.reflection.app.api.core.shared.call.
	 * GetLinkedAccountsRequest, io.reflection.app.api.core.shared.call.GetLinkedAccountsResponse)
	 */
	@Override
	public void getLinkedAccountsSuccess(GetLinkedAccountsRequest input, GetLinkedAccountsResponse output) {
		preloaderTable.hide();
		if (output.status == StatusType.StatusTypeSuccess) {
			// if (LinkedAccountController.get().getLinkedAccountsCount() > output.pager.count) {
			// simplePager.setVisible(true);
			// } else {
			// simplePager.setVisible(false);
			// }
			if (LinkedAccountController.get().getLinkedAccountsCount() > 0) {
				addLinkedAccount.setVisible(true);
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
		preloaderTable.hide();
	}

}
