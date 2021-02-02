import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {ModalDismissReasons, NgbModal} from "@ng-bootstrap/ng-bootstrap";
import { interval } from 'rxjs';
import {Subject,Subscription} from "rxjs";
declare var $:any;

@Component({
  selector: 'app-result',
  templateUrl: './result.component.html',
  styleUrls: ['./result.component.css']
})
export class ResultComponent implements OnInit {

   now: any = ""
   d:any = []
   d2:any = []
   d3:any
   change:any = ""
   changePercent:any = ""
   close:any = ""
   cha: any =""
   close2:any = ""
   exist:any = ""
   private closeResult: string;
   private buyQ: number;
   private subscription: Subscription;
   showSpinner:boolean;
   valid:any=""

  constructor(
    private http: HttpClient,
    private route: ActivatedRoute,
    private modalService: NgbModal,
  ) { }

  httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
  }


  open(content) {
    this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title'}).result.then((result) => {
      this.closeResult = `Closed with: ${result}`;
    }, (reason) => {
      this.closeResult = `Dismissed ${ResultComponent.getDismissReason(reason)}`;
    });
  }

  private static getDismissReason(reason: any): string {
    if (reason === ModalDismissReasons.ESC) {
      return 'by pressing ESC';
    } else if (reason === ModalDismissReasons.BACKDROP_CLICK) {
      return 'by clicking on a backdrop';
    } else {
      return  `with: ${reason}`;
    }
  }

  ngOnInit(): void{
    this.showSpinner = true;
    this.getStock();
    this.getSummary2();
    const source = interval(15000);
    this.subscription = source.subscribe(() => this.getSummary2());
    let jsonArray = JSON.parse(localStorage.getItem('watchlist'))
    const ticker = this.route.snapshot.paramMap.get('ticker');
    let i = 0;
    this.exist = 0;
    for(i; i < jsonArray.length;i++) {
      if(jsonArray[i] == ticker){
        this.exist = 1;
        break;
      }
    }
  }

  getStock(): void{
    const id = this.route.snapshot.paramMap.get('ticker');
    const url ='http://zhugexiu.us-east-1.elasticbeanstalk.com/details/'+ id;
    this.http.get(url).subscribe((res)=>{
      this.d = res
      console.log(this.d)
      if(this.d['description'].hasOwnProperty('detail') ){
        this.valid = 0;
        this.showSpinner = false;
      }else{
        this.valid = 1;
      }
    })
  }

  getSummary2(): void{
    const id = this.route.snapshot.paramMap.get('ticker');
    const url ='http://zhugexiu.us-east-1.elasticbeanstalk.com/price/'+id;
    const date = new Date();

    this.http.get(url).subscribe((res)=>{
      if(res['description'].length !=0){
        this.d2 = res['description'][0]
        this.d3 = res['description'].length
        this.change = (res['description'][0]['last'] - res['description'][0]['prevClose']).toFixed(2)
        this.changePercent = (this.change/100 * res['description'][0]['prevClose']).toFixed(2)
        this.now = this.dateToStrSlash2(date)
        this.close = this.dateToStrSlash2(new Date(this.d2['timestamp']));
        this.close2 = new Date(this.d2['timestamp']);
        this.cha = Math.abs(date.getTime()-this.close2.getTime())/1000;
        console.log(this.close)
        this.showSpinner = false;
      }

    })
  }


  dateToStrSlash2(date) {
    const Y = date.getFullYear() + '-';
    const M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-';
    const D = (date.getDate() < 10 ? '0' + date.getDate() : date.getDate()) + ' ';
    const h = (date.getHours() < 10 ? '0' + date.getHours() : date.getHours()) + ':';
    const m = (date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes()) + ':';
    const s = (date.getSeconds() < 10 ? '0' + date.getSeconds() : date.getSeconds());
    return Y + M + D + h + m + s;
  }


   private _success = new Subject<string>();
   save(): void {
     this.getStock()
     const ticker = this.d['description']['ticker']
     console.log(JSON.stringify(ticker))
     let array = JSON.parse(localStorage.getItem('watchlist'))
     array.push(ticker)
     localStorage.setItem("watchlist",JSON.stringify(array));
     this._success.next(`${new Date()} - Message successfully changed.`);

     $("#exist1").removeClass("d-none");
     setTimeout(function () {
       if (!$("#exist0").hasClass("d-none"))
         $("#exist0").addClass("d-none");
     }, 5);

     $("#added-success").removeClass("d-none");
     setTimeout(function () {
       if (!$("#added-success").hasClass("d-none"))
         $("#added-success").addClass("d-none");
     }, 5000);
  }

  delete(): void{
    const ticker = this.route.snapshot.paramMap.get('ticker');
    let array = JSON.parse(localStorage.getItem('watchlist'))
    console.log(JSON.stringify(array))
    let i = 0;
    let newArray = [];
    for(i; i < array.length;i++) {
      if(array[i] != ticker)
        newArray.push(array[i])
    }
    localStorage.setItem("watchlist",JSON.stringify(newArray));
    $("#exist0").removeClass("d-none");
    setTimeout(function () {
      if (!$("#exist1").hasClass("d-none"))
        $("#exist1").addClass("d-none");
    }, 5);
    $("#removed-success").removeClass("d-none");
    setTimeout(function () {
      if (!$("#removed-success").hasClass("d-none"))
        $("#removed-success").addClass("d-none");
    }, 5000);
  }

  onKey(event: KeyboardEvent) {
    this.buyQ = parseInt((event.target as HTMLInputElement).value);
  }

  buyStock(buyQ): void{
    this.getStock()
    const ticker = this.d['description']['ticker']
    const name = this.d['description']['name']
    this.getSummary2()
    const price = this.d2['last']
    let array = JSON.parse(localStorage.getItem('portfolio'))
    let i = 0;
    let total = price * buyQ
    let exist = 0
    for(i; i < array.length;i++) {
      if(array[i]['ticker'] == ticker){
        exist = 1;
        total += parseInt(array[i]['total'])
        buyQ += array[i]['quantity']
        array[i] = {
          "ticker" : ticker,
          "name" : name,
          "quantity" : buyQ,
          "total": total.toFixed(2)
        }
      }
    }
    if(exist == 0) {
      array.push({
        "ticker" : ticker,
        "name" : name,
        "quantity" : buyQ,
        "total": total.toFixed(2)
      })
    }
    localStorage.setItem("portfolio",JSON.stringify(array));
  }

}
