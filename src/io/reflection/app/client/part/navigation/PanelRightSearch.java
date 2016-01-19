//
//  PanelRightSearch.java
//  storedata
//
//  Created by Stefano Capuzzi on 25 Feb 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part.navigation;

import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.page.MyAppsPage;
import io.reflection.app.client.page.PageType;

/**
 * @author Stefano Capuzzi
 *
 */
public class PanelRightSearch extends Composite {

	private static PanelRightSearchUiBinder uiBinder = GWT.create(PanelRightSearchUiBinder.class);

	interface PanelRightSearchUiBinder extends UiBinder<Widget, PanelRightSearch> {}

	private static PanelRightSearch INSTANCE = null;
	private String searchValue;
	private final ArrayList<Integer> invalidKeyCodesForSearch;
	private Timer timer;

	@UiField DivElement panelOverlay;
	@UiField DivElement appResultsContainer;
	@UiField DivElement developerResultsContainer;
	@UiField TextBox inputSearch;
	@UiField UListElement appResults;
	@UiField UListElement developerResults;
	@UiField DivElement noResultsContainer;
	@UiField Element loadingSpinner;

	public PanelRightSearch() {
		initWidget(uiBinder.createAndBindUi(this));
		INSTANCE = this;
		inputSearch.getElement().setAttribute("autocomplete", "off");
		loadingSpinner.getStyle().setProperty("display", "none");

		final int[] invalidCodes = { 0, 17, 18, 27, 37, 38, 39, 40 };
		// invalidCodes = §±, ctrl, alt, escape, left, up, right, down
		invalidKeyCodesForSearch = new ArrayList<Integer>();
		for (int i = 0; i < invalidCodes.length; i++) {
			invalidKeyCodesForSearch.add(invalidCodes[i]);
		}
	}

	@UiHandler("inputSearch")
	void onFieldModified(final KeyUpEvent event) {

		final NativeEvent nativeEvent = event.getNativeEvent();
		// check the keyup event keycode is valid for app/developer search
		final int keyCharacter = nativeEvent.getKeyCode();
		if (!invalidKeyCodesForSearch.contains(keyCharacter)) {

			searchValue = inputSearch.getValue();

			// create timer after keyup to allow continuous typing without constant search
			// this will reduce searches to when a user has finished typing
			// if timer exists from previous keyup cancel that timer
			if (timer != null) {
				timer.cancel();
			}

			timer = new Timer() {
				@Override
				public void run() {
					if (searchValue.length() > 0) {
						loadingSpinner.getStyle().setProperty("display", "block");
						search(searchValue);
					} else {
						showNoResults();
					}
				}
			};

			timer.schedule(500);
		}
	}

	public DivElement getPanelOverlay() {
		return panelOverlay;
	}

	private void showResults(JsonArray jarray) {

		appResults.removeAllChildren();
		developerResults.removeAllChildren();

		int outputCounter = 0;
		int outputDevCounter = 0;
		final ArrayList<String> validDeveloperResults = new ArrayList<String>();

		for (int a = 0; a < jarray.size(); a++) {

			final JsonObject dataItem = jarray.get(a).getAsJsonObject();
			final String trackName = dataItem.get("trackName").getAsString();
			final String trackId = dataItem.get("trackId").getAsString();

			if (trackName != null && outputCounter <= 10) {
				// check that the results from iTunes API are relevant for the App Name
				final char[] charArray = searchValue.toCharArray();
				boolean isValidAppNameResult = true;
				for (int c = 0; c < charArray.length; c++) {
					if (isValidAppNameResult) {
						// don't include space characters

						if (trackName.indexOf(Character.toUpperCase(charArray[c])) == -1 && trackName.indexOf(Character.toLowerCase(charArray[c])) == -1) {
							isValidAppNameResult = false;
						}
					}
				}

				if (isValidAppNameResult) {
					final LIElement listItem = Document.get().createLIElement();
					final AnchorElement linkElement = Document.get().createAnchorElement();
					final SafeUri linkToApp = PageType.ItemPageType.asHref(NavigationController.VIEW_ACTION_PARAMETER_VALUE, trackId,
							FilterController.DOWNLOADS_CHART_TYPE, MyAppsPage.COMING_FROM_PARAMETER, FilterController.get().getFilter().asItemFilterString());
					linkElement.setHref(linkToApp);

					linkElement.setAttribute("onClick", "closeRightPanelSearch()");

					final String imageName = dataItem.get("artworkUrl60").getAsString();
					if (imageName != null) {
						final ImageElement imageElement = Document.get().createImageElement();
						imageElement.setAttribute("src", imageName);
						linkElement.appendChild(imageElement);
					}

					final SpanElement spanElement = Document.get().createSpanElement();
					spanElement.setInnerText(trackName);
					linkElement.appendChild(spanElement);
					listItem.appendChild(linkElement);
					appResults.appendChild(listItem);

					outputCounter++;
				}
			}

			final String artistName = dataItem.get("artistName").getAsString();
			final String artistId = dataItem.get("artistId").getAsString();
			if (artistName != null && outputDevCounter <= 10) {
				// check that the results from iTunes API are relevant for the App Name
				final char[] charArray = searchValue.toCharArray();
				boolean isValidDevNameResult = true;
				for (int c = 0; c < charArray.length; c++) {
					if (isValidDevNameResult) {
						// check that the artistName contains the searched characters, not order specific
						if (artistName.indexOf(Character.toUpperCase(charArray[c])) == -1 && artistName.indexOf(Character.toLowerCase(charArray[c])) == -1) {
							isValidDevNameResult = false;
							c = charArray.length;
						}
					}
				}

				if (isValidDevNameResult) {

					// add to found array if it doesn't already exist in found array
					if (!validDeveloperResults.contains(artistId)) {
						validDeveloperResults.add(artistId);

						// output result in the developer list
						final LIElement listItem = Document.get().createLIElement();
						final AnchorElement linkElement = Document.get().createAnchorElement();
						linkElement.setAttribute("href", "#!developer/" + artistId);
						linkElement.setAttribute("onClick", "closeRightPanelSearch()");

						final SpanElement spanElement = Document.get().createSpanElement();
						spanElement.setInnerText(artistName);
						linkElement.appendChild(spanElement);
						listItem.appendChild(linkElement);
						developerResults.appendChild(listItem);

						outputDevCounter++;
					}
				}
			}
		}

		loadingSpinner.getStyle().setProperty("display", "none");

		if (outputCounter == 0) {
			appResultsContainer.getStyle().setProperty("display", "none");
		} else {
			appResultsContainer.getStyle().setProperty("display", "block");
		}

		if (outputDevCounter == 0) {
			developerResultsContainer.getStyle().setProperty("display", "none");
		} else {
			developerResultsContainer.getStyle().setProperty("display", "block");
		}

		if (outputCounter == 0 && outputDevCounter == 0) {
			noResultsContainer.getStyle().setProperty("display", "block");
		} else {
			noResultsContainer.getStyle().setProperty("display", "none");
		}
	}

	private void showNoResults() {
		appResultsContainer.getStyle().setProperty("display", "none");
		developerResultsContainer.getStyle().setProperty("display", "none");
		noResultsContainer.getStyle().setProperty("display", "block");
		loadingSpinner.getStyle().setProperty("display", "none");
	}

	private native void search(String searchString) /*-{
		$wnd.$('#ref-iTunesSearchApps').remove();
		$wnd
				.$('body')
				.append(
						$wnd
								.$("<script>")
								.attr("id", "ref-iTunesSearchApps")
								.attr(
										"src",
										"https://itunes.apple.com/gb/search?term="
												+ searchString
												+ "&media=software&limit=20&callback=handleAppSearchFromHeader"));

		// we can be more specific with results
		// search by app name = https://itunes.apple.com/search?term=candy&entity=software&attribute=allTrackTerm&limit=200
		// search by developer = https://itunes.apple.com/search?term=dung%20nguyen&entity=allArtist&attribute=softwareDeveloper&limit=200
	}-*/;

	public static void processAppSearchResponse(String response) {
		boolean isValidResult = false;
		final JsonElement jelement = new JsonParser().parse(response);
		if (jelement != null) {
			final JsonObject jobject = jelement.getAsJsonObject();
			if (jobject != null) {
				final JsonArray jarray = jobject.getAsJsonArray("results");
				if (jarray != null && jarray.size() > 0) {
					INSTANCE.showResults(jarray);
					isValidResult = true;
				}
			}
		}

		if (!isValidResult) {
			INSTANCE.showNoResults();
		}
	}

	public static void closeRightPanelSearch() {
		NavigationController.get().getHeader().closePanelRightSearch();
	}

	public static native void exportAppSearchResponseHandler() /*-{
		$wnd.processAppSearchResponse = $entry(@io.reflection.app.client.part.navigation.PanelRightSearch::processAppSearchResponse(Ljava/lang/String;));
	}-*/;

	public static native void exportCloseRightPanelSearch() /*-{
		$wnd.closeRightPanelSearch = $entry(@io.reflection.app.client.part.navigation.PanelRightSearch::closeRightPanelSearch());
	}-*/;
}