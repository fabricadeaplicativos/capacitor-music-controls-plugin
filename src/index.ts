import { registerPlugin } from '@capacitor/core';

import type { MusicControlPlugin } from './definitions';

const MusicControl = registerPlugin<MusicControlPlugin>('MusicControl', {
  web: () => import('./web').then(m => new m.MusicControlWeb()),
});

export * from './definitions';
export { MusicControl };
