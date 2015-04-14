package io.reflection.app.client.component;

import java.util.List;
import java.util.Set;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.AbstractHasData;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionModel;

public class CellListElem<T> extends AbstractHasData<T> {

	interface LiTemplate extends SafeHtmlTemplates {
		@Template("<li onclick=\"\" __idx=\"{0}\" class=\"{1}\" style=\"outline:none;\" >{2}</li>")
		SafeHtml li(int idx, String classes, SafeHtml cellContents);
	}

	private static final int DEFAULT_PAGE_SIZE = 25;

	private static final LiTemplate LI_TEMPLATE = GWT.create(LiTemplate.class);

	// private int pageSize = DEFAULT_PAGE_SIZE;
	private final Cell<T> cell;
	private boolean cellIsEditing;
	private final Element childContainer;
	private final SimplePanel emptyListWidgetContainer = new SimplePanel();
	private final SimplePanel loadingIndicatorContainer = new SimplePanel();
	/**
	 * A {@link DeckPanel} to hold widgets associated with various loading states.
	 */
	private final DeckPanel messagesPanel = new DeckPanel();
	private ValueUpdater<T> valueUpdater;

	private String liStyle = "";

	/**
	 * @param elem
	 * @param pageSize
	 * @param keyProvider
	 */
	public CellListElem(Boolean isOrderedList, Cell<T> cell) {
		this(isOrderedList, cell, null);
	}

	public CellListElem(Boolean isOrderedList, Cell<T> cell, ProvidesKey<T> keyProvider) {
		super(Document.get().createDivElement(), DEFAULT_PAGE_SIZE, keyProvider);
		this.cell = cell;
		childContainer = (isOrderedList ? Document.get().createOLElement() : Document.get().createULElement());
		getElement().appendChild(childContainer);
		getElement().appendChild(messagesPanel.getElement());
		messagesPanel.add(emptyListWidgetContainer);
		messagesPanel.add(loadingIndicatorContainer);
	}

	public void setCellStyle(String styleName) {
		liStyle = styleName;
	}

	public void setEmptyListWidget(Widget widget) {
		emptyListWidgetContainer.setWidget(widget);
	}

	/**
	 * Set the widget to display when the data is loading.
	 * 
	 * @param widget
	 *            the loading indicator
	 */
	public void setLoadingIndicator(Widget widget) {
		loadingIndicatorContainer.setWidget(widget);
	}

	/**
	 * Set the value updater to use when cells modify items.
	 * 
	 * @param valueUpdater
	 *            the {@link ValueUpdater}
	 */
	public void setValueUpdater(ValueUpdater<T> valueUpdater) {
		this.valueUpdater = valueUpdater;
	}

	/**
	 * Fire an event to the cell.
	 * 
	 * @param context
	 *            the {@link Context} of the cell
	 * @param event
	 *            the event that was fired
	 * @param parent
	 *            the parent of the cell
	 * @param value
	 *            the value of the cell
	 */
	protected void fireEventToCell(Context context, Event event, Element parent, T value) {
		Set<String> consumedEvents = cell.getConsumedEvents();
		if (consumedEvents != null && consumedEvents.contains(event.getType())) {
			cell.onBrowserEvent(context, parent, value, event, valueUpdater);
			cellIsEditing = cell.isEditing(context, parent, value);
		}
	}

	/**
	 * Return the cell used to render each item.
	 */
	protected Cell<T> getCell() {
		return cell;
	}

	/**
	 * Get the parent element that wraps the cell from the list item. Override this method if you add structure to the element.
	 * 
	 * @param item
	 *            the row element that wraps the list item
	 * @return the parent element of the cell
	 */
	protected Element getCellParent(Element item) {
		return item;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.cellview.client.AbstractHasData#dependsOnSelection()
	 */
	@Override
	protected boolean dependsOnSelection() {
		return cell.dependsOnSelection();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.cellview.client.AbstractHasData#getChildContainer()
	 */
	@Override
	protected Element getChildContainer() {
		return childContainer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.cellview.client.AbstractHasData#getKeyboardSelectedElement()
	 */
	@Override
	protected Element getKeyboardSelectedElement() {
		// Do not use getRowElement() because that will flush the presenter.
		int rowIndex = getKeyboardSelectedRow();
		if (rowIndex >= 0 && childContainer.getChildCount() > rowIndex) { return childContainer.getChild(rowIndex).cast(); }
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.cellview.client.AbstractHasData#isKeyboardNavigationSuppressed()
	 */
	@Override
	protected boolean isKeyboardNavigationSuppressed() {
		return cellIsEditing;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.cellview.client.AbstractHasData#renderRowValues(com.google.gwt.safehtml.shared.SafeHtmlBuilder, java.util.List, int,
	 * com.google.gwt.view.client.SelectionModel)
	 */
	@Override
	protected void renderRowValues(SafeHtmlBuilder sb, List<T> values, int start, SelectionModel<? super T> selectionModel)
			throws UnsupportedOperationException {

		// String keyboardSelectedItem = " " + style.cellListKeyboardSelectedItem();
		// String selectedItem = " " + style.cellListSelectedItem();
		// String evenItem = style.cellListEvenItem();
		// String oddItem = style.cellListOddItem();
		// int keyboardSelectedRow = getKeyboardSelectedRow() + getPageStart();
		int length = values.size();
		int end = start + length;
		for (int i = start; i < end; i++) {
			T value = values.get(i - start);
			// boolean isSelected = selectionModel == null ? false : selectionModel.isSelected(value);

			StringBuilder classesBuilder = new StringBuilder();
			classesBuilder.append(liStyle);
			// classesBuilder.append(i % 2 == 0 ? evenItem : oddItem);
			// if (isSelected) {
			// classesBuilder.append(selectedItem);
			// }
			// if (i == keyboardSelectedRow) {
			// classesBuilder.append(keyboardSelectedItem);
			// }

			SafeHtmlBuilder cellBuilder = new SafeHtmlBuilder();
			Context context = new Context(i, 0, getValueKey(value));
			cell.render(context, value, cellBuilder);
			sb.append(LI_TEMPLATE.li(i, classesBuilder.toString(), cellBuilder.toSafeHtml()));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.cellview.client.AbstractHasData#resetFocusOnCell()
	 */
	@Override
	protected boolean resetFocusOnCell() {
		int row = getKeyboardSelectedRow();
		if (isRowWithinBounds(row)) {
			Element rowElem = getKeyboardSelectedElement();
			Element cellParent = getCellParent(rowElem);
			T value = getVisibleItem(row);
			Context context = new Context(row + getPageStart(), 0, getValueKey(value));
			return cell.resetFocus(context, cellParent, value);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.cellview.client.AbstractHasData#setKeyboardSelected(int, boolean, boolean)
	 */
	@Override
	protected void setKeyboardSelected(int index, boolean selected, boolean stealFocus) {}

}
