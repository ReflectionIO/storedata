(function() {
  var template = Handlebars.template, templates = Handlebars.templates = Handlebars.templates || {};
templates['accountContainer'] = template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
  return "<!-- <div class=\"panel-right-container js-account-container\"> -->\n	<div class=\"panel-right__overlay\"></div>\n	<div class=\"panel-right js-custom-scrollbar\" data-mcs-theme=\"minimal-dark\">\n		<form class=\"reflection-form forms--dark-theme\" autocomplete=\"off\">\n			<div class=\"form-field\">\n				<input type=\"email\" name=\"email\" id=\"email\" autocomplete=\"off\" />\n				<label for=\"email\">Email</label>\n			</div>\n			<div class=\"form-field\">\n				<input type=\"password\" name=\"password\" id=\"password\" autocomplete=\"off\" />\n				<label for=\"password\">Password</label>\n			</div>\n			<a href=\"#\" class=\"form-link\">I've misplaced my password</a>\n			<div class=\"form-field--checkbox-list\">\n				<ul>\n					<li>\n						<input type=\"checkbox\" name=\"rememberSignedIn\" id=\"rememberSignedIn\" />\n						<label for=\"rememberSignedIn\" class=\"checkboxLabel\">Keep me signed in</label>\n						<label for=\"rememberSignedIn\" class=\"checkboxLabelVisible\">Keep me signed in</label>\n					</li>\n				</ul>\n			</div>\n			<input type=\"submit\" class=\"ref-button--full-width\" value=\"Log in\" />\n		</form>\n		<div class=\"blockquote-container\">\n			<blockquote>\n				<p>We see our customers as invited guests to a party, and we are the hosts. It's our job every day to make every important aspect of the customer experience a little bit better.</p>\n				<footer>\n					<cite>\n						<span>Jeff Bezos</span>\n						Founder, Amazon\n					</cite>\n				</footer>\n			</blockquote>\n		</div>\n	</div>\n<!-- </div> -->";
  },"useData":true});
templates['accountContainerLoggedIn'] = template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
  return "<!-- <div class=\"panel-right-container js-account-container\"> -->\n	<div class=\"panel-right__overlay\"></div>\n	<div class=\"panel-right js-custom-scrollbar\" data-mcs-theme=\"minimal-dark\">\n		<div class=\"account-user-details\">\n			<h2 class=\"user-name\">Jan Molby</h2>\n			<h3 class=\"user-company\">Electronic Arts</h3>\n		</div>\n		<nav class=\"main-navigation main-navigation--account js-main-nav js-account-navigation\" role=\"navigation\">\n			<ul>\n				<li>\n					<a href=\"#\">\n						<svg version=1.1 xmlns=http://www.w3.org/2000/svg xmlns:xlink=http://www.w3.org/1999/xlink x=0px y=0px viewBox=\"0 0 32 32\" enable-background=\"new 0 0 32 32\" xml:space=preserve><path id=My_Apps fill=#3B3735 d=\"M11,1H5C2.8,1,1,2.8,1,5v6c0,2.2,1.8,4,4,4h6c2.2,0,4-1.8,4-4V5C15,2.8,13.2,1,11,1z M13,11\n	c0,1.1-0.9,2-2,2H5c-1.1,0-2-0.9-2-2V5c0-1.1,0.9-2,2-2h6c1.1,0,2,0.9,2,2V11z M27,1h-6c-2.2,0-4,1.8-4,4v6c0,2.2,1.8,4,4,4h6\n	c2.2,0,4-1.8,4-4V5C31,2.8,29.2,1,27,1z M29,11c0,1.1-0.9,2-2,2h-6c-1.1,0-2-0.9-2-2V5c0-1.1,0.9-2,2-2h6c1.1,0,2,0.9,2,2V11z\n	 M27,17h-6c-2.2,0-4,1.8-4,4v6c0,2.2,1.8,4,4,4h6c2.2,0,4-1.8,4-4v-6C31,18.8,29.2,17,27,17z M29,27c0,1.1-0.9,2-2,2h-6\n	c-1.1,0-2-0.9-2-2v-6c0-1.1,0.9-2,2-2h6c1.1,0,2,0.9,2,2V27z M11,17H5c-2.2,0-4,1.8-4,4v6c0,2.2,1.8,4,4,4h6c2.2,0,4-1.8,4-4v-6\n	C15,18.8,13.2,17,11,17z M13,27c0,1.1-0.9,2-2,2H5c-1.1,0-2-0.9-2-2v-6c0-1.1,0.9-2,2-2h6c1.1,0,2,0.9,2,2V27z\"/></svg>\n						<span>My Apps</span>\n					</a>\n				</li>\n				<li>\n					<a href=\"#\">\n						<svg version=1.1 xmlns=http://www.w3.org/2000/svg xmlns:xlink=http://www.w3.org/1999/xlink x=0px y=0px viewBox=\"0 0 32 32\" enable-background=\"new 0 0 32 32\" xml:space=preserve><path id=Linked_Accounts fill=#3B3735 d=\"M18.3,11.3C18,11,17.6,11,17.2,11.1c-0.4,0.1-0.7,0.3-0.9,0.7C16,12.1,16,12.5,16,12.9\n	c0.1,0.4,0.3,0.7,0.7,0.9c1.4,0.9,2.3,2.5,2.3,4.2c0,1.3-0.5,2.6-1.5,3.5l-6,6C10.6,28.5,9.3,29,8,29c-2.8,0-5-2.2-5-5\n	c0-1.3,0.5-2.6,1.5-3.5l2.8-2.8c0.3-0.3,0.4-0.7,0.4-1.1c0-0.4-0.2-0.7-0.4-1c-0.3-0.3-0.7-0.4-1.1-0.4c-0.4,0-0.8,0.1-1.1,0.4\n	l-2.8,2.8C0.8,19.9,0,21.9,0,24c0,4.4,3.6,8,8,8c2.1,0,4.1-0.8,5.7-2.3l6-6c1.5-1.5,2.3-3.5,2.3-5.7C22,15.3,20.6,12.8,18.3,11.3z\n	 M24,0c-2.1,0-4.1,0.8-5.7,2.3l-6,6C10.8,9.9,10,11.9,10,14c0,2.7,1.4,5.3,3.7,6.7c0.4,0.2,0.8,0.3,1.1,0.2c0.4-0.1,0.7-0.3,0.9-0.7\n	c0.2-0.3,0.3-0.8,0.2-1.1c-0.1-0.4-0.3-0.7-0.7-0.9C13.9,17.3,13,15.7,13,14c0-1.3,0.5-2.6,1.5-3.5l6-6C21.4,3.5,22.7,3,24,3\n	c2.8,0,5,2.2,5,5c0,1.3-0.5,2.6-1.5,3.5l-2.8,2.8c-0.3,0.3-0.4,0.7-0.4,1.1c0,0.4,0.2,0.7,0.4,1c0.3,0.3,0.7,0.4,1.1,0.4\n	c0.4,0,0.8-0.1,1.1-0.4l2.8-2.8C31.2,12.2,32,10.1,32,8C32,3.6,28.4,0,24,0z\"/></svg>\n						<span>Linked Accounts</span>\n					</a>\n				</li>\n				<li>\n					<a href=\"#\">\n						<svg version=1.1 id=Layer_1 xmlns=http://www.w3.org/2000/svg xmlns:xlink=http://www.w3.org/1999/xlink x=0px y=0px viewBox=\"0 0 32 32\" enable-background=\"new 0 0 32 32\" xml:space=preserve><path id=Account_Settings fill=#3B3735 d=\"M16,2c2.6,0,5.2,0.7,5.2,6.2c0,3.4-2.3,6.2-5.2,6.2s-5.2-2.8-5.2-6.2\n	C10.8,2.7,13.5,2,16,2 M16,0c-4,0-7.2,1.9-7.2,8.2c0,4.5,3.2,8.2,7.2,8.2c4,0,7.2-3.7,7.2-8.2C23.2,1.9,20,0,16,0L16,0z M10.3,19.1\n	c1.7,1.1,3.7,1.6,5.7,1.6c2,0,4-0.6,5.7-1.6l4.7,2.4l3.6,7.8c0,0.1,0,0.3,0,0.4c-0.1,0.1-0.2,0.3-0.2,0.3H2.3\n	c-0.1,0-0.1-0.2-0.2-0.3c0-0.1,0-0.3,0-0.4l3.6-7.8L10.3,19.1 M10.4,16.8c-0.1,0-0.1,0-0.2,0l-5.6,2.9c-0.4,0.2-0.7,0.5-0.8,0.9\n	l-3.6,8c-0.2,0.6-0.2,1.7,0.1,2.2C0.7,31.2,1.2,32,1.8,32h28.3c0.6,0,1.2-0.8,1.5-1.3c0.3-0.5,0.4-1.6,0.1-2.2l-3.6-8\n	c-0.2-0.4-0.5-0.7-0.8-0.9l-5.6-2.9c-0.1,0-0.1,0-0.2,0c-0.1,0-0.2,0-0.2,0.1c-1.6,1.2-3.5,1.8-5.4,1.8c-1.9,0-3.8-0.6-5.4-1.8\n	C10.5,16.8,10.5,16.8,10.4,16.8L10.4,16.8z\"/></svg>\n						<span>Account Settings</span>\n					</a>\n				</li>\n				<li>\n					<a href=\"#\">\n						<svg version=1.1 id=Layer_1 xmlns=http://www.w3.org/2000/svg xmlns:xlink=http://www.w3.org/1999/xlink x=0px y=0px viewBox=\"0 0 32 32\" enable-background=\"new 0 0 32 32\" xml:space=preserve><path id=Sign_Out fill=#3B3735 d=\"M22,21v7c0,2.2-1.8,4-4,4H4c-2.2,0-4-1.8-4-4V4c0-2.2,1.8-4,4-4h14c2.2,0,4,1.8,4,4v7h-2V4\n	c0-1.1-0.9-2-2-2H4C2.9,2,2,2.9,2,4v24c0,1.1,0.9,2,2,2h14c1.1,0,2-0.9,2-2v-7H22z M12,17h15.5l-1.8,1.9c-0.4,0.4-0.4,1.2,0,1.6\n	c0.4,0.4,1.1,0.5,1.6,0l3.1-3.1C30.8,17,31,16.5,31,16c0-0.5-0.2-0.9-0.5-1.3l-3.1-3.1c-0.4-0.4-1.1-0.4-1.6,0\n	c-0.4,0.4-0.4,1.2,0,1.6l1.8,1.8H12\"/></svg>\n						<span>Sign Out</span>\n					</a>\n				</li>\n			</ul>\n	</nav>\n	</div>\n<!-- </div> -->";
  },"useData":true});
templates['appMenu'] = template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
  return "<!-- <div class=\"panel-left\"> -->\n	<nav class=\"main-navigation js-main-nav js-main-navigation\" role=\"navigation\">\n		<ul>\n			<li>\n				<a href=\"#\">\n					<svg version=1.1 id=Layer_1 xmlns=http://www.w3.org/2000/svg xmlns:xlink=http://www.w3.org/1999/xlink x=0px y=0px viewBox=\"0 0 32 32\" enable-background=\"new 0 0 32 32\" xml:space=preserve><path id=Leaderboard fill=#3B3735 d=\"M0,2v28h32V2H0z M30,4v5h-8h-2h-8h-2H2V4H30z M20,11v17h-8V11H20z M2,11h8v17H2V11z M22,28\n	V11h8v17H22z\"/></svg>\n					<span>Leaderboard</span>\n				</a>\n			</li>\n			<li>\n				<a href=\"#\">\n					<svg version=1.1 id=Layer_1 xmlns=http://www.w3.org/2000/svg xmlns:xlink=http://www.w3.org/1999/xlink x=0px y=0px viewBox=\"0 0 32 32\" enable-background=\"new 0 0 32 32\" xml:space=preserve><path id=Blog fill=#3B3735 d=\"M28,14.7V26c0,2.2-1.8,4-4,4H4c-2.2,0-4-1.8-4-4V5c0-2.2,1.8-4,4-4h18.8l-2.5,2H4C2.9,3,2,3.9,2,5\n	v21c0,1.1,0.9,2,2,2h20c1.1,0,2-0.9,2-2v-9.7L28,14.7z M30.1,9.2c-1.2,1-14.2,12.3-14.4,12.4l-0.2,0.2l-7.6,0.8l2.1-7.3l0.2-0.2\n	C10.3,15,22.2,4.8,24.6,2.7c0.4-0.3,0.9-0.5,1.5-0.5c1.8,0,3.9,2,4.5,3.9C31.1,7.4,30.9,8.5,30.1,9.2z M28.8,6.7\n	c-0.5-1.3-1.9-2.6-2.7-2.6c-0.1,0-0.2,0-0.2,0.1c-2,1.7-10.6,9.1-13.4,11.6c1.2,0.5,2.5,2.1,2.9,3.4c3.1-2.7,12.4-10.7,13.4-11.5\n	C29,7.5,28.8,6.9,28.8,6.7z\"/></svg>\n					<span>Blog</span>\n				</a>\n			</li>\n			<li>\n				<a href=\"#\">\n					<svg version=1.1 id=Layer_1 xmlns=http://www.w3.org/2000/svg xmlns:xlink=http://www.w3.org/1999/xlink x=0px y=0px viewBox=\"0 0 32 32\" enable-background=\"new 0 0 32 32\" xml:space=preserve><path id=Forum fill=#3B3735 d=\"M7,31.9V24H4c-2.2,0-4-1.8-4-4V4c0-2.2,1.8-4,4-4h24c2.2,0,4,1.8,4,4v16c0,2.2-1.8,4-4,4h-9.7\n	L7,31.9z M4,2C2.9,2,2,2.9,2,4v16c0,1.1,0.9,2,2,2h5v6.1l8.7-6.1H28c1.1,0,2-0.9,2-2V4c0-1.1-0.9-2-2-2H4z\"/></svg>\n					<span>Forum</span>\n				</a>\n			</li>\n			<li class=\"has-child js-is-collapsible\">\n				<a href=\"#\">\n					<svg version=1.1 id=Layer_1 xmlns=http://www.w3.org/2000/svg xmlns:xlink=http://www.w3.org/1999/xlink x=0px y=0px viewBox=\"0 0 32 32\" enable-background=\"new 0 0 32 32\" xml:space=preserve><path id=Reflection_R_Icon fill-rule=evenodd clip-rule=evenodd fill=#3B3735 d=\"M25,3.5C25,4.3,24.3,5,23.5,5h-20\n	C2.7,5,2,4.3,2,3.5l0,0C2,2.7,2.7,2,3.5,2h20C24.3,2,25,2.7,25,3.5L25,3.5z M30,8.5c0,0.8-0.7,1.5-1.5,1.5h-25C2.7,10,2,9.3,2,8.5\n	l0,0C2,7.7,2.7,7,3.5,7h25C29.3,7,30,7.7,30,8.5L30,8.5z M20,18.5c0,0.8-0.7,1.5-1.5,1.5h-15C2.7,20,2,19.3,2,18.5l0,0\n	C2,17.7,2.7,17,3.5,17h15C19.3,17,20,17.7,20,18.5L20,18.5z M25,23.5c0,0.8-0.7,1.5-1.5,1.5h-20C2.7,25,2,24.3,2,23.5l0,0\n	C2,22.7,2.7,22,3.5,22h20C24.3,22,25,22.7,25,23.5L25,23.5z M25,13.5c0,0.8-0.7,1.5-1.5,1.5h-20C2.7,15,2,14.3,2,13.5l0,0\n	C2,12.7,2.7,12,3.5,12h20C24.3,12,25,12.7,25,13.5L25,13.5z M30,28.5c0,0.8-0.7,1.5-1.5,1.5h-25C2.7,30,2,29.3,2,28.5l0,0\n	C2,27.7,2.7,27,3.5,27h25C29.3,27,30,27.7,30,28.5L30,28.5z\"/></svg>\n					<span>Admin</span>\n				</a>\n				<ul>\n					<li><a href=\"#\">Feed Browser</a></li>\n					<li><a href=\"#\">Simple Modal Run</a></li>\n					<li><a href=\"#\">Items</a></li>\n					<li><a href=\"#\">Categories</a></li>\n					<li><a href=\"#\">Users</a></li>\n					<li><a href=\"#\">Roles</a></li>\n					<li><a href=\"#\">Permissions</a></li>\n					<li><a href=\"#\">Data accounts</a></li>\n					<li><a href=\"#\">Data account fetches</a></li>\n					<li><a href=\"#\">Events</a></li>\n					<li><a href=\"#\">Event Subscriptions</a></li>\n					<li><a href=\"#\">Send Notification</a></li>\n					<li><a href=\"#\">Blog</a></li>\n				</ul>\n			</li>\n		</ul>\n	</nav>\n	<ul class=\"main-social-links\">\n		<li><a href=\"https://www.facebook.com/reflection.io\" class=\"ref-icon-before ref-icon-before--facebook\" target=\"_blank\"><span>Facebook</span></a></li>\n		<li><a href=\"https://twitter.com/reflectionio\" class=\"ref-icon-before ref-icon-before--twitter\" target=\"_blank\"><span>Twitter</span></a></li>\n		<li><a href=\"https://www.linkedin.com/company/reflection-io-ltd\" class=\"ref-icon-before ref-icon-before--linkedin\" target=\"_blank\"><span>LinkedIn</span></a></li>\n	</ul>\n	<div class=\"main-footer-links\">\n		<ul>\n			<li><a href=\"#\">Contact Us</a></li>\n			<li><a href=\"#\">Terms &amp; Conditions</a></li>\n		</ul>\n	</div>\n<!-- </div> -->";
  },"useData":true});
templates['designKitBanner'] = template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
  var helper, functionType="function", helperMissing=helpers.helperMissing, escapeExpression=this.escapeExpression;
  return "<!-- <div class=\"banner\"> -->\n	<div>\n		<div class=\"banner__graphic\">\n			<img src=\""
    + escapeExpression(((helper = (helper = helpers.bannerImage || (depth0 != null ? depth0.bannerImage : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"bannerImage","hash":{},"data":data}) : helper)))
    + "\" alt=\""
    + escapeExpression(((helper = (helper = helpers.bannerImageAlt || (depth0 != null ? depth0.bannerImageAlt : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"bannerImageAlt","hash":{},"data":data}) : helper)))
    + "\" />\n		</div>\n		<div class=\"banner__intro\">\n			<h1>"
    + escapeExpression(((helper = (helper = helpers.sectionTitle || (depth0 != null ? depth0.sectionTitle : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"sectionTitle","hash":{},"data":data}) : helper)))
    + "</h1>\n			<h2>"
    + escapeExpression(((helper = (helper = helpers.componentTitle || (depth0 != null ? depth0.componentTitle : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"componentTitle","hash":{},"data":data}) : helper)))
    + "</h2>\n			<p>"
    + escapeExpression(((helper = (helper = helpers.intro || (depth0 != null ? depth0.intro : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"intro","hash":{},"data":data}) : helper)))
    + "</p>						\n		</div>\n	</div>\n<!-- </div> -->";
},"useData":true});
templates['fontDeclarations'] = template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
  return "<link href='http://fonts.googleapis.com/css?family=Lato:400,700,400italic' rel='stylesheet' type='text/css'>\n<link href='http://fonts.googleapis.com/css?family=Open+Sans:400italic,600italic,400,600,700' rel='stylesheet' type='text/css'>\n<link href='http://fonts.googleapis.com/css?family=Source+Sans+Pro:600' rel='stylesheet' type='text/css'>";
  },"useData":true});
templates['globalHeader'] = template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
  return "<!-- <header class=\"global-header\"> -->\n	<div class=\"hamburger\">\n		<button class=\"hamburger__button ref-icon-before ref-icon-before--hamburger is-selected js-hamburger-button\"></button>\n	</div>\n	<div class=\"site-logo\">\n		<a href=\"/\">\n			<picture> <!-- uses js/picturefill.2.2.0.min.js -->\n				<!--[if IE 9]><video style=\"display: none;\"><![endif]-->\n				<source srcset=\"images/logo-reflection-header.png\" media=\"(min-width: 720px)\" />\n				<source srcset=\"images/logo-reflection-header-mobile.png\" />\n				<!--[if IE 9]></video><![endif]-->\n				<img src=\"images/logo-reflection-header.png\" alt=\"Reflection logo\" />\n			</picture>\n		</a>\n	</div>\n	<div class=\"global-header__actions\">\n		<a href=\"#\" class=\"ref-button--cta-small--wide\">Apply</a>\n		<div class=\"search-link-container\">\n			<a href=\"#\" class=\"ref-icon-before ref-icon-before--search link-open-search js-open-search\"><span>Search for app or developer</span></a>\n		</div>\n		<div class=\"actions-group-container\">\n			<div class=\"actions-group ref-icon-before ref-icon-before--cog\">\n				<div class=\"actions-group__content\">\n					<div class=\"link-log-in-container js-link-log-in-container\">\n						<a href=\"#\" class=\"link-log-in js-link-log-in\">Log In</a>\n					</div>\n				</div>\n			</div>\n		</div>\n	</div>\n<!-- </header> -->";
  },"useData":true});
templates['globalHeaderLoggedIn'] = template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
  return "<!-- <header class=\"global-header\"> -->\n	<div class=\"hamburger\">\n		<button class=\"hamburger__button ref-icon-before ref-icon-before--hamburger is-selected js-hamburger-button\"></button>\n	</div>\n	<div class=\"site-logo\">\n		<a href=\"/\">\n			<picture> <!-- uses js/picturefill.2.2.0.min.js -->\n				<!--[if IE 9]><video style=\"display: none;\"><![endif]-->\n				<source srcset=\"images/logo-reflection-header.png\" media=\"(min-width: 720px)\" />\n				<source srcset=\"images/logo-reflection-header-mobile.png\" />\n				<!--[if IE 9]></video><![endif]-->\n				<img src=\"images/logo-reflection-header.png\" alt=\"Reflection logo\" />\n			</picture>\n		</a>\n	</div>\n	<div class=\"global-header__actions\">\n		<div class=\"search-link-container\">\n			<a href=\"#\" class=\"ref-icon-before ref-icon-before--search link-open-search js-open-search\"><span>Search for app or developer</span></a>\n		</div>\n		<div class=\"actions-group-container\">\n			<div class=\"actions-group ref-icon-before ref-icon-before--cog\">\n				<div class=\"actions-group__content\">\n					<div class=\"link-log-in-container js-link-log-in-container\">\n						<a href=\"#\" class=\"link-log-in js-link-log-in\">My Account</a>\n					</div>\n				</div>\n			</div>\n		</div>\n	</div>\n<!-- </header> -->";
  },"useData":true});
templates['panelLeft'] = template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
  return "<!-- <div class=\"panel-left\"> -->\n	<nav class=\"main-navigation js-main-nav js-main-navigation\" role=\"navigation\">\n		<ul>\n			<li><a href=\"#\">Overview</a></li>\n			<li><a href=\"#\">Brand</a></li>\n			<li class=\"has-child js-is-collapsible\"><a href=\"#\">Design Assets</a>\n				<ul>\n					<li><a href=\"colours.html\">Colours</a></li>\n					<li><a href=\"typography.html\">Typography</a></li>\n					<li class=\"has-child js-is-collapsible\">\n						<a href=\"#\">Menu demo</a>\n						<ul>\n							<li><a href=\"#\">Dolor Amet Lorem</a></li>\n						</ul>\n					</li>\n				</ul>\n			</li>\n			<li><a href=\"grids.html\">Grid &amp; Breakpoints</a></li>\n			<li class=\"has-child js-is-collapsible\">\n				<a href=\"#\">UI Components</a>\n				<ul>\n					<li><a href=\"buttons.html\">Buttons</a></li>\n					<li><a href=\"tabs.html\">Tabs</a></li>\n					<li><a href=\"forms.html\">Form Fields</a></li>\n				</ul>\n			</li>\n			<li><a href=\"interactions.html\">UI Transitions</a></li>\n			<li class=\"has-child js-is-collapsible\">\n				<a href=\"#\">Page Templates</a>\n				<ul>\n					<li><a href=\"apply.html\">Apply</a></li>\n					<li><a href=\"account-setup.html\">Account Setup</a></li>\n					<li><a href=\"leaderboard.html\">Leaderboard</a></li>\n					<li><a href=\"my-apps.html\">My Apps</a></li>\n					<li><a href=\"blog.html\">Blog</a></li>\n					<li><a href=\"blog-article.html\">Blog Article</a></li>\n				</ul>\n			</li>\n		</ul>\n	</nav>\n	<ul class=\"main-social-links\">\n		<li><a href=\"#\" class=\"ref-icon-before ref-icon-before--facebook\" onclick=\"return false;\"><span>Facebook</span></a></li>\n		<li><a href=\"#\" class=\"ref-icon-before ref-icon-before--twitter\" onclick=\"return false;\"><span>Twitter</span></a></li>\n		<li><a href=\"#\" class=\"ref-icon-before ref-icon-before--linkedin\" onclick=\"return false;\"><span>LinkedIn</span></a></li>\n	</ul>\n	<div class=\"main-footer-links\">\n		<ul>\n			<li><a href=\"#\">Contact Us</a></li>\n			<li><a href=\"#\">Terms &amp; Conditions</a></li>\n			<li><a href=\"#\">Privacy</a></li>\n			<li><a href=\"#\">Sitemap</a></li>\n		</ul>\n	</div>\n<!-- </div> -->";
  },"useData":true});
templates['searchContainer'] = template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
  return "<!-- <div class=\"panel-right-container js-account-container\"> -->\n	<div class=\"panel-right__overlay\"></div>\n	<div class=\"panel-right search-container\">\n		<form class=\"search__form forms--dark-theme\">\n			<div class=\"form-field\">\n				<input type=\"text\" id=\"search\" name=\"search\" class=\"js-get-items\" autocomplete=\"off\" />\n				<label for=\"search\">Search</label>\n				<input type=\"submit\" class=\"ref-button search-button-mobile\" value=\"a\" />\n			</div>\n			<input type=\"submit\" class=\"ref-button--full-width\" value=\"Show All Results\" />\n		</form>\n		<div class=\"search__results-container\">\n			<div class=\"search__results-section js-item-results--apps js-custom-scrollbar\" data-mcs-theme=\"minimal-dark\">\n				<h2>Apps</h2>\n				<ul>\n					<li><a href=\"#\"><img src=\"images/placeholder-app-thumb-sm1.png\" alt=\"App icon\"/><span>Lorem ipsum dolor</span></a></li>\n					<li><a href=\"#\"><img src=\"images/placeholder-app-thumb-sm2.png\" alt=\"App icon\"/><span>Lorem ipsum dolor</span></a></li>\n					<li><a href=\"#\"><img src=\"images/placeholder-app-thumb-sm3.png\" alt=\"App icon\"/><span>Lorem ipsum dolor</span></a></li>\n					<li><a href=\"#\"><img src=\"images/placeholder-app-thumb-sm4.png\" alt=\"App icon\"/><span>Lorem ipsum dolor</span></a></li>\n					<li><a href=\"#\"><img src=\"images/placeholder-app-thumb-sm5.png\" alt=\"App icon\"/><span>Lorem ipsum dolor</span></a></li>\n					<li><a href=\"#\"><img src=\"images/placeholder-app-thumb-sm6.png\" alt=\"App icon\"/><span>Lorem ipsum dolor</span></a></li>\n				</ul>\n			</div>\n			<div class=\"search__results-section search__results-section--developers js-item-results--developers js-custom-scrollbar\" data-mcs-theme=\"minimal-dark\">\n				<h2>Developers</h2>\n				<ul>\n					<li><a href=\"#\">Lorem ipsum dolor</a></li>\n					<li><a href=\"#\">Lorem ipsum dolor</a></li>\n					<li><a href=\"#\">Lorem ipsum dolor</a></li>\n				</ul>\n			</div>\n			<div class=\"search-results-none-container js-no-results\">\n				<svg version=\"1.1\" class=\"svg-unhappy\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\"\n				 viewBox=\"0 0 32 32\" enable-background=\"new 0 0 32 32\" xml:space=\"preserve\">\n					<path id=\"No_Search_Results_2\" fill=\"#3B3735\" d=\"M16,0C7.2,0,0,7.2,0,16s7.2,16,16,16s16-7.2,16-16S24.8,0,16,0z M21,8\n						c1.1,0,2,1.3,2,3s-0.9,3-2,3s-2-1.3-2-3S19.9,8,21,8z M11,8c1.1,0,2,1.3,2,3s-0.9,3-2,3s-2-1.3-2-3S9.9,8,11,8z M22.4,22.8\n						c-4.1-3.4-8.5-3.4-12.7,0l-1.3-1.5c4.9-4.1,10.4-4.1,15.3,0L22.4,22.8z\"/>\n				</svg>\n				<span>No Results</span>\n			</div>\n		</div>\n	</div>\n</div>";
  },"useData":true});
})();