//
//  Footer.java
//  storedata
//
//  Created by William Shakour (billy1380) on 3 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.part;

import static io.reflection.app.client.controller.FilterController.OVERALL_LIST_TYPE;
import io.reflection.app.api.shared.datatypes.Session;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.controller.FilterController.Filter;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.handler.FilterEventHandler;
import io.reflection.app.client.handler.user.SessionEventHandler;
import io.reflection.app.client.page.PageType;
import io.reflection.app.client.res.Styles;
import io.reflection.app.datatypes.shared.User;

import java.util.Date;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.Error;

/**
 * @author billy1380
 * 
 */
/**
 * @author billy1380
 *
 */
/**
 * @author billy1380
 * 
 */
public class Footer extends Composite implements FilterEventHandler, SessionEventHandler {

	private static FooterUiBinder uiBinder = GWT.create(FooterUiBinder.class);

	interface FooterUiBinder extends UiBinder<Widget, Footer> {}

	@UiField FocusPanel footer;
	@UiField SpanElement year;
	@UiField SpanElement yearXs;
	@UiField Anchor mArrow;

	@UiField InlineHyperlink ranks;
	@UiField InlineHyperlink terms;
	@UiField HTMLPanel links;

	private boolean open;
	private HandlerRegistration filterChangedRegistration;
	private HandlerRegistration sessionRegistration;

	private final int FOOTER_HIDDEN_BOTTOM_LINKS = -125;
	private final int FOOTER_HIDDEN_BOTTOM_LINKLESS = -80;

	private final int FOOTER_HEIGHT_LINKS = 165;
	private final int FOOTER_HEIGHT_LINKLESS = 120;

	private int footerBottom = FOOTER_HIDDEN_BOTTOM_LINKS;
	private int footerHeight = FOOTER_HEIGHT_LINKS;

	@SuppressWarnings("deprecation")
	public Footer() {
		initWidget(uiBinder.createAndBindUi(this));

		Styles.INSTANCE.reflection().ensureInjected();

		open = false;

		if (SessionController.get().isValidSession()) {
			footerBottom = FOOTER_HIDDEN_BOTTOM_LINKS;
			footerHeight = FOOTER_HEIGHT_LINKS;
		} else {
			footerBottom = FOOTER_HIDDEN_BOTTOM_LINKLESS;
			footerHeight = FOOTER_HEIGHT_LINKLESS;
		}

		footer.getElement().getStyle().setBottom(footerBottom, Unit.PX);
		footer.getElement().getStyle().setHeight(footerHeight, Unit.PX);

		year.setInnerHTML(Integer.toString(1900 + (new Date()).getYear()));
		yearXs.setInnerHTML(Integer.toString(1900 + (new Date()).getYear()));

		ranks.setTargetHistoryToken(PageType.RanksPageType.asTargetHistoryToken("view", OVERALL_LIST_TYPE, FilterController.get().asRankFilterString()));

		removeLeaderboard();
		removeTerms();

		links.setVisible(false);
	}

	private void removeTerms() {
		terms.removeFromParent();
	}

	private void addTerms() {
		links.getElement().appendChild(terms.getElement());
	}

	private void removeLeaderboard() {
		ranks.removeFromParent();
	}

	private void addLeaderboard() {
		links.getElement().appendChild(ranks.getElement());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		filterChangedRegistration = EventController.get().addHandlerToSource(FilterEventHandler.TYPE, FilterController.get(), this);
		sessionRegistration = EventController.get().addHandlerToSource(SessionEventHandler.TYPE, SessionController.get(), this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onDetach()
	 */
	@Override
	protected void onDetach() {

		if (filterChangedRegistration != null) {
			filterChangedRegistration.removeHandler();
		}

		if (sessionRegistration != null) {
			sessionRegistration.removeHandler();
		}

		super.onDetach();
	}

	@UiHandler("footer")
	void onClickFooter(ClickEvent event) {
		if (open) {
			mArrow.setStyleName(Styles.INSTANCE.reflection().footerUpArrow());
			footer.getElement().getStyle().setBottom(footerBottom, Unit.PX);
		} else {
			mArrow.setStyleName(Styles.INSTANCE.reflection().footerDownArrow());
			footer.getElement().getStyle().setBottom(0, Unit.PX);
		}
		open = !open;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.FilterEventHandler#filterParamChanged(java.lang.String, java.lang.Object, java.lang.Object)
	 */
	@Override
	public <T> void filterParamChanged(String name, T currentValue, T previousValue) {
		ranks.setTargetHistoryToken(PageType.RanksPageType.asTargetHistoryToken("view", OVERALL_LIST_TYPE, FilterController.get().asRankFilterString()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.FilterEventHandler#filterParamsChanged(io.reflection.app.client.controller.FilterController.Filter, java.util.Map)
	 */
	@Override
	public void filterParamsChanged(Filter currentFilter, Map<String, ?> previousValues) {
		ranks.setTargetHistoryToken(PageType.RanksPageType.asTargetHistoryToken("view", OVERALL_LIST_TYPE, FilterController.get().asRankFilterString()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.SessionEventHandler#userLoggedIn(io.reflection.app.datatypes.shared.User,
	 * io.reflection.app.api.shared.datatypes.Session)
	 */
	@Override
	public void userLoggedIn(User user, Session session) {

		// Set new height and close
		footerBottom = FOOTER_HIDDEN_BOTTOM_LINKS;
		footerHeight = FOOTER_HEIGHT_LINKS;
		footer.getElement().getStyle().setHeight(footerHeight, Unit.PX);
		footer.getElement().getStyle().setBottom(footerBottom, Unit.PX);
		mArrow.setStyleName(Styles.INSTANCE.reflection().footerUpArrow());
		open = false;

		// Add links
		addLeaderboard();
		addTerms();
		links.setVisible(true);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.SessionEventHandler#userLoggedOut()
	 */
	@Override
	public void userLoggedOut() {

		// Set new height and close
		footerBottom = FOOTER_HIDDEN_BOTTOM_LINKLESS;
		footerHeight = FOOTER_HEIGHT_LINKLESS;
		footer.getElement().getStyle().setHeight(footerHeight, Unit.PX);
		footer.getElement().getStyle().setBottom(footerBottom, Unit.PX);
		mArrow.setStyleName(Styles.INSTANCE.reflection().footerUpArrow());
		open = false;

		// Remove links
		removeLeaderboard();
		removeTerms();
		links.setVisible(false);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.SessionEventHandler#userLoginFailed(com.willshex.gson.json.service.shared.Error)
	 */
	@Override
	public void userLoginFailed(Error error) {}

}
