var templateFontDeclarations = Handlebars.templates['fontDeclarations'];
var htmlFontDeclarations = templateFontDeclarations({});
$("head").append(htmlFontDeclarations);

var templateGlobalHeader = Handlebars.templates['globalHeaderLoggedIn'];
var htmlGlobalHeader = templateGlobalHeader({});
$("#js-component-import--global-header").html(htmlGlobalHeader);

var templateAppMenu = Handlebars.templates['appMenu'];
var htmlAppMenu = templateAppMenu({});
$("#js-component-import--panel-left").html(htmlAppMenu);

var templateAccountContainer = Handlebars.templates['accountContainerLoggedIn'];
var htmlPanelRight = templateAccountContainer({});
$("#js-component-import--account-container").html(htmlPanelRight);

var templateSearchContainer = Handlebars.templates['searchContainer'];
var htmlPanelRight = templateSearchContainer({});
$("#js-component-import--search-container").html(htmlPanelRight);