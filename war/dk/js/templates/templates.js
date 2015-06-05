(function() {
  var template = Handlebars.template, templates = Handlebars.templates = Handlebars.templates || {};
templates['accountContainer'] = template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
  return "<!-- <div class=\"panel-right-container js-account-container\"> -->\n	<div class=\"panel-right__overlay\"></div>\n	<div class=\"panel-right js-custom-scrollbar\" data-mcs-theme=\"minimal-dark\">\n		<form class=\"reflection-form forms--dark-theme form--password-reset\">\n			<div class=\"form-field js-rotate-content\">						\n				<input type=\"email\" name=\"password-reset-email\" id=\"password-reset-email\" autocomplete=\"off\" />\n				<label for=\"password-reset-email\">Email</label>\n			</div>\n			<p class=\"js-fade-content\">Fear not, we'll send an email to reset your password to the above address</p>\n			<input type=\"submit\" class=\"ref-button--full-width js-mock-send-reset-password js-rotate-content\" value=\"Send password reset email\" />\n		</form>\n		<form class=\"reflection-form forms--dark-theme form--login\" autocomplete=\"off\">\n			<div class=\"form-field js-rotate-content\">\n				<input type=\"email\" name=\"email\" id=\"email\" autocomplete=\"off\" />\n				<label for=\"email\">Email</label>\n			</div>\n			<div class=\"form-field js-rotate-content\">\n				<input type=\"password\" name=\"password\" id=\"password\" autocomplete=\"off\" />\n				<label for=\"password\">Password</label>\n			</div>\n			<a href=\"#\" class=\"form-link js-fade-content js-mock-show-reset-password\">I've misplaced my password</a>\n			<div class=\"form-field--checkbox-list\">\n				<ul>\n					<li>\n						<input type=\"checkbox\" name=\"rememberSignedIn\" id=\"rememberSignedIn\" />\n						<label for=\"rememberSignedIn\" class=\"checkboxLabel\">Keep me signed in</label>\n						<label for=\"rememberSignedIn\" class=\"checkboxLabelVisible\">Keep me signed in</label>\n					</li>\n				</ul>\n			</div>\n			<input type=\"submit\" class=\"ref-button--full-width js-rotate-content\" value=\"Log in\" />\n		</form>		\n		<div class=\"reflection-form form-submitted-success\">\n			<svg version=\"1.1\" x=\"0px\" y=\"0px\" viewBox=\"0 0 93 32.7\" enable-background=\"new 0 0 93 32.7\" xml:space=\"preserve\" class=email-delivery-animation><path class=email-delivery-animation--email fill=\"#5A5A68\" d=\"M0,8.1v12.1h18.3V8.1H0z M15.1,9.8l-6,3.7l-6-3.7H15.1z M1.7,18.5v-8.4l7.4,4.6l7.4-4.6v8.4H1.7z\"/><g class=email-delivery-animation--van-body><g><path fill=\"none\" stroke=\"#A0A0A9\" stroke-width=\"2.117\" stroke-linecap=\"round\" stroke-linejoin=\"round\" stroke-miterlimit=\"10\" d=\"M87.9,23\"/><polyline fill=\"#29292C\" points=\"39.9,27.2 29.8,27.2 29.8,1 69.2,1 69.2,27.6 \"/><path fill=\"#5A5A68\" d=\"M73.1,19.6h-1.7c-0.3,0-0.5-0.2-0.5-0.5s0.2-0.5,0.5-0.5h1.7c0.3,0,0.5,0.2,0.5,0.5S73.4,19.6,73.1,19.6z\"/><path fill-rule=\"evenodd\" clip-rule=\"evenodd\" fill=\"#5A5A68\" d=\"M42.1,5.8c0,0.3-0.2,0.5-0.5,0.5h-6.3c-0.3,0-0.5-0.2-0.5-0.5l0,0c0-0.3,0.2-0.5,0.5-0.5h6.3C41.9,5.3,42.1,5.6,42.1,5.8L42.1,5.8z\"/><path fill-rule=\"evenodd\" clip-rule=\"evenodd\" fill=\"#5A5A68\" d=\"M43.7,7.4c0,0.3-0.2,0.5-0.5,0.5h-7.9c-0.3,0-0.5-0.2-0.5-0.5l0,0c0-0.3,0.2-0.5,0.5-0.5h7.9C43.5,6.9,43.7,7.1,43.7,7.4L43.7,7.4z\"/><path fill-rule=\"evenodd\" clip-rule=\"evenodd\" fill=\"#5A5A68\" d=\"M40.6,10.6c0,0.3-0.2,0.5-0.5,0.5h-4.8c-0.3,0-0.5-0.2-0.5-0.5l0,0c0-0.3,0.2-0.5,0.5-0.5h4.8C40.3,10.1,40.6,10.3,40.6,10.6L40.6,10.6z\"/><path fill-rule=\"evenodd\" clip-rule=\"evenodd\" fill=\"#5A5A68\" d=\"M42.1,12.2c0,0.3-0.2,0.5-0.5,0.5h-6.3c-0.3,0-0.5-0.2-0.5-0.5l0,0c0-0.3,0.2-0.5,0.5-0.5h6.3C41.9,11.7,42.1,11.9,42.1,12.2L42.1,12.2z\"/><path fill-rule=\"evenodd\" clip-rule=\"evenodd\" fill=\"#5A5A68\" d=\"M42.1,9c0,0.3-0.2,0.5-0.5,0.5h-6.3c-0.3,0-0.5-0.2-0.5-0.5l0,0c0-0.3,0.2-0.5,0.5-0.5h6.3C41.9,8.5,42.1,8.7,42.1,9L42.1,9z\"/><path fill-rule=\"evenodd\" clip-rule=\"evenodd\" fill=\"#5A5A68\" d=\"M43.7,13.8c0,0.3-0.2,0.5-0.5,0.5h-7.9c-0.3,0-0.5-0.2-0.5-0.5l0,0c0-0.3,0.2-0.5,0.5-0.5h7.9C43.5,13.3,43.7,13.5,43.7,13.8L43.7,13.8z\"/></g><g><g><circle fill=\"#29292C\" cx=\"44.6\" cy=\"26.6\" r=\"5.1\"/><circle fill=\"none\" stroke=\"#5A5A68\" stroke-miterlimit=\"10\" cx=\"44.6\" cy=\"26.6\" r=\"2.4\"/></g><g><g><circle fill=\"#29292C\" cx=\"84.1\" cy=\"26.6\" r=\"5.1\"/><path fill=\"#5A5A68\" d=\"M91.6,17.2L91.5,17c-0.2-0.4-1.1-1.7-2.1-1.9l-6.9-1.7L77,8.1L76.8,8c-0.2-0.1-1.5-0.9-2.7-0.9h-3.9V0H28.8v28.2h9.9c0.7,2.6,3.1,4.5,5.9,4.5c2.8,0,5.1-1.9,5.8-4.4h27.8c0.7,2.5,3.1,4.4,5.8,4.4c2.8,0,5.1-1.9,5.8-4.4l1.2,0c0.7-0.1,1.8-0.7,1.8-2.2V21C93,19.4,92.1,18,91.6,17.2z M70.2,11h4.1l3.6,3.9v1.5h-7.6V11z M44.6,30.7c-2.3,0-4.1-1.8-4.1-4.1s1.8-4.1,4.1-4.1s4.1,1.8,4.1,4.1S46.9,30.7,44.6,30.7z M68.2,26.3H50.7c-0.2-3.2-2.8-5.8-6.1-5.8c-3.2,0-5.9,2.5-6.1,5.7h-7.7V2h37.4V26.3z M84.1,30.7c-2.3,0-4.1-1.8-4.1-4.1s1.8-4.1,4.1-4.1s4.1,1.8,4.1,4.1S86.4,30.7,84.1,30.7z M91,26.2c0,0.1,0,0.1,0,0.2h-0.8c-0.2-3.2-2.8-5.8-6.1-5.8c-3.3,0-5.9,2.6-6.1,5.8h-7.8v-9h8.6v-2.9L74.7,10h-4.5V9.1h3.9c0.4,0,1.1,0.3,1.6,0.6l5.7,5.6l7.5,1.8c0.2,0.1,0.6,0.5,0.9,1c0,0.1,0.1,0.2,0.2,0.3C90.3,18.9,91,20,91,21V26.2z\"/></g><circle fill=\"none\" stroke=\"#5A5A68\" stroke-miterlimit=\"10\" cx=\"84.1\" cy=\"26.6\" r=\"2.4\"/></g></g></g></svg>\n\n			<svg version=1.1 xmlns=http://www.w3.org/2000/svg xmlns:xlink=http://www.w3.org/1999/xlink x=0px y=0px viewBox=\"0 0 61 54\" enable-background=\"new 0 0 61 54\" xml:space=preserve class=success-tick><path fill=#1BC79F d=\"M60.6,7.4l-8.1-7.1C52.2,0.1,51.9,0,51.6,0c-0.3,0-0.6,0.2-0.9,0.4L21.3,33.8L10.9,20\n				c-0.2-0.3-0.5-0.4-0.8-0.5c-0.3,0-0.7,0-0.9,0.2l-8.6,6.5c-0.6,0.4-0.7,1.2-0.2,1.7l19.4,25.5c0.2,0.3,0.6,0.5,1,0.5c0,0,0,0,0,0\n				c0.4,0,0.7-0.2,0.9-0.4L60.7,9.2C61.2,8.7,61.1,7.9,60.6,7.4z\"/></svg>\n			<p>We've sent the email, click on the link contained therein to reset your password once it arrives</p>\n		</div>\n		<div class=\"blockquote-container\">\n			<blockquote>\n				<p>We see our customers as invited guests to a party, and we are the hosts. It's our job every day to make every important aspect of the customer experience a little bit better.</p>\n				<footer>\n					<cite>\n						<span>Jeff Bezos</span>\n						Founder, Amazon\n					</cite>\n				</footer>\n			</blockquote>\n		</div>\n	</div>\n<!-- </div> -->";
  },"useData":true});
templates['accountContainerLoggedIn'] = template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
  return "<!-- <div class=\"panel-right-container js-account-container\"> -->\n	<div class=\"panel-right__overlay\"></div>\n	<div class=\"panel-right js-custom-scrollbar\" data-mcs-theme=\"minimal-dark\">\n		<div class=\"account-user-details\">\n			<h2 class=\"user-name\">Jan Molby</h2>\n			<h3 class=\"user-company\">Electronic Arts</h3>\n		</div>\n		<nav class=\"main-navigation main-navigation--account js-main-nav js-account-navigation\" role=\"navigation\">\n			<ul>\n				<li>\n					<a href=\"#\">\n						<svg version=1.1 x=0px y=0px viewBox=\"0 0 32 32\" enable-background=\"new 0 0 32 32\" xml:space=preserve><path id=My_Apps_List fill-rule=evenodd clip-rule=evenodd fill=none stroke=#5A5A68 stroke-width=2 stroke-miterlimit=10 d=\"\n	M11.3,14.3H7c-1.7,0-3-1.3-3-3V7c0-1.7,1.3-3,3-3h4.3c1.7,0,3,1.3,3,3v4.3C14.3,12.9,12.9,14.3,11.3,14.3z M28,11.3V7\n	c0-1.7-1.3-3-3-3h-4.3c-1.7,0-3,1.3-3,3v4.3c0,1.7,1.3,3,3,3H25C26.7,14.3,28,12.9,28,11.3z M28,25v-4.3c0-1.7-1.3-3-3-3h-4.3\n	c-1.7,0-3,1.3-3,3V25c0,1.7,1.3,3,3,3H25C26.7,28,28,26.7,28,25z M14.3,25v-4.3c0-1.7-1.3-3-3-3H7c-1.7,0-3,1.3-3,3V25\n	c0,1.7,1.3,3,3,3h4.3C12.9,28,14.3,26.7,14.3,25z\"/></svg>\n						<span>My Apps</span>\n					</a>\n				</li>\n				<li>\n					<a href=\"#\">\n						<svg version=1.1 x=0px y=0px viewBox=\"0 0 32 32\" enable-background=\"new 0 0 32 32\" xml:space=preserve><path id=Linked_Accounts fill=#5A5A68 d=\"M18,12c-0.3-0.2-0.6-0.2-1-0.2c-0.3,0.1-0.6,0.3-0.8,0.6C16,12.7,16,13,16,13.3\n	c0.1,0.3,0.3,0.6,0.6,0.8c1.2,0.8,1.9,2.1,1.9,3.6c0,1.1-0.4,2.2-1.2,3l-5.1,5.1c-0.8,0.8-1.9,1.2-3,1.2C6.9,27,5,25.1,5,22.8\n	c0-1.1,0.4-2.2,1.2-3l2.4-2.4C8.9,17.1,9,16.8,9,16.4c0-0.3-0.1-0.6-0.4-0.9c-0.2-0.2-0.6-0.4-0.9-0.4c-0.3,0-0.6,0.1-0.9,0.4\n	L4.5,18c-1.3,1.3-2,3-2,4.8c0,3.7,3,6.8,6.8,6.8c1.8,0,3.5-0.7,4.8-2l5.1-5.1c1.3-1.3,2-3,2-4.8C21.1,15.4,19.9,13.3,18,12z\n	 M22.8,2.5c-1.8,0-3.5,0.7-4.8,2l-5.1,5.1c-1.3,1.3-2,3-2,4.8c0,2.3,1.2,4.4,3.1,5.7c0.3,0.2,0.6,0.2,1,0.2c0.3-0.1,0.6-0.3,0.8-0.6\n	c0.2-0.3,0.2-0.6,0.2-1c-0.1-0.3-0.3-0.6-0.6-0.8c-1.2-0.8-1.9-2.1-1.9-3.6c0-1.1,0.4-2.2,1.2-3l5.1-5.1c0.8-0.8,1.9-1.2,3-1.2\n	C25.1,5,27,6.9,27,9.2c0,1.1-0.4,2.2-1.2,3l-2.4,2.4c-0.3,0.3-0.4,0.6-0.4,0.9c0,0.3,0.1,0.6,0.4,0.9c0.2,0.2,0.6,0.4,0.9,0.4\n	c0.3,0,0.6-0.1,0.9-0.4l2.4-2.4c1.3-1.3,2-3,2-4.8C29.5,5.5,26.5,2.5,22.8,2.5z\"/></svg>\n						<span>Linked Accounts</span>\n					</a>\n				</li>\n				<li>\n					<a href=\"#\">\n						<svg version=1.1 id=Layer_1 xmlns=http://www.w3.org/2000/svg xmlns:xlink=http://www.w3.org/1999/xlink x=0px y=0px viewBox=\"0 0 32 32\" enable-background=\"new 0 0 32 32\" xml:space=preserve><path id=Account_Settings fill=#3B3735 d=\"M16,2c2.6,0,5.2,0.7,5.2,6.2c0,3.4-2.3,6.2-5.2,6.2s-5.2-2.8-5.2-6.2\n	C10.8,2.7,13.5,2,16,2 M16,0c-4,0-7.2,1.9-7.2,8.2c0,4.5,3.2,8.2,7.2,8.2c4,0,7.2-3.7,7.2-8.2C23.2,1.9,20,0,16,0L16,0z M10.3,19.1\n	c1.7,1.1,3.7,1.6,5.7,1.6c2,0,4-0.6,5.7-1.6l4.7,2.4l3.6,7.8c0,0.1,0,0.3,0,0.4c-0.1,0.1-0.2,0.3-0.2,0.3H2.3\n	c-0.1,0-0.1-0.2-0.2-0.3c0-0.1,0-0.3,0-0.4l3.6-7.8L10.3,19.1 M10.4,16.8c-0.1,0-0.1,0-0.2,0l-5.6,2.9c-0.4,0.2-0.7,0.5-0.8,0.9\n	l-3.6,8c-0.2,0.6-0.2,1.7,0.1,2.2C0.7,31.2,1.2,32,1.8,32h28.3c0.6,0,1.2-0.8,1.5-1.3c0.3-0.5,0.4-1.6,0.1-2.2l-3.6-8\n	c-0.2-0.4-0.5-0.7-0.8-0.9l-5.6-2.9c-0.1,0-0.1,0-0.2,0c-0.1,0-0.2,0-0.2,0.1c-1.6,1.2-3.5,1.8-5.4,1.8c-1.9,0-3.8-0.6-5.4-1.8\n	C10.5,16.8,10.5,16.8,10.4,16.8L10.4,16.8z\"/></svg>\n						<span>Account Settings</span>\n					</a>\n				</li>\n				<li>\n					<a href=\"#\">\n						<svg version=1.1 id=Layer_1 xmlns=http://www.w3.org/2000/svg xmlns:xlink=http://www.w3.org/1999/xlink x=0px y=0px viewBox=\"0 0 32 32\" enable-background=\"new 0 0 32 32\" xml:space=preserve><path id=Sign_Out fill=#3B3735 d=\"M22,21v7c0,2.2-1.8,4-4,4H4c-2.2,0-4-1.8-4-4V4c0-2.2,1.8-4,4-4h14c2.2,0,4,1.8,4,4v7h-2V4\n	c0-1.1-0.9-2-2-2H4C2.9,2,2,2.9,2,4v24c0,1.1,0.9,2,2,2h14c1.1,0,2-0.9,2-2v-7H22z M12,17h15.5l-1.8,1.9c-0.4,0.4-0.4,1.2,0,1.6\n	c0.4,0.4,1.1,0.5,1.6,0l3.1-3.1C30.8,17,31,16.5,31,16c0-0.5-0.2-0.9-0.5-1.3l-3.1-3.1c-0.4-0.4-1.1-0.4-1.6,0\n	c-0.4,0.4-0.4,1.2,0,1.6l1.8,1.8H12\"/></svg>\n						<span>Sign Out</span>\n					</a>\n				</li>\n			</ul>\n	</nav>\n	</div>\n<!-- </div> -->";
  },"useData":true});
templates['appMenu'] = template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
  return "<!-- <div class=\"panel-left\"> -->\n	<nav class=\"main-navigation js-main-nav js-main-navigation\" role=\"navigation\">\n		<ul>\n			<li>\n				<a href=\"#\">\n					<svg version=1.1 id=Layer_1 xmlns=http://www.w3.org/2000/svg xmlns:xlink=http://www.w3.org/1999/xlink x=0px y=0px viewBox=\"0 0 32 32\" enable-background=\"new 0 0 32 32\" xml:space=preserve><path id=Leaderboard fill=#3B3735 d=\"M0,2v28h32V2H0z M30,4v5h-8h-2h-8h-2H2V4H30z M20,11v17h-8V11H20z M2,11h8v17H2V11z M22,28\n	V11h8v17H22z\"/></svg>\n					<span>Leaderboard</span>\n				</a>\n			</li>\n			<li>\n				<a href=\"#\">\n					<svg version=1.1 id=Layer_1 xmlns=http://www.w3.org/2000/svg xmlns:xlink=http://www.w3.org/1999/xlink x=0px y=0px viewBox=\"0 0 32 32\" enable-background=\"new 0 0 32 32\" xml:space=preserve><path id=Blog fill=#3B3735 d=\"M28,14.7V26c0,2.2-1.8,4-4,4H4c-2.2,0-4-1.8-4-4V5c0-2.2,1.8-4,4-4h18.8l-2.5,2H4C2.9,3,2,3.9,2,5\n	v21c0,1.1,0.9,2,2,2h20c1.1,0,2-0.9,2-2v-9.7L28,14.7z M30.1,9.2c-1.2,1-14.2,12.3-14.4,12.4l-0.2,0.2l-7.6,0.8l2.1-7.3l0.2-0.2\n	C10.3,15,22.2,4.8,24.6,2.7c0.4-0.3,0.9-0.5,1.5-0.5c1.8,0,3.9,2,4.5,3.9C31.1,7.4,30.9,8.5,30.1,9.2z M28.8,6.7\n	c-0.5-1.3-1.9-2.6-2.7-2.6c-0.1,0-0.2,0-0.2,0.1c-2,1.7-10.6,9.1-13.4,11.6c1.2,0.5,2.5,2.1,2.9,3.4c3.1-2.7,12.4-10.7,13.4-11.5\n	C29,7.5,28.8,6.9,28.8,6.7z\"/></svg>\n					<span>Blog</span>\n				</a>\n			</li>\n			<li>\n				<a href=\"#\">\n					<svg version=1.1 id=Layer_1 xmlns=http://www.w3.org/2000/svg xmlns:xlink=http://www.w3.org/1999/xlink x=0px y=0px viewBox=\"0 0 32 32\" enable-background=\"new 0 0 32 32\" xml:space=preserve><path id=Forum fill=#3B3735 d=\"M7,31.9V24H4c-2.2,0-4-1.8-4-4V4c0-2.2,1.8-4,4-4h24c2.2,0,4,1.8,4,4v16c0,2.2-1.8,4-4,4h-9.7\n	L7,31.9z M4,2C2.9,2,2,2.9,2,4v16c0,1.1,0.9,2,2,2h5v6.1l8.7-6.1H28c1.1,0,2-0.9,2-2V4c0-1.1-0.9-2-2-2H4z\"/></svg>\n					<span>Forum</span>\n				</a>\n			</li>\n			<li class=\"has-child js-is-collapsible\">\n				<a href=\"#\">\n					<svg version=1.1 x=0px y=0px viewBox=\"0 0 32 32\" enable-background=\"new 0 0 32 32\" xml:space=preserve><path id=Admin fill-rule=evenodd clip-rule=evenodd fill=#5A5A68 d=\"M24.4,4.4c0,0.8-0.6,1.4-1.4,1.4H4.4\n	C3.6,5.8,3,5.2,3,4.4l0,0C3,3.6,3.6,3,4.4,3H23C23.7,3,24.4,3.6,24.4,4.4L24.4,4.4z M29,9c0,0.8-0.6,1.4-1.4,1.4H4.4\n	C3.6,10.4,3,9.8,3,9l0,0c0-0.8,0.6-1.4,1.4-1.4h23.2C28.4,7.6,29,8.3,29,9L29,9z M19.7,18.3c0,0.8-0.6,1.4-1.4,1.4H4.4\n	c-0.8,0-1.4-0.6-1.4-1.4l0,0c0-0.8,0.6-1.4,1.4-1.4h13.9C19.1,16.9,19.7,17.6,19.7,18.3L19.7,18.3z M24.4,23c0,0.8-0.6,1.4-1.4,1.4\n	H4.4C3.6,24.4,3,23.7,3,23l0,0c0-0.8,0.6-1.4,1.4-1.4H23C23.7,21.6,24.4,22.2,24.4,23L24.4,23z M24.4,13.7c0,0.8-0.6,1.4-1.4,1.4\n	H4.4c-0.8,0-1.4-0.6-1.4-1.4l0,0c0-0.8,0.6-1.4,1.4-1.4H23C23.7,12.3,24.4,12.9,24.4,13.7L24.4,13.7z M29,27.6\n	c0,0.8-0.6,1.4-1.4,1.4H4.4C3.6,29,3,28.4,3,27.6l0,0c0-0.8,0.6-1.4,1.4-1.4h23.2C28.4,26.2,29,26.8,29,27.6L29,27.6z\"/></svg>\n					<span>Admin</span>\n				</a>\n				<ul>\n					<li><a href=\"feed-browser.html\">Feed Browser</a></li>\n					<li><a href=\"simple-modal-run.html\">Simple Modal Run</a></li>\n					<li><a href=\"items.html\">Items</a></li>\n					<li><a href=\"categories.html\">Categories</a></li>\n					<li><a href=\"users.html\">Users</a></li>\n					<li><a href=\"roles.html\">Roles</a></li>\n					<li><a href=\"permissions.html\">Permissions</a></li>\n					<li><a href=\"data-accounts.html\">Data accounts</a></li>\n					<li><a href=\"data-account-fetches.html\">Data account fetches</a></li>\n					<li><a href=\"events.html\">Events</a></li>\n					<li><a href=\"event-subscriptions.html\">Event Subscriptions</a></li>\n					<li><a href=\"send-notification.html\">Send Notification</a></li>\n					<li><a href=\"blog-admin.html\">Blog Admin</a></li>\n				</ul>\n			</li>\n		</ul>\n	</nav>\n	<ul class=\"main-social-links\">\n		<li><a href=\"https://www.facebook.com/reflection.io\" class=\"ref-icon-before ref-icon-before--facebook\" target=\"_blank\"><span>Facebook</span></a></li>\n		<li><a href=\"https://twitter.com/reflectionio\" class=\"ref-icon-before ref-icon-before--twitter\" target=\"_blank\"><span>Twitter</span></a></li>\n		<li><a href=\"https://www.linkedin.com/company/reflection-io-ltd\" class=\"ref-icon-before ref-icon-before--linkedin\" target=\"_blank\"><span>LinkedIn</span></a></li>\n	</ul>\n	<div class=\"main-footer-links\">\n		<ul>\n			<li><a href=\"#\">Contact Us</a></li>\n			<li><a href=\"#\">Terms &amp; Conditions</a></li>\n		</ul>\n	</div>\n<!-- </div> -->";
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
  return "";
},"useData":true});
templates['globalHeader'] = template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
  return "<!-- <header class=\"global-header\"> -->\n	<div class=\"hamburger\">\n		<button class=\"hamburger__button ref-icon-before ref-icon-before--hamburger is-selected js-hamburger-button\"></button>\n	</div>\n	<div class=\"site-logo\">\n		<a href=\"/\">\n			<picture> <!-- uses js/picturefill.2.3.1.min.js -->\n				<!--[if IE 9]><video style=\"display: none;\"><![endif]-->\n				<source srcset=\"images/logo-reflection-header.png\" media=\"(min-width: 720px)\" />\n				<source srcset=\"images/logo-reflection-header-mobile.png\" />\n				<!--[if IE 9]></video><![endif]-->\n				<img src=\"images/logo-reflection-header.png\" alt=\"Reflection logo\" />\n			</picture>\n		</a>\n	</div>\n	<div class=\"global-header__actions\">\n		<a href=\"#\" class=\"ref-button--cta-small--wide\">Apply</a>\n		<div class=\"search-link-container\">\n			<a href=\"#\" class=\"ref-icon-before ref-icon-before--search link-open-search js-open-search\"><span>Search for app or developer</span></a>\n		</div>\n		<div class=\"actions-group-container\">\n			<div class=\"actions-group ref-icon-before ref-icon-before--cog\">\n				<div class=\"actions-group__content\">\n					<div class=\"link-log-in-container js-link-log-in-container\">\n						<a href=\"#\" class=\"link-log-in js-link-log-in\">Log In</a>\n					</div>\n				</div>\n			</div>\n		</div>\n	</div>\n<!-- </header> -->";
  },"useData":true});
templates['globalHeaderLoggedIn'] = template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
  return "<!-- <header class=\"global-header\"> -->\n	<div class=\"hamburger\">\n		<button class=\"hamburger__button ref-icon-before ref-icon-before--hamburger is-selected js-hamburger-button\"></button>\n	</div>\n	<div class=\"site-logo\">\n		<a href=\"/\">\n			<picture> <!-- uses js/picturefill.2.3.1.min.js -->\n				<!--[if IE 9]><video style=\"display: none;\"><![endif]-->\n				<source srcset=\"images/logo-reflection-header.png\" media=\"(min-width: 720px)\" />\n				<source srcset=\"images/logo-reflection-header-mobile.png\" />\n				<!--[if IE 9]></video><![endif]-->\n				<img src=\"images/logo-reflection-header.png\" alt=\"Reflection logo\" />\n			</picture>\n		</a>\n	</div>\n	<div class=\"global-header__actions\">\n		<div class=\"search-link-container\">\n			<a href=\"#\" class=\"ref-icon-before ref-icon-before--search link-open-search js-open-search\"><span>Search for app or developer</span></a>\n		</div>\n		<div class=\"actions-group-container\">\n			<div class=\"actions-group ref-icon-before ref-icon-before--cog\">\n				<div class=\"actions-group__content\">\n					<div class=\"link-log-in-container js-link-log-in-container\">\n						<a href=\"#\" class=\"link-log-in js-link-log-in\">My Account</a>\n					</div>\n				</div>\n			</div>\n		</div>\n	</div>\n<!-- </header> -->";
  },"useData":true});
templates['panelLeft'] = template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
  return "s<!-- <div class=\"panel-left\"> -->\n	<nav class=\"main-navigation js-main-nav js-main-navigation\" role=\"navigation\">\n		<ul>\n			<li><a href=\"#\">Overview</a></li>\n			<li><a href=\"#\">Brand</a></li>\n			<li class=\"has-child js-is-collapsible\"><a href=\"#\">Design Assets</a>\n				<ul>\n					<li><a href=\"colours.html\">Colours</a></li>\n					<li><a href=\"typography.html\">Typography</a></li>\n					<li class=\"has-child js-is-collapsible\">\n						<a href=\"#\">Menu demo</a>\n						<ul>\n							<li><a href=\"#\">Dolor Amet Lorem</a></li>\n						</ul>\n					</li>\n				</ul>\n			</li>\n			<li><a href=\"grids.html\">Grid &amp; Breakpoints</a></li>\n			<li class=\"has-child js-is-collapsible\">\n				<a href=\"#\">UI Components</a>\n				<ul>\n					<li><a href=\"buttons.html\">Buttons</a></li>\n					<li><a href=\"tabs.html\">Tabs</a></li>\n					<li><a href=\"forms.html\">Form Fields</a></li>\n				</ul>\n			</li>\n			<li class=\"has-child js-is-collapsible\">\n				<a href=\"#\">UI Transitions</a>\n				<ul>\n					<li><a href=\"interactions.html\">Interactions Overview</a></li>\n					<li><a href=\"loading-indicators.html\">Loading Indicators</a></li>\n				</ul>\n			</li>\n			<li class=\"has-child js-is-collapsible\">\n				<a href=\"#\">Page Templates</a>\n				<ul>\n					<li><a href=\"apply.html\">Apply</a></li>\n					<li><a href=\"account-setup.html\">Account Setup</a></li>\n					<li><a href=\"reset-password.html\">Reset Password</a></li>\n					<li><a href=\"leaderboard.html\">Leaderboard</a></li>\n					<li><a href=\"my-apps.html\">My Apps</a></li>\n					<li><a href=\"my-apps-none.html\">My Apps - None</a></li>\n					<li><a href=\"my-apps-coming-soon.html\">My Apps - Coming Soon</a></li>\n					<li><a href=\"blog.html\">Blog</a></li>\n					<li><a href=\"blog-article.html\">Blog Article</a></li>\n					<li><a href=\"general-content.html\">General Content</a></li>\n					<li><a href=\"account-settings.html\">Account Settings</a></li>\n					<li><a href=\"no-linked-accounts.html\">No Linked Account</a></li>\n					<li><a href=\"app.html\">App Page</a></li>\n					<li><a href=\"app-no-data.html\">App No Data</a></li>\n					<li><a href=\"logged-in-landing.html\">Logged-in Landing</a></li>\n					<li><a href=\"404.html\">404 Error</a></li>\n					<li><a href=\"site-maintenance.html\">Site Maintenance</a></li>\n				</ul>\n			</li>\n			<li class=\"has-child js-is-collapsible\">\n				<a href=\"#\">\n					<svg version=1.1 x=0px y=0px viewBox=\"0 0 32 32\" enable-background=\"new 0 0 32 32\" xml:space=preserve><path id=Admin fill-rule=evenodd clip-rule=evenodd fill=#5A5A68 d=\"M24.4,4.4c0,0.8-0.6,1.4-1.4,1.4H4.4\n	C3.6,5.8,3,5.2,3,4.4l0,0C3,3.6,3.6,3,4.4,3H23C23.7,3,24.4,3.6,24.4,4.4L24.4,4.4z M29,9c0,0.8-0.6,1.4-1.4,1.4H4.4\n	C3.6,10.4,3,9.8,3,9l0,0c0-0.8,0.6-1.4,1.4-1.4h23.2C28.4,7.6,29,8.3,29,9L29,9z M19.7,18.3c0,0.8-0.6,1.4-1.4,1.4H4.4\n	c-0.8,0-1.4-0.6-1.4-1.4l0,0c0-0.8,0.6-1.4,1.4-1.4h13.9C19.1,16.9,19.7,17.6,19.7,18.3L19.7,18.3z M24.4,23c0,0.8-0.6,1.4-1.4,1.4\n	H4.4C3.6,24.4,3,23.7,3,23l0,0c0-0.8,0.6-1.4,1.4-1.4H23C23.7,21.6,24.4,22.2,24.4,23L24.4,23z M24.4,13.7c0,0.8-0.6,1.4-1.4,1.4\n	H4.4c-0.8,0-1.4-0.6-1.4-1.4l0,0c0-0.8,0.6-1.4,1.4-1.4H23C23.7,12.3,24.4,12.9,24.4,13.7L24.4,13.7z M29,27.6\n	c0,0.8-0.6,1.4-1.4,1.4H4.4C3.6,29,3,28.4,3,27.6l0,0c0-0.8,0.6-1.4,1.4-1.4h23.2C28.4,26.2,29,26.8,29,27.6L29,27.6z\"/></svg>\n					<span>Admin</span>\n				</a>\n				<ul>\n					<li><a href=\"feed-browser.html\">Feed Browser</a></li>\n					<li><a href=\"simple-modal-run.html\">Simple Modal Run</a></li>\n					<li><a href=\"items.html\">Items</a></li>\n					<li><a href=\"categories.html\">Categories</a></li>\n					<li><a href=\"users.html\">Users</a></li>\n					<li><a href=\"roles.html\">Roles</a></li>\n					<li><a href=\"permissions.html\">Permissions</a></li>\n					<li><a href=\"data-accounts.html\">Data accounts</a></li>\n					<li><a href=\"data-account-fetches.html\">Data account fetches</a></li>\n					<li><a href=\"events.html\">Events</a></li>\n					<li><a href=\"event-subscriptions.html\">Event Subscriptions</a></li>\n					<li><a href=\"send-notification.html\">Send Notification</a></li>\n					<li><a href=\"blog-admin.html\">Blog Admin</a></li>					\n				</ul>\n			</li>\n		</ul>\n	</nav>\n	<ul class=\"main-social-links\">\n		<li><a href=\"#\" class=\"ref-icon-before ref-icon-before--facebook\" onclick=\"return false;\"><span>Facebook</span></a></li>\n		<li><a href=\"#\" class=\"ref-icon-before ref-icon-before--twitter\" onclick=\"return false;\"><span>Twitter</span></a></li>\n		<li><a href=\"#\" class=\"ref-icon-before ref-icon-before--linkedin\" onclick=\"return false;\"><span>LinkedIn</span></a></li>\n	</ul>\n	<div class=\"main-footer-links\">\n		<ul>\n			<li><a href=\"#\">Contact Us</a></li>\n			<li><a href=\"#\">Terms &amp; Conditions</a></li>\n			<li><a href=\"#\">Privacy</a></li>\n			<li><a href=\"#\">Sitemap</a></li>\n		</ul>\n	</div>\n<!-- </div> -->";
  },"useData":true});
templates['searchContainer'] = template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
  return "<!-- <div class=\"panel-right-container js-account-container\"> -->\n	<div class=\"panel-right__overlay\"></div>\n	<div class=\"panel-right search-container\">\n		<form class=\"search__form forms--dark-theme\">\n			<div class=\"form-field\">\n				<input type=\"text\" id=\"search\" name=\"search\" class=\"js-get-items\" autocomplete=\"off\" />\n				<label for=\"search\">Search</label>\n				<input type=\"submit\" class=\"ref-button search-button-mobile\" value=\"a\" />\n			</div>\n			<input type=\"submit\" class=\"ref-button--full-width\" value=\"Show All Results\" />\n		</form>\n		<div class=\"search__results-container\">\n			<div class=\"search__results-section js-item-results--apps js-custom-scrollbar\" data-mcs-theme=\"minimal-dark\">\n				<h2>Apps</h2>\n				<ul>\n					<li><a href=\"#\"><img src=\"images/placeholder-app-thumb-sm1.png\" alt=\"App icon\"/><span>Lorem ipsum dolor</span></a></li>\n					<li><a href=\"#\"><img src=\"images/placeholder-app-thumb-sm2.png\" alt=\"App icon\"/><span>Lorem ipsum dolor</span></a></li>\n					<li><a href=\"#\"><img src=\"images/placeholder-app-thumb-sm3.png\" alt=\"App icon\"/><span>Lorem ipsum dolor</span></a></li>\n					<li><a href=\"#\"><img src=\"images/placeholder-app-thumb-sm4.png\" alt=\"App icon\"/><span>Lorem ipsum dolor</span></a></li>\n					<li><a href=\"#\"><img src=\"images/placeholder-app-thumb-sm5.png\" alt=\"App icon\"/><span>Lorem ipsum dolor</span></a></li>\n					<li><a href=\"#\"><img src=\"images/placeholder-app-thumb-sm6.png\" alt=\"App icon\"/><span>Lorem ipsum dolor</span></a></li>\n				</ul>\n			</div>\n			<div class=\"search__results-section search__results-section--developers js-item-results--developers js-custom-scrollbar\" data-mcs-theme=\"minimal-dark\">\n				<h2>Developers</h2>\n				<ul>\n					<li><a href=\"#\">Lorem ipsum dolor</a></li>\n					<li><a href=\"#\">Lorem ipsum dolor</a></li>\n					<li><a href=\"#\">Lorem ipsum dolor</a></li>\n				</ul>\n			</div>\n			<div class=\"search-results-none-container js-no-results\">\n				<svg version=\"1.1\" class=\"svg-unhappy\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\"\n				 viewBox=\"0 0 32 32\" enable-background=\"new 0 0 32 32\" xml:space=\"preserve\">\n					<path id=\"No_Search_Results_2\" fill=\"#3B3735\" d=\"M16,0C7.2,0,0,7.2,0,16s7.2,16,16,16s16-7.2,16-16S24.8,0,16,0z M21,8\n						c1.1,0,2,1.3,2,3s-0.9,3-2,3s-2-1.3-2-3S19.9,8,21,8z M11,8c1.1,0,2,1.3,2,3s-0.9,3-2,3s-2-1.3-2-3S9.9,8,11,8z M22.4,22.8\n						c-4.1-3.4-8.5-3.4-12.7,0l-1.3-1.5c4.9-4.1,10.4-4.1,15.3,0L22.4,22.8z\"/>\n				</svg>\n				<span>No Results</span>\n			</div>\n		</div>\n	</div>\n</div>";
  },"useData":true});
})();