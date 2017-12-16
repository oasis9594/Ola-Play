package com.example.dell.olaapp;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Oasis on 12/16/2017.
 */

public class SongModel extends RealmObject {

    String song;
    @PrimaryKey
    String url;
    String artists;
    String coverImage;
    boolean isDownloaded;
    boolean isPlaying;
    public SongModel()
    {
        isDownloaded=false;
        isPlaying=false;
    }
    public SongModel(Song s)
    {
        this.song=s.getSong();
        this.url=s.getUrl();
        this.artists=s.getArtists();
        this.coverImage=s.getCoverImage();
        isDownloaded=false;
        isPlaying=false;
    }
}
