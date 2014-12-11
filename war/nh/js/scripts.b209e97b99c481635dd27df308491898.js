// jQuery SIAF
(function($) {

	window.refMap; // global map object

	initLinkToPageTop();
	initScrollToAnchor();
	initLandingScrollEffect();
	$(window).on("load", function(){
		initializeMap();
	});

	reflectionMap = function() {
		var isDraggable = $('html.touch').length == 0;
		this.myLatlng = new google.maps.LatLng(51.518680, -0.136578);
		this.mapOptions = {
		  zoom: 17,
		  center: this.myLatlng,
		  disableDefaultUI: true,
		  scrollwheel: false,
		  streetViewControl: true,
		  draggable: isDraggable
		}
		this.markerImage = {
	    url: 'imgs/contact/Map_Pin@2x.png',
	    size: new google.maps.Size(30, 43),
	    scaledSize: new google.maps.Size(30, 43),
	    anchor: new google.maps.Point(30, 43)
	  };
		this.map = new google.maps.Map(document.getElementById("js-map-location"), this.mapOptions);	

		this.addMarker = function() {
			var marker = this.marker = new google.maps.Marker({
		    position: this.myLatlng,
		    map: this.map,
		    title: "40-44 Newman Street",
		    icon: this.markerImage,
		    animation: google.maps.Animation.DROP
			});

			var contentString = '<div class="map__info-box">'+
      '<h1>Reflection</h1>'+
      '<div class="map__text-content">' +
      '<p>40-44 Newman Street<br />London<br />W1T 1QD</p>' +
      '<p onclick="window.refMap.toggleStreetView();" class="map__street-view-link">Streetview</p>' +
      '<p><a href="https://www.google.co.uk/maps/place/44+Newman+St,+Marylebone,+London+W1T+1QD/@51.5187779,-0.1364135,17z/data=!4m7!1m4!3m3!1s0x48761b2b95192b1b:0xc2fcb9753b8ff12b!2s44+Newman+St,+Marylebone,+London+W1T+1QD!3b1!3m1!1s0x48761b2b95192b1b:0xc2fcb9753b8ff12b" target="_blank">Open in Google Maps</a></p></div></div><div class="map__info-box__down-arrow"></div>';

		  var infowindow = new google.maps.InfoWindow({
		      content: contentString
		  });

			google.maps.event.addListener(marker, 'click', function() {
	    	infowindow.open(this.map,marker);
	  	});	  	
		}

		// We get the map's default panorama and set up some defaults.
	  // Note that we don't yet set it visible.
	  this.panorama = this.map.getStreetView();
	  this.panorama.setPosition(new google.maps.LatLng(51.518660, -0.136540));
	  this.panorama.setPov(/** @type {google.maps.StreetViewPov} */({
	    heading: 40,
	    pitch: 0
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

	function initializeMap() {
		window.refMap = new reflectionMap(),
				$win = $(window),
				mapTop = $('.landing-section-map').offset().top,
				dropped = false;

		if($('.no-touch').length > 0 && $win.scrollTop() < (mapTop - ($win.height()/2))) {
			$win.on("scroll", function(){
				if(!dropped) {
					if($win.scrollTop() > (mapTop - ($win.height()/2))) {
						window.refMap.addMarker();
						dropped = true;
					}
				}			
			});
		}
		else {
			window.refMap.addMarker();
			dropped = true;
		}
	}

	function initLinkToPageTop() {
		var linkToPageTop = $('.link-to-page-top');
		if(linkToPageTop.length > 0) {
			linkToPageTop.click(function (e) {
	      e.preventDefault();
	      $('html, body').animate({ scrollTop: 0 }, 'slow', 'swing');
	  	});
		}		
	}

	function initScrollToAnchor() {
		if($('.js-scroll-to-anchor').length > 0) {
			$('.js-scroll-to-anchor').click(function(e){
				e.preventDefault();
				var anchor = $(this).attr("href"),
					navHeight = 60,
					targetTop = $(anchor).offset().top;
      	$('html, body').animate({ scrollTop: targetTop - navHeight }, 455, 'swing');	
			});
		}
	}

	function initLandingScrollEffect() {
		var scrollValue = $('#scroll-value');
		$(window).bind('touchmove', function(e) { 
		    scrollValue.text($(this).scrollTop());
		});
		if($('.window-height-scroll-effect-container').length > 0 && $('.csstransforms').length > 0 && $('.no-touch').length > 0) {
			var windowHeight = $(window).height(),
					windowWidth = $(window).width(),
					breakpointVertical = 680,
					breakpointHorizontal = 980;
			
			if(windowHeight >= breakpointVertical && windowWidth >= breakpointHorizontal) {
				$(window).scroll(function(){
					var scrollTop = $(window).scrollTop(),
							scrollAsPercentageOfWindowHeight = (scrollTop / windowHeight) * 50,
							scale = 1 - (scrollAsPercentageOfWindowHeight/100),
							opacityScale = 1 - ((scrollAsPercentageOfWindowHeight * 2.5)/100),
							opacityScaleShadowLayer = 0 + ((scrollAsPercentageOfWindowHeight * 2)/85);

					$('.landing-section-main').css({'-ms-transform': 'scale(' + scale + ')', '-webkit-transform': 'scale(' + scale + ')', 'transform': 'scale(' + scale + ')', 'top': -scrollTop/4.3 + 'px', 'opacity': opacityScale});
					$('.section-size-shadow-layer').css('opacity', opacityScaleShadowLayer);
				});
			}
		}
	}

})(jQuery);