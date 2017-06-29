package com.showcase;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.showcase.adapter.CustomViewPagerAdpater;
import com.showcase.adapter.MainPagerAdapter;
import com.showcase.componentHelper.PhoneMediaControl;
import com.showcase.componentHelper.PhotoPreview;
import com.showcase.dialog.ImageInfoDialog;
import com.showcase.fragments.GalleryFragment;
import com.showcase.fragments.GalleryFragment2;
import com.showcase.helper.DateHelper;
import com.showcase.helper.FunctionHelper;
import com.showcase.helper.ProgressBarHelper;
import com.showcase.helper.ProgressListener;
import com.showcase.helper.UIHelper;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class PhotoPreviewActivity extends ActionBarActivity implements OnPageChangeListener {

    private ViewPager mViewPager;
    protected List<PhoneMediaControl.PhotoEntry> photos;
    protected int current, folderPosition;
    protected Context context;
    private Toolbar toolbar;
    private CustomViewPagerAdpater mPagerAdapter1;
    ProgressListener progressListener;
//    private MainPagerAdapter mainPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photopreview);

        init();

    }

    private void init() {
        context = PhotoPreviewActivity.this;
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressListener = new ProgressBarHelper(context, "Please wait...");

        Bundle mBundle = getIntent().getExtras();
        folderPosition = mBundle.getInt("Key_FolderID");
        current = mBundle.getInt("Key_ID");

        photos = GalleryFragment2.albumsSorted.get(folderPosition).photos;

        mViewPager = (ViewPager) findViewById(R.id.vp_base_app);
        mViewPager.setOnPageChangeListener(this);
        overridePendingTransition(R.anim.activity_alpha_action_in, 0);
        bindData(photos);
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
        itemDelete.setVisible(true);


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
                UIHelper.dialogWithTwoOpt(context, "Are you sure you want to delete this pic?", new UIHelper.DialogOptionsSelectedListener() {
                    @Override
                    public void onSelect(boolean isYes) {
                        if (isYes) {
                            deleteImage();
                        }
                    }
                }, "yes", "no");
                break;
            case R.id.action_info:
                imageInfo();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void imageInfo() {
        PhoneMediaControl.PhotoEntry selectedImage = photos.get(mViewPager.getCurrentItem());
        long date = selectedImage.dateTaken;
        String imgDate = DateHelper.dateToString(new Date(date), DateHelper.MMM_dd_yy);
        String imgPath = selectedImage.path;
        Resources resources = getResources();
       /* UIHelper.dialogWithOneOption

                (context, getString(R.string.synopsisTitle), String.format(resources.getString(R.string.imgSynopsis), imgDate, imgPath), new UIHelper.DialogOptionsSelectedListener() {
                    @Override
                    public void onSelect(boolean isYes) {
                        //Do nothing
                    }
                }, "Ok");*/

        new ImageInfoDialog(context, new File(imgPath).getName(), imgDate, FunctionHelper.getFileSize(imgPath), imgPath);
    }

    private void deleteImage() { //todo: remaining to implement
        progressListener.showProgressDialog();
        PhoneMediaControl.PhotoEntry selectedImage = photos.get(mViewPager.getCurrentItem());
        File file = new File(selectedImage.path);
        if (file.exists()) {
            deleteWithProjection(file);
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
                progressListener.hidProgressDialog();
            }
            if (!file.exists()) {

                int currentPostion = mViewPager.getCurrentItem();
                photos.remove(currentPostion);
                mPagerAdapter1 = new CustomViewPagerAdpater(photos, context);
                mViewPager.setAdapter(mPagerAdapter1);
//                mPagerAdapter1.notifyDataSetChanged();
                if ((currentPostion + 1) >= photos.size()) {
                    if (photos != null && !photos.isEmpty()) {
                        mViewPager.setCurrentItem(0);
                    }
                } else {
                    mViewPager.setCurrentItem(currentPostion);
                }
            } else {
                Toast.makeText(context, "Unable to delete image.", Toast.LENGTH_SHORT).show();
            }
            progressListener.hidProgressDialog();
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


    protected void bindData(List<PhoneMediaControl.PhotoEntry> photos) {
        mPagerAdapter1 = new CustomViewPagerAdpater(photos, context);
        mViewPager.setAdapter(mPagerAdapter1);
        mViewPager.setCurrentItem(current);
        toolbar.setTitle((current + 1) + "/" + this.photos.size());
    }

//    private PagerAdapter mPagerAdapter = new PagerAdapter() {
//
//        @Override
//        public int getCount() {
//            if (photos == null) {
//                return 0;
//            } else {
//                return photos.size();
//            }
//        }
//
//        @Override
//        public View instantiateItem(final ViewGroup container, final int position) {
//            PhotoPreview photoPreview = new PhotoPreview(context);
//            ((ViewPager) container).addView(photoPreview);
//            photoPreview.loadImage(photos.get(position));
//            return photoPreview;
//        }
//
//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//            container.removeView((View) object);
//        }
//
//        @Override
//        public boolean isViewFromObject(View view, Object object) {
//            return view == object;
//        }
//
//    };

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

    private void deleteWithProjection(File fDelete) {
        // Set up the projection (we only need the ID)
        String[] projection = {MediaStore.Images.Media._ID};

        // Match on the file path
        String selection = MediaStore.Images.Media.DATA + " = ?";
        String[] selectionArgs = new String[]{fDelete.getAbsolutePath()};

        // Query for the ID of the media matching the file path
        Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = context.getContentResolver();
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
}
