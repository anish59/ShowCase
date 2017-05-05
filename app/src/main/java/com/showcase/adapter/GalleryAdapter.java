package com.showcase.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.showcase.R;
import com.showcase.componentHelper.PhoneMediaControl;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by anish on 04-05-2017.
 */

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<PhoneMediaControl.AlbumEntry> albumsSorted = null;
    private OnItemClicked onItemClicked;
    private boolean isRemoveFirstPostionBackground = false;

    public GalleryAdapter(Context context, ArrayList<PhoneMediaControl.AlbumEntry> albumsSorted, OnItemClicked onItemClicked) {
        this.context = context;
        this.albumsSorted = albumsSorted;
        this.onItemClicked = onItemClicked;
    }

    public void setItems(Context context, ArrayList<PhoneMediaControl.AlbumEntry> albumsSorted, boolean isRemoveFirstPostionBackground) {
        this.context = context;
        this.albumsSorted = albumsSorted;
        this.isRemoveFirstPostionBackground = isRemoveFirstPostionBackground;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.photo_picker_album_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        PhoneMediaControl.AlbumEntry albumEntry = albumsSorted.get(position);
        if (albumEntry.coverPhoto != null && albumEntry.coverPhoto.path != null) {
            Glide.with(context).load(new File(albumEntry.coverPhoto.path))
                    .centerCrop()
                    .placeholder(R.drawable.nophotos)
                    .crossFade()
                    .into(holder.media_photo_image);
        } else {
            holder.media_photo_image.setImageResource(R.drawable.nophotos);
        }
        holder.album_name.setText(albumEntry.bucketName);
        holder.album_count.setText("" + albumEntry.photos.size());

        if (isRemoveFirstPostionBackground) {
            holder.galleryItemFrame.setBackgroundResource(0);
        }

        holder.galleryItemFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClicked.onClick(position, v);
            }
        });
        holder.galleryItemFrame.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onItemClicked.onLongClick(position, v);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumsSorted != null ? albumsSorted.size() : 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView media_photo_image;
        private TextView album_count;
        private TextView album_name;
        private FrameLayout galleryItemFrame;

        public MyViewHolder(View itemView) {
            super(itemView);
            galleryItemFrame = (FrameLayout) itemView.findViewById(R.id.galleryItemFrame);
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
