package com.showcase;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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
import com.showcase.adapter.BaseFragmentAdapter;
import com.showcase.componentHelper.PhoneMediaControl;
import com.showcase.fragments.GalleryFragment;
import com.showcase.helper.UIHelper;

import java.util.ArrayList;

public class AlbumActivity extends ActionBarActivity {

	private GridView mView;
	private Context mContext;
	Toolbar toolbar;

	public static ArrayList<PhoneMediaControl.AlbumEntry> albumsSorted = null;
	public static ArrayList<PhoneMediaControl.PhotoEntry> photos = new ArrayList<PhoneMediaControl.PhotoEntry>();

	private int itemWidth = 100;
	private ListAdapter listAdapter;
	private int AlbummID=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album);
		toolbar=(Toolbar)findViewById(R.id.tool_bar);
		
		 mContext=AlbumActivity.this;
		 initializeActionBar();
		 initializeView();
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
		
		Bundle mBundle=getIntent().getExtras();
		String nameAlbum = mBundle.getString("Key_Name");
		AlbummID = Integer.parseInt(mBundle.getString("Key_ID")) ;
		albumsSorted= GalleryFragment.albumsSorted;
		
		photos=albumsSorted.get(AlbummID).photos;

		UIHelper.initToolbar(AlbumActivity.this,toolbar,nameAlbum+" ("+photos.size()+")");
		/*toolbar = (Toolbar) findViewById(R.id.tool_bar);
		toolbar.setTitle(nameAlbum+" ("+photos.size()+")");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
	}
	
	private void initializeView(){
		mView=(GridView)findViewById(R.id.grid_view);
		mView.setAdapter(listAdapter = new ListAdapter(AlbumActivity.this));

        int position = mView.getFirstVisiblePosition();
        int columnsCount = 2;
        mView.setNumColumns(columnsCount);
        itemWidth = (ShowCaseApplication.displaySize.x - ((columnsCount + 1) * ShowCaseApplication.dp(4))) / columnsCount;
        mView.setColumnWidth(itemWidth);

        listAdapter.notifyDataSetChanged();
        mView.setSelection(position);
        mView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            	Intent mIntent=new Intent(AlbumActivity.this,PhotoPreviewActivity.class);
            	Bundle mBundle=new Bundle();
            	mBundle.putInt("Key_FolderID", AlbummID);
            	mBundle.putInt("Key_ID", position);
            	mIntent.putExtras(mBundle);
            	startActivity(mIntent);
            }
        });
        
		LoadAllAlbum();
	}
 
	private void LoadAllAlbum(){
		if (mView != null && mView.getEmptyView() == null) {
			mView.setEmptyView(null);
        }
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
	}
	

	private class ListAdapter extends BaseFragmentAdapter {
		private Context mContext;
		private LayoutInflater layoutInflater;
		private DisplayImageOptions options;
		private ImageLoader imageLoader = ImageLoader.getInstance();

		public ListAdapter(Context context) {
			this.mContext = context;
			this.layoutInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			options = new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.nophotos)
					.showImageForEmptyUri(R.drawable.nophotos)
					.showImageOnFail(R.drawable.nophotos).cacheInMemory(true)
					.cacheOnDisc(true).considerExifParams(true).build();
			imageLoader.init(ImageLoaderConfiguration.createDefault(context));

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
			return photos != null ? photos.size() : 0;
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
			viewHolder mHolder;
			if (view == null) {
				mHolder = new viewHolder();
				view = layoutInflater.inflate(R.layout.album_image, viewGroup,false);
				mHolder.imageView = (ImageView) view.findViewById(R.id.album_image);
				ViewGroup.LayoutParams params = view.getLayoutParams();
				params.width = itemWidth;
				params.height = itemWidth;
				view.setLayoutParams(params);
				mHolder.imageView.setTag(i);
				
				view.setTag(mHolder);
			} else {
				mHolder = (viewHolder) view.getTag();
			}
			PhoneMediaControl.PhotoEntry mPhotoEntry = photos.get(i);
			String path = mPhotoEntry.path;
			if (path != null && !path.equals("")) {
				ImageLoader.getInstance().displayImage("file://" + path, mHolder.imageView);
			}

			return view;
		}

		@Override
		public int getItemViewType(int i) {
			return 0;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public boolean isEmpty() {
			return albumsSorted == null || albumsSorted.isEmpty();
		}

		class viewHolder {
			public ImageView imageView;
		}

	}

}
