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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.showcase.R;
import com.showcase.componentHelper.PhoneMediaControl;

import java.util.ArrayList;

/**
 * Created by ANISH on 22-04-2017.
 */

public class CameraFragmentAdapter extends RecyclerView.Adapter<CameraFragmentAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<PhoneMediaControl.PhotoEntry> photos = new ArrayList<PhoneMediaControl.PhotoEntry>();
    private DisplayImageOptions options;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private OnItemClicked onItemClicked;

    public CameraFragmentAdapter(Context context, ArrayList<PhoneMediaControl.PhotoEntry> photos, OnItemClicked onItemClicked) {
        this.context = context;
        this.photos = photos;
        this.onItemClicked = onItemClicked;
    }
  /* options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.nophotos)
                .showImageForEmptyUri(R.drawable.nophotos)
                .showImageOnFail(R.drawable.nophotos).cacheInMemory(true)
                .cacheOnDisc(true).considerExifParams(true).build();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));*/

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        PhoneMediaControl.PhotoEntry mPhotoEntry = photos.get(position);
        String path = mPhotoEntry.path;
        if (path != null && !path.equals("")) {
//            ImageLoader.getInstance().displayImage("file://" + path, holder.imgCamPic,options);
            Glide.with(context).load("file://" + path)
                    .centerCrop()
                    .placeholder(R.drawable.nophotos)
                    .crossFade()
                    .into(holder.imgCamPic);
        }

        holder.itemImgFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClicked.onClick(position, view);
            }
        });
        holder.itemImgFrame.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                onItemClicked.onLongClick(position, view);

                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return photos != null ? photos.size() : 0; // if not null then return size of the photo or if null return 0'
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView emptyView;
        private ImageView imgCamPic;
        private FrameLayout itemImgFrame;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemImgFrame =(FrameLayout)itemView.findViewById(R.id.itemImgFrame);
            imgCamPic = (ImageView) itemView.findViewById(R.id.album_image);
            emptyView = (TextView) itemView.findViewById(R.id.searchEmptyView);
        }
    }

    public interface OnItemClicked {
        void onClick(int position, View view);

        void onLongClick(int position, View view);
    }
}
