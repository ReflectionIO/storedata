//
//  ConfirmationDialog.java
//  storedata
//
//  Created by Daniel Gerson
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part.text;

import io.reflection.app.client.res.Styles;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author danielg
 */
public abstract class UrlAndTitlePopup extends PopupPanel {

    
    @UiField HTMLPanel textPanel;
    @UiField HTMLPanel buttonsPanel;
    
    @UiField TextBox title ;
    @UiField TextBox url ;

    // HTMLPanel dialogWidget = new HTMLPanel("");
    // HTMLPanel textPanel = new HTMLPanel("<h1></h1><p></p>");
    // HTMLPanel buttonsPanel = new HTMLPanel("");
    Button cancel = new Button("Cancel");
    Button insert = new Button("Insert");

    private static final Binder binder = GWT.create(Binder.class);

    interface Binder extends UiBinder<Widget, UrlAndTitlePopup> {}

    public UrlAndTitlePopup(String dialogTitle) {

        super();
        setWidget(binder.createAndBindUi(this));
        Styles.INSTANCE.confirmationDialog().ensureInjected();
        hide(true);
        setGlassEnabled(true);
        setModal(true);
        // this.setWidget(dialogWidget);
        textPanel.getElement().getElementsByTagName("h1").getItem(0).setInnerText(dialogTitle);
        buttonsPanel.add(cancel);
        buttonsPanel.add(insert);
//        dialogWidget.add(textPanel);
//        dialogWidget.add(buttonsPanel);
        cancel.getElement().addClassName("btn btn-cancel");
        insert.getElement().addClassName("btn btn-cancel");

        cancel.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                UrlAndTitlePopup.this.hide();
            }
        });

        insert.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                
                insert(title.getText(),
                        url.getText());
                UrlAndTitlePopup.this.hide();
            }
        });
    }

    public abstract void insert(String title, String url);

    public Button getCancelButton() {
        return cancel;
    }

    public Button getDeleteButton() {
        return insert;
    }

    public void prepAndShow() {
        this.center();
        this.show();
    }

}
