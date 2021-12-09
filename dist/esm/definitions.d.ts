import type { PluginListenerHandle } from '@capacitor/core';
export declare enum MusicControlEvents {
    FINISHED = "songFinished",
    IS_PLAYING = "isPlaying",
    NOTIFICATION_ACTIONS = "mediaActions",
    TIME_UPDATED = "timeUpdated",
    MUSIC_LOADED = "musicLoaded"
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
    autoPlay?: boolean;
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
     * @returns {void}
     */
    destroy(): void;
    /**
     * Toggle play/pause:
     * @returns {Promise<TogglePlayPauseResponse>}
     */
    togglePlayPause(): Promise<TogglePlayPauseResponse>;
    addListener(eventName: MusicControlEvents.IS_PLAYING | MusicControlEvents.FINISHED, listenerFunc: (info: {
        isPlaying: boolean;
    }) => void): Promise<PluginListenerHandle>;
    addListener(eventName: MusicControlEvents.TIME_UPDATED, listenerFunc: (info: {
        currentTime: number;
    }) => void): Promise<PluginListenerHandle>;
    addListener(eventName: MusicControlEvents.MUSIC_LOADED, listenerFunc: (info: {
        duration: number;
    }) => void): Promise<PluginListenerHandle>;
    addListener(eventName: MusicControlEvents.NOTIFICATION_ACTIONS, listenerFunc: (info: {
        action: 'play' | 'pause' | 'previous' | 'next' | 'destroy';
    }) => void): Promise<PluginListenerHandle>;
}
export interface PermissionStatus {
    wake_lock: PermissionState;
}
