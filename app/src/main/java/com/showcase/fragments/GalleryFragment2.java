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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.showcase.AlbumActivity2;
import com.showcase.R;
import com.showcase.adapter.GalleryAdapter;
import com.showcase.componentHelper.PhoneMediaControl;
import com.showcase.helper.FunctionHelper;
import com.showcase.helper.ProgressBarHelper;
import com.showcase.helper.ProgressListener;
import com.showcase.helper.SimpleDividerItemDecoration;
import com.showcase.helper.UIHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GalleryFragment2 extends Fragment {
    private MenuItem itemDeselect, itemShare, itemDelete;
    private TextView searchEmptyView;
    private FloatingActionButton fabCam;
//    private View epmtyView;
    private GridView mView;
    private Context mContext;
    private RecyclerView recyclerView;

    private int firstSelectedPosition;
    private boolean isMultiSelectionMode = false;
    private boolean isDeselectIconVisible = false;

    private ProgressListener progressListener;


    public static ArrayList<PhoneMediaControl.AlbumEntry> albumsSorted = null;
    private GalleryAdapter galleryAdapter;

    /*   public GalleryFragment() {
           loadAllAlbum();
       }
   */
    @Override
    public void onResume() {
        super.onResume();
        loadAllAlbum();
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
        initializeView(v);
//        initListeners();
        return v;
    }

 /*   private void initListeners() {
        fabCam.setOnClickListener(new View.OnClickListener() {
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
        });
    }*/

    private void initAdapter() {
        galleryAdapter = new GalleryAdapter(getActivity(), albumsSorted, new GalleryAdapter.OnItemClicked() {
            @Override
            public void onClick(int position, View view) {
                if (isMultiSelectionMode) {
                    setImageSelection(view, position);
                } else {
                    Intent mIntent = new Intent(mContext, AlbumActivity2.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putString("Key_ID", position + "");
                    mBundle.putString("Key_Name", albumsSorted.get(position).bucketName + "");
                    mIntent.putExtras(mBundle);
                    mContext.startActivity(mIntent);
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
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setItemViewCacheSize(albumsSorted != null ? albumsSorted.size() : 0);//keep it minimum 1 to avoid any conflict
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        recyclerView.setAdapter(galleryAdapter);
        if (galleryAdapter != null) {
            galleryAdapter.notifyDataSetChanged();
        }
    }

    private void setImageSelection(View view, int position) {
        if (albumsSorted.get(position).isSelected()) {
            if (position != firstSelectedPosition) {
                albumsSorted.get(position).setSelected(false);
            } else {
                firstSelectedPosition = -1;
            }
        } else {
            albumsSorted.get(position).setSelected(true);
        }

        view.setBackgroundResource(albumsSorted.get(position).isSelected() ? R.drawable.img_selection_square : 0);

    }

    private void initializeView(View v) {
//        epmtyView = v.findViewById(R.id.emptyView);
        mView = (GridView) v.findViewById(R.id.grid_view);
        searchEmptyView = (TextView) v.findViewById(R.id.searchEmptyView);
        recyclerView = (RecyclerView) v.findViewById(R.id.rvImages);
        searchEmptyView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        searchEmptyView.setText("NoPhotos");
//        fabCam = (FloatingActionButton) v.findViewById(R.id.fabCam);
        initAdapter();
        // loadAllAlbum();
    }


    private void loadAllAlbum() {
        PhoneMediaControl mediaControl = new PhoneMediaControl();
        mediaControl.setLoadalbumphoto(new PhoneMediaControl.loadAlbumPhoto() {

            @Override
            public void loadPhoto(ArrayList<PhoneMediaControl.AlbumEntry> albumsSorted_) {
                albumsSorted = new ArrayList<PhoneMediaControl.AlbumEntry>();
                albumsSorted = albumsSorted_;
                if (mView != null && mView.getEmptyView() == null) {
                    mView.setEmptyView(null);
                }

                if (albumsSorted.isEmpty() || albumsSorted_ == null) {
//                    epmtyView.setVisibility(View.VISIBLE);
                } else {
//                    epmtyView.setVisibility(View.GONE);
                }
                if (galleryAdapter != null) {
                    galleryAdapter.setItems(getActivity(), albumsSorted, true);
                }
            }
        });
        mediaControl.loadGalleryPhotosAlbums(mContext, 0);
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
                UIHelper.dialogWithTwoOpt(mContext, "Are you sure you want to delete it.", new UIHelper.DialogOptionsSelectedListener() {
                    @Override
                    public void onSelect(boolean isYes) {
                        if (isYes) {
                            deleteFolderImages();
                        }
                    }
                }, "yes", "no");
                break;
        }
        return super.onOptionsItemSelected(item);
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
            loadAllAlbum();
        }
    }

    private void deleteFolderImages() {

        try {
            progressListener = new ProgressBarHelper(mContext, "Please Wait..");
            progressListener.showProgressDialog();
            if (isMultiSelectionMode && !albumsSorted.isEmpty()) {
                Log.e("RemainingPics(a): ", "" + albumsSorted.size());
                for (int i = albumsSorted.size() - 1; i >= 0; i--) {
                    if (albumsSorted.get(i).isSelected) {

                        ArrayList<PhoneMediaControl.PhotoEntry> photos = albumsSorted.get(i).photos;
                        for (PhoneMediaControl.PhotoEntry photo : photos) {
                            File fDelete = new File(photo.path);
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
                            }
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
        String[] projection = {MediaStore.Images.Media._ID};

        // Match on the file path
        String selection = MediaStore.Images.Media.DATA + " = ?";
        String[] selectionArgs = new String[]{fDelete.getAbsolutePath()};

        // Query for the ID of the media matching the file path
        Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = mContext.getContentResolver();
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