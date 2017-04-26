package com.showcase;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.showcase.adapter.AlbumAdapter;
import com.showcase.adapter.BaseFragmentAdapter;
import com.showcase.componentHelper.PhoneMediaControl;
import com.showcase.fragments.GalleryFragment;
import com.showcase.helper.SimpleDividerItemDecoration;
import com.showcase.helper.UIHelper;

import java.util.ArrayList;

public class AlbumActivity2 extends ActionBarActivity {

    private Context mContext = AlbumActivity2.this;
    private AlbumAdapter mAdapter;
    Toolbar toolbar;
    RecyclerView recyclerView;

    public static ArrayList<PhoneMediaControl.AlbumEntry> albumsSorted = null;
    public static ArrayList<PhoneMediaControl.PhotoEntry> photos = new ArrayList<PhoneMediaControl.PhotoEntry>();

    private int AlbummID = 0;
    private String nameAlbum;

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
            case android.R.id.home:
                onBackPressed();
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
                /*if (isMultiSelectionMode) {
                    setImageSelection(view, position);
                } else {*/
                Intent mIntent = new Intent(mContext, PhotoPreviewActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putInt("Key_FolderID", AlbummID);
                mBundle.putInt("Key_ID", position);
                mIntent.putExtras(mBundle);
                startActivity(mIntent);
//                }

            }

            @Override
            public void onLongClick(int position, View view) {
                /*if (!isMultiSelectionMode) {
                    firstSelectedPosition = position;
                    setImageSelection(view, position);
                    isMultiSelectionMode = true;
                }
                isDeselectIconVisible = true;
                getActivity().invalidateOptionsMenu();*/
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

        //LoadAllAlbum();
    }

    private void LoadAllAlbum() {
        /*if (mView != null && mView.getEmptyView() == null) {
            mView.setEmptyView(null);
        }
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }*/
    }


}
