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
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.controller.CountryController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.controller.StoreController;
import io.reflection.app.client.handler.NavigationEventHandler;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
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

		String orangeBar();

		String progressBar();

	}

	@UiField LoadingPageStyle style;

	@UiField SpanElement task;
	@UiField SpanElement dots;

	@UiField DivElement bar;
	@UiField DivElement bar2;
	@UiField DivElement bar3;
	@UiField DivElement bar4;
	@UiField DivElement bar5;
	@UiField DivElement bar6;
	private List<DivElement> bars = new ArrayList<DivElement>();

	private List<Runnable> tasks = new ArrayList<Runnable>();
	private List<String> taskNames = new ArrayList<String>();

	private int currentTaskIndex;

	public LoadingPage() {
		initWidget(uiBinder.createAndBindUi(this));

		bars.add(bar);
		bars.add(bar2);
		bars.add(bar3);
		bars.add(bar4);
		bars.add(bar5);
		bars.add(bar6);

		addTask(new Runnable() {

			@Override
			public void run() {
				boolean attemptRestoreSession = SessionController.get().restoreSession();

				if (!attemptRestoreSession) {
					runTask(++currentTaskIndex);
				}
			}
		}, "Restoring user session ");

		addTask(new Runnable() {

			@Override
			public void run() {

				boolean attemptPreload = SessionController.get().prefetchRolesAndPermissions();

				if (!attemptPreload) {
					runTask(++currentTaskIndex);
				}
			}
		}, "Loading user account ");

		addTask(new Runnable() {

			@Override
			public void run() {
				boolean attemptPreload = CountryController.get().prefetchCountries();

				if (!attemptPreload) {
					runTask(++currentTaskIndex);
				}
			}
		}, "Loading countries ");

		addTask(new Runnable() {

			@Override
			public void run() {
				boolean attemptPreload = StoreController.get().prefetchStores();

				if (!attemptPreload) {
					runTask(++currentTaskIndex);
				}
			}
		}, "Loading stores ");

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

		register(DefaultEventBus.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(LoginEventHandler.TYPE, SessionController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(GetRolesAndPermissionsEventHandler.TYPE, SessionController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(GetCountriesEventHandler.TYPE, CountryController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(GetStoresEventHandler.TYPE, StoreController.get(), this));

		currentTaskIndex = 0;

		// ((Header) NavigationController.get().getHeader()).setReadOnly(true);
		// ((Footer) NavigationController.get().getFooter()).setVisible(false);

	}

	/**
	 * 
	 */
	private void resetProgressBar() {
		dots.getStyle().setProperty("visibility", "visible");
		setProgress("Loading", 0, tasks.size());
		for (DivElement b : bars) {
			b.removeClassName("progress-bar-danger");
			b.removeClassName(style.orangeBar());
			b.addClassName("progress-bar");
			b.addClassName(style.purpleBar());
			b.addClassName(style.progressBar());
		}
	}

	private void setProgressBarError() {
		dots.getStyle().setProperty("visibility", "hidden");
		for (DivElement b : bars) {
			b.addClassName("progress-bar");
			b.addClassName("progress-bar-danger");
		}
	}

	private void setProgressBarComplete() {
		dots.getStyle().setProperty("visibility", "hidden");
		for (DivElement b : bars) {
			b.addClassName("progress-bar");
			b.addClassName(style.orangeBar());
			b.addClassName(style.progressBar());
		}
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
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) {
		if (PageType.LoadingPageType.equals(current.getPage())) {
			runTask(currentTaskIndex);
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
					if (!SessionController.get().canSeePredictions() && !SessionController.get().isSessionRestored()) {
						PageType.LinkItunesPageType.show();
					} else {
						NavigationController.get().showIntendedPage();
					}
				}
			}).schedule(600);

		}
	}

	private void setProgress(String doing, int progress, int total) {
		int percentageProgress = (int) (((float) progress / (float) total) * 66.0f);
		for (DivElement b : bars) {
			b.setAttribute("aria-valuenow", Integer.toString(progress));
			b.setAttribute("aria-valuemin", "0");
			b.setAttribute("aria-valuemax", Integer.toString(total));
			b.getStyle().setWidth(percentageProgress, Unit.PX);
		}

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
			runTask(++currentTaskIndex);
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
			runTask(++currentTaskIndex);
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
			runTask(++currentTaskIndex);
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
				currentTaskIndex = 0;
			}

			runTask(++currentTaskIndex);
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
