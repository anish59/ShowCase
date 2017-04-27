package com.showcase.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.showcase.R;
import com.showcase.ShowCaseApplication;
import com.showcase.adapter.VideoAdapter;
import com.showcase.componentHelper.PhoneMediaVideoController;
import com.showcase.helper.SimpleDividerItemDecoration;

import java.util.ArrayList;

public class VideoFragment2 extends Fragment implements PhoneMediaVideoController.loadAllVideoMediaInterface {

    private RecyclerView recyclerView;
    private Context mContext;
    private VideoAdapter mAdapter;
    private ArrayList<PhoneMediaVideoController.VideoDetails> arrayVideoDetails = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /** Inflating the layout for this fragment **/
        mContext = this.getActivity();
        View v = inflater.inflate(R.layout.fragment_gallery2, null);
        recyclerView = (RecyclerView) v.findViewById(R.id.rvImages);
        loadData();
        initAdapter();
        return v;
    }

    private void initAdapter() {
        mAdapter = new VideoAdapter(getActivity(), arrayVideoDetails, new VideoAdapter.OnItemClickedListener() {
            @Override
            public void onClick(String videoPath, View v, int position) {

            }

            @Override
            public void onLongClick(String videoPath, View v, int position) {

            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setItemViewCacheSize(arrayVideoDetails != null ? arrayVideoDetails.size() : 0);//keep it minimum 1 to avoid any conflict
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
        arrayVideoDetails = arrVideoDetails;
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }


}
