import { WebPlugin } from "@capacitor/core";
import {
  MusicControlPlugin,
  MusicControlOptions,
  UpdateNotificationOptions,
} from "./definitions";

export class MusicControlWeb extends WebPlugin implements MusicControlPlugin {
  constructor() {
    super({
      name: "MusicControl",
      platforms: ["web"],
    });
  }

  create(options: MusicControlOptions): Promise<any> {
    console.log(options);
    return new Promise((resolve) => resolve("any"));
  }

  destroy(): Promise<any> {
    return new Promise((resolve) => resolve("any"));
  }

  updateIsPlaying(args: UpdateNotificationOptions): void {
    console.log("isPlaying", args);
  }

  updateElapsed(args: { elapsed: string; isPlaying: boolean }): void {
    console.log(args);
  }

  updateDismissable(dismissible: boolean): void {
    console.log(dismissible);
  }
}

const MusicControl = new MusicControlWeb();

export { MusicControl };

import { registerWebPlugin } from "@capacitor/core";
registerWebPlugin(MusicControl);
