package com.showcase;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.showcase.componentHelper.PhoneMediaControl;
import com.showcase.componentHelper.PhotoPreview;
import com.showcase.fragments.GalleryFragment;

import java.io.File;
import java.util.List;

public class PhotoPreviewActivity extends ActionBarActivity implements OnPageChangeListener {

    private ViewPager mViewPager;
    protected List<PhoneMediaControl.PhotoEntry> photos;
    protected int current, folderPosition;

    protected Context context;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photopreview);


        context = PhotoPreviewActivity.this;
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle mBundle = getIntent().getExtras();
        folderPosition = mBundle.getInt("Key_FolderID");
        current = mBundle.getInt("Key_ID");

        photos = GalleryFragment.albumsSorted.get(folderPosition).photos;

        mViewPager = (ViewPager) findViewById(R.id.vp_base_app);
        mViewPager.setOnPageChangeListener(this);
        overridePendingTransition(R.anim.activity_alpha_action_in, 0);
        bindData();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem itemDelete, itemShare, itemDeselect;
        getMenuInflater().inflate(R.menu.main, menu);

        itemDeselect = menu.findItem(R.id.action_unSelect);
        itemDeselect.setVisible(false);

        itemShare = menu.findItem(R.id.action_shareImages);
        itemShare.setVisible(true);

        itemDelete = menu.findItem(R.id.action_deleteImages);
        itemDelete.setVisible(false);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_shareImages:
                PhoneMediaControl.PhotoEntry selectedImage = photos.get(mViewPager.getCurrentItem());
                File file = new File(selectedImage.path);
                Intent mShareIntent = new Intent(Intent.ACTION_SEND);
                mShareIntent.setType("image/*");
                mShareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                startActivity(Intent.createChooser(mShareIntent, "Share image"));
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    protected void bindData() {
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(current);
        toolbar.setTitle((current + 1) + "/" + photos.size());
    }

    private PagerAdapter mPagerAdapter = new PagerAdapter() {

        @Override
        public int getCount() {
            if (photos == null) {
                return 0;
            } else {
                return photos.size();
            }
        }

        @Override
        public View instantiateItem(final ViewGroup container, final int position) {
            PhotoPreview photoPreview = new PhotoPreview(context);
            ((ViewPager) container).addView(photoPreview);
            photoPreview.loadImage(photos.get(position));
            return photoPreview;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    };

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int arg0) {
        current = arg0;
        updatePercent();
    }

    protected void updatePercent() {
        toolbar.setTitle((current + 1) + "/" + photos.size());
    }
}
