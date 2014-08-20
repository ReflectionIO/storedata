//
//  DiscreteSizePager.java
//  storedata
//
//  Created by daniel on 20 Aug 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.forum.part;

import com.google.gwt.view.client.Range;

import io.reflection.app.client.part.SimplePager;

/**
 * @author daniel
 * 
 * Creating this class as an interim, because Stefano said he is going to be styling the pager.
 * Ideally the functionality should be combined, but I'm unsure about the context in which
 * the other SimplePagers on the site are used.
 *
 */
public class DiscreteSizePager extends SimplePager {
    
    public DiscreteSizePager()
    {
        //This badly named setting seems to mean that the paging is not purely governed by the available data.
        //i.e. if false can represent the last page even if rows don't add up to page size.
        setRangeLimited(false);
    }
    /**
     * Adapted from the solution proposed here.
     * https://groups.google.com/forum/#!topic/google-web-toolkit/RedwgreWKB0
     */
    public void setPageStart(int index) {
        if (getDisplay() != null) {
          Range range = getDisplay().getVisibleRange();
          int pageSize = range.getLength();
          if (!isRangeLimited() && getDisplay().isRowCountExact()) {
            index = Math.min(index, getDisplay().getRowCount() - pageSize);
          }
          index = Math.max(0, index);
          if (index != range.getStart()) {
            getDisplay().setVisibleRange(index, pageSize);
          }
        }
      }

}
