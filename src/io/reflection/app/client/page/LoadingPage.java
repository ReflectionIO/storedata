//
//  LoadingPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 19 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import io.reflection.app.api.core.shared.call.GetCountriesRequest;
import io.reflection.app.api.core.shared.call.GetCountriesResponse;
import io.reflection.app.api.core.shared.call.GetRolesAndPermissionsRequest;
import io.reflection.app.api.core.shared.call.GetRolesAndPermissionsResponse;
import io.reflection.app.api.core.shared.call.GetStoresRequest;
import io.reflection.app.api.core.shared.call.GetStoresResponse;
import io.reflection.app.api.core.shared.call.LoginRequest;
import io.reflection.app.api.core.shared.call.LoginResponse;
import io.reflection.app.api.core.shared.call.event.GetCountriesEventHandler;
import io.reflection.app.api.core.shared.call.event.GetRolesAndPermissionsEventHandler;
import io.reflection.app.api.core.shared.call.event.GetStoresEventHandler;
import io.reflection.app.api.core.shared.call.event.LoginEventHandler;
import io.reflection.app.client.controller.CountryController;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.controller.StoreController;
import io.reflection.app.client.handler.NavigationEventHandler;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class LoadingPage extends Page implements NavigationEventHandler, LoginEventHandler, GetCountriesEventHandler, GetStoresEventHandler,
		GetRolesAndPermissionsEventHandler {

	private static LoadingPageUiBinder uiBinder = GWT.create(LoadingPageUiBinder.class);

	interface LoadingPageUiBinder extends UiBinder<Widget, LoadingPage> {}

	interface LoadingPageStyle extends CssResource {
		String purpleBar();

		String instantBar();
	}

	@UiField LoadingPageStyle style;

	@UiField ParagraphElement task;
	@UiField DivElement bar;
	@UiField SpanElement description;

	private List<Runnable> tasks = new ArrayList<Runnable>();
	private List<String> taskNames = new ArrayList<String>();

	private int current;

	public LoadingPage() {
		initWidget(uiBinder.createAndBindUi(this));

		addTask(new Runnable() {

			@Override
			public void run() {
				boolean attemptRestoreSession = SessionController.get().restoreSession();

				if (!attemptRestoreSession) {
					runTask(++current);
				}
			}
		}, "Restoring user session...");

		addTask(new Runnable() {

			@Override
			public void run() {

				boolean attemptPreload = SessionController.get().prefetchRolesAndPermissions();

				if (!attemptPreload) {
					runTask(++current);
				}
			}
		}, "Loading user account...");

		addTask(new Runnable() {

			@Override
			public void run() {
				boolean attemptPreload = CountryController.get().prefetchCountries();

				if (!attemptPreload) {
					runTask(++current);
				}
			}
		}, "Loading countries...");

		addTask(new Runnable() {

			@Override
			public void run() {
				boolean attemptPreload = StoreController.get().prefetchStores();

				if (!attemptPreload) {
					runTask(++current);
				}
			}
		}, "Loading stores...");

		// TODO: preload categories for all stores
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		resetProgressBar();

		register(EventController.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));
		register(EventController.get().addHandlerToSource(LoginEventHandler.TYPE, SessionController.get(), this));
		register(EventController.get().addHandlerToSource(GetRolesAndPermissionsEventHandler.TYPE, SessionController.get(), this));
		register(EventController.get().addHandlerToSource(GetCountriesEventHandler.TYPE, CountryController.get(), this));
		register(EventController.get().addHandlerToSource(GetStoresEventHandler.TYPE, StoreController.get(), this));
	}

	/**
	 * 
	 */
	private void resetProgressBar() {
		setProgress("Loading", 0, tasks.size());
		bar.getParentElement().setClassName("progress progress-striped active");
		bar.setClassName("progress-bar " + style.purpleBar());
	}

	private void setProgressBarError() {
		bar.getParentElement().setClassName("progress");
		bar.setClassName("progress-bar progress-bar-danger " + style.instantBar());
	}

	private void setProgressBarComplete() {
		bar.getParentElement().setClassName("progress");
		bar.setClassName("progress-bar progress-bar-success " + style.instantBar());
	}

	/**
	 * @param task
	 * @param taskName
	 * @param handlerRegistration
	 */
	private void addTask(Runnable task, String taskName) {
		tasks.add(task);
		taskNames.add(taskName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack stack) {
		if (PageType.LoadingPageType.equals(stack.getPage())) {
			runTask(current);
		}
	}

	/**
	 * @param i
	 */
	private void runTask(int i) {
		if (i < tasks.size()) {
			setProgress(taskNames.get(i), i + 1, tasks.size());
			tasks.get(i).run();
		} else {
			setProgressBarComplete();
			setProgress("Done! :)", 1, 1);

			(new Timer() {
				@Override
				public void run() {
					if (SessionController.get().getLoggedInUser() != null || PageType.LoginPageType.equals(NavigationController.get().getIntendedPage())) {
						NavigationController.get().showIntendedPage();
					} else {
						PageType.LoginPageType.show();
					}
				}
			}).schedule(600);

		}
	}

	private void setProgress(String doing, int progress, int total) {
		bar.setAttribute("aria-valuenow", Integer.toString(progress));
		bar.setAttribute("aria-valuemin", "0");
		bar.setAttribute("aria-valuemax", Integer.toString(total));

		int percentageProgress = (int) (((float) progress / (float) total) * 100.0f);

		bar.getStyle().setWidth(percentageProgress, Unit.PCT);

		description.setInnerText(Integer.toString(percentageProgress) + "%");
		task.setInnerHTML(doing);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.core.shared.call.event.GetCountriesEventHandler#getCountriesSuccess(io.reflection.app.api.core.shared.call.GetCountriesRequest,
	 * io.reflection.app.api.core.shared.call.GetCountriesResponse)
	 */
	@Override
	public void getCountriesSuccess(GetCountriesRequest input, GetCountriesResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {
			runTask(++current);
		} else {
			showRefreshToRetryMessage();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.core.shared.call.event.GetCountriesEventHandler#getCountriesFailure(io.reflection.app.api.core.shared.call.GetCountriesRequest,
	 * java.lang.Throwable)
	 */
	@Override
	public void getCountriesFailure(GetCountriesRequest input, Throwable caught) {
		showRefreshToRetryMessage();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.GetStoresEventHandler#getStoresSuccess(io.reflection.app.api.core.shared.call.GetStoresRequest,
	 * io.reflection.app.api.core.shared.call.GetStoresResponse)
	 */
	@Override
	public void getStoresSuccess(GetStoresRequest input, GetStoresResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {
			runTask(++current);
		} else {
			showRefreshToRetryMessage();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.GetStoresEventHandler#getStoresFailure(io.reflection.app.api.core.shared.call.GetStoresRequest,
	 * java.lang.Throwable)
	 */
	@Override
	public void getStoresFailure(GetStoresRequest input, Throwable caught) {
		showRefreshToRetryMessage();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.core.shared.call.event.GetRolesAndPermissionsEventHandler#getRolesAndPermissionsSuccess(io.reflection.app.api.core.shared.call.
	 * GetRolesAndPermissionsRequest, io.reflection.app.api.core.shared.call.GetRolesAndPermissionsResponse)
	 */
	@Override
	public void getRolesAndPermissionsSuccess(GetRolesAndPermissionsRequest input, GetRolesAndPermissionsResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {
			runTask(++current);
		} else {
			showRefreshToRetryMessage();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.core.shared.call.event.GetRolesAndPermissionsEventHandler#getRolesAndPermissionsFailure(io.reflection.app.api.core.shared.call.
	 * GetRolesAndPermissionsRequest, java.lang.Throwable)
	 */
	@Override
	public void getRolesAndPermissionsFailure(GetRolesAndPermissionsRequest input, Throwable caught) {
		showRefreshToRetryMessage();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.LoginEventHandler#loginSuccess(io.reflection.app.api.core.shared.call.LoginRequest,
	 * io.reflection.app.api.core.shared.call.LoginResponse)
	 */
	@Override
	public void loginSuccess(LoginRequest input, LoginResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {
			if (SessionController.get().getLoggedInUser() != null) {
				current = 0;
			}

			runTask(++current);
		} else {
			showRefreshToRetryMessage();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.LoginEventHandler#loginFailure(io.reflection.app.api.core.shared.call.LoginRequest,
	 * java.lang.Throwable)
	 */
	@Override
	public void loginFailure(LoginRequest input, Throwable caught) {
		showRefreshToRetryMessage();
	}

	private void showRefreshToRetryMessage() {
		setProgressBarError();
		setProgress("An error occured - Press refresh to retry! :'(", 1, 1);
	}
}
