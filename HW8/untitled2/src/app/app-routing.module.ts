import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import {RouterModule, Routes} from "@angular/router";
import {SearchComponent} from "./search/search.component";
import {ResultComponent} from './result/result.component';
import {WatchComponent} from "./watch/watch.component";
import {ProComponent} from "./pro/pro.component";

const routes: Routes = [
  { path: '', redirectTo: '/', pathMatch: 'full' },
  { path: '', component : SearchComponent },
  { path: 'watchlist', component: WatchComponent },
  { path: 'detail/:ticker', component: ResultComponent },
  { path: 'portfolio', component: ProComponent}
];

@NgModule({
  imports: [ RouterModule.forRoot(routes) ],
  exports: [ RouterModule ]
})
export class AppRoutingModule { }
