//
//  SimplePager.java
//  storedata
//
//  Created by William Shakour (billy1380) on 10 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.part;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.AbstractPager;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.HasRows;
import com.google.gwt.view.client.Range;

/**
 * @author Daniel Gerson
 * 
 */
public class NumberedPager extends AbstractPager {

    private static NumberedPagerUiBinder uiBinder = GWT.create(NumberedPagerUiBinder.class);

    interface NumberedPagerUiBinder extends UiBinder<Widget, NumberedPager> {}

    private static final int DEFAULT_FAST_FORWARD_ROWS = 1000;

    // private final int mFastForwardRows;

    @UiField HTMLPanel htmlPanel;

    @UiField MyAnchor mFirstPage;

    @UiField InlineLabel firstBar;

    @UiField MyAnchor mLastPage;

    @UiField InlineLabel lastBar;

    @UiField MyAnchor mNextPage;

    @UiField MyAnchor mPrevPage;

    /**
     * Construct a {@link NumberedPager} with the specified text location.
     * 
     */
    @UiConstructor
    public NumberedPager() {
        this(true, DEFAULT_FAST_FORWARD_ROWS, true);
    }

    /**
     * Construct a {@link NumberedPager} with the default resources, fast forward rows and default image button names.
     * 
     * @param showFirstPageButton
     *            if true, show a fast-forward button that advances by a larger increment than a single page
     * @param showLastPageButton
     *            if true, show a button to go the the last page
     */
    public NumberedPager(boolean showFirstPageButton, boolean showLastPageButton) {
        this(showFirstPageButton, DEFAULT_FAST_FORWARD_ROWS, showLastPageButton);
    }

    /**
     * Construct a {@link NumberedPager} with the specified resources.
     * 
     * @param showFastForwardButton
     *            if true, show a fast-forward button that advances by a larger increment than a single page
     * @param fastForwardRows
     *            the number of rows to jump when fast forwarding
     * @param showLastPageButton
     *            if true, show a button to go the the last page
     * @param imageButtonConstants
     *            Constants that contain the image button names
     */
    public NumberedPager(boolean showFirstPageButton, final int fastForwardRows, boolean showLastPageButton) {
        initWidget(uiBinder.createAndBindUi(this));

        BootstrapGwtNumberedPager.INSTANCE.styles().ensureInjected();

        // this.mFastForwardRows = fastForwardRows;

        if (!showLastPageButton) {
            mLastPage.removeFromParent();
            mLastPage = null;
        }

        if (!showFirstPageButton) {
            mFirstPage.removeFromParent();
            mFirstPage = null;
        }

        // Disable the buttons by default.
        setDisplay(null);
    }

    @UiHandler("mFirstPage")
    void onFirstPageClicked(ClickEvent e) {
        firstPage();
    }

    @UiHandler("mNextPage")
    void onNextPageClicked(ClickEvent e) {
        nextPage();
    }

    @UiHandler("mPrevPage")
    void onPrevPageClicked(ClickEvent e) {
        previousPage();
    }

    protected void previousPage() {
        if (getDisplay() != null) {
            Range range = getDisplay().getVisibleRange();
            int remainder = range.getStart() % range.getLength();
            int index = range.getStart() - range.getLength() - remainder;
            index = Math.max(index, 0);
            setPageStart(index);
        }
    }

    @UiHandler("mLastPage")
    void onLastPageClicked(ClickEvent e) {
        lastPage();
    }

    @Override
    public void firstPage() {
        super.firstPage();
    }

    @Override
    public void setDisplay(HasRows display) {
        // Enable or disable all buttons.
        boolean disableButtons = (display == null);

        setNextPageButtonsDisabled(disableButtons);
        setPrevPageButtonsDisabled(disableButtons);

        super.setDisplay(display);
    }

    @Override
    public void setPage(int index) {
        super.setPage(index);
    }

    @Override
    public void setPageSize(int pageSize) {
        super.setPageSize(pageSize);
    }

    /**
     * Adapted from the solution proposed here. https://groups.google.com/forum/#!topic/google-web-toolkit/RedwgreWKB0
     */
    @Override
    public void setPageStart(int index) {
        if (getDisplay() != null) {
            Range range = getDisplay().getVisibleRange();
            int pageSize = range.getLength();
            index = Math.max(0, index);
            if (index != range.getStart()) {
                getDisplay().setVisibleRange(index, pageSize);
            }
        }
    }

    /**
     * Let the page know that the table is loading. Call this method to clear all data from the table and hide the current range when new data is being loaded
     * into the table.
     */
    public void startLoading() {
        getDisplay().setRowCount(0, true);
        clearNumbers();
    }

    /**
     * 
     */
    private void clearNumbers() {

        htmlPanel.clear();
        htmlPanel.add(mPrevPage);
        htmlPanel.add(mNextPage);
    }

    /**
     * Get the text to display in the pager that reflects the state of the pager.
     * 
     * @return the text
     */
    protected String createText() {
        // Default text is 1 based.
        NumberFormat formatter = NumberFormat.getFormat("#,###");
        HasRows display = getDisplay();
        Range range = display.getVisibleRange();
        int pageStart = range.getStart() + 1;
        int pageSize = range.getLength();
        int dataSize = display.getRowCount();
        int endIndex = Math.min(dataSize, pageStart + pageSize - 1);
        endIndex = Math.max(pageStart, endIndex);
        boolean exact = display.isRowCountExact();
        return formatter.format(pageStart) + "-" + formatter.format(endIndex) + (exact ? " of " : " of over ") + formatter.format(dataSize);
    }

    @Override
    protected void onRangeOrRowCountChanged() {
        // HasRows display = getDisplay();
        generateNumberLinks();

        // Update the prev and first buttons.
        setPrevPageButtonsDisabled(!hasPreviousPage());

        // Update the next and last buttons.
        setNextPageButtonsDisabled(!hasNextPage());

    }

    /**
     * 
     */
    private void generateNumberLinks() {

        // this doesn't seem to clear all elements, only fields!
        htmlPanel.clear();
        if (getPageCount() > 1) {
            if (mFirstPage != null) {
                htmlPanel.add(mFirstPage);
                htmlPanel.add(firstBar);
            }
            htmlPanel.add(mPrevPage);
            for (int i = 1; i <= getPageCount(); i++) {
                LIElement li = Document.get().createLIElement();

                Anchor anchor = new Anchor();
                final int page = i;
                anchor.addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        NumberedPager thePager = NumberedPager.this;
                        thePager.setPageStart((page - 1) * thePager.getPageSize());
                    }
                });
                anchor.getElement().setInnerHTML(Integer.toString(i));

                // current page
                if (this.getPage() == i - 1) {
                    anchor.setStyleName(BootstrapGwtNumberedPager.INSTANCE.styles().selected());
                    anchor.setEnabled(false);

                }
                anchor.addStyleName(BootstrapGwtNumberedPager.INSTANCE.styles().spaceApart());
                li.appendChild(anchor.getElement());
                htmlPanel.add(anchor);
            }

            htmlPanel.add(mNextPage);
            if (mLastPage != null) {
                htmlPanel.add(lastBar);
                htmlPanel.add(mLastPage);
            }
        }
    }

    /**
     * Check if the next button is disabled. Visible for testing.
     */
    boolean isNextButtonDisabled() {
        return mNextPage.getElement().getClassName().contains("disabled"); // no longer an element, this may not work
    }

    /**
     * Check if the previous button is disabled. Visible for testing.
     */
    boolean isPreviousButtonDisabled() {
        return mPrevPage.getElement().getClassName().contains("disabled"); // no longer an element, this may not work
    }

    void setDisabled(FocusWidget e, boolean disabled) {
        e.setEnabled(!disabled);
    }

    /**
     * Enable or disable the next page buttons.
     * 
     * @param disabled
     *            true to disable, false to enable
     */
    private void setNextPageButtonsDisabled(boolean disabled) {
        setDisabled(mNextPage, disabled);
        if (mLastPage != null) {
            setDisabled(mLastPage, disabled);
        }
    }

    /**
     * Enable or disable the previous page buttons.
     * 
     * @param disabled
     *            true to disable, false to enable
     */
    private void setPrevPageButtonsDisabled(boolean disabled) {
        if (mFirstPage != null) {
            setDisabled(mFirstPage, disabled);
        }
        setDisabled(mPrevPage, disabled);
    }

}
