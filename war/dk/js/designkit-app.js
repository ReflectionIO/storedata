// APPLICATION JAVASCRIPT
// Use prototypal inheritance to set functions relevant to the page to encapsulate functions (page objects below)
// Use functions as variables to modularise and encapsulate component functionality in application.js - which contains reusable components JS for the application
// Keep design kit only JS in this file, and resusable application JS in application.js

/* COMPONENT OBJECTS FOR DESIGN KIT */
var SubmitButtonWithFeedback = function() {
	$('.js-submit-with-feedback').on("click", function(){
		var thisButton = $(this);
		if(!thisButton.hasClass("ref-button--is-loading") && !thisButton.hasClass("ref-button--success") && !thisButton.hasClass("ref-button--error")) {
			thisButton.addClass("ref-button--is-loading").attr("value", "Loading...");

  		window.setTimeout(function(){
  			if(thisButton.hasClass("js-submit-success")) {
  				thisButton.removeClass("ref-button--is-loading").addClass("ref-button--success").attr("value", "Success!");
  			}
  			else {
  				thisButton.removeClass("ref-button--is-loading").addClass("ref-button--error").attr("value", "Oops, something went wrong");
  			}
  			window.setTimeout(function(){
  				thisButton.removeClass("ref-button--success ref-button--error").attr("value", "Submit");
  			}, 3000);
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
/* END PAGE OBJECTS FOR TEMPLATES */



