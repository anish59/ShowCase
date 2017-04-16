package com.showcase.helper;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.showcase.R;


/**
 * Created by ANISH on 16-04-2017.
 */

public class UIHelper {
    public static void initToolbar(final AppCompatActivity activity, Toolbar toolbar, String title) {
        toolbar.setTitle(title);
        toolbar.setTitleTextColor(Color.WHITE);
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.fireIntent(activity, false);
            }
        });
    }

    public static void fireIntent(Activity context, boolean isNewActivity) {
        context.finish();
        if (!isNewActivity) {
            context.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } else {
            context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }
}
