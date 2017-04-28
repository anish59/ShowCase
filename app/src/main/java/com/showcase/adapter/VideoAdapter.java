package com.showcase.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.showcase.R;
import com.showcase.componentHelper.PhoneMediaVideoController;
import com.showcase.componentHelper.VideoThumbleLoader;

import java.util.ArrayList;

/**
 * Created by anish on 27-04-2017.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<PhoneMediaVideoController.VideoDetails> arrayVideoDetails;
    private OnItemClickedListener onItemClickedListener;
    private VideoThumbleLoader thumbleLoader;
    private boolean isRemoveFirstPostionBackground;

    public VideoAdapter(Context context, ArrayList<PhoneMediaVideoController.VideoDetails> arrayVideoDetails, OnItemClickedListener onItemClickedListener) {
        this.context = context;
        this.arrayVideoDetails = arrayVideoDetails;
        this.onItemClickedListener = onItemClickedListener;
        this.thumbleLoader = new VideoThumbleLoader(context);
    }

    public void setItems(ArrayList<PhoneMediaVideoController.VideoDetails> arrayVideoDetails, boolean isRemoveFirstPostionBackground) {
        this.arrayVideoDetails = arrayVideoDetails;
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
        PhoneMediaVideoController.VideoDetails mVideoDetails = arrayVideoDetails.get(position);
        holder.txtTitle.setText(mVideoDetails.displayname);
        thumbleLoader.DisplayImage("" + mVideoDetails.imageId, context, holder.img, null);
        holder.txtTitle.setText(mVideoDetails.displayname);
        final String videoPath = mVideoDetails.path;

        if (isRemoveFirstPostionBackground) {
            holder.img.getRootView().setBackgroundResource(0);
        }

        holder.img.getRootView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickedListener.onClick(videoPath, v, position);
            }
        });
        holder.img.getRootView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onItemClickedListener.onLongClick(videoPath, v, position);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayVideoDetails != null ? arrayVideoDetails.size() : 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView txtTitle;
        TextView txtCount;

        public MyViewHolder(View itemView) {
            super(itemView);

            img = (ImageView) itemView.findViewById(R.id.media_photo_image);
            txtTitle = (TextView) itemView.findViewById(R.id.album_name);
            txtCount = (TextView) itemView.findViewById(R.id.album_count);
        }
    }


    public interface OnItemClickedListener {
        void onClick(String videoPath, View v, int position);

        void onLongClick(String videoPath, View v, int position);
    }


}
