/* eslint-disable @typescript-eslint/no-unused-vars */
import { WebPlugin } from '@capacitor/core';

import type { PermissionStatus, TogglePlayPauseResponse } from '.';
import type { MusicControlOptions, MusicControlPlugin } from './definitions';

export class MusicControlWeb extends WebPlugin implements MusicControlPlugin {
  constructor() {
    super({
      name: 'MusicControl',
      platforms: ['web'],
    });
  }

  checkPermissions(): Promise<PermissionStatus> {
    throw this.unimplemented('checkPermissions -> not implemented on web');
  }

  requestPermissions(): Promise<PermissionStatus> {
    throw this.unimplemented('requestPermissions -> not implemented on web');
  }

  async create(_options: MusicControlOptions): Promise<any> {
    throw this.unimplemented('create -> not implemented on web');
  }

  destroy(): Promise<any> {
    throw this.unimplemented('destroy -> not implemented on web');
  }

  async togglePlayPause(): Promise<TogglePlayPauseResponse> {
    throw this.unimplemented('togglePlayPause -> not implemented on web');
  }

  updateElapsed(_args: { elapsed: string; isPlaying: boolean }): void {
    throw this.unimplemented('updateElapsed -> not implemented on web');
  }

  updateDismissable(_dismissible: boolean): void {
    throw this.unimplemented('_dismissible -> not implemented on web');
  }
}
