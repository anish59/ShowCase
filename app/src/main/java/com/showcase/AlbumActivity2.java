package com.showcase;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.gun0912.tedpermission.PermissionListener;
import com.showcase.adapter.AlbumAdapter;
import com.showcase.componentHelper.PhoneMediaControl;
import com.showcase.helper.FileHelper;
import com.showcase.helper.FunctionHelper;
import com.showcase.helper.ProgressBarHelper;
import com.showcase.helper.ProgressListener;
import com.showcase.helper.SimpleDividerItemDecoration;
import com.showcase.helper.SingleMediaScanner;
import com.showcase.helper.UIHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AlbumActivity2 extends AppCompatActivity {

    private Context mContext = AlbumActivity2.this;
    private AlbumAdapter mAdapter;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ProgressListener progressListener;

    public static ArrayList<PhoneMediaControl.AlbumEntry> albumsSorted = null;
    public static ArrayList<PhoneMediaControl.PhotoEntry> photos = new ArrayList<PhoneMediaControl.PhotoEntry>();

    private int AlbummID = 0;
    private String nameAlbum;

    private boolean isMultiSelectionMode = false;
    private int firstSelectedPosition;
    private boolean isDeselectIconVisible = false;
    private MenuItem itemDeselect, itemShare, itemDelete, itemPinImage;
    private boolean isPinnedAlbum = false;
    private View emptyView;
    private long mLastClickTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album2);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        recyclerView = (RecyclerView) findViewById(R.id.rvImages);
        emptyView = findViewById(R.id.emptyView);
//        fabCam = (FloatingActionButton) findViewById(R.id.fabCam);

        getIntentSetData();

    }


    private void initializeActionBar() {
        UIHelper.initToolbar(AlbumActivity2.this, toolbar, nameAlbum + " (" + photos.size() + ")");
    }

    private void getIntentSetData() {
        Bundle mBundle = getIntent().getExtras();
        nameAlbum = mBundle.getString("Key_Name");
        AlbummID = Integer.parseInt(mBundle.getString("Key_ID"));
        loadAlbum(nameAlbum, false);
    }

    private void loadAlbum(String nameAlbum, final boolean isFromRestart) {
        PhoneMediaControl mediaControl = new PhoneMediaControl();
        mediaControl.setLoadalbumphoto(new PhoneMediaControl.loadAlbumPhoto() {
            @Override
            public void loadPhoto(ArrayList<PhoneMediaControl.AlbumEntry> albumsSorted1) {

                albumsSorted = albumsSorted1;
                if (albumsSorted != null && albumsSorted.size() > 0) {
                    emptyView.setVisibility(View.GONE);
                    photos = new ArrayList<PhoneMediaControl.PhotoEntry>();
                    photos = albumsSorted.get(0).photos;
                    initializeActionBar();
                    if (!isFromRestart) {
                        initAdapter();
                    } else {
                        mAdapter.setItems(photos, mContext, true);
                    }
                } else {
                    emptyView.setVisibility(View.VISIBLE);
                }
            }
        });
        if (nameAlbum.equals(getString(R.string.allPics))) {
            mediaControl.loadAlbumsAllPics(mContext, getString(R.string.allPics));
        } else {
            if (nameAlbum.equals(FileHelper.PINNED_FOLDER)) {
                isPinnedAlbum = true;
            }
            mediaControl.loadPhotosByBucketName(mContext, nameAlbum);
        }
    }

    private void initAdapter() {
        mAdapter = new AlbumAdapter(mContext, photos, new AlbumAdapter.OnItemClicked() {
            @Override
            public void onClick(int position, View view) {
                if (isMultiSelectionMode) {
                    setImageSelection(view, position);
                } else {
                    Intent mIntent = new Intent(mContext, PhotoPreviewActivity.class);
                    Bundle mBundle = new Bundle();
                    /*mBundle.putInt("Key_FolderID", AlbummID);*/
                    mBundle.putString("Key_FolderName", albumsSorted.get(0).bucketName);
                    mBundle.putInt("Key_ID", position);
                    mIntent.putExtras(mBundle);
                    startActivity(mIntent);
                }
            }

            @Override
            public void onLongClick(int position, View view) {
                if (photos.get(position).path.contains(FileHelper.PINNED_FOLDER)) {
                    itemPinImage.setVisible(false);
                }
                if (!isMultiSelectionMode) {
                    firstSelectedPosition = position;
                    setImageSelection(view, position);
                    isMultiSelectionMode = true;
                }
                isDeselectIconVisible = true;
                invalidateOptionsMenu();
            }
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
//        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL));

        recyclerView.setItemViewCacheSize(photos != null ? photos.size() : 0);//keep it minimum 1 to avoid any conflict
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(mContext));
        recyclerView.setAdapter(mAdapter);
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
//        view.setBackgroundResource(photos.get(position).isSelected() ? R.drawable.img_selection_square : 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.album_menu, menu);
        itemDeselect = menu.findItem(R.id.action_unSelect);
        itemShare = menu.findItem(R.id.action_shareImages);
        itemDelete = menu.findItem(R.id.action_deleteImages);
        itemPinImage = menu.findItem(R.id.action_PinImage);
        if (isDeselectIconVisible) {
            itemDeselect.setVisible(true);
            itemShare.setVisible(true);
            itemDelete.setVisible(true);
            itemPinImage.setVisible(true);
        } else {
            itemDeselect.setVisible(false);
            itemShare.setVisible(false);
            itemDelete.setVisible(false);
            itemPinImage.setVisible(false);
        }

        if (isPinnedAlbum) {
            itemPinImage.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_unSelect:
                imageDeselectionAndNotify(itemDeselect, itemShare, itemDelete, itemPinImage);
                break;
            case R.id.action_shareImages:
                shareImages();
                break;
            case R.id.action_deleteImages:
                UIHelper.dialogWithTwoOpt(mContext, "Are you sure want to delete it?", new UIHelper.DialogOptionsSelectedListener() {
                    @Override
                    public void onSelect(boolean isYes) {
                        if (isYes) {
                            deleteImages();
                        }
                    }
                }, "yes", "no");
                break;
            case R.id.action_PinImage:
                try {
                    pinSelectedImages();
                } catch (Exception e) {
                    System.out.println("pinError:: " + e.getMessage());
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void imageDeselectionAndNotify(MenuItem itemDeselect, MenuItem itemShare, MenuItem itemDelete, MenuItem itemPinImage) {
        refreshData();
        itemDeselect.setVisible(false);
        itemShare.setVisible(false);
        itemDelete.setVisible(false);
        itemPinImage.setVisible(false);
    }


    private void refreshData() {
        if (photos != null && !photos.isEmpty()) {
            isMultiSelectionMode = false;
//            photos = new ArrayList<>();
           /* albumsSorted = GalleryFragment.albumsSorted;
            if (photos2 != null && photos2.isEmpty()) {
                Toast.makeText(mContext, "No Image Found", Toast.LENGTH_SHORT).show();
            } else {
                int size = photos2.size();
                Log.e("RefreshSize :", size + "");
                photos.addAll(photos2);
            }*/
            if (photos != null && !photos.isEmpty()) {
                mAdapter.setItems(photos, mContext, true);
            } else {
                Toast.makeText(mContext, "No image found.", Toast.LENGTH_SHORT).show();
            }
            for (PhoneMediaControl.PhotoEntry photo : photos) {
                photo.setSelected(false);
            }
           /* for (PhoneMediaControl.PhotoEntry photoEntry : photos2) {
                photoEntry.setSelected(false);
            }*/
        }
    }

    private void shareImages() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        /*intent.putExtra(Intent.EXTRA_SUBJECT, "Share Image");*/
        intent.setType("image/jpeg"); /* This is sharing jpeg images. */

        ArrayList<Uri> files = new ArrayList<>();
        if (isMultiSelectionMode && !photos.isEmpty()) {
            for (PhoneMediaControl.PhotoEntry photo : photos) {
                if (photo.isSelected()) {
                    files.add(Uri.parse("file:///" + photo.path));
                }
            }

            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
            intent = Intent.createChooser(intent, "Share with...");
            startActivity(intent);
            imageDeselectionAndNotify(itemDeselect, itemShare, itemDelete, itemPinImage);
        }
    }


    private void pinSelectedImages() {
        try {
            progressListener = new ProgressBarHelper(mContext, "Please Wait..");
            progressListener.showProgressDialog();
            if (isMultiSelectionMode && !photos.isEmpty()) {
                for (int i = photos.size() - 1; i >= 0; i--) {
                    if (photos.get(i).isSelected) {
                        FunctionHelper.logE("selected: ", i + "> " + photos.get(i).path);
                        pinImage(photos.get(i).path);
                    }
                }
            }
            progressListener.hidProgressDialog();
            Toast.makeText(mContext, "Images Pinned", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, "Parsing error", Toast.LENGTH_SHORT).show();
            progressListener.hidProgressDialog();
        }

        imageDeselectionAndNotify(itemDeselect, itemShare, itemDelete, itemPinImage);
    }

    private void pinImage(String path) {
        File imgFile = new File(path);
        FileHelper.copyFile(imgFile.getAbsolutePath(), imgFile.getName(), FileHelper.getPinnedPath());
        FunctionHelper.callBroadCast(mContext, new File(FileHelper.getPinnedPath())); //needed to include parent image folder to include it in media type
        new SingleMediaScanner(mContext, imgFile);
    }

    private void deleteImages() {

        try {
            progressListener = new ProgressBarHelper(mContext, "Please Wait..");
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
                                    getApplicationContext().deleteFile(fDelete.getName());
                                }
                            }
                            photos.remove(i);
                            mAdapter.notifyDataSetChanged();
                            FunctionHelper.callBroadCast(mContext, fDelete);
                        } else {
                            Log.e("fDelete: ", " : " + fDelete.getAbsoluteFile());
                        }
                    }
                }
                /*if (!removedPositions.isEmpty()) {
                    for (int removedPosition : removedPositions) {
                        photos.remove(removedPosition);
                        mAdapter.notifyItemRemoved(removedPosition);
                    }
                    Log.e("RemainingPics(b): ", "" + photos.size());
                }*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        progressListener.hidProgressDialog();
        //        refreshAfterDelete();
        imageDeselectionAndNotify(itemDeselect, itemShare, itemDelete, itemPinImage);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        //  new GalleryFragment();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadAlbum(nameAlbum, true);
    }
}
