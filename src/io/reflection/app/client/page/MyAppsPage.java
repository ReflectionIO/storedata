//
//  MyAppsPage.java
//  storedata
//
//  Created by Stefano Capuzzi on 7 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import static io.reflection.app.client.controller.FilterController.OVERALL_LIST_TYPE;
import io.reflection.app.api.core.shared.call.GetLinkedAccountItemsRequest;
import io.reflection.app.api.core.shared.call.GetLinkedAccountItemsResponse;
import io.reflection.app.api.core.shared.call.GetLinkedAccountsRequest;
import io.reflection.app.api.core.shared.call.GetLinkedAccountsResponse;
import io.reflection.app.api.core.shared.call.event.GetLinkedAccountItemsEventHandler;
import io.reflection.app.api.core.shared.call.event.GetLinkedAccountsEventHandler;
import io.reflection.app.client.cell.MiniAppCell;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.controller.FilterController.Filter;
import io.reflection.app.client.controller.LinkedAccountController;
import io.reflection.app.client.controller.MyAppsController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.ServiceConstants;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.handler.FilterEventHandler;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.page.part.MyAccountSidePanel;
import io.reflection.app.client.page.part.MyAppsTopPanel;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.BootstrapGwtDatePicker;
import io.reflection.app.client.part.SimplePager;
import io.reflection.app.client.part.datatypes.MyApp;
import io.reflection.app.client.part.myapps.MyAppsEmptyTable;
import io.reflection.app.client.res.Images;
import io.reflection.app.client.res.Styles;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.User;
import io.reflection.app.shared.util.DataTypeHelper;

import java.util.Map;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author stefanocapuzzi
 * 
 */
public class MyAppsPage extends Page implements FilterEventHandler, NavigationEventHandler, GetLinkedAccountsEventHandler, GetLinkedAccountItemsEventHandler {

    private static MyAppsPageUiBinder uiBinder = GWT.create(MyAppsPageUiBinder.class);

    interface MyAppsPageUiBinder extends UiBinder<Widget, MyAppsPage> {}

    interface MyAppsPageStyle extends CssResource {
        String red();

        String green();

        String silver();
    }

    @UiField MyAppsPageStyle style;

    @UiField(provided = true) CellTable<MyApp> appsTable = new CellTable<MyApp>(ServiceConstants.STEP_VALUE, BootstrapGwtCellTable.INSTANCE);
    @UiField SimplePager simplePager;

    @UiField MyAppsTopPanel myAppsTopPanel;
    @UiField MyAccountSidePanel myAccountSidePanel;

    private MyAppsEmptyTable myAppsEmptyTable = new MyAppsEmptyTable();

    private User user = SessionController.get().getLoggedInUser();

    long linkedAccountsCount = -1;

    // Columns
    private Column<MyApp, SafeHtml> columnRank;
    private Column<MyApp, Item> columnAppDetails;
    private Column<MyApp, SafeHtml> columnPrice;
    private Column<MyApp, SafeHtml> columnDownloads;
    private Column<MyApp, SafeHtml> columnRevenue;
    private Column<MyApp, SafeHtml> columnIap;

    public MyAppsPage() {
        initWidget(uiBinder.createAndBindUi(this));

        BootstrapGwtDatePicker.INSTANCE.styles().ensureInjected();

        Styles.INSTANCE.reflection().ensureInjected();

        FilterController.get().setListType(OVERALL_LIST_TYPE);

        createColumns();

        appsTable.setEmptyTableWidget(myAppsEmptyTable);

        appsTable.setLoadingIndicator(new Image(Images.INSTANCE.preloader()));
        MyAppsController.get().addDataDisplay(appsTable);
        simplePager.setDisplay(appsTable);

        myAppsTopPanel.setFiltersEnabled(false);
        myAppsTopPanel.setFilterAccountEnabled(false);
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
        register(EventController.get().addHandlerToSource(FilterEventHandler.TYPE, FilterController.get(), this));
        register(EventController.get().addHandlerToSource(GetLinkedAccountsEventHandler.TYPE, LinkedAccountController.get(), this));
        register(EventController.get().addHandlerToSource(GetLinkedAccountItemsEventHandler.TYPE, MyAppsController.get(), this));

        // boolean hasPermission = SessionController.get().loggedInUserHas(SessionController.);

        // pager.setVisible(hasPermission);
        // redirect.setVisible(!hasPermission);

    }

    /**
	 * 
	 */
    private void createColumns() {

        final SafeHtml spinnerLoaderHTML = SafeHtmlUtils.fromSafeConstant("<img src=\"" + Images.INSTANCE.spinner().getSafeUri().asString() + "\"/>");

        columnRank = new Column<MyApp, SafeHtml>(new SafeHtmlCell()) {
            @Override
            public SafeHtml getValue(MyApp object) {
                return (object.overallPosition != null) ? SafeHtmlUtils.fromSafeConstant(object.overallPosition) : spinnerLoaderHTML;
            }
        };
        appsTable.addColumn(columnRank, "Rank");

        columnAppDetails = new Column<MyApp, Item>(new MiniAppCell()) {
            @Override
            public Item getValue(MyApp object) {
                return object.item;
            }
        };
        appsTable.addColumn(columnAppDetails, "App Details");

        columnPrice = new Column<MyApp, SafeHtml>(new SafeHtmlCell()) {
            @Override
            public SafeHtml getValue(MyApp object) {
                return (object.overallPrice != null) ? SafeHtmlUtils.fromSafeConstant(object.overallPrice) : spinnerLoaderHTML;
            }
        };
        appsTable.addColumn(columnPrice, "Price");

        columnDownloads = new Column<MyApp, SafeHtml>(new SafeHtmlCell()) {
            @Override
            public SafeHtml getValue(MyApp object) {
                return (object.overallDownloads != null) ? SafeHtmlUtils.fromSafeConstant(object.overallDownloads) : spinnerLoaderHTML;
            }
        };
        appsTable.addColumn(columnDownloads, "Downloads");

        columnRevenue = new Column<MyApp, SafeHtml>(new SafeHtmlCell()) {
            @Override
            public SafeHtml getValue(MyApp object) {
                return (object.overallRevenue != null) ? SafeHtmlUtils.fromSafeConstant(object.overallRevenue) : spinnerLoaderHTML;
            }
        };
        appsTable.addColumn(columnRevenue, "Revenue");

        columnIap = new Column<MyApp, SafeHtml>(new SafeHtmlCell()) {

			private final String IAP_DONT_KNOW_HTML = "<span class=\"icon-help " + style.silver() + "\"></span>";
			private final String IAP_YES_HTML = "<span class=\"icon-ok " + style.green() + "\"></span>";
			private final String IAP_NO_HTML = "<span></span>";

			@Override
			public SafeHtml getValue(MyApp object) {
				return (object.item != null) ? SafeHtmlUtils.fromSafeConstant(DataTypeHelper.itemIapState(object.item, IAP_YES_HTML, IAP_NO_HTML,
						IAP_DONT_KNOW_HTML)) : spinnerLoaderHTML;
			}

        };
        appsTable.addColumn(columnIap, "IAP");

    }

    /*
     * (non-Javadoc)
     * 
     * @see io.reflection.app.client.handler.FilterEventHandler#filterParamChanged(java.lang.String, java.lang.Object, java.lang.Object)
     */
    @Override
    public <T> void filterParamChanged(String name, T currentValue, T previousValue) {
        if (LinkedAccountController.get().hasLinkedAccounts() && LinkedAccountController.get().getLinkedAccountsCount() > 0) { // There are linked accounts
            myAppsEmptyTable.setLinkAccountVisible(false);
            MyAppsController.get().reset();
            myAppsTopPanel.setFiltersEnabled(false);
            MyAppsController.get().fetchLinkedAccountItems();
        }
        myAccountSidePanel.getMyAppsLink().setTargetHistoryToken(
                PageType.UsersPageType.asTargetHistoryToken(PageType.MyAppsPageType.toString(), user.id.toString(), FilterController.get()
                        .asMyAppsFilterString()));
        PageType.UsersPageType.show(PageType.MyAppsPageType.toString(), user.id.toString(), FilterController.get().asMyAppsFilterString());
    }

    /*
     * (non-Javadoc)
     * 
     * @see io.reflection.app.client.handler.FilterEventHandler#filterParamsChanged(io.reflection.app.client.controller.FilterController.Filter, java.util.Map)
     */
    @Override
    public void filterParamsChanged(Filter currentFilter, Map<String, ?> previousValues) {
        if (LinkedAccountController.get().hasLinkedAccounts() && LinkedAccountController.get().getLinkedAccountsCount() > 0) { // There are linked accounts
            myAppsEmptyTable.setLinkAccountVisible(false);
            MyAppsController.get().reset();
            myAppsTopPanel.setFiltersEnabled(false);
            MyAppsController.get().fetchLinkedAccountItems();
        }
        myAccountSidePanel.getMyAppsLink().setTargetHistoryToken(
                PageType.UsersPageType.asTargetHistoryToken(PageType.MyAppsPageType.toString(), user.id.toString(), FilterController.get()
                        .asMyAppsFilterString()));
        PageType.UsersPageType.show(PageType.MyAppsPageType.toString(), user.id.toString(), FilterController.get().asMyAppsFilterString());
    }

    /*
     * (non-Javadoc)
     * 
     * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack,
     * io.reflection.app.client.controller.NavigationController.Stack)
     */
    @Override
    public void navigationChanged(Stack previous, Stack current) {

        myAccountSidePanel.setMyAppsLinkActive();

        user = SessionController.get().getLoggedInUser();

        if (user != null) {
            myAccountSidePanel.getLinkedAccountsLink().setTargetHistoryToken(
                    PageType.UsersPageType.asTargetHistoryToken(PageType.LinkedAccountsPageType.toString(), user.id.toString()));

            myAccountSidePanel.getCreatorNameLink().setInnerText(user.company);

            myAccountSidePanel.getPersonalDetailsLink().setTargetHistoryToken(
                    PageType.UsersPageType.asTargetHistoryToken(PageType.ChangeDetailsPageType.toString(), user.id.toString()));

            myAccountSidePanel.getChangePasswordLink().setTargetHistoryToken(
                    PageType.UsersPageType.asTargetHistoryToken(PageType.ChangePasswordPageType.toString(), user.id.toString()));
        }

        String currentFilter = FilterController.get().asMyAppsFilterString();
        if (currentFilter != null && currentFilter.length() > 0) {
            if (user != null) {
                myAccountSidePanel.getMyAppsLink().setTargetHistoryToken(
                        PageType.UsersPageType.asTargetHistoryToken(PageType.MyAppsPageType.toString(), user.id.toString(), FilterController.get()
                                .asMyAppsFilterString()));
            }
        }

        // Linked accounts retrieved in LinkedAccountPage but not here, or Check if Added or deleted a linked account
        if ((linkedAccountsCount == -1 && LinkedAccountController.get().hasLinkedAccounts())
                || linkedAccountsCount != LinkedAccountController.get().getLinkedAccountsCount()) {
            myAppsTopPanel.fillAccountNameList();
            linkedAccountsCount = LinkedAccountController.get().getLinkedAccountsCount();
            if (LinkedAccountController.get().getLinkedAccountsCount() > 0) {
                FilterController.get().setLinkedAccount(LinkedAccountController.get().getAllLinkedAccounts().get(0).id);
            } else {
                MyAppsController.get().reset();
            }
        }

        if (linkedAccountsCount > 0) {
            myAppsEmptyTable.setLinkAccountVisible(false);
            myAppsTopPanel.setFilterAccountEnabled(true);
        } else {
            myAppsEmptyTable.setLinkAccountVisible(true);
            myAppsTopPanel.setFilterAccountEnabled(false);
        }

        myAppsTopPanel.updateFromFilter();

    }

    /*
     * (non-Javadoc)
     * 
     * @see io.reflection.app.api.core.shared.call.event.GetLinkedAccountsEventHandler#getLinkedAccountsSuccess(io.reflection.app.api.core.shared.call.
     * GetLinkedAccountsRequest, io.reflection.app.api.core.shared.call.GetLinkedAccountsResponse)
     */
    @Override
    public void getLinkedAccountsSuccess(GetLinkedAccountsRequest input, GetLinkedAccountsResponse output) {
        if (output.status == StatusType.StatusTypeSuccess) {
            if (output.pager.totalCount != null) {
                if (LinkedAccountController.get().hasLinkedAccounts()) {
                    myAppsTopPanel.fillAccountNameList();
                    linkedAccountsCount = LinkedAccountController.get().getLinkedAccountsCount();
                    if (LinkedAccountController.get().getLinkedAccountsCount() > 0) {
                        FilterController.get().setLinkedAccount(LinkedAccountController.get().getAllLinkedAccounts().get(0).id);
                        myAppsTopPanel.setFilterAccountEnabled(true);
                    } else {
                        MyAppsController.get().reset();
                        myAppsTopPanel.setFilterAccountEnabled(false);
                    }
                } else { // No linked accounts associated with this user
                    MyAppsController.get().reset();
                }
            }
        } else {
            myAppsTopPanel.setFilterAccountEnabled(false);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see io.reflection.app.api.core.shared.call.event.GetLinkedAccountsEventHandler#getLinkedAccountsFailure(io.reflection.app.api.core.shared.call.
     * GetLinkedAccountsRequest, java.lang.Throwable)
     */
    @Override
    public void getLinkedAccountsFailure(GetLinkedAccountsRequest input, Throwable caught) {
        MyAppsController.get().reset();

    }

    /*
     * (non-Javadoc)
     * 
     * @see io.reflection.app.api.core.shared.call.event.GetLinkedAccountItemsEventHandler#getLinkedAccountItemsSuccess(io.reflection.app.api.core.shared.call.
     * GetLinkedAccountItemsRequest, io.reflection.app.api.core.shared.call.GetLinkedAccountItemsResponse)
     */
    @Override
    public void getLinkedAccountItemsSuccess(GetLinkedAccountItemsRequest input, GetLinkedAccountItemsResponse output) {
        if (output.status == StatusType.StatusTypeSuccess) {
            if (MyAppsController.get().getUserItemsCount() > output.pager.count) {
                simplePager.setVisible(true);
            } else {
                simplePager.setVisible(false);
            }
            if (MyAppsController.get().getUserItemsCount() > 0) {
                myAppsTopPanel.setFiltersEnabled(true);
            } else {
                myAppsTopPanel.setFiltersEnabled(false);
            }
        } else {
            myAppsTopPanel.setFiltersEnabled(false);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see io.reflection.app.api.core.shared.call.event.GetLinkedAccountItemsEventHandler#getLinkedAccountItemsFailure(io.reflection.app.api.core.shared.call.
     * GetLinkedAccountItemsRequest, java.lang.Throwable)
     */
    @Override
    public void getLinkedAccountItemsFailure(GetLinkedAccountItemsRequest input, Throwable caught) {
        MyAppsController.get().reset();
        myAppsTopPanel.setFiltersEnabled(false);
    }

}
