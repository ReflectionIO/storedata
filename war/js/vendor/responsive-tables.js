function updateTables() {
	if (($(window).width() < 720)) {
		$("table.responsive").each(function(i, element) {
			var tableSwitched = $(element).attr('switched');
			if(!tableSwitched){
				$(element).attr("switched", true);
				splitTable($(element));
			}
		});
	} else if ($(window).width() > 720) {
		$("table.responsive").each(function(i, element) {
			var tableSwitched = $(element).attr('switched');
			if(tableSwitched){
				$(element).removeAttr("switched");
				unsplitTable($(element));
			}
		});
	}
}

function splitTable(original) {
	original.wrap("<div class='table-wrapper' />");

	var copy = original.clone();
	copy.find("td:not(:first-child), th:not(:first-child)").css("display",
			"none");
	copy.removeClass("responsive");

	original.closest(".table-wrapper").append(copy);
	copy.wrap("<div class='pinned' />");
	original.wrap("<div class='scrollable' />");

	setCellHeights(original, copy);
}

function unsplitTable(original) {
	original.closest(".table-wrapper").find(".pinned").remove();
	original.unwrap();
	original.unwrap();
}

function setCellHeights(original, copy) {
	var tr = original.find('tr'), tr_copy = copy.find('tr'), heights = [];

	tr.each(function(index) {
		var self = $(this), tx = self.find('th, td');

		tx.each(function() {
			var height = $(this).outerHeight(true);
			heights[index] = heights[index] || 0;
			if (height > heights[index])
				heights[index] = height;
		});

	});

	tr_copy.each(function(index) {
		$(this).height(heights[index]);
	});
}

$(window).on("redraw", function() {
	updateTables();
});

$(window).on("resize", function() {
	updateTables();
});
