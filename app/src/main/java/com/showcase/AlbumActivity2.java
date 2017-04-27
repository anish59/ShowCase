package com.showcase;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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

public class AlbumActivity2 extends ActionBarActivity {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album2);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        recyclerView = (RecyclerView) findViewById(R.id.rvImages);


        getIntentData();
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

    private void refreshData() {
        if (!photos.isEmpty()) {
            isMultiSelectionMode = false;
            photos = new ArrayList<>();
            albumsSorted = GalleryFragment.albumsSorted;
            if (albumsSorted.isEmpty()) {
                Toast.makeText(mContext, "No Image Found", Toast.LENGTH_SHORT).show();
            } else {
                int size = GalleryFragment.albumsSorted.get(AlbummID).photos.size();
                Log.e("RefreshSize :", size + "");
                photos.addAll(GalleryFragment.albumsSorted.get(AlbummID).photos);
            }
            mAdapter.setItems(photos, mContext, true);
            for (PhoneMediaControl.PhotoEntry photo : photos) {
                photo.setSelected(false);
            }
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
                int picsSize = photos.size();
                for (int i = 0; i < picsSize; i++) {
                    if (photos.get(i).isSelected) {
                        File fDelete = new File(photos.get(i).path);
                        Log.e("path:", " " + i + ": " + photos.get(i).path);
                        if (fDelete.exists()) {
                            Log.e("gettingDeleted: ", "" + fDelete.delete() + " : " + fDelete.getAbsolutePath());

                            fDelete.delete();
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
                            //photos2.remove(i);
                        } else {
                            Log.e("fDelete: ", "" + fDelete.delete() + " : " + fDelete.getAbsoluteFile());
                        }
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        progressListener.hidProgressDialog();
        FunctionHelper.callBroadCast(mContext);
        imageDeselectionAndNotify(itemDeselect, itemShare, itemDelete);

    }


}
