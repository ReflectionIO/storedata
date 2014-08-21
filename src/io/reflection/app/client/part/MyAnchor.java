//
//  MyAnchor.java
//  storedata
//
//  Created by daniel on 21 Aug 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Anchor;

public class MyAnchor extends Anchor {

        private static String LABEL_HREF = "href";
        private static String LABEL_TABINDEX = "tabindex";
        private String href = null;
        private String tabindex = null;
        private boolean enabled = true;

        public MyAnchor(String text) {
                super(text);
        }
        
        public MyAnchor(){
            super();
        }
        
        @Override
        public void onBrowserEvent(Event event) {
                switch (DOM.eventGetType(event)) {
                        case Event.ONDBLCLICK:
                        case Event.ONFOCUS:
                        case Event.ONCLICK:
                        case Event.ONMOUSEDOWN:
                        case Event.ONMOUSEUP:
                                if (!isEnabled()) {
                                        return;
                                }
                                break;
                }
                super.onBrowserEvent(event);
        }
        
        @Override
        public void setEnabled(boolean enabled) {
            BootstrapGwtDisableAnchor.INSTANCE.styles().ensureInjected();
                if(this.enabled != enabled)
                {
                        this.enabled = enabled;
                        if(enabled && href != null && tabindex != null)
                        {
                                this.getElement().setAttribute(LABEL_HREF, href);
                                this.getElement().setAttribute(LABEL_TABINDEX, tabindex);
                                this.removeStyleName(BootstrapGwtDisableAnchor.INSTANCE.styles().disabled());
                        }
                        else
                        {
                                this.addStyleName(BootstrapGwtDisableAnchor.INSTANCE.styles().disabled());
                                href = this.getElement().getAttribute(LABEL_HREF);
                                this.getElement().removeAttribute(LABEL_HREF);
                                tabindex = this.getElement().getAttribute(LABEL_TABINDEX);
                                this.getElement().removeAttribute(LABEL_TABINDEX);
                        }
                }
        }

        @Override
        public boolean isEnabled() {
                return enabled;
        }
}
