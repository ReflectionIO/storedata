// Global variables
var storeCode = "";
var date = new Date().getTime();
var country = "us";
var overviewType = "all";
var maxRows = 30;
var pageStartAll = 0;

var idLookup = { 

};

$(document).ready(function(){

	$('#leaderboard-all').show();
	$('#leaderboard-single').hide();

	createTableRows();
	createTableRowsSingle();

	getTopItemsAll();

	// show the load more button "loading..."
	var btn = $( "#load_more_all" );
	btn.button('loading');

 });

	$('#appstore').change(function(){
    	//alert("rub a dub");
    	//alert($(this).find('option:selected').attr('rel'));
    	//alert($(this).find('option:selected').text());
    	var name = $(this).find('option:selected').text();

    	if (name == "iPhone") {
    		storeCode = "";
    	} else if (name = "iPad") {
    		storeCode = "ipad";
    	}
    	else {
    		storeCode = "";
    	}
    	
    	if (overviewType == "all") {
    		getTopItemsAll();
    	} else {
    		getTopItemsSingle("top"+overviewType+storeCode+"applications","paid");
    	}
    	
	});

    $('#overview').change(function(){
    	
    	var selected = $(this).find('option:selected');
    	var value = selected.attr("value");

    	overviewType = value.split("_")[1];

    	if (value == "overview_all") {
    		$('#leaderboard-all').show();
        	$('#leaderboard-single').hide();
        	getTopItemsAll();
    	} else {
    		$('#leaderboard-all').hide();
        	$('#leaderboard-single').show();

        	

        	getTopItemsSingle("top"+overviewType+storeCode+"applications","paid");
    	}

    	//alert(selected + " " + value);
	});

	$('#country').change(function(){
    	
    	var selected = $(this).find('option:selected');
    	var value = selected.attr("value");

    	//country = value;

    	//getTopItemsAll();

    	//alert(selected + " " + value);
	});


	$('#datepicker').datepicker({
      format: "dd-mm-yyyy",
      endDate: convertDate(new Date()),
      //autoclose: true,
      todayHighlight: true
    });

	$('#datepicker').datepicker().on('changeDate', function(e){
        //alert(e.date.toString());
        //alert(e.date.getTime());
        date = e.date.getTime();
        //alert(date);
        getTopItemsAll();
    });

	// load more entries if you click the "Load more" button
    $( "#load_more_all" ).click(function() {

    	var btn = $(this)
        btn.button('loading')
        //setTimeout(function () {
         //   btn.button('reset')
        //}, 3000)

  		pageStartAll += maxRows;
  		createTableRows();
  		getTopItemsAll();
	});


  // --------------- METHODS ---------------
  function convertDate(d) {
    function pad2(n) {
      return n > 9 ? n : '0' + n;
    }
    var year = d.getUTCFullYear();
    var month = d.getUTCMonth() + 1;  // months start at zero
    var day = d.getUTCDate();

    return pad2(day) + '-' + pad2(month) + '-' + year;
  }

  function getTopItemsAll() {

    getTopItems("toppaid"+storeCode+"applications","paid");
	getTopItems("topfree"+storeCode+"applications","free");
	getTopItems("topgrossing"+storeCode+"applications","grossing");

  }

  function getTopItems(listType, listID) {

		
  //var date;
  //var date = new Date(2010, 6, 26).getTime() / 1000;
  //var date = new Date().getTime() / 1000;
  //var date = 1373041735000; //1375657200000
  //date = new Date(2013, 6, 5).getTime(); // year, month, day:  note: month starts from zero 
  //date = new Date(2013, 6, 18).getTime();
   //var date = new Date().getTime();
	var pager =	"{'count':"+maxRows+",'start':"+pageStartAll+"}";
	//var pager = "{'count':"+maxRows+",'start':30}";
   var requestString = "{'accessCode':'b72b4e32-1062-4cc7-bc6b-52498ee10f09','country':{'a2Code':"+country+"},'listType':"+listType+",'on':"+date+",'store':{'a3Code':'ios'},'pager':"+pager+"}";
   
   //alert(requestString);
   $.ajax({
            type: "POST",
            url: "../core",
            data: { action: "GetTopItems", request: requestString },
            //contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function(data) {

            	if (data.ranks != null) {
            		
            		var idLookupString = "";

            		var len = data.items.length;

            		var currentLookup = new Object();
            		
            		$.each(data.items, function(index, item){      
            			// create the lookup table for ids
            			currentLookup[item.externalId] = item.internalId;     
            			
            			idLookupString += "%22" + item.externalId + "%22";

            			if (index < len - 1) {
            				idLookupString += ",";
            			};
            		});

            		if (listType == "toppaidapplications") {
            			console.log(data);
            		}

            		// update the lookup table for the current list type (e.g. paid, free, grossing)
            		idLookup[listID] = currentLookup;

            		//updateTable(data.ranks, listType, listID);
            		lookupApplications(idLookupString, data.ranks, listType, listID);
            	} else {
            		// only display the error message once (this function is called 3 times)
            		if (listType == "toppaidapplications") {
            			alert("No data found for this date - try 5th or 10th of September");
            		};
            		
            	}
            }
    });
}

function getTopItemsSingle(listType, listID) {
  	var requestString = "{'accessCode':'b72b4e32-1062-4cc7-bc6b-52498ee10f09','country':{'a2Code':"+country+"},'listType':"+listType+",'on':"+date+",'store':{'a3Code':'ios'},'pager':{'count':30}}";
   //alert(requestString);
   $.ajax({
            type: "POST",
            url: "../core",
            data: { action: "GetTopItems", request: requestString },
            //contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function(data) {

            	if (data.ranks != null) {
            		//alert(data.ranks.length);

            		/*
            		$.each(data.ranks, function(index, item){           
            			item.date = new Date(item.date);
            			//item.position++;
            			//console.log(item.itemId);
            		});
					*/

            		updateTableSingle(data.ranks, listType, listID);
            	} else {
            		
            		alert("No data found for this date - try 5th or 10th of September");
            		
            	}
            }
    });
  }

  function lookupApplications(lookupList, chartdata, listType, listID) {

  	var requestString = "{'accessCode':'b72b4e32-1062-4cc7-bc6b-52498ee10f09','externalIds':["+lookupList+"],'detail':'short'}";
  	//var requestString = '{"accessCode":"b72b4e32-1062-4cc7-bc6b-52498ee10f09","externalIds":["com.rovio.AngryBirdsHalloween"],"detail":"short"}';

  	// reset the "Load more" button when the data has loaded
	var btn = $( "#load_more_all" );
	btn.button('reset');

   $.ajax({
            type: "POST",
            url: "../lookup",
            data: { action: "LookupApplication", request: requestString },
            //contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function(data) {

            	updateTable(chartdata, listType, listID, data.applications);
            }

            // reset the "Load more" button
            //$( "#load_more_all" ).button('reset');

            
            
    });
  }

function createTableRows() {

		var startPos = pageStartAll;
		var endPos = startPos + maxRows;
      // create the required number of rows
      for (var i = startPos; i < endPos; i++) {
        $('#table-charts > tbody:last').append('<tr id=posall'+i+'></tr>');
      };

      // create the numbers
      for (var i = startPos; i < endPos; i++) {
        $('#posall'+i).append('<td class="game-rank">'+(i+1)+'</td>');
      };

      // add to the paid charts
      for (var i = startPos; i < endPos; i++) {
        $('#posall'+i).append('<td id="paid'+i+'"><img class="game-icon img-rounded" src="../assets/img/icon_placeholder.png"><div><span class="game-name">Name<br></span><span class="game-publisher">Publisher</span></div></td>');
      };
      
      // add to the free charts
      for (var i = startPos; i < endPos; i++) {
        //$('#pos'+i).append('<td id="topfreeapplications'+i+'">blank</td>');
        $('#posall'+i).append('<td id="free'+i+'"><img class="game-icon img-rounded" src="../assets/img/icon_placeholder.png"><div><span class="game-name">Name<br></span><span class="game-publisher">Publisher</span></div></td>');

      };

      // add to the top grossing charts
      for (var i = startPos; i < endPos; i++) {
        //$('#pos'+i).append('<td id="topgrossingapplications'+i+'">blank</td>');
        $('#posall'+i).append('<td id="grossing'+i+'"><img class="game-icon img-rounded" src="../assets/img/icon_placeholder.png"><div><span class="game-name">Name<br></span><span class="game-publisher">Publisher</span></div></td>');

      };

  }

  function createTableRowsSingle() {

      // create the required number of rows
      for (var i = 0; i < maxRows; i++) {
        $('#table-detail > tbody:last').append('<tr id=possingle'+i+'></tr>');
      };

      // create the numbers
      for (var i = 0; i < maxRows; i++) {
        $('#possingle'+i).append('<td class="game-rank">'+(i+1)+'</td>');
      };

      // add to the info column
      for (var i = 0; i < maxRows; i++) {
        $('#possingle'+i).append('<td id="info'+i+'"><img class="game-icon2" src="../assets/img/icon_placeholder.png"><div><span class="game-name2">Name<br></span><span class="game-publisher2">Publisher</span></div></td>');
      };

      // add to the price column
      for (var i = 0; i < maxRows; i++) {
        //$('#pos'+i).append('<td id="topfreeapplications'+i+'">blank</td>');
        $('#possingle'+i).append('<td id="price'+i+'">$0.99</td>');

      };
      
      // add to the downloads column
      for (var i = 0; i < maxRows; i++) {
        //$('#pos'+i).append('<td id="topfreeapplications'+i+'">blank</td>');
        $('#possingle'+i).append('<td id="downloads'+i+'">92150</td>');

      };

      // add to the revenue column
      for (var i = 0; i < maxRows; i++) {
        //$('#pos'+i).append('<td id="topgrossingapplications'+i+'">blank</td>');
        $('#possingle'+i).append('<td id="revenue'+i+'">$60000</td>');

      };

      // add to the IAP column
      for (var i = 0; i < maxRows; i++) {
        //$('#pos'+i).append('<td id="topgrossingapplications'+i+'">blank</td>');
        $('#possingle'+i).append('<td id="iap'+i+'">Yes</td>');

      };
  }

  function updateTable(chartdata, listType, listID, appdata) {

  		var lenAppData = appdata.length;

  		var startPos = 0;
		var endPos = startPos + chartdata.length;

		var rowPos = pageStartAll;

		console.log("endPos = " + endPos);

      for (var i = startPos; i < endPos; i++) {
      	/*<td><img class="game-icon" src="../assets/img/icon_placeholder.png">
              <div>
                <span class="game-text">Terraria<br></span>
                <span class="game-text">505 Games</span>
              </div>
            </td>*/
        //$('td#'+listID+i).replaceWith('<td id="'+listID+i+'"><a href="rankings.html?itemId='+chartdata[i].itemId+'&type='+listID+'&country='+country+'">'+chartdata[i].itemId+'</a></td>');
      	//$('td#'+listID+i).html('Whatever <b>HTML</b> you want here.');

      	// grab the internal id for this product to get it's full name from the appdata object
      	var appInfo = null;

      	var currentLookup = idLookup[listID];
      	var internalId = currentLookup[chartdata[i].itemId];
      	

      	for (var j = 0; j < lenAppData; j++) {
      		if (internalId == appdata[j].id) {
      			appInfo = appdata[j];
      			break;
      		}
      			
      	}
      	

      	// replace the icon
      	// e.g. $('td #toppaidapplications .game-icon')
      	//$('td#'+listID+i+' .game-icon').attr("src".,"./assets/img/icon_placeholder.png");

      	// replace the app name
      	//$('td#'+listID+i+' .game-name').html('Terraria<br>');

      	// use the external id to generate the name and publisher of the app (temporary fix for missing entries in our appdata object)
      	var itemNameArray = chartdata[i].itemId.split(".");
      	var itemName = itemNameArray[itemNameArray.length-1]; // assume the app name is the last element
      	var itemPublisher = itemNameArray[0]; // assume the publihser name is the first element although will probably be 2nd

      	if (itemNameArray.length > 2) {
      		itemPublisher = itemNameArray[1];
      	};

      	// we current don't always get the appdata in the LookupApplication call so check that we have
      	if (appInfo != null) {
      		itemName = appInfo.title;
      		itemPublisher = appInfo.artistName;
      		// replace the icon
       		$('td#'+listID+rowPos+' .game-icon').attr("src", appInfo.artworkUrlSmall );
      	}

      	var maxStringLength = 20;

      	// shorten the app name if necessary
      	if(itemName.length > maxStringLength) {
    		itemName = itemName.substring(0,maxStringLength)+"...";
		}

		// shorten the publisher name if necessary
      	if(itemPublisher.length > maxStringLength) {
    		itemPublisher = itemPublisher.substring(0,maxStringLength)+"...";
		}

      	// replace the app name
      	$('td#'+listID+rowPos+' .game-name').html('<a href="graph.html?itemId='+chartdata[i].itemId+'&type='+listType+'&country='+country+'">'+itemName+'</a><br>');
      	

      	// replace the publisher name
      	$('td#'+listID+rowPos+' .game-publisher').html(itemPublisher);

      	rowPos++;
      };

  }

  function updateTableSingle(chartdata, listType, listID) {

      
      for (var i = 0; i < maxRows; i++) {
      	

      	// replace the icon
      	// e.g. $('td #toppaidapplications .game-icon')
      	$('td#info'+i+' .game-icon2').html('<img src="../assets/img/icon_placeholder.png">');

      	// replace the app name
      	//$('td#'+listID+i+' .game-name').html('Terraria<br>');
      	var itemNameArray = chartdata[i].itemId.split(".");
      	var itemName = itemNameArray[itemNameArray.length-1]; // assume the app name is the last element
      	var itemPublisher = itemNameArray[0]; // assume the publihser name is the first element although will probably be 2nd

      	if (itemNameArray.length > 2) {
      		itemPublisher = itemNameArray[1];
      	};

      	$('td#info'+i+' .game-name2').html('<a href="graph.html?itemId='+chartdata[i].itemId+'&type='+listType+'&country='+country+'">'+itemName+'</a><br>');
      	

      	// replace the app name
      	$('td#info'+i+' .game-publisher2').html(itemPublisher);


      };

  }

  