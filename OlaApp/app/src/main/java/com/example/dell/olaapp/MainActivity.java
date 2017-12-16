package com.example.dell.olaapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements Callback<List<Song >> {

    public static final String BASE_URL = "http://starlord.hackerearth.com/";
    public static final String TAG = "dell.olaapp.TAG";
    SongNetworkInterface songInterface;
    List<Song> songs = new ArrayList<>();
    int items_per_page=4;
    int page_no=-1;
    RecyclerView songList;
    SongAdapter songAdapter;
    List<Song> cursongs;
    LinearLayout prevPage, nextPage, curPage, firstPage, lastPage;
    TextView curPageNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        songInterface=retrofit.create(SongNetworkInterface.class);
        Call<List<Song>> songCall = songInterface.getSongs();
        songCall.enqueue(this);
        prevPage=(LinearLayout)findViewById(R.id.prev_page);
        nextPage=(LinearLayout)findViewById(R.id.next_page);
        curPage=(LinearLayout)findViewById(R.id.cur_page);
        firstPage=(LinearLayout)findViewById(R.id.first_page);
        lastPage=(LinearLayout)findViewById(R.id.last_page);
        curPageNum=(TextView)findViewById(R.id.cur_page_text);
        songList=(RecyclerView)findViewById(R.id.song_list);
        cursongs = new ArrayList<>();
        songAdapter=new SongAdapter(cursongs, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        songList.setLayoutManager(layoutManager);
        songList.setAdapter(songAdapter);
        prevPage.setOnClickListener(v -> refreshSongs(page_no-1));
        nextPage.setOnClickListener(v -> refreshSongs(page_no+1));
        firstPage.setOnClickListener(v -> refreshSongs(0));
        lastPage.setOnClickListener(v -> refreshSongs(songs.size()));
    }

    public void refreshSongs(int page)
    {
        int maxPages = (int) Math.ceil((1.0*songs.size())/items_per_page);
        if(maxPages>0)maxPages-=1;
        if(page>maxPages)page=maxPages;
        if(page<0)page=0;
        if(page_no==page)return;
        page_no=page;
        Log.d(TAG, String.valueOf(page));
        curPageNum.setText(String.valueOf(page+1));
        cursongs.clear();
        int base=page*items_per_page;
        int maxsize = base+items_per_page;
        if(maxsize>songs.size())maxsize=songs.size();
        for(int i=base;i<maxsize;i++)
            cursongs.add(i-base, songs.get(i));
        songAdapter.notifyDataSetChanged();
    }
    @Override
    public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
        if(response.isSuccessful())
        {
            songs = response.body();
            /*for(Song s:songs)
            {
                String u=s.getUrl();
                try {
                    URL url = new URL(u);
                    HttpURLConnection ucon = (HttpURLConnection) url.openConnection();
                    ucon.setInstanceFollowRedirects(false);
                    URL secondURL = new URL(ucon.getHeaderField("Location"));
                    s.setUrl(secondURL.toString());
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }*/

                try (Realm realm = Realm.getDefaultInstance()) {
                // No need to close the Realm instance manually
                realm.executeTransaction(realm1 -> {
                    for(Song s:songs)
                    {
                        Log.d(TAG, s.getSong());
                        SongModel songModel = new SongModel(s);
                        realm1.copyToRealmOrUpdate(songModel);
                    }
                });
            }
            refreshSongs(0);
        }
    }

    @Override
    public void onFailure(Call<List<Song>> call, Throwable t) {
        t.printStackTrace();
    }
}