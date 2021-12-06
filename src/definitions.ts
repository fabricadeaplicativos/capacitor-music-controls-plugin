import type { PluginListenerHandle } from '@capacitor/core';

export enum MusicControlEvents {
  FINISHED = 'songFinished',
  IS_PLAYING = 'isPlaying',
  NOTIFICATION_ACTIONS = 'mediaActions',
}

export interface TogglePlayPauseResponse {
  isPlaying: boolean;
}

export interface MusicControlOptions {
  album: string;
  artist: string;
  cover?: string;
  track: string;
  url: string;
}

export interface MusicControlPlugin {
  checkPermissions(): Promise<PermissionStatus>;
  requestPermissions(): Promise<PermissionStatus>;

  /**
   * Creates the notification and plays the url on a native media player
   * @param options {MusicControlsOptions}
   * @returns {Promise<any>}
   */
  create(options: MusicControlOptions): Promise<any>;

  /**
   * Destroy the media controller
   * @returns {Promise<any>}
   */
  destroy(): Promise<any>;

  /**
   * Toggle play/pause:
   * @returns {Promise<TogglePlayPauseResponse>}
   */
  togglePlayPause(): Promise<TogglePlayPauseResponse>;

  addListener(
    eventName: MusicControlEvents,
    listenerFunc: (info: any) => void,
  ): PluginListenerHandle;
}

export interface PermissionStatus {
  wake_lock: PermissionState;
}
