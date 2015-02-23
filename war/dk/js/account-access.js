//import the header from handlebars
var templateFontDeclarations = Handlebars.templates['fontDeclarations'];
var htmlFontDeclarations = templateFontDeclarations({});
$("head").append(htmlFontDeclarations);

var templateGlobalHeader = Handlebars.templates['globalHeader'];
var htmlGlobalHeader = templateGlobalHeader({});
$("#js-component-import--global-header").html(htmlGlobalHeader);

var AccountAccess = function() {
	instance = this;
	// App Components
	new BrowserDetection();
	new FormInteractions();
	new Tabs();

	// Functionality for this template set
	this.pageLoad();
	this.mockSubmitApply();
	this.customTabTransition();
	this.resetPasswordForm();
	this.mockSubmitPasswordReset();
};

AccountAccess.prototype.pageLoad = function() {
	var urlHash = window.location.hash;	
	if(urlHash == '#login') {
		$('body').addClass('login-form-is-showing');
		$('a[href=#tab-content-login]').parents('.tabs__tab').addClass('is-active');
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

AccountAccess.prototype.mockSubmitApply = function() {
	// Apply button click success
	$('.account-access-page .js-mock-send').on("click", function(e){
		e.preventDefault();
		var $this = $(this);
		$this.attr('value', 'Application Sent!').addClass('ref-button--success');
		$this.parents('.tabs__content--is-showing').addClass('tabs__content--is-submitted').find('.form-submitted-success').addClass('is-showing');
	});
};

AccountAccess.prototype.customTabTransition = function() {
	$('.account-form-container .tabs__content:not(.tabs__content--is-showing)').css({"visibility":"hidden","position":"absolute"});
	$('.account-form-container .tabs__content--is-showing').css({"visibility":"visible","position":"relative"});
	if($('.ie8').length > 0) {
		$('.account-form-container .tabs__content:not(.tabs__content--is-showing)').css("display","none");
	}

	$('.account-form-container .js-tab-select').on("click", function(e){
		var $this = $(this);
		var contentId = $this.find('.tabs__link').attr("href");
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
		$this.attr('value', 'Email is on the way').addClass('ref-button--success');
		$this.parents('.tabs__content--is-showing').addClass('tabs__content--is-submitted').find('.form-submitted-success').addClass('is-showing');
	});
};

var AccountSetup = function() {
	instance = this;
	// App Components
	new BrowserDetection();
	new FormInteractions();

	// Functionality for this template set
	this.pageLoad();
	this.mockSubmitContinue();
	this.mockLinkAccount();
};

AccountSetup.prototype.pageLoad = function() {
	$('.connect-account-content').css({"visibility":"hidden","position":"absolute"});
	$('.create-password-content').css({"visibility":"visible","position":"relative"});
	if($('.ie8').length > 0) {
		$('.connect-account-content').css("display","none");
		$('.create-password-content').css("display","block");
	}
};

AccountSetup.prototype.mockSubmitContinue = function() {
	$('.js-mock-continue').on("click", function(e){
		e.preventDefault();
		$('body').removeClass('create-password-form-is-showing').addClass('connect-account-is-showing');
		var $connectAccountContainer = $('.connect-account-content');
		setTimeout(function(){
			$('.create-password-content').css({"visibility":"hidden","position":"absolute"});
			$connectAccountContainer.css({"visibility":"visible","position":"relative"});
			if($('.ie8').length > 0) {
				$('.create-password-content').css("display","none");
				$connectAccountContainer.css("display","block");
			}
		}, 150);
	});
};

AccountSetup.prototype.mockLinkAccount = function() {
	$('.js-mock-link-account').on("click", function(e) {
		e.preventDefault();
		$('body').addClass('form-submitted-success-complete');
		$(this).attr('value', 'Account Linked!').addClass('ref-button--success');
		$('.account-connect-animation').addClass('plugs-connected');
		$('.form-submitted-success').addClass('is-showing');
	});
	$('.js-mock-link-another-account').on("click", function(e){
		e.preventDefault();
		$(this).parents('.form-submitted-success').removeClass('is-showing');
		$('.account-connect-animation').removeClass('plugs-connected');
		$('.js-mock-link-account').attr('value', 'Link this Account').removeClass('ref-button--success');
		$('.form-submitted-success-complete').removeClass('form-submitted-success-complete');
	});
};
