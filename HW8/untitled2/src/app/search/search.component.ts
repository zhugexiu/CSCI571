import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import {FormControl} from '@angular/forms';
import {debounceTime, distinctUntilChanged, map, startWith, switchMap} from 'rxjs/operators';
import {HttpClient} from "@angular/common/http";


import { NavigationExtras, Router } from '@angular/router';

export interface User {
  ticker: string;
  name: string;
}

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent implements OnInit {

  myControl = new FormControl();
  options: User[] = [];
  filteredOptions: Observable<User[]>;
  private searchTicker: string;
  show: boolean;


  constructor(
    private http: HttpClient,
    private router: Router,
  ) {
  }

  ngOnInit(): void {
    this.get()
  }

  lookup(ticker: string) : Observable<User[]>{
    return this.http.get('http://zhugexiu.us-east-1.elasticbeanstalk.com/auto/'+ ticker).pipe(
      map((data:any) => {
        this.show = false;
        return data.map((entry:any) => ({
          ticker: entry.ticker,
          name: entry.name
        }as User));
      })
    );
  }

  displayFn(user: User): string {
    return user && user.ticker ? user.ticker : '';
  }

  finds(): void{
    this.searchTicker = (<HTMLInputElement>document.getElementById("input")).value.toUpperCase();
    const Url = '/detail/' + this.searchTicker;
    this.router.navigate([Url])
  }

  private get() {
    this.filteredOptions = this.myControl.valueChanges
      .pipe(
        startWith(''),
        debounceTime(500),
        distinctUntilChanged(),
        switchMap(ticker =>{
          if(ticker !== ''){
            this.show = true;
            return this.lookup(ticker);
          }else{
            return [];
          }
        })
      );
  }
}
