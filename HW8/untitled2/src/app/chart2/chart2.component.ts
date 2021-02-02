import { Component, OnInit } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {ActivatedRoute} from "@angular/router";

import * as Highcharts from "highcharts/highstock";
import { Options } from "highcharts/highstock";
import ExportingModule from 'highcharts/modules/exporting';
import SunsetTheme from 'highcharts/themes/sunset.src.js';

ExportingModule(Highcharts);
SunsetTheme(Highcharts);

import IndicatorsCore from "highcharts/indicators/indicators";
import vbp from "highcharts/indicators/volume-by-price";

IndicatorsCore(Highcharts);

vbp(Highcharts);


@Component({
  selector: 'app-chart2',
  templateUrl: './chart2.component.html',
  styleUrls: ['./chart2.component.css']
})
export class Chart2Component implements OnInit {

  Highcharts: typeof Highcharts = Highcharts;//required
  c2: any = {};
  d2: any= [];
  chartOptions: Highcharts.Options;

  constructor(
    private http: HttpClient,
    private route: ActivatedRoute,
  ) { }

  ngOnInit(): void {
    console.log(window);
    const id = this.route.snapshot.paramMap.get('ticker');
    const url ='http://zhugexiu.us-east-1.elasticbeanstalk.com/history/'+ id;
    this.http.get(url).subscribe((res)=> {
      this.c2 = res
      this.d2 = res['description'];
      const dataLength = this.d2.length;
      let i = 0;
      const ohlc = [];
      const volume = [];
      let title = '';
      let name = '';
      for (i; i < dataLength; i += 1) {
        ohlc.push([
          this.d2[i]['date'], // the date
          this.d2[i]['open'], // open
          this.d2[i]['high'], // high
          this.d2[i]['low'], // low
          this.d2[i]['close'] // close
        ]);
        volume.push([
          this.d2[i]['date'], // the date
          this.d2[i]['volume'] // the volume
        ]);
      }
      title = id +' '+ 'Historical';
      name = id;
      console.log(ohlc);
      console.log(volume);
      //this.chartOptions.series[0]['data'] = this.ohlc;
      //this.chartOptions.series[1]['data'] = this.volume;
      //this.chartOptions.title['text'] = this.title+' '+'Historical';
      //this.chartOptions.series[0]['name'] = this.name;

      this.chartOptions = {

        rangeSelector: {
          selected: 2
        },

        title: {
          text: title
        },

        subtitle: {
          text: 'With SMA and Volume by Price technical indicators'
        },

        yAxis: [{
          startOnTick: false,
          endOnTick: false,
          labels: {
            align: 'right',
            x: -3
          },
          title: {
            text: 'OHLC'
          },
          height: '60%',
          lineWidth: 2,
          resize: {
            enabled: true
          }
        }, {
          labels: {
            align: 'right',
            x: -3
          },
          title: {
            text: 'Volume'
          },
          top: '65%',
          height: '35%',
          offset: 0,
          lineWidth: 2
        }],

        tooltip: {
          split: true
        },

        plotOptions: {
          series: {
            dataGrouping: {
              units: [['week', [1]], ['month', [1, 2, 3, 4, 6]]]
            }
          }
        },


        series: [{
          type: 'candlestick',
          name: name,
          id: 'aapl',
          zIndex: 2,
          data: ohlc
        }, {
          type: 'column',
          name: 'Volume',
          id: 'volume',

          data: volume,
          yAxis: 1
        }, {
          type: 'vbp',
          linkedTo: 'aapl',
          params: {
            volumeSeriesID: 'volume'
          },
          dataLabels: {
            enabled: false
          },
          zoneLines: {
            enabled: false
          }
        }, {
          type: 'sma',
          linkedTo: 'aapl',
          zIndex: 1,
          marker: {
            enabled: false
          }
        }]
      };
    })
  }
}


