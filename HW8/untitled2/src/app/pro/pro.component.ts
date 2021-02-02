import { Component, OnInit } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {ActivatedRoute} from "@angular/router";
import {ModalDismissReasons, NgbModal} from "@ng-bootstrap/ng-bootstrap";


@Component({
  selector: 'app-pro',
  templateUrl: './pro.component.html',
  styleUrls: ['./pro.component.css']
})
export class ProComponent implements OnInit {
  d2:any = []
  ans:any = [];
  private buyQ: number;
  private closeResult: string;
  private SellQ: number;
  showSpinner:boolean;
  showAlert: boolean;
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
      this.closeResult = `Dismissed ${ProComponent.getDismissReason(reason)}`;
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

  ngOnInit(): void {
    this.showSpinner = true;
    let array = JSON.parse(localStorage.getItem('portfolio'))
    let i = 0
    let lists= [];
    if(array.length == 0){
      const url2 ='http://zhugexiu.us-east-1.elasticbeanstalk.com/price/aapl';
      this.http.get(url2).subscribe((res)=>{
        this.showSpinner = false;
        this.showAlert = true;
      })
    }else{
      for(i; i < array.length;i++) {
        let list = new Array(9)
        list[0] = array[i]['ticker'];
        list[1] = array[i]['name'];
        list[2] = array[i]['quantity']
        list[6] = (array[i]['total']/1).toFixed(2)
        list[4] = (array[i]['total']/array[i]['quantity']).toFixed(2)
        list[8] = true;
        const url2 ='http://zhugexiu.us-east-1.elasticbeanstalk.com/price/'+array[i]['ticker'];
        this.http.get(url2).subscribe((res)=>{
          this.showSpinner = false;
          this.showAlert = false;
          this.d2 = res['description']
          list[5] = this.d2[0]['last']
          list[3] = (this.d2[0]['last'] - list[4]).toFixed(2)
          list[7] = (list[5] * list[2]).toFixed(2)
        })
        lists.push(list);
      }
    }
    console.log(lists)
    this.ans = lists

  }

  onKey(event: KeyboardEvent) {
    this.buyQ = parseInt((event.target as HTMLInputElement).value);
  }
  onKey2(event: KeyboardEvent) {
    this.SellQ = parseInt((event.target as HTMLInputElement).value);
  }

  buyStock(ticker,price,buyQ): void{

    let array2 = JSON.parse(localStorage.getItem('portfolio'))
    let i = 0;
    let total = price * buyQ
    let exist = 0
    for(i; i < array2.length;i++) {
      if(array2[i]['ticker'] == ticker){
        exist = 1;
        total += parseInt(array2[i]['total'])
        buyQ += array2[i]['quantity']
        array2[i] = {
          "ticker" : ticker,
          "name" : name,
          "quantity" : buyQ,
          "total": total.toFixed(2)
        }
      }
    }
    if(exist == 0) {
      array2.push({
        "ticker" : ticker,
        "name" : name,
        "quantity" : buyQ,
        "total": total.toFixed(2)
      })
    }
    localStorage.setItem("portfolio",JSON.stringify(array2));

  }

  sellStock(ticker, price, sellQ): void{
    let array3 = JSON.parse(localStorage.getItem('portfolio'))
    let i = 0;
    let total = price * sellQ
    for(i; i < array3.length;i++) {
      if(array3[i]['ticker'] == ticker){
        total = parseInt(array3[i]['total']) -total
        sellQ = array3[i]['quantity'] - sellQ
        if(sellQ == 0) {
          array3.splice(i, 1);
          if(array3.length == 0)
            this.showAlert = true;
        }else{
          array3[i] = {
            "ticker" : ticker,
            "name" : name,
            "quantity" : sellQ,
            "total": total.toFixed(2)
          }
        }

      }
    }
    localStorage.setItem("portfolio",JSON.stringify(array3));
  }
}
