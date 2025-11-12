import { Component } from '@angular/core';
import { Hero } from "../../components/hero/hero";
import { EspecialistaBanner } from "../../components/especialista-banner/especialista-banner";

@Component({
  selector: 'app-home',
  imports: [Hero, EspecialistaBanner],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {

}
