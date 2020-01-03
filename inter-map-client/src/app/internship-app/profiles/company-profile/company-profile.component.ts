import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';
import {Internship} from '../../data/Internship';
import {CompanyProfileService} from './company-profile.service';
import {InternshipDTO} from '../../data/InternshipDTO';

@Component({
  selector: 'app-company-profile',
  templateUrl: './company-profile.component.html',
  styleUrls: ['./company-profile.component.css']
})
export class CompanyProfileComponent implements OnInit, OnDestroy {

  profileForm: FormGroup;

  subscriptions = [];
  internships: InternshipDTO[];
  private error: Error;

  constructor(private formBuilder: FormBuilder, private companyService: CompanyProfileService) {
    this.profileForm = this.formBuilder.group({
      profile: [],
      interests: [],
      about: []
    });
  }

  ngOnInit() {
    this.loadItems();
  }

  loadItems() {
    this.subscriptions.push(this.companyService.getAllInternships()
      .subscribe(internships => this.internships = internships,
        error => this.error = error));
  }

  getInternships() {
    return this.internships;
  }

  processFile(imageInput: HTMLInputElement) {
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }
}
