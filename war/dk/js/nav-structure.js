(function($) {

	function initialise() {
		var ieVersion = detectIE();
		//alert(ieVersion);
		if(ieVersion) {
			$('body').addClass('is-ie ie' + ieVersion);
		}
		setMainContentWidthForIE();
		initBrowserPulling();
		initMainNavCollapsibleLists();
		initLeftPanelInteraction();
		initGlobalFormInteractions();
		initRightPanelInteraction();
		initCustomScrollbar();
	};

	function initBrowserPulling() {
		$(window).resize(function () {
			setMainContentWidthForIE();
	  });
	}

	function detectIE() {
    var ua = window.navigator.userAgent;
    var msie = ua.indexOf('MSIE ');
    var trident = ua.indexOf('Trident/');

    if (msie > 0) {
        // IE 10 or older => return version number
        return parseInt(ua.substring(msie + 5, ua.indexOf('.', msie)), 10);
    }

    if (trident > 0) {
        // IE 11 (or newer) => return version number
        var rv = ua.indexOf('rv:');
        return parseInt(ua.substring(rv + 3, ua.indexOf('.', rv)), 10);
    }

    // other browser
    return false;
	}

	function setMainContentWidthForIE() {
		// calc(100%-220px) CSS does work for IE to set the width, but the width transition effect doesn't work for calc'd width in IE
		// calculate the width for IE instead using JavaScript
		if($('.is-ie').length > 0) {
			if($(window).width() > 960) {
				if($('.panel-left-open').length > 0) {
					var mainContentElement = $('.is-ie .l-main');
					mainContentElement.width($(window).width() - 220);
				}
				else {
					$('.is-ie .l-main').width("100%");
				}
			}
			else {
				$('.is-ie .l-main').width("100%");
			}
		}
	}

	function initLeftPanelInteraction() {

		$('.js-hamburger-button').on("click", function(){
			$(this).toggleClass('is-selected');
			if($('body').hasClass('panel-left-open')) {
				$('.panel-left').addClass('is-animating-out');
				$('body').removeClass('panel-left-open');
				setMainContentWidthForIE();
				window.setTimeout(function(){
					$('.panel-left').removeClass('is-animating-out'); // class for motion blur effect
				}, 140);
			} else {
				$('body').toggleClass('panel-left-open');
				$('.panel-left').addClass('is-animating-in');
				setMainContentWidthForIE();
				window.setTimeout(function(){
					$('.panel-left').removeClass('is-animating-in'); // class for motion blur effect
				}, 140);
			}
		});

		var topLevelNavItems = $('.js-main-nav > ul > li');
		topLevelNavItems.on("click", function(){
			if(!$(this).hasClass("js-is-collapsible")) {
				topLevelNavItems.removeClass('is-selected');
				$(this).addClass('is-selected');
				$('.js-main-nav > ul > li.js-is-collapsible.is-open > a').trigger("click");
			}
		});

		$('.js-main-nav > ul > li.js-is-collapsible > a').on("click", function(e){
			e.preventDefault();
			var collapsibleContainer = $(this).parent('.js-is-collapsible');
			var collapsibleList = $(this).next('ul');
			if(collapsibleContainer.hasClass('is-open')) {
				collapsibleList.css("margin-top", -collapsibleList.height());
				collapsibleContainer.removeClass('is-open');
				collapsibleContainer.removeClass('is-selected');
			}
			else {
				collapsibleList.css("margin-top", 0);
				collapsibleContainer.addClass('is-open');
				collapsibleContainer.addClass('is-selected');
				collapsibleContainer.siblings().removeClass('is-selected');
			}
		});

		if($(window).width() <= 960) {
			$('.js-hamburger-button').removeClass('is-selected');
			$('body').removeClass('panel-left-open');
		}
	}

	function initMainNavCollapsibleLists() {

		// Collapse all menu items if not open
		var topLevelNavItems = $('.js-main-nav > ul > li');	
		topLevelNavItems.each(function(){
			var totalHeight = 0;
			$(this).find('li.has-child').each(function(){
				var collapsibleList = $(this).children('ul');
				collapsibleList.css("margin-top", -collapsibleList.height());
				totalHeight += collapsibleList.height();
			});
			var collapsibleList = $(this).children('ul');
			collapsibleList.css("margin-top", -collapsibleList.height());
		});

		$('.js-main-nav > ul > li ul li.js-is-collapsible a').on("click", function(e){
			e.preventDefault();
			var collapsibleContainer = $(this).parent('.js-is-collapsible');
			var collapsibleList = $(this).next('ul');
			if(collapsibleContainer.hasClass('is-open')) {
				collapsibleList.css("margin-top", -collapsibleList.height());
				collapsibleContainer.removeClass('is-open');
			}
			else {
				collapsibleList.css("margin-top", 0);
				collapsibleContainer.addClass('is-open');
			}
		});

		window.setTimeout(function() {
			topLevelNavItems.each(function(){
				if($(this).hasClass('is-selected')) {
					$(this).children("a").trigger("click");
					$(this).find("li.is-selected.js-is-collapsible > a").trigger("click");
				}
			});
		}, 100);
	}

	function initGlobalFormInteractions() {
		$('.form-field input[type=email], .form-field input[type=password], .form-field input[type=text]').each(function(){
			var $this = $(this);
			var $thisParent = $this.parent('.form-field');
			if(!$(this).val().length) {
				$thisParent.addClass('is-closed');
			}
			$this.on("focus", function(){
				$thisParent.removeClass('is-closed');
			});
			$this.on("blur", function(){
				if(!$this.val().length) {
					$thisParent.addClass('is-closed');
				}
			});
		});
	}

	function initRightPanelInteraction() {
		$('.actions-group').on("click", function() {
			if(!$('.actions-group__content').is(':visible')) {
				$('.panel-right-container').toggleClass('is-showing');
				$('.actions-group').toggleClass('is-on');
				$('body').toggleClass('no-scroll');
				$('.l-page-container').toggleClass('is-blurred');
				$('.panel-left').toggleClass('is-blurred');
			}
		});
		$('.link-log-in').on("click", function(e) {
			e.preventDefault();
			if($('.panel-right-container').hasClass('is-showing')) {
				$('.panel-right-container').addClass('is-animating-out');
				$('.panel-right-container').removeClass('is-showing');
				window.setTimeout(function(){
					$('.panel-right-container').removeClass('is-animating-out');
				}, 180);
			}
			else {
				$('.panel-right-container').addClass('is-animating-in');
				$('.panel-right-container').addClass('is-showing');
				window.setTimeout(function(){
					$('.panel-right-container').removeClass('is-animating-in');
				}, 240);
			}
			$('body').toggleClass('no-scroll');
			$('.l-page-container').toggleClass('is-blurred');
			$('.panel-left').toggleClass('is-blurred');
		});
		$('.panel-right__overlay').on("click", function() {
			if(!$('.actions-group__content').is(':visible')) {
				$('.actions-group').trigger("click");
			}
			else {
				$('.link-log-in').trigger("click");
			}
		});
	}

	function initCustomScrollbar() {
		if($('.ie10').length == 0 && $('.ie8').length == 0) {
			$(".js-custom-scrollbar").mCustomScrollbar({
	    	scrollInertia: 200
	    });
		}
	}

	$(document).ready(function(){
		initialise();
	});

})(jQuery);