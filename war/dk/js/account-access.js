//import the header from handlebars
var templateGlobalHeader = Handlebars.templates['globalHeader'];
var htmlGlobalHeader = templateGlobalHeader({});
$("#js-component-import--global-header").html(htmlGlobalHeader);

var templateAppMenu = Handlebars.templates['appMenu'];
var htmlAppMenu = templateAppMenu({});
$("#js-component-import--panel-left").html(htmlAppMenu);

var AccountAccess = function() {
	instance = this;
	// App Components
	new BrowserDetection();
	new FormInteractions();
	new Tabs();

	// Functionality for this template set
	this.pageLoad();
	this.customTabTransition();
	this.resetPasswordForm();
	this.mockSubmitPasswordReset();
};

AccountAccess.prototype.pageLoad = function() {
	var urlHash = window.location.hash;	
	if(urlHash == '#login') {
		$('body').addClass('login-form-is-showing');
		$('a[data-content=#tab-content-login]').parents('.tabs__tab').addClass('is-active');
		$('#tab-content-login').addClass('tabs__content--is-showing');
	} else {
		$('body').addClass('apply-form-is-showing');
		$('a[href=#tab-content-apply]').parents('.tabs__tab').addClass('is-active');
		$('#tab-content-apply').addClass('tabs__content--is-showing');
	}
	if($(window).width() <= 350) {
		$('.login-content .blockquote--large').removeClass('blockquote--large');
	}
};

AccountAccess.prototype.customTabTransition = function() {
	$('.account-form-container .tabs__content:not(.tabs__content--is-showing)').css({"visibility":"hidden","position":"absolute"});
	$('.account-form-container .tabs__content--is-showing').css({"visibility":"visible","position":"relative"});
	if($('.ie8').length > 0) {
		$('.account-form-container .tabs__content:not(.tabs__content--is-showing)').css("display","none");
	}

	$('.account-form-container .js-tab-select').on("mouseup", function(e){
		var $this = $(this);
		var contentId = $this.find('.tabs__link').data("content");
		$(contentId).addClass('will-show');
		var $body = $('body');
		if($body.hasClass('apply-form-is-showing') && contentId == '#tab-content-login') {
			$body.removeClass('apply-form-is-showing').addClass('login-form-is-showing');
		} else if (contentId == '#tab-content-apply') {
			$body.removeClass('login-form-is-showing').addClass('apply-form-is-showing');
		}
		setTimeout(function(){
			$('.account-form-container .tabs__content:not(.tabs__content--is-showing)').css({"visibility":"hidden","position":"absolute"});
			$('.account-form-container .tabs__content--is-showing').css({"visibility":"visible","position":"relative"});
			if($('.ie8').length > 0) {
				$('.account-form-container .tabs__content:not(.tabs__content--is-showing)').css("display","none");
				$('.account-form-container .tabs__content--is-showing').css("display","block");
			}
			$(contentId).removeClass('will-show');
		}, 150);

		if($(window).width() <= 720) {
			instance.scrollToFormContainerTop();
		}
	});
};

AccountAccess.prototype.scrollToFormContainerTop = function() {
	setTimeout(function(){
		var formContentTop = $('.account-form-container').offset().top;
		$('html,body').animate({
      scrollTop: formContentTop - 70
    }, 310);
	}, 300);
}

AccountAccess.prototype.resetPasswordForm = function() {
	$('.js-mock-show-reset-password').on("click", function(e){
		e.preventDefault();
		var $currentTab = $(this).parents('.tabs__content--is-showing').addClass('show-reset-password-form').addClass('will-show');
		$('body').addClass('reset-password-form-is-showing');
		setTimeout(function(){
			$('.tabs__content--is-showing .form--login').css({"visibility":"hidden","position":"absolute"});
			$('.tabs__content--is-showing .form--password-reset').css({"visibility":"visible","position":"relative"});
			$currentTab.removeClass('will-show');
			if($('.ie8').length > 0) {
				$('.tabs__content--is-showing .form--login').css("display","none");
				$('.tabs__content--is-showing .form--password-reset').css("display","block");
			}
		}, 150);
		if($(window).width() <= 720) {
			instance.scrollToFormContainerTop();
		}
	});
};

AccountAccess.prototype.mockSubmitPasswordReset = function() {
	$('.account-access-page .js-mock-send-reset-password').on("click", function(e){
		e.preventDefault();
		var $this = $(this);		
		if(!$this.hasClass("ref-button--success") && !$this.hasClass("ref-button--positive")) {
			console.log('ello');
			$this.attr('value', 'Email is on the way').addClass('ref-button--positive');
			$this.parents('.tabs__content--is-showing').addClass('tabs__content--is-submitted').find('.form-submitted-success').addClass('is-showing');
			setTimeout(function(){
				$this.addClass('ref-button--success').removeClass('ref-button--positive');
			}, 2000);
		}		
	});
};

var AccountSetup = function() {
	instance = this;
	// App Components
	new BrowserDetection();
	new FormInteractions();

	// Functionality for this template set
	this.mockLinkAccount();
}

AccountSetup.prototype.mockLinkAccount = function() {
	$('.js-mock-link-account').on("click", function(e) {
		e.preventDefault();
		if(!$('body').hasClass("form-submitted-loading")) {
			$('body').addClass('form-submitted-loading');
			$(this).addClass("ref-button--is-loading").attr("value", "Loading");
			setTimeout(function(){
				$('body').addClass('form-submitted-success-complete').removeClass("form-submitted-loading");
				$(this).attr('value', 'Account Linked!').addClass('ref-button--success');
				$('.account-connect-animation').addClass('plugs-connected');
				$('.form-submitted-success').addClass('is-showing');
			}, 10000)
		}
	});
	$('.js-mock-link-another-account').on("click", function(e){
		e.preventDefault();
		$(this).parents('.form-submitted-success').removeClass('is-showing');
		$('.account-connect-animation').removeClass('plugs-connected');
		$('.js-mock-link-account').attr('value', 'Link this Account').removeClass('ref-button--success ref-button--is-loading');
		$('.form-submitted-success-complete').removeClass('form-submitted-success-complete');
	});
};

var ResetPasswordPage = function() {
	new BrowserDetection();
	new FormInteractions();

	// JS for reset password button
	$('.js-mock-reset-password').on("click", function(e) {
		e.preventDefault();
		$('body').addClass('form-submitted-success-complete');
		$(this).attr('value', "Your Password's Been Changed").addClass('ref-button--success');
		$('.form-submitted-success').addClass('is-showing');
	});
};
