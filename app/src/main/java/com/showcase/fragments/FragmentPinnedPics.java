package com.showcase.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import com.showcase.helper.SimpleDividerItemDecoration;
import com.showcase.helper.UIHelper;

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
    private RecyclerView recyclerView;


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
                            //   deleteFolderImages();
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
                photos = new ArrayList<PhoneMediaControl.PhotoEntry>();
                photos = albumsSorted.get(0).photos;
                initializeActionBar();
                if (!isFromRestart) {
                    initAdapter();
                } else {
                    mAdapter.setItems(photos, context, true);
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
        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL));

        recyclerView.setItemViewCacheSize(photos != null ? photos.size() : 0);//keep it minimum 1 to avoid any conflict
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(context));
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

    private void initializeActionBar() {
        UIHelper.initToolbar((MainActivity) getActivity(), new MainActivity().toolbar, "Pinned Images" + " (" + photos.size() + ")");
    }


}
