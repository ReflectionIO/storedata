var templateVendorCSS = Handlebars.templates['vendorCSS'];
var htmlVendorCSS = templateVendorCSS({});
$("head").append(htmlVendorCSS);

var templateGlobalHeader = Handlebars.templates['globalHeader'];
var htmlGlobalHeader = templateGlobalHeader({});
$("#js-component-import--global-header").html(htmlGlobalHeader);

var templatePanelLeft = Handlebars.templates['panelLeft'];
var htmlPanelLeft = templatePanelLeft({});
$("#js-component-import--panel-left").html(htmlPanelLeft);

var templateAccountContainer = Handlebars.templates['accountContainer'];
var htmlPanelRight = templateAccountContainer({});
$("#js-component-import--account-container").html(htmlPanelRight);

var templateSearchContainer = Handlebars.templates['searchContainer'];
var htmlPanelRight = templateSearchContainer({});
$("#js-component-import--search-container").html(htmlPanelRight);