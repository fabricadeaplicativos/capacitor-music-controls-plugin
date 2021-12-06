import type { PluginListenerHandle } from '@capacitor/core';
export declare type NotificationCallback = (info: {
    message: string;
}) => void;
export interface TogglePlayPauseResponse {
    isPlaying: boolean;
}
export interface UpdateIsPlaying {
    isPlaying: boolean;
}
export interface MusicControlOptions {
    track?: string;
    artist?: string;
    cover?: string;
    hasNext?: boolean;
    url: string;
    album?: string;
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
     * Manually play/pause
     * @param args {UpdateIsPlaying}
     */
    updateIsPlaying(args: UpdateIsPlaying): Promise<TogglePlayPauseResponse>;
    /**
     * Toggle play/pause:
     * @returns {Promise<TogglePlayPauseResponse>}
     */
    togglePlayPause(): Promise<TogglePlayPauseResponse>;
    /**
     * Update elapsed time, optionally toggle play/pause:
     * @param args {Object}
     */
    updateElapsed(args: {
        elapsed: string;
        isPlaying: boolean;
    }): void;
    /**
     * Toggle dismissible:
     * @param dismissible {boolean}
     */
    updateDismissable(dismissible: boolean): void;
    addListener(eventName: string, listenerFunc: (info: any) => void): PluginListenerHandle;
}
export interface PermissionStatus {
    wake_lock: PermissionState;
}
