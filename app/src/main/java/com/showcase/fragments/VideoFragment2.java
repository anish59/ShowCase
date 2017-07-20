package com.showcase.fragments;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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

import com.gun0912.tedpermission.PermissionListener;
import com.showcase.MainActivity;
import com.showcase.R;
import com.showcase.adapter.VideoAdapter;
import com.showcase.componentHelper.PhoneMediaVideoController;
import com.showcase.helper.FunctionHelper;
import com.showcase.helper.ProgressBarHelper;
import com.showcase.helper.ProgressListener;
import com.showcase.helper.SimpleDividerItemDecoration;
import com.showcase.helper.UIHelper;

import java.io.File;
import java.io.IOException;
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
    private ProgressListener progressListener;
    private View emptyView;

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

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
        emptyView = v.findViewById(R.id.emptyView);
        recyclerView = (RecyclerView) v.getRootView().getRootView().findViewById(R.id.rvImages);
//        fabCam = (FloatingActionButton) v.findViewById(R.id.fabCam);
//        initListeners();
        initAdapter();
        return v;
    }

    private void initListeners() {
     /*   fabCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FunctionHelper.setPermission(mContext, new String[]{Manifest.permission.CAMERA}, new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        UIHelper.fireIntent(mContext, intent, true);
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                        Toast.makeText(mContext, "Action Unavailable", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });*/
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

            if (arrVideoDetails != null || arrVideoDetails.size() > 0) {
                emptyView.setVisibility(View.VISIBLE);
            } else {
                emptyView.setVisibility(View.GONE);
            }
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
                UIHelper.dialogWithTwoOpt(getActivity(), "Are you sure want to delete it?", new UIHelper.DialogOptionsSelectedListener() {
                    @Override
                    public void onSelect(boolean isYes) {
                        if (isYes) {
                            deleteVideos();
                        }
                    }
                }, "yes", "no");
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

    private void deleteVideos() {

        try {
            progressListener = new ProgressBarHelper(mContext, "Please Wait..");
            progressListener.showProgressDialog();
            if (isMultiSelectionMode && !videos.isEmpty()) {
                Log.e("RemainingPics(a): ", "" + videos.size());
                for (int i = videos.size() - 1; i >= 0; i--) {
                    if (videos.get(i).isSelected) {
                        FunctionHelper.logE("selected: ", i + "> " + videos.get(i).path);
                        File fDelete = new File(videos.get(i).path);
                        Log.e("path:", " " + i + ": " + videos.get(i).path);
                        if (fDelete.exists()) {

                            deleteWithProjection(fDelete);

                            if (fDelete.exists()) {
                                fDelete.delete();
                            }
                            if (fDelete.exists()) {
                                try {
                                    fDelete.getCanonicalFile().delete();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (fDelete.exists()) {
                                    getActivity().deleteFile(fDelete.getName());
                                }

                            }
//                            photos.remove(i);
                            videos.remove(i);
                            mAdapter.notifyDataSetChanged();
                            FunctionHelper.callBroadCast(mContext, fDelete);
                            //photos2.remove(i);
                        } else {
                            Log.e("fDelete: ", " : " + fDelete.getAbsoluteFile());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        progressListener.hidProgressDialog();
        //        refreshAfterDelete();
        imageDeselectionAndNotify(itemDeselect, itemShare, itemDelete);
        isDeselectIconVisible = false;
    }

    private void deleteWithProjection(File fDelete) {
        // Set up the projection (we only need the ID)
        String[] projection = {MediaStore.Video.Media._ID};

        // Match on the file path
        String selection = MediaStore.Video.Media.DATA + " = ?";
        String[] selectionArgs = new String[]{fDelete.getAbsolutePath()};

        // Query for the ID of the media matching the file path
        Uri queryUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor c = contentResolver.query(queryUri, projection, selection, selectionArgs, null);
        if (c.moveToFirst()) {
            // We found the ID. Deleting the item via the content provider will also remove the file
            long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
            Uri deleteUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
            contentResolver.delete(deleteUri, null, null);
        } else {
            // File not found in media store DB
            FunctionHelper.logE("fnf: ", "File not found in media store DB");
        }
        c.close();
    }

    private void refreshData() {
        if (!videos.isEmpty()) {
            isMultiSelectionMode = false;
            for (PhoneMediaVideoController.VideoDetails video : videos) {
                video.setSelected(false);
            }
            mAdapter.setItems(videos, true);
            // loadData();
        }
    }

}
