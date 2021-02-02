import { BrowserModule } from '@angular/platform-browser';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { NgModule } from '@angular/core';
import { HttpClientModule }    from '@angular/common/http';
import { FormsModule }    from '@angular/forms';
import { AppComponent } from './app.component';
import { NavbarComponent } from './navbar/navbar.component';
import { FooterComponent } from './footer/footer.component';
import { SearchComponent } from './search/search.component';
import {ReactiveFormsModule} from "@angular/forms";
import { AppRoutingModule } from './app-routing.module';
import { ResultComponent } from './result/result.component';
import {MatTabsModule} from "@angular/material/tabs";
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Summary1Component } from './summary1/summary1.component';
import { Chart1Component } from './chart1/chart1.component';
import { NewsComponent } from './news/news.component';
import {MatCardModule} from "@angular/material/card";
import { Chart2Component } from './chart2/chart2.component';
import { HighchartsChartModule } from 'highcharts-angular';
import {MatAutocompleteModule} from "@angular/material/autocomplete";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";


import { WatchComponent } from './watch/watch.component';
import { ProComponent } from './pro/pro.component';
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";



@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    FooterComponent,
    SearchComponent,
    ResultComponent,
    Summary1Component,
    Chart1Component,
    NewsComponent,
    Chart2Component,
    WatchComponent,
    ProComponent,
  ],
    imports: [
        BrowserModule,
        NgbModule,
        ReactiveFormsModule,
        HttpClientModule,
        AppRoutingModule,
        MatTabsModule,
        BrowserAnimationsModule,
        MatCardModule,
        HighchartsChartModule,
        FormsModule,
        MatAutocompleteModule,
        MatFormFieldModule,
        MatInputModule,
        MatProgressSpinnerModule,
    ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
