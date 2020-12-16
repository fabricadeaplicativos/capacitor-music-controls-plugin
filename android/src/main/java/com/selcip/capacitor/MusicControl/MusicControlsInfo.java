package com.selcip.capacitor.MusicControl;

import com.getcapacitor.JSObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MusicControlsInfo {
    public String artist;
    public String album;
    public String track;
    public String ticker;
    public String cover;
    public boolean isPlaying;
    public boolean hasPrev;
    public boolean hasNext;
    public boolean hasClose;
    public boolean dismissible;
    public String playIcon;
    public String pauseIcon;
    public String prevIcon;
    public String nextIcon;
    public String closeIcon;
    public String notificationIcon;

    public MusicControlsInfo(JSObject args) throws JSONException {
        this.track = args.getString("track");
        this.artist = args.getString("artist");
        this.album = args.getString("album");
        this.ticker = args.getString("ticker");
        this.cover = args.getString("cover");
        this.isPlaying = !args.has("isPlaying") || args.getBoolean("isPlaying");
        this.hasPrev = !args.has("hasPrev") || args.getBoolean("hasPrev");
        this.hasNext = !args.has("hasNext") || args.getBoolean("hasNext");
        this.hasClose = args.has("hasClose") && args.getBoolean("hasPrev");
        this.dismissible = args.has("dismissible") && args.getBoolean("dismissible");
        this.playIcon = args.has("playIcon") ? args.getString("playIcon") : "media_play";
        this.pauseIcon = args.has("pauseIcon") ? args.getString("pauseIcon") : "media_pause";
        this.prevIcon = args.has("prevIcon") ? args.getString("prevIcon") : "media_prev";
        this.nextIcon = args.has("nextIcon") ? args.getString("nextIcon") : "media_next";
        this.closeIcon = args.has("closeIcon") ? args.getString("closeIcon") : "media_close";
        this.notificationIcon = args.has("notificationIcon") ? args.getString("notificationIcon") : "notification";
    }

    public JSObject getValues() {
        JSObject obj = new JSObject();
        obj.put("artist", artist);
        obj.put("album", album);
        obj.put("track", track);
        obj.put("ticker", ticker);
        obj.put("cover", cover);
        obj.put("isPlaying", isPlaying);
        obj.put("hasPrev", hasPrev);
        obj.put("hasNext", hasNext);
        obj.put("hasClose", hasClose);
        obj.put("dismissible", dismissible);
        obj.put("playIcon", playIcon);
        obj.put("pauseIcon", pauseIcon);
        obj.put("prevIcon", prevIcon);
        obj.put("nextIcon", nextIcon);
        obj.put("closeIcon", closeIcon);
        obj.put("notificationIcon", notificationIcon);
        return obj;
    }

}
