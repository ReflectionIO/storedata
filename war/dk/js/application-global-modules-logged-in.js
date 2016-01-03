var templateGlobalHeader = Handlebars.templates['globalHeaderLoggedIn'];
var htmlGlobalHeader = templateGlobalHeader({});
$("#js-component-import--global-header").html(htmlGlobalHeader);

var templateAppMenu = Handlebars.templates['appMenuLoggedIn'];
var htmlAppMenu = templateAppMenu({});
$("#js-component-import--panel-left").html(htmlAppMenu);

var templateAccountContainer = Handlebars.templates['accountContainerLoggedIn'];
var htmlPanelRight = templateAccountContainer({});
$("#js-component-import--account-container").html(htmlPanelRight);

var templateSearchContainer = Handlebars.templates['searchContainer'];
var htmlPanelRight = templateSearchContainer({});
$("#js-component-import--search-container").html(htmlPanelRight);

var templateGlobalFooter = Handlebars.templates['globalFooter'];
var htmlGlobalFooter = templateGlobalFooter({});
$("#js-component-import--global-footer").html(htmlGlobalFooter);