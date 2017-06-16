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
import com.showcase.fragments.GalleryFragment2;

import java.io.File;
import java.io.IOException;
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

        photos = GalleryFragment2.albumsSorted.get(folderPosition).photos;

        mViewPager = (ViewPager) findViewById(R.id.vp_base_app);
        mViewPager.setOnPageChangeListener(this);
        overridePendingTransition(R.anim.activity_alpha_action_in, 0);
        bindData();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem itemDelete, itemShare, itemInfo;
        getMenuInflater().inflate(R.menu.menu_photo_preview, menu);

        itemInfo = menu.findItem(R.id.action_info);
        itemInfo.setVisible(true);

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
                shareImage();
                break;
            case R.id.action_deleteImages:
                // deleteImage();
                break;
            case R.id.action_info:
                imageInfo();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void imageInfo() {
        PhoneMediaControl.PhotoEntry selectedImage = photos.get(mViewPager.getCurrentItem());
        long imgDate = selectedImage.dateTaken;

    }

    private void deleteImage() { //todo: remaining to implement
        PhoneMediaControl.PhotoEntry selectedImage = photos.get(mViewPager.getCurrentItem());
        File file = new File(selectedImage.path);
        if (file.exists()) {
            file.delete();
            if (file.exists()) {
                try {
                    file.getCanonicalFile().delete();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (file.exists()) {
                    context.deleteFile(file.getName());
                }

            }
            photos.remove(mViewPager.getCurrentItem());
            mViewPager.removeViewAt(mViewPager.getCurrentItem());
            if ((mViewPager.getCurrentItem() + 1) >= photos.size()) {
                if (photos != null && !photos.isEmpty()) {
                    mViewPager.setCurrentItem(0);
                }
            } else {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
            }
            mPagerAdapter.notifyDataSetChanged();
        }

    }

    private void shareImage() {
        PhoneMediaControl.PhotoEntry selectedImage = photos.get(mViewPager.getCurrentItem());
        File file = new File(selectedImage.path);
        Intent mShareIntent = new Intent(Intent.ACTION_SEND);
        mShareIntent.setType("image/*");
        mShareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        startActivity(Intent.createChooser(mShareIntent, "Share image"));
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
