import { Component, OnInit } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-watch',
  templateUrl: './watch.component.html',
  styleUrls: ['./watch.component.css']
})
export class WatchComponent implements OnInit {

  d:any = []
  d2:any = []
  showSpinner:boolean;
  showalert:boolean;
  private length1: any;

  constructor(
    private http: HttpClient,
    private route: ActivatedRoute,
  ) { }

  httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
  }
   ans:any = [];

  ngOnInit(): void {
    this.showSpinner = true;
    this.getwatch()
  }

  getwatch():void{
    let array = JSON.parse(localStorage.getItem('watchlist'))
    let i = 0;
    let id ="";
    let lists= [];
    if(array.length == 0){
      const url ='http://zhugexiu.us-east-1.elasticbeanstalk.com/details/ibm';
      this.http.get(url).subscribe((res)=>{
        this.showSpinner = false;
        this.showalert = true;
      })
    }else{
      for(i; i < array.length;i++) {
        id = array[i];
        let list = new Array(6)
        const url ='http://zhugexiu.us-east-1.elasticbeanstalk.com/details/'+ id;
        this.http.get(url).subscribe((res)=>{
          this.d = res['description']
          list[0]=this.d['ticker']
          list[1]=this.d['name']
        })
        const url2 ='http://zhugexiu.us-east-1.elasticbeanstalk.com/price/'+id;
        this.http.get(url2).subscribe((res)=>{
          this.showSpinner = false;
          this.showalert = false;
          this.d2 = res['description']
          console.log(this.d2)
          list[2] = this.d2[0]['last']
          list[3] = (this.d2[0]['last']-this.d2[0]['prevClose']).toFixed(2)
          list[4] = ((this.d2[0]['last']-this.d2[0]['prevClose'])*100/this.d2[0]['prevClose']).toFixed(2)
          list[5] = true;
        })
        lists.push(list);
        this.length1 = array.length;
      }
    }

    console.log(lists)
    this.ans = lists
    console.log(this.ans)
  }

  delete(ticker): void{
    console.log(ticker)
    let array = JSON.parse(localStorage.getItem('watchlist'))
    console.log(JSON.stringify(array))
    let i = 0;
    let newArray = [];
    for(i; i < array.length;i++) {
      if(array[i] != ticker)
        newArray.push(array[i])
    }
    if(newArray.length == 0)
      this.showalert = true;
    localStorage.setItem("watchlist",JSON.stringify(newArray));
  }
}
