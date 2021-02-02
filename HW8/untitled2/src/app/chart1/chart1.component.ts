import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient, HttpClientModule} from "@angular/common/http";
import * as Highcharts from "highcharts/highstock";
import { forkJoin } from "rxjs";
import ExportingModule from 'highcharts/modules/exporting';
import SunsetTheme from 'highcharts/themes/sunset.src.js';

ExportingModule(Highcharts);
SunsetTheme(Highcharts);

import IndicatorsCore from "highcharts/indicators/indicators";
import vbp from "highcharts/indicators/volume-by-price";
IndicatorsCore(Highcharts);

vbp(Highcharts);
import { interval } from 'rxjs';
import {Subject,Subscription} from "rxjs";

@Component({
  selector: 'app-chart1',
  templateUrl: './chart1.component.html',
  styleUrls: ['./chart1.component.css']
})
export class Chart1Component implements OnInit {
  Highcharts: typeof Highcharts = Highcharts;//required
  c1: any = {};
  s1: any = {};

  chartOptions: Highcharts.Options;
  private color: string;
  private subscription: Subscription;
  constructor(
    private http: HttpClient,
    private route: ActivatedRoute,
  ) { }

  ngOnInit(): void {
    this.getChart()
    const source = interval(15000);
    this.subscription = source.subscribe(() => this.getChart());
  }

  getChart():void{
    const id = this.route.snapshot.paramMap.get('ticker');
    const url ='http://zhugexiu.us-east-1.elasticbeanstalk.com/dailychart/'+id;
    const url2 ='http://zhugexiu.us-east-1.elasticbeanstalk.com/price/'+id;
    let get1 = this.http.get(url);
    let get2 = this.http.get(url2);

    forkJoin([get1,get2]).subscribe((res)=>{
      this.s1 = res[1]['description'][0]
      console.log(this.s1)
      let c = ""
      if(this.s1['last'] - this.s1['prevClose']>0){
        c = 'green'
      }else if(this.s1['last'] - this.s1['prevClose']<0){
        c = 'red'
      }else{
        c = 'black'
      }
      this.color = c;
      console.log(this.color)

        this.c1 = res[0]['description']
        const dataLength = this.c1.length;
        let i = 0;
        let title = '';
        let name = '';
        const volume = [];
        for (i; i < dataLength; i += 1) {
          volume.push([
            Date.parse(this.c1[i]['date']), // the date
            this.c1[i]['volume'] // the volume
          ]);
        }
        title = id;
        name = id;
        console.log(volume)
        console.log(this.color)
        this.chartOptions = {
          navigator: {
            series: {
              fillOpacity: 0.05,
              color: c,
            }
          },
          rangeSelector: {
            enabled: false
          },

          title: {
            text: title
          },
          time: {
            useUTC: false,
          },
          series: [{
            name: name,
            data: volume,
            tooltip: {
              valueDecimals: 2
            },
            type:'line',
            color: c
          }],
        };
      })
  }
}

