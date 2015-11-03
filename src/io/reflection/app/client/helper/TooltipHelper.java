//
//  PopupsHelper.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 3 Jul 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.helper;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

/**
 * @author Stefano Capuzzi (capuzzistefano)
 *
 */
public class TooltipHelper {

	public static void updateHelperTooltip() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				TooltipHelper.nativeUpdateHelperTooltip();
			}
		});
	}

	/**
	 * To be called in onAttach
	 */
	private static native void nativeUpdateHelperTooltip()/*-{

		$wnd.$('.touch body').on("click", function(e) { // remove all tooltips on body touch
			if ($wnd.$('.tooltip').length) {
				$wnd.$('.tooltip').remove();
			}
		});

		$wnd.$('.js-tooltip').each(function() {
			var $this = $wnd.$(this);
			var tooltip;
			if ($wnd.$('html.no-touch').length) {
				$this.on("mouseenter", function() {
					if ($this.hasClass("js-tooltip--info")) {
						tooltip = generateTooltip($this, true);
					} else {
						tooltip = generateTooltip($this, false);
					}
				});
				$this.on("mouseleave", function() {
					tooltip.remove();
				});
				$this.on("click", function() {
					if($this.attr("href") == "#") {
						e.preventDefault();
					}
					if (!$this.hasClass("js-tooltip--info")) {
						tooltip.remove();
					}
				});
			} else if ($wnd.$('html.touch').length) {
				$this.on("click", function(e) {
					if (!$this.hasClass("form-field--select")) {
						if ($this.attr("href") != undefined) {
							e.preventDefault();
						}
						if ($this.hasClass("js-tooltip-generated")) {
							tooltip.remove();
							$this.removeClass("js-tooltip-generated");
						} else {
							$this.addClass("js-tooltip-generated");
							setTimeout(function() {
								tooltip = generateTooltip($this, true);
							}, 50); // delay to avoid all tooltip removal on body touch
						}
					}
				});
			}
		});

		var generateTooltip = function($tooltipParent, isInstantTooltip) {
			var $this = $tooltipParent, tooltipText = $tooltipParent
					.data("tooltip");

			var tooltip = $wnd.$('<div>').addClass("tooltip").append(
					$wnd.$('<div>').addClass("tooltip-text").text(tooltipText));
			if ($this.find('.icon-member--standard').length > 0) {
				tooltip.prepend($wnd.$('<span>').addClass(
						"tooltip-feature tooltip-feature--standard").text(
						"MEMBER FEATURE"));
			} else if ($this.find('.icon-member--pro').length > 0) {
				tooltip.prepend($wnd.$('<span>').addClass(
						"tooltip-feature tooltip-feature--pro").text(
						"PREMIUM FEATURE"));
			}
			$wnd.$('body').append(tooltip);
			var topPosition = $this.offset().top;
			var leftPosition = $this.offset().left;
			var tooltipHeight = tooltip.innerHeight();
			var componentHeight = $this.innerHeight();
			tooltip.hide();
			if ($this.hasClass('js-tooltip--right')) {
				var tooltipWidth = tooltip.innerWidth();
				var componentWidth = $this.innerWidth();
				if ($this.hasClass('js-tooltip--right--no-pointer-padding')) {
					leftPosition = (leftPosition + componentWidth - tooltipWidth) + 10;
				} else {
					leftPosition = leftPosition + componentWidth - tooltipWidth;
				}
				tooltip.addClass("tooltip-right");
			}
			tooltip.css({
				"top" : topPosition - tooltipHeight - 20,
				"left" : leftPosition
			});
			if (isInstantTooltip) {
				tooltip.show();
			} else {
				setTimeout(function() {
					tooltip.fadeIn(100);
				}, 700);
			}

			return tooltip;
		}

	}-*/;

	public static void updateWhatsThisTooltip() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				TooltipHelper.nativeUpdateWhatsThisTooltip();
			}
		});
	}

	private static native void nativeUpdateWhatsThisTooltip()/*-{

		$wnd
				.$('.js-whats-this-tooltip')
				.on(
						"click",
						function(e) {
							e.preventDefault();
							var $this = $wnd.$(this);
							if (!($this.hasClass('is-open'))) {
								$this.addClass('is-open');
								var topPosition = $this.offset().top;
								var leftPosition = $this.offset().left;
								var tooltipContainer = $wnd.$('<div>')
										.addClass("whats-this-tooltip-popup");
								if ($this.hasClass('whats-this-tooltip--dark')) {
									tooltipContainer
											.addClass("whats-this-tooltip--dark");
								}
								var tooltip = $wnd.$('<div>').addClass(
										"whats-this-tooltip");
								tooltip.append($wnd.$('<h2>').text(
										"What's This?"));
								tooltip.append($wnd.$('<p>').html(
										$this.data("whatsthis")));
								tooltip.append($wnd.$('<img>').attr("src",
										"images/icon-bulb.png").attr("alt",
										"Bulb icon"));
								tooltipContainer.append(tooltip);
								$wnd.$('body').append(tooltipContainer);
								var tooltipWidth = tooltip.innerWidth();
								var tooltipHeight = tooltip.innerHeight();
								var iconOffset = 10;
								if ($wnd.$($wnd).width() > 480) {
									if ($this.hasClass('position-tooltip-left')) {
										tooltip
												.addClass("position-tooltip-left");
										tooltipContainer.css({
											"top" : topPosition
													- (tooltipHeight / 2)
													+ iconOffset,
											"left" : leftPosition
													- tooltipWidth - 20
										});
									} else if ($this
											.hasClass('position-tooltip-right')) {
										tooltip
												.addClass("position-tooltip-right");
										tooltipContainer.css({
											"top" : topPosition
													- (tooltipHeight / 2)
													+ iconOffset,
											"left" : leftPosition + 38
										});
									} else if ($this
											.hasClass('position-tooltip-top')) {
										tooltip
												.addClass("position-tooltip-top");
										tooltipContainer.css({
											"top" : topPosition - 20
													- tooltipHeight,
											"left" : leftPosition
													- (tooltipWidth / 2)
													+ iconOffset
										});
									} else {
										tooltipContainer.css({
											"top" : topPosition + 40,
											"left" : leftPosition
													- (tooltipWidth / 2) + 8
										});
									}
								} else {
									tooltipContainer.css({
										"top" : topPosition + 38,
										"left" : "50%",
										"margin-left" : -(tooltipWidth / 2)
									});
								}

								setTimeout(function() {
									tooltip.addClass("is-open");
								}, 10);
								$wnd.$('body').on("click", function(e) {
									if (!($wnd.$(e.target).is($this))) {
										tooltipContainer.remove();
										$this.removeClass('is-open');
									}
								});
							} else {
								$wnd.$('.whats-this-tooltip-popup').remove();
								$this.removeClass('is-open');
							}
							$wnd
									.$($wnd)
									.on(
											"resize",
											function() {
												$wnd
														.$(
																'.whats-this-tooltip-popup')
														.remove();
												$wnd
														.$(
																'.js-whats-this-tooltip.is-open')
														.removeClass('is-open');
											});
						});

	}-*/;

}
