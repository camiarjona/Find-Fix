import { AfterViewInit, Component, EventEmitter, NgZone, Output, inject } from '@angular/core';
import { environment } from '../../../environments/environment';

declare var google: any;

@Component({
  selector: 'app-google-login-button',
  imports: [],
  templateUrl: './google-login-button.html',
  styleUrl: './google-login-button.css',
})
export class GoogleLoginButton implements AfterViewInit {

  private ngZone = inject(NgZone);

  @Output() googleToken = new EventEmitter<string>();

  ngAfterViewInit(): void {
    if (typeof google === 'undefined') {
      console.error('Google API not loaded');
      return;
    }

    google.accounts.id.initialize({
      client_id: environment.googleClientId,
      callback: (response: any) => this.handleGoogleCredential(response),
    });

    const btnContainer = document.getElementById('google-btn');

    if (btnContainer) {
      google.accounts.id.renderButton(btnContainer, {
        theme: 'outline',
        size: 'large',
        width: '350',
        text: 'continue_with',
        shape: 'rectangular',
        logo_alignment: 'left',
      });
    }
  }

  handleGoogleCredential(response: any): void {
    this.ngZone.run(() => {
      if (response.credential) {
        this.googleToken.emit(response.credential);
      }
    });
  }
}
