//
//  LoadingIndicator.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 5 Aug 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableColElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Stefano Capuzzi (capuzzistefano)
 *
 */
public class LoadingIndicator extends Composite {

	private static LoadingIndicatorUiBinder uiBinder = GWT.create(LoadingIndicatorUiBinder.class);

	interface LoadingIndicatorUiBinder extends UiBinder<Widget, LoadingIndicator> {}

	@UiField Element colgroup;
	@UiField Element tbody;

	private int rowCount;
	private int colCount;

	private List<TableColElement> cols = new ArrayList<TableColElement>();
	private Map<TableRowElement, List<TableCellElement>> cells = new HashMap<TableRowElement, List<TableCellElement>>();

	public LoadingIndicator(int rowCount, int colCount) {
		initWidget(uiBinder.createAndBindUi(this));

		this.rowCount = rowCount;
		this.colCount = colCount;

		for (int j = 0; j < colCount; j++) {
			TableColElement col = Document.get().createColElement();
			colgroup.appendChild(col);
			cols.add(col);
		}

		for (int i = 0; i < rowCount; i++) {
			TableRowElement tr = Document.get().createTRElement();
			List<TableCellElement> columns = new ArrayList<TableCellElement>();
			for (int j = 0; j < colCount; j++) {
				TableCellElement td = Document.get().createTDElement();
				tr.appendChild(td);
				columns.add(td);
			}
			cells.put(tr, columns);
			tbody.appendChild(tr);
		}
	}

	public int getRowCount() {
		return rowCount;
	}

	public int getColCount() {
		return colCount;
	}

	public void setColStyleName(int index, String styleName) {
		cols.get(index).setClassName(styleName);
	}

	public void setColStyle(int index, String style) {
		cols.get(index).setAttribute("style", style);
	}

	public void setColumnCellsStyleName(int index, String styleName) {
		for (TableRowElement tr : cells.keySet()) {
			for (int i = 0; i < cells.get(tr).size(); i++) {
				if (i == index) {
					cells.get(tr).get(i).setClassName(styleName);;
				}
			}
		}
	}

	public void setColumnCellsSafeHtml(int index, SafeHtml safeHtml) {
		for (TableRowElement tr : cells.keySet()) {
			for (int i = 0; i < cells.get(tr).size(); i++) {
				if (i == index) {
					cells.get(tr).get(i).setInnerSafeHtml(safeHtml);
				}
			}
		}
	}

}
