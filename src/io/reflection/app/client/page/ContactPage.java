//
//  ContactPage.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 4 Sep 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
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

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				ScriptInjector.fromUrl("https://maps.googleapis.com/maps/api/js?callback=generateMapContact").setWindow(ScriptInjector.TOP_WINDOW).inject();
			}
		});
	}

	native void nativeInitMap() /*-{

		$wnd.refMap; // global map object
		$wnd.generateMapContact = function() {
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
			var zoomValue = 17;
			if ($wnd.$($wnd).innerWidth() < 720) {
				zoomValue = 16;
			}
			this.myLatlng = new $wnd.google.maps.LatLng(51.518680, -0.136578);
			this.mapOptions = {
				zoom : zoomValue,
				zoomControl : true,
				center : this.myLatlng,
				disableDefaultUI : true,
				scrollwheel : false,
				streetViewControl : true,
				draggable : isDraggable,
				styles : mapStyles
			}
			this.markerImage = {
				url : 'images/map-marker.png',
				size : new $wnd.google.maps.Size(34, 49),
				scaledSize : new $wnd.google.maps.Size(34, 49),
				anchor : new $wnd.google.maps.Point(0, 53)
			};
			this.map = new $wnd.google.maps.Map($doc
					.getElementById("js-map--contact"), this.mapOptions);

			var marker = this.marker = new $wnd.google.maps.Marker({
				position : this.myLatlng,
				map : this.map,
				title : "40-44 Newman Street",
				icon : this.markerImage,
			// animation: google.maps.Animation.DROP
			});

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

	}
}
