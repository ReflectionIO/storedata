
var averagesData = [];
var rangeData = [];


$(document).ready(function(){

    getItemsRank();

 });


/*$(function () { 
    $('#container').highcharts({
        chart: {
            type: 'bar'
        },
        title: {
            text: 'Fruit Consumption'
        },
        xAxis: {
            categories: ['Apples', 'Bananas', 'Oranges']
        },
        yAxis: {
            title: {
                text: 'Fruit eaten'
            }
        },
        series: [{
            name: 'Jane',
            data: [1, 0, 4]
        }, {
            name: 'John',
            data: [5, 7, 3]
        }]
    });
});*/

/*$(function () {

    var ranges = [
            [1246406400000, 14.3, 27.7],
            [1246492800000, 14.5, 27.8],
            [1246579200000, 15.5, 29.6],
            [1246665600000, 16.7, 30.7],
            [1246752000000, 16.5, 25.0],
            [1246838400000, 17.8, 25.7],
            [1246924800000, 13.5, 24.8],
            [1247011200000, 10.5, 21.4],
            [1247097600000, 9.2, 23.8],
            [1247184000000, 11.6, 21.8],
            [1247270400000, 10.7, 23.7],
            [1247356800000, 11.0, 23.3],
            [1247443200000, 11.6, 23.7],
            [1247529600000, 11.8, 20.7],
            [1247616000000, 12.6, 22.4],
            [1247702400000, 13.6, 19.6],
            [1247788800000, 11.4, 22.6],
            [1247875200000, 13.2, 25.0],
            [1247961600000, 14.2, 21.6],
            [1248048000000, 13.1, 17.1],
            [1248134400000, 12.2, 15.5],
            [1248220800000, 12.0, 20.8],
            [1248307200000, 12.0, 17.1],
            [1248393600000, 12.7, 18.3],
            [1248480000000, 12.4, 19.4],
            [1248566400000, 12.6, 19.9],
            [1248652800000, 11.9, 20.2],
            [1248739200000, 11.0, 19.3],
            [1248825600000, 10.8, 17.8],
            [1248912000000, 11.8, 18.5],
            [1248998400000, 10.8, 16.1]
        ],
        averages = [
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
        ];

    
        $('#container').highcharts({
        
            title: {
                text: 'July temperatures'
            },
        
            xAxis: {
                type: 'datetime'
            },
            
            yAxis: {
                title: {
                    text: null
                }
            },
        
            tooltip: {
                crosshairs: true,
                shared: true,
                valueSuffix: '°C'
            },
            
            legend: {
            },
        
            series: [{
                name: 'Temperature',
                data: averages,
                zIndex: 1,
                marker: {
                    fillColor: 'white',
                    lineWidth: 2,
                    lineColor: Highcharts.getOptions().colors[0]
                }
            }, {
                name: 'Range',
                data: ranges,
                type: 'arearange',
                lineWidth: 0,
                linkedTo: ':previous',
                color: Highcharts.getOptions().colors[0],
                fillOpacity: 0.3,
                zIndex: 0
            }]
        
        });
    
});*/

/*$(function () {

        var averages = [
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
        ];

        $('#container').highcharts({
            title: {
                text: 'Monthly Average Temperature',
                x: -20 //center
            },
            subtitle: {
                text: 'Source: WorldClimate.com',
                x: -20
            },
            xAxis: {
                type: 'datetime'
            },
            
            yAxis: {
                title: {
                    text: null
                }
            },
            tooltip: {
                valueSuffix: '°C'
            },
            legend: {
                layout: 'vertical',
                align: 'right',
                verticalAlign: 'middle',
                borderWidth: 0
            },
            series: [{
                name: 'Tokyo',
                data: averages
            }]
        });
    });
*/


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

    var requestString = "{'accessCode':'b72b4e32-1062-4cc7-bc6b-52498ee10f09','item':{'source':'ios','externalId':"+itemId+"},'listType':'"+type+"','country':{'a2Code':'"+country+"'}}";

    $.ajax({
        type: "POST",
        url: "../core",
        data: { action: "GetItemRanks", request: requestString },
            //contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function(data) {

                console.log(data);

                var dateData = [];
                var minPosition = 1000;
                var maxPosition = 20;
                var pos = 0;
                var nodeDate;
                var dayOfMonth = 0; // the current day of the month. i only want to show 1 entry per day
                var dayOfMonthOld = -1;

                // the data comes in reverse so I need to loop through it in reverse()
                $.each(data.ranks.reverse(), function(index, item){    
                    // only use the specific "type" of list e.g. topfreeapplications, topfreeipadapplications
                    if (item.type == type) {
                        
                        var nodeDate = new Date(item.date);
                        dateData.push(nodeDate); // this is purely for the console.log call below

                        dayOfMonth = nodeDate.getDate();

                        // only add the node to the graph if we haven't already got one for that day of the month
                        //if (dayOfMonth != dayOfMonthOld) {
                            pos = item.position;
                            var thisEntry = [item.date, pos];
                            averagesData.push(thisEntry); // store the dates
                            

                            if (pos > maxPosition) {
                                maxPosition = pos;
                            }

                            if (pos < minPosition) {
                                minPosition = pos;
                            }
                            dayOfMonthOld = dayOfMonth;
                        //}

                        
                    };       
                    
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

                console.log("minPosition = " + minPosition);
                console.log("maxPosition = " + maxPosition);

                loadChart(minPosition, maxPosition);
            }
    });
}

function loadChart(minPosition, maxPosition) {

    var averages = [
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
        ];

        $('#container').highcharts({
            title: {
                text: 'AppStore Rank Data',
                x: -20 //center
            },
            subtitle: {
                text: 'Source: Reflection',
                x: -20
            },
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