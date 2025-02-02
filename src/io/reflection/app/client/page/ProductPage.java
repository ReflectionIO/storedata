//
//  ProductPage.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 14 Sep 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import io.reflection.app.client.mixpanel.MixpanelHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ScrollEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Stefano Capuzzi (capuzzistefano)
 *
 */
public class ProductPage extends Page {

	private static ProductPageUiBinder uiBinder = GWT.create(ProductPageUiBinder.class);

	interface ProductPageUiBinder extends UiBinder<Widget, ProductPage> {}

	@UiField Button signUpBtn;
	@UiField Element productAnimationContainerLeaderboard;
	@UiField Element leaderboardAnimationContainer;
	boolean leaderboardIllustrationHasAnimated = false;

	public ProductPage() {
		initWidget(uiBinder.createAndBindUi(this));

		Window.addWindowScrollHandler(new Window.ScrollHandler() {

			@Override
			public void onWindowScroll(ScrollEvent event) {
				if (!leaderboardIllustrationHasAnimated) {
					checkScrollForProductLeaderboardAnimation();
				}
			}

		});
		checkScrollForProductLeaderboardAnimation();
	}

	@UiHandler("signUpBtn")
	void onSignUpClicked(ClickEvent event) {
		event.preventDefault();
		MixpanelHelper.trackClicked(MixpanelHelper.Event.GO_TO_SIGNUP_PAGE, "product_button_signup");
		PageType.RegisterPageType.show();
	}

	private void checkScrollForProductLeaderboardAnimation() {
		if (Window.getScrollTop() > leaderboardAnimationContainer.getOffsetTop() - (Window.getClientHeight() / 2.5)) {
			productAnimationContainerLeaderboard.addClassName("is-showing");
			leaderboardIllustrationHasAnimated = true;
		}
	}
}
