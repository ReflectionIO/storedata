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
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.AbstractPager;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.HasRows;
import com.google.gwt.view.client.Range;
import com.google.gwt.user.client.Window;

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

	@UiField Anchor mShowMore;
	@UiField Anchor mShowLess;
	@UiField LIElement mShowLessItem;
	@UiField LIElement mShowMoreItem;

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
		
		if (this.increment == -1) {
			//go to login
			mShowMore.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					Window.alert("go to login page");
				}
			});			
		} else if (this.increment == -2) {
			//go to upgrade
			mShowMore.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					Window.alert("go to upgrade page");
				}
			});			
		} else {

			// Show more button.
			mShowMore.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					if (((Anchor) event.getSource()).isEnabled()) {
						// Display should be non-null, but we check defensively.
						HasRows display = getDisplay();
						if (display != null) {
							Range range = display.getVisibleRange();
							int pageSize = Math.min(range.getLength() + increment, display.getRowCount() + (display.isRowCountExact() ? 0 : increment));
							display.setVisibleRange(range.getStart(), pageSize);
						}
					}
				}
			});
			mShowLess.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					if (((Anchor) event.getSource()).isEnabled()) {
						// Display should be non-null, but we check defensively.
						HasRows display = getDisplay();
						if (display != null) {
							Range range = display.getVisibleRange();
							int pageSize = Math.max(range.getLength() - increment, increment);
							display.setVisibleRange(range.getStart(), pageSize);
						}
					}
				}
			});
		
		}
		// Hide the buttons by default.
		setDisplay(null);
	}

	@Override
	public void setDisplay(HasRows display) {
		// Hide the buttons if the display is null. If the display is non-null, the
		// buttons will be displayed in onRangeOrRowCountChanged().
		if (display == null) {
			mShowLess.setVisible(false);
			mShowMore.setVisible(false);
		}
		super.setDisplay(display);
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
		mShowLess.setVisible(hasLess);
		mShowMore.setVisible(hasMore);
		
		//set the exception in case of redirect to login or upgrade page 
		if (this.increment == -1 || this.increment == -2)
			mShowLess.setVisible(false);

		if (mLoading) {
			processLoading();
		}
	}

	/**
	 * 
	 */
	private void processLoading() {
		mShowMore.setEnabled(!mLoading);
		mShowLess.setEnabled(!mLoading);

		Anchor visible = mShowMore.isVisible() ? mShowMore : mShowLess;
		
		if (mLoading) {
			if (mSpinner == null) {
				mSpinner = new Image(Images.INSTANCE.spinner());
			}

			visible.getElement().setInnerHTML("Loading... ");
			visible.getElement().appendChild(mSpinner.getElement());

			mShowMoreItem.addClassName("disabled");
			mShowLessItem.addClassName("disabled");
		} else {
			visible.getElement().setInnerHTML("Show " + (visible == mShowMore ? "More" : "Less"));

			mShowMoreItem.removeClassName("disabled");
			mShowLessItem.removeClassName("disabled");
		}
	}

	/**
	 * Visible for testing.
	 */
	boolean isShowLessButtonVisible() {
		return mShowLess.isVisible();
	}

	/**
	 * Visible for testing.
	 */
	boolean isShowMoreButtonVisible() {
		return mShowMore.isVisible();
	}

	public void setLoading(boolean value) {
		mLoading = value;

		if (!mLoading) {
			processLoading();
		}
	}
}
