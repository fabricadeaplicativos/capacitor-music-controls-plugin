'use strict';

var core = require('@capacitor/core');

exports.MusicControlEvents = void 0;
(function (MusicControlEvents) {
    MusicControlEvents["FINISHED"] = "songFinished";
    MusicControlEvents["IS_PLAYING"] = "isPlaying";
    MusicControlEvents["NOTIFICATION_ACTIONS"] = "mediaActions";
    MusicControlEvents["TIME_UPDATED"] = "timeUpdated";
    MusicControlEvents["MUSIC_LOADED"] = "musicLoaded";
})(exports.MusicControlEvents || (exports.MusicControlEvents = {}));

const MusicControl = core.registerPlugin('MusicControl', {
    web: () => Promise.resolve().then(function () { return web; }).then(m => new m.MusicControlWeb()),
});

/* eslint-disable @typescript-eslint/no-unused-vars */
var AudioEvents;
(function (AudioEvents) {
    AudioEvents["PLAYING"] = "playing";
    AudioEvents["PAUSE"] = "pause";
    AudioEvents["TIMEUPDATE"] = "timeupdate";
    AudioEvents["LOADSTART"] = "loadstart";
    AudioEvents["LOADEDDATA"] = "loadeddata";
    AudioEvents["ENDED"] = "ended";
    AudioEvents["SEEKED"] = "seeked";
    AudioEvents["SEEKING"] = "seeking";
})(AudioEvents || (AudioEvents = {}));
class MusicControlWeb extends core.WebPlugin {
    constructor() {
        super(...arguments);
        this.webAudioPlayer = null;
        this.autoPlay = true;
    }
    checkPermissions() {
        throw this.unimplemented('checkPermissions -> not implemented on web');
    }
    requestPermissions() {
        throw this.unimplemented('requestPermissions -> not implemented on web');
    }
    async create(info) {
        if (this.webAudioPlayer) {
            this.webAudioPlayer.pause();
            this.webAudioPlayer = null;
        }
        this.autoPlay = info.autoPlay !== null ? !!info.autoPlay : true;
        this.webAudioPlayer = new Audio(info.url);
        this.setListeners();
    }
    setListeners() {
        this.webAudioPlayer.addEventListener(AudioEvents.PLAYING, () => this.notifyListeners(exports.MusicControlEvents.IS_PLAYING, { isPlaying: true }));
        this.webAudioPlayer.addEventListener(AudioEvents.PAUSE, () => this.notifyListeners(exports.MusicControlEvents.IS_PLAYING, { isPlaying: false }));
        this.webAudioPlayer.addEventListener(AudioEvents.ENDED, () => this.notifyListeners(exports.MusicControlEvents.FINISHED, { isPlaying: false }));
        this.webAudioPlayer.addEventListener(AudioEvents.TIMEUPDATE, () => {
            var _a;
            return this.notifyListeners(exports.MusicControlEvents.TIME_UPDATED, {
                currentTime: (_a = this.webAudioPlayer) === null || _a === void 0 ? void 0 : _a.currentTime,
            });
        });
        this.webAudioPlayer.addEventListener(AudioEvents.LOADEDDATA, () => {
            this.notifyListeners(exports.MusicControlEvents.MUSIC_LOADED, {
                duration: this.webAudioPlayer.duration,
            });
            if (this.autoPlay) {
                this.webAudioPlayer.play();
            }
        });
    }
    destroy() {
        if (this.webAudioPlayer) {
            this.webAudioPlayer.pause();
            this.webAudioPlayer = null;
        }
    }
    async togglePlayPause() {
        if (!this.webAudioPlayer) {
            throw Error("player isn't ready");
        }
        if (this.webAudioPlayer.paused) {
            this.webAudioPlayer.play();
        }
        else {
            this.webAudioPlayer.pause();
        }
        return {
            isPlaying: !this.webAudioPlayer.paused,
        };
    }
    updateElapsed(_args) {
        throw this.unimplemented('updateElapsed -> not implemented on web');
    }
    updateDismissable(_dismissible) {
        throw this.unimplemented('_dismissible -> not implemented on web');
    }
    jumpTo(data) {
        this.webAudioPlayer.currentTime = data.time;
    }
}

var web = /*#__PURE__*/Object.freeze({
    __proto__: null,
    MusicControlWeb: MusicControlWeb
});

exports.MusicControl = MusicControl;
//# sourceMappingURL=plugin.cjs.js.map
