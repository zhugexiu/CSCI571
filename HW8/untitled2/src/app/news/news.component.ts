import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient, HttpClientModule} from "@angular/common/http";
import { NgbActiveModal, NgbModal,ModalDismissReasons } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-news',
  templateUrl: './news.component.html',
  styleUrls: ['./news.component.css']
})
export class NewsComponent implements OnInit {

  news: any = [];

  title = 'appBootstrap';
  closeResult: string;

  constructor(
    private http: HttpClient,
    private route: ActivatedRoute,
    private modalService: NgbModal,
  ) { }

  open(content) {
    this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title'}).result.then((result) => {
      this.closeResult = `Closed with: ${result}`;
    }, (reason) => {
      this.closeResult = `Dismissed ${NewsComponent.getDismissReason(reason)}`;
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
    this.getNews()
  }

  getNews(): void{
    const id = this.route.snapshot.paramMap.get('ticker');
    const url ='http://zhugexiu.us-east-1.elasticbeanstalk.com/news/'+id
    this.http.get(url).subscribe((res)=>{
      this.news = res
      console.log(this.news)
    })
  }

}
