import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { LabService } from '../../services/lab/lab.service';
import { combineLatest } from 'rxjs';
import { Lab } from '../../models/lab.model';
import { UserService } from '../../services/user/user.service';
import { Team } from '../../models/team.model';

@Component({
  selector: 'app-lab-settings',
  templateUrl: './lab-settings.component.html',
  styleUrl: './lab-settings.component.scss'
})
export class LabSettingsComponent implements OnInit {
  labId: string = '';
  labName: string = '';

  admin = false;

  constructor(private route: ActivatedRoute, private labService: LabService, private userService: UserService) { }

  ngOnInit(): void {
    combineLatest([
      this.route.paramMap,
      this.route.queryParamMap
    ]).subscribe(([paramMap, queryParamMap]) => {
      this.labName = paramMap.get('name')!;
      this.labId = queryParamMap.get('id')!;
      console.log(this.labName, this.labId);

      this.labService.getLabById(this.labId).subscribe((lab: Lab) => {
        if (!lab) return;

        console.log('lab', lab);
      });
    });

    this.userService.getLoggedInUser().subscribe((user) => {
      if (!user) return;

      console.log('user', user);

      let team: Team = user.team!;
    });
  }
}
