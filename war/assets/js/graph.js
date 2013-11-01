
var rangeData = [];
var historyList = {};
var selectedGraph = "rank";

var chart;
var chartLoaded = false;

var startDate;
var endDate;


$(document).ready(function(){

    var start = moment().subtract('days', 29);
    var end =  moment();

    startDate = moment(start).unix() * 1000;
    endDate = moment(end).unix() * 1000;

    $('#reportrange span').html(start.format('MMMM D, YYYY') + ' - ' + end.format('MMMM D, YYYY'));

    // console.log(startDate);
    // console.log(endDate);

    var url = purl(); // parse the current page URL
    var name = url.param("name");
    var publisher = url.param("publisher");
    $('#app-name').html(name+'<br>');
    $('#publisher-name').html(publisher);

    getItemsRank();

 });

$('#reportrange').daterangepicker(
    {
      ranges: {
         /*'Today': [moment(), moment()],
         'Yesterday': [moment().subtract('days', 1), moment().subtract('days', 1)],
         'Last 7 Days': [moment().subtract('days', 6), moment()],*/
         'Last 30 Days': [moment().subtract('days', 29), moment()],
         'This Month': [moment().startOf('month'), moment().endOf('month')],
         'Last Month': [moment().subtract('month', 1).startOf('month'), moment().subtract('month', 1).endOf('month')]
      },
      startDate: moment().subtract('days', 29),
      endDate: moment()
    },
    function(start, end) {
        $('#graph').hide();
        $('#loader').show();
        $('#reportrange span').html(start.format('MMMM D, YYYY') + ' - ' + end.format('MMMM D, YYYY'));
        startDate = moment(start).unix() * 1000;
        endDate = moment(end).unix() * 1000;
        getItemsRank();
    }
);

// when the user presses a nav button
$(".btn").click(function () {

    var value = $(this).attr("value");

    if (value == "overview") {
        
    }
    else {
        // $('#graph-container').toggle();

        var itemArray = value.split("_");
        selectedGraph = itemArray[1]; 

        loadChart(historyList[selectedGraph]);
        
    }

});


function getItemsRank() {

    // to do a gather run this: http://127.0.0.1:8888/dev/devhelper?action=gather&store=ios&cron=yes
    // to check on it go here: http://127.0.0.1:8888/_ah/admin and click on "Task Queues"
    // var externalId = $.url().param("itemId"); //"com.gameloft.despicableme2";
    // var listType = $.url().param("type");
    // var country = $.url().param("country");

    var url = purl(); // parse the current page URL
    var itemId = url.param("itemId");
    var type = url.param("type");
    var country = url.param("country");

    //var startDate = new Date(2013,8,29).getTime(); // remember month starts at index 0 (not 1)
    //var endDate = new Date().getTime(); // today

    // var endDate = new Date();
    // var startDate = endDate.setDate(endDate.getDate()-7);

    // endDate = endDate.getTime();
    // startDate = startDate.getTime();

    var pager = "{'count':" + 60 + ",'start':" + 0 + ",'sortDirection':'descending','sortBy':'date'}";
    

    var requestString = "{'accessCode':'b72b4e32-1062-4cc7-bc6b-52498ee10f09','item':{'source':'ios','externalId':"+itemId+"},'listType':'"+type+"','country':{'a2Code':'"+country+"'},'start':"+startDate+",'end':"+endDate+", 'pager':" + pager + "}";

    $.ajax({
        type: "POST",
        url: "../core",
        data: { action: "GetItemRanks", request: requestString },
            //contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function(data) {

                console.log(data);

                 if (!chartLoaded) {
                    updateApp(data.item);
                    chartLoaded = true;
                }

                createGraphData(data, type, "rank");
                createGraphData(data, type, "revenue");
                createGraphData(data, type, "downloads");

                loadChart(historyList[selectedGraph]);
                
            }
    });
}

function createGraphData(data, type, graphType) {

    var rankData = [];
    // var dateData = [];
    var minPosition = 1000;
    var maxPosition = 20;
    var pos = 0;
    var rangeData = [];
    var rangeTop = 0;
    var rangeBottom = 0;
    var nodeDate;
    var dayOfMonth = 0; // the current day of the month. i only want to show 1 entry per day
    var dayOfMonthOld = -1;
    var d;

    $.each(data.ranks, function(index, item){    
        // only use the specific "type" of list e.g. topfreeapplications, topfreeipadapplications
        //if (item.type == type) {
            
            var nodeDate = new Date(item.date);
            // dateData.push(nodeDate); // this is purely for the console.log call below

            dayOfMonth = nodeDate.getDate();

            // if (graphType == "rank") {
            //     console.log("dayOfMonth = " + dayOfMonth + " | dayOfMonthOld = " + dayOfMonthOld);
            // }


            if (dayOfMonthOld != -1) {
                // IMPORTANT NOTE: TO TEST THIS FEATURE I NEED TO USE THE FOLLOWING GAME DETAILS
                // http://127.0.0.1:8888/alpha/graph.html?itemId=com.nerdyoctopus.dots&type=topfreeapplications&country=us
                // http://127.0.0.1:8888/alpha/graph.html?itemId=com.merekdavis.Mextures&type=toppaidapplications&country=us
                // http://127.0.0.1:8888/alpha/graph.html?itemId=com.bitstrips.bitstrips&type=topfreeapplications&country=us&name=Bitstrips&publisher=Bitstrips
                // http://127.0.0.1:8888/alpha/graph.html?itemId=uk.co.cubeinteractive.dipdap.paid&type=toppaidipadapplications&country=gb&name=Dipdap&publisher=Cube%20Interactive
                // THE APPLICABLE DATES ARE 22nd September 2013 - 17th October 2013
                // THIS CODE CURRENTLY ONLY WORKS FOR RANKS. I NEED A DIFERENT VALUE FOR pos ON REVENUE AND DOWNLOADS
                var addMissingPoints = false;
                // this handles when a game drops of the chart then comes back in again
                if (dayOfMonthOld > dayOfMonth ) {
                    // check whether the drop off occured at the end of month
                    if (dayOfMonth != 1) {
                        d = 1;
                        addMissingPoints = true;
                    }
                }
                else if ( (dayOfMonth - dayOfMonthOld) > 1 ) {
                    // then check that the drop off didn't occur during the month
                    d = dayOfMonthOld;
                    addMissingPoints = true;
                }

                if (addMissingPoints) {
                    while (d < dayOfMonth) {
                        pos = 1001;
                        rangeTop = pos;
                        rangeBottom = pos;
                        rankData.push([item.date, pos]);
                        rangeData.push([item.date, rangeTop, rangeBottom]);
                        d++;
                    }
                }
            }
            

            if (dayOfMonth != dayOfMonthOld) {
             
                if (graphType == "rank") {
                    // do things a bit differently if a top grossing app
                    if ((type.indexOf("grossing") != -1)) {
                        pos = item.grossingPosition;
                    } else {
                        pos = item.position;
                    }
                    rangeTop = pos;
                    rangeBottom = pos;
                }
                else if (graphType == "revenue") {
                    pos = item.revenue;
                    rangeTop = pos - 5;
                    rangeBottom = pos + 5;
                }
                else /*if (graphType == "downloads")*/ {
                    pos = item.downloads;
                    rangeTop = pos - 5;
                    rangeBottom = pos + 5;
                }
                
                    
                var thisEntry = [item.date, pos];
                rankData.push(thisEntry); // store the dates

                rangeData.push([item.date, rangeTop, rangeBottom]);
                

                if (pos > maxPosition) {
                    maxPosition = pos;
                }

                if (pos < minPosition) {
                    minPosition = pos;
                }
            
                dayOfMonthOld = dayOfMonth;
            }
        //};       
    });

    // console.log(rankData);
    // console.log(dateData);

    maxPosition += 10;

    if (minPosition < 20) {
        minPosition = 1;
    }
    else  {
        minPosition -= 10;
    }

    var history = {};
    history.data = rankData;
    history.rangeData = rangeData;
    history.minPosition = minPosition;
    history.maxPosition = maxPosition;
    var heading;
    var seriesName;
    if (graphType == "rank") {
        heading = "Rank History"
        seriesName = "Rank";
    }
    else if (graphType == "revenue") {
       heading = "Revenue History";
       seriesName = "Revenue";
    }
    else /*if (graphType == "downloads")*/ {
        heading = "Downloads History";
        seriesName = "Downloads";
    }
    history.heading = heading;
    history.seriesName = seriesName;

    historyList[graphType] = history;
    // console.log(historyList);

}

function updateApp(item) {

     // $('#app-name').html(item.name+'<br>');
     // $('#publisher-name').html(item.creatorName);
     $('.game-icon-heading').attr("src", item.mediumImage);

}

function loadChart(graphData) {

    var pointData = graphData.data;
    var rangeData = graphData.rangeData;
    var minPosition = graphData.minPosition;
    var maxPosition = graphData.maxPosition;
    var heading = graphData.heading;
    var seriesName = graphData.seriesName;

    var isReversed = false;

    if (seriesName == "Rank") {
        isReversed = true;
    }

    // console.log(rangeData);


    $('#graph').show();
    $('#loader').hide();

    chart = new Highcharts.Chart({
        chart: {
            renderTo: 'graph',
            //defaultSeriesType: 'spline',
        },
        title: {
                text: heading,
                x: -20 //center
            },
            /*
            subtitle: {
                text: 'Source: Reflection.io',
                x: -20
            },
            */
            xAxis: {
                type: 'datetime'
            },
            
            yAxis: {
                title: {
                    text: null
                },
                reversed: isReversed,
                min: minPosition,
                max: maxPosition
            },
            tooltip: {
                //valueSuffix: '°C'
            },
            legend: {
                layout: 'vertical',
                align: 'right',
                verticalAlign: 'middle',
                borderWidth: 0
            },
            series: [{
                name: seriesName,
                data: pointData,
                marker: {
                    fillColor: 'white',
                    lineWidth: 2,
                    lineColor: Highcharts.getOptions().colors[0]
                }
            }, {
                name: 'Range',
                data: rangeData,
                type: 'arearange',
                lineWidth: 0,
                linkedTo: ':previous',
                color: Highcharts.getOptions().colors[0],
                fillOpacity: 0.3,
                zIndex: 0
            }]
    });

}

function refreshChart(rankData, minPosition, maxPosition) {
    
    // chart.series[0].yAxis.min = 15;
    chart.series[0].setData(rankData,true);
    // chart.redraw();
}