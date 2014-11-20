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
	var map = new google.maps.Map(document.getElementById("js-map-location"), mapOptions);
	var marker = new google.maps.Marker({
	    position: myLatlng,
	    map: map,
	    title: "40-45 Newman Street"
	});
}

// jQuery SIAF
(function($) {

	initLinkToPageTop();

	function initLinkToPageTop() {
		var linkToPageTop = $('.link-to-page-top');
		if(linkToPageTop.length > 0) {
			linkToPageTop.click(function (e) {
	      e.preventDefault();
	      $('html, body').animate({ scrollTop: 0 }, 'slow', 'swing');	        
	  	});
		}		
	}

})(jQuery);