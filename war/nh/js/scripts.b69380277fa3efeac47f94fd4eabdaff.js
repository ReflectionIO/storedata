// Landing page map
google.maps.event.addDomListener(window, 'load', initializeMap);

function initializeMap() {
	var myLatlng = new google.maps.LatLng(51.518717, -0.136253);
	var mapOptions = {
	  zoom: 17,
	  center: myLatlng,
	  disableDefaultUI: true,
	  scrollwheel: false
	}
	var markerImage = {
    url: 'imgs/contact/Map_Pin@2x.png',
    size: new google.maps.Size(30, 43),
    scaledSize: new google.maps.Size(30, 43),
    anchor: new google.maps.Point(30, 43)
  };
	var map = new google.maps.Map(document.getElementById("js-map-location"), mapOptions);
	var marker = new google.maps.Marker({
	    position: myLatlng,
	    map: map,
	    title: "40-45 Newman Street",
	    icon: markerImage
	});
}

// jQuery SIAF
(function($) {

	initLinkToPageTop();
	initScrollToAnchor();
	initLandingScrollEffect();

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
		if($('.window-height-scroll-effect-container').length > 0 && $('.csstransforms').length > 0) {
			var windowHeight = $(window).height(),
					windowWidth = $(window).width(),
					breakpointVertical = 680,
					breakpointHorizontal = 720;
			
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