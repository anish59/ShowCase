package com.showcase.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.showcase.R;
import com.showcase.componentHelper.PhoneMediaControl;

import java.util.ArrayList;

/**
 * Created by anish on 04-05-2017.
 */

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<PhoneMediaControl.AlbumEntry> albumsSorted = null;
    private OnItemClicked onItemClicked;

    public GalleryAdapter(Context context, ArrayList<PhoneMediaControl.AlbumEntry> albumsSorted, OnItemClicked onItemClicked) {
        this.context = context;
        this.albumsSorted = albumsSorted;
        this.onItemClicked = onItemClicked;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return albumsSorted != null ? albumsSorted.size() : 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView media_photo_image;
        private TextView album_count;
        private TextView album_name;

        public MyViewHolder(View itemView) {
            super(itemView);

            media_photo_image = (ImageView) itemView.findViewById(R.id.media_photo_image);
            album_count = (TextView) itemView.findViewById(R.id.album_count);
            album_name = (TextView) itemView.findViewById(R.id.album_name);

        }
    }

    public interface OnItemClicked {
        void onClick(int position, View view);

        void onLongClick(int position, View view);
    }
}
