//
//  BackToTop.java
//  storedata
//
//  Created by Stefano Capuzzi (stefanocapuzzi) on 23 Jul 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ScrollEvent;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Stefano Capuzzi (stefanocapuzzi)
 * 
 */
public class BackToTop extends Composite {

    private static BackToTopUiBinder uiBinder = GWT.create(BackToTopUiBinder.class);

    interface BackToTopUiBinder extends UiBinder<Widget, BackToTop> {}

    @UiField Anchor link;

    public BackToTop() {
        initWidget(uiBinder.createAndBindUi(this));

        Window.addWindowScrollHandler(new Window.ScrollHandler() {
            @Override
            public void onWindowScroll(ScrollEvent event) {
                if (event.getScrollTop() > 300) {
                    link.setVisible(true);
                } else {
                    link.setVisible(false);
                }
            }
        });
    }

    @UiHandler("link")
    void onBackToTopClicked(ClickEvent event) {
        Window.scrollTo(0, 0);
    }

}
