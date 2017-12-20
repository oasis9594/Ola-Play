package com.example.dell.olaapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class FavActivity extends AppCompatActivity {
    List<Song> songs = new ArrayList<>();
    int items_per_page=4;
    int page_no=-1;
    boolean favChange;
    private final static String TAG = "olaapp.FavActivity";
    RecyclerView songList;
    SongAdapter songAdapter;
    List<Song> cursongs;
    LinearLayout prevPage, nextPage, curPage, firstPage, lastPage;
    TextView curPageNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);
        TextView titleText = (TextView) findViewById(R.id.title_text);
        titleText.setText("My Favourites");
        prevPage=(LinearLayout)findViewById(R.id.prev_page);
        nextPage=(LinearLayout)findViewById(R.id.next_page);
        curPage=(LinearLayout)findViewById(R.id.cur_page);
        firstPage=(LinearLayout)findViewById(R.id.first_page);
        lastPage=(LinearLayout)findViewById(R.id.last_page);
        curPageNum=(TextView)findViewById(R.id.cur_page_text);
        songList=(RecyclerView)findViewById(R.id.song_list);
        cursongs = new ArrayList<>();
        try(Realm realm = Realm.getDefaultInstance())
        {
            PlayList playList = realm.where(PlayList.class).equalTo("name", "Favourites").findFirst();
            if(playList!=null)
            for(SongModel s:playList.songs){
                Song song = new Song();
                song.setUrl(s.url);
                song.setArtists(s.artists);
                song.setCoverImage(s.coverImage);
                song.setSong(s.song);
                songs.add(song);
            }
        }
        songAdapter=new SongAdapter(cursongs, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        songList.setLayoutManager(layoutManager);
        songList.setAdapter(songAdapter);
        prevPage.setOnClickListener(v -> refreshSongs(page_no-1));
        nextPage.setOnClickListener(v -> refreshSongs(page_no+1));
        firstPage.setOnClickListener(v -> refreshSongs(0));
        lastPage.setOnClickListener(v -> refreshSongs(songs.size()));
        favChange=false;
        refreshSongs(0);
    }
    public void refreshSongs(int page)
    {
        int maxPages = (int) Math.ceil((1.0*songs.size())/items_per_page);
        if(maxPages>0)maxPages-=1;
        if(page>maxPages)page=maxPages;
        if(page<0)page=0;
        if(page_no==page&&!favChange)return;
        page_no=page;
        favChange=false;
        curPageNum.setText(String.valueOf(page+1));
        cursongs.clear();
        int base=page*items_per_page;
        int maxsize = base+items_per_page;
        if(maxsize>songs.size())maxsize=songs.size();
        for(int i=base;i<maxsize;i++)
            cursongs.add(songs.get(i));
        Log.d(TAG, "onActivityResult "+ songs.size());
        songAdapter.notifyDataSetChanged();
    }
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, requestCode+ " " + resultCode);
        if(requestCode==400&&resultCode==RESULT_OK)
        {
            boolean isFav = data.getBooleanExtra("fav", false);
            Log.d(TAG,  "fav " + isFav);
            if(isFav) return;
            if(!isOnline())return;
            try(Realm realm = Realm.getDefaultInstance())
            {
                songs.clear();
                PlayList playList = realm.where(PlayList.class).equalTo("name", "Favourites").findFirst();
                if(playList!=null) {
                    for (SongModel s : playList.songs) {
                        Song song = new Song();
                        song.setUrl(s.url);
                        song.setArtists(s.artists);
                        song.setCoverImage(s.coverImage);
                        song.setSong(s.song);
                        songs.add(song);
                    }
                    Log.d(TAG, "onActivityResult "+ songs.size());
                }
                else Log.d(TAG,  "playlist null");
            }
            favChange=true;
            refreshSongs(page_no);
        }
    }
}
