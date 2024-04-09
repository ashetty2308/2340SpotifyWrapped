package com.example.spotify_sdk;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class RecycleViewAdapterSong extends RecyclerView.Adapter<RecycleViewAdapterSong.ViewHolder> {

    private ArrayList<String> mSongNames = new ArrayList<>();
    private ArrayList<String> mSongImages = new ArrayList<>();
    private ArrayList<String> mSongArtist = new ArrayList<>();
    private Context mContext;
    private Bundle mBundle = new Bundle();
    MediaPlayer mediaPlayer = new MediaPlayer();


    public RecycleViewAdapterSong(Context context, ArrayList<String> names, ArrayList<String> images, ArrayList<String> artists, Bundle bundle) {
        mSongNames = names;
        mSongImages = images;
        mSongArtist = artists;
        mContext = context;
        mBundle = bundle;
    }


    public Bundle getBundle() {
        Log.d("BUNDLE IN GETTER", String.valueOf(mBundle));
        Log.d("THIS.BUNDLE IN GETTER", String.valueOf(this.mBundle));
        return mBundle;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item, parent, false);
        mediaPlayer.stop();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewAdapterSong.ViewHolder holder, int position) {
        holder.songName.setText(mSongNames.get(position));
        holder.songArtist.setText(mSongArtist.get(position));
        Glide.with(mContext).asBitmap()
                .load(mSongImages.get(position))
                .into(holder.songImage);


        mediaPlayer.stop();
        Bundle bundle = getBundle();

        holder.songImage.setOnClickListener(view -> {
            String[] snippets = bundle.getStringArray("SONG_SNIPPETS");
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            try {
                mediaPlayer.setDataSource(snippets[position]);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // below line is use to prepare
            // and start our media player.

        });
    }



    @Override
    public int getItemCount() {
        return mSongImages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView songImage;
        TextView songName;
        TextView songArtist;

        public ViewHolder(View itemView) {
            super(itemView);
            songImage = itemView.findViewById(R.id.imageViewSong);
            songName = itemView.findViewById(R.id.textViewSong);
            songArtist = itemView.findViewById(R.id.textViewSongArtist);
        }
    }


}
