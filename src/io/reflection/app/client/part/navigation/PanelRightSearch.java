//
//  PanelRightSearch.java
//  storedata
//
//  Created by Stefano Capuzzi on 25 Feb 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part.navigation;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.HasKeyUpHandlers;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Stefano Capuzzi
 *
 */
public class PanelRightSearch extends Composite {

	private static PanelRightSearchUiBinder uiBinder = GWT.create(PanelRightSearchUiBinder.class);

	interface PanelRightSearchUiBinder extends UiBinder<Widget, PanelRightSearch> {}
	
	private static PanelRightSearch INSTANCE = null;

	@UiField DivElement panelOverlay;
	@UiField TextBox inputSearch;

	public PanelRightSearch() {
		initWidget(uiBinder.createAndBindUi(this));
		INSTANCE = this;
		inputSearch.getElement().setAttribute("autocomplete", "off");
	}
	
	@UiHandler("inputSearch")
	void onFieldModified(KeyUpEvent event) {
		String searchValue = inputSearch.getValue();
		
	}

	public DivElement getPanelOverlay() {
		return panelOverlay;
	}
	
	private void showResults() {
		Window.alert("result!");
	}
	
	private native void search(String searchString) /*-{
		$wnd.$('body').append(
			$wnd.$("<script>").attr("id", "ref-iTunesSearchApps").attr(
					"src",
					"https://itunes.apple.com/search?term=" + searchString + "&media=software&limit=10&callback=handleAppSearch"));
	}-*/;
	
	public static void processAppSearchResponse(String response) {
//		JsonElement jelement = new JsonParser().parse(response);
//		JsonObject jobject = jelement.getAsJsonObject();
//		JsonArray jarray = jobject.getAsJsonArray("results");
//		jobject = jarray.get(0).getAsJsonObject();

		INSTANCE.showResults();
	}

	public static native void exportAppSearchResponseHandler() /*-{
		$wnd.processAppSearchResponse = $entry(@io.reflection.app.client.part.navigation.PanelRightSearch::processAppSearchResponse(Ljava/lang/String;));
	}-*/;
}