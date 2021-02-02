import { Component } from '@angular/core';
import * as Highcharts from "highcharts/highstock";
import ExportingModule from 'highcharts/modules/exporting';
import SunsetTheme from 'highcharts/themes/sunset.src.js';

ExportingModule(Highcharts);
SunsetTheme(Highcharts);


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'STOCK SEARCH';
  ngOnInit(): void {
    if(JSON.parse(localStorage.getItem('watchlist'))==[] || localStorage.getItem('watchlist') == null){
      localStorage.setItem("watchlist",JSON.stringify([]));
    }
    if(JSON.parse(localStorage.getItem('portfolio'))==[] || localStorage.getItem('portfolio') == null){
      localStorage.setItem("portfolio",JSON.stringify([]));
    }
  }
}
