package com.showcase;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.showcase.adapter.AlbumAdapter;
import com.showcase.componentHelper.PhoneMediaControl;
import com.showcase.fragments.GalleryFragment;
import com.showcase.helper.FunctionHelper;
import com.showcase.helper.ProgressBarHelper;
import com.showcase.helper.ProgressListener;
import com.showcase.helper.SimpleDividerItemDecoration;
import com.showcase.helper.UIHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private MenuItem itemDeselect, itemShare, itemDelete;
    private ArrayList<PhoneMediaControl.PhotoEntry> photos2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album2);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        recyclerView = (RecyclerView) findViewById(R.id.rvImages);


        getIntentData();
        //photos2= GalleryFragment.albumsSorted.get(AlbummID).photos;
        initializeActionBar();
        initAdapter();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_unSelect:
                imageDeselectionAndNotify(itemDeselect, itemShare, itemDelete);
                break;
            case R.id.action_shareImages:
                shareImages();
                break;
            case R.id.action_deleteImages:
                deleteImages();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeActionBar() {
        UIHelper.initToolbar(AlbumActivity2.this, toolbar, nameAlbum + " (" + photos.size() + ")");
    }

    private void getIntentData() {
        Bundle mBundle = getIntent().getExtras();
        nameAlbum = mBundle.getString("Key_Name");
        AlbummID = Integer.parseInt(mBundle.getString("Key_ID"));
        albumsSorted = GalleryFragment.albumsSorted;

        photos = albumsSorted.get(AlbummID).photos;
        photos2 = photos;
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
                    mBundle.putInt("Key_FolderID", AlbummID);
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
                invalidateOptionsMenu();
            }
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
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
        return super.onCreateOptionsMenu(menu);
    }

    private void imageDeselectionAndNotify(MenuItem itemDeselect, MenuItem itemShare, MenuItem itemDelete) {
        refreshData();
        itemDeselect.setVisible(false);
        itemShare.setVisible(false);
        itemDelete.setVisible(false);

    }

    private void refreshAfterDelete() {
        isMultiSelectionMode = false;
        for (PhoneMediaControl.PhotoEntry photo : photos) {
            photo.setSelected(false);
        }
        itemDeselect.setVisible(false);
        itemShare.setVisible(false);
        itemDelete.setVisible(false);
        initializeActionBar();
       /* if (photos != null && !photos.isEmpty()) {
            isMultiSelectionMode = false;
            for (PhoneMediaControl.PhotoEntry photo : photos) {
                photo.setSelected(false);
            }
            Log.e("RemainingPics(C): ", "" + photos.size());
            mAdapter.setItems(photos, mContext, true);
        }*/
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

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Sharing Images");
        intent.setType("image/jpeg"); /* This is sharing jpeg images. */

        ArrayList<Uri> files = new ArrayList<>();
        if (isMultiSelectionMode && !photos.isEmpty()) {
            for (PhoneMediaControl.PhotoEntry photo : photos) {
                if (photo.isSelected()) {
                    files.add(Uri.parse(photo.path));
                }
            }

            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
            startActivity(intent);
            imageDeselectionAndNotify(itemDeselect, itemShare, itemDelete);
        }
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

//                            photos.remove(i);
                            photos.remove(i);
                            mAdapter.notifyDataSetChanged();
                            FunctionHelper.callBroadCast(mContext, fDelete);
                            //photos2.remove(i);
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

}
