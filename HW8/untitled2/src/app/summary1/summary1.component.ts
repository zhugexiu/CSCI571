import {Component, NgModule, OnInit} from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Location } from '@angular/common';
import { HttpClient, HttpClientModule} from "@angular/common/http";
import {timestamp} from "rxjs/operators";
import { interval } from 'rxjs';
import {Subject,Subscription} from "rxjs";

@Component({
  selector: 'app-summary1',
  templateUrl: './summary1.component.html',
  styleUrls: ['./summary1.component.css']
})
export class Summary1Component implements OnInit {

  s1: any = {};
  date = new Date();
  now: any = ""
  cha: any =""
  close2:any = ""
  private subscription: Subscription;

  constructor(
    private http: HttpClient,
    private route: ActivatedRoute,
  ) { }

  ngOnInit(): void {
    this.getSummary1();
    const source = interval(15000);
    this.subscription = source.subscribe(() => this.getSummary1());
  }

  getSummary1(): void{
    const id = this.route.snapshot.paramMap.get('ticker');
    const url ='http://zhugexiu.us-east-1.elasticbeanstalk.com/price/'+id
    this.http.get(url).subscribe((res)=>{
      this.s1 = res['description'][0]
      console.log(this.s1)
      this.now = Math.round(this.date.getTime());
      this.close2 = new Date(this.s1['timestamp']);
      console.log(this.date)
      console.log(this.close2)
      this.cha = Math.abs(this.date.getTime()-this.close2.getTime())/1000;
      console.log(this.cha)
    })
  }
}
