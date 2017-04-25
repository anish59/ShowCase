package com.showcase.fragments;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
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
import android.widget.TextView;
import android.widget.Toast;

import com.showcase.PhotoPreviewActivity;
import com.showcase.R;
import com.showcase.adapter.CameraFragmentAdapter;
import com.showcase.componentHelper.PhoneMediaControl;
import com.showcase.helper.ProgressBarHelper;
import com.showcase.helper.ProgressListener;
import com.showcase.helper.SimpleDividerItemDecoration;

import java.io.File;
import java.util.ArrayList;

//Todo: Remaining work
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
    private boolean isDeselectIconVisible = false;
    private ProgressListener progressListener;
    private MenuItem itemDeselect, itemShare, itemDelete;
    private ArrayList<PhoneMediaControl.PhotoEntry> photos2;


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
            photos2 = GalleryFragment.albumsSorted.get(0).photos;
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
                isDeselectIconVisible = true;
                getActivity().invalidateOptionsMenu();
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        if (isDeselectIconVisible) {
            itemDeselect = menu.findItem(R.id.action_unSelect);
            itemDeselect.setVisible(true);

            itemShare = menu.findItem(R.id.action_shareImages);
            itemShare.setVisible(true);

            itemDelete = menu.findItem(R.id.action_deleteImages);
            itemDelete.setVisible(true);
        }
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

    private void deleteImages() {

        try {
            progressListener = new ProgressBarHelper(getActivity(), "Please Wait..");
            progressListener.showProgressDialog();
            if (isMultiSelectionMode && !photos.isEmpty()) {
                int picsSize = photos.size();
                for (int i = 0; i < picsSize; i++) {
                    if (photos.get(i).isSelected) {
                        File fDelete = new File(photos.get(i).path);
                        Log.e("path:", " " + i + ": " + photos.get(i).path);
                        if (fDelete.exists()) {
                            Log.e("gettingDeleted: ", "" + fDelete.delete() + " : " + fDelete.getAbsolutePath());

                            fDelete.getAbsoluteFile().delete();
                            photos2.remove(i);
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
        callBroadCast();
        imageDeselectionAndNotify(itemDeselect, itemShare, itemDelete);

    }

    public void callBroadCast() {
        if (Build.VERSION.SDK_INT >= 14) {
            Log.e("-->", " >= 14");
            MediaScannerConnection.scanFile(getActivity(), new String[]{Environment.getExternalStorageDirectory().toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                /*
                 *   (non-Javadoc)
                 * @see android.media.MediaScannerConnection.OnScanCompletedListener#onScanCompleted(java.lang.String, android.net.Uri)
                 */
                public void onScanCompleted(String path, Uri uri) {
                    Log.e("ExternalStorage", "Scanned " + path + ":");
                    Log.e("ExternalStorage", "-> uri=" + uri);
                }
            });
        } else {
            Log.e("-->", " < 14");
            getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" + Environment.getExternalStorageDirectory())));
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
                Log.e("size", photos2 + "");
                photos.addAll(photos2);
            }
            for (PhoneMediaControl.PhotoEntry photo : photos) {
                photo.setSelected(false);
            }
            mAdapter.setItems(photos, getActivity(), true);
        }
    }
}