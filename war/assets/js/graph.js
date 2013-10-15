
var rangeData = [];

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
        $('#reportrange span').html(start.format('MMMM D, YYYY') + ' - ' + end.format('MMMM D, YYYY'));
        startDate = moment(start).unix() * 1000;
        endDate = moment(end).unix() * 1000;
        getItemsRank();
    }
);


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

    var pager = "{'count':" + 30 + ",'start':" + 0 + ",'sortDirection':'descending','sortBy':'date'}";
    

    var requestString = "{'accessCode':'b72b4e32-1062-4cc7-bc6b-52498ee10f09','item':{'source':'ios','externalId':"+itemId+"},'listType':'"+type+"','country':{'a2Code':'"+country+"'},'start':"+startDate+",'end':"+endDate+", 'pager':" + pager + "}";

    $.ajax({
        type: "POST",
        url: "../core",
        data: { action: "GetItemRanks", request: requestString },
            //contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function(data) {

                console.log(data);

                var averagesData = [];
                var dateData = [];
                var minPosition = 1000;
                var maxPosition = 20;
                var pos = 0;
                var nodeDate;
                var dayOfMonth = 0; // the current day of the month. i only want to show 1 entry per day
                var dayOfMonthOld = -1;

                $.each(data.ranks, function(index, item){    
                    // only use the specific "type" of list e.g. topfreeapplications, topfreeipadapplications
                    //if (item.type == type) {
                        
                        var nodeDate = new Date(item.date);
                        dateData.push(nodeDate); // this is purely for the console.log call below

                        dayOfMonth = nodeDate.getDate();

                        if (dayOfMonth != dayOfMonthOld) {
                         
                            // do things a bit differently if a top grossing app
                            if ((type.indexOf("grossing") != -1)) {
                                pos = item.grossingPosition;
                            } else {
                                pos = item.position;
                            }
                                
                            var thisEntry = [item.date, pos];
                            averagesData.push(thisEntry); // store the dates
                            

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

                console.log(averagesData);
                console.log(dateData);

                maxPosition += 10;

                if (minPosition < 20) {
                    minPosition = 1;
                }
                else  {
                    minPosition -= 10;
                }

                // console.log("minPosition = " + minPosition);
                // console.log("maxPosition = " + maxPosition);

                // if (chartLoaded) {
                //     refreshChart(averagesData, minPosition, maxPosition);
                // } else {
                //     updateApp(data.item);
                //     loadChart(averagesData, minPosition, maxPosition);
                //     chartLoaded = true;
                // }

                if (!chartLoaded) {
                    updateApp(data.item);
                    chartLoaded = true;
                }

                loadChart(averagesData, minPosition, maxPosition);
                
            }
    });
}

function updateApp(item) {

     $('#app-name').html(item.name+'<br>');
     $('#publisher-name').html(item.creatorName);
     $('.game-icon-heading').attr("src", item.mediumImage);

}

function loadChart(averagesData, minPosition, maxPosition) {

    /*var averages = [
            [1246406400000, 21.5],
            [1246492800000, 22.1],
            [1246579200000, 23],
            [1246665600000, 23.8],
            [1246752000000, 21.4],
            [1246838400000, 21.3],
            [1246924800000, 18.3],
            [1247011200000, 15.4],
            [1247097600000, 16.4],
            [1247184000000, 17.7],
            [1247270400000, 17.5],
            [1247356800000, 17.6],
            [1247443200000, 17.7],
            [1247529600000, 16.8],
            [1247616000000, 17.7],
            [1247702400000, 16.3],
            [1247788800000, 17.8],
            [1247875200000, 18.1],
            [1247961600000, 17.2],
            [1248048000000, 14.4],
            [1248134400000, 13.7],
            [1248220800000, 15.7],
            [1248307200000, 14.6],
            [1248393600000, 15.3],
            [1248480000000, 15.3],
            [1248566400000, 15.8],
            [1248652800000, 15.2],
            [1248739200000, 14.8],
            [1248825600000, 14.4],
            [1248912000000, 15],
            [1248998400000, 13.6]
        ];*/

        $('#graph').show();

        chart = new Highcharts.Chart({
            chart: {
                renderTo: 'graph',
                //defaultSeriesType: 'spline',
            },
            title: {
                    text: 'Rank History',
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
                    reversed: true,
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
                    name: 'Rank',
                    data: averagesData
                }/*, {
                    name: 'New York',
                    data: [-0.2, 0.8, 5.7, 11.3, 17.0, 22.0, 24.8, 24.1, 20.1, 14.1, 8.6, 2.5]
                }, {
                    name: 'Berlin',
                    data: [-0.9, 0.6, 3.5, 8.4, 13.5, 17.0, 18.6, 17.9, 14.3, 9.0, 3.9, 1.0]
                }, {
                    name: 'London',
                    data: [3.9, 4.2, 5.7, 8.5, 11.9, 15.2, 17.0, 16.6, 14.2, 10.3, 6.6, 4.8]
                }*/]
        });

        

}

function refreshChart(averagesData, minPosition, maxPosition) {
    
    chart.series[0].setData(averagesData,true);
}