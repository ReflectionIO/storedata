package io.reflection.app.client.page;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;

import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.helper.ResponsiveDesignHelper;
import io.reflection.app.datatypes.shared.Item;

public class AppDetails extends Page implements NavigationEventHandler {

	private static AppDetailsUiBinder uiBinder = GWT.create(AppDetailsUiBinder.class);

	interface AppDetailsUiBinder extends UiBinder<Widget, AppDetails> {}

	private static AppDetails INSTANCE = null;

	@UiField HeadingElement trackName;
	@UiField Image image;
	@UiField SpanElement artistName;
	@UiField SpanElement storeName;
	@UiField AnchorElement viewInStore;	
	@UiField SpanElement formattedPrice;
	@UiField Element formattedPriceDuplicate;
	@UiField Element categories;
	@UiField Element fileSize;
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
	private String iapDescription;

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

	private String displayingAppId;
	private String comingPage;
	private Item displayingApp;

	public AppDetails() {
		initWidget(uiBinder.createAndBindUi(this));
		INSTANCE = this;
		
		getAppDetails("903183877");
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

	private void setAppDetails(JsonObject data) {

		if (data.get("trackName") != null) {
			trackName.setInnerText(data.get("trackName").toString().replace("\"", ""));
		}
		if (data.get("artistName") != null) {
			artistName.setInnerText(data.get("artistName").toString().replace("\"", ""));
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
		this.screenshotsList.getStyle().setProperty("width", screenshotsArray.size() * imageWidth - 20 + "px");
		if(screenshotsArray != null) {
			for (int i = 0; i < screenshotsArray.size(); i++) {
				String srcString = screenshotsArray.get(i).getAsString();
				String imageCSSClass = "image-portrait";
				if(isLandscape) {
					srcString = srcString.replace("320x320", "640x640");
					imageCSSClass = "image-landscape";
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
		
		if (data.get("contentAdvisoryRating") != null) {
			rated.setInnerText(data.get("contentAdvisoryRating").toString().replace("\"", ""));
		}
		if (data.get("version") != null) {
			version.setInnerText(data.get("version").toString().replace("\"", ""));
		}
		
		if (data.get("releaseDate") != null) {
			// Crashes the page, needs fix to format date			
//			try {
//				SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
//				Date dateToFormat = dateFormatter.parse(data.get("releaseDate").toString().replace("\"", ""));
//				String formattedDate = new SimpleDateFormat("dd/MM/yyyy, Ka").format(dateToFormat);
//				releaseDate.setInnerText(formattedDate.toString());
//			} catch (ParseException e) {
//				// Log exception
//			}
			releaseDate.setInnerText(data.get("releaseDate").toString().replace("\"", ""));
		}
		
		if (data.get("description") != null) {
			String appDescription = htmlForTextWithEmbeddedNewlines(data.get("description").toString().replace("\"", ""));
  			// appDescription = urlify(appDescription);
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
			developersSite.setHref(data.get("sellerUrl").toString().replace("\"", ""));
		} else {
			developersSiteContainer.removeFromParent();
		}
		
		if(data.get("trackViewUrl") != null) {
			appStoreLink.setHref(data.get("trackViewUrl").toString().replace("\"", ""));
		} else {
			appStoreLinkContainer.removeFromParent();
		}
		
		if(data.get("artistViewUrl") != null) {
			artistStoreLink.setHref(data.get("artistViewUrl").toString().replace("\"", ""));
		} else {
			artistStoreLinkContainer.removeFromParent();
		}
		
		if(data.get("version") != null) {
			appVersion.setInnerText(data.get("version").toString().replace("\"", ""));
		} else {
			appVersionContainer.removeFromParent();
		}
		
		if(data.get("releaseNotes") != null) {
			String notes = htmlForTextWithEmbeddedNewlines(data.get("releaseNotes").toString().replace("\"", ""));
			releaseNotesText.setInnerHTML(notes);
		} else {
			whatsNewContainer.removeFromParent();
		}
	}

	@Override
	public void navigationChanged(Stack previous, Stack current) {
		String newInternalId = current.getAction();
		getAppDetails(newInternalId);
		
		storeName.setInnerText("View in Appstore");

		ResponsiveDesignHelper.makeTabsResponsive();
	}

	private void getAppDetails(String appId) {
		search(appId);
	}

	/**
	 * @param string
	 */
	private native void search(String searchId) /*-{
		$wnd.$('body').append(
				$wnd.$("<script>").attr("id", "get-app-details").attr(
						"src",
						"https://itunes.apple.com/gb/lookup?id=" + searchId
								+ "&callback=handleAppDetailsSearch"));
	}-*/;

	/**
	 * @param response
	 */
	public static void processAppSearchResponse(String response) {
		JsonElement jelement = new JsonParser().parse(response);
		JsonObject jobject = jelement.getAsJsonObject();
		JsonArray jarray = jobject.getAsJsonArray("results");
		jobject = jarray.get(0).getAsJsonObject();

		INSTANCE.setAppDetails(jobject);
	}

	public static native void exportAppDetailsResponseHandler() /*-{
		$wnd.processAppSearchResponse = $entry(@io.reflection.app.client.page.AppDetails::processAppSearchResponse(Ljava/lang/String;));
	}-*/;
}