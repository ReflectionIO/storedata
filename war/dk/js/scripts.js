(function($) {

	initMainNavCollapsibleLists();
	initLeftPanelInteraction();
	initGlobalFormInteractions();
	initRightPanelInteraction();

	function initLeftPanelInteraction() {

		$('.js-hamburger-button').on("click", function(){
			$(this).toggleClass('is-selected');
			$('.l-page-container').toggleClass('panel-left-open');
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

		if($(window).width() <= 480) {
			$('.js-hamburger-button').removeClass('is-selected');
			$('.l-page-container').removeClass('panel-left-open');
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
			collapsibleList.css("margin-top", -totalHeight);
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
			}
		});
		$('.link-log-in').on("click", function() {
			$('.panel-right-container').toggleClass('is-showing');
			$('body').toggleClass('no-scroll');
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

})(jQuery);