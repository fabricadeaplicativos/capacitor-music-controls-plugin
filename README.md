<h2 align="center">Capacitor Music Control</h2>
<p align="center"><strong><code>capacitor-music-control</code></strong></p>
<p align="center">
  This is a <a href="https://capacitor.ionicframework.com">Capacitor</a> plugin that allows you to display a media notification like the one in the image on both <strong>Android</strong> and <strong>iOS</strong>, and also handling headset events on <strong>Android</strong>;</a>.
</p>
<p align="center">
	<br>
	<img src="https://i.imgur.com/UmzZcuU.png" />
</p>

This plugin is forked from a fork of the original Cordova plugin that can be found here:
https://github.com/ingageco/capacitor-music-controls-plugin

## Installation

Using npm:

```console
npm install capacitor-music-control
```

Using yarn:

```console
yarn add capacitor-music-control
```

Sync native files to capacitor:

```bash
npx cap sync
```

No further steps needed on iOS.

For Android just register the plugin in your MainActivity:

```java
import com.selcip.capacitor.MusicControl.MusicControl;

[...]

public class MainActivity extends BridgeActivity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	this.init(savedInstanceState, new ArrayList<Class<? extends Plugin>>() {{
      [...]
      add(MusicControl.class);
    }});
  }
}
```

## Configuration

No configuration is required for this plugin.

## Usage

```typescript
import { Plugins } from "@capacitor/core";

const { MusicControl } = Plugins;

/**
 * Create the media controls
 * */
MusicControl.create(
  {
    track: "Time is Running Out", // optional, default : ''
    artist: "Muse", // optional, default : ''
    album: "Absolution", // optional, default: ''
    cover: "albums/absolution.jpg", // optional, default : nothing
    // cover can be a local path (use fullpath 'file:///storage/emulated/...', or only 'my_image.jpg' if my_image.jpg is in the www folder of your app)
    //			 or a remote url ('http://...', 'https://...', 'ftp://...')

    // hide previous/next/close buttons:
    hasPrev: false, // show previous button, optional, default: true
    hasNext: false, // show next button, optional, default: true
    hasClose: true, // show close button, optional, default: false

    // iOS only, optional
    duration: 60, // optional, default: 0
    elapsed: 10, // optional, default: 0
    hasSkipForward: true, //optional, default: false. true value overrides hasNext.
    hasSkipBackward: true, //optional, default: false. true value overrides hasPrev.
    skipForwardInterval: 15, //optional. default: 15.
    skipBackwardInterval: 15, //optional. default: 15.
    hasScrubbing: false, //optional. default to false. Enable scrubbing from control center progress bar

    // Android only, optional
    isPlaying: true, // optional, default : true
    dismissible: true, // optional, default : false
    // text displayed in the status bar when the notification (and the ticker) are updated
    ticker: 'Now playing "Time is Running Out"',
    //All icons default to their built-in android equivalents
    //The supplied drawable name, e.g. 'media_play', is the name of a drawable found under android/res/drawable* folders
    playIcon: "media_play",
    pauseIcon: "media_pause",
    prevIcon: "media_prev",
    nextIcon: "media_next",
    closeIcon: "media_close",
    notificationIcon: "notification",
  },
  onSuccess,
  onError
);

/**
 * Update whether the music is playing true/false, as well as the time elapsed (seconds)
 * */
MusicControl.updateIsPlaying({
  isPlaying: true, // affects Android only
  elapsed: timeElapsed, // affects iOS Only
});

/**
 * Listen for events and pass them to your handler function
 * */
MusicControl.addListener("controlsNotification", (info: any) => {
  console.log("controlsNotification was fired");
  console.log(info);
  handleControlsEvent(info);
});

/**
 * Example event handler
 * */
handleControlsEvent(action){

	console.log("hello from handleControlsEvent")
	const message = action.message;

	console.log("message: " + message)

	switch(message) {
		case 'music-controls-next':
			// next
			break;
		case 'music-controls-previous':
			// previous
			break;
		case 'music-controls-pause':
			// paused
			break;
		case 'music-controls-play':
			// resumed
			break;
		case 'music-controls-destroy':
			// controls were destroyed
			break;

		// External controls (iOS only)
		case 'music-controls-toggle-play-pause' :
			// do something
			break;
		case 'music-controls-seek-to':
			// do something
			break;
		case 'music-controls-skip-forward':
			// Do something
			break;
		case 'music-controls-skip-backward':
			// Do something
			break;

		// Headset events (Android only)
		// All media button events are listed below
		case 'music-controls-media-button' :
			// Do something
			break;
		case 'music-controls-headset-unplugged':
			// Do something
			break;
		case 'music-controls-headset-plugged':
			// Do something
			break;
		default:
			break;
	}
}
```

## Credits

Original plugin by [ingageco](https://github.com/ingageco)

Original _cordova_ plugin by [homerours](https://github.com/homerours)

Documentation shamelessly copied from [capacitor-community](https://github.com/capacitor-community)
