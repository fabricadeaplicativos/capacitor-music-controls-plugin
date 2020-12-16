import { WebPlugin } from "@capacitor/core";
import { MusicControlPlugin, MusicControlOptions } from "./definitions";

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

  updateIsPlaying(isPlaying: boolean): void {
    console.log("isPlaying", isPlaying);
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
