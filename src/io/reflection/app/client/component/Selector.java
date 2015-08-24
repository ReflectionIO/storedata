//
//  FormFieldSelect.java
//  storedata
//
//  Created by Stefano Capuzzi on 4 Mar 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.component;

import io.reflection.app.client.helper.DOMHelper;
import io.reflection.app.client.res.Styles;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Stefano Capuzzi
 *
 */
public class Selector extends Composite implements HasChangeHandlers {

	private static FormFieldSelectUiBinder uiBinder = GWT.create(FormFieldSelectUiBinder.class);

	interface FormFieldSelectUiBinder extends UiBinder<Widget, Selector> {}

	@UiField HTMLPanel formField;
	@UiField DivElement overlayPanel;
	@UiField SelectElement selectElem;
	@UiField HTMLPanel selectContainer;
	@UiField UListElement uListElem;
	@UiField Anchor closeLink;
	@UiField SpanElement spanSelectLabel;
	@UiField SpanElement spanOptionLabel;
	@UiField DivElement listContainer;
	private List<LIElement> itemList = new ArrayList<LIElement>(); // List of items excluded the placeholder
	private int selectedIndex = -1; // Simulates a SELECT OPTION where there is no placegolder
	private boolean isFilter;

	public Selector(final boolean isFilter) {
		initWidget(uiBinder.createAndBindUi(this));

		this.isFilter = isFilter;

		setOverlay(false);

		Event.sinkEvents(spanSelectLabel, Event.ONCLICK);
		Event.setEventListener(spanSelectLabel, new EventListener() {

			@Override
			public void onBrowserEvent(Event event) {
				if (Event.ONCLICK == event.getTypeInt()) {
					open();
				}
			}
		});

		// Close drop-down clicking out of it, i.e. on the overlay
		Event.sinkEvents(overlayPanel, Event.ONCLICK);
		Event.setEventListener(overlayPanel, new EventListener() {

			@Override
			public void onBrowserEvent(Event event) {
				if (Event.ONCLICK == event.getTypeInt()) {
					close();
				}
			}
		});

		// Update margin when resizing window
		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				uListElem.getStyle().setMarginTop(-(uListElem.getClientHeight()), Unit.PX);
			}
		});

		if (!isFilter) {
			closeLink.removeFromParent();
			spanOptionLabel.removeFromParent();
			selectContainer.getElement().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().reflectionSelectFilter());
		}

	}

	public Selector() {
		this(true);
	}

	private void open() {
		if (!selectElem.getPropertyBoolean("disabled")) {
			formField.getElement().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isOpen());
			selectContainer.getElement().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isOpen());
			uListElem.getStyle().setMarginTop(9, Unit.PX);
			if (Window.getClientWidth() < 720 && isFilter) {
				DOMHelper.setScrollEnabled(false);
			}
		}
	}

	private void close() {
		formField.getElement().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isOpen());
		selectContainer.getElement().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isOpen());
		uListElem.getStyle().setMarginTop(-(uListElem.getClientHeight()), Unit.PX);
		if (Window.getClientWidth() < 720 && isFilter) {
			DOMHelper.setScrollEnabled(true);
		}
	}

	// Close drop-down clicking on 'X'
	@UiHandler("closeLink")
	void onClick(ClickEvent event) {
		event.preventDefault();
		close();
	}

	public void setLabelText(String text) {
		selectElem.getFirstChildElement().setInnerText(text);
		spanSelectLabel.setInnerText(text);
		if ("".equals(selectElem.getAttribute("data-title"))) {
			spanOptionLabel.setInnerText(text);
		}
	}

	public void setDropdownTitle(String text) {
		if (isFilter) {
			selectElem.setAttribute("data-title", text);
			spanOptionLabel.setInnerText(text);
		}
	}

	public void setOverlay(boolean overlaid) {
		if (overlaid) {
			formField.getElement().insertFirst(overlayPanel);
		} else {
			overlayPanel.removeFromParent();
		}
	}

	public void addItem(String item, String value) {
		OptionElement option = Document.get().createOptionElement();
		option.setInnerText(item);
		option.setAttribute("value", value);
		selectElem.appendChild(option);
		final LIElement listElem = createListItemOption(item, value);
		listContainer.appendChild(listElem);
		itemList.add(listElem);

		Event.sinkEvents(listElem, Event.ONCLICK);
		Event.setEventListener(listElem, new EventListener() {

			@Override
			public void onBrowserEvent(Event event) {
				if (Event.ONCLICK == event.getTypeInt()) {
					selectOption(listElem, true);
				}

			}
		});
	}

	private void selectOption(final LIElement listElem, boolean fireEvents) {
		selectedIndex = itemList.indexOf(listElem);
		spanSelectLabel.setInnerText(listElem.getInnerText());
		spanSelectLabel.addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isActivated());

		for (LIElement le : itemList) {
			le.removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isSelected());
		}

		listElem.addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isSelected());

		for (int i = 0; i < selectElem.getOptions().getLength(); i++) {
			if (i == selectedIndex + 1) {
				selectElem.getOptions().getItem(i).setAttribute("selected", "");
			} else {
				selectElem.getOptions().getItem(i).removeAttribute("selected");
			}
		}

		if (fireEvents) {
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this); // Fire Change Event
		}

		close();

	}

	public void selectDefault() {
		spanSelectLabel.setInnerText(selectElem.getFirstChildElement().getInnerText());
		spanSelectLabel.removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isActivated());
		for (LIElement le : itemList) {
			le.removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isSelected());
		}
		for (int i = 0; i < selectElem.getOptions().getLength(); i++) {
			if (i == 0) {
				selectElem.getOptions().getItem(i).setAttribute("selected", "");
			} else {
				selectElem.getOptions().getItem(i).removeAttribute("selected");
			}
		}
		selectedIndex = -1;
	}

	public void setSelectedIndex(int index, boolean fireEvents) {
		if (index < 0 || index >= itemList.size()) {
			selectDefault();
		} else {
			selectOption(itemList.get(index), fireEvents);
		}
	}

	public void setSelectedIndex(int index) {
		setSelectedIndex(index, true);
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}

	public String getSelectedValue() {
		return selectedIndex != -1 ? itemList.get(selectedIndex).getAttribute("value") : "";
	}

	public int getItemCount() {
		return itemList.size();
	}

	public String getItemText(int index) {
		return (getItemCount() > index && index >= 0) ? itemList.get(index).getInnerText() : "";
	}

	public String getValue(int index) {
		return (getItemCount() > index && index >= 0) ? itemList.get(index).getAttribute("data-value") : "";
	}

	public int getValueIndex(String value) {
		int index = -1;
		for (LIElement le : itemList) {
			if (value.equals(le.getAttribute("data-value"))) {
				index = itemList.indexOf(le);
				break;
			}
		}
		return index;
	}

	public void clear() {
		spanSelectLabel.removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isActivated());
		for (int i = 1; i < selectElem.getOptions().getLength(); i++) {
			selectElem.remove(i);
		}
		for (LIElement le : itemList) {
			le.removeFromParent();
		}
		itemList.clear();
		selectedIndex = -1;
	}

	public void setEnabled(boolean enabled) {
		if (enabled) {
			formField.getElement().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().formFieldSelectDisabled());
		} else {
			formField.getElement().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().formFieldSelectDisabled());
		}
		selectElem.setPropertyBoolean("disabled", !enabled);
	}

	private LIElement createListItemOption(String item, String value) {
		LIElement listItem = Document.get().createLIElement();
		listItem.setAttribute("data-value", value);
		listItem.setAttribute("data-selectedtext", "");
		listItem.setInnerText(item);
		return listItem;
	}

	public void setAlignLeft(String s) {
		selectElem.removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().reflectionSelectCenter());
		selectContainer.getElement().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().reflectionSelectCenter());
		selectElem.removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().reflectionSelectRight());
		selectContainer.getElement().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().reflectionSelectRight());
	}

	public void setAlignCenter(String s) {
		selectElem.removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().reflectionSelectRight());
		selectContainer.getElement().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().reflectionSelectRight());
		selectElem.addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().reflectionSelectCenter());
		selectContainer.getElement().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().reflectionSelectCenter());
	}

	public void setAlignRight(String s) {
		selectElem.removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().reflectionSelectCenter());
		selectContainer.getElement().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().reflectionSelectCenter());
		selectElem.addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().reflectionSelectRight());
		selectContainer.getElement().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().reflectionSelectRight());
	}

	public void setTooltip(String text) {
		getElement().addClassName("js-tooltip");
		getElement().setAttribute("data-tooltip", text);
	}

	@Override
	public void setStyleName(String style) {
		setStyleName(style, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();
		uListElem.getStyle().setMarginTop(-(uListElem.getClientHeight()), Unit.PX);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.event.dom.client.HasChangeHandlers#addChangeHandler(com.google.gwt.event.dom.client.ChangeHandler)
	 */
	@Override
	public HandlerRegistration addChangeHandler(ChangeHandler handler) {
		return addDomHandler(handler, ChangeEvent.getType());
	}

}
