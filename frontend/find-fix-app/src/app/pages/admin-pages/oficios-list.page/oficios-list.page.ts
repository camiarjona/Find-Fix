import { Component } from '@angular/core';
import { OficiosFormAdminComponent } from "../../../components/admin-components/oficios-form-admin-component/oficios-form-admin-component";
import { OficiosListAdminComponent } from "../../../components/admin-components/oficios-list-admin-component/oficios-list-admin-component";

@Component({
  selector: 'app-oficios-list.page',
  imports: [OficiosFormAdminComponent, OficiosListAdminComponent],
  templateUrl: './oficios-list.page.html',
  styleUrl: './oficios-list.page.css',
})
export class OficiosListPage {

}
