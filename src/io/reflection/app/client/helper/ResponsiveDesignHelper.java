//
//  RespondiveHelper.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 4 May 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.helper;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ScriptElement;

/**
 * @author Stefano Capuzzi (capuzzistefano)
 *
 */
public class ResponsiveDesignHelper {

	public static final ScriptElement scriptResponsiveTable = DOMHelper.getJSScriptFromUrl("js/vendor/responsive-tables.js");

	public static native void nativeRevealContent(Element elem)/*-{
		$wnd.$(elem).on(
				"click",
				function(e) {
					e.preventDefault();
					var $this = $wnd.$(this), openText = $this
							.data('open-text'), closedText = $this
							.data('closed-text');
					$this.toggleClass('is-open');
					$this.next('.reveal-element').slideToggle(150);
					if ($this.text() == closedText) {
						$this.text($this.data('open-text'));
					} else {
						$this.text($this.data('closed-text'));
					}
				});
	}-*/;

	public static native void initTabsResponsive()/*-{

		$wnd.$($wnd).on("redraw", function() {
			updateTabs();
		});

		$wnd.$($wnd).on("resize", function() {
			updateTabs();
		});

		updateTabs = function() {
			var instance = this;
			if ($wnd.$($wnd).width() < 720) {
				$wnd.$(".tabs-to-dropdown").each(
						function(i, element) {
							var tabSwitched = $wnd.$(element).parent('div')
									.hasClass('tabs-to-dropdown-container');
							if (!tabSwitched) {
								turnTabsToDropDown($wnd.$(element));
							} else {
								unsplitTabsToMobileDropDown($wnd.$(element));
								turnTabsToDropDown($wnd.$(element));
							}
						});
			} else if ($wnd.$($wnd).width() > 720) {
				$wnd.$(".tabs-to-dropdown").each(
						function(i, element) {
							var tabSwitched = $wnd.$(element).parent('div')
									.hasClass('tabs-to-dropdown-container');
							if (tabSwitched) {
								unsplitTabsToMobileDropDown($wnd.$(element));
							}
						});
			}
		};

		turnTabsToDropDown = function(original) {
			var activeElement = $wnd.$('<span>');

			original.find('.is-active .tabs__link').each(
					function() {
						$this = $wnd.$(this);
						activeElement.html($this.find('span').html()).addClass(
								'ref-icon-after ref-icon-after--angle-down');
					});
			$container = original.parent('div').addClass(
					'tabs-to-dropdown-container');
			original.hide();
			activeElement.insertBefore(original);

			activeElement.on("click", function() {
				original.slideToggle(300);
				activeElement.toggleClass("is-open");
			});

			original.find(".tabs__tab").not(".is-disabled").on("click",
					function() {
						original.slideUp(200);
						activeElement.html($wnd.$(this).find('span').html());
						activeElement.removeClass("is-open");
					});

			var isIE8 = $wnd.$('.ie8').length;
			if (!isIE8) {
				$wnd.$('.default-tabs-transition .tabs__content-area').css(
						"opacity", 1);
			}
		};

		unsplitTabsToMobileDropDown = function(element) {
			$wnd.$('.tabs-to-dropdown-container').find('ul.tabs-to-dropdown')
					.css("display", "block");
			$wnd.$('.tabs-to-dropdown-container').removeClass(
					'tabs-to-dropdown-container').find('> span').remove();
			element.find(".tabs__tab").unbind("click");
			element.find(".tabs__tab").on("click", function(e) {
				e.preventDefault();
			});
		};

	}-*/;

	public static native void makeTabsResponsive()/*-{
		new updateTabs;
	}-*/;

}
