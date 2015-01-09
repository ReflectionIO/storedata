(function($) {

	function initialise() {
		// Open search functionality
		var searchOpenLink = $('.js-open-search'),
				searchCancelLink = $('.js-close-search');
		if(searchOpenLink.length) {
			searchOpenLink.click(function(e){
				e.preventDefault();
				toggleSearchView();
			});
			searchCancelLink.click(function(e){
				e.preventDefault();
				toggleSearchView();
				$('.js-get-items').blur();
			});
		}

		initSearch();
	};

	function toggleSearchView() {
		$('.search-overlay').toggleClass('is-showing');
		$('.search-container').toggleClass('is-showing');
		$('.l-page-container').toggleClass('is-blurred-heavy');
		$('.panel-left').toggleClass('is-blurred-heavy');
		$('html').toggleClass('no-scroll');
		$('.search__input-search').select();
	}

	function initSearch() {
		// get mock data from file - this will contain results when implemented so shouldn't need JS regex below
		var data;
		$.ajax({
        url: "js/search-data.json",
        async: true,
        dataType: "json",
        success: function (items){
          data = items;
        }
    });
		
		var inputValue,
				$appsList = $('.js-item-results--apps ul'),
				$devList = $('.js-item-results--developers ul');

		// on key up loop through object and search - for implentation, amend to call service to return results in json and display
		$('.js-get-items').keyup(function(){
			searchResultsApps = [];
			searchResultsDevs = [];
			inputValueCaseInsensitiveRegEx = new RegExp($(this).val(), "i");

			// if found add to result array
			for(var i = 0; i < data.items.length; i++) {
				if(data.items[i].name.search(inputValueCaseInsensitiveRegEx) > -1) {
					searchResultsApps.push(data.items[i]);
				}
				if(data.items[i].creatorName.search(inputValueCaseInsensitiveRegEx) > -1) {
					searchResultsDevs.push(data.items[i]);
				}
			}
			
			// output results to screen
			$appsList.empty();
			for(var i = 0; i < searchResultsApps.length; i++) {
				$appsList.append($('<li>').append($('<a>').append($('<img>').attr("src", "" + searchResultsApps[i].smallImage + "")).append($('<span>').text(searchResultsApps[i].name))));
			}

			$devList.empty();
			for(var i = 0; i < searchResultsDevs.length; i++) {
				$devList.append($('<li>').append($('<a>').append($('<span>').text(searchResultsDevs[i].creatorName))));
			}
		});
	};

	$(document).ready(function(){
		initialise();
	});

})(jQuery);