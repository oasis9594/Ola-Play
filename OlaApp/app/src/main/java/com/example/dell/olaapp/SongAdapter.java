package com.example.dell.olaapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Oasis on 12/16/2017.
 */

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder>{
    List<Song>songs;
    Activity activity;
    public SongAdapter(List<Song>cursongs, Activity activity)
    {
        this.songs=cursongs;
        this.activity=activity;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView poster;
        RelativeLayout songLayout;
        TextView songName;
        TextView authors;
        public ViewHolder(View itemView) {
            super(itemView);
            songLayout=(RelativeLayout)itemView.findViewById(R.id.song_row_layout);
            poster=(ImageView)itemView.findViewById(R.id.poster_image);
            songName=(TextView)itemView.findViewById(R.id.list_song_name);
            authors=(TextView)itemView.findViewById(R.id.authors_text);
        }
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View customView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_row, parent, false);
        ViewHolder vh= new ViewHolder(customView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Song song=songs.get(position);
        holder.songName.setText(song.getSong());
        holder.authors.setText(song.getArtists());
        //Picasso.with(holder.poster.getContext()).load(song.getUrl()).error(R.drawable.noposter).into(holder.poster);
        Picasso.Builder builder = new Picasso.Builder(activity);
        builder.listener((picasso, uri, exception) -> holder.poster.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.noposter)));
        builder.downloader(new OkHttpDownloader(activity));
        builder.build().load(song.getCoverImage()).into(holder.poster);
        holder.songLayout.setOnClickListener(v -> activity.startActivityForResult(new Intent(activity, SongPlayer.class).putExtra("url", song.getUrl()), 400));
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

}
