package com.showcase.helper;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.showcase.R;


/**
 * Created by ANISH on 16-04-2017.
 */

public class UIHelper {
    public static void initToolbar(final AppCompatActivity activity, Toolbar toolbar, String title) {
        if (toolbar != null) {
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
    }

    public static void fireIntent(Activity context, boolean isNewActivity) {
        context.finish();
        if (!isNewActivity) {
            context.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } else {
            context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    public static void fireIntent(Context context, Intent intent, boolean isNewActivity) {
        Activity activity = (Activity) context;
        context.startActivity(intent);
        if (!isNewActivity) {
            activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } else {
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    public static void dialogWithTwoOpt(Context mContext, String message, final DialogOptionsSelectedListener selectedListener, String yesOption, String noOption) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(yesOption, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (selectedListener != null)
                            selectedListener.onSelect(true);
                        dialog.dismiss();
                    }

                })
                .setNegativeButton(noOption, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (selectedListener != null)
                            selectedListener.onSelect(false);
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public interface DialogOptionsSelectedListener {
        void onSelect(boolean isYes);
    }
}
