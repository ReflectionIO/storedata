//
//  ExpandableCellTableBuilder.java
//  storedata
//
//  Created by William Shakour (billy1380) on 16 Dec 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.builder.shared.DivBuilder;
import com.google.gwt.dom.builder.shared.TableCellBuilder;
import com.google.gwt.dom.builder.shared.TableRowBuilder;
import com.google.gwt.dom.client.Style.OutlineStyle;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.AbstractCellTable.Style;
import com.google.gwt.user.cellview.client.AbstractCellTableBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.RowStyles;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SelectionModel.AbstractSelectionModel;
import com.google.gwt.view.client.SetSelectionModel;

/**
 * @author William Shakour (billy1380)
 *
 */
public class ExpandableCellTableBuilder<T, U> extends AbstractCellTableBuilder<T> {

	private Column<T, U> expandColumn = null;
	private PlaceHolderColumn<T, ?> placeholderColumn = null;

	private final String evenRowStyle;
	private final String oddRowStyle;
	private final String selectedRowStyle;
	private final String cellStyle;
	private final String evenCellStyle;
	private final String oddCellStyle;
	private final String firstColumnStyle;
	private final String lastColumnStyle;
	private final String selectedCellStyle;

	public static class ExpandMultiSelectionModel<T> extends AbstractSelectionModel<T> implements SetSelectionModel<T> {

		Map<Object, T> selected = new HashMap<Object, T>();
		private T lastSelected;

		/**
		 * @param keyProvider
		 */
		public ExpandMultiSelectionModel(ProvidesKey<T> keyProvider) {
			super(keyProvider);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.gwt.view.client.SelectionModel#isSelected(java.lang.Object)
		 */
		@Override
		public boolean isSelected(T object) {
			return isKeySelected(getKey(object));
		}

		protected boolean isKeySelected(Object key) {
			return selected.get(key) != null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.gwt.view.client.SelectionModel#setSelected(java.lang.Object, boolean)
		 */
		@Override
		public void setSelected(T object, boolean selected) {
			Object key = getKey(object);
			if (isKeySelected(key)) {
				this.selected.remove(key);
				lastSelected = null;
			} else {
				this.selected.put(key, object);
				lastSelected = object;
			}
			scheduleSelectionChangeEvent();
		}

		public T getLastSelection() {
			return lastSelected;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.gwt.view.client.SetSelectionModel#clear()
		 */
		@Override
		public void clear() {
			lastSelected = null;
			selected.clear();

			scheduleSelectionChangeEvent();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.gwt.view.client.SetSelectionModel#getSelectedSet()
		 */
		@Override
		public Set<T> getSelectedSet() {
			return new HashSet<T>(selected.values());
		}

	}

	public static abstract class PlaceHolderColumn<T, C> extends Column<T, C> {

		private boolean isSelected;

		/**
		 * @param cell
		 */
		public PlaceHolderColumn(Cell<C> cell) {
			super(cell);
		}

		protected boolean getSelected() {
			return isSelected;
		}

	}

	private int expandColumnIndex;

	public ExpandableCellTableBuilder(AbstractCellTable<T> cellTable, Column<T, U> expandColumn) {
		this(cellTable, expandColumn, new ExpandMultiSelectionModel<T>(cellTable.getKeyProvider()), null);
	}

	public ExpandableCellTableBuilder(AbstractCellTable<T> cellTable, Column<T, U> exandColumn, SelectionModel<T> selectionModel) {
		this(cellTable, exandColumn, selectionModel, null);
	}

	public ExpandableCellTableBuilder(AbstractCellTable<T> cellTable, Column<T, U> exandColumn, PlaceHolderColumn<T, ?> placeHolder) {
		this(cellTable, exandColumn, new ExpandMultiSelectionModel<T>(cellTable.getKeyProvider()), placeHolder);
	}

	/**
	 * @param cellTable
	 * @param columnBody
	 */
	public ExpandableCellTableBuilder(AbstractCellTable<T> cellTable, Column<T, U> expandColumn, SelectionModel<T> selectionModel,
			PlaceHolderColumn<T, ?> placeHolder) {
		super(cellTable);

		this.expandColumn = expandColumn;

		this.cellTable.setSelectionModel(selectionModel);

		if (placeHolder == null) {
			this.placeholderColumn = new PlaceHolderColumn<T, String>(new TextCell()) {
				@Override
				public String getValue(T object) {
					return getSelected() ? "-" : "+";
				}
			};
		} else {
			this.placeholderColumn = placeHolder;
		}

		// Cache styles for faster access.
		Style style = cellTable.getResources().style();
		evenRowStyle = style.evenRow();
		oddRowStyle = style.oddRow();
		selectedRowStyle = " " + style.selectedRow();
		cellStyle = style.cell();
		evenCellStyle = " " + style.evenRowCell();
		oddCellStyle = " " + style.oddRowCell();
		firstColumnStyle = " " + style.firstColumn();
		lastColumnStyle = " " + style.lastColumn();
		selectedCellStyle = " " + style.selectedRowCell();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.cellview.client.AbstractCellTableBuilder#buildRowImpl(java.lang.Object, int)
	 */
	@Override
	protected void buildRowImpl(T rowValue, int absRowIndex) {
		// Calculate the row styles.
		SelectionModel<? super T> selectionModel = cellTable.getSelectionModel();
		final boolean isSelected = (selectionModel == null || rowValue == null) ? false : selectionModel.isSelected(rowValue);
		boolean isEven = absRowIndex % 2 == 0;
		StringBuilder trClasses = new StringBuilder(isEven ? evenRowStyle : oddRowStyle);
		if (isSelected) {
			trClasses.append(selectedRowStyle);
		}

		// Add custom row styles.
		RowStyles<T> rowStyles = cellTable.getRowStyles();
		if (rowStyles != null) {
			String extraRowStyles = rowStyles.getStyleNames(rowValue, absRowIndex);
			if (extraRowStyles != null) {
				trClasses.append(" ").append(extraRowStyles);
			}
		}

		// Build the row.
		TableRowBuilder tr = startRow();
		tr.className(trClasses.toString());

		// Build the columns.
		int columnCount = cellTable.getColumnCount();
		for (int curColumn = 0; curColumn < columnCount; curColumn++) {
			Column<T, ?> column = cellTable.getColumn(curColumn);

			if (column == expandColumn) {
				expandColumnIndex = curColumn;
				column = placeholderColumn;
				placeholderColumn.isSelected = isSelected;
			}

			// Create the cell styles.
			StringBuilder tdClasses = new StringBuilder(cellStyle);
			tdClasses.append(isEven ? evenCellStyle : oddCellStyle);
			if (curColumn == 0) {
				tdClasses.append(firstColumnStyle);
			}
			if (isSelected) {
				tdClasses.append(selectedCellStyle);
			}
			// The first and last column could be the same column.
			if (curColumn == columnCount - 1) {
				tdClasses.append(lastColumnStyle);
			}

			// Add class names specific to the cell.
			Context context = new Context(absRowIndex, curColumn, cellTable.getValueKey(rowValue));
			String cellStyles = column.getCellStyleNames(context, rowValue);
			if (cellStyles != null) {
				tdClasses.append(" " + cellStyles);
			}

			// Build the cell.
			HorizontalAlignmentConstant hAlign = column.getHorizontalAlignment();
			VerticalAlignmentConstant vAlign = column.getVerticalAlignment();
			TableCellBuilder td = tr.startTD();
			td.className(tdClasses.toString());
			if (hAlign != null) {
				td.align(hAlign.getTextAlignString());
			}
			if (vAlign != null) {
				td.vAlign(vAlign.getVerticalAlignString());
			}

			// Add the inner div.
			DivBuilder div = td.startDiv();
			div.style().outlineStyle(OutlineStyle.NONE).endStyle();

			// Render the cell into the div.
			renderCell(div, context, column, rowValue);

			// End the cell.
			div.endDiv();
			td.endTD();
		}

		// End the row.
		tr.endTR();

		if (isSelected) {
			buildExpandedRow(rowValue, absRowIndex, columnCount, trClasses, isEven, isSelected);
		}
	}

	/**
	 * @param trClasses
	 * 
	 */
	private void buildExpandedRow(T rowValue, int absRowIndex, int columnCount, StringBuilder trClasses, boolean isEven, boolean isSelected) {
		TableRowBuilder tr = startRow();
		tr.className(trClasses.toString());

		Column<T, ?> column = expandColumn;
		// Create the cell styles.
		StringBuilder tdClasses = new StringBuilder(cellStyle);
		tdClasses.append(isEven ? evenCellStyle : oddCellStyle);
		tdClasses.append(firstColumnStyle);
		if (isSelected) {
			tdClasses.append(selectedCellStyle);
		}
		tdClasses.append(lastColumnStyle);

		// Add class names specific to the cell.
		Context context = new Context(absRowIndex, expandColumnIndex, cellTable.getValueKey(rowValue));
		String cellStyles = column.getCellStyleNames(context, rowValue);
		if (cellStyles != null) {
			tdClasses.append(" " + cellStyles);
		}

		// Build the cell.
		HorizontalAlignmentConstant hAlign = column.getHorizontalAlignment();
		VerticalAlignmentConstant vAlign = column.getVerticalAlignment();
		TableCellBuilder td = tr.startTD();
		td.colSpan(columnCount);
		td.className(tdClasses.toString());
		if (hAlign != null) {
			td.align(hAlign.getTextAlignString());
		}
		if (vAlign != null) {
			td.vAlign(vAlign.getVerticalAlignString());
		}

		// Add the inner div.
		DivBuilder div = td.startDiv();
		div.style().outlineStyle(OutlineStyle.NONE).endStyle();

		// Render the cell into the div.
		renderCell(div, context, column, rowValue);

		// End the cell.
		div.endDiv();
		td.endTD();

		// End the row.
		tr.endTR();
	}

}
