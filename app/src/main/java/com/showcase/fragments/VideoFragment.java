package com.showcase.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.showcase.R;
import com.showcase.ShowCaseApplication;
import com.showcase.adapter.BaseFragmentAdapter;
import com.showcase.componentHelper.PhoneMediaVideoController;
import com.showcase.componentHelper.VideoThumbleLoader;

import java.util.ArrayList;

public class VideoFragment extends Fragment implements PhoneMediaVideoController.loadAllVideoMediaInterface {

	private GridView mView;
	private Context mContext;
	private int itemWidth = 100;
	private ListAdapter listAdapter;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		/** Inflating the layout for this fragment **/
		mContext = this.getActivity();
		View v = inflater.inflate(R.layout.fragment_gallery, null);
		mView=(GridView)v.findViewById(R.id.grid_view);
		initAdapter();
		return v;
	}
	
	private void initAdapter(){
		mView.setAdapter(listAdapter = new ListAdapter(mContext));
        int position = mView.getFirstVisiblePosition();
        int columnsCount = 3;
        mView.setNumColumns(columnsCount);
        itemWidth = (ShowCaseApplication.displaySize.x - ((columnsCount + 1) * ShowCaseApplication.dp(4))) / columnsCount;
        mView.setColumnWidth(itemWidth);

        listAdapter.notifyDataSetChanged();
        mView.setSelection(position);
        loadData();
	}
	
	private void loadData() {
		PhoneMediaVideoController mPhoneMediaVideoController = new PhoneMediaVideoController();
		mPhoneMediaVideoController.setLoadallvideomediainterface(this);
		mPhoneMediaVideoController.loadAllVideoMedia(mContext);
	}

	@Override
	public void loadVideo(ArrayList<PhoneMediaVideoController.VideoDetails> arrVideoDetails) {
		arrayVideoDetails=arrVideoDetails;
		if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
	}
	

	private ArrayList<PhoneMediaVideoController.VideoDetails> arrayVideoDetails = null;
	private class ListAdapter extends BaseFragmentAdapter {
		private Context mContext;
		private VideoThumbleLoader thumbleLoader;
		private LayoutInflater inflater;
		
		public ListAdapter(Context context) {
			this.mContext = context;
			this.thumbleLoader=new VideoThumbleLoader(mContext);
			this.inflater= (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public boolean areAllItemsEnabled() {
			return true;
		}

		@Override
		public boolean isEnabled(int i) {
			return true;
		}

		@Override
		public int getCount() {
			return arrayVideoDetails != null ? arrayVideoDetails.size() : 0;
		}

		@Override
		public Object getItem(int i) {
			return null;
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			ViewHolder mViewHolder ;
			if (view == null) {
				mViewHolder=new ViewHolder();
				view = inflater.inflate(R.layout.photo_picker_album_layout,viewGroup, false);
				mViewHolder.img = (ImageView) view.findViewById(R.id.media_photo_image);
				mViewHolder.txtTitle = (TextView) view.findViewById(R.id.album_name);
				mViewHolder.txtCount = (TextView) view.findViewById(R.id.album_count);
				
				ViewGroup.LayoutParams params = view.getLayoutParams();
				params.width = itemWidth;
				params.height = itemWidth;
				view.setLayoutParams(params);
				view.setTag(mViewHolder);
			}else { 
				mViewHolder = (ViewHolder) view.getTag();
			}
			
			PhoneMediaVideoController.VideoDetails mVideoDetails = arrayVideoDetails.get(i);
			final String videoPath=mVideoDetails.path;
			thumbleLoader.DisplayImage(""+mVideoDetails.imageId, mContext, mViewHolder.img, null); 
			mViewHolder.txtTitle.setText(mVideoDetails.displayname);

			view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					try {
						if (videoPath == null) {
							return;
						}
						Intent tostart = new Intent(Intent.ACTION_VIEW);
						tostart.setDataAndType(Uri.parse(videoPath), "video/*");
						startActivity(tostart);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			return view;
		}
		private class ViewHolder{
			ImageView img;
			TextView txtTitle;
			TextView txtCount;
		}

	}
}
	