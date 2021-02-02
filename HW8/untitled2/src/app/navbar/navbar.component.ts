import { Component, OnInit } from '@angular/core';
import {NavigationEnd, Router} from "@angular/router";
import {Location} from '@angular/common'
declare var $:any;

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {
  active;

  constructor(
    private router:Router
  ) { }

  ngOnInit(): void {
    this.router.events.
      subscribe((event)=>{
        if(event instanceof NavigationEnd) {
          if(location.pathname ==='/') this.active = 1;
          else if(location.pathname == '/watchlist') this.active = 2;
          else if(location.pathname == '/portfolio') this.active = 3;
          else this.active = 0;
        }
    });
  }

}
