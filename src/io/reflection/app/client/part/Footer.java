//
//  Footer.java
//  storedata
//
//  Created by William Shakour (billy1380) on 3 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.part;

import static io.reflection.app.client.controller.FilterController.OVERALL_LIST_TYPE;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.controller.FilterController.Filter;
import io.reflection.app.client.handler.FilterEventHandler;
import io.reflection.app.client.page.PageType;
import io.reflection.app.client.res.Styles;

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
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class Footer extends Composite implements FilterEventHandler {

	private static FooterUiBinder uiBinder = GWT.create(FooterUiBinder.class);

	interface FooterUiBinder extends UiBinder<Widget, Footer> {}

	@UiField HTMLPanel mFooter;
	@UiField SpanElement mYear;
	@UiField Anchor mArrow;
	@UiField InlineHyperlink ranks;

	private boolean open;
	private HandlerRegistration filterChangedRegistration;

	@SuppressWarnings("deprecation")
	public Footer() {
		initWidget(uiBinder.createAndBindUi(this));

		Styles.INSTANCE.reflection().ensureInjected();

		open = false;
		mFooter.getElement().getStyle().setBottom(-125, Unit.PX);

		mYear.setInnerHTML(Integer.toString(1900 + (new Date()).getYear()));

		ranks.setTargetHistoryToken(PageType.RanksPageType.asTargetHistoryToken("view", OVERALL_LIST_TYPE, FilterController.get().asRankFilterString()));
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

		super.onDetach();
	}

	@UiHandler("mArrow")
	void onClickArrow(ClickEvent event) {
		if (open) {
			mArrow.setStyleName(Styles.INSTANCE.reflection().footerUpArrow());
			mFooter.getElement().getStyle().setBottom(-125, Unit.PX);
		} else {
			mArrow.setStyleName(Styles.INSTANCE.reflection().footerDownArrow());
			mFooter.getElement().getStyle().setBottom(0, Unit.PX);
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

}
