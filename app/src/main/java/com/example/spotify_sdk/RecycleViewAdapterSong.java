package com.example.spotify_sdk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class RecycleViewAdapterSong extends RecyclerView.Adapter<RecycleViewAdapterSong.ViewHolder> {

    private ArrayList<String> mSongNames = new ArrayList<>();
    private ArrayList<String> mSongImages = new ArrayList<>();
    private Context mContext;

    public RecycleViewAdapterSong(Context context, ArrayList<String> names, ArrayList<String> images) {
        mSongNames = names;
        mSongImages = images;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewAdapterSong.ViewHolder holder, int position) {
        holder.songName.setText(mSongNames.get(position));
        Glide.with(mContext).asBitmap()
                .load(mSongImages.get(position))
                .into(holder.songImage);
    }

    @Override
    public int getItemCount() {
        return mSongImages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView songImage;
        TextView songName;

        public ViewHolder(View itemView) {
            super(itemView);
            songImage = itemView.findViewById(R.id.imageViewSong);
            songName = itemView.findViewById(R.id.textViewSong);
        }
    }


}