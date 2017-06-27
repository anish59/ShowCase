package com.showcase.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.showcase.componentHelper.PhoneMediaControl;
import com.showcase.componentHelper.PhotoPreview;

import java.util.List;

/**
 * Created by anish on 27-06-2017.
 */

public class CustomViewPagerAdpater extends PagerAdapter {

    private List<PhoneMediaControl.PhotoEntry> photos;
    private Context context;

    public CustomViewPagerAdpater(List<PhoneMediaControl.PhotoEntry> photos, Context context) {
        this.photos = photos;
        this.context = context;
    }


    @Override
    public int getCount() {
        if (photos == null) {
            return 0;
        } else {
            return photos.size();
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PhotoPreview photoPreview = new PhotoPreview(context);
        ((ViewPager) container).addView(photoPreview);
        photoPreview.loadImage(photos.get(position));
        return photoPreview;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public void setPagerItems(List<PhoneMediaControl.PhotoEntry> photos) {
        this.photos = photos;
        notifyDataSetChanged();
    }


}
