(function($) {

	var templateFontDeclarations = Handlebars.templates['fontDeclarations'];
	var htmlFontDeclarations = templateFontDeclarations({});
	$("head").append(htmlFontDeclarations);

  var templateGlobalHeader = Handlebars.templates['globalHeader'];
	var htmlGlobalHeader = templateGlobalHeader({});
	$("#js-component-import--global-header").html(htmlGlobalHeader);

	var templatePanelLeft = Handlebars.templates['panelLeft'];
	var htmlPanelLeft = templatePanelLeft({});
	$("#js-component-import--panel-left").html(htmlPanelLeft);

	var templatePanelRight = Handlebars.templates['panelRight'];
	var htmlPanelRight = templatePanelRight({});
	$("#js-component-import--panel-right").html(htmlPanelRight);

	var templateSearchOverlay = Handlebars.templates['searchOverlay'];
	var htmlSearchOverlay = templateSearchOverlay({});
	$("#js-component-import--search").html(htmlSearchOverlay);
	
})(jQuery);