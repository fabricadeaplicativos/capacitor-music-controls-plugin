/* eslint-disable @typescript-eslint/no-unused-vars */
import { WebPlugin } from '@capacitor/core';

import type {
  MusicControlOptions,
  MusicControlPlugin,
  PermissionStatus,
  TogglePlayPauseResponse,
} from './definitions';
import { MusicControlEvents } from './definitions';

enum AudioEvents {
  PLAYING = 'playing',
  PAUSE = 'pause',
  TIMEUPDATE = 'timeupdate',
  LOADSTART = 'loadstart',
  LOADEDDATA = 'loadeddata',
  ENDED = 'ended',
  SEEKED = 'seeked',
  SEEKING = 'seeking',
}

export class MusicControlWeb extends WebPlugin implements MusicControlPlugin {
  private webAudioPlayer: any = null;
  private autoPlay = true;

  checkPermissions(): Promise<PermissionStatus> {
    throw this.unimplemented('checkPermissions -> not implemented on web');
  }

  requestPermissions(): Promise<PermissionStatus> {
    throw this.unimplemented('requestPermissions -> not implemented on web');
  }

  async create(info: MusicControlOptions): Promise<any> {
    if (this.webAudioPlayer) {
      this.webAudioPlayer.pause();
      this.webAudioPlayer = null;
    }

    this.autoPlay = info.autoPlay !== null ? !!info.autoPlay : true;
    this.webAudioPlayer = new Audio(info.url);
    this.setListeners();
  }

  setListeners(): void {
    this.webAudioPlayer.addEventListener(AudioEvents.PLAYING, () =>
      this.notifyListeners(MusicControlEvents.IS_PLAYING, { isPlaying: true }),
    );

    this.webAudioPlayer.addEventListener(AudioEvents.PAUSE, () =>
      this.notifyListeners(MusicControlEvents.IS_PLAYING, { isPlaying: false }),
    );

    this.webAudioPlayer.addEventListener(AudioEvents.ENDED, () =>
      this.notifyListeners(MusicControlEvents.FINISHED, { isPlaying: false }),
    );

    this.webAudioPlayer.addEventListener(AudioEvents.TIMEUPDATE, () =>
      this.notifyListeners(MusicControlEvents.TIME_UPDATED, {
        currentTime: this.webAudioPlayer?.currentTime,
      }),
    );

    this.webAudioPlayer.addEventListener(AudioEvents.LOADEDDATA, () => {
      this.notifyListeners(MusicControlEvents.MUSIC_LOADED, {
        duration: this.webAudioPlayer.duration,
      });

      if (this.autoPlay) {
        this.webAudioPlayer.play();
      }
    });
  }

  destroy(): void {
    if (this.webAudioPlayer) {
      this.webAudioPlayer.pause();
      this.webAudioPlayer = null;
    }
  }

  async togglePlayPause(): Promise<TogglePlayPauseResponse> {
    if (!this.webAudioPlayer) {
      throw Error("player isn't ready");
    }

    if (this.webAudioPlayer.paused) {
      this.webAudioPlayer.play();
    } else {
      this.webAudioPlayer.pause();
    }

    return {
      isPlaying: !this.webAudioPlayer.paused,
    };
  }

  updateElapsed(_args: { elapsed: string; isPlaying: boolean }): void {
    throw this.unimplemented('updateElapsed -> not implemented on web');
  }

  updateDismissable(_dismissible: boolean): void {
    throw this.unimplemented('_dismissible -> not implemented on web');
  }
}
