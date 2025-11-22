import { Component } from '@angular/core';
import { RolesListAdminComponent } from "../../../components/admin-components/roles-list-admin-component/roles-list-admin-component";
import { FooterComponent } from "../../../components/general/footer-component/footer-component";

@Component({
  selector: 'app-roles-list.page',
  imports: [RolesListAdminComponent, FooterComponent],
  templateUrl: './roles-list.page.html',
  styleUrl: './roles-list.page.css',
})
export class RolesListPage {

}
