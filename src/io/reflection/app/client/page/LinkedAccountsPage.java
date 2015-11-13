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
import io.reflection.app.client.cell.DeleteLinkedAccountCell;
import io.reflection.app.client.cell.EditLinkedAccountCell;
import io.reflection.app.client.component.LoadingBar;
import io.reflection.app.client.controller.LinkedAccountController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.helper.AnimationHelper;
import io.reflection.app.client.helper.DOMHelper;
import io.reflection.app.client.helper.ResponsiveDesignHelper;
import io.reflection.app.client.helper.TooltipHelper;
import io.reflection.app.client.mixpanel.MixpanelHelper;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.linkaccount.IosMacLinkAccountForm;
import io.reflection.app.client.part.linkaccount.LinkedAccountChangeEvent.EVENT_TYPE;
import io.reflection.app.client.part.linkaccount.LinkedAccountChangeEvent.LinkedAccountChangeEventHandler;
import io.reflection.app.client.part.linkaccount.LinkedAccountsEmptyTable;
import io.reflection.app.client.popup.AddLinkedAccountPopup;
import io.reflection.app.client.popup.DeleteLinkedAccountPopup;
import io.reflection.app.client.res.Styles;
import io.reflection.app.client.res.Styles.ReflectionMainStyles;
import io.reflection.app.datatypes.shared.DataAccount;

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
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
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
import com.google.gwt.user.client.ui.Button;
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

	@UiField Element linkedAccountsCount;

	private TextColumn<DataAccount> columnAccountName;
	private Column<DataAccount, SafeHtml> columnStore;
	private TextColumn<DataAccount> columnDateAdded;
	private Column<DataAccount, String> columnEdit;
	private Column<DataAccount, String> columnDelete;

	@UiField Button addAnotherLinkedAccountBtn;

	private IosMacLinkAccountForm updatingLinkedAccountForm = null;

	private LoadingBar loadingBar = new LoadingBar(false);
	private AddLinkedAccountPopup addLinkedAccountPopup = new AddLinkedAccountPopup();
	private DeleteLinkedAccountPopup deleteLinkedAccountPopup = new DeleteLinkedAccountPopup();

	public LinkedAccountsPage() {
		initWidget(uiBinder.createAndBindUi(this));

		linkedAccountsTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);

		// addSoonTag(mPlayLink);

		LinkedAccountsEmptyTable linkedAccountsEmptyTable = new LinkedAccountsEmptyTable();
		linkedAccountsEmptyTable.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				MixpanelHelper.trackClicked(MixpanelHelper.Event.OPEN_LINK_ACCOUNT_POPUP, "linkedaccounts_linkFirstAccount");
				addLinkedAccountPopup.show("Link an Account", null);
			}
		});
		linkedAccountsTable.setEmptyTableWidget(linkedAccountsEmptyTable);

		LinkedAccountController.get().addDataDisplay(linkedAccountsTable);
		if (!LinkedAccountController.get().linkedAccountsFetched()) {
			loadingBar.show("Getting linked accounts ..");
		}

		linkedAccountsTable.setLoadingIndicator(AnimationHelper.getLinkedAccountsIndicator(1));

		linkedAccountsCount.setInnerSafeHtml(AnimationHelper.getLoaderInlineSafeHTML());

		linkedAccountsTable.setTableBuilder(new CustomTableBuilder(linkedAccountsTable));
		createColumns();

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
					deleteLinkedAccountPopup.show(rowValue.id);
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

		ResponsiveDesignHelper.makeTabsResponsive();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onDetach()
	 */
	@Override
	protected void onDetach() {
		super.onDetach();

		addLinkedAccountPopup.hide();
		deleteLinkedAccountPopup.hide();
		loadingBar.reset();
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
		SafeHtmlHeader accountNameHeader = new SafeHtmlHeader(SafeHtmlUtils.fromTrustedString("Account Name"));
		columnAccountName.setCellStyleNames(style.linkedAccountName());
		linkedAccountsTable.addColumn(columnAccountName, accountNameHeader);

		columnStore = new Column<DataAccount, SafeHtml>(new SafeHtmlCell()) {

			@Override
			public SafeHtml getValue(DataAccount object) {
				return (object.source != null) ? SafeHtmlUtils.fromSafeConstant("<span class=\"" + style.refIconBefore() + " " + style.refIconBeforeApple()
						+ "\">" + object.source.name + "</span>") : SafeHtmlUtils.fromSafeConstant("-");
			}

		};
		SafeHtmlHeader storeHeader = new SafeHtmlHeader(SafeHtmlUtils.fromTrustedString("Store"));
		columnStore.setCellStyleNames(style.linkedAccountStore());
		linkedAccountsTable.addColumn(columnStore, storeHeader);

		columnDateAdded = new TextColumn<DataAccount>() {
			@Override
			public String getValue(DataAccount object) {
				return (object.source.created != null) ? DATE_FORMATTER_DD_MMM_YYYY.format(object.created, null) : "-";
			}
		};
		SafeHtmlHeader dateAddedHeader = new SafeHtmlHeader(SafeHtmlUtils.fromTrustedString("Date Added"));
		dateAddedHeader.setHeaderStyleNames(style.tableHeadingDateAdded());
		columnDateAdded.setCellStyleNames(style.linkedAccountDate());
		linkedAccountsTable.addColumn(columnDateAdded, dateAddedHeader);

		columnEdit = new Column<DataAccount, String>(new EditLinkedAccountCell()) {

			@Override
			public String getValue(DataAccount object) {
				return object.id.toString();
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

		columnDelete = new Column<DataAccount, String>(new DeleteLinkedAccountCell()) {

			@Override
			public String getValue(DataAccount object) {
				return object.id.toString();
			}
		};

		columnDelete.setCellStyleNames(style.linkedAccountDelete());
		linkedAccountsTable.addColumn(columnDelete);
		columnDelete.setFieldUpdater(new FieldUpdater<DataAccount, String>() {

			@Override
			public void update(int index, DataAccount object, String value) {
				deleteLinkedAccountPopup.show(Long.valueOf(object.id.intValue()));
			}
		});

		linkedAccountsTable.addColumnStyleName(0, style.tableColumnAccountName());
		linkedAccountsTable.addColumnStyleName(1, style.tableColumnStore());
		linkedAccountsTable.addColumnStyleName(2, style.tableColumnDateAdded());
		linkedAccountsTable.addColumnStyleName(3, style.tableColumnEditAccount());
		linkedAccountsTable.addColumnStyleName(4, style.tableColumnDeleteAccount());

	}

	private void setTableEmpty(boolean empty) {
		addAnotherLinkedAccountBtn.setEnabled(!empty);
		linkedAccountsTable.setStyleName(style.tableLinkedAccountsDisabled(), empty);
	}

	private void updateViewFromLinkedAccountCount() {
		long count = LinkedAccountController.get().getLinkedAccountsCount();
		if (count >= 0) {
			linkedAccountsCount.setInnerText(Long.toString(count));
			linkedAccountsTable.redraw();
			TooltipHelper.updateWhatsThisTooltip();
		}
		setTableEmpty(count <= 0);
	}

	@UiHandler("addAnotherLinkedAccountBtn")
	void onAddLinkedAccountClicked(ClickEvent event) {
		MixpanelHelper.trackClicked(MixpanelHelper.Event.OPEN_LINK_ACCOUNT_POPUP, "linkedaccounts_linkAnotherAccount");
		addLinkedAccountPopup.show("Link Another Account", null);
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
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.LinkAccountEventHandler# linkAccountFailure (io.reflection.app.api.core.shared.call.LinkAccountRequest,
	 * java.lang.Throwable)
	 */
	@Override
	public void linkAccountFailure(LinkAccountRequest input, Throwable caught) {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.DeleteLinkedAccountEventHandler#deleteLinkedAccountSuccess(io.reflection.app.api.core.shared.call.
	 * DeleteLinkedAccountRequest, io.reflection.app.api.core.shared.call.DeleteLinkedAccountResponse)
	 */
	@Override
	public void deleteLinkedAccountSuccess(DeleteLinkedAccountRequest input, DeleteLinkedAccountResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {
			updateViewFromLinkedAccountCount();
			deleteLinkedAccountPopup.setStatusSuccess();
		} else {
			deleteLinkedAccountPopup.setStatusError();
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
		deleteLinkedAccountPopup.setStatusError();
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
				updatingLinkedAccountForm.clearPassword();
			} else if (output.error != null) {
				if (output.error.code == ApiError.InvalidDataAccountCredentials.getCode()) {
					updatingLinkedAccountForm.setStatusError("Invalid credentials!");
					// updatingLinkedAccountForm.setUsernameError("iTunes Connect username or password entered incorrectly");
					updatingLinkedAccountForm.setPasswordError("iTunes Connect password entered incorrectly");
					updatingLinkedAccountForm.setFormErrors();
				} else if (output.error.code == ApiError.DuplicateVendorId.getCode()) {
					updatingLinkedAccountForm.setStatusError("Account already linked!");
					updatingLinkedAccountForm.setUsernameError("The vendor ID you entered is already in use");
					updatingLinkedAccountForm.setPasswordError("The vendor ID you entered is already in use");
					updatingLinkedAccountForm.setFormErrors();
				} else {
					updatingLinkedAccountForm.setStatusError();
					updatingLinkedAccountForm.clearPassword();
				}

			}
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
			loadingBar.hide(true);
			updateViewFromLinkedAccountCount();
		} else {
			loadingBar.hide(false);
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
		loadingBar.hide(false);
	}

}
