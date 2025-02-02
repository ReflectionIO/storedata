//
//  ForumSummarySidePanel.java
//  storedata
//
//  Created by daniel on 1 Aug 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.forum.part;

import io.reflection.app.client.controller.ForumController;
import io.reflection.app.client.part.BootstrapGwtCellList;
import io.reflection.app.datatypes.shared.Forum;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * @author daniel
 * 
 */
public class ForumSummarySidePanel extends Composite {

    private static ForumSummarySidePanelUiBinder uiBinder = GWT.create(ForumSummarySidePanelUiBinder.class);

    interface ForumSummarySidePanelUiBinder extends UiBinder<Widget, ForumSummarySidePanel> {}

    @UiField(provided = true) CellList<Forum> forums = new CellList<Forum>(new ForumSummaryCell(), BootstrapGwtCellList.INSTANCE);
    private SingleSelectionModel<Forum> selectionModel;

    public ForumSummarySidePanel() {
        initWidget(uiBinder.createAndBindUi(this));

        forums.setEmptyListWidget(new HTMLPanel("No forums found!"));
        ForumController.get().addDataDisplay(forums);
    }

    /**
	 * 
	 */
    public void redraw() {
        forums.redraw();
    }

    /**
     * @param selectedForumId
     */
    public void selectItem(Forum selectedForum) {
        selectionModel = new SingleSelectionModel<Forum>();
        forums.setSelectionModel(selectionModel);

        selectionModel.setSelected(selectedForum, true);
        redraw();
    }

    /**
     * 
     */
    public void reset() {
        if (selectionModel != null) {
            selectionModel.clear();
        }
    }

}
