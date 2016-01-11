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
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
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
	@UiField UListElement appResults;
	@UiField UListElement developerResults;

	public PanelRightSearch() {
		initWidget(uiBinder.createAndBindUi(this));
		INSTANCE = this;
		inputSearch.getElement().setAttribute("autocomplete", "off");
	}
	
	@UiHandler("inputSearch")
	void onFieldModified(KeyUpEvent event) {
		// do regex to check that the value of event was a letter or number
		String searchValue = inputSearch.getValue();
		search(searchValue);
	}

	public DivElement getPanelOverlay() {
		return panelOverlay;
	}
	
	private void showResults(JsonArray jarray) {
		appResults.removeAllChildren();
		for(int a = 0; a < jarray.size(); a++) {
			JsonObject dataItem = jarray.get(a).getAsJsonObject();
			String trackName = dataItem.get("trackName").getAsString();
			if(trackName != null) {
				LIElement listItem = Document.get().createLIElement();
				AnchorElement linkElement = Document.get().createAnchorElement();
				linkElement.setAttribute("href", "#!appdetails/556164350/leaderboard/");
				
				String imageName = dataItem.get("artworkUrl60").getAsString();
				if(imageName != null) {
					ImageElement imageElement = Document.get().createImageElement();
					imageElement.setAttribute("src", imageName);
					linkElement.appendChild(imageElement);
				}
				
				SpanElement spanElement = Document.get().createSpanElement();
				spanElement.setInnerText(trackName);
				linkElement.appendChild(spanElement);
				listItem.appendChild(linkElement);
				appResults.appendChild(listItem);
			}
		}
	}
	
	private native void search(String searchString) /*-{
		$wnd.$('#ref-iTunesSearchApps').remove();
		$wnd.$('body').append(
			$wnd.$("<script>").attr("id", "ref-iTunesSearchApps").attr(
					"src",
					"https://itunes.apple.com/search?term=" + searchString + "&media=software&limit=10&callback=handleAppSearchFromHeader"));
	}-*/;
	
	public static void processAppSearchResponse(String response) {
		JsonElement jelement = new JsonParser().parse(response);
		if(jelement != null) {
			JsonObject jobject = jelement.getAsJsonObject();
			if(jobject != null) {
				JsonArray jarray = jobject.getAsJsonArray("results");
				if(jarray != null && jarray.size() > 0) {
					INSTANCE.showResults(jarray);
				}				
			}			
		}		
	}

	public static native void exportAppSearchResponseHandler() /*-{
		$wnd.processAppSearchResponse = $entry(@io.reflection.app.client.part.navigation.PanelRightSearch::processAppSearchResponse(Ljava/lang/String;));
	}-*/;
}