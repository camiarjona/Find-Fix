import { Component } from '@angular/core';
import { Hero } from "../../components/landing-page/hero/hero";
import { EspecialistaBanner } from "../../components/landing-page/especialista-banner/especialista-banner";
import { NavBarLanding } from "../../components/landing-page/nav-bar-landing/nav-bar-landing";

@Component({
  selector: 'app-home',
  imports: [Hero, EspecialistaBanner, NavBarLanding],
  templateUrl: './home.page.html',
  styleUrl: './home.page.css',
})
export class Home {

}
