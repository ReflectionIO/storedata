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

	public interface HomePageStyles extends CssResource {
		@ClassName("actions-container")
		String actionsContainer();

		@ClassName("border-radius-point")
		String borderRadiusPoint();

		String browserupgrade();

		@ClassName("centre-content-container")
		String centreContentContainer();

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

		String touch();

		@ClassName("vertical-center")
		String verticalCenter();

		@ClassName("window-height-scroll-effect-container")
		String windowHeightScrollEffectContainer();
	}

	public interface ConfirmationDialogStyles extends CssResource {}

	public interface BlogStyles extends CssResource {}

	public static final Styles INSTANCE = GWT.create(Styles.class);

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

}
