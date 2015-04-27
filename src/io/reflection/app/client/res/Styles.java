//
//  Styles.java
//  storedata
//
//  Created by William Shakour (billy1380) on 9 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.res;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;

/**
 * @author billy1380
 * 
 */
public interface Styles extends ClientBundle {
	public interface ReflectionStyles extends CssResource {

		String footerFacebook();

		String footerLinkedin();

		String footerTwitter();

		String footerLogo();

		String footerUpArrow();

		String footerDownArrow();

		String unknownAppSmall();

		String unknownAppLarge();

		String linkedAccountClose();

		String linkedAccountMinus();

		String linkedAccountPlus();

		String smooth();

		String noteDetail();

		String header();

		String footer();
	}

	public interface ReflectionMainStyles extends CssResource {

		@ClassName("MHXTE6C-I-a")
		String mhxte6cIA();

		@ClassName("MHXTE6C-i-a")
		String mhxte6ciA();

		@ClassName("MHXTE6C-i-c")
		String mhxte6cIC();

		@ClassName("MHXTE6C-i-d")
		String mhxte6cID();

		@ClassName("MHXTE6C-i-f")
		String mhxte6cIF();

		@ClassName("MHXTE6C-i-s")
		String mhxte6cIS();

		@ClassName("MHXTE6C-ob-a")
		String mhxte6cObA();

		@ClassName("MHXTE6C-r-a")
		String mhxte6cRA();

		String accordion();

		@ClassName("accordion-bottom-with-divider")
		String accordionBottomWithDivider();

		@ClassName("accordion-content")
		String accordionContent();

		@ClassName("accordion-switch")
		String accordionSwitch();

		@ClassName("account-access-content")
		String accountAccessContent();

		@ClassName("account-access-page")
		String accountAccessPage();

		@ClassName("account-connect-animation")
		String accountConnectAnimation();

		@ClassName("account-form-container")
		String accountFormContainer();

		@ClassName("account-form-heading")
		String accountFormHeading();

		@ClassName("account-settings-section")
		String accountSettingsSection();

		@ClassName("account-setup-content-wrapper")
		String accountSetupContentWrapper();

		@ClassName("account-user-details")
		String accountUserDetails();

		@ClassName("actions-group")
		String actionsGroup();

		@ClassName("actions-group-container")
		String actionsGroupContainer();

		@ClassName("actions-group__content")
		String actionsGroup__content();

		String addthis_sharing_toolbox();

		@ClassName("app-creator")
		String appCreator();

		@ClassName("app-details")
		String appDetails();

		@ClassName("app-details-column")
		String appDetailsColumn();

		@ClassName("app-icon")
		String appIcon();

		@ClassName("app-icon-small")
		String appIconSmall();

		@ClassName("app-name")
		String appName();

		@ClassName("app-price")
		String appPrice();

		@ClassName("app-title")
		String appTitle();

		@ClassName("app-user-functions-container")
		String appUserFunctionsContainer();

		@ClassName("apply-content")
		String applyContent();

		@ClassName("apply-form-is-showing")
		String applyFormIsShowing();

		@ClassName("article-list--article")
		String articleListArticle();

		String ascending();

		@ClassName("at-share-btn")
		String atShareBtn();

		@ClassName("at-share-tbx-element")
		String atShareTbxElement();

		@ClassName("blockquote--large")
		String blockquoteLarge();

		@ClassName("blockquote--login")
		String blockquoteLogin();

		@ClassName("blockquote--on-purple")
		String blockquoteOnPurple();

		@ClassName("blockquote--reset-password")
		String blockquoteResetPassword();

		@ClassName("blockquote-container")
		String blockquoteContainer();

		@ClassName("blog-comments")
		String blogComments();

		@ClassName("blog-content-container")
		String blogContentContainer();

		@ClassName("blog-tags")
		String blogTags();

		String breadcrumb();

		@ClassName("breadcrumb-container")
		String breadcrumbContainer();

		String browserupgrade();

		@ClassName("button-example-code-container")
		String buttonExampleCodeContainer();

		@ClassName("button-example-container")
		String buttonExampleContainer();

		@ClassName("button-example-container--full-width")
		String buttonExampleContainerFullWidth();

		@ClassName("buttons-container")
		String buttonsContainer();

		@ClassName("calendar-container")
		String calendarContainer();

		@ClassName("can-be-sorted")
		String canBeSorted();

		@ClassName("can-toggle")
		String canToggle();

		@ClassName("can-toggle--size-large")
		String canToggleSizeLarge();

		@ClassName("can-toggle--size-small")
		String canToggleSizeSmall();

		@ClassName("can-toggle__label-text")
		String canToggle__labelText();

		@ClassName("can-toggle__switch")
		String canToggle__switch();

		String cellTableLoading();

		@ClassName("centre-content-container")
		String centreContentContainer();

		@ClassName("centred-element")
		String centredElement();

		String checkboxLabel();

		String checkboxLabelDisabled();

		String checkboxLabelVisible();

		String checkboxLabelVisibleDisabled();

		@ClassName("clear-both")
		String clearBoth();

		@ClassName("close-popup")
		String closePopup();

		@ClassName("collapsible-content")
		String collapsibleContent();

		@ClassName("collapsible-trigger")
		String collapsibleTrigger();

		@ClassName("colour-box")
		String colourBox();

		@ClassName("colour-box--dark-grey-one")
		String colourBoxDarkGreyOne();

		@ClassName("colour-box--dark-grey-two")
		String colourBoxDarkGreyTwo();

		@ClassName("colour-box--divider-grey")
		String colourBoxDividerGrey();

		@ClassName("colour-box--graph-fill-grey")
		String colourBoxGraphFillGrey();

		@ClassName("colour-box--light-grey-one")
		String colourBoxLightGreyOne();

		@ClassName("colour-box--light-grey-two")
		String colourBoxLightGreyTwo();

		@ClassName("colour-box--mid-grey-one")
		String colourBoxMidGreyOne();

		@ClassName("colour-box--mid-grey-two")
		String colourBoxMidGreyTwo();

		@ClassName("colour-box--panel-grey")
		String colourBoxPanelGrey();

		@ClassName("colour-box--reflection-green")
		String colourBoxReflectionGreen();

		@ClassName("colour-box--reflection-purple")
		String colourBoxReflectionPurple();

		@ClassName("colour-box--reflection-red")
		String colourBoxReflectionRed();

		@ClassName("colour-box--reflection-white")
		String colourBoxReflectionWhite();

		@ClassName("colour-link")
		String colourLink();

		@ClassName("colour-palette-container")
		String colourPaletteContainer();

		@ClassName("comments-link")
		String commentsLink();

		@ClassName("components-row-with-border")
		String componentsRowWithBorder();

		@ClassName("connect-account-content")
		String connectAccountContent();

		@ClassName("connect-account-is-showing")
		String connectAccountIsShowing();

		@ClassName("connect-account-main-content")
		String connectAccountMainContent();

		@ClassName("content-list")
		String contentList();

		@ClassName("content-list--featured")
		String contentListFeatured();

		@ClassName("content-list--minor")
		String contentListMinor();

		@ClassName("content-list-item")
		String contentListItem();

		String cord();

		@ClassName("create-password-content")
		String createPasswordContent();

		@ClassName("create-password-form-is-showing")
		String createPasswordFormIsShowing();

		@ClassName("create-password-main-content")
		String createPasswordMainContent();

		@ClassName("custom--tablet-one-half")
		String customTabletOneHalf();

		@ClassName("data-bar")
		String dataBar();

		@ClassName("data-display-container")
		String dataDisplayContainer();

		@ClassName("data-display-header")
		String dataDisplayHeader();

		@ClassName("data-display-header--component")
		String dataDisplayHeaderComponent();

		@ClassName("data-heading")
		String dataHeading();

		@ClassName("data-summary-container")
		String dataSummaryContainer();

		@ClassName("data-summary-container-synced")
		String dataSummaryContainerSynced();

		@ClassName("date-select-container")
		String dateSelectContainer();

		@ClassName("date-select-to-indicator")
		String dateSelectToIndicator();

		@ClassName("date-value")
		String dateValue();

		String dateBoxPopup();

		String datePickerDayIsDisabled();

		String datePickerDayIsFiller();

		String datePickerDayIsToday();

		String datePickerDayIsValue();

		String datePickerDays();

		String datePickerMonth();

		String datePickerMonthSelector();

		String datePickerNextButton();

		String datePickerPreviousButton();

		String datePickerWeekdayLabel();

		String datePickerWeekendLabel();

		@ClassName("default-tabs-transition")
		String defaultTabsTransition();

		String descending();

		@ClassName("dk-component-row")
		String dkComponentRow();

		@ClassName("downloads-column")
		String downloadsColumn();

		@ClassName("easing-figure-container")
		String easingFigureContainer();

		@ClassName("easing-represenation-container")
		String easingRepresenationContainer();

		@ClassName("easing-representation")
		String easingRepresentation();

		@ClassName("easing-representation__slider")
		String easingRepresentation__slider();

		@ClassName("easing-representation__slider--ease-in")
		String easingRepresentation__sliderEaseIn();

		@ClassName("easing-section")
		String easingSection();

		@ClassName("email-delivery-animation")
		String emailDeliveryAnimation();

		@ClassName("email-delivery-animation--email")
		String emailDeliveryAnimationEmail();

		@ClassName("email-delivery-animation--van-body")
		String emailDeliveryAnimationVanBody();

		@ClassName("email-post-animation")
		String emailPostAnimation();

		@ClassName("email-success-envelope")
		String emailSuccessEnvelope();

		@ClassName("email-success-handle")
		String emailSuccessHandle();

		@ClassName("feature-complete")
		String featureComplete();

		@ClassName("filter-container--app")
		String filterContainerApp();

		@ClassName("filter-container--my-apps")
		String filterContainerMyApps();

		@ClassName("filter-switch")
		String filterSwitch();

		@ClassName("filter-switch--small")
		String filterSwitchSmall();

		@ClassName("filter-toggle")
		String filterToggle();

		@ClassName("filters-container")
		String filtersContainer();

		@ClassName("filters-group-graph-options")
		String filtersGroupGraphOptions();

		@ClassName("filters-group-other")
		String filtersGroupOther();

		@ClassName("filters-group-time")
		String filtersGroupTime();

		@ClassName("font-demo")
		String fontDemo();

		@ClassName("form--login")
		String formLogin();

		@ClassName("form--password-reset")
		String formPasswordReset();

		@ClassName("form-demo")
		String formDemo();

		@ClassName("form-field")
		String formField();

		@ClassName("form-field--checkbox")
		String formFieldCheckbox();

		@ClassName("form-field--checkbox-list")
		String formFieldCheckboxList();

		@ClassName("form-field--checkbox-list--inline")
		String formFieldCheckboxListInline();

		@ClassName("form-field--date-select")
		String formFieldDateSelect();

		@ClassName("form-field--date-select--no-icon")
		String formFieldDateSelectNoIcon();

		@ClassName("form-field--disabled")
		String formFieldDisabled();

		@ClassName("form-field--error")
		String formFieldError();

		@ClassName("form-field--radio-list")
		String formFieldRadioList();

		@ClassName("form-field--radio-list--inline")
		String formFieldRadioListInline();

		@ClassName("form-field--select")
		String formFieldSelect();

		@ClassName("form-field--select-disabled")
		String formFieldSelectDisabled();

		@ClassName("form-field-help")
		String formFieldHelp();

		@ClassName("form-link")
		String formLink();

		@ClassName("form-submitted-success")
		String formSubmittedSuccess();

		@ClassName("form-submitted-success-complete")
		String formSubmittedSuccessComplete();

		@ClassName("forms--dark-theme")
		String formsDarkTheme();

		@ClassName("forms--mid-theme")
		String formsMidTheme();

		@ClassName("full-screen-intro")
		String fullScreenIntro();

		@ClassName("general-error-message")
		String generalErrorMessage();

		@ClassName("global-header")
		String globalHeader();

		@ClassName("global-header__actions")
		String globalHeader__actions();

		@ClassName("graph-container")
		String graphContainer();

		@ClassName("grid-container")
		String gridContainer();

		@ClassName("grid-demo-column")
		String gridDemoColumn();

		@ClassName("grid-demo-column--alternate")
		String gridDemoColumnAlternate();

		@ClassName("grid-demo-container")
		String gridDemoContainer();

		@ClassName("grid-demo-layer-top")
		String gridDemoLayerTop();

		@ClassName("grid-demo-overlay")
		String gridDemoOverlay();

		@ClassName("grid-demo-overlay__column")
		String gridDemoOverlay__column();

		@ClassName("grid-demo-overlay__content")
		String gridDemoOverlay__content();

		String grid__column();

		@ClassName("grid__column--five-columns")
		String grid__columnFiveColumns();

		@ClassName("grid__column--full-width")
		String grid__columnFullWidth();

		@ClassName("grid__column--one-half")
		String grid__columnOneHalf();

		@ClassName("grid__column--one-quarter")
		String grid__columnOneQuarter();

		@ClassName("grid__column--one-sixth")
		String grid__columnOneSixth();

		@ClassName("grid__column--one-third")
		String grid__columnOneThird();

		@ClassName("grid__column--one-twelfth")
		String grid__columnOneTwelfth();

		@ClassName("grid__column--push-one")
		String grid__columnPushOne();

		@ClassName("grid__column--push-two")
		String grid__columnPushTwo();

		@ClassName("grid__column--right")
		String grid__columnRight();

		@ClassName("grid__column--seven-columns")
		String grid__columnSevenColumns();

		@ClassName("grid__column--three-quarters")
		String grid__columnThreeQuarters();

		@ClassName("grid__column--two-thirds")
		String grid__columnTwoThirds();

		String grid__row();

		@ClassName("grid__row--demo")
		String grid__rowDemo();

		@ClassName("grid__row-container")
		String grid__rowContainer();

		@ClassName("gwt-DatePicker")
		String gwtDatepicker();

		String hamburger();

		String hamburger__button();

		@ClassName("has-child")
		String hasChild();

		@ClassName("has-single-option")
		String hasSingleOption();

		@ClassName("has-transparency")
		String hasTransparency();

		@ClassName("heading-style")
		String headingStyle();

		@ClassName("heading-style--heading-five")
		String headingStyleHeadingFive();

		@ClassName("heading-style--heading-four")
		String headingStyleHeadingFour();

		@ClassName("heading-style--heading-one")
		String headingStyleHeadingOne();

		@ClassName("heading-style--heading-six")
		String headingStyleHeadingSix();

		@ClassName("heading-style--heading-three")
		String headingStyleHeadingThree();

		@ClassName("heading-style--heading-two")
		String headingStyleHeadingTwo();

		@ClassName("hidden-text")
		String hiddenText();

		@ClassName("highcharts-axis-labels")
		String highchartsAxisLabels();

		@ClassName("highcharts-container")
		String highchartsContainer();

		@ClassName("highcharts-tooltip")
		String highchartsTooltip();

		@ClassName("highcharts-xaxis-labels")
		String highchartsXaxisLabels();

		@ClassName("highcharts-yaxis-labels")
		String highchartsYaxisLabels();

		@ClassName("horizontal-centre-content")
		String horizontalCentreContent();

		@ClassName("html-face")
		String htmlFace();

		@ClassName("iap-column")
		String iapColumn();

		@ClassName("icon-dollar")
		String iconDollar();

		String ie8();

		String ie9();

		@ClassName("illustration-container")
		String illustrationContainer();

		@ClassName("img-rounded")
		String imgRounded();

		@ClassName("inline-search-form")
		String inlineSearchForm();

		@ClassName("input-hint")
		String inputHint();

		@ClassName("interactions-page")
		String interactionsPage();

		@ClassName("is-activated")
		String isActivated();

		@ClassName("is-active")
		String isActive();

		@ClassName("is-animating-in")
		String isAnimatingIn();

		@ClassName("is-animating-out")
		String isAnimatingOut();

		@ClassName("is-ascending")
		String isAscending();

		@ClassName("is-blurred-heavy")
		String isBlurredHeavy();

		@ClassName("is-chrome")
		String isChrome();

		@ClassName("is-closed")
		String isClosed();

		@ClassName("is-current")
		String isCurrent();

		@ClassName("is-descending")
		String isDescending();

		@ClassName("is-focused")
		String isFocused();

		@ClassName("is-highlighted")
		String isHighlighted();

		@ClassName("is-ie")
		String isIe();

		@ClassName("is-impressive")
		String isImpressive();

		@ClassName("is-ok")
		String isOk();

		@ClassName("is-on")
		String isOn();

		@ClassName("is-open")
		String isOpen();

		@ClassName("is-opera")
		String isOpera();

		@ClassName("is-pathetic")
		String isPathetic();

		@ClassName("is-selected")
		String isSelected();

		@ClassName("is-showing")
		String isShowing();

		@ClassName("is-strong")
		String isStrong();

		@ClassName("js-fade-content")
		String jsFadeContent();

		@ClassName("js-field--select")
		String jsFieldSelect();

		@ClassName("js-rotate-content")
		String jsRotateContent();

		@ClassName("l-full-screen-section")
		String lFullScreenSection();

		@ClassName("l-main")
		String lMain();

		@ClassName("l-page-container")
		String lPageContainer();

		@ClassName("label--hidden-text")
		String labelHiddenText();

		@ClassName("landing-page")
		String landingPage();

		@ClassName("landing-section-contact")
		String landingSectionContact();

		@ClassName("landing-section-contact__address")
		String landingSectionContact__address();

		@ClassName("landing-section-contact__email")
		String landingSectionContact__email();

		@ClassName("landing-section-contact__tel")
		String landingSectionContact__tel();

		String lato();

		@ClassName("lato-bold")
		String latoBold();

		@ClassName("link-log-in")
		String linkLogIn();

		@ClassName("link-log-in-container")
		String linkLogInContainer();

		@ClassName("link-open-search")
		String linkOpenSearch();

		@ClassName("linked-account-date")
		String linkedAccountDate();

		@ClassName("linked-account-delete")
		String linkedAccountDelete();

		@ClassName("linked-account-edit")
		String linkedAccountEdit();

		@ClassName("linked-account-name")
		String linkedAccountName();

		@ClassName("linked-account-store")
		String linkedAccountStore();

		@ClassName("list-item-complete")
		String listItemComplete();

		@ClassName("list-partners")
		String listPartners();

		@ClassName("logged-in-section-content")
		String loggedInSectionContent();

		@ClassName("logged-in-section-main")
		String loggedInSectionMain();

		@ClassName("logged-in-section-main__column-one")
		String loggedInSectionMain__columnOne();

		@ClassName("logged-in-section-main__column-two")
		String loggedInSectionMain__columnTwo();

		@ClassName("login-content")
		String loginContent();

		@ClassName("login-form-is-showing")
		String loginFormIsShowing();

		@ClassName("logo-container")
		String logoContainer();

		@ClassName("logo-reflection-footer")
		String logoReflectionFooter();

		@ClassName("main-footer-links")
		String mainFooterLinks();

		@ClassName("main-navigation")
		String mainNavigation();

		@ClassName("main-navigation--account")
		String mainNavigationAccount();

		@ClassName("main-social-links")
		String mainSocialLinks();

		@ClassName("maintain-after-submit")
		String maintainAfterSubmit();

		@ClassName("make-comment-button")
		String makeCommentButton();

		@ClassName("mobile-width")
		String mobileWidth();

		@ClassName("more-content")
		String moreContent();

		@ClassName("more-link")
		String moreLink();

		@ClassName("no-accordion-content")
		String noAccordionContent();

		@ClassName("no-csstransitions")
		String noCsstransitions();

		@ClassName("no-flexbox")
		String noFlexbox();

		@ClassName("no-js")
		String noJs();

		@ClassName("no-linked-accounts-container")
		String noLinkedAccountsContainer();

		@ClassName("no-opacity")
		String noOpacity();

		@ClassName("no-scroll")
		String noScroll();

		@ClassName("no-touch")
		String noTouch();

		@ClassName("open-sans")
		String openSans();

		@ClassName("open-sans-bold")
		String openSansBold();

		@ClassName("open-sans-semi")
		String openSansSemi();

		@ClassName("or-text")
		String orText();

		@ClassName("overflow-visible")
		String overflowVisible();

		@ClassName("p-style--featured")
		String pStyleFeatured();

		@ClassName("p-style--minor")
		String pStyleMinor();

		@ClassName("page-app")
		String pageApp();

		@ClassName("page-app__header")
		String pageApp__header();

		@ClassName("page-blog")
		String pageBlog();

		@ClassName("page-function-button")
		String pageFunctionButton();

		@ClassName("page-functions")
		String pageFunctions();

		@ClassName("page-leaderboard")
		String pageLeaderboard();

		@ClassName("page-logged-in-landing")
		String pageLoggedInLanding();

		@ClassName("page-my-account")
		String pageMyAccount();

		@ClassName("page-my-apps")
		String pageMyApps();

		@ClassName("page-overlay")
		String pageOverlay();

		@ClassName("page-overlay--confirm")
		String pageOverlayConfirm();

		@ClassName("page-popup")
		String pagePopup();

		@ClassName("page-section")
		String pageSection();

		@ClassName("page-section--with-border")
		String pageSectionWithBorder();

		@ClassName("page-title")
		String pageTitle();

		@ClassName("pages-link")
		String pagesLink();

		String pagination();

		@ClassName("panel-left")
		String panelLeft();

		@ClassName("panel-left-open")
		String panelLeftOpen();

		@ClassName("panel-right")
		String panelRight();

		@ClassName("panel-right-container")
		String panelRightContainer();

		@ClassName("panel-right__overlay")
		String panelRight__overlay();

		@ClassName("password-strength-description")
		String passwordStrengthDescription();

		@ClassName("password-strength-indicator")
		String passwordStrengthIndicator();

		@ClassName("password-strength-indicator-container")
		String passwordStrengthIndicatorContainer();

		@ClassName("phablet-width")
		String phabletWidth();

		@ClassName("plug-left")
		String plugLeft();

		@ClassName("plug-logo")
		String plugLogo();

		@ClassName("plug-logo-container")
		String plugLogoContainer();

		@ClassName("plug-logo-glow")
		String plugLogoGlow();

		@ClassName("plug-logo-part-glow")
		String plugLogoPartGlow();

		@ClassName("plug-right")
		String plugRight();

		String plugs();

		@ClassName("plugs-connected")
		String plugsConnected();

		@ClassName("popup-close")
		String popupClose();

		@ClassName("popup-content")
		String popupContent();

		@ClassName("popup-content--account__heading")
		String popupContentAccount__heading();

		@ClassName("popup-content--delete-account")
		String popupContentDeleteAccount();

		@ClassName("popup-content--link-account")
		String popupContentLinkAccount();

		@ClassName("pre-selected")
		String preSelected();

		@ClassName("price-column")
		String priceColumn();

		String radioButtonLabel();

		String radioButtonLabelDisabled();

		String radioButtonLabelVisible();

		String radioButtonLabelVisibleDisabled();

		@ClassName("rank-column")
		String rankColumn();

		@ClassName("ref-button")
		String refButton();

		@ClassName("ref-button--cta-large")
		String refButtonCtaLarge();

		@ClassName("ref-button--cta-large--wide")
		String refButtonCtaLargeWide();

		@ClassName("ref-button--cta-small")
		String refButtonCtaSmall();

		@ClassName("ref-button--cta-small--wide")
		String refButtonCtaSmallWide();

		@ClassName("ref-button--error")
		String refButtonError();

		@ClassName("ref-button--full-width")
		String refButtonFullWidth();

		@ClassName("ref-button--function-large")
		String refButtonFunctionLarge();

		@ClassName("ref-button--function-large--wide")
		String refButtonFunctionLargeWide();

		@ClassName("ref-button--function-small")
		String refButtonFunctionSmall();

		@ClassName("ref-button--function-small--wide")
		String refButtonFunctionSmallWide();

		@ClassName("ref-button--is-loading")
		String refButtonIsLoading();

		@ClassName("ref-button--link")
		String refButtonLink();

		@ClassName("ref-button--link-large")
		String refButtonLinkLarge();

		@ClassName("ref-button--success")
		String refButtonSuccess();

		@ClassName("ref-button--toggle")
		String refButtonToggle();

		@ClassName("ref-button--with-icon")
		String refButtonWithIcon();

		@ClassName("ref-icon-after")
		String refIconAfter();

		@ClassName("ref-icon-after--angle-down")
		String refIconAfterAngleDown();

		@ClassName("ref-icon-after--angle-left")
		String refIconAfterAngleLeft();

		@ClassName("ref-icon-after--angle-right")
		String refIconAfterAngleRight();

		@ClassName("ref-icon-after--angle-up")
		String refIconAfterAngleUp();

		@ClassName("ref-icon-after--calendar")
		String refIconAfterCalendar();

		@ClassName("ref-icon-after--check")
		String refIconAfterCheck();

		@ClassName("ref-icon-after--close")
		String refIconAfterClose();

		@ClassName("ref-icon-after--dot")
		String refIconAfterDot();

		@ClassName("ref-icon-after--question")
		String refIconAfterQuestion();

		@ClassName("ref-icon-after--triangle-down")
		String refIconAfterTriangleDown();

		@ClassName("ref-icon-before")
		String refIconBefore();

		@ClassName("ref-icon-before--angle-up")
		String refIconBeforeAngleUp();

		@ClassName("ref-icon-before--blog")
		String refIconBeforeBlog();

		@ClassName("ref-icon-before--cancel")
		String refIconBeforeCancel();

		@ClassName("ref-icon-before--check")
		String refIconBeforeCheck();

		@ClassName("ref-icon-before--cloud")
		String refIconBeforeCloud();

		@ClassName("ref-icon-before--cog")
		String refIconBeforeCog();

		@ClassName("ref-icon-before--facebook")
		String refIconBeforeFacebook();

		@ClassName("ref-icon-before--forum")
		String refIconBeforeForum();

		@ClassName("ref-icon-before--hamburger")
		String refIconBeforeHamburger();

		@ClassName("ref-icon-before--help")
		String refIconBeforeHelp();

		@ClassName("ref-icon-before--info")
		String refIconBeforeInfo();

		@ClassName("ref-icon-before--leaderboard")
		String refIconBeforeLeaderboard();

		@ClassName("ref-icon-before--linkedin")
		String refIconBeforeLinkedin();

		@ClassName("ref-icon-before--location")
		String refIconBeforeLocation();

		@ClassName("ref-icon-before--mail")
		String refIconBeforeMail();

		@ClassName("ref-icon-before--minus")
		String refIconBeforeMinus();

		@ClassName("ref-icon-before--my-apps")
		String refIconBeforeMyApps();

		@ClassName("ref-icon-before--my-data")
		String refIconBeforeMyData();

		@ClassName("ref-icon-before--refresh")
		String refIconBeforeRefresh();

		@ClassName("ref-icon-before--revenue")
		String refIconBeforeRevenue();

		@ClassName("ref-icon-before--search")
		String refIconBeforeSearch();

		@ClassName("ref-icon-before--twitter")
		String refIconBeforeTwitter();

		@ClassName("ref-icon-hidden-text")
		String refIconHiddenText();

		@ClassName("reflection-form")
		String reflectionForm();

		@ClassName("reflection-select")
		String reflectionSelect();

		@ClassName("reflection-select--filter")
		String reflectionSelectFilter();

		@ClassName("refresh-button")
		String refreshButton();

		@ClassName("reset-password-form-is-showing")
		String resetPasswordFormIsShowing();

		@ClassName("revenue-column")
		String revenueColumn();

		@ClassName("revenue-date-range")
		String revenueDateRange();

		@ClassName("revenue-percentage")
		String revenuePercentage();

		@ClassName("revenue-summary-value")
		String revenueSummaryValue();

		@ClassName("revenue-value")
		String revenueValue();

		@ClassName("search-button-mobile")
		String searchButtonMobile();

		@ClassName("search-container")
		String searchContainer();

		@ClassName("search-field-no-label")
		String searchFieldNoLabel();

		@ClassName("search-link-container")
		String searchLinkContainer();

		@ClassName("search-results-none-container")
		String searchResultsNoneContainer();

		String search__form();

		@ClassName("search__results-container")
		String search__resultsContainer();

		@ClassName("search__results-section")
		String search__resultsSection();

		@ClassName("search__results-section--developers")
		String search__resultsSectionDevelopers();

		@ClassName("show-reset-password-form")
		String showResetPasswordForm();

		@ClassName("single-date-input")
		String singleDateInput();

		@ClassName("single-date-selector-container")
		String singleDateSelectorContainer();

		@ClassName("site-article")
		String siteArticle();

		@ClassName("site-article--info")
		String siteArticleInfo();

		@ClassName("site-article-full")
		String siteArticleFull();

		@ClassName("site-article__footer")
		String siteArticle__footer();

		@ClassName("site-logo")
		String siteLogo();

		@ClassName("sort-svg")
		String sortSvg();

		@ClassName("success-tick")
		String successTick();

		@ClassName("svg-unhappy")
		String svgUnhappy();

		@ClassName("tab-animate-active")
		String tabAnimateActive();

		@ClassName("tab-border")
		String tabBorder();

		@ClassName("table-app-group")
		String tableAppGroup();

		@ClassName("table-cell-even-row")
		String tableCellEvenRow();

		@ClassName("table-cell-odd-row")
		String tableCellOddRow();

		@ClassName("table-data")
		String tableData();

		@ClassName("table-data-first")
		String tableDataFirst();

		@ClassName("table-linked-accounts")
		String tableLinkedAccounts();

		@ClassName("table-linked-accounts--disabled")
		String tableLinkedAccountsDisabled();

		@ClassName("table-my-apps")
		String tableMyApps();

		@ClassName("table-overall")
		String tableOverall();

		@ClassName("table-revenue")
		String tableRevenue();

		@ClassName("tablet-width")
		String tabletWidth();

		@ClassName("tabs-container")
		String tabsContainer();

		@ClassName("tabs-container--large-tabs")
		String tabsContainerLargeTabs();

		@ClassName("tabs-template")
		String tabsTemplate();

		String tabs__content();

		@ClassName("tabs__content--is-showing")
		String tabs__contentIsShowing();

		@ClassName("tabs__content--is-submitted")
		String tabs__contentIsSubmitted();

		@ClassName("tabs__content-container")
		String tabs__contentContainer();

		String tabs__link();

		String tabs__tab();

		@ClassName("text-muted")
		String textMuted();

		@ClassName("th-first")
		String thFirst();

		String toggle();

		@ClassName("toggle-calendar")
		String toggleCalendar();

		String touch();

		@ClassName("update-linked-accounts-container")
		String updateLinkedAccountsContainer();

		@ClassName("user-company")
		String userCompany();

		@ClassName("user-name")
		String userName();

		@ClassName("vertical-centre-above-phablet")
		String verticalCentreAbovePhablet();

		@ClassName("vertical-centre-content")
		String verticalCentreContent();

		@ClassName("warning-sign")
		String warningSign();

		@ClassName("will-show")
		String willShow();

		@ClassName("window-warning")
		String windowWarning();

		@ClassName("with-icon")
		String withIcon();

	}

	public interface HomePageStyles extends CssResource {
		@ClassName("actions-container")
		String actionsContainer();

		@ClassName("border-radius-point")
		String borderRadiusPoint();

		String browserupgrade();

		@ClassName("centre-content-container")
		String centreContentContainer();

		@ClassName("content-list")
		String contentList();

		String header();

		String header__actions();

		@ClassName("hidden-text")
		String hiddenText();

		@ClassName("icon-angle-right")
		String iconAngleRight();

		@ClassName("img-width-landing-column-thin")
		String imgWidthLandingColumnThin();

		@ClassName("img-width-landing-column-wide")
		String imgWidthLandingColumnWide();

		@ClassName("landing-column-thin")
		String landingColumnThin();

		@ClassName("landing-column-thin__content")
		String landingColumnThin__content();

		@ClassName("landing-column-wide")
		String landingColumnWide();

		@ClassName("landing-column-wide__content")
		String landingColumnWide__content();

		@ClassName("landing-footer")
		String landingFooter();

		@ClassName("landing-footer__copyright")
		String landingFooter__copyright();

		@ClassName("landing-footer__inline-link")
		String landingFooter__inlineLink();

		@ClassName("landing-footer__social-links")
		String landingFooter__socialLinks();

		@ClassName("landing-section-analyse")
		String landingSectionAnalyse();

		@ClassName("landing-section-analyse__column_two")
		String landingSectionAnalyse__column_two();

		@ClassName("landing-section-careers")
		String landingSectionCareers();

		@ClassName("landing-section-contact")
		String landingSectionContact();

		@ClassName("landing-section-contact__address")
		String landingSectionContact__address();

		@ClassName("landing-section-contact__email")
		String landingSectionContact__email();

		@ClassName("landing-section-contact__tel")
		String landingSectionContact__tel();

		@ClassName("landing-section-insights")
		String landingSectionInsights();

		@ClassName("landing-section-insights__column_one")
		String landingSectionInsights__column_one();

		@ClassName("landing-section-main")
		String landingSectionMain();

		@ClassName("landing-section-main-scroll-effect-container")
		String landingSectionMainScrollEffectContainer();

		@ClassName("landing-section-main__column-one")
		String landingSectionMain__columnOne();

		@ClassName("landing-section-main__column-two")
		String landingSectionMain__columnTwo();

		@ClassName("landing-section-main__content")
		String landingSectionMain__content();

		@ClassName("landing-section-map")
		String landingSectionMap();

		@ClassName("landing-section-story")
		String landingSectionStory();

		@ClassName("link-facebook")
		String linkFacebook();

		@ClassName("link-leaderboard")
		String linkLeaderboard();

		@ClassName("link-linkedin")
		String linkLinkedin();

		@ClassName("link-log-in")
		String linkLogIn();

		@ClassName("link-to-page-top")
		String linkToPageTop();

		@ClassName("link-to-page-top__text")
		String linkToPageTop__text();

		@ClassName("link-twitter")
		String linkTwitter();

		@ClassName("list-partners")
		String listPartners();

		@ClassName("logo-ref-imp")
		String logoRefImp();

		@ClassName("logo-ref-imp-container")
		String logoRefImpContainer();

		@ClassName("logo-reflection-footer")
		String logoReflectionFooter();

		@ClassName("no-flexbox")
		String noFlexbox();

		@ClassName("no-opacity")
		String noOpacity();

		@ClassName("no-scroll")
		String noScroll();

		@ClassName("ref-button")
		String refButton();

		@ClassName("ref-button-apply")
		String refButtonApply();

		@ClassName("ref-button-cta-large")
		String refButtonCtaLarge();

		@ClassName("ref-icon-after")
		String refIconAfter();

		@ClassName("ref-icon-before")
		String refIconBefore();

		@ClassName("section-size-shadow-layer")
		String sectionSizeShadowLayer();

		@ClassName("site-logo")
		String siteLogo();

		@ClassName("small-text")
		String smallText();

		String touch();

		@ClassName("vertical-center")
		String verticalCenter();

		@ClassName("window-height-scroll-effect-container")
		String windowHeightScrollEffectContainer();
	}

	public interface ReflectionMainIE8Styles extends CssResource {

	}

	public interface ConfirmationDialogStyles extends CssResource {}

	public interface BlogStyles extends CssResource {}

	public static final Styles STYLES_INSTANCE = GWT.create(Styles.class);

	@Source("preloader.gif")
	@ImageOptions(repeatStyle = RepeatStyle.None)
	ImageResource cellTableLoading();

	@Source("footersprite.png")
	@ImageOptions(repeatStyle = RepeatStyle.None)
	ImageResource footerSprite();

	@Source("unknownsprite.png")
	@ImageOptions(repeatStyle = RepeatStyle.None)
	ImageResource unknownSprite();

	@Source("linkedaccountssprite.png")
	@ImageOptions(repeatStyle = RepeatStyle.None)
	ImageResource linkedAccountSprite();

	@Source("reflection.gss")
	ReflectionStyles reflection();

	@Source("confirmationdialog.gss")
	ConfirmationDialogStyles confirmationDialog();

	@Source("blog.gss")
	ConfirmationDialogStyles blog();

	@Source("homepage.gss")
	HomePageStyles homePageStyle();

	@Source("reflectionMain.gss")
	ReflectionMainStyles reflectionMainStyle();

	@Source("reflectionMainIE8.gss")
	ReflectionMainIE8Styles reflectionMainIE8Style();
}
