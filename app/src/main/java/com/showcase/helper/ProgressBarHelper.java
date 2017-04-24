package com.showcase.helper;

import android.app.ProgressDialog;
import android.content.Context;

public class ProgressBarHelper implements ProgressListener {

    ProgressDialog fpd;


    public ProgressBarHelper(Context context, String msg) {
        fpd = new ProgressDialog(context);
        fpd.setCanceledOnTouchOutside(false);
        fpd.setMessage(msg);
    }

    @Override
    public void showProgressDialog() {
        if (fpd != null) {
            fpd.show();
        }
    }

    @Override
    public void hidProgressDialog() {
        if (fpd != null) {
            fpd.dismiss();
        }
    }


}
