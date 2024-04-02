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

public class RecycleViewAdapterArtist extends RecyclerView.Adapter<RecycleViewAdapterArtist.ViewHolder> {

    private ArrayList<String> mArtistNames = new ArrayList<>();
    private ArrayList<String> mArtistImages = new ArrayList<>();
    private Context mContext;

    public RecycleViewAdapterArtist(Context context, ArrayList<String> names, ArrayList<String> images) {
        mArtistNames = names;
        mArtistImages = images;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.artistname.setText(mArtistNames.get(position));
        Glide.with(mContext).asBitmap()
                .load(mArtistImages.get(position))
                .into(holder.artistimage);
    }

    @Override
    public int getItemCount() {
        return mArtistNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView artistimage;
        TextView artistname;

        public ViewHolder(View itemView) {
            super(itemView);
            artistimage = itemView.findViewById(R.id.imageViewArtist);
            artistname = itemView.findViewById(R.id.textViewArtist);
        }
    }


}
