//
//  ContactPage.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 4 Sep 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Stefano Capuzzi (capuzzistefano)
 *
 */
public class ContactPage extends Page {

	private static ContactPageUiBinder uiBinder = GWT.create(ContactPageUiBinder.class);

	interface ContactPageUiBinder extends UiBinder<Widget, ContactPage> {}
	
	private boolean mapScriptInjected;

	public ContactPage() {
		initWidget(uiBinder.createAndBindUi(this));

		 nativeInitMap();
	}

	native void nativeInitMap() /*-{

		$wnd.refMap; // global map object
		$wnd.generateMap = function() {
			$wnd.refMap = new $wnd.reflectionMap();
			mapTop = $wnd.$('.contact__map-container').offset().top;
			dropped = false;

			if ($wnd.$('.no-touch').length > 0
					&& $wnd.$($wnd).scrollTop() < (mapTop - ($wnd.$($wnd)
							.height() / 2))) {
				$wnd.$($wnd).on(
						"scroll",
						function() {
							if (!dropped) {
								if ($wnd.$($wnd).scrollTop() > (mapTop - ($wnd
										.$($wnd).height() / 2))) {
									$wnd.refMap.addMarker();
									dropped = true;
								}
							}
						});
			} else {
				$wnd.refMap.addMarker();
				dropped = true;
			}
		}

		$wnd.reflectionMap = function() {
			var isDraggable = $wnd.$('html.touch').length == 0;
			var mapStyles = [ {
				featureType : "poi",
				elementType : "labels",
				stylers : [ {
					visibility : "off"
				} ]
			} ];
			this.myLatlng = new $wnd.google.maps.LatLng(51.518680, -0.136578);
			this.mapOptions = {
				zoom : 17,
				center : this.myLatlng,
				disableDefaultUI : true,
				scrollwheel : false,
				streetViewControl : true,
				draggable : isDraggable,
				styles : mapStyles
			}
			this.markerImage = {
				url : 'images/contact/Map_Pin@2x.png',
				size : new $wnd.google.maps.Size(30, 43),
				scaledSize : new $wnd.google.maps.Size(30, 43),
				anchor : new $wnd.google.maps.Point(30, 43)
			};
			this.map = new $wnd.google.maps.Map($doc
					.getElementById("js-map--contact"), this.mapOptions);

			this.addMarker = function() {
				var marker = this.marker = new $wnd.google.maps.Marker({
					position : this.myLatlng,
					map : this.map,
					title : "40-44 Newman Street",
					icon : this.markerImage,
					animation : $wnd.google.maps.Animation.DROP
				});

				var contentString = '<div class="map__info-box">'
						+ '<h1>Reflection</h1>'
						+ '<div class="map__text-content">'
						+ '<p>40-44 Newman Street<br />London<br />W1T 1QD</p>'
						+ '<p onclick="window.refMap.toggleStreetView();" class="map__street-view-link">Streetview</p>'
						+ '<p><a href="https://www.google.co.uk/maps/place/44+Newman+St,+Marylebone,+London+W1T+1QD/@51.5187779,-0.1364135,17z/data=!4m7!1m4!3m3!1s0x48761b2b95192b1b:0xc2fcb9753b8ff12b!2s44+Newman+St,+Marylebone,+London+W1T+1QD!3b1!3m1!1s0x48761b2b95192b1b:0xc2fcb9753b8ff12b" target="_blank">Open in Google Maps</a></p></div></div><div class="map__info-box__down-arrow"></div>';

				var infowindow = new $wnd.google.maps.InfoWindow({
					content : contentString
				});

				$wnd.google.maps.event.addListener(marker, 'click', function() {
					infowindow.open(this.map, marker);
				});
			}

			this.panorama = this.map.getStreetView();
			this.panorama.setPosition(new $wnd.google.maps.LatLng(51.518660,
					-0.136540));
			this.panorama.setPov(({
				heading : 40,
				pitch : 0
			}));

			this.toggleStreetView = function() {
				var toggle = this.panorama.getVisible();
				if (toggle == false) {
					this.panorama.setVisible(true);
				} else {
					this.panorama.setVisible(false);
				}
			}

			return this;
		}

	}-*/;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		if (!mapScriptInjected) {
			ScriptInjector.fromUrl("https://maps.googleapis.com/maps/api/js?key=AIzaSyD7mXBIrN4EgMflWKxUOK6C9rfoDMa5zyo&callback=generateMap")
					.setWindow(ScriptInjector.TOP_WINDOW).inject();
			mapScriptInjected = true;
		}
	}
}
