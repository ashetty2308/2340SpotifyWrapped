package com.example.spotify_sdk;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;


public class FragmentOne extends Fragment {

    private ArrayList<String> mArtistNames = new ArrayList<>();
    private ArrayList<String> mArtistImages = new ArrayList<>();
    private ArrayList<String> mSongNames = new ArrayList<>();
    private ArrayList<String> mSongImages = new ArrayList<>();;
    private ArrayList<String> mSongArtists = new ArrayList<>();;

    LinearLayoutManager layoutManagerArtist, layoutManagerSong;
    RecyclerView recyclerViewArtist, recyclerViewSong;
    RecycleViewAdapterArtist adapterArtist;
    RecycleViewAdapterSong adapterSong;

    public FragmentOne() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_one, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d("args", String.valueOf(getArguments()));
        // populate the test array
        getTestImages();

        layoutManagerArtist = new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false);
        layoutManagerSong = new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false);


        // recycleview for Artists
        recyclerViewArtist = view.findViewById(R.id.recyclerviewArtists);
        recyclerViewArtist.setHasFixedSize(true);
        recyclerViewArtist.setLayoutManager(layoutManagerArtist);
        adapterArtist = new RecycleViewAdapterArtist(this.getContext(), mArtistNames, mArtistImages);
        recyclerViewArtist.setAdapter(adapterArtist);

        // recycleview for Songs
        recyclerViewSong = view.findViewById(R.id.recyclerviewSongs);
        recyclerViewSong.setHasFixedSize(true);
        recyclerViewSong.setLayoutManager(layoutManagerSong);
        adapterSong = new RecycleViewAdapterSong(this.getContext(), mSongNames, mSongImages, mSongArtists);
        recyclerViewSong.setAdapter(adapterSong);

//        adapterArtist.notifyDataSetChanged();
//        adapterSong.notifyDataSetChanged();
    }

    public void getTestImages() {
        Bundle argsPassedIn = getArguments();
        String[] artistNames = argsPassedIn.getStringArray("ARTIST_NAMES");
        String[] artistImages = argsPassedIn.getStringArray("ARTIST_IMAGES");
        String[] songImages = argsPassedIn.getStringArray("SONG_IMAGES");
        String[] songArtists = argsPassedIn.getStringArray("SONG_WHO");
        String[] songNames = argsPassedIn.getStringArray("SONG");
        for (int i = 0; i < 5; i++) {
            mArtistImages.add(artistImages[i]);
            mArtistNames.add(artistNames[i]);
            mSongImages.add(songImages[i]);
            mSongNames.add(songNames[i]);
        }
    }


}