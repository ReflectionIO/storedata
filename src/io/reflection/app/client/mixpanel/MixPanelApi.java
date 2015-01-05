//
//  MixPanelApi.java
//  storedata
//
//  Created by William Shakour (billy1380) on 28 Nov 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.mixpanel;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayMixed;

/**
 * @author William Shakour (billy1380)
 *
 */
public class MixPanelApi {

	private static MixPanelApi one = null;

	public static MixPanelApi get() {
		if (one == null) {
			inject();
			one = new MixPanelApi();
		}

		return one;
	}

	private static native MixPanelApi inject() /*-{
		(function(f, b) {
		    if (!b.__SV) {
		        var a, e, i, g;
		        $wnd.mixpanel = b;
		        b._i = [];
		        b.init = function(a, e, d) {
		            function f(b, h) {
		                var a = h.split(".");
		                2 == a.length && (b = b[a[0]], h = a[1]);
		                b[h] = function() {
		                    b.push([h].concat(Array.prototype.slice.call(arguments, 0)))
		                }
		            }
		            var c = b;
		            "undefined" !== typeof d ? c = b[d] = [] : d = "mixpanel";
		            c.people = c.people || [];
		            c.toString = function(b) {
		                var a = "mixpanel";
		                "mixpanel" !== d && (a += "." + d);
		                b || (a += " (stub)");
		                return a
		            };
		            c.people.toString = function() {
		                return c.toString(1) + ".people (stub)"
		            };
		            i = "disable track track_pageview track_links track_forms register register_once alias unregister identify name_tag set_config people.set people.set_once people.increment people.append people.track_charge people.clear_charges people.delete_user".split(" ");
		            for (g = 0; g < i.length; g++) f(c, i[g]);
		            b._i.push([a, e, d])
		        };
		        b.__SV = 1.2;
		        a = f.createElement("script");
		        a.type = "text/javascript";
		        a.async = !0;
		        a.src = "//cdn.mxpnl.com/libs/mixpanel-2-latest.min.js";
		        e = f.getElementsByTagName("script")[0];
		        e.parentNode.insertBefore(a, e)
		    }
		})($wnd.document, $wnd.mixpanel || []);
	}-*/;

	public native void init(String token) /*-{
		$wnd.mixpanel.init(token);
	}-*/;

	public native void init(String token, JavaScriptObject config) /*-{
		$wnd.mixpanel.init(token, config);
	}-*/;

	public native void init(String token, JavaScriptObject config, String name) /*-{
		$wnd.mixpanel.init(token, config, name);
	}-*/;

	public native void push(JsArrayMixed item) /*-{
		$wnd.mixpanel.push(item);
	}-*/;

	public native void disable(JsArrayMixed events) /*-{
		$wnd.mixpanel.disable(events);
	}-*/;

	public native void track(String eventName) /*-{
		$wnd.mixpanel.track(eventName);
	}-*/;

	public native void track(String eventName, JavaScriptObject properties) /*-{
		$wnd.mixpanel.track(eventName, properties);
	}-*/;

	public native void trackLinks(String query, String eventNname, JavaScriptObject properties) /*-{
		$wnd.mixpanel.track_links(query, eventName, properties);
	}-*/;

	public native void trackForms(String query, String eventName, JavaScriptObject properties) /*-{
		$wnd.mixpanel.track_forms(query, eventName, properties);
	}-*/;

	public native void register(JavaScriptObject properties) /*-{
		$wnd.mixpanel.register(properties);
	}-*/;
	
	public native void register(JavaScriptObject properties, Number days) /*-{
		$wnd.mixpanel.register(properties, days);
	}-*/;

	public native void registerOnce(JavaScriptObject properties, Number days) /*-{
		$wnd.mixpanel.register_once(properties, days);
	}-*/;

	public native void unregister(String property) /*-{
		$wnd.mixpanel.unregister(property)
	}-*/;

	public native void identify(String uniqueId) /*-{
		$wnd.mixpanel.identify(uniqueId);
	}-*/;

	public native void getDistinctId() /*-{
		$wnd.mixpanel.get_distinct_id();
	}-*/;

	public native void alias(String alias, String original) /*-{
		$wnd.mixpanel.alias(alias, original);
	}-*/;

	public native void setConfig(JavaScriptObject config) /*-{
		$wnd.mixpanel.set_config(config);
	}-*/;

	public native JavaScriptObject getConfig() /*-{
		return $wnd.mixpanel.get_config();
	}-*/;

	public native JavaScriptObject getProperty(String propertyName) /*-{
		return $wnd.mixpanel.get_property(propertyName);
	}-*/;

	public native void peopleSet(String prop, JavaScriptObject to) /*-{
		$wnd.mixpanel.people.set(prop, to);
	}-*/;

	public native void peopleSetOnce(String prop, JavaScriptObject to) /*-{
		$wnd.mixpanel.people.set_once(prop, to);
	}-*/;

	public native void peopleIncrement(String prop, Number by) /*-{
		$wnd.mixpanel.people.increment(prop, by);
	}-*/;

	public native void peopleAppend(String prop, JavaScriptObject value) /*-{
		$wnd.mixpanel.people.append(prop, value);
	}-*/;

	public native void peopleTrackCharge(Number amount, JavaScriptObject properties) /*-{
		$wnd.mixpanel.people.track_charge(amount, properties);
	}-*/;

	public native void peopleClearCharges() /*-{
		$wnd.mixpanel.people.clear_charges();
	}-*/;

	public native void peopleDeleteUser() /*-{
		$wnd.mixpanel.people.delete_user();
	}-*/;

}
