package com.example.spotify_sdk;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    TextView llmTextView;
    Button aiButton;

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

        // populate the test array
        getTestImages();

        layoutManagerArtist = new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false);
        layoutManagerSong = new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false);
        llmTextView = view.findViewById(R.id.llmTextView);
        aiButton = view.findViewById(R.id.aiButton);

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
        adapterSong = new RecycleViewAdapterSong(this.getContext(), mSongNames, mSongImages, mSongArtists, getArguments());
        recyclerViewSong.setAdapter(adapterSong);

//        adapterArtist.notifyDataSetChanged();
//        adapterSong.notifyDataSetChanged();

        aiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("gemini_description", getArguments().getString("GEMINI_DESCRIPTION"));
                llmTextView.setMovementMethod(new ScrollingMovementMethod());
                llmTextView.setText(getArguments().getString("GEMINI_DESCRIPTION"));
            }
        });
    }

    public void getTestImages() {
        Bundle argsPassedIn = getArguments();
        Log.d("WOAH", String.valueOf(argsPassedIn));
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
            mSongArtists.add(songArtists[i]);
        }
    }



}