package io.reflection.app.client.page;

import static io.reflection.app.client.controller.FilterController.DOWNLOADS_CHART_TYPE;
import static io.reflection.app.client.controller.FilterController.OVERALL_LIST_TYPE;
import static io.reflection.app.client.controller.FilterController.RANKING_CHART_TYPE;
import static io.reflection.app.client.controller.FilterController.REVENUE_CHART_TYPE;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;

import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.component.LoadingBar;
import io.reflection.app.client.component.Selector;
import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.helper.AnimationHelper;
import io.reflection.app.client.helper.FilterHelper;
import io.reflection.app.client.helper.FormHelper;
import io.reflection.app.client.helper.ResponsiveDesignHelper;
import io.reflection.app.client.res.Styles;
import io.reflection.app.client.res.Styles.ReflectionMainStyles;
import io.reflection.app.datatypes.shared.Item;

public class AppDetails extends Page implements NavigationEventHandler {

	private static AppDetailsUiBinder uiBinder = GWT.create(AppDetailsUiBinder.class);

	interface AppDetailsUiBinder extends UiBinder<Widget, AppDetails> {}

	private static AppDetails INSTANCE = null;
	
	ReflectionMainStyles style = Styles.STYLES_INSTANCE.reflectionMainStyle();

	@UiField HeadingElement trackName;
	@UiField Image image;
	@UiField SpanElement artistName;
	@UiField SpanElement storeName;
	@UiField AnchorElement viewInStore;	
	@UiField SpanElement formattedPrice;
	@UiField Element formattedPriceDuplicate;
	@UiField Element categories;
	@UiField Element fileSize;
	@UiField Element languageCodes;
	@UiField Element rated;
	@UiField Element version;
	@UiField Element releaseDate;
	@UiField Element universal;
	@UiField Element gameCenter;
	@UiField Element supportedDevices;
	@UiField ParagraphElement description;
	@UiField AnchorElement developersSite;
	@UiField ParagraphElement developersSiteContainer;	
	@UiField AnchorElement appStoreLink;
	@UiField ParagraphElement appStoreLinkContainer;
	@UiField AnchorElement artistStoreLink;
	@UiField ParagraphElement artistStoreLinkContainer;
	@UiField Element appVersionContainer;
	@UiField SpanElement appVersion;
	@UiField DivElement whatsNewContainer;
	@UiField SpanElement releaseNotesText;
	@UiField UListElement screenshotsList;
	@UiField UListElement moreAppsList;
	@UiField Element moreAppsListContainer;
	@UiField DivElement reviewsContainer;
	@UiField UListElement reviewsList;
	@UiField DivElement ratingCurrent;
	@UiField DivElement ratingAll;
	@UiField ParagraphElement moreAppsLink;	

	@UiField InlineHyperlink revenueLink;
	@UiField Element premiumIconRevenue;
	@UiField InlineHyperlink downloadsLink;
	@UiField Element premiumIconDownload;
	@UiField InlineHyperlink rankingLink;
	@UiField SpanElement rankingText;
	@UiField InlineHyperlink appDetailsLink;
	@UiField SpanElement appDetailsText;
	@UiField LIElement revenueItem;
	@UiField LIElement downloadsItem;
	@UiField LIElement rankingItem;
	@UiField LIElement appDetailsItem;

	@UiField Selector countrySelector;
	
	private String iapDescription;
	private String displayingAppId;
	private String comingPage;
	private String previousFilter;
	private Item displayingApp;
	
	private String artistId;
	private JsonArray languagesArray;
	private LoadingBar loadingBar = new LoadingBar(false);
	
	public AppDetails() {
		initWidget(uiBinder.createAndBindUi(this));
		INSTANCE = this;
		FilterHelper.addCountries(countrySelector, SessionController.get().isAdmin());
	}
	
	@Override
	protected void onAttach() {
		super.onAttach();

		register(DefaultEventBus.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));
	}
	
	@Override
	protected void onDetach() {
		super.onDetach();
		screenshotsList.removeAllChildren();
		moreAppsList.removeAllChildren();
		removeLookupScripts();
	}

	@Override
	public void navigationChanged(Stack previous, Stack current) {
		if (isValidStack(current)) {
			displayingAppId = current.getAction();
			removePageContent();
			AnimationHelper.nativeScrollTop(0, 300, "swing");			
			updateSelectorsFromFilter();			
			comingPage = current.getParameter(0);
			previousFilter = current.getParameter(1);
			getAppDetails(displayingAppId);
			
			revenueLink.setTargetHistoryToken(PageType.ItemPageType.asTargetHistoryToken(NavigationController.VIEW_ACTION_PARAMETER_VALUE, displayingAppId,
					REVENUE_CHART_TYPE, comingPage, previousFilter));
			downloadsLink.setTargetHistoryToken(PageType.ItemPageType.asTargetHistoryToken(NavigationController.VIEW_ACTION_PARAMETER_VALUE, displayingAppId,
					DOWNLOADS_CHART_TYPE, comingPage, previousFilter));
			rankingLink.setTargetHistoryToken(PageType.ItemPageType.asTargetHistoryToken(NavigationController.VIEW_ACTION_PARAMETER_VALUE, displayingAppId,
					RANKING_CHART_TYPE, comingPage, previousFilter));
			appDetailsLink.setTargetHistoryToken(PageType.AppDetailsPage.asTargetHistoryToken(displayingAppId, comingPage, previousFilter));
			
			if (SessionController.get().isAdmin() || MyAppsPage.COMING_FROM_PARAMETER.equals(comingPage)) {
				setRevenueDownloadTabsEnabled(true);
			} else {
				setRevenueDownloadTabsEnabled(false);
			}
			
			if (!SessionController.get().isAdmin() && MyAppsPage.COMING_FROM_PARAMETER.equals(comingPage)) {
				setRankTabEnabled(false);
			} else {
				setRankTabEnabled(true);
			}
					
			ResponsiveDesignHelper.makeTabsResponsive();
		} else {			
			PageType.RanksPageType.show(NavigationController.VIEW_ACTION_PARAMETER_VALUE, OVERALL_LIST_TYPE, FilterController.get().asRankFilterString());
		}
	}
	
	@UiHandler({ "countrySelector" })
	void onFiltersChanged(ChangeEvent event) {
		 String countryCode = countrySelector.getSelectedValue().toString();
		INSTANCE.searchReviews(displayingAppId, countryCode);
		INSTANCE.searchAppForRatingByCountry(displayingAppId, countryCode);
	}
	
	private void removePageContent() {
		image.setUrl("");
		trackName.setInnerText("");
		artistName.setInnerText("");
		formattedPrice.setInnerText("");
		viewInStore.getStyle().setProperty("display", "none");
		screenshotsList.removeAllChildren();
		description.setInnerHTML("");
		whatsNewContainer.getStyle().setProperty("display", "none");
		
		ratingCurrent.removeAllChildren();
		ratingAll.removeAllChildren();
		reviewsList.removeAllChildren();
		reviewsContainer.getStyle().setProperty("display", "none");
		
		moreAppsListContainer.getStyle().setProperty("display", "none");
		moreAppsList.removeAllChildren();
		
		formattedPrice.setInnerText("");
		formattedPriceDuplicate.setInnerText("");
		categories.setInnerText("");
		fileSize.setInnerText("");
		rated.setInnerText("");
		version.setInnerText("");
		releaseDate.setInnerText("");
		universal.setInnerText("");
		gameCenter.setInnerText("");
		
		developersSiteContainer.getStyle().setProperty("display", "none");
		appStoreLinkContainer.getStyle().setProperty("display", "none");
		artistStoreLinkContainer.getStyle().setProperty("display", "none");
		supportedDevices.setInnerText("");
	}
	
	private String htmlForTextWithEmbeddedNewlines(String text) {
		List<String> htmls = new ArrayList<String>();
	    String[] lines = text.split("\n");
		// Window.alert(lines[0]);
	    for (int i = 0 ; i < lines.length; i++) {
	    	DivElement el = Document.get().createDivElement();	
	    	el.setInnerText(lines[i]);
	        htmls.add(el.getInnerText());
	    }
	    String[] htmlAsArray = new String[ htmls.size() ];
	    htmls.toArray( htmlAsArray );
	    StringBuilder htmlString = new StringBuilder();
	    for(int x = 0; x < htmls.size(); x++) {	    	
	    	htmlString.append("<span>" + htmlAsArray[x] + "</span>");
	    }
	    return htmlString.toString();
	}
	
	private DivElement createReviewArticleDomElement(JsonObject review) {
		DivElement reviewArticle = Document.get().createDivElement(); 
		reviewArticle.addClassName(style.customerReview());
		DivElement ratingsContainer = Document.get().createDivElement(); 
		ratingsContainer.addClassName(style.ratingsContainer());
		DivElement ratingsFull = Document.get().createDivElement(); 
		ratingsFull.addClassName(style.ratingsFull());
		
		JsonObject reviewRating = review.getAsJsonObject("[im:rating]"); // this doesn't work - needs javascript dot/bracket notation equivalent
		if(reviewRating != null) {
			Window.alert(reviewRating.getAsString());
			//	Float ratingAsPercentage = (reviewRating.get("label").getAsFloat() / 5) * 100;
			//	Intended output - <div class="{STYLES_INSTANCE.reflectionMainStyle.ratingsFull}" style="width:100%"></div>
		}		
		
		JsonObject reviewTitle = review.getAsJsonObject("title");
		HeadingElement reviewTitleDomElement = Document.get().createHElement(3);
		reviewTitleDomElement.addClassName(style.customerReview__title());
		reviewTitleDomElement.setInnerText(reviewTitle.get("label").getAsString());
		
		JsonObject reviewContent = review.getAsJsonObject("content");
		ParagraphElement reviewParagraphDomElement = Document.get().createPElement();
		reviewParagraphDomElement.addClassName(style.customerReview__content());
		if(reviewContent != null) {			
			reviewParagraphDomElement.setInnerText(reviewContent.get("label").getAsString());
		}
		
		ratingsContainer.appendChild(ratingsFull);
		reviewArticle.appendChild(ratingsContainer);
		reviewArticle.appendChild(reviewTitleDomElement);
		reviewArticle.appendChild(reviewParagraphDomElement);
		
		return reviewArticle;
	}
	
	private void populateReviews(JsonArray jarray) {
		reviewsList.removeAllChildren();
		for(int r = 1; r < jarray.size() && r < 4; r++) { // first item of array is not a review so start at index 1
			JsonObject review = jarray.get(r).getAsJsonObject();
			final LIElement listItem = Document.get().createLIElement();			
			
			DivElement reviewArticle = createReviewArticleDomElement(review);
			
			listItem.appendChild(reviewArticle);
			this.reviewsList.appendChild(listItem);			
		}
		
		if(jarray.size() > 3) {
			final DivElement moreReviewsContainerDomElement = Document.get().createDivElement();
			moreReviewsContainerDomElement.addClassName(style.moreReviewsContainer());
			moreReviewsContainerDomElement.getStyle().setProperty("display", "none");
			
			for(int r = 4; r < jarray.size() && r < 11; r++) {	
				JsonObject review = jarray.get(r).getAsJsonObject();
				final LIElement listItem = Document.get().createLIElement();
				
				DivElement reviewArticle = createReviewArticleDomElement(review);
				
				listItem.appendChild(reviewArticle);
				moreReviewsContainerDomElement.appendChild(listItem);
			}
			
			reviewsList.appendChild(moreReviewsContainerDomElement);
			
			final ButtonElement viewMoreReviewsButton = Document.get().createButtonElement();
			viewMoreReviewsButton.addClassName(style.refButtonFunctionSmall());
			viewMoreReviewsButton.setInnerText("View More"); // Update to be multilingual
			
			Event.sinkEvents(viewMoreReviewsButton, Event.ONCLICK);
			Event.setEventListener(viewMoreReviewsButton, new EventListener() {
	
				@Override
				public void onBrowserEvent(Event event) {
					if (Event.ONCLICK == event.getTypeInt()) {
						moreReviewsContainerDomElement.getStyle().setProperty("display", "block");
						viewMoreReviewsButton.removeFromParent();
					}
				}
			});
		
			reviewsList.appendChild(viewMoreReviewsButton);
		}
	}
	
	private void populateMoreApps(JsonArray jarray) {
		moreAppsList.removeAllChildren();
		for(int a = 0; a < jarray.size() && a < 16; a++) {
			JsonObject jobject = jarray.get(a).getAsJsonObject();		
			final LIElement listItem = Document.get().createLIElement();
			this.moreAppsList.appendChild(listItem);
			
			AnchorElement aElement = Document.get().createAnchorElement();
			AnchorElement aElementTitle = Document.get().createAnchorElement();
			aElement.setHref("#!appdetails/" + String.valueOf(jobject.get("trackId")) + "/" + comingPage + "/" + previousFilter); // requires update to add non-static appdetails path
			ImageElement imgElement = Document.get().createImageElement();
			imgElement.setSrc(jobject.get("artworkUrl100").toString().replace("\"", ""));
			aElementTitle.setInnerText(jobject.get("trackName").toString().replace("\"", ""));
			aElementTitle.setHref("#!appdetails/" + String.valueOf(jobject.get("trackId")) + "/" + comingPage + "/" + previousFilter); // requires update to add non-static appdetails path
			aElement.appendChild(imgElement);
			listItem.appendChild(aElement);
			listItem.appendChild(aElementTitle);
			moreAppsList.appendChild(listItem);
		}
		
		if(jarray.size() > 16) {
			JsonObject firstAppFromMoreAppsList = jarray.get(0).getAsJsonObject();
			if(firstAppFromMoreAppsList.get("artistViewUrl") != null) {
				moreAppsLink.getFirstChildElement().setAttribute("href", firstAppFromMoreAppsList.get("artistViewUrl").toString().replace("\"", ""));
				moreAppsLink.getStyle().setProperty("display", "block");
			}
		} else {
			moreAppsLink.getStyle().setProperty("display", "none");
		}
	}
	
	private void setRatingsToZero() {
		ratingCurrent.removeAllChildren();
		ratingAll.removeAllChildren();
		
		DivElement ratingsContainer = Document.get().createDivElement();
		ratingsContainer.addClassName(style.ratingsContainer());
		DivElement ratingsContainerAll = Document.get().createDivElement();
		ratingsContainerAll.addClassName(style.ratingsContainer());
		
		SpanElement numberOfRatings = Document.get().createSpanElement();
		SpanElement numberOfRatingsAll = Document.get().createSpanElement();
		numberOfRatings.setInnerText("No Ratings");
		ratingCurrent.appendChild(numberOfRatings);
		ratingCurrent.appendChild(ratingsContainer);
				
		numberOfRatingsAll.setInnerText("No Ratings");
		ratingAll.appendChild(numberOfRatingsAll);
		ratingAll.appendChild(ratingsContainerAll);
	}
	
	private void handleAverageRatings(JsonObject data) {
		ratingCurrent.removeAllChildren();
		ratingAll.removeAllChildren();
		DivElement ratingsContainer = Document.get().createDivElement();
		ratingsContainer.addClassName(style.ratingsContainer());
		DivElement ratingsContainerAll = Document.get().createDivElement();
		ratingsContainerAll.addClassName(style.ratingsContainer());
		
		SpanElement numberOfRatings = Document.get().createSpanElement();
		SpanElement numberOfRatingsAll = Document.get().createSpanElement();
		
		if(data.get("averageUserRatingForCurrentVersion") != null) {
			Float ratingAsPercentage = (data.get("averageUserRatingForCurrentVersion").getAsFloat() / 5) * 100;
			DivElement ratingsFull = Document.get().createDivElement();
			ratingsFull.addClassName(style.ratingsFull());
			ratingsFull.getStyle().setProperty("width", ratingAsPercentage + "%");			
			ratingsContainer.appendChild(ratingsFull);
						
			if(data.get("userRatingCountForCurrentVersion") != null) {
				numberOfRatings.setInnerText(data.get("userRatingCountForCurrentVersion").toString() + " Ratings");
				ratingCurrent.appendChild(numberOfRatings);
			}
			ratingCurrent.appendChild(ratingsContainer);
		} else {			
			numberOfRatings.setInnerText("No Ratings"); // update so can be non-static multilingual term
			ratingCurrent.appendChild(numberOfRatings);
			ratingCurrent.appendChild(ratingsContainer);
		}
		
		if(data.get("averageUserRating") != null) {
			Float ratingAsPercentage = (data.get("averageUserRating").getAsFloat() / 5) * 100;
			DivElement ratingsFull = Document.get().createDivElement();
			ratingsFull.addClassName(style.ratingsFull());
			ratingsFull.getStyle().setProperty("width", ratingAsPercentage + "%");			
			ratingsContainerAll.appendChild(ratingsFull);
						
			if(data.get("userRatingCount") != null) {
				numberOfRatingsAll.setInnerText(data.get("userRatingCount").toString() + " Ratings");
				ratingAll.appendChild(numberOfRatingsAll);
			}
			ratingAll.appendChild(ratingsContainerAll);
		} else {			
			numberOfRatingsAll.setInnerText("No Ratings"); // update so can be non-static multilingual term
			ratingAll.appendChild(numberOfRatingsAll);
			ratingAll.appendChild(ratingsContainerAll);
		}
  	}

	private void setAppDetails(JsonObject data) {
		
		if(data.get("trackViewUrl") != null) {
			viewInStore.getStyle().setProperty("display", "block");
			storeName.setInnerText("View in Appstore");
			viewInStore.setHref(data.get("trackViewUrl").toString().replace("\"", ""));
		} else {
			viewInStore.getStyle().setProperty("display", "none");
		}
		
		if (data.get("trackName") != null) {
			trackName.setInnerText(data.get("trackName").toString().replace("\"", ""));
		}
		
		if (data.get("artistName") != null) {
			artistName.setInnerText("By " + data.get("artistName").toString().replace("\"", ""));
		}

		if (data.get("artworkUrl100") != null) {
			image.setUrl(data.get("artworkUrl100").toString().replace("\"", ""));
		}
		
		boolean isLandscape = false;
		int imageWidth = 340; // (320 + 20 margin)
		JsonArray screenshotsArray = data.getAsJsonArray("screenshotUrls");
		if(Window.getClientWidth() < 720) {
			imageWidth = 180;
		}
		
		String stringToMatch = "320x320";
		String firstScreenshotUrl = screenshotsArray.get(0).getAsString();
		if(firstScreenshotUrl.toLowerCase().contains(stringToMatch)) {
			imageWidth = 588;
			isLandscape = true;
			if(Window.getClientWidth() < 720) {
				imageWidth = 340;
			}
		}
		
		screenshotsList.removeAllChildren();	
		this.screenshotsList.getStyle().setProperty("width", screenshotsArray.size() * imageWidth - 20 + "px");
		if(screenshotsArray != null) {
			for (int i = 0; i < screenshotsArray.size(); i++) {
				String srcString = screenshotsArray.get(i).getAsString();
				String imageCSSClass = style.imagePortrait();
				if(isLandscape) {
					srcString = srcString.replace("320x320", "640x640");
					imageCSSClass = style.imageLandscape();
				}
				final LIElement listItem = Document.get().createLIElement();
				this.screenshotsList.appendChild(listItem);
	
				ImageElement imgElement = Document.get().createImageElement();
				imgElement.setSrc(srcString);
				imgElement.addClassName(imageCSSClass);
				listItem.appendChild(imgElement);
			}
		}
		
		if (data.get("formattedPrice") != null) {
			formattedPrice.setInnerText(data.get("formattedPrice").toString().replace("\"", ""));
			formattedPriceDuplicate.setInnerText(data.get("formattedPrice").toString().replace("\"", ""));
		}
		
		if (data.get("genres") != null) {
			String genres = "";
			JsonArray categoriesArray = data.getAsJsonArray("genres");
			for(int g = 0; g < categoriesArray.size(); g++) {
				genres += categoriesArray.get(g).getAsString() + ", ";
			}
			genres = genres.substring(0, genres.length() - 2);
			categories.setInnerText(genres);
		}
		
		if (data.get("fileSizeBytes") != null) {
			int fileSizeBytes = (int)(data.get("fileSizeBytes").getAsFloat() / 1000000);
			fileSize.setInnerText(fileSizeBytes + " MB");
		}
		
		if (data.get("languageCodesISO2A") != null) {
			String languageCodesString = "";
			languagesArray = data.getAsJsonArray("languageCodesISO2A");
			
			for(int g = 0; g < languagesArray.size(); g++) {
				languageCodesString += languagesArray.get(g).getAsString().toLowerCase() + ", ";
			}
			languageCodesString = languageCodesString.substring(0, languageCodesString.length() - 2);
			languageCodes.setInnerText(languageCodesString);
		}
		
		if (data.get("contentAdvisoryRating") != null) {
			rated.setInnerText(data.get("contentAdvisoryRating").toString().replace("\"", ""));
		}
		
		if (data.get("version") != null) {
			version.setInnerText(data.get("version").toString().replace("\"", ""));
		}
		
		if (data.get("releaseDate") != null) {
//			String myString = DateFormat.getDateInstance().format(data.get("releaseDate").toString().replace("\"", ""));
//			Window.alert(myString);
			// Crashes the page, needs fix to format date			
//			try {
//				SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
//				Date dateToFormat = dateFormatter.parse(data.get("releaseDate").toString().replace("\"", ""));
////				String formattedDate = new SimpleDateFormat("dd/MM/yyyy, Ka").format(dateToFormat);
////				releaseDate.setInnerText(formattedDate.toString());
//			} catch (ParseException e) {
//			}
			releaseDate.setInnerText(data.get("releaseDate").toString().replace("\"", ""));
		}
		
		if(data.get("description") != null) {
			String appDescription = htmlForTextWithEmbeddedNewlines(data.get("description").toString().replace("\"", ""));
			String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
			// appDescription = appDescription.replaceAll(regex, "This was a link");
			description.setInnerHTML(appDescription);
		}
		
		if(data.get("features") != null) {
			JsonArray featuresArray = data.getAsJsonArray("features");
			for(int f = 0; f < featuresArray.size(); f++) {
				String featureName = featuresArray.get(f).toString().replace("\"", "");
				if (featureName == "iosUniversal") {					
					universal.setInnerText("Yes");
				} else if (featureName == "gameCenter") {
					gameCenter.setInnerText("Yes");
				}
			}
		}
		
		if(data.get("supportedDevices") != null) {
			String devices = "";
			JsonArray devicesArray = data.getAsJsonArray("supportedDevices");
			for(int d = 0; d < devicesArray.size(); d++) {
				devices += devicesArray.get(d).getAsString() + ", ";
			}
			devices = devices.substring(0, devices.length() - 2);
			supportedDevices.setInnerText(devices);
		}
		
		if(data.get("sellerUrl") != null) {
			developersSiteContainer.getStyle().setProperty("display", "block");
			developersSite.setHref(data.get("sellerUrl").toString().replace("\"", ""));
		}
		
		if(data.get("trackViewUrl") != null) {
			appStoreLinkContainer.getStyle().setProperty("display", "block");
			appStoreLink.setHref(data.get("trackViewUrl").toString().replace("\"", ""));
		}
		
		if(data.get("artistViewUrl") != null) {
			artistStoreLinkContainer.getStyle().setProperty("display", "block");
			artistStoreLink.setHref(data.get("artistViewUrl").toString().replace("\"", ""));
		}
		
		if(data.get("version") != null) {
			appVersionContainer.getStyle().setProperty("display", "block");
			appVersion.setInnerText(data.get("version").toString().replace("\"", ""));
		} else {
			appVersionContainer.getStyle().setProperty("display", "none");
		}
		
		if(data.get("releaseNotes") != null) {
			whatsNewContainer.getStyle().setProperty("display", "block");
			String notes = htmlForTextWithEmbeddedNewlines(data.get("releaseNotes").toString().replace("\"", ""));
			releaseNotesText.setInnerHTML(notes);
		}
	}
	
	private void updateSelectorsFromFilter() {
		FilterController fc = FilterController.get();
		countrySelector.setSelectedIndex(FormHelper.getItemIndex(countrySelector, fc.getFilter().getCountryA2Code()));
	}
	
	private boolean isValidStack(Stack current) {
		return (current != null
				&& PageType.AppDetailsPage.equals(current.getPage())
				&& current.getAction().matches("[0-9]+"));
	}

	private void getAppDetails(String appId) {
		search(appId);
	}
	
	private void setRevenueDownloadTabsEnabled(boolean enable) {
		if (enable) {
			revenueItem.removeClassName(style.isDisabled());
			revenueItem.getStyle().setCursor(Cursor.POINTER);
			downloadsItem.removeClassName(style.isDisabled());
			downloadsItem.getStyle().setCursor(Cursor.POINTER);
			appDetailsItem.removeClassName(style.isDisabled());
			appDetailsItem.getStyle().setCursor(Cursor.POINTER);
		} else {
			revenueItem.addClassName(style.isDisabled());
			revenueItem.getStyle().setCursor(Cursor.DEFAULT);
			revenueLink.setTargetHistoryToken(NavigationController.get().getStack().toString());
			downloadsItem.addClassName(style.isDisabled());
			downloadsItem.getStyle().setCursor(Cursor.DEFAULT);
			downloadsLink.setTargetHistoryToken(NavigationController.get().getStack().toString());
			appDetailsItem.getStyle().setCursor(Cursor.DEFAULT);
			appDetailsLink.setTargetHistoryToken(NavigationController.get().getStack().toString());
		}
	}
	
	private void setRankTabEnabled(boolean enable) {
		if (enable) {
			rankingText.setInnerText("Rank");
			rankingItem.removeClassName(style.isDisabled());
			rankingItem.getStyle().setCursor(Cursor.POINTER);
		} else {
			rankingText.setInnerHTML("Rank <span class=\"text-small\">coming soon</span>");
			rankingItem.addClassName(style.isDisabled());
			rankingItem.getStyle().setCursor(Cursor.DEFAULT);
			rankingLink.setTargetHistoryToken(NavigationController.get().getStack().toString());
		}
	}
	
	private native void removeLookupScripts() /*-{
		$wnd.$('#ref-searchApiLookupGetAppDetails').remove();
		$wnd.$('#ref-searchApiSearchTermMoreApps').remove();
		$wnd.$('#ref-customerReviewsRssRequest').remove();
	}-*/;
	
	private native void search(String searchId) /*-{
		$wnd.$('#ref-searchApiLookupGetAppDetails').remove();
		$wnd.$('body').append(
				$wnd.$("<script>").attr("id", "ref-searchApiLookupGetAppDetails").attr(
						"src",
						"https://itunes.apple.com/gb/lookup?id=" + searchId
								+ "&callback=handleAppDetailsSearch"));
	}-*/;

	private native void searchMoreApps(String artistName) /*-{
		$wnd.$('#ref-searchApiSearchTermMoreApps').remove();
		$wnd.$('body').append(
			$wnd.$("<script>").attr("id", "ref-searchApiSearchTermMoreApps").attr(
					"src",
					"https://itunes.apple.com/gb/search?term=" + artistName
							+ "&media=software&limit=40&entity=software&attribute=softwareDeveloper&callback=handleMoreAppsSearch"));
	}-*/;
	
	private native void searchReviews(String trackId, String countryCode) /*-{
		$wnd.$('#ref-customerReviewsRssRequest').remove();
		$wnd.$('body').append(
			$wnd.$("<script>").attr("id", "ref-customerReviewsRssRequest").attr(
					"src",
					"http://itunes.apple.com/" + countryCode + "/rss/customerreviews/id=" + trackId
							+ "/json?callback=handleReviews"));
	}-*/;
	
	private native void searchAppForRatingByCountry(String trackId, String countryCode) /*-{
	$wnd.$('#ref-searchApiLookupGetAppDetails').remove();
	$wnd.$('body').append(
			$wnd.$("<script>").attr("id", "ref-searchApiLookupGetAppDetails").attr(
					"src",
					"https://itunes.apple.com/" + countryCode + "/lookup?id=" + trackId + "&callback=handleAverageRatings"));
	}-*/;
	
	/**
	 * @param response
	 */
	public static void processAppSearchResponse(String response) {
		JsonElement jelement = new JsonParser().parse(response);
		if(jelement != null) {
			JsonObject jobject = jelement.getAsJsonObject();
			if(jobject != null) {
				JsonArray jarray = jobject.getAsJsonArray("results");
				if(jarray != null && jarray.size() > 0) {
					jobject = jarray.get(0).getAsJsonObject();

					INSTANCE.setAppDetails(jobject);
					if(jobject.get("artistId") != null) {
						INSTANCE.artistId = jobject.get("artistId").toString();
					}
					if (jobject.get("artistName") != null) {
						INSTANCE.searchMoreApps(jobject.get("artistName").toString());
					}
					
					if (jobject.get("trackId") != null) {
						INSTANCE.searchReviews(jobject.get("trackId").toString(), String.valueOf(INSTANCE.countrySelector.getSelectedValue()));	
					}
				}
			}
		}
	}
	
	public static void processMoreAppsSearchResponse(String response) {
		JsonElement jelement = new JsonParser().parse(response);
		if(jelement != null) {
			JsonObject jobject = jelement.getAsJsonObject();
			if(jobject != null) {
				JsonArray jarray = jobject.getAsJsonArray("results");
				if(jarray != null && jarray.size() > 0) {
					// make sure results match artistId with current artistId
					JsonArray filteredArray = new JsonArray();
					for(int a = 0; a < jarray.size(); a++) {
						JsonObject thisResult = jarray.get(a).getAsJsonObject();
						if(thisResult.get("artistId").toString() == INSTANCE.artistId) {
							filteredArray.add(thisResult);
						}
					}
					
					if(filteredArray != null && filteredArray.size() > 0) {
						INSTANCE.moreAppsListContainer.getStyle().setProperty("display", "block");
						INSTANCE.populateMoreApps(filteredArray);
					}
				}
			}
		}
	}
	
	public static void processReviewsResponse(String response) {
		JsonElement jelement = new JsonParser().parse(response);
		if(jelement != null) {
			JsonObject jobject = jelement.getAsJsonObject();
			if(jobject != null) {
				JsonObject jobjectFeed = jobject.getAsJsonObject("feed");
				JsonArray entries = jobjectFeed.getAsJsonArray("entry");
				if(entries != null && entries.size() > 0) {
					INSTANCE.reviewsContainer.getStyle().setProperty("display", "block");
					INSTANCE.populateReviews(entries);
				} else {
					INSTANCE.reviewsContainer.getStyle().setProperty("display", "none");
				}
			} else {
				INSTANCE.reviewsContainer.getStyle().setProperty("display", "none");
			}
		} else {
			INSTANCE.reviewsContainer.getStyle().setProperty("display", "none");
		}
	}
	
	public static void processRatingsResponse(String response) {
		JsonElement jelement = new JsonParser().parse(response);
		if(jelement != null) {
			JsonObject jobject = jelement.getAsJsonObject();
			if(jobject != null) {
				JsonArray jarray = jobject.getAsJsonArray("results");
				if(jarray != null && jarray.size() > 0) {
					jobject = jarray.get(0).getAsJsonObject();
				
					INSTANCE.handleAverageRatings(jobject);
				} else {
					INSTANCE.setRatingsToZero();
				}
			}
		}
	}
	
	public static void processLanguagesResponse(String response) {
		Window.alert("processLanguagesResponse");
	}	

	public static native void exportAppDetailsResponseHandler() /*-{
		$wnd.processAppSearchResponse = $entry(@io.reflection.app.client.page.AppDetails::processAppSearchResponse(Ljava/lang/String;));
	}-*/;
	
	public static native void exportMoreAppDetailsResponseHandler() /*-{
		$wnd.processMoreAppsSearchResponse = $entry(@io.reflection.app.client.page.AppDetails::processMoreAppsSearchResponse(Ljava/lang/String;));
	}-*/;
	
	public static native void exportReviewsResponseHandler() /*-{
		$wnd.processReviewsResponse = $entry(@io.reflection.app.client.page.AppDetails::processReviewsResponse(Ljava/lang/String;));
	}-*/;
	
	public static native void exportRatingsResponseHandler() /*-{
		$wnd.processRatingsResponse = $entry(@io.reflection.app.client.page.AppDetails::processRatingsResponse(Ljava/lang/String;));
	}-*/;
	
	public static native void exportLanguagesResponseHandler() /*-{
	 	$wnd.processLanguagesResponse = $entry(@io.reflection.app.client.page.AppDetails::processLanguagesResponse(Ljava/lang/String;));
	}-*/;
}