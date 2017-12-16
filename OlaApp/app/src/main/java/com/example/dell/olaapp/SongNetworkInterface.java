package com.example.dell.olaapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Oasis on 12/16/2017.
 */

public interface SongNetworkInterface {
    @GET("studio")
    Call<List<Song>> getSongs();
}
