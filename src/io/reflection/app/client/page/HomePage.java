//
//  HomePage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 13 Jan 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.helper.AlertBoxHelper;
import io.reflection.app.client.helper.FormHelper;
import io.reflection.app.client.part.AlertBox;
import io.reflection.app.client.part.AlertBox.AlertBoxType;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.OListElement;
import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ScrollEvent;
import com.google.gwt.user.client.Window.ScrollHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class HomePage extends Page {

	private final int DELTA = (int) (1000.0 / 30.0);

	private static HomePageUiBinder uiBinder = GWT.create(HomePageUiBinder.class);

	interface HomePageUiBinder extends UiBinder<Widget, HomePage> {}

	interface HomePageStyle extends CssResource {
		String hide();
		
		String moveDownALittle();
		
		String moveDown();
	}

	@UiField HomePageStyle style;

	@UiField SpanElement year;

	@UiField DivElement firstPage;
	@UiField DivElement features;

	@UiField Anchor revenueFeature;
	@UiField Anchor leaderboardFeature;
	@UiField Anchor modelFeature;
	@UiField Anchor storesFeature;
	@UiField Anchor searchFeature;
	@UiField Anchor functionalFeature;

	@UiField DivElement contact;
	@UiField TextBox name;
	@UiField HTMLPanel nameGroup;
	@UiField HTMLPanel nameNote;
	private String nameError;

	@UiField TextBox email;
	@UiField HTMLPanel emailGroup;
	@UiField HTMLPanel emailNote;
	private String emailError;

	@UiField TextArea message;
	@UiField HTMLPanel messageGroup;
	@UiField HTMLPanel messageNote;
	private String messageError;

	@UiField Button submit;

	@UiField AlertBox contactAlert;

	@UiField HeadingElement homeHeading;
	@UiField ParagraphElement homeDescription;
	@UiField InlineHyperlink requestInvite;
	
	@UiField DivElement gotoFeaturesContainer;
	@UiField Anchor gotoFeatures;

	@UiField OListElement carouselIndicators;
	@UiField DivElement carouselContainer;
	@UiField Anchor carouselRight;
	@UiField Anchor carouselLeft;
	private Timer scrollTimer;

	@UiField Anchor workWithUs;
	@UiField Anchor getInTouch;
	@UiField Anchor gotoTop;

	private int destinationTop;
	private int selectedCarouselImage = 0;
	private Timer carouselSpinTimer;
	private boolean carouselSpinning = false;

	@SuppressWarnings("deprecation")
	public HomePage() {
		initWidget(uiBinder.createAndBindUi(this));

		name.getElement().setAttribute("placeholder", "Name");
		email.getElement().setAttribute("placeholder", "Email Address");
		message.getElement().setAttribute("placeholder", "Message");

		requestInvite.setTargetHistoryToken(PageType.RegisterPageType.asTargetHistoryToken("requestinvite"));

		setupIntroSequence();

		year.setInnerHTML(Integer.toString(1900 + (new Date()).getYear()));

		// rotate the carousel every 5 of seconds
		(carouselSpinTimer = new Timer() {

			@Override
			public void run() {
				spinCarousel(carouselLeft);
			}

		}).scheduleRepeating(10000);
	}

	private void setupIntroSequence() {
		homeHeading.getStyle().clearDisplay();
		(new Timer() {
			@Override
			public void run() {
				homeHeading.removeClassName(style.hide());
				homeHeading.removeClassName(style.moveDownALittle());
				
				homeDescription.getStyle().clearDisplay();
				(new Timer() {
					@Override
					public void run() {
						homeDescription.removeClassName(style.hide());
						homeDescription.removeClassName(style.moveDown());
						homeDescription.addClassName(style.moveDownALittle());
						requestInvite.setVisible(true);

						(new Timer() {
							@Override
							public void run() {
								requestInvite.removeStyleName(style.hide());
								gotoFeaturesContainer.getStyle().clearDisplay();

								(new Timer() {
									@Override
									public void run() {
										gotoFeaturesContainer.removeClassName(style.hide());
										gotoFeaturesContainer.removeClassName(style.moveDownALittle());
										gotoFeaturesContainer.addClassName(style.moveDown());
									}
								}).schedule(500);
							}
						}).schedule(600);
					}
				}).schedule(600);
			}
		}).schedule(200);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		NavigationController.get().getHeader().getElement().getStyle().setBorderColor("#272733");
		NavigationController.get().getFooter().getElement().getStyle().setHeight(0, Unit.PX);
		NavigationController.get().getPageHolderPanel().getElement().getStyle().setPaddingTop(0, Unit.PX);

		firstPage.getStyle().setHeight(Window.getClientHeight(), Unit.PX);

		register(Window.addWindowScrollHandler(new ScrollHandler() {

			@Override
			public void onWindowScroll(ScrollEvent event) {
				if (event.getScrollTop() > Window.getClientHeight()) {
					NavigationController.get().getHeader().getElement().getStyle().clearBorderColor();
				} else {
					NavigationController.get().getHeader().getElement().getStyle().setBorderColor("#272733");
				}

				if (event.getScrollTop() < 0) {
					PageType.HomePageType.show();
				}
			}
		}));

		register(Window.addResizeHandler(new ResizeHandler() {

			Timer resizeTimer = new Timer() {
				@Override
				public void run() {
					firstPage.getStyle().setHeight(Window.getClientHeight(), Unit.PX);
				}
			};

			@Override
			public void onResize(ResizeEvent event) {
				resizeTimer.cancel();
				resizeTimer.schedule(250);
			}
		}));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onDetach()
	 */
	@Override
	protected void onDetach() {
		if (scrollTimer != null) {
			scrollTimer.cancel();
		}

		super.onDetach();

		NavigationController.get().getHeader().getElement().getStyle().clearBorderColor();
		NavigationController.get().getPageHolderPanel().getElement().getStyle().setPaddingTop(60, Unit.PX);
		NavigationController.get().getFooter().getElement().getStyle().clearHeight();
	}

	@UiHandler({ "gotoFeatures", "workWithUs", "getInTouch", "gotoTop"
	// , "revenueFeature", "leaderboardFeature", "modelFeature", "storesFeature", "searchFeature", "functionalFeature"
	})
	void onClickHandler(ClickEvent e) {
		Object source = e.getSource();
		if (source == gotoFeatures || source == revenueFeature || source == leaderboardFeature || source == modelFeature || source == storesFeature
				|| source == searchFeature || source == functionalFeature) {
			if (scrollTimer == null) {
				createNewScrollTimer();
			} else {
				scrollTimer.cancel();
			}

			destinationTop = features.getAbsoluteTop() - 60;
			scrollTimer.scheduleRepeating(DELTA);
		} else if (source == getInTouch || source == workWithUs) {
			if (scrollTimer == null) {
				createNewScrollTimer();
			} else {
				scrollTimer.cancel();
			}

			destinationTop = contact.getAbsoluteTop();
			scrollTimer.scheduleRepeating(DELTA);
		} else if (source == gotoTop) {
			if (scrollTimer == null) {
				createNewScrollTimer();
			} else {
				scrollTimer.cancel();
			}

			destinationTop = getAbsoluteTop();
			scrollTimer.scheduleRepeating(DELTA);
		}
	}

	@UiHandler({ "carouselLeft", "carouselRight" })
	void onCarouselArrowClickHandler(ClickEvent e) {
		carouselSpinTimer.cancel();
		spinCarousel(e.getSource());
		carouselSpinTimer.scheduleRepeating(10000);
	}

	private void spinCarousel(final Object source) {
		if (!carouselSpinning && (source == carouselLeft || source == carouselRight)) {
			carouselSpinning = true;

			final Element sourceHighlight = DOM.getChild(carouselIndicators, selectedCarouselImage);
			final Element sourceImage = DOM.getChild(carouselContainer, selectedCarouselImage);

			final Element destinationHighlight, destinationImage;

			Element nextHighlight = null;
			Element nextImage = null;

			if (source == carouselLeft) {
				nextHighlight = DOM.getChild(carouselIndicators, selectedCarouselImage + 1);
				nextImage = DOM.getChild(carouselContainer, selectedCarouselImage + 1);

				if (nextHighlight == null) {
					selectedCarouselImage = 0;
				} else {
					selectedCarouselImage++;
				}
			} else if (source == carouselRight) {
				nextHighlight = DOM.getChild(carouselIndicators, selectedCarouselImage - 1);
				nextImage = DOM.getChild(carouselContainer, selectedCarouselImage - 1);

				if (nextHighlight == null) {
					selectedCarouselImage = DOM.getChildCount(carouselIndicators) - 1;
				} else {
					selectedCarouselImage--;
				}
			}

			destinationHighlight = (nextHighlight == null ? DOM.getChild(carouselIndicators, selectedCarouselImage) : nextHighlight);
			destinationImage = (nextImage == null ? DOM.getChild(carouselContainer, selectedCarouselImage) : nextImage);

			if (source == carouselLeft) {
				destinationImage.addClassName("next");

				sourceImage.addClassName("left");
			} else if (source == carouselRight) {
				destinationImage.addClassName("prev");

				sourceImage.addClassName("right");

			}

			// this is done in a timer because applying both styles in one go gives a resulting effect with the animation skipped
			(new Timer() {
				@Override
				public void run() {
					if (source == carouselLeft) {
						destinationImage.addClassName("left");
					} else if (source == carouselRight) {
						destinationImage.addClassName("right");
					}
				}
			}).schedule(100);

			// wait for the duration of the animation then sort out the state
			(new Timer() {
				@Override
				public void run() {

					if (source == carouselLeft) {
						destinationImage.removeClassName("left");
						destinationImage.removeClassName("next");
					} else if (source == carouselRight) {
						destinationImage.removeClassName("right");
						destinationImage.removeClassName("prev");
					}

					destinationImage.addClassName("active");

					destinationHighlight.addClassName("active");

					sourceImage.removeClassName("active");

					if (source == carouselLeft) {
						sourceImage.removeClassName("left");
					} else if (source == carouselRight) {
						sourceImage.removeClassName("right");
					}

					sourceHighlight.removeClassName("active");

					carouselSpinning = false;
				}
			}).schedule(600);
		}
	}

	private void createNewScrollTimer() {
		scrollTimer = new Timer() {

			@Override
			public void run() {
				int top = Window.getScrollTop();

				if (top != destinationTop) {
					int distance = (int) (((double) destinationTop - (double) top) / 3.0);

					if (Math.abs(distance) < 4) {
						Window.scrollTo(0, destinationTop);
					} else {
						Window.scrollTo(0, top + distance);
					}

					// top has not changed due scroll - if so we have probably hit the end of the page
					int newTop = Window.getScrollTop();
					if (newTop == top) {
						destinationTop = top;
					}
				} else {
					Window.scrollTo(0, destinationTop);
					this.cancel();
				}
			}
		};
	}

	@UiHandler("submit")
	void onSubmitClicked(ClickEvent e) {
		contactAlert.setVisible(false);

		if (validate()) {
			clearErrors();

			setEnabled(false);

			RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, "/sendmail");
			builder.setHeader("Content-type", "application/x-www-form-urlencoded");

			StringBuilder sb = new StringBuilder();
			sb.append("action=contact");

			sb.append("&name=");
			sb.append(name.getValue());

			sb.append("&email=");
			sb.append(email.getValue());

			sb.append("&message=");
			sb.append(message.getValue());

			try {
				AlertBoxHelper.configureAlert(contactAlert, AlertBoxType.InfoAlertBoxType, true, "Sending: ", "Sending message to Reflection.io...", false)
						.setVisible(true);

				/* Request response = */builder.sendRequest(sb.toString(), new RequestCallback() {

					public void onError(Request request, Throwable exception) {
						AlertBoxHelper.configureAlert(contactAlert, AlertBoxType.DangerAlertBoxType, false, "Error: ",
								"An error occured while sending the message!", true).setVisible(true);

						setEnabled(true);
					}

					public void onResponseReceived(Request request, Response response) {
						if (response.getText() != null && response.getText().getBytes().length > 0 && response.getText().getBytes()[0] == 1) {
							AlertBoxHelper.configureAlert(contactAlert, AlertBoxType.SuccessAlertBoxType, false, "Thanks: ", "We will get back to you soon.",
									true).setVisible(true);

							(new Timer() {
								@Override
								public void run() {
									contactAlert.setVisible(false);
								}
							}).schedule(2000);

							name.setValue("");
							email.setValue("");
							message.setValue("");
						} else {
							AlertBoxHelper.configureAlert(contactAlert, AlertBoxType.DangerAlertBoxType, false, "Error: ",
									"An error occured while sending the message!", true).setVisible(true);
						}

						setEnabled(true);
					}
				});
			} catch (RequestException re) {
				AlertBoxHelper.configureAlert(contactAlert, AlertBoxType.DangerAlertBoxType, false, "Error: ", "An error occured while sending the message!",
						true).setVisible(true);

				setEnabled(true);
			}

		} else {
			if (nameError != null) {
				FormHelper.showNote(true, nameGroup, nameNote, nameError);
			} else {
				FormHelper.hideNote(nameGroup, nameNote);
			}

			if (emailError != null) {
				FormHelper.showNote(true, emailGroup, emailNote, emailError);
			} else {
				FormHelper.hideNote(emailGroup, emailNote);
			}

			if (messageError != null) {
				FormHelper.showNote(true, messageGroup, messageNote, messageError);
			} else {
				FormHelper.hideNote(messageGroup, messageNote);
			}
		}

	}

	private boolean validate() {
		boolean validated = true;
		// Retrieve fields to validate
		String nameValue = name.getText();
		String emailValue = email.getText();
		String messageValue = message.getText();

		// Check fields constraints
		if (nameValue == null || nameValue.length() == 0) {
			nameError = "Cannot be empty";
			validated = false;
		} else if (nameValue.length() < 2) {
			nameError = "Too short (minimum 2 characters)";
			validated = false;
		} else if (nameValue.length() > 1000) {
			nameError = "Too long (maximum 1000 characters)";
			validated = false;
		} else {
			nameError = null;
			validated = validated && true;
		}

		if (emailValue == null || emailValue.length() == 0) {
			emailError = "Cannot be empty";
			validated = false;
		} else if (emailValue.length() < 6) {
			emailError = "Too short (minimum 6 characters)";
			validated = false;
		} else if (emailValue.length() > 255) {
			emailError = "Too long (maximum 255 characters)";
			validated = false;
		} else if (!FormHelper.isValidEmail(emailValue)) {
			emailError = "Invalid email address";
			validated = false;
		} else {
			emailError = null;
			validated = validated && true;
		}

		if (messageValue == null || messageValue.length() == 0) {
			messageError = "Cannot be empty";
			validated = false;
		} else if (messageValue.length() < 2) {
			messageError = "(minimum 2 characters)";
			validated = false;
		} else if (messageValue.length() > 10000) {
			messageError = "Too long (maximum 10000 characters)";
			validated = false;
		} else {
			messageError = null;
			validated = validated && true;
		}

		return validated;
	}

	public void setEnabled(boolean value) {
		name.setEnabled(value);
		email.setEnabled(value);
		message.setEnabled(value);

		submit.setEnabled(value);
	}

	private void clearErrors() {
		FormHelper.hideNote(nameGroup, nameNote);
		FormHelper.hideNote(emailGroup, emailNote);
		FormHelper.hideNote(messageGroup, messageNote);
	}

}