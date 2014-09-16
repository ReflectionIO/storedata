//
//  PageSizePager.java
//  storedata
//
//  Created by William Shakour (billy1380) on 11 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.part;

import io.reflection.app.client.res.Images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.AbstractPager;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.HasRows;
import com.google.gwt.view.client.Range;

/**
 * @author billy1380
 * 
 */
public class PageSizePager extends AbstractPager {

	private static PageSizePagerUiBinder uiBinder = GWT.create(PageSizePagerUiBinder.class);

	interface PageSizePagerUiBinder extends UiBinder<Widget, PageSizePager> {}

	/**
	 * The increment by which to grow or shrink the page size.
	 */
	private final int increment;

	@UiField Button viewLessButton;
	@UiField Button viewMoreButton;

	private Image mSpinner;

	private boolean mLoading;

	/**
	 * Construct a PageSizePager with a given increment.
	 * 
	 * @param increment
	 *            the amount by which to increase the page size
	 */
	@UiConstructor
	public PageSizePager(final int increment) {
		initWidget(uiBinder.createAndBindUi(this));

		this.increment = increment;

		// Hide the buttons by default.
		setDisplay(null);

		setViewMoreText("View More"); // Set default text
	}

	public void setViewMoreText(String s) {
		viewMoreButton.setHTML(s);
	}

	public void setViewLessText(String s) {
		viewMoreButton.setHTML(s);
	}

	@Override
	public void setDisplay(HasRows display) {
		// Hide the buttons if the display is null. If the display is non-null, the
		// buttons will be displayed in onRangeOrRowCountChanged().
		if (display == null) {
			viewLessButton.setVisible(false);
			viewMoreButton.setVisible(false);
		}
		super.setDisplay(display);
	}

	@UiHandler("viewLessButton")
	void onviewLessButtonClicked(ClickEvent event) {
		if (((Button) event.getSource()).isEnabled()) {
			// Display should be non-null, but we check defensively.
			HasRows display = getDisplay();
			if (display != null) {
				Range range = display.getVisibleRange();
				int pageSize = Math.max(range.getLength() - increment, increment);
				display.setVisibleRange(range.getStart(), pageSize);
			}
		}
	}

	@UiHandler("viewMoreButton")
	void onviewMoreButtonClicked(ClickEvent event) {
		if (((Button) event.getSource()).isEnabled()) {
			// Display should be non-null, but we check defensively.
			HasRows display = getDisplay();
			if (display != null) {
				Range range = display.getVisibleRange();
				int pageSize = Math.min(range.getLength() + increment, display.getRowCount() + (display.isRowCountExact() ? 0 : increment));
				display.setVisibleRange(range.getStart(), pageSize);
			}
		}
	}

	@Override
	public void setPageSize(int pageSize) {
		super.setPageSize(pageSize);
	}

	@Override
	protected void onRangeOrRowCountChanged() {
		// Assumes a page start index of 0.
		HasRows display = getDisplay();
		int pageSize = display.getVisibleRange().getLength();
		boolean hasLess = pageSize > increment;
		boolean hasMore = !display.isRowCountExact() || pageSize < display.getRowCount();
		viewLessButton.setVisible(hasLess);
		viewMoreButton.setVisible(hasMore);

		if (mLoading) {
			processLoading();
		}
	}

	/**
	 * 
	 */
	private void processLoading() {
		viewLessButton.setEnabled(!mLoading);
		viewMoreButton.setEnabled(!mLoading);

		Widget visible = viewMoreButton.isVisible() ? viewMoreButton : viewLessButton;

		if (mLoading) {
			if (mSpinner == null) {
				mSpinner = new Image(Images.INSTANCE.spinner());
			}

			visible.getElement().setInnerHTML("Loading... ");
			visible.getElement().appendChild(mSpinner.getElement());

			viewLessButton.setEnabled(Boolean.FALSE);
			viewMoreButton.setEnabled(Boolean.FALSE);
		} else {
			visible.getElement().setInnerHTML("Show " + (visible == viewMoreButton ? "More" : "Less"));

			viewLessButton.setEnabled(Boolean.TRUE);
			viewMoreButton.setEnabled(Boolean.TRUE);
		}
	}

	/**
	 * Visible for testing.
	 */
	// boolean isShowLessButtonVisible() {
	// return viewLessButton.isVisible();
	// }

	/**
	 * Visible for testing.
	 */
	boolean isShowMoreButtonVisible() {
		return viewMoreButton.isVisible();
	}

	public void setLoading(boolean value) {
		mLoading = value;

		if (!mLoading) {
			processLoading();
		}
	}
}
