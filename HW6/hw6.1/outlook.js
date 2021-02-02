document.querySelector('#submit').addEventListener('click', function() {
    var isValid = document.querySelector('#myForm').reportValidity();
    console.log("diandiandian")
    if (isValid) {
        URL = "/api/search/?";
        URL += "keyword=" + document.getElementById('keyword').value;
        jsonObj = loadJSON(URL);
        function loadJSON(url) {
            if (window.XMLHttpRequest) {// code for IE7+, Firefox, Chrome, Opera, Safari
                xmlhttp = new XMLHttpRequest();
            } else {// code for IE6, IE5
                xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
            }
            xmlhttp.open("GET", url, false);
            xmlhttp.send();
            jsonObj = JSON.parse(xmlhttp.responseText);
            return jsonObj;
        }
        console.log(jsonObj);
        if(jsonObj.hasOwnProperty('message')){
            html_text404  = "";
            html_text404 += "<p class = 'ep'> Error : No Record has been found, please enter a valid symbol</p>";
            document.getElementById("Error").innerHTML = html_text404;
        }else {
            //1
            var a = jsonObj['outlook'];
            html_text1 = "<td>";
            html_text1 += a['name'] + "</td>";
            document.getElementById("companyName").innerHTML = html_text1;
            html_text2 = "<td>";
            html_text2 += a['ticker'] + "</td>";
            document.getElementById("tickerSymbol").innerHTML = html_text2;
            html_text3 = "<td>";
            html_text3 += a['exchangeCode'] + "</td>";
            document.getElementById("code").innerHTML = html_text3;
            html_text4 = "<td>";
            html_text4 += a['startDate'] + "</td>";
            document.getElementById("startDate").innerHTML = html_text4;
            var description = a['description'];
            html_text5 = "<td>";
            html_text5 += description + "</td>";
            document.getElementById("des").innerHTML = html_text5;

            //2
            var b = jsonObj['summary'];
            html_text = "<div>";
            html_text += "<table class = 'summaryTable'>";
            html_text += "<tr>";
            html_text += "<td class = 'fix'>Stock Ticker Information</td>";
            html_text += "<td class = 'fix2'>"+ b['ticker']+"</td>";
            html_text += "</tr>";
            var date = new Date(b['timestamp']);
            html_text += "<tr>" + "<td class = 'fix'>Trading Day</td>" + "<td class = 'fix2'>"+ dateToStrSlash2(date)+"</td>" +"</tr>";
            html_text += "<tr>" + "<td class = 'fix'>Previous Closing Price</td>" + "<td class = 'fix2'>"+ b['prevClose']+"</td>" +"</tr>";
            html_text += "<tr>" + "<td class = 'fix'>Opening Price</td>" + "<td class = 'fix2'>"+ b['open']+"</td>" +"</tr>";
            html_text += "<tr>" + "<td class = 'fix'>High Price</td>" + "<td class = 'fix2'>"+ b['high']+"</td>" +"</tr>";
            html_text += "<tr>" + "<td class = 'fix'>Low Price</td>" + "<td class = 'fix2'>"+ b['low']+"</td>" +"</tr>";
            html_text += "<tr>" + "<td class = 'fix'>Last Price</td>" + "<td class = 'fix2'>"+ b['last']+"</td>" +"</tr>";

            var change = Number(b['last'])- Number(b['prevClose']);
            change = change.toFixed(2);
            if(change>0)
                html_text +=  "<tr>" + "<td class = 'fix'>Change</td>" + "<td class = 'fix2'>"+ change +
                    "<img src = 'https://csci571.com/hw/hw6/images/GreenArrowUp.jpg' style='width:4%'>"+ "</td>" +"</tr>";
            else if(change<0)
                html_text +=  "<tr>" + "<td class = 'fix'>Change</td>" + "<td class = 'fix2'>"+ change +
                    "<img src = 'https://csci571.com/hw/hw6/images/RedArrowDown.jpg' style='width:4%'>"+ "</td>" +"</tr>";
            else
                html_text +=  "<tr>" + "<td class = 'fix'>Change</td>" + "<td class = 'fix2'>"+ change + "</td>" +"</tr>";

            var changeprecent = Number((change/Number(b['prevClose'])) * 100);
            changeprecent = changeprecent.toFixed(2);
            if(changeprecent>0)
                html_text +=  "<tr>" + "<td class = 'fix'>Change Percent</td>" + "<td class = 'fix2'>"+ changeprecent +
                    "<img src = 'https://csci571.com/hw/hw6/images/GreenArrowUp.jpg'  style='width:4%'>"+ "</td>" +"</tr>";
            else if(changeprecent<0)
                html_text +=  "<tr>" + "<td class = 'fix'>Change Percent</td>" + "<td class = 'fix2'>"+ changeprecent +
                    "<img src = 'https://csci571.com/hw/hw6/images/RedArrowDown.jpg' style='width:4%'>"+ "</td>" +"</tr>";
            else
                html_text +=  "<tr>" + "<td class = 'fix'>Change Percent</td>" + "<td class = 'fix2'>"+ changeprecent + "</td>" +"</tr>";

            html_text += "<tr>" + "<td class = 'fix'>Number of Shares Traded</td>" + "<td class = 'fix2'>"+ b['volume']+"</td>" +"</tr>";
            html_text += "</table>" +"</div>";
            document.getElementById('h2').innerHTML = html_text;

            //3
            var d = jsonObj['chart'];
            var arr1 = [];
            var arr2 = [];
            for(var k = 0; k < d.length; k++){
                var points1 = []
                points1.push(Number(new Date(d[k]['date'])));
                points1.push(d[k]['close']);
                arr1.push(points1);
            }
            for(var j = 0; j < d.length; j++){
                var points2 = [];
                points2.push(Number(new Date(d[j]['date'])));
                points2.push(d[j]['volume']);
                arr2.push(points2);
            }

            var date = new Date();
            var nowMonth = date.getMonth() + 1;
            var strDate = date.getDate();
            var seperator = "-";
            if (nowMonth >= 1 && nowMonth <= 9) {
               nowMonth = "0" + nowMonth;
            }
            if (strDate >= 0 && strDate <= 9) {
               strDate = "0" + strDate;
            }
            var nowDate = date.getFullYear() + seperator + nowMonth + seperator + strDate;

            Highcharts.stockChart('h3', {

                title: {
                  text: 'stock Price ' + document.getElementById('keyword').value.toUpperCase() + '  '+ nowDate
                },

                subtitle: {
                    text: '<a href="https://api.tiingo.com/" target="_blank">Source Tiingo</a>'
                },

                yAxis: [{
                    labels: {
                        align: 'right',
                        formatter: function () {
                            return this.value / 1000 + 'k';
                        }
                    },
                    title: {
                        text: 'Volume'
                    }
                },{
                    labels: {
                        align: 'left',
                    },
                    title: {
                        text: 'Stock Price'
                    },
                    opposite: false,
                }],

               rangeSelector: {
                    buttons: [{
                        type: 'day',
                        count: 7,
                        text: '7d',
                    },{
                        type: 'day',
                        count: 15,
                        text: '15d',
                    },
                    {
                        type: 'month',
                        count: 1,
                        text: '1m',
                    }, {
                        type: 'month',
                        count: 3,
                        text: '3m'
                    }, {
                        type: 'month',
                        count: 6,
                        text: '6m'
                    }],
                    selected: 1,
                    inputEnabled: false
                },
                 plotOptions: {
                    area: {

                    }
                },
                series: [{
                  name: document.getElementById('keyword').value.toUpperCase(),
                  type: 'area',
                  data: arr2,
                  yAxis: 0,
                  tooltip: {
                    valueDecimals: 2
                  },
                  threshold: null
                },{
                  name: document.getElementById('keyword').value.toUpperCase() + ' Volume',
                  type: 'column',
                  data: arr1,
                  yAxis: 1,
                  tooltip: {
                    valueDecimals: 0
                  },
                  threshold: null
                }]
                });


            //4
            html_text6 = "";
            var c = jsonObj['news']
            for (var i = 0; i < c.length; i++){
                html_text6 += "<div class = 'card'>";
                html_text6 += "<div class='left'><img src='"+ c[i]['urlToImage'] + "'></div>";
                html_text6 += "<div class='right'>"+ "<div class = 'title1'>" +c[i]['title'] + "</div>";
                var date = new Date(c[i]['publishedAt']);
                html_text6 += "<div class='date1'><span>Publish Date: </span>"+ dateToStrSlash(date)+"</div>";
                html_text6 += "<a href='"+c[i]['url']+"'target='_blank'>" + "<div class = 'post1'>See Orginal Post</div></a>";
                html_text6 += "</div></div>";
            }
            document.getElementById('h4').innerHTML = html_text6;



            document.getElementById('result').style.display="inline-block";
        }
    }


});


function dateToStrSlash2(date) {
    var day = ("0" + date.getDate()).slice(-2);
    var month = ("0" + (date.getMonth() + 1)).slice(-2);
    var result = date.getFullYear() + "-" + month +"-" + day;
    return result;
}

function dateToStrSlash(date) {
    var day = ("0" + date.getDate()).slice(-2);
    var month = ("0" + (date.getMonth() + 1)).slice(-2);
    var result = month+'/'+day+'/'+date.getFullYear();
    return result;
}

