import { registerPlugin } from '@capacitor/core';
const MusicControl = registerPlugin('MusicControl', {
    web: () => import('./web').then(m => new m.MusicControlWeb()),
});
export * from './definitions';
export { MusicControl };
//# sourceMappingURL=index.js.map