//
//  MyAppsEmptyTable.java
//  storedata
//
//  Created by Stefano Capuzzi (stefanocapuzzi) on 22 Jul 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part.myapps;

import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.page.PageType;
import io.reflection.app.datatypes.shared.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Stefano Capuzzi (stefanocapuzzi)
 * 
 */
public class MyAppsEmptyTable extends Composite {

    private static MyAppsEmptyTableUiBinder uiBinder = GWT.create(MyAppsEmptyTableUiBinder.class);

    interface MyAppsEmptyTableUiBinder extends UiBinder<Widget, MyAppsEmptyTable> {}

    @UiField HTMLPanel linkAccount;
    @UiField InlineHyperlink link;

    public MyAppsEmptyTable() {
        initWidget(uiBinder.createAndBindUi(this));
        User user = SessionController.get().getLoggedInUser();
        if (user != null) {
            link.setTargetHistoryToken(PageType.UsersPageType.asTargetHistoryToken(PageType.LinkedAccountsPageType.toString(), user.id.toString(), "add"));
        }
    }

    public void setLinkAccountVisible(boolean visible) {
        linkAccount.setVisible(visible);
    }
}
