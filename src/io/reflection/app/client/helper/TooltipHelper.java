//
//  PopupsHelper.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 3 Jul 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.helper;

/**
 * @author Stefano Capuzzi (capuzzistefano)
 *
 */
public class TooltipHelper {

	/**
	 * To be called in onAttach
	 */
	public static native void initHelperTooltip()/*-{

		if ($wnd.$('.no-touch').length) {
			$wnd.$('.js-tooltip').each(
					function() {
						var $this = $wnd.$(this);
						var tooltipText = $wnd.$(this).data("tooltip");
						var tooltip = $wnd.$('<div>').addClass("tooltip").text(
								tooltipText);
						var topPosition = $this.offset().top;
						var leftPosition = $this.offset().left;
						var componentHeight = $this.innerHeight();
						$this.on("mouseenter", function() {
							$wnd.$('body').append(tooltip);
							var tooltipHeight = tooltip.innerHeight();
							tooltip.hide()
							tooltip.css({
								"top" : topPosition - tooltipHeight - 20,
								"left" : leftPosition
							});
							setTimeout(function() {
								tooltip.fadeIn(200);
							}, 800);
						});
						$this.on("mouseleave", function() {
							tooltip.remove();
						});
					});
		}
	}-*/;

	public static native void initWhatsThisTooltip()/*-{

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
								tooltip.append($wnd.$('<p>').text(
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
							}
						});

	}-*/;

}
