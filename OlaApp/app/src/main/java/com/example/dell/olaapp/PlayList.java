package com.example.dell.olaapp;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Oasis on 12/19/2017.
 */

public class PlayList extends RealmObject {
    RealmList<SongModel> songs;
    @PrimaryKey
    String name;
    public PlayList(){
        songs=new RealmList<>();
    }
    public PlayList(SongModel song)
    {
        songs=new RealmList<>();
        songs.add(song);
        this.name="Favourites";
    }
    public PlayList(SongModel song, String name)
    {
        songs=new RealmList<>();
        songs.add(song);
        this.name=name;
    }
}
