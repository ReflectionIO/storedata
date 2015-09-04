// APPLICATION JAVASCRIPT
// Use prototypal inheritance to set functions relevant to the page to encapsulate functions (page objects below)
// Use functions as variables to modularise and encapsulate component functionality in application.js - which contains reusable components JS for the application
// Keep design kit only JS in this file, and resusable application JS in application.js

/* COMPONENT OBJECTS FOR DESIGN KIT */
var SubmitButtonWithFeedback = function() {
	$('.js-submit-with-feedback').on("click", function(){
		var thisButton = $(this);
		if(!thisButton.hasClass("ref-button--is-loading") && !thisButton.hasClass("ref-button--success") && !thisButton.hasClass("ref-button--error")) {
			thisButton.addClass("ref-button--is-loading").attr("value", "Loading");

  		window.setTimeout(function(){
  			if(thisButton.hasClass("js-submit-success")) {
  				thisButton.removeClass("ref-button--is-loading").addClass("ref-button--success").attr("value", "Success!");
  			}
  			else {
  				thisButton.removeClass("ref-button--is-loading").addClass("ref-button--error").attr("value", "Oops, there's an error");
  			}
  			window.setTimeout(function(){
  				thisButton.removeClass("ref-button--success ref-button--error").attr("value", "Submit");
  			}, 2000);
  		}, 3000);
		}
	});
};

/* PAGE OBJECTS FOR TEMPLATES */

// DesignKitPage object
	var DesignKitPage = function(dkPageProperties) {
		// Import page banner
		var templateDesignKitBanner = Handlebars.templates['designKitBanner'];
		var htmlDesignKitBanner = templateDesignKitBanner({
			bannerImage: dkPageProperties.bannerImage,
			bannerImageAlt: dkPageProperties.bannerImageAlt, 
			sectionTitle: dkPageProperties.sectionTitle, 
			componentTitle: dkPageProperties.componentTitle, 
			intro: dkPageProperties.intro
		});
		$("#js-component-import--design-kit-banner").html(htmlDesignKitBanner);

		// syntax highlighter
		var script = document.createElement('script');
		  script.type = 'text/javascript';
		  script.src = 'js/vendor/prism/prism.js';
	  	$('#js-appendScriptsContainer').append(script);

		// Trigger menu click for current page
		if(dkPageProperties.pageUrl) {
			var thisPageLink = $('#js-component-import--panel-left').find('a[href="' + dkPageProperties.pageUrl + '"]');
		  thisPageLink.parent("li").addClass("is-selected").parents("li").addClass("is-selected");
		}
	};


// DKStarterPage object
	var DKStarterPage = function() {
		new Page();
		var dkPageProperties = {
			bannerImage: "images/banner-icon-typography.png",
			bannerImageAlt: "Tt", 
			sectionTitle: "Design Assets", 
			componentTitle: "Component Title", 
			intro: "Component Introduction"
		}
		new DesignKitPage(dkPageProperties);
	};


// HomePage object
	var HomePage = function() {
		new Page();
		var dkPageProperties = {
			bannerImage: "images/banner-icon-typography.png",
			bannerImageAlt: "Tt", 
			sectionTitle: "Design Assets", 
			componentTitle: "Component Title", 
			intro: "Component Introduction"
		}
		new DesignKitPage(dkPageProperties);
	};


// ButtonsPage object
	var ButtonsPage = function() {
		new Page();
		var dkPageProperties = {
			bannerImage: "images/banner-icon-buttons.png",
			bannerImageAlt: "Button icon", 
			sectionTitle: "UI Components", 
			componentTitle: "Buttons", 
			intro: "Our buttons perform a range of tasks such as linking to other sections, opening panels or submitting a form or a post. See and interact with the different types below and learn the applications in which they can be used.",
			pageUrl: "buttons.html"
		}
		new DesignKitPage(dkPageProperties);

		// Components
		new SubmitButtonWithFeedback();
	};


// ColoursPage object
	var ColoursPage = function() {
		new Page();
		var dkPageProperties = {
			bannerImage: "images/banner-icon-palette.png",
			bannerImageAlt: "Art palette icon", 
			sectionTitle: "Design Assets", 
			componentTitle: "Colours", 
			intro: "Our colour palette is designed to exude authority and trustworthiness whilst also making content easy to consume and interact with. Our colour accents are a clear identifier in our industry but also denote function as well as our brand values and personality.",
			pageUrl: "colours.html"
		}
		new DesignKitPage(dkPageProperties);

		// Functionality just for this template, and not reusable
		this.templateFunctions();
	};

	ColoursPage.prototype.templateFunctions = function() {
		$('.colour-link').on("click", function(e){
  		e.preventDefault();
  		$(this).find("input").select();
  	});
	};

// GridsPage object
	var GridsPage = function() {
		new Page();
		var dkPageProperties = {
			bannerImage: "images/banner-icon-grids.png",
			bannerImageAlt: "Grid icon", 
			sectionTitle: "Layout",
			componentTitle: "Grids & Breakpoints", 
			intro: "The base grid for our responsive app is structured of 12 columns with 2% padding and 0 gutters. There is a fixed margin on either side which changes based on screen width: 30px on laptop /desktop, 20px on phablet and 10px on mobile. Use the tabs below to see how this grid changes across different devices.",
			pageUrl: "grids.html"
		}
		new DesignKitPage(dkPageProperties);

		// Components
		new Tabs();

		// Functionality just for this template, and not reusable
		this.templateFunctions();
	};

	GridsPage.prototype.templateFunctions = function() {
		$('.js-toggle-overlay').on("click", function(){
  		$('.grid-demo-layer-top').toggleClass("has-transparency");
  	});

  	$('.js-change-grid-desktop').on("click", function(){
  		$('.grid-demo-container').removeClass("mobile-width phablet-width tablet-width");
  	});
  	$('.js-change-grid-tablet').on("click", function(){
  		$('.grid-demo-container').removeClass("mobile-width phablet-width").addClass("tablet-width");
  	});
  	$('.js-change-grid-phablet').on("click", function(){
  		$('.grid-demo-container').removeClass("mobile-width tablet-width").addClass("phablet-width");
  	});
  	$('.js-change-grid-mobile').on("click", function(){
  		$('.grid-demo-container').removeClass("phablet-width tablet-width").addClass("mobile-width");
  	});
	};


// TypographyPage object
	var TypographyPage = function() {
		new Page();
		var dkPageProperties = {
			bannerImage: "images/banner-icon-typography.png",
			bannerImageAlt: "Tt", 
			sectionTitle: "Design Assets", 
			componentTitle: "Typography", 
			intro: "Below are the styling rules for sitewide typography. Our brand typefaces are clean, uncomplicated and easy to read at any size. Specific spacing rules to ensure legibility and UX integrity are detailed below alongside examples.",
			pageUrl: "typography.html"
		}
		new DesignKitPage(dkPageProperties);
	};


// TabsPage object
	var TabsPage = function() {
		new Page();
		var dkPageProperties = {
			bannerImage: "images/banner-icon-folder.png",
			bannerImageAlt: "Folder",
			sectionTitle: "UI Components", 
			componentTitle: "Tabs", 
			intro: "Tabs switch between sections or types of content within a page. Their design with a container box indicates clearly what components within or below are asscoiated with them hierarchically.",
			pageUrl: "tabs.html"
		}
		new DesignKitPage(dkPageProperties);

		// Components
		new Tabs();
	};


// InteractionsPage object
	var InteractionsPage = function() {
		new Page();
		var dkPageProperties = {
			bannerImage: "images/banner-icon-film.png",
			bannerImageAlt: "Film icon",
			sectionTitle: "UI Interactions",
			componentTitle: "Interactions Overview",
			intro: "These rules govern all motion, transitions and interactive behaviour throughout the site. All these behaviours are aligned with our core brand values, make the interface feel intuitive, provide clear feedback and provide obvious visual cues to intended function.",
			pageUrl: "interactions.html"
		}
		new DesignKitPage(dkPageProperties);

		// Components
		new Tabs();
		new FormFieldSelect();

		// Functionality just for this template, and not reusable
		this.templateFunctions();
	};

	InteractionsPage.prototype.templateFunctions = function() {
		$('.grid__column--one-third .js-main-nav a').on("click", function(e){
			e.preventDefault();
		});
	};


// FormsPage object
	var FormsPage = function() {
		new Page();
		var dkPageProperties = {
			bannerImage: "images/banner-icon-buttons.png",
			bannerImageAlt: "Button icon",
			sectionTitle: "UI Components",
			componentTitle: "Form Fields",
			intro: "The following range of components are used to construct forms that can perform any function. Certain styles are altered based on the form fields location but the interactions and behaviours will remain consistent throughout as demonstrated below.",
			pageUrl: "forms.html"
		}
		new DesignKitPage(dkPageProperties);

		// Components
		new FormFieldSelect();

		// Functionality just for this template, and not reusable
		this.templateFunctions();
	};

	FormsPage.prototype.templateFunctions = function() {
		$('.form-field--date-select input').on("click", function(){
			$this = $(this);
			$this.select();
			$popup = $this.parents('.date-select-container').siblings('.dateBoxPopup');
			if($popup.hasClass('is-showing')) {
				$popup.removeClass('is-showing');
			}
			else {
				$popup.addClass('is-showing');
			}
		});
	};


	// Loading indicators page object
	var LoadingPage = function() {
		var instance = this;
		new Page();
		var dkPageProperties = {
			bannerImage: "images/banner-icon-timer.png",
			bannerImageAlt: "Timer icon",
			sectionTitle: "UI Interactions",
			componentTitle: "Loading Indicators",
			intro: "The loading indicators are consistent throughout the site and flexible enough to work in every application. They clearly indicate to the user that something is happening, how long it will take (where possible) and make any downtime useful with interesting stats and facts about the app market.",
			pageUrl: "loading-indicators.html"
		}
		new DesignKitPage(dkPageProperties);

		// Components
		new SubmitButtonWithFeedback();		

		// Functionality just for this template, and not reusable
		this.templateFunctions();		
	};

	LoadingPage.prototype.templateFunctions = function() {
		var instance = this;		
		$(".js-submit-loading").on("click", function() {
			instance.createLoadingBar();
			new LoadingMessageBox();
		}); // demo loading bar
		$(".js-submit-loading-determinate").on("click", function() {
			instance.createLoadingBarDeterminate();
			new LoadingMessageBox();
		}); // demo loading bar
		$(".js-submit-component-loading").on("click", function() {
			instance.createComponentLoadingBar();
		}); // demo loading bar
		$(".js-submit-component-loading-determinate").on("click", function() {
			instance.createComponentLoadingBarDeterminate();
		}); // demo loading bar
		$(".js-submit-determinate-with-feedback").on("click", function(){
			instance.loadingButtonDeterminate($(this));
		});

		instance.loadTableDataSwitch = 1;
		$(".js-load-table-data").on("click", function(){
			instance.simulateLoadTableData();
		});

		$('.js-load-app-icon').on("click", function(){
			instance.mockLoadAppDetails();		
		});
	};

	LoadingPage.prototype.createLoadingBar = function() {
		var $loadingText = $("<span>").text("Loading...");
		var $loadingBar = $("<div>").addClass("page-loading").addClass("is-opening").append($loadingText);
		$("body").append($loadingBar);			
		this.mockLoading($loadingBar, $loadingText);
	};

	LoadingPage.prototype.createLoadingBarDeterminate = function() {
		var $loadingText = $("<span>").text("Loading...");
		var $loadingBar = $("<div>").addClass("page-loading").addClass("page-loading--determinate").addClass("is-opening").append($loadingText);
		var $progressBar = $("<div>").addClass("loading-progress");
		$loadingBar.append($progressBar);
		$("body").append($loadingBar);
		this.mockLoadingDeterminate($loadingBar, $loadingText, $progressBar);
	}

	LoadingPage.prototype.createComponentLoadingBar = function() {
		var $loadingText = $("<span>").text("Loading...");
		var $loadingBar = $("<div>").addClass("page-loading").addClass("component-loading").addClass("is-opening").append($loadingText);
		$(".component-example").append($loadingBar);
		this.mockLoading($loadingBar, $loadingText);
	};

	LoadingPage.prototype.createComponentLoadingBarDeterminate = function() {
		var $loadingText = $("<span>").text("Loading...");
		var $loadingBar = $("<div>").addClass("page-loading").addClass("component-loading").addClass("page-loading--determinate").addClass("is-opening").append($loadingText);
		var $progressBar = $("<div>").addClass("loading-progress");
		$loadingBar.append($progressBar);
		$(".component-example").append($loadingBar);
		this.mockLoadingDeterminate($loadingBar, $loadingText, $progressBar);
	};

	LoadingPage.prototype.mockLoading = function($loadingBar, $loadingText) {
		setTimeout(function(){
			$loadingBar.removeClass("is-opening");
		}, 100);
		setTimeout(function(){
			$loadingText.text("Searching app data...");
			setTimeout(function(){
				$loadingText.text("Loading app details...");
				setTimeout(function(){
					$loadingText.text("Oh dear! Something went wrong. Please try again.");
					$loadingBar.addClass('is-complete--error');
				}, 3000);
			}, 2000);
		}, 1000);

		$("html, body").on("click", function(){
			var loadingBar = $(".page-loading.is-complete--error");
			if(loadingBar.length) {
				loadingBar.addClass("is-closing");
				setTimeout(function(){
					loadingBar.remove();
				}, 1000);
			}
		});	
	}

	LoadingPage.prototype.mockLoadingDeterminate = function($loadingBar, $loadingText, $progressBar) {
		setTimeout(function(){
			$loadingBar.removeClass("is-opening");
		}, 100);
		setTimeout(function(){
			$progressBar.width("25%");
			setTimeout(function(){
				$loadingText.text("Searching app data...");
				$progressBar.width("35%");
				setTimeout(function(){
					$loadingText.text("Loading app details...");
					$progressBar.width("56%");
					setTimeout(function(){
						$progressBar.width("80%");
						setTimeout(function(){
							$progressBar.width("100%");
							setTimeout(function(){
								$loadingText.text("Done!");
								$loadingBar.addClass('is-complete');
								setTimeout(function(){
									$loadingBar.addClass("is-closing");
									setTimeout(function(){
										$loadingBar.remove();
									}, 1000);
								}, 1000);
							}, 400);
						}, 1500);
					}, 500);
				}, 1000);
			}, 500);
		}, 300);
	}

	LoadingPage.prototype.loadingButtonDeterminate = function($button) {
		if(!$button.hasClass("ref-button--is-loading") && !$button.hasClass("ref-button--success") && !$button.hasClass("ref-button--error")) {
			$button.addClass("ref-button--is-loading");
			var $buttonText = $button.find(".loading-button-text").text("Loading");
			var $progressBar = $button.find(".loading-progress");
			$progressBar.show();
			setTimeout(function(){
				$progressBar.width("25%");
				setTimeout(function(){
					$progressBar.width("35%");
					setTimeout(function(){
						$progressBar.width("56%");
						setTimeout(function(){
							$progressBar.width("80%");
							setTimeout(function(){
								$progressBar.width("100%");
								setTimeout(function(){
									$progressBar.hide().width(0);
									if($button.hasClass("js-submit-success")) {
										$button.removeClass("ref-button--is-loading").addClass("ref-button--success");
										$buttonText.text("Success!");
									}
									else {
										$button.removeClass("ref-button--is-loading").addClass("ref-button--error");
										$buttonText.text("Oops, there's an error");
									}
									window.setTimeout(function(){
					  				$button.removeClass("ref-button--success ref-button--error");
					  				$buttonText.text("Submit");
					  			}, 2000);
								}, 400);
							}, 1500);
						}, 500);
					}, 1000);
				}, 500);
			}, 300);
		}
	};

	LoadingPage.prototype.simulateLoadTableData = function() {
		instance = this;
		var $loadingIcon = $('<svg class="loading-ellipsis" version="1.1" x="0px" y="0px" viewBox="0 0 24.2 6.6" enable-background="new 0 0 24.2 6.6" xml:space="preserve"><circle class="dot-2" fill="#E7E7EA" cx="12.1" cy="3.4" r="3.2"/><circle class="dot-1" fill="#E7E7EA" cx="3.2" cy="3.2" r="3.2"/><circle class="dot-3" fill="#E7E7EA" cx="21" cy="3.2" r="3.2"/></svg>');
		if(!$('.table-demo-loading').hasClass("is-loading")) {
			$('.table-demo-loading').addClass("is-loading");
			$('.table-demo-loading span').hide();
			$('.table-demo-loading td').append($loadingIcon);
			var mockData = [];
			if(instance.loadTableDataSwitch > 0) {
				mockData.push("Delta-V Racing", "Zoo Keeper Ken", "Clash of Clans");
			} else {
				mockData.push("Crossy Road", "MineCraft", "Candy Crush Saga");
			}
			var i = 0;
			$('.table-demo-loading td span.js-demo-app-name').each(function(){
				$(this).text(mockData[i]);
				i++;
			});
			setTimeout(function(){
				$('.table-demo-loading').removeClass('is-loading');
				$('.table-demo-loading td svg').remove();
				$('.table-demo-loading td span').fadeIn(200);
			}, 3000);
			instance.loadTableDataSwitch *= -1;
		}
	};

	LoadingPage.prototype.mockLoadAppDetails = function() {
		if(!$('.loading-demo-icon-container').hasClass('is-loading')) {
			$('.loading-demo-icon-container').addClass('is-loading');
			var $loadingIcon = $('<svg class="loading-ellipsis" version="1.1" x="0px" y="0px" viewBox="0 0 24.2 6.6" enable-background="new 0 0 24.2 6.6" xml:space="preserve"><circle class="dot-2" fill="#E7E7EA" cx="12.1" cy="3.4" r="3.2"/><circle class="dot-1" fill="#E7E7EA" cx="3.2" cy="3.2" r="3.2"/><circle class="dot-3" fill="#E7E7EA" cx="21" cy="3.2" r="3.2"/></svg>');
			var $appHeading = $('.loading-demo-app-details h2').html($loadingIcon);
			setTimeout(function(){
				$appHeading.hide().html("Monument Valley").fadeIn(200);
				$appIconImage = $('<img>').attr("src", "images/loading-demo-app-icon.png").attr("alt", "App Icon");
				$('.loading-demo-icon-container').append($appIconImage.hide().fadeIn(200));
				$('.loading-demo-icon-container').removeClass('is-loading');
			}, 3000);
		}
	};


// PopupsPage object
	var PopupsPage = function() {
		new Page();
		var dkPageProperties = {
			bannerImage: "images/banner-icon-popup.png",
			bannerImageAlt: "Popup icon", 
			sectionTitle: "UI Components", 
			componentTitle: "Popups", 
			intro: "Content that is only required at specific points in a user journey is contained within pop ups e.g. help copy that identifies features or modal boxes that carry out specific functions. The various applications are detailed below.",
			pageUrl: "popups.html"
		}
		new DesignKitPage(dkPageProperties);

		// Components
		new FormFieldSelect();
		new ToolTip();
	};

// ChartsPage object
	var ChartsPage = function() {
		new Page();
		var dkPageProperties = {
			bannerImage: "images/banner-icon-grids.png",
			bannerImageAlt: "Button icon", 
			sectionTitle: "Charts", 
			componentTitle: "Highcharts Demos", 
			intro: "",
			pageUrl: "highcharts.html"
		}
		new DesignKitPage(dkPageProperties);

		// Components
		
	};

// ChartsPage object
	var AMChartsPage = function() {
		new Page();
		var dkPageProperties = {
			bannerImage: "images/banner-icon-grids.png",
			bannerImageAlt: "Button icon", 
			sectionTitle: "Charts", 
			componentTitle: "AM Charts Demos", 
			intro: "",
			pageUrl: "am-charts.html"
		}
		new DesignKitPage(dkPageProperties);

		// Components
		
	};
/* END PAGE OBJECTS FOR TEMPLATES */

