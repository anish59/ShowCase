package com.showcase.fragments;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.showcase.MainActivity;
import com.showcase.PhotoPreviewActivity;
import com.showcase.R;
import com.showcase.adapter.AlbumAdapter;
import com.showcase.componentHelper.PhoneMediaControl;
import com.showcase.helper.FileHelper;
import com.showcase.helper.FunctionHelper;
import com.showcase.helper.ProgressBarHelper;
import com.showcase.helper.ProgressListener;
import com.showcase.helper.SimpleDividerItemDecoration;
import com.showcase.helper.UIHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by anish on 30-06-2017.
 */

public class FragmentPinnedPics extends Fragment {
    private Context context;
    private RecyclerView rvImages;
    private TextView searchEmptyView;
    private MenuItem itemDeselect, itemShare, itemDelete;
    public static ArrayList<PhoneMediaControl.AlbumEntry> albumsSorted = null;
    public static ArrayList<PhoneMediaControl.PhotoEntry> photos = new ArrayList<PhoneMediaControl.PhotoEntry>();
    private int firstSelectedPosition;
    private boolean isMultiSelectionMode = false;
    private boolean isDeselectIconVisible = false;
    private AlbumAdapter mAdapter;
    private ProgressListener progressListener;
    private View emptyView;

    @Override

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = this.getActivity();
        View view = inflater.inflate(R.layout.fragment_pinned_pics, null);
        initViews(view);
        progressListener = new ProgressBarHelper(context, "Please wait...");
        loadAlbum(FileHelper.PINNED_FOLDER, false);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        itemDeselect = menu.findItem(R.id.action_unSelect);
        itemShare = menu.findItem(R.id.action_shareImages);
        itemDelete = menu.findItem(R.id.action_deleteImages);
        if (isDeselectIconVisible) {
            itemDeselect.setVisible(true);
            itemShare.setVisible(false);
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
            case R.id.action_deleteImages:
                UIHelper.dialogWithTwoOpt(context, "Are you sure you want to delete it.", new UIHelper.DialogOptionsSelectedListener() {
                    @Override
                    public void onSelect(boolean isYes) {
                        if (isYes) {
                            deleteImages();
                        }
                    }
                }, "yes", "no");
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void initViews(View view) {
        searchEmptyView = (TextView) view.findViewById(R.id.searchEmptyView);
        rvImages = (RecyclerView) view.findViewById(R.id.rvImages);
        emptyView = view.findViewById(R.id.emptyView);
    }


    private void imageDeselectionAndNotify(MenuItem itemDeselect, MenuItem itemShare, MenuItem itemDelete) {
        refreshData();
        itemDeselect.setVisible(false);
        itemShare.setVisible(false);
        itemDelete.setVisible(false);
    }

    private void refreshData() {
        if (!albumsSorted.isEmpty()) {
            isMultiSelectionMode = false;
            for (PhoneMediaControl.AlbumEntry albumEntry : albumsSorted) {
                albumEntry.setSelected(false);
            }
            loadAlbum(FileHelper.PINNED_FOLDER, true);
        }
    }

    private void loadAlbum(String nameAlbum, final boolean isFromRestart) {
        PhoneMediaControl mediaControl = new PhoneMediaControl();
        mediaControl.setLoadalbumphoto(new PhoneMediaControl.loadAlbumPhoto() {
            @Override
            public void loadPhoto(ArrayList<PhoneMediaControl.AlbumEntry> albumsSorted1) {
                albumsSorted = albumsSorted1; //todo: check validation if its not empty
                if (albumsSorted != null && albumsSorted.size() > 0) {
                    emptyView.setVisibility(View.GONE);
                    photos = new ArrayList<PhoneMediaControl.PhotoEntry>();
                    photos = albumsSorted.get(0).photos;
                    initializeActionBar();
                    if (!isFromRestart) {
                        initAdapter();
                    } else {
                        mAdapter.setItems(photos, context, true);
                    }
                } else {
                    emptyView.setVisibility(View.VISIBLE);
                }
            }
        });
        mediaControl.loadPhotosByBucketName(context, nameAlbum);
    }

    private void initAdapter() {
        mAdapter = new AlbumAdapter(context, photos, new AlbumAdapter.OnItemClicked() {
            @Override
            public void onClick(int position, View view) {
                if (isMultiSelectionMode) {
                    setImageSelection(view, position);
                } else {
                    Intent mIntent = new Intent(context, PhotoPreviewActivity.class);
                    Bundle mBundle = new Bundle();
//                    mBundle.putInt("Key_FolderID", albumsSorted.get(0).bucketId);
                    mBundle.putString("Key_FolderName", albumsSorted.get(0).bucketName);
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
                isDeselectIconVisible = true;
                getActivity().invalidateOptionsMenu();
            }
        });
        rvImages.setHasFixedSize(true);
        rvImages.setLayoutManager(new GridLayoutManager(context, 3));
//        rvImages.setLayoutManager(new StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL));

        rvImages.setItemViewCacheSize(photos != null ? photos.size() : 0);//keep it minimum 1 to avoid any conflict
        rvImages.addItemDecoration(new SimpleDividerItemDecoration(context));
        rvImages.setAdapter(mAdapter);
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }


    private void setImageSelection(View view, int position) {

        if (photos.get(position).isSelected()) {
            if (position != firstSelectedPosition) {
                photos.get(position).setSelected(false);
            } else {
                firstSelectedPosition = -1;
            }
        } else {
            photos.get(position).setSelected(true);
        }
        view.setBackgroundResource(photos.get(position).isSelected() ? R.drawable.img_selection_square : 0);
    }

    private void initializeActionBar() {
        UIHelper.initToolbar((MainActivity) getActivity(), new MainActivity().toolbar, "Pinned Images" + " (" + photos.size() + ")");
    }

    private void deleteImages() {

        try {
            progressListener = new ProgressBarHelper(context, "Please Wait..");
            progressListener.showProgressDialog();
            if (isMultiSelectionMode && !photos.isEmpty()) {
                Log.e("RemainingPics(a): ", "" + photos.size());
                for (int i = photos.size() - 1; i >= 0; i--) {
                    if (photos.get(i).isSelected) {
                        FunctionHelper.logE("selected: ", i + "> " + photos.get(i).path);
                        File fDelete = new File(photos.get(i).path);
                        Log.e("path:", " " + i + ": " + photos.get(i).path);
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
                                    context.deleteFile(fDelete.getName());
                                }
                            }
                            photos.remove(i);
                            mAdapter.notifyDataSetChanged();
                            FunctionHelper.callBroadCast(context, fDelete);
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
        initializeActionBar();
    }

    private void deleteWithProjection(File fDelete) {
        // Set up the projection (we only need the ID)
        String[] projection = {MediaStore.Images.Media._ID};

        // Match on the file path
        String selection = MediaStore.Images.Media.DATA + " = ?";
        String[] selectionArgs = new String[]{fDelete.getAbsolutePath()};

        // Query for the ID of the media matching the file path
        Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = context.getContentResolver();
        Cursor c = contentResolver.query(queryUri, projection, selection, selectionArgs, null);
        if (c.moveToFirst()) {
            // We found the ID. Deleting the item via the content provider will also remove the file
            long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
            Uri deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            contentResolver.delete(deleteUri, null, null);
        } else {
            // File not found in media store DB
            FunctionHelper.logE("fnf: ", "File not found in media store DB");
        }
        c.close();
    }
}
