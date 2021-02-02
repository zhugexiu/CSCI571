import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Summary1Component } from './summary1.component';

describe('Summary1Component', () => {
  let component: Summary1Component;
  let fixture: ComponentFixture<Summary1Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ Summary1Component ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(Summary1Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
