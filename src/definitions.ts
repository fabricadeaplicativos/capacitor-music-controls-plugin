import { PluginListenerHandle } from "@capacitor/core";

declare module "@capacitor/core" {
  interface PluginRegistry {
    MusicControl: MusicControlPlugin;
  }
}

export type NotificationCallback = (info: { message: string }) => void;

export interface UpdateNotificationOptions {
  isPlaying: boolean;
}

export interface MusicControlOptions {
  track?: string;
  artist?: string;
  cover?: string;
  isPlaying?: boolean;
  dismissible?: boolean;
  hasPrevious?: boolean;
  hasNext?: boolean;
  hasSkipForward?: boolean;
  hasSkipBackward?: boolean;
  skipForwardInterval?: number;
  skipBackwardInterval?: number;
  hasScrubbing?: boolean;
  hasClose?: boolean;
  album?: string;
  duration?: number;
  elapsed?: number;
  ticker?: string;
  playIcon?: string;
  pauseIcon?: string;
  prevIcon?: string;
  nextIcon?: string;
  closeIcon?: string;
  notificationIcon?: string;
}

export interface MusicControlPlugin {
  /**
   * Create the media controls
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
   * Subscribe to the events of the media controller
   * @returns {Observable<any>}
   */
  // subscribe(): Observable<any>;
  // /**
  //  * Start listening for events, this enables the Observable from the subscribe method
  //  */
  // listen(): void;

  /**
   * Toggle play/pause:
   * @param args {UpdateNotificationOptions}
   */
  updateIsPlaying(args: UpdateNotificationOptions): void;

  /**
   * Update elapsed time, optionally toggle play/pause:
   * @param args {Object}
   */
  updateElapsed(args: { elapsed: string; isPlaying: boolean }): void;

  /**
   * Toggle dismissible:
   * @param dismissible {boolean}
   */
  updateDismissable(dismissible: boolean): void;

  addListener(
    eventName: "controlsNotification",
    listenerFunc: (info: { message: string }) => void
  ): PluginListenerHandle;
}
