//  LinkedAccountsPage.java
//
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
import io.reflection.app.client.component.LoadingButton;
import io.reflection.app.client.component.PopupDialog;
import io.reflection.app.client.controller.LinkedAccountController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.helper.AnimationHelper;
import io.reflection.app.client.helper.DOMHelper;
import io.reflection.app.client.helper.ResponsiveDesignHelper;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.linkaccount.IosMacLinkAccountForm;
import io.reflection.app.client.part.linkaccount.LinkedAccountChangeEvent.EVENT_TYPE;
import io.reflection.app.client.part.linkaccount.LinkedAccountChangeEvent.LinkedAccountChangeEventHandler;
import io.reflection.app.client.part.linkaccount.LinkedAccountsEmptyTable;
import io.reflection.app.client.res.Images;
import io.reflection.app.client.res.Styles;
import io.reflection.app.client.res.Styles.ReflectionMainStyles;
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.User;

import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.builder.shared.TableCellBuilder;
import com.google.gwt.dom.builder.shared.TableRowBuilder;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.FormElement;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DefaultCellTableBuilder;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.SafeHtmlHeader;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.StatusType;

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

	private static ReflectionMainStyles style = Styles.STYLES_INSTANCE.reflectionMainStyle();

	@UiField(provided = true) CellTable<DataAccount> linkedAccountsTable = new CellTable<DataAccount>(Integer.MAX_VALUE, BootstrapGwtCellTable.INSTANCE);

	@UiField InlineHyperlink accountSettingsLink;
	@UiField InlineHyperlink linkedAccountsLink;
	@UiField InlineHyperlink usersLink;
	@UiField InlineHyperlink notificationsLink;

	@UiField LIElement accountSettingsItem;
	@UiField LIElement linkedAccountsItem;
	@UiField LIElement usersItem;
	@UiField LIElement notificationsItem;
	@UiField SpanElement usersText;
	@UiField SpanElement notifText;

	@UiField Element linkedAccountsCount;

	@UiField PopupDialog deleteLinkedAccountDialog;
	@UiField Anchor closeDeleteDialog;
	@UiField Button cancelDelete;
	@UiField LoadingButton confirmDelete;

	@UiField PopupDialog addLinkedAccountDialog;
	@UiField Anchor closeAddDialog;

	private TextColumn<DataAccount> columnAccountName;
	private Column<DataAccount, SafeHtml> columnStore;
	private TextColumn<DataAccount> columnDateAdded;
	private Column<DataAccount, String> columnEdit;
	private Column<DataAccount, String> columnDelete;

	@UiField IosMacLinkAccountForm iosMacAddForm;
	@UiField Button addAnotherLinkedAccount;

	private IosMacLinkAccountForm updatingLinkedAccountForm = null;

	private User user = SessionController.get().getLoggedInUser();

	public LinkedAccountsPage() {
		initWidget(uiBinder.createAndBindUi(this));

		deleteLinkedAccountDialog.removeFromParent();
		addLinkedAccountDialog.removeFromParent();

		createColumns();
		linkedAccountsTable.setTableBuilder(new CustomTableBuilder(linkedAccountsTable));
		linkedAccountsTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);

		// addSoonTag(mPlayLink);

		LinkedAccountsEmptyTable linkedAccountsEmptyTable = new LinkedAccountsEmptyTable();
		linkedAccountsEmptyTable.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				addLinkedAccountDialog.center();
				iosMacAddForm.resetForm();
			}
		});
		linkedAccountsTable.setEmptyTableWidget(linkedAccountsEmptyTable);

		LinkedAccountController.get().addDataDisplay(linkedAccountsTable);

		linkedAccountsTable.setLoadingIndicator(new Image(Images.INSTANCE.preloader()));

		if (!SessionController.get().isLoggedInUserAdmin()) {
			usersText.setInnerHTML("Users <span class=\"text-small\">coming soon</span>");
			usersItem.addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isDisabled());
			usersItem.getStyle().setCursor(Cursor.DEFAULT);
			notifText.setInnerHTML("Notifications <span class=\"text-small\">coming soon</span>");
			notificationsItem.addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isDisabled());
			notificationsItem.getStyle().setCursor(Cursor.DEFAULT);
			usersLink.setTargetHistoryToken(NavigationController.get().getStack().toString());
			notificationsLink.setTargetHistoryToken(NavigationController.get().getStack().toString());
		} else {
			if (user != null) {
				notificationsLink.setTargetHistoryToken(PageType.UsersPageType.asTargetHistoryToken(PageType.NotificationsPageType.toString(),
						user.id.toString()));
			}
		}
		if (user != null) {
			accountSettingsLink
					.setTargetHistoryToken(PageType.UsersPageType.asTargetHistoryToken(PageType.ChangeDetailsPageType.toString(), user.id.toString()));

		}

		iosMacAddForm.addLinkedAccountChangeEventHander(new LinkedAccountChangeEventHandler() {

			@Override
			public void onChange(DataAccount dataAccount, EVENT_TYPE eventType) {
				iosMacAddForm.setEnabled(false);
				iosMacAddForm.setStatusLoading("Loading");
				Document.get().getBody().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().formSubmittedLoading());
				LinkedAccountController.get().linkAccount(iosMacAddForm.getAccountSourceId(), iosMacAddForm.getUsername(), iosMacAddForm.getPassword(),
						iosMacAddForm.getProperties());
			}
		});

		linkedAccountsCount.setInnerSafeHtml(AnimationHelper.getLoaderInlineSafeHTML());

		// Add click event to LI element so the event is fired when clicking on the whole tab
		Event.sinkEvents(accountSettingsItem, Event.ONCLICK);
		Event.sinkEvents(linkedAccountsItem, Event.ONCLICK);
		Event.sinkEvents(usersItem, Event.ONCLICK);
		Event.sinkEvents(notificationsItem, Event.ONCLICK);
		Event.setEventListener(accountSettingsItem, new EventListener() {

			@Override
			public void onBrowserEvent(Event event) {
				if (Event.ONCLICK == event.getTypeInt()) {
					History.newItem(accountSettingsLink.getTargetHistoryToken());
				}
			}
		});
		Event.setEventListener(linkedAccountsItem, new EventListener() {

			@Override
			public void onBrowserEvent(Event event) {
				if (Event.ONCLICK == event.getTypeInt()) {
					History.newItem(linkedAccountsLink.getTargetHistoryToken());
				}
			}
		});
		Event.setEventListener(usersItem, new EventListener() {

			@Override
			public void onBrowserEvent(Event event) {
				if (Event.ONCLICK == event.getTypeInt()) {
					History.newItem(usersLink.getTargetHistoryToken());
				}
			}
		});
		Event.setEventListener(notificationsItem, new EventListener() {

			@Override
			public void onBrowserEvent(Event event) {
				if (Event.ONCLICK == event.getTypeInt()) {
					History.newItem(notificationsLink.getTargetHistoryToken());
				}
			}
		});
	}

	private class CustomTableBuilder extends DefaultCellTableBuilder<DataAccount> {

		public CustomTableBuilder(CellTable<DataAccount> linkedAccountsTable) {
			super(linkedAccountsTable);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.gwt.user.cellview.client.DefaultCellTableBuilder#buildRowImpl(java.lang.Object, int)
		 */
		@Override
		public void buildRowImpl(DataAccount rowValue, int absRowIndex) {
			super.buildRowImpl(rowValue, absRowIndex); // Build default row

			buildUpdateLinkedAccountRow(rowValue, absRowIndex); // Build sliding row
		}

		private void buildUpdateLinkedAccountRow(final DataAccount rowValue, final int absRowIndex) {
			TableRowBuilder row = startRow();
			TableCellBuilder td = row.startTD();
			final String uniqueCellId = DOM.createUniqueId();
			td.id(uniqueCellId);
			td.className(style.updateLinkedAccountsContainer());
			if (cellTable.getColumnCount() > 1) {
				td.colSpan(cellTable.getColumnCount());
			}
			td.endTD();
			row.endTR();
			final IosMacLinkAccountForm updateLinkedAccountForm = new IosMacLinkAccountForm();
			if (absRowIndex % 2 == 0) {
				updateLinkedAccountForm.setStyleName(style.formsMidTheme());
			}
			updateLinkedAccountForm.setTitleText("Edit Details");
			updateLinkedAccountForm.setTitleStyleName(style.headingStyleHeadingFive());
			updateLinkedAccountForm.setButtonText("Save Changes");
			updateLinkedAccountForm.setAccount(rowValue);
			updateLinkedAccountForm.showAccountUsername(false);

			updateLinkedAccountForm.addLinkedAccountChangeEventHander(new LinkedAccountChangeEventHandler() {

				@Override
				public void onChange(DataAccount dataAccount, EVENT_TYPE eventType) {
					if (updateLinkedAccountForm.validate()) {
						updateLinkedAccountForm.setFormErrors();
						updateLinkedAccountForm.setEnabled(false);
						updateLinkedAccountForm.setStatusLoading("Updating ..");
						updatingLinkedAccountForm = updateLinkedAccountForm;
						LinkedAccountController.get().updateLinkedAccont(rowValue.id, dataAccount.password, dataAccount.properties);
					} else {
						updateLinkedAccountForm.setFormErrors();
					}
				}
			});

			final Button updateLinkedAccountDeleteButton = new Button("Delete this account");
			updateLinkedAccountDeleteButton.setStyleName(style.refButtonLink());
			updateLinkedAccountDeleteButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					deleteLinkedAccountDialog.setParameter(rowValue.id);
					deleteLinkedAccountDialog.center();
				}
			});

			Scheduler.get().scheduleDeferred(new ScheduledCommand() {

				@Override
				public void execute() {
					Element cell = Document.get().getElementById(uniqueCellId);
					if (cell == null) return;
					if (!updateLinkedAccountForm.isAttached()) {
						RootPanel.get().add(updateLinkedAccountForm);
						RootPanel.get().add(updateLinkedAccountDeleteButton);
						updateLinkedAccountForm.getElement().appendChild(updateLinkedAccountDeleteButton.getElement());
					}
					cell.appendChild(updateLinkedAccountForm.getElement());
					TableRowElement rowElem = linkedAccountsTable.getRowElement(absRowIndex).cast();
					TableRowElement subRowElem = rowElem.getNextSiblingElement().cast();
					AnimationHelper.nativeHide(subRowElem);
					AnimationHelper.nativeHide(updateLinkedAccountForm.getElement());
					AnimationHelper.nativeHide(updateLinkedAccountForm.getElement().getElementsByTagName("h2").getItem(0));
				}
			});

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

		register(DefaultEventBus.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(LinkAccountEventHandler.TYPE, LinkedAccountController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(GetLinkedAccountsEventHandler.TYPE, LinkedAccountController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(DeleteLinkedAccountEventHandler.TYPE, LinkedAccountController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(UpdateLinkedAccountEventHandler.TYPE, LinkedAccountController.get(), this));

		ResponsiveDesignHelper.makeTabsResponsiveMultiPage();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onDetach()
	 */
	@Override
	protected void onDetach() {
		super.onDetach();

		Document.get().getBody().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().formSubmittedLoading());
		addLinkedAccountDialog.hide();
		deleteLinkedAccountDialog.hide();
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
		SafeHtmlHeader accountNameHeader = new SafeHtmlHeader(SafeHtmlUtils.fromTrustedString("Account Name " + AnimationHelper.getSorterSvg()));
		accountNameHeader.setHeaderStyleNames(style.canBeSorted());
		columnAccountName.setCellStyleNames(style.linkedAccountName());
		linkedAccountsTable.addColumn(columnAccountName, accountNameHeader);

		columnStore = new Column<DataAccount, SafeHtml>(new SafeHtmlCell()) {

			@Override
			public SafeHtml getValue(DataAccount object) {
				return (object.source != null) ? SafeHtmlUtils.fromSafeConstant("<span class=\"" + style.refIconBefore() + " " + style.refIconBeforeApple()
						+ "\">" + object.source.name + "</span>") : SafeHtmlUtils.fromSafeConstant("-");
			}

		};
		SafeHtmlHeader storeHeader = new SafeHtmlHeader(SafeHtmlUtils.fromTrustedString("Store " + AnimationHelper.getSorterSvg()));
		storeHeader.setHeaderStyleNames(style.canBeSorted());
		columnStore.setCellStyleNames(style.linkedAccountStore());
		linkedAccountsTable.addColumn(columnStore, storeHeader);

		columnDateAdded = new TextColumn<DataAccount>() {
			@Override
			public String getValue(DataAccount object) {
				return (object.source.created != null) ? DATE_FORMATTER_DD_MMM_YYYY.format(object.created, null) : "-";
			}
		};
		SafeHtmlHeader dateAddedHeader = new SafeHtmlHeader(SafeHtmlUtils.fromTrustedString("Date Added " + AnimationHelper.getSorterSvg()));
		dateAddedHeader.setHeaderStyleNames(style.canBeSorted());
		dateAddedHeader.setHeaderStyleNames(style.tableHeadingDateAdded());
		columnDateAdded.setCellStyleNames(style.linkedAccountDate());
		linkedAccountsTable.addColumn(columnDateAdded, dateAddedHeader);

		columnEdit = new Column<DataAccount, String>(new ClickableTextCell(new SafeHtmlRenderer<String>() {

			@Override
			public void render(String object, SafeHtmlBuilder builder) {
				builder.appendHtmlConstant(object);
			}

			@Override
			public SafeHtml render(String object) {
				return SafeHtmlUtils.fromSafeConstant("<a class=\"" + style.refButtonFunctionSmall() + "\" style=\"cursor: pointer\">Edit</a>");
			}
		})) {
			@Override
			public String getValue(DataAccount object) {
				return "";
			}
		};
		columnEdit.setCellStyleNames(style.linkedAccountEdit());
		linkedAccountsTable.addColumn(columnEdit);
		columnEdit.setFieldUpdater(new FieldUpdater<DataAccount, String>() {

			@Override
			public void update(int index, DataAccount object, String value) {

				TableRowElement rowElem = linkedAccountsTable.getRowElement(index).cast();
				TableRowElement subRowElem = rowElem.getNextSiblingElement().cast();
				FormElement formElem = subRowElem.getFirstChildElement().getElementsByTagName("form").getItem(0).cast();
				AnimationHelper.nativeSlideToggle(subRowElem, 200);
				AnimationHelper.nativeSlideToggle(formElem, 200);
				AnimationHelper.nativeSlideToggle(formElem.getFirstChildElement().getElementsByTagName("h2").getItem(0), 200);
				DOMHelper.toggleClassName(rowElem.getFirstChildElement(), style.isOpen());
				DOMHelper.toggleClassName(linkedAccountsTable.getRowElement(index).getCells().getItem(3).getFirstChildElement().getElementsByTagName("a")
						.getItem(0), style.isPressed());
			}
		});

		columnDelete = new Column<DataAccount, String>(new ClickableTextCell(new SafeHtmlRenderer<String>() {

			@Override
			public void render(String object, SafeHtmlBuilder builder) {
				builder.appendHtmlConstant(object);
			}

			@Override
			public SafeHtml render(String object) {
				return SafeHtmlUtils.fromSafeConstant("<a class=\"" + style.refButtonLink() + " " + style.warningText()
						+ "\" style=\"cursor: pointer\">Delete</a>");
			}
		})) {
			@Override
			public String getValue(DataAccount object) {
				return "";
			}
		};
		columnDelete.setCellStyleNames(style.linkedAccountDelete());
		linkedAccountsTable.addColumn(columnDelete);
		columnDelete.setFieldUpdater(new FieldUpdater<DataAccount, String>() {

			@Override
			public void update(int index, DataAccount object, String value) {
				deleteLinkedAccountDialog.setParameter(Long.valueOf(object.id.intValue()));
				deleteLinkedAccountDialog.center();
				// new FadeInAnimation(deleteAccountOverlay).run(200);
			}
		});

		linkedAccountsTable.addColumnStyleName(0, style.tableColumnAccountName());
		linkedAccountsTable.addColumnStyleName(1, style.tableColumnStore());
		linkedAccountsTable.addColumnStyleName(2, style.tableColumnDateAdded());
		linkedAccountsTable.addColumnStyleName(3, style.tableColumnEditAccount());
		linkedAccountsTable.addColumnStyleName(4, style.tableColumnDeleteAccount());

	}

	private void setTableEmpty(boolean empty) {
		addAnotherLinkedAccount.setEnabled(!empty);
		linkedAccountsTable.setStyleName(style.tableLinkedAccountsDisabled(), empty);
	}

	private void updateViewFromLinkedAccountCount() {
		long count = LinkedAccountController.get().getLinkedAccountsCount();
		if (count >= 0) {
			linkedAccountsCount.setInnerText(Long.toString(count));
		}
		setTableEmpty(count == 0);
	}

	@UiHandler("addAnotherLinkedAccount")
	void onAddLinkedAccountClicked(ClickEvent event) {
		addLinkedAccountDialog.center();
		// if (typeParameter == null) {
		// mIosMacLink.setTargetHistoryToken(current.toString("iosmac"));
		// }
		// if ("iosmac".equals(typeParameter)) {
		iosMacAddForm.resetForm();

	}

	@UiHandler("cancelDelete")
	void onCancelDeleteClicked(ClickEvent event) {
		deleteLinkedAccountDialog.hide();
	}

	@UiHandler("confirmDelete")
	void onConfirmDeleteClicked(ClickEvent event) {
		LinkedAccountController.get().deleteLinkedAccount(LinkedAccountController.get().getLinkedAccount(deleteLinkedAccountDialog.getParameter()));
		confirmDelete.setStatusLoading("Deleting..");
		// deleteAccountOverlay.setVisible(false);
	}

	@UiHandler("closeAddDialog")
	void onCloseAddDialogClicked(ClickEvent event) {
		event.preventDefault();
		addLinkedAccountDialog.hide();
	}

	@UiHandler("closeDeleteDialog")
	void onCloseDeleteDialogClicked(ClickEvent event) {
		event.preventDefault();
		deleteLinkedAccountDialog.hide();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) {
		updateViewFromLinkedAccountCount();
		linkedAccountsLink.setTargetHistoryToken(current.toString());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.LinkAccountEventHandler# linkAccountSuccess (io.reflection.app.api.core.shared.call.LinkAccountRequest,
	 * io.reflection.app.api.core.shared.call.LinkAccountResponse)
	 */
	@Override
	public void linkAccountSuccess(LinkAccountRequest input, LinkAccountResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {
			updateViewFromLinkedAccountCount();
			iosMacAddForm.setStatusSuccess("Success!");
			Timer t = new Timer() {

				@Override
				public void run() {
					Document.get().getBody().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().formSubmittedLoading());
					addLinkedAccountDialog.hide();
					iosMacAddForm.resetForm();
				}
			};
			t.schedule(1000);
		} else if (output.error != null) {
			Document.get().getBody().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().formSubmittedLoading());
			if (output.error.code == ApiError.InvalidDataAccountCredentials.getCode()) {
				iosMacAddForm.setStatusError("Invalid credentials!");
				iosMacAddForm.setUsernameError("iTunes Connect username or password entered incorrectly");
				iosMacAddForm.setPasswordError("iTunes Connect username or password entered incorrectly");
				iosMacAddForm.setFormErrors();
			} else if (output.error.code == ApiError.InvalidDataAccountVendor.getCode()) {
				iosMacAddForm.setStatusError("Invalid vendor ID!");
				iosMacAddForm.setVendorError("iTunes Connect vendor number entered incorrectly");
				iosMacAddForm.setFormErrors();
			} else { // TODO NULL POINTER EXCEPTION DUE TO DUPLICATE LINKED ACCOUNT
				iosMacAddForm.setStatusError();
			}
			iosMacAddForm.setEnabled(true);
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
		Document.get().getBody().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().formSubmittedLoading());
		iosMacAddForm.resetForm();
		iosMacAddForm.setStatusError();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.DeleteLinkedAccountEventHandler#deleteLinkedAccountSuccess(io.reflection.app.api.core.shared.call.
	 * DeleteLinkedAccountRequest, io.reflection.app.api.core.shared.call.DeleteLinkedAccountResponse)
	 */
	@Override
	public void deleteLinkedAccountSuccess(DeleteLinkedAccountRequest input, DeleteLinkedAccountResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {
			confirmDelete.resetStatus();
			deleteLinkedAccountDialog.hide();
			updateViewFromLinkedAccountCount();
		} else {
			confirmDelete.setStatusError();
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
		confirmDelete.setStatusError();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.UpdateLinkedAccountEventHandler#updateLinkedAccountSuccess(io.reflection.app.api.core.shared.call.
	 * UpdateLinkedAccountRequest, io.reflection.app.api.core.shared.call.UpdateLinkedAccountResponse)
	 */
	@Override
	public void updateLinkedAccountSuccess(UpdateLinkedAccountRequest input, UpdateLinkedAccountResponse output) {
		if (updatingLinkedAccountForm != null) {
			if (output.status == StatusType.StatusTypeSuccess) {
				updatingLinkedAccountForm.setStatusSuccess("Updated!");
			} else {
				updatingLinkedAccountForm.setStatusError();
			}
			updatingLinkedAccountForm.clearPassword();
			updatingLinkedAccountForm.setEnabled(true);
			updatingLinkedAccountForm = null;
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
		if (updatingLinkedAccountForm != null) {
			updatingLinkedAccountForm.setStatusError();
			updatingLinkedAccountForm.clearPassword();
			updatingLinkedAccountForm.setEnabled(true);
			updatingLinkedAccountForm = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.GetLinkedAccountsEventHandler #getLinkedAccountsSuccess(io.reflection.app.api.core.shared.call.
	 * GetLinkedAccountsRequest, io.reflection.app.api.core.shared.call.GetLinkedAccountsResponse)
	 */
	@Override
	public void getLinkedAccountsSuccess(GetLinkedAccountsRequest input, GetLinkedAccountsResponse output) {
		// preloaderTable.hide();
		if (output.status == StatusType.StatusTypeSuccess) {
			// if (LinkedAccountController.get().getLinkedAccountsCount() > output.pager.count) {
			// simplePager.setVisible(true);
			// } else {
			// simplePager.setVisible(false);
			// }
			updateViewFromLinkedAccountCount();
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
		// preloaderTable.hide();
	}

}
