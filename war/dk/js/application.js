/* APPLICATION JAVASCRIPT
// Use prototypal inheritance to set functions relevant to the page to encapsulate functions (page objects below)
// Use functions as variables (component objects below) to modularise and encapsulate component functionality


/* PAGE OBJECTS FOR TEMPLATES */

// Page object
	var Page = function() {
		instance = this;
		new BrowserDetection();
		new LeftPanelAndHamburger();
		new FormInteractions();
		new PanelRightOverlay();
		new PanelRightMisplacedPassword();
		new AccountContainer();
		new SearchContainer();
		this.customScrollbars();
		this.pictureFillFix();
	};

	Page.prototype.customScrollbars = function() {
		// load custom scroll bars for everything but IE9/10
		if (/*@cc_on!@*/true) {
			var script = document.createElement('script');
		  script.type = 'text/javascript';
		  script.src = 'js/vendor/jquery.mCustomScrollbar.concat.min.js';
	  	$('#js-appendScriptsContainer').append(script);
		}

		if($('.ie10').length == 0 && $('.ie8').length == 0) {
			$(".js-custom-scrollbar").mCustomScrollbar({
	    	scrollInertia: 200
	    });
		}
	};

	Page.prototype.pictureFillFix = function() {
		if($('.is-firefox').length) {
			/**
			 * FF's first picture implementation is static and does not react to viewport changes, this tiny script fixes this.
			 */
			var ua = navigator.userAgent;

			if(window.HTMLPictureElement && ((/ecko/).test(ua) && ua.match(/rv\:(\d+)/) && RegExp.$1 < 41)){
				addEventListener('resize', (function(){
					var timer;

					var dummySrc = document.createElement('source');

					var fixPicture = function(img){
						var picture = img.parentNode;
						var source = dummySrc.cloneNode();
						picture.insertBefore(source, picture.firstElementChild);
						setTimeout(function(){
							picture.removeChild(source);
						});
					};

					var findPictureImgs = function(){
						var i;
						var imgs = document.querySelectorAll('picture > img');
						for(i = 0; i < imgs.length; i++){
							if(imgs[i].complete){
								if(imgs[i].currentSrc){
									fixPicture(imgs[i]);
								}
							} else if(imgs[i].currentSrc){
								removeEventListener('resize', onResize);
								break;
							}
						}
					};
					var onResize = function(){
						clearTimeout(timer);
						timer = setTimeout(findPictureImgs, 99);
					};

					dummySrc.srcset = 'data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==';

					return onResize;
				})());
			}
		}
	}


// object for browser detection related functionality
	var BrowserDetection = function() {
		this.setIEClass();
		this.setChromeClass();
		this.setOperaClass();
		this.setSafariClass();
		this.setFireFoxClass();
	}

	BrowserDetection.prototype.setIEClass = function() {
		this.ieVersion = this.detectIE();
		if(this.ieVersion) {
			$('body').addClass('is-ie ie' + this.ieVersion);
		}
	};

	BrowserDetection.prototype.setChromeClass = function() {
		var isChrome = !!window.chrome && !!window.chrome.webstore;
		if(isChrome) {
			$('html').addClass('is-chrome');
		}
	};

	BrowserDetection.prototype.setOperaClass = function() {
		var isOpera = !!window.opera || /opera|opr/i.test(navigator.userAgent);
		if(isOpera) {
			$('html').addClass('is-opera');
		}
	};

	BrowserDetection.prototype.setSafariClass = function() {
		var isChrome = navigator.userAgent.indexOf('Chrome') > -1;
	  var isSafari = navigator.userAgent.toLowerCase().indexOf('safari/') > -1;
	  if (isChrome && isSafari) {
	  	isSafari = false;
	  }
	  if(isSafari) {
			$('html').addClass('is-safari');
		}
	}

	BrowserDetection.prototype.detectIE = function() {
	  var ua = window.navigator.userAgent;
	  var msie = ua.indexOf('MSIE ');
	  var trident = ua.indexOf('Trident/');
	  var edge = ua.indexOf('Edge/');

	  if (msie > 0) {
	      // IE 10 or older => return version number
	      return parseInt(ua.substring(msie + 5, ua.indexOf('.', msie)), 10);
	  }

	  if (trident > 0) {
	      // IE 11 (or newer) => return version number
	      var rv = ua.indexOf('rv:');
	      return parseInt(ua.substring(rv + 3, ua.indexOf('.', rv)), 10);
	  }

	  if (edge > 0) {
	  	return "edge";
	  }

	  // other browser
	  return false;
	};

	BrowserDetection.prototype.setFireFoxClass = function() {
		var is_firefox = navigator.userAgent.toLowerCase().indexOf('firefox') > -1;
		if(is_firefox) { $('html').addClass('is-firefox'); }
	}


/* COMPONENT OBJECTS */
	var LeftPanelAndHamburger = function() {

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

		var topLevelNavItems = $('.js-main-nav > ul > li'),
			siteNavTopLevelItems = $('.js-main-navigation > ul > li'),
			accountNavTopLevelItems = $('.js-account-navigation > ul > li');
		
		topLevelNavItems.on("click", function(){
			$clickedItem = $(this);
			if(!$(this).hasClass("js-is-collapsible")) {
				if($clickedItem.parents('.js-main-navigation').length > 0) {
					siteNavTopLevelItems.removeClass('is-selected');
					$clickedItem.addClass('is-selected');
					$('.js-main-navigation > ul > li.js-is-collapsible.is-open > a').trigger("click");
				} else {
					if($clickedItem.parents('.js-account-navigation').length > 0) {
						accountNavTopLevelItems.removeClass('is-selected');
						$clickedItem.addClass('is-selected');
						$('.js-account-navigation > ul > li.js-is-collapsible.is-open > a').trigger("click");
					}
				}
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
			$('body, html').removeClass('panel-left-open');
		}

		// hide panel on content click/tap
		if($('html.touch').length && $(window).width() < 940) {
			$('.l-main').on("click", function() {
				if($('.panel-left-open').length) {
					$('.js-hamburger-button').trigger("click");
				}
			});
		}

		// Hamburger/left panel interaction
		$('.js-hamburger-button').on("click", function(){
			$(this).toggleClass('is-selected');
			if($('body').hasClass('panel-left-open')) {
				$('body, html').removeClass('panel-left-open');			
				if(!$('html.is-chrome').length && !$('html.is-opera').length) {
					$('.panel-left').addClass('is-animating-out');
					$('.l-main').addClass('is-animating-out');
					window.setTimeout(function(){
						$('.panel-left, .l-main').removeClass('is-animating-out'); // class for motion blur effect
					}, 140);
				}
			} else {
				$('body, html').toggleClass('panel-left-open');
				if(!$('html.is-chrome').length && !$('html.is-opera').length) {
					$('.panel-left').addClass('is-animating-in');
					$('.l-main').addClass('is-animating-in');
					window.setTimeout(function(){
						$('.panel-left, .l-main').removeClass('is-animating-in'); // class for motion blur effect
					}, 140);
				}
			}
		});
	};


	var FormInteractions = function() {
		setTimeout(function(){
			$('.form-field input[type=email], .form-field input[type=password], .form-field input[type=text], .form-field textarea').each(function(){
				var $this = $(this);
				var $thisParent = $this.parent('.form-field');
				if(!$thisParent.hasClass('form-field--error')) {
					if(!$(this).val().length || $(this).val().length == 0) {
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
				}
			});
		}, 100); // fixes bug in IE11 for prepopulated data
	};

	var PanelRightOverlay = function() {
		var instance = this;
		$('body.ie10').on("click", function(e){
			if($(this).hasClass("no-scroll")) {
				if($(e.target).find('.panel-right__overlay').length) {
					instance.CloseRightPanel();
				}
			}
		});
		$('.panel-right__overlay').on("click", function() {
			instance.CloseRightPanel();
		});
	};

	PanelRightOverlay.prototype.CloseRightPanel = function() {
			if($('.js-account-container').hasClass('is-showing')) {
				if(!$('.actions-group__content').is(':visible')) {
					$('.actions-group').trigger("click");
				}
				else {
					$('.js-link-log-in-container .js-link-log-in').trigger("click");
				}
			}
			else if($('.js-search-container').hasClass('is-showing')) {
				$('.js-open-search').click();
			}
			$('html, body').removeClass('no-scroll');
	};

	var PanelRightMisplacedPassword = function() {
		$('.panel-right .js-mock-show-reset-password').on("click", function(e){
			$('.panel-right').addClass('show-reset-password-form').addClass('will-show');
			setTimeout(function(){
				$('.panel-right .form--login').css({"visibility":"hidden","position":"absolute"});
				$('.panel-right .form--password-reset').css({"visibility":"visible","position":"relative"});
				$('.panel-right').removeClass('will-show');
				if($('.ie8').length > 0) {
					$('.panel-right .form--login').css("display","none");
					$('.panel-right .form--password-reset').css("display","block");
				}
			}, 150);
		});

		$('.panel-right .js-mock-send-reset-password').on("click", function(e){
			e.preventDefault();
			var $this = $(this);
			$this.attr('value', 'Email is on the way').addClass('ref-button--success');
			$('.panel-right').addClass('reset-password-is-submitted').find('.form-submitted-success').addClass('is-showing');
		});

		$('.panel-right .js-link-to-login').on("click", function(e){
			e.preventDefault();
			$('.panel-right').removeClass('show-reset-password-form').removeClass('reset-password-is-submitted').find('.form-submitted-success').removeClass('is-showing')
			$('.panel-right .form--login').css({"visibility":"visible","position":"relative"});
			$('.panel-right .form--password-reset').css({"visibility":"hidden","position":"absolute"});
			$('.panel-right .js-mock-send-reset-password').removeClass('ref-button--success').attr('value', 'Send password reset email');
			if($('.ie8').length > 0) {
				$('.panel-right .form--login').css("display","block");
				$('.panel-right .form--password-reset').css("display","none");
			}
		});
	}

	var AccountContainer = function() {
		$('.actions-group').on("click", function() {
			if(!$('.actions-group__content').is(':visible')) {
				if($('.js-search-container').hasClass('is-showing')) {
					$('.js-open-search').click();
				}
				$('.js-account-container').toggleClass('is-showing');
				$('.actions-group').toggleClass('is-on');
				$('html, body').toggleClass('no-scroll');
			}
		});
		$('.js-link-log-in').on("click", function(e) {
			e.preventDefault();
			var $this = $(this);
			if($this.hasClass('is-selected')) {
				$this.removeClass('is-selected');
			} else {
				$this.addClass('is-selected');
			}
			if($('.js-account-container').hasClass('is-showing')) {
				$('.js-account-container').removeClass('is-showing');
				$('html, body').removeClass('no-scroll');
				if($('.no-touch').length) {
					$('.js-account-container').addClass('is-animating-out');
					window.setTimeout(function(){
						$('.js-account-container').removeClass('is-animating-out');
					}, 180);
				}
			}
			else {
				if($('.js-search-container').hasClass('is-showing')) {
					$('.js-open-search').click();
				}
				$('.js-account-container').addClass('is-showing');
				$('html, body').addClass('no-scroll');
				if($('.no-touch').length) {
					$('.js-account-container').addClass('is-animating-in');
					window.setTimeout(function(){
						$('.js-account-container').removeClass('is-animating-in');
					}, 240);
				}
			}
		});
	};

	var SearchContainer = function() {
		var pInstance = this;
		instance.searchOpenLink = $('.js-open-search');
		if(instance.searchOpenLink.length) {
			instance.searchOpenLink.click(function(e){
				e.preventDefault();
				$(this).toggleClass('is-selected');
				pInstance.toggleSearchView();
			});
		}

		this.initSearch();
	};

	SearchContainer.prototype.toggleSearchView = function() {
		if($('.js-search-container').hasClass('is-showing')) {
			$('.js-search-container').removeClass('is-showing');
			$('html, body').removeClass('no-scroll');
			if($('.no-touch').length) {
				$('.js-search-container').addClass('is-animating-out');
				window.setTimeout(function(){
					$('.js-search-container').removeClass('is-animating-out');
				}, 180);
			}
		}
		else {
			if($('.js-account-container').hasClass('is-showing')) {
				if(!$('.actions-group__content').is(':visible')) {
					$('.actions-group').trigger("click");
				}
				else {
					$('.js-link-log-in-container .js-link-log-in').trigger("click");
				}
			}
			$('.js-search-container').addClass('is-showing');
			$('html, body').addClass('no-scroll');
			$('.search__form .js-get-items').select();
			if($('.no-touch').length) {
				$('.js-search-container').addClass('is-animating-in');
				window.setTimeout(function(){
					$('.js-search-container').removeClass('is-animating-in');
				}, 240);
			}
		}
	};

	SearchContainer.prototype.initSearch = function() {
		// get mock data from file - this will contain results when implemented so shouldn't need JS regex below
		var data;
		$.ajax({
        url: "js/search-data.json",
        async: true,
        dataType: "json",
        success: function (items){
          data = items;
        }
    });
		
		var inputValue,
				$appsContainer = $('.js-item-results--apps'),
				$devListContainer = $('.js-item-results--developers'),
				$noResultsContainer = $('.js-no-results'),
				$appsList = $appsContainer.find('ul'),
				$devList = $devListContainer.find('ul');

		// on key up loop through object and search - for implentation, amend to call service to return results in json and display
		$('.js-get-items').keyup(function(){
			searchResultsApps = [];
			searchResultsDevs = [];
			inputValueCaseInsensitiveRegEx = new RegExp($(this).val(), "i");

			var $searchButtonMobile = $('.panel-right .form-field .search-button-mobile');
			if($(this).val().length > 0) {
				$searchButtonMobile.addClass('is-highlighted');
			} else {
				$searchButtonMobile.removeClass('is-highlighted');
			}

			// if found add to result array
			for(var i = 0; i < data.items.length; i++) {
				if(data.items[i].name.search(inputValueCaseInsensitiveRegEx) > -1) {
					searchResultsApps.push(data.items[i]);
				}
				if(data.items[i].creatorName.search(inputValueCaseInsensitiveRegEx) > -1) {
					searchResultsDevs.push(data.items[i]);
				}
			}

			// show and hide containers for nil results
			if (searchResultsApps.length == 0) {			
				$appsContainer.hide();
			} else {
				$appsContainer.show();
				$noResultsContainer.hide();
			}

			if(searchResultsDevs.length == 0) { 
				$devListContainer.hide();
			} else {
				$devListContainer.show();
				$noResultsContainer.hide();
			}

			if(searchResultsApps.length == 0 && searchResultsDevs.length == 0) {
				$noResultsContainer.show();
			}
			
			// output results to screen
			$appsList.empty();
			for(var i = 0; i < searchResultsApps.length; i++) {
				$appsList.append($('<li>').append($('<a>').append($('<img>').attr("src", "" + searchResultsApps[i].smallImage + "")).append($('<span>').text(searchResultsApps[i].name))));
			}

			$devList.empty();
			for(var i = 0; i < searchResultsDevs.length; i++) {
				$devList.append($('<li>').append($('<a>').append($('<span>').text(searchResultsDevs[i].creatorName))));
			}
		});
	};

	var Tabs = function() {
		var isIE8 = $('.ie8').length;
		if(!isIE8) {
			$('.default-tabs-transition .tabs__content-area').css("opacity", 1);
		}

		$('.js-tab-select').unbind("mouseup");
		$('.js-tab-select').on("mouseup", function(e){
  		e.preventDefault();
  		var $this = $(this);
  		var thisParent = $this.parents(".tabs-container");
  		thisParent.find('.is-active').removeClass('is-active');
  		$this.addClass('is-active');
  		var contentId = $this.find('.tabs__link').data("content");
  		$(contentId).parents('.tabs__content-container').find('.tabs__content--is-showing').removeClass('tabs__content--is-showing');
  		$(contentId).addClass('tabs__content--is-showing');
  		if(!isIE8 && $(contentId).parents('.tabs__content-container').hasClass('default-tabs-transition')) {
	  		$(contentId + ' > div').css("opacity", 0);
	  		$(contentId + ' > div').animate({opacity: 1}, 200);
	  	}
  	});

  	if($(window).width() < 720) {
  		// wait for LHS panel to move in before calculating height with setTimout
  		setTimeout(function(){
  			$('.collapsible-content').each(function(){
  				if(!$(this).parents('.tabs__content--is-showing').length) {
	  				contentHeight = $(this).height();
	  				$(this).css('margin-top', -contentHeight);
	  			}
	  		});
	  		$('.collapsible-trigger').click(function(){
	  			var $this = $(this);
	  			if($this.parents('.tabs__content--is-showing').length) {
	  				$this.parents('.tabs__content--is-showing').removeClass('tabs__content--is-showing');
	  				contentHeight = $this.next('.collapsible-content').height();
	  				$this.next('.collapsible-content').css('margin-top', -contentHeight);
	  			} else {
	  				$this.parents('.tabs__content-container').find('.tabs__content--is-showing').removeClass('tabs__content--is-showing');
		  			$this.parents('.tabs__content').addClass('tabs__content--is-showing');
		  			$('.collapsible-content').each(function(){
			  			if(!$(this).parents('.tabs__content--is-showing').length) { 
			  				contentHeight = $(this).height();
			  				$(this).css('margin-top', -contentHeight);
			  			} else {
			  				$(this).css('margin-top', 0);
			  			}
		  			});
		  			setTimeout(function(){
		  				var currentContentTop = $this.offset().top;
		  				$('html,body').animate({
			          scrollTop: currentContentTop - 70
			        }, 310);
		  			}, 300);
	  			}
	  		});
  		}, 500);
  	}
	};

	var MockFormSelectMultipleWithSearch = function() {
		$('.js-field--country-select').each(function(){
			new FormSelectMultipleWithSearch($(this));
		});
	}

	var FormSelectMultipleWithSearch = function($element) {
		var instance = this,
				inputValue;

		instance.$moduleContainer = $element;
		instance.$selectedValueContainer = instance.$moduleContainer.find('.selected-value');
		instance.$currentSelectedItemsContainer = instance.$moduleContainer.find('.js-country-select--container');
		instance.$resultsContainer = instance.$moduleContainer.find('.js-suggested-results-container'),
		instance.$noResultsContainer = instance.$moduleContainer.find('.js-no-results'),
		instance.$appsList = instance.$resultsContainer.find('ul');
		instance.$closeButton = instance.$moduleContainer.find('.close-popup');
		instance.$removeAllButton = instance.$moduleContainer.find('.js-multiple-select__remove-all-selected');
		instance.$applyButton = instance.$moduleContainer.find('.js-multiple-select-apply');
		instance.$addAnotherButton = instance.$moduleContainer.find('.js-btn-add-another-country');
		instance.maxNumberAllowed = 5;

		$.ajax({
        url: "js/search-countries.json",
        async: true,
        dataType: "json",
        success: function (items){
          instance.mockData = items;
        }
    });

		instance.$moduleContainer.find('.js-country-search-form-field').each(function(){
			instance.initFormField($(this));
		});

		instance.initPopupDisplay();
		instance.initAddAnotherButton();
		instance.initRemoveAll();
		instance.initApply();
		instance.multipleSelectWasUpdated();
	}

	FormSelectMultipleWithSearch.prototype.initPopupDisplay = function() {
		var instance = this;
		if(!instance.$moduleContainer.hasClass('.form-field--select-disabled')) {
			instance.$moduleContainer.find('.selected-value').on('click', function() {
				if(instance.$moduleContainer.hasClass('is-open')) {
					instance.$moduleContainer.removeClass('is-open');
				}
				else {
					instance.$moduleContainer.addClass('is-open');
				}
			});
			instance.$moduleContainer.find(".close-popup, .page-overlay").on("click", function(e){
				e.preventDefault();
				instance.$moduleContainer.removeClass('is-open');
				if($(window).width() < 720) {
					$('html.touch body, html.touch').removeClass('no-scroll');
				}
			});
		}
	}	

	FormSelectMultipleWithSearch.prototype.initFormField = function($element) {
		var instance = this;

		var $thisFormField = $element,
				$thisInputBox = $thisFormField.find('input[type=text]');

		$thisFormField.find('.remove-form-field').on("click", function(){
			var allFormFields = instance.$currentSelectedItemsContainer.find('.form-field'),
			$firstFormField = instance.$currentSelectedItemsContainer.find('.form-field:first-child');
			if(allFormFields.length > 1) {
				var allFieldsHaveAcceptedValue = true;
				if($thisFormField.is($firstFormField)) { // If first form field remove clicked, make sure another form field is available and has an accepted value
					allFormFields.each(function(){
						if(!$(this).hasClass("js-has-accepted-value")) {
							allFieldsHaveAcceptedValue = false;
						}
					});
					if(allFieldsHaveAcceptedValue) {
						$thisFormField.remove();
					}	
				} else {
					$thisFormField.remove(); // not the first form field so can be removed regardless of whether it has content or not
				}
				instance.$resultsContainer.hide();
				instance.$noResultsContainer.hide();
				instance.multipleSelectWasUpdated();
				instance.$applyButton.removeAttr("disabled");
			}			
		});

		$thisInputBox.on("focus", function(){
			$thisInputBox.select();
			instance.showPopularResults($thisFormField);
		});

		$thisFormField.currentSelectedValue = $thisInputBox.val();
		$thisFormField.currentFlag = $thisFormField.find('svg');
		// on key up loop through object and search - for implementation, amend to call service to return results in json and display
		$thisInputBox.keyup(function(e){			
			var currentInput = $(this);
			var searchResults = [];
			var inputValueCaseInsensitiveRegEx = new RegExp($(this).val(), "i");

			if(e.keyCode == "13") { // user pressed Enter
				if(currentInput.val().length > 0) {
					instance.$appsList.find('li:first-child a').trigger("click");
					$thisInputBox.blur();
				}
			} else {
				$thisFormField.currentFlag.hide()
				$thisFormField.removeClass("js-has-accepted-value");

				if(currentInput.val().length == 0) {
					$thisFormField.find('svg').hide();
				}

				if($thisFormField.hasClass("form-field--alert")) {
					$thisFormField.removeClass("form-field--alert");
				}
				
				// get search results
				searchResults = instance.mockSearch(inputValueCaseInsensitiveRegEx);

				// show and hide containers for nil results
				if (searchResults.length == 0) {			
					instance.$resultsContainer.hide();
				} else {
					instance.positionSearchResultsContainer($thisFormField);
					instance.$resultsContainer.show();
					instance.$noResultsContainer.hide();
				}

				if(searchResults.length == 0) {
					instance.$noResultsContainer.show();
				}
				
				instance.populateSearchResults($thisFormField, searchResults);
			}

		});

		$thisInputBox.blur(function(e){
			setTimeout(function(){
				if(!$thisFormField.hasClass("js-has-accepted-value")) { // add previous value before false edit
					$thisInputBox.val($thisFormField.currentSelectedValue);
					if($thisFormField.currentSelectedValue != 0) {
						$thisFormField.currentFlag.show();
						$thisFormField.addClass("js-has-accepted-value");
					}
				}
				var isFocusOnInput = false;
				instance.$currentSelectedItemsContainer.find('.form-field input').each(function(){
					if($(this).is(":focus")) {
						isFocusOnInput = true;
					}
				});

				if(!isFocusOnInput) {
					instance.$resultsContainer.hide();
					instance.$noResultsContainer.hide();
				}
			}, 200);
		});
	}

	FormSelectMultipleWithSearch.prototype.mockSearch = function(inputValueCaseInsensitiveRegEx) {
		var instance = this,
				searchResults = [];
		for(var i = 0; i < instance.mockData.items.length; i++) {
			if(instance.mockData.items[i].name.search(inputValueCaseInsensitiveRegEx) > -1) {
				searchResults.push(instance.mockData.items[i]);
			}
		}

		return searchResults;
	}

	FormSelectMultipleWithSearch.prototype.populateSearchResults = function($thisFormField, searchResults) {
		var instance = this,
				$thisInputBox = $thisFormField.find("input[type=text]");
		instance.$appsList.empty();
		for(var i = 0; i < searchResults.length; i++) {

			// see if chosen country is already selected
			var resultId = searchResults[i].id,
					countryAlreadySelected = false,
					countryAlreadySelectedFormField;

			instance.$currentSelectedItemsContainer.find('.form-field').each(function(){
				if($(this).attr("data-id") == resultId) {
					countryAlreadySelected = true;
				}
			});

			var newLink = $('<a>').html(searchResults[i].flag)
														.append($('<span>').text(searchResults[i].name).attr("data-id", searchResults[i].id))
														.on("click", function(){ // on click add value to currently edited input
															var selectedSpan = $(this).find('span'),
																	selectedId = selectedSpan.data("id");

															if(!$(this).hasClass("is-selected")) {
																$thisFormField.attr("data-id", selectedId);
																if(instance.$resultsContainer.is(":visible")) {
																	$thisFormField.find('svg').remove();
																}
																$thisInputBox.val(selectedSpan.text());
																$thisFormField.append($(this).find('svg'));
																$thisFormField.addClass("js-has-accepted-value js-country-search-form-field");
																instance.$resultsContainer.hide();
																$thisFormField.currentSelectedValue = $thisInputBox.val();
																$thisFormField.currentFlag = $thisFormField.find('svg');
																$thisFormField.id = selectedId;
																instance.multipleSelectWasUpdated();
																instance.$applyButton.removeAttr("disabled");
															}
														});

			if(countryAlreadySelected) {
				newLink.addClass("is-selected");
			}

			instance.$appsList.append($('<li>').append(newLink));					
		}
	}

	FormSelectMultipleWithSearch.prototype.multipleSelectWasUpdated = function() {
		var instance = this;
		instance.setNumberSelected();
		instance.setRemoveAllButton();
		instance.setAddAnotherButton();
		instance.setFirstItemRemoveVisibility();
	}

	FormSelectMultipleWithSearch.prototype.initAddAnotherButton = function () {
		var instance = this;
		instance.$addAnotherButton.on("click", function(e){
			e.preventDefault();
			var currentSelectedItems = instance.$currentSelectedItemsContainer.find('.form-field'),
			allInputsHaveValues = true,
			$emptyInput;

			currentSelectedItems.each(function(){
				if(!$(this).hasClass("js-has-accepted-value")) {
					allInputsHaveValues = false;
					$emptyInput = $(this).find('input');
				}
			});

			if(allInputsHaveValues && currentSelectedItems.length < instance.maxNumberAllowed) {
				// add another input select option
				var newInput = $('<input>').attr("type", "text").attr("id", "country" + currentSelectedItems.length).attr("name", "country" + currentSelectedItems.length).attr("autocomplete", "off");
				var newFormField = $('<div>').addClass("form-field is-closed")						
							.append(newInput)
							.append($('<label>').attr("for", "country" + currentSelectedItems.length).text("Add Another Country"));
				var newRemoveButton = $('<a>').addClass("remove-form-field")
																			.text("K");
																			
				newFormField.append(newRemoveButton);
				setTimeout(function() { newInput.focus(); }, 100);
				instance.$currentSelectedItemsContainer.append(newFormField);
				instance.initFormField(newFormField);
			}
		});
	}

	FormSelectMultipleWithSearch.prototype.initRemoveAll = function() {
		var instance = this;
		instance.$removeAllButton.on("click", function(){
			instance.$currentSelectedItemsContainer.find('.form-field:not(:first-child)').each(function(){
				$(this).find('.remove-form-field').trigger("click");
			});
		});
	}

	FormSelectMultipleWithSearch.prototype.setNumberSelected = function() {
		var instance = this,
				numberSelected = instance.$currentSelectedItemsContainer.find('.form-field').length;
		instance.$moduleContainer.find('.js-multiple-select__number-selected').text(numberSelected + " / " + instance.maxNumberAllowed);
	}

	FormSelectMultipleWithSearch.prototype.setRemoveAllButton = function() {
		var instance = this;
		if(instance.$currentSelectedItemsContainer.find('.form-field').length > 1) {
			instance.$removeAllButton.removeClass("is-disabled");
		} else {
			instance.$removeAllButton.addClass("is-disabled");
		}
	}

	FormSelectMultipleWithSearch.prototype.setFirstItemRemoveVisibility = function() {
		var instance = this,
				allFormFields = instance.$currentSelectedItemsContainer.find('.form-field');

		if(allFormFields.length > 1) {
			allFormFields.first().find('.remove-form-field').show();
		} else {
			allFormFields.first().find('.remove-form-field').hide();
		}
	}

	FormSelectMultipleWithSearch.prototype.setAddAnotherButton = function() {
		var instance = this;
		if(instance.$currentSelectedItemsContainer.find('.form-field').length == instance.maxNumberAllowed) {
			instance.$addAnotherButton.hide();
		} else {
			instance.$addAnotherButton.show();
		}
	}

	FormSelectMultipleWithSearch.prototype.initApply = function() {
		var instance = this;
		instance.$applyButton.on("click", function(e){
			e.preventDefault();
			instance.$closeButton.trigger("click");
			var formFields = instance.$currentSelectedItemsContainer.find('.js-has-accepted-value');
			if(formFields.length > 1) {
				instance.$selectedValueContainer.text(formFields.length + " Countries");
			} else {
				instance.$selectedValueContainer.text(formFields.first().find('input[type=text]').val());
			}
			instance.$applyButton.attr("disabled", "disabled");
			var formFieldsNotAccepted = instance.$currentSelectedItemsContainer.find('.form-field:not(.js-has-accepted-value)');
			formFieldsNotAccepted.remove();
		});
	}

	FormSelectMultipleWithSearch.prototype.showPopularResults = function($thisFormField) {
		var instance = this,
				inputValueCaseInsensitiveRegEx = new RegExp("", "i"),
				searchResults = instance.mockSearch(inputValueCaseInsensitiveRegEx);				

				// show and hide containers for nil results
				if (searchResults.length > 0) {
					instance.populateSearchResults($thisFormField, searchResults);
					instance.positionSearchResultsContainer($thisFormField);
					instance.$resultsContainer.show();
				}
	}

	FormSelectMultipleWithSearch.prototype.positionSearchResultsContainer = function($thisFormField) {
		var instance = this;
		var containerOffsetTop = instance.$currentSelectedItemsContainer.offset().top,
				formFieldOffsetTop = $thisFormField.offset().top;

		var topPosition = formFieldOffsetTop - containerOffsetTop + $thisFormField.innerHeight() + 1;
		instance.$resultsContainer.css("top", topPosition);
		instance.$noResultsContainer.css("top", topPosition);
	}


	var FormFieldSelect = function() {
		// List of checkboxes drop down
		var pInstance = this;
		$('.reflection-select').each(function(){
			var $this = $(this),
						selectedOptionsContainer = $this.find('.js-selected-values'),
						optionsList = $this.find('ul'),
						listItems = $this.find('li');

			optionsList.css('margin-top', -optionsList.height());
			pInstance.populateSelectedValues(listItems, selectedOptionsContainer);

			if($this.parent('.form-field--select-disabled').length == 0 && $this.parent('.form-field--select-restricted').length == 0) {
				selectedOptionsContainer.on('click', function() {
					if($this.hasClass('is-open')) {
						$this.removeClass('is-open');
						optionsList.css('margin-top', -optionsList.height());
					}
					else {
						$this.addClass('is-open');
						optionsList.css('margin-top', "9px");
					}	
				});
			}

			listItems.on("click", function(){
				pInstance.populateSelectedValues(listItems, selectedOptionsContainer);
			});
		});

		// List of select options drop down
		if(!$('.ie8').length) {
			$('.js-field--select').each(function() {
				var selectInput = $(this),
				selectOptions = selectInput.find('option'),
				optionsList = $('<ul>'),
				refSelectContainer = $('<div>').addClass('reflection-select'),
				refSelectDefault = $('<span>').addClass('selected-value').text('Choose your option'),
				isFilter = selectInput.hasClass('reflection-select--filter'),
				isCentered = selectInput.hasClass('reflection-select--center'),
				isRightAligned = selectInput.hasClass('reflection-select--right'),
				listContainer = $("<div>").addClass("list-container");
				if(isFilter) {
					optionsList.append($('<a>').addClass('close-popup').text('K').on("click", function(e){
						e.preventDefault();
						$('.reflection-select').removeClass('is-open');
						$('.form-field--select').removeClass('is-open');
						if($(window).width() < 720) {
							$('html.touch body, html.touch').removeClass('no-scroll');
						}							
					}));
					refSelectContainer.addClass('reflection-select--filter');
					var selectTitle = (selectInput.data("title")) ? selectInput.data("title") : $(selectOptions[0]).text();
					optionsList.append($('<span>').text(selectTitle)).append(listContainer);
				}
				if(isCentered) {
					refSelectContainer.addClass('reflection-select--center');
				}
				if(isRightAligned) {
					refSelectContainer.addClass('reflection-select--right');
				}
				selectOptions.each(function(){
					$this = $(this);
					if($this.attr('value')) {
						var preSelectedClass = '',
								selectedText = '';
						if($this.data("previous")) {
							preSelectedClass = 'pre-selected';
							selectedText = $this.data("selectedtext");
						}
						if(isFilter) {
							listContainer.append($('<li>').addClass(preSelectedClass).attr('data-value', $this.attr('value')).attr('data-selectedtext', selectedText).text($this.text()));
						} else {
							optionsList.append($('<li>').addClass(preSelectedClass).attr('data-value', $this.attr('value')).attr('data-selectedtext', selectedText).text($this.text()));
						}						
					}
					else {
						refSelectDefault.text($this.text());
					}
				});
				
				refSelectContainer.append(refSelectDefault).append(optionsList);
				selectInput.parents('.form-field--select').append(refSelectContainer);

				var listHeight = optionsList.innerHeight();
				if(!selectInput.hasClass('reflection-select--filter')) {
					optionsList.css('margin-top', -listHeight);
				}				

				if(selectInput.parent('.form-field--select-disabled').length == 0 && selectInput.parent('.form-field--select-restricted').length == 0) {
					optionsList.find('li').on('click', function() {
						if(!$(this).hasClass('pre-selected')) {
							listItem = $(this);
							optionsList.find('li').removeClass('is-selected');
							listItem.addClass('is-selected');
							refSelectDefault.text(listItem.text()).addClass('is-activated');

							selectOptions.each(function() {
								if($(this).val() == listItem.data("value")) {
									$(this).attr("selected", "selected");
								}
								else {
									$(this).removeAttr("selected");	
								}
							});

							if(selectInput.hasClass('reflection-select--filter')) {
								$('.reflection-select').removeClass('is-open');
							}
							toggleDropDown(refSelectContainer, optionsList, listHeight);
						}
					});
					
					refSelectDefault.on('click', function() {
						toggleDropDown(refSelectContainer, optionsList, listHeight);
					});
				}

				$(window).on("resize", function(){
					listHeight = optionsList.innerHeight();
					optionsList.css('margin-top', -listHeight);	
				});

				$(this).siblings('.page-overlay').on("click", function() {
					toggleDropDown(refSelectContainer, optionsList, listHeight);
				});
			});
		}
	};	

	FormFieldSelect.prototype.populateSelectedValues = function(listItems, selectedOptionsContainer) {
		var selectedValues = '';
		listItems.each(function(){
			var $this = $(this);
			if($this.find('input:checked').length > 0) {
				selectedValues += $this.find('.checkboxLabelVisible').text() + ', ';
			}
		});

		if(selectedValues.length > 0) {
			selectedValues = selectedValues.substring(0, selectedValues.length - 2);
			selectedOptionsContainer.text(selectedValues);
		}
		else {
			selectedOptionsContainer.text('Choose your option(s)');
		}
	};

	function toggleDropDown(refSelectContainer, optionsList, listHeight) {
		if(refSelectContainer.hasClass('is-open')) {
			refSelectContainer.removeClass('is-open');
			refSelectContainer.parents('.form-field--select').removeClass('is-open');
			optionsList.css('margin-top', -listHeight);
		}
		else {
			$('.reflection-select').removeClass('is-open');
			$('.form-field--select').removeClass('is-open');
			refSelectContainer.addClass('is-open');
			refSelectContainer.parents('.form-field--select').addClass('is-open');
			optionsList.css('margin-top', "9px");
			if($('.touch').length) {
				refSelectContainer.siblings('.js-field--select').focus();
			}
		}
		if($(window).width() < 720) {
			if(refSelectContainer.hasClass('reflection-select--filter') && refSelectContainer.hasClass('is-open')) {
				$('html.touch body, html.touch').addClass('no-scroll');
			} else {
				$('html.touch body, html.touch').removeClass('no-scroll');
			}
		}
	};

	var BackToTop = function() {
		if($('.MHXTE6C-ob-c').length > 0) {
			$('.MHXTE6C-ob-c').on("click", function(e){
				e.preventDefault();
				$('html, body').animate({ scrollTop: 0 }, 300, 'swing');
			});
		}

		$(window).scroll(function(){
			var scrollTop = $(window).scrollTop();
			var windowHeight = $('body').height();
			if(scrollTop > 500) {
				$('.MHXTE6C-ob-c').addClass('is-showing');
			}
			else {
				$('.MHXTE6C-ob-c').removeClass('is-showing');
			}
		});
	};

	var BackToTopInlineLink = function($link) {
		if($link) {
			$link.on("click", function(){
				var linkAnchor = $link.attr("href");
				if(linkAnchor) {
					var pageTopBarHeight = $('.global-header').innerHeight(),
							anchorId = $link.attr("href"),
							scrollTopOfTheAnchor = $(anchorId).offset().top;
					$('html, body').animate({ scrollTop: scrollTopOfTheAnchor - pageTopBarHeight - 20}, 300, 'swing');
					
				} else {
					$('html, body').animate({ scrollTop: 0 }, 300, 'swing');
				}
			});
		}
	}


	var Accordion = function() {
		that = this;
		this.calculateAccordionHeights();
		$(window).on("resize", function() {			
			that.calculateAccordionHeights();
		});		
	};

	Accordion.prototype.calculateAccordionHeights = function() {
		$('.accordion').each(function(){
			$this = $(this);
			$this.find('> ul > li').addClass('is-closed');
			$this.find('.accordion-content').each(function(){
				$contentContainer = $(this);
				var accordionHeight = $contentContainer.innerHeight();
				$contentContainer.css('margin-top', -accordionHeight);
			});
			$this.find('.accordion-switch').unbind("click");
			$this.find('.accordion-switch').on("click", function(){
				var accordionSwitch = $(this), accordionSwitchParent = accordionSwitch.parent('li');
				if(!accordionSwitch.hasClass('no-accordion-content')) {
					if(accordionSwitchParent.hasClass('is-closed')) {
						accordionSwitchParent.removeClass('is-closed');
						accordionSwitch.next('.accordion-content').css('margin-top', "0");
					}
					else {
						var accordionContent = accordionSwitch.next('.accordion-content');
						accordionSwitchParent.addClass('is-closed');
						accordionContent.css('margin-top', -accordionContent.innerHeight());
					}
				}				
			});
		});
	};

	var ReadMore = function() {
		$('.more-content').hide();
		$('.js-more-link').on("click", function(e){
			e.preventDefault();
			var $this = $(this), $thisLink = $this.find('a');
			$this.prev('.more-content').slideToggle(150);
			if($thisLink.text() == "Read more") {
				$thisLink.text("Read less");
			}
			else {
				$thisLink.text("Read more");
			}
		});
	};

	var TabsToMobileDropDown = function() {
		var instance = this;
		this.switchedFromLarge = false;
		this.switchedFromSmall = false;
  	$(window).load(instance.updateTabs());
  	$(window).on("redraw",function(){
  		instance.switched = false;
  		instance.updateTabs();
  	});
  	$(window).on("resize", function(){
  		instance.updateTabs();
  	});
  };

  TabsToMobileDropDown.prototype.updateTabs = function() {
  	var instance = this;
    if (($(window).width() < 720) && !instance.switchedFromLarge) {
      instance.switchedFromLarge = true;
      instance.switchedFromSmall = false;
      $(".tabs-to-dropdown").each(function(i, element) {
        instance.turnTabsToDropDown($(element));
      });
    }
    else if (!instance.switchedFromSmall && ($(window).width() > 720)) {
      instance.switchedFromSmall = true;
      instance.switchedFromLarge = false;
      $(".tabs-to-dropdown").each(function(i, element) {
        instance.unsplitTabsToMobileDropDown($(element));
      });
    }
  };
	
	TabsToMobileDropDown.prototype.turnTabsToDropDown = function(original) {
		var activeElement = $('<span>');
	
		original.find('.is-active .tabs__link').each(function(){
			$this = $(this);
			activeElement.html($this.find('span').html()).addClass('ref-icon-after ref-icon-after--angle-down');
		});
		$container = original.parent('div').addClass('tabs-to-dropdown-container');
		original.hide();
		activeElement.insertBefore(original);

		activeElement.on("click", function(){
			original.slideToggle(300);
			activeElement.toggleClass("is-open");
		});

		original.find(".js-tab-select").on("click", function(){
			original.slideUp(200);
			activeElement.html($(this).find('span').html());
			activeElement.removeClass("is-open");
		});

		var isIE8 = $('.ie8').length;
		if(!isIE8) {
			$('.default-tabs-transition .tabs__content-area').css("opacity", 1);
		}
	};

	TabsToMobileDropDown.prototype.unsplitTabsToMobileDropDown = function(element) {
		$('.tabs-to-dropdown-container').find('ul.tabs-to-dropdown').css("display", "block");
		$('.tabs-to-dropdown-container').removeClass('tabs-to-dropdown-container').find('> span').remove();
		new Tabs();
	}

	var RevealContent = function() {
		$('.js-reveal-element').on("click", function(e){
			e.preventDefault();
			var $this = $(this),
			openText = $this.data('open-text'),
			closedText = $this.data('closed-text');
			$this.toggleClass('is-open');
			$this.next('.reveal-element').slideToggle(150);
			if($this.text() == closedText) {
				$this.text($this.data('open-text'));
			}
			else {
				$this.text($this.data('closed-text'));
			}			
		});
	};

	var ToolTip = function() {
		var instance = this;
		$('.touch body').on("click", function(e){ // remove all tooltips on body touch
			if($('.tooltip').length) {			
				$('.tooltip').remove();
			}
		});

		$('.js-tooltip').each(function(){
			var $this = $(this);
			var tooltip;
			if($('html.no-touch').length) {
				$this.on("mouseenter", function(){
					tooltip = instance.generateTooltip($this, false);
				});
				$this.on("mouseleave", function(){
					tooltip.remove();
				});
				$this.on("click", function(){
					tooltip.remove();
				});
			} else if($('html.touch').length) {
				$this.on("click", function(e){
					if($this.attr("href") != undefined) {
						e.preventDefault();
					}
					if($this.hasClass("js-tooltip-generated")) {
						tooltip.remove();
						$this.removeClass("js-tooltip-generated");
					} else {						
						$this.addClass("js-tooltip-generated");
						setTimeout(function() { 
							tooltip = instance.generateTooltip($this, true);
						}, 50); // delay to avoid all tooltip removal on body touch
					}					
				});
			}
		});

		$('.js-whats-this-tooltip').on("click", function(e){
			e.preventDefault();
			var $this = $(this);
			if(!($this.hasClass('is-open'))) {
				$this.addClass('is-open');
				var topPosition = $this.offset().top;
				var leftPosition = $this.offset().left;
				var tooltipContainer = $('<div>').addClass("whats-this-tooltip-popup");
				if($this.hasClass('whats-this-tooltip--dark')) {
					tooltipContainer.addClass("whats-this-tooltip--dark");
				}
				var tooltip = $('<div>').addClass("whats-this-tooltip");
				tooltip.append($('<h2>').text("What's This?"));
				tooltip.append($('<p>').html($this.data("whatsthis")));
				tooltip.append($('<img>').attr("src", "images/icon-bulb.png").attr("alt", "Bulb icon"));
				tooltipContainer.append(tooltip);
				$('body').append(tooltipContainer);
				var tooltipWidth = tooltip.innerWidth();
				var tooltipHeight = tooltip.innerHeight();
				var iconOffset = 10;
				if($(window).width() > 480) {
					if($this.hasClass('position-tooltip-left')) {
						tooltip.addClass("position-tooltip-left");
						tooltipContainer.css({"top": topPosition - (tooltipHeight/2) + iconOffset, "left": leftPosition - tooltipWidth - 20});
					} else if($this.hasClass('position-tooltip-right')) {
						tooltip.addClass("position-tooltip-right");
						tooltipContainer.css({"top": topPosition - (tooltipHeight/2) + iconOffset, "left": leftPosition + 38});
					} else if($this.hasClass('position-tooltip-top')) {
						tooltip.addClass("position-tooltip-top");
						tooltipContainer.css({"top": topPosition - 20 - tooltipHeight, "left": leftPosition - (tooltipWidth/2) + iconOffset});
					} else {
						tooltipContainer.css({"top": topPosition + 40, "left": leftPosition - (tooltipWidth/2) + 8});
					}
				} else {
					tooltipContainer.css({"top": topPosition + 38, "left": "50%", "margin-left": -(tooltipWidth/2)});
				}
				
				setTimeout(function(){
					tooltip.addClass("is-open");
				}, 10);				
				$('body').on("click", function(e){
					if(!($(e.target).is($this))) {
						tooltipContainer.remove();
						$this.removeClass('is-open');
					}
				});				
			} else {
				$('.whats-this-tooltip-popup').remove();
				$this.removeClass('is-open');
			}
		});
		
		$(window).on("resize", function(){
			$('.whats-this-tooltip-popup').remove();
			$('.js-whats-this-tooltip.is-open').removeClass('is-open');
		});
	}

	ToolTip.prototype.generateTooltip = function($tooltipParent, isTouchTooltip) {
		var $this = $tooltipParent,
				tooltipText = $tooltipParent.data("tooltip");

		var tooltip = $('<div>').addClass("tooltip").append($('<div>').addClass("tooltip-text").text(tooltipText));
		if($this.find('.icon-member--standard').length > 0) {
			tooltip.prepend($('<span>').addClass("tooltip-feature tooltip-feature--standard").text("MEMBER FEATURE"));
		} else if($this.find('.icon-member--pro').length > 0) {
			tooltip.prepend($('<span>').addClass("tooltip-feature tooltip-feature--pro").text("PREMIUM FEATURE"));
		}
		$('body').append(tooltip);
		var topPosition = $this.offset().top;
		var leftPosition = $this.offset().left;
		var tooltipHeight = tooltip.innerHeight();
		var componentHeight = $this.innerHeight();						
		tooltip.hide();
		if($this.hasClass('js-tooltip--right')) {
			var tooltipWidth = tooltip.innerWidth();
			var componentWidth = $this.innerWidth();	
			if($this.hasClass('js-tooltip--right--no-pointer-padding')) {
				leftPosition = (leftPosition + componentWidth - tooltipWidth) + 10;
			} else {
				leftPosition = leftPosition + componentWidth - tooltipWidth;
			}
			tooltip.addClass("tooltip-right");
		}
		tooltip.css({"top": topPosition - tooltipHeight - 20, "left": leftPosition});
		if(isTouchTooltip) {
			tooltip.show();
		} else {
			setTimeout(function(){
				tooltip.fadeIn(100);
			}, 400);
		}

		return tooltip;
	}

	var LoadingMessageBoxMessages = [{
		message: "<h2>Did you know...</h2><p>The app market is currently worth $40.5 Billion annually (2015)</p><img alt=\"Bulb icon\" src=\"images/icon-bulb.png\">"
	},
	{
		message: "<h2>Did you know...</h2><p>There are currently 5.5 Million developers making apps (2015)</p><img alt=\"Bulb icon\" src=\"images/icon-bulb.png\">"
	},
	{
		message: "<h2>Did you know...</h2><p>More than 3.7 Million apps are available across all stores today (2015)</p><img alt=\"Bulb icon\" src=\"images/icon-bulb.png\">"
	},
	{
		message: "<h2>Did you know...</h2><p>Over 2000 apps are released every day (2015)</p><img alt=\"Bulb icon\" src=\"images/icon-bulb.png\">"
	},
	{
		message: "<h2>Did you know...</h2><p>52% of app developers make less than $1,000 per month (2015)</p><img alt=\"Bulb icon\" src=\"images/icon-bulb.png\">"
	},
	{
		message: "<h2>Did you know...</h2><p>17% of app developers make absolutely nothing :( (2015)</p><img alt=\"Bulb icon\" src=\"images/icon-bulb.png\">"
	},
	{
		message: "<h2>Did you know...</h2><p>Mobile games revenue ($30.3 Billion) eclipsed console games ($26.4 Billion) for first time in 2015</p><img alt=\"Bulb icon\" src=\"images/icon-bulb.png\">"
	},
	{
		message: "<h2>Did you know...</h2><p>Only 5% of developers make over $500,000 a month (2015)</p><img alt=\"Bulb icon\" src=\"images/icon-bulb.png\">"
	},
	{
		message: "<h2>Did you know...</h2><p>Only 10% of developers make over $100,000 a month (2015)</p><img alt=\"Bulb icon\" src=\"images/icon-bulb.png\">"
	}];

	var LoadingMessageBox = function() {
		var popupToastContainer = $("<div>").addClass("loading-message-box-container");
		var firstMessageObject = LoadingMessageBoxMessages[0];
		var popupToast = $("<div>").addClass("loading-message-box").html(firstMessageObject.message);
		LoadingMessageBoxMessages.shift();
		LoadingMessageBoxMessages.push(firstMessageObject);
		popupToastContainer.append(popupToast);
		$('body').append(popupToastContainer);
		popupToastContainer.css("z-index", 900);
		setTimeout(function(){
			popupToast.addClass("is-open");
		}, 500);
		
		setTimeout(function(){
			popupToast.removeClass("is-open");
			setTimeout(function(){
				popupToastContainer.remove();
			}, 1000);
		}, 5000);

		popupToastContainer.on("click", function(){
			popupToast.removeClass("is-open");
		});
	}

	var StickyTableHead = function() {
		$('.sticky-table-head:visible').each(function(){
			var $this = $(this);
			var headerHeight = $('.global-header').innerHeight();
			var dataTableTopPosition = $this.siblings('table').offset().top - headerHeight;
			
			$(window).on("scroll", function(){
				if($(window).scrollTop() >= dataTableTopPosition) {
					$this.css({"visibility": "visible", "opacity": 1});
				} else {
					$this.css({"visibility": "hidden", "opacity": 0});
				}
			});
		});
	}

	var reflectionMap = function() {	
		var isDraggable = $('html.touch').length == 0;
		var mapStyles = [{
	      featureType: "poi",
	      elementType: "labels",
	      stylers: [
          { visibility: "off" }
	      ]
	    }];
	  var zoomValue = 17;
	  if($(window).innerWidth() < 720) {
	  	zoomValue = 16;
	  }
		this.myLatlng = new google.maps.LatLng(51.518680, -0.136578);
		this.mapOptions = {
		  zoom: zoomValue,
		  zoomControl: true,
		  center: this.myLatlng,
		  disableDefaultUI: true,
		  scrollwheel: false,
		  streetViewControl: true,
		  draggable: isDraggable,
		  styles: mapStyles
		}
		this.markerImage = {
	    url: 'images/map-marker.png',
	    size: new google.maps.Size(34, 49),
	    scaledSize: new google.maps.Size(34, 49),
	    anchor: new google.maps.Point(0, 53)
	  };
		this.map = new google.maps.Map(document.getElementById("js-map--contact"), this.mapOptions);	

		var marker = this.marker = new google.maps.Marker({
	    position: this.myLatlng,
	    map: this.map,
	    title: "40-44 Newman Street",
	    icon: this.markerImage,
	    // animation: google.maps.Animation.DROP
		});

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

	var profileListContainer = function($profileListContainer) {
		var $this = $profileListContainer;
		$profileListContainer.find('.js-open-profile').on("click", function(){
			var openProfile = $this.find("li.is-active").addClass("is-closing");
			setTimeout(function(){
				openProfile.removeClass("is-active is-closing");
			}, 200);
		});
	}

	var profileList = function($profileContainer) {
		var $this = $profileContainer;	

		$this.find('.js-open-profile').on("click", function(e){
			e.preventDefault();
			if($this.hasClass("is-active")) {
				$this.addClass("is-closing");
				setTimeout(function(){
					$this.removeClass("is-active is-closing");
				}, 200);
			} else {
				setTimeout(function(){
					$this.addClass("is-active");
				}, 10);
			}		
		});
		$this.find('.js-close-profile').on("click", function(e){
			e.preventDefault();
			$this.addClass("is-closing");
			setTimeout(function(){
				$this.removeClass("is-active is-closing");
			}, 150);
		});
	}

	var FAQSet = function($faqContainer) {
		$faqContainer.removeClass("faqs-list-container--fixed");
		$faqContainer.find('li a').each(function(){
			var $thisLink = $(this);
			$thisLink.on("click", function(e){
				e.preventDefault();
				$faqContainer.find('li a').removeClass("is-active");
				$thisLink.addClass("is-active");
				var pageTopBarHeight = $('.global-header').innerHeight(),
						anchorId = $(this).attr("href"),
						scrollTopOfTheAnchor = $(anchorId).offset().top;
				$('html, body').animate({ scrollTop: scrollTopOfTheAnchor - pageTopBarHeight - 20}, 300, 'swing');
			});
		});
		
		var pageTopBarHeight = $('.global-header').innerHeight();
		var faqContainerTopPosition = $faqContainer.offset().top - pageTopBarHeight;
		var $window = $(window);

		if($("body").hasClass("no-touch")) {
			$window.on("scroll", function(){
				if($window.innerWidth() > 719 && $window.innerHeight() > 799) {

					if($(window).scrollTop() >= faqContainerTopPosition) {
						if(!$faqContainer.hasClass("faqs-list-container--fixed")) {
							$faqContainer.addClass("faqs-list-container--fixed");
						}
					} else {					
						if($faqContainer.hasClass("faqs-list-container--fixed")) {
							$faqContainer.removeClass("faqs-list-container--fixed");
						}
					}
				}
			});
		}		
	}
/* END COMPONENT OBJECTS */

/* PAGE OBJECTS FOR TEMPLATES */

// LeaderboardPage object
	var LeaderboardPage = function() {
		new Page();

		// Components
		new Tabs();
		new TabsToMobileDropDown();
		new FormFieldSelect();
		new BackToTop();
		new StickyTableHead();
		new MockFormSelectMultipleWithSearch();
		new ToolTip();

		$('.js-tab-select').on("mouseup", function(e){
			new StickyTableHead();
		});

		if($('.toggle-view-state #compact-view').attr("checked") !== undefined) {
			$(".page-leaderboard").addClass("compact-view");
		}

		$('.toggle-view-state input').on("click", function(){
			$(".page-leaderboard").removeClass("list-view compact-view");
			$(".page-leaderboard").addClass($(this).attr("id"));
		});

		var resetFiltersButton = $('.js-reset-filters');

		resetFiltersButton.on("click", function(e){
			e.preventDefault();
			$(this).parents("form").find("select").each(function(){
				var popupList = $(this).siblings(".reflection-select").find("ul");
				popupList.find("li:first-child").trigger("click");
				popupList.find(".close-popup").trigger("click");
			});
			$(this).attr("disabled", "disabled");
		});
	}

// BlogPage object
	var BlogPage = function() {
		new Page();

		// Components
		new FormFieldSelect();
		new BackToTop();
	}

// LeaderboardPage object
	var AppPage = function() {
		new Page();

		// Components
		new Tabs();
		new TabsToMobileDropDown();
		new FormFieldSelect();
		new RevealContent();
	}

	var AccountSettingsPage = function() {
		new Page();

		// Components
		new Tabs();
		new TabsToMobileDropDown();
		new ToolTip();
	}