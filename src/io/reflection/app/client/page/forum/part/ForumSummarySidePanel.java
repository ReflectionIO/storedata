//
//  ForumSummarySidePanel.java
//  storedata
//
//  Created by daniel on 1 Aug 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.forum.part;

import io.reflection.app.api.forum.shared.call.GetForumsRequest;
import io.reflection.app.api.forum.shared.call.GetForumsResponse;
import io.reflection.app.api.forum.shared.call.event.GetForumsEventHandler;
import io.reflection.app.client.controller.ForumController;
import io.reflection.app.client.controller.TopicController;
import io.reflection.app.client.page.part.MyAccountSidePanel;
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
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author daniel
 *
 */
public class ForumSummarySidePanel extends Composite implements GetForumsEventHandler {
	
	private static ForumSummarySidePanelUiBinder uiBinder = GWT.create(ForumSummarySidePanelUiBinder.class);
	interface ForumSummarySidePanelUiBinder extends UiBinder<Widget, ForumSummarySidePanel> {}


	
	@UiField(provided = true) CellList<Forum> forums = new CellList<Forum>(new ForumSummaryCell(), BootstrapGwtCellList.INSTANCE);
	private Runnable onForumsSuccessHandler;
	private SingleSelectionModel<Forum> selectionModel;
	
	public ForumSummarySidePanel()
	{
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
	
	public void setOnForumsSuccessHandler(Runnable runnable)
	{
		this.onForumsSuccessHandler = runnable;
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.forum.shared.call.event.GetForumsEventHandler#getForumsSuccess(io.reflection.app.api.forum.shared.call.GetForumsRequest,
	 * io.reflection.app.api.forum.shared.call.GetForumsResponse)
	 */
	@Override
	public void getForumsSuccess(GetForumsRequest input, GetForumsResponse output) {
		if (output.status == StatusType.StatusTypeSuccess && output.forums != null && output.forums.size() > 0) {
			selectionModel = new SingleSelectionModel<Forum>();
			forums.setSelectionModel(selectionModel);
			redraw();
			
			if (onForumsSuccessHandler != null)
				onForumsSuccessHandler.run();
			
			
		}
	}
	
	public Long selectFirstItemAndReturnId()
	{
		if (forums.getVisibleItemCount() > 0) {
			Forum selectedForum = forums.getVisibleItem(0);
			selectionModel.setSelected(selectedForum, true);
			return selectedForum.id;
		}
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.forum.shared.call.event.GetForumsEventHandler#getForumsFailure(io.reflection.app.api.forum.shared.call.GetForumsRequest,
	 * java.lang.Throwable)
	 */
	@Override
	public void getForumsFailure(GetForumsRequest input, Throwable caught) {}
	
	

	

}
