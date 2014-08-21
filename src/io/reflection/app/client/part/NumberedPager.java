//
//  SimplePager.java
//  storedata
//
//  Created by William Shakour (billy1380) on 10 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.part;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.UListElement;
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

    private final int mFastForwardRows;
    
    @UiField HTMLPanel htmlPanel ;

    @UiField MyAnchor mFirstPage;
    
    @UiField MyAnchor mLastPage;

    @UiField MyAnchor mNextPage;

    @UiField MyAnchor mPrevPage;

    /**
     * Construct a {@link NumberedPager} with the specified text location.
     * 
     */
    @UiConstructor
    public NumberedPager() {
        this(false, DEFAULT_FAST_FORWARD_ROWS, false);
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

        this.mFastForwardRows = fastForwardRows;

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
        
        mPrevPage.setEnabled(false);
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

    @Override
    public void setPageStart(int index) {
        super.setPageStart(index);
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
        HasRows display = getDisplay();
        generateNumberLinks();

        // Update the prev and first buttons.
        setPrevPageButtonsDisabled(!hasPreviousPage());

        // Update the next and last buttons.
        if (isRangeLimited() || !display.isRowCountExact()) {
            setNextPageButtonsDisabled(!hasNextPage());

        }
    }

    
    List<Anchor> pageAnchors = new ArrayList<Anchor>();
    
    /**
     * 
     */
    private void generateNumberLinks() {

        BootstrapGwtNumberedPager.INSTANCE.styles().ensureInjected();
       htmlPanel.clear();
       htmlPanel.add(mPrevPage);
        pageAnchors.clear();
        for (int i = 1; i < 3; i++) {
            LIElement li = Document.get().createLIElement();
            
            //This anchor clickhandler doesn't work.
            //neither does wrapping it with Anchor.wrap.
            
            Anchor anchor = new Anchor();
//            AnchorElement anchor = Document.get().createAnchorElement();
            final int page = i ;
            pageAnchors.add(anchor); //so the main widget doesn't get garbage collected, but the element not.
            anchor.addClickHandler(new ClickHandler(){

                @Override
                public void onClick(ClickEvent event) {
                    NumberedPager thePager = NumberedPager.this ;
                    thePager.setPageStart((page - 1) * thePager.getPageSize());
                }});
            anchor.getElement().appendChild(Document.get().createTextNode(Integer.toString(i)));
        
            
            if (this.getPage() == i -1) //current page
            {
                anchor.setStyleName(BootstrapGwtNumberedPager.INSTANCE.styles().selected());
                anchor.setEnabled(false);
                
            }
            anchor.addStyleName(BootstrapGwtNumberedPager.INSTANCE.styles().spaceApart());
            li.appendChild(anchor.getElement());
            htmlPanel.add(anchor);//.appendChild(anchor.getElement());
        }

        htmlPanel.add(mNextPage);
    }

    /**
     * Check if the next button is disabled. Visible for testing.
     */
    boolean isNextButtonDisabled() {
        return mNextPage.getElement().getClassName().contains("disabled"); //no longer an element, this may not work
    }

    /**
     * Check if the previous button is disabled. Visible for testing.
     */
    boolean isPreviousButtonDisabled() {
        return mPrevPage.getElement().getClassName().contains("disabled"); //no longer an element, this may not work
    }

    /**
     * Get the number of pages to fast forward based on the current page size.
     * 
     * @return the number of pages to fast forward
     */
    private int getFastForwardPages() {
        int pageSize = getPageSize();
        return pageSize > 0 ? mFastForwardRows / pageSize : 0;
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
        if (mFirstPage != null)
            setDisabled(mFirstPage, disabled);
        setDisabled(mPrevPage, disabled);
    }

}
