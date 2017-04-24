package com.showcase.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.showcase.PhotoPreviewActivity;
import com.showcase.R;
import com.showcase.adapter.CameraFragmentAdapter;
import com.showcase.componentHelper.PhoneMediaControl;
import com.showcase.helper.SimpleDividerItemDecoration;

import java.util.ArrayList;

public class CameraFragment2 extends Fragment {

    private TextView emptyView;
    //    private GridView mView;
    private RecyclerView recyclerView;
    private Context mContext;
    private CameraFragmentAdapter mAdapter;

    public static ArrayList<PhoneMediaControl.PhotoEntry> photos = new ArrayList<PhoneMediaControl.PhotoEntry>();
    public static ArrayList<PhoneMediaControl.AlbumEntry> albumsSorted = null;

    private PhoneMediaControl.AlbumEntry selectedAlbum = null;

    private int firstSelectedPosition;
    private boolean isMultiSelectionMode = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /** Inflating the layout for this fragment **/
        mContext = this.getActivity();
        View v = inflater.inflate(R.layout.fragment_gallery2, null);
        initializeView(v);
        return v;
    }

    private void initializeView(View v) {
        //    mView=(GridView)v.findViewById(R.id.grid_view);
        recyclerView = (RecyclerView) v.findViewById(R.id.rvImages);
        emptyView = (TextView) v.findViewById(R.id.searchEmptyView);
        emptyView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        emptyView.setText("NoPhotos");

        albumsSorted = GalleryFragment.albumsSorted;
        if (albumsSorted.isEmpty()) {
            Toast.makeText(mContext, "No Image Found", Toast.LENGTH_SHORT).show();
        } else {
            photos = GalleryFragment.albumsSorted.get(0).photos;
        }
        initAdapter();
    }

    private void initAdapter() {
        mAdapter = new CameraFragmentAdapter(getActivity(), photos, new CameraFragmentAdapter.OnItemClicked() {
            @Override
            public void onClick(int position, View view) {
                if (isMultiSelectionMode) {
                    setImageSelection(view, position);
                } else {
                    Intent mIntent = new Intent(mContext, PhotoPreviewActivity.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putInt("Key_FolderID", 0);
                    mBundle.putInt("Key_ID", position);
                    mIntent.putExtras(mBundle);
                    startActivity(mIntent);
                }
            }

            @Override
            public void onLongClick(int position, View view) {
                if (!isMultiSelectionMode) {
                    firstSelectedPosition = position;
                    setImageSelection(view, position);
                    isMultiSelectionMode = true;
                }
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setItemViewCacheSize(photos != null ? photos.size() : 0);//keep it minimum 1 to avoid any conflict
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        recyclerView.setAdapter(mAdapter);
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void setImageSelection(View view, int position) {

        if (photos.get(position).getSelected()) {
            if (position != firstSelectedPosition) {
                photos.get(position).setSelected(false);
            } else {
                firstSelectedPosition = -1;
            }
        } else {
            photos.get(position).setSelected(true);
        }

        view.setBackgroundResource(photos.get(position).getSelected() ? R.drawable.img_selection_square : 0);
    }
}