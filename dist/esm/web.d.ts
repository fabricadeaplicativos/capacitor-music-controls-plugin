import { WebPlugin } from '@capacitor/core';
import type { MusicControlOptions, MusicControlPlugin, PermissionStatus, TogglePlayPauseResponse, JumpToObject } from './definitions';
export declare class MusicControlWeb extends WebPlugin implements MusicControlPlugin {
    private webAudioPlayer;
    private autoPlay;
    checkPermissions(): Promise<PermissionStatus>;
    requestPermissions(): Promise<PermissionStatus>;
    create(info: MusicControlOptions): Promise<any>;
    setListeners(): void;
    destroy(): void;
    togglePlayPause(): Promise<TogglePlayPauseResponse>;
    updateElapsed(_args: {
        elapsed: string;
        isPlaying: boolean;
    }): void;
    updateDismissable(_dismissible: boolean): void;
    jumpTo(data: JumpToObject): any;
}
