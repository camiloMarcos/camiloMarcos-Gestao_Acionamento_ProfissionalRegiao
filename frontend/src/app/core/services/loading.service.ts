import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class LoadingService {
  private pendingRequests = 0;
  readonly active = signal(false);

  start(): void {
    this.pendingRequests += 1;
    this.active.set(true);
  }

  stop(): void {
    this.pendingRequests = Math.max(0, this.pendingRequests - 1);
    this.active.set(this.pendingRequests > 0);
  }
}
