//
//  DeleteUserPopup.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 19 Oct 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.popup;

import io.reflection.app.client.component.LoadingButton;
import io.reflection.app.client.controller.UserController;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Stefano Capuzzi (capuzzistefano)
 *
 */
public class DeleteUserPopup extends Composite {

	private static DeleteUserPopupUiBinder uiBinder = GWT.create(DeleteUserPopupUiBinder.class);

	interface DeleteUserPopupUiBinder extends UiBinder<Widget, DeleteUserPopup> {}

	@UiField PopupBase popup;
	@UiField LoadingButton confirmDelete;
	@UiField Button cancelDelete;

	private Long idToDelete;

	public DeleteUserPopup() {
		initWidget(uiBinder.createAndBindUi(this));

	}

	public void show(Long idToDelete) {
		this.idToDelete = idToDelete;

		if (!this.asWidget().isAttached()) {
			RootPanel.get().add(this);
		}
		popup.show();

	}

	public void hide() {
		popup.closePopup();
	}

	@UiHandler("popup")
	void onPopupClosed(CloseEvent<PopupBase> event) {
		RootPanel.get().remove(this.asWidget());
		idToDelete = null;
	}

	@UiHandler("confirmDelete")
	void onConfirmDeleteClicked(ClickEvent event) {
		if (idToDelete != null) {
			UserController.get().deleteUser(idToDelete);
			confirmDelete.setStatusLoading("Deleting");
		}
	}

	@UiHandler("cancelDelete")
	void onCancelDeleteClicked(ClickEvent event) {
		hide();
	}

	public void setStatusSuccess() {
		confirmDelete.setStatusSuccess("Deleted!");
		Timer t = new Timer() {

			@Override
			public void run() {
				hide();
			}
		};
		t.schedule(1000);
	}

	public void setStatusError() {
		confirmDelete.setStatusError();
	}

}
