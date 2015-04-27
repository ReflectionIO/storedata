//
//  FormFieldSelect.java
//  storedata
//
//  Created by Stefano Capuzzi on 4 Mar 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.component;

import io.reflection.app.client.res.Styles;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
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
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
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
public class FormFieldSelect extends Composite implements HasChangeHandlers {

	private static FormFieldSelectUiBinder uiBinder = GWT.create(FormFieldSelectUiBinder.class);

	interface FormFieldSelectUiBinder extends UiBinder<Widget, FormFieldSelect> {}

	@UiField HTMLPanel formField;
	@UiField SelectElement selectElem;
	@UiField HTMLPanel selectContainer;
	@UiField UListElement uListElem;
	@UiField Anchor selectLink;
	@UiField SpanElement spanSelectLabel;
	@UiField SpanElement spanOptionLabel;
	private List<LIElement> itemList = new ArrayList<LIElement>(); // List of items excluded the placeholder
	private int selectedIndex = -1; // Simulates a SELECT OPTION where there is no placegolder

	public FormFieldSelect() {
		initWidget(uiBinder.createAndBindUi(this));

		selectContainer.sinkEvents(Event.ONCLICK);
		selectContainer.addHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (!selectElem.getPropertyBoolean("disabled")) {
					if (formField.getElement().hasClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isOpen())) {
						formField.getElement().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isOpen());
						selectContainer.getElement().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isOpen());
						uListElem.getStyle().setMarginTop(-(uListElem.getClientHeight()), Unit.PX);
					} else {
						formField.getElement().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isOpen());
						selectContainer.getElement().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isOpen());
						uListElem.getStyle().setMarginTop(9, Unit.PX);
					}
				}
			}
		}, ClickEvent.getType());

		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				uListElem.getStyle().setMarginTop(-(uListElem.getClientHeight()), Unit.PX);
			}
		});

	}

	public void setLabelText(String text) {
		selectElem.getFirstChildElement().setInnerText(text);
		spanSelectLabel.setInnerText(text);
		spanOptionLabel.setInnerText(text);
	}

	public void addItem(String item, String value) {
		OptionElement option = Document.get().createOptionElement();
		option.setInnerText(item);
		option.setAttribute("value", value);
		selectElem.appendChild(option);
		final LIElement listElem = createListItemOption(item, value);
		uListElem.appendChild(listElem);
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
		if (!spanSelectLabel.hasClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isActivated())) {
			spanSelectLabel.addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isActivated());
		}
		for (LIElement le : itemList) {
			le.removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isSelected());
		}
		if (!listElem.hasClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isSelected())) {
			listElem.addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isSelected());
		}
		for (int i = 0; i < selectElem.getOptions().getLength(); i++) {
			if (i == selectedIndex + 1) {
				selectElem.getOptions().getItem(i).setAttribute("selected", "");
			} else {
				selectElem.getOptions().getItem(i).removeAttribute("selected");
			}
		}

		if (formField.getElement().hasClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isOpen())) {
			formField.getElement().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isOpen());
			selectContainer.getElement().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isOpen());
			uListElem.getStyle().setMarginTop(-(uListElem.getClientHeight()), Unit.PX);
		}

		if (fireEvents) {
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this); // Fire Change Event
		}

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
			if (!formField.getElement().hasClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().formFieldSelectDisabled())) {
				formField.getElement().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().formFieldSelectDisabled());
			}
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
