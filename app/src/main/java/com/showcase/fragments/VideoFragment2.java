package com.showcase.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.showcase.MainActivity;
import com.showcase.R;
import com.showcase.adapter.VideoAdapter;
import com.showcase.componentHelper.PhoneMediaVideoController;
import com.showcase.helper.SimpleDividerItemDecoration;

import java.util.ArrayList;

public class VideoFragment2 extends Fragment implements PhoneMediaVideoController.loadAllVideoMediaInterface {

    private RecyclerView recyclerView;
    private Context mContext;
    private VideoAdapter mAdapter;
    private ArrayList<PhoneMediaVideoController.VideoDetails> videos = null;
    private MenuItem itemDeselect, itemShare, itemDelete;
    private boolean isDeselectIconVisible = false;
    private boolean isMultiSelectionMode = false;
    private int firstSelectedPosition;
//    private Toolbar toolbar;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /** Inflating the layout for this fragment **/
        mContext = this.getActivity();
        View v = inflater.inflate(R.layout.fragment_gallery2, null);
        recyclerView = (RecyclerView) v.getRootView().getRootView().findViewById(R.id.rvImages);
//        toolbar = (Toolbar) v.getRootView().findViewById(R.id.tool_bar);
        loadData();
        initAdapter();
        return v;
    }

    private void initAdapter() {
        mAdapter = new VideoAdapter(getActivity(), videos, new VideoAdapter.OnItemClickedListener() {
            @Override
            public void onClick(String videoPath, View v, int position) {
                if (isMultiSelectionMode) {
                    setVideoSelection(v, position);
                } else {
                    try {
                        if (videoPath == null) {
                            return;
                        }
                        Intent toStart = new Intent(Intent.ACTION_VIEW);
                        toStart.setDataAndType(Uri.parse(videoPath), "video/*");
                        startActivity(toStart);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onLongClick(String videoPath, View v, int position) {
                if (!isMultiSelectionMode) {
                    firstSelectedPosition = position;
                    setVideoSelection(v, position);
                    isMultiSelectionMode = true;
                }
                isDeselectIconVisible = true;
                getActivity().invalidateOptionsMenu();
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        recyclerView.setAdapter(mAdapter);
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void loadData() {
        PhoneMediaVideoController mPhoneMediaVideoController = new PhoneMediaVideoController();
        mPhoneMediaVideoController.setLoadallvideomediainterface(this);
        mPhoneMediaVideoController.loadAllVideoMedia(mContext);
    }

    @Override
    public void loadVideo(ArrayList<PhoneMediaVideoController.VideoDetails> arrVideoDetails) {
        videos = arrVideoDetails;
        if (mAdapter != null) {
            mAdapter.setItems(arrVideoDetails, true);
            recyclerView.setItemViewCacheSize(videos != null ? videos.size() : 0);//keep it minimum 1 to avoid any conflict
//            new MainActivity().setToolBar(toolbar, "Camera", "(" + videos.size() + ")");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        itemDeselect = menu.findItem(R.id.action_unSelect);
        itemShare = menu.findItem(R.id.action_shareImages);
        itemDelete = menu.findItem(R.id.action_deleteImages);
        if (isDeselectIconVisible) {
            itemDeselect.setVisible(true);
            itemShare.setVisible(true);
            itemDelete.setVisible(true);
        } else {
            itemDeselect.setVisible(false);
            itemShare.setVisible(false);
            itemDelete.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_unSelect:
                imageDeselectionAndNotify(itemDeselect, itemShare, itemDelete);
                break;
            case R.id.action_shareImages:
                shareVideos();
                break;
            case R.id.action_deleteImages:
                //deleteImages();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setVideoSelection(View view, int position) {

        if (videos.get(position).getSelected()) {
            if (position != firstSelectedPosition) {
                videos.get(position).setSelected(false);
            } else {
                firstSelectedPosition = -1;
            }
        } else {
            videos.get(position).setSelected(true);
        }

        view.setBackgroundResource(videos.get(position).getSelected() ? R.drawable.img_selection_square : 0);
    }


    private void shareVideos() {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Sharing videos");
        intent.setType("video/*"); /* This is sharing video images. */

        ArrayList<Uri> files = new ArrayList<>();
        if (isMultiSelectionMode && !videos.isEmpty()) {
            for (PhoneMediaVideoController.VideoDetails video : videos) {
                if (video.getSelected()) {
                    files.add(Uri.parse(video.path));
                }
            }
        }

        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
        startActivity(intent);
        imageDeselectionAndNotify(itemDeselect, itemShare, itemDelete);
    }


    private void imageDeselectionAndNotify(MenuItem itemDeselect, MenuItem itemShare, MenuItem itemDelete) {
        refreshData();
        itemDeselect.setVisible(false);
        itemShare.setVisible(false);
        itemDelete.setVisible(false);
    }

    private void refreshData() {
        if (!videos.isEmpty()) {
            isMultiSelectionMode = false;
            videos = new ArrayList<>();

            for (PhoneMediaVideoController.VideoDetails video : videos) {
                video.setSelected(false);
            }
            loadData();
        }
    }


}
