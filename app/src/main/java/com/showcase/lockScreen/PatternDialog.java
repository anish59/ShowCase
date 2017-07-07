package com.showcase.lockScreen;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.showcase.R;

import java.util.List;

/**
 * Created by anish on 06-07-2017.
 */

public class PatternDialog extends Dialog implements PatternLockViewListener {
    private Context context;
    private static OnPatternCompleteListener patternCompleteListener;


    public PatternDialog(@NonNull Context context, OnPatternCompleteListener patternCompleteListener) {
        super(context);
        this.context = context;
        this.patternCompleteListener = patternCompleteListener;
        init();
        initListener();
        show();
    }

    private void initListener() {

    }

    private void init() {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_pattern, null, false);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(view);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);

        this.setCanceledOnTouchOutside(true);
    }

    @Override
    public void onStarted() {
        patternCompleteListener.onStarted();
    }

    @Override
    public void onProgress(List<PatternLockView.Dot> progressPattern) {
        patternCompleteListener.onProgress(progressPattern);
    }

    @Override
    public void onComplete(List<PatternLockView.Dot> pattern) {
        patternCompleteListener.onComplete(pattern);
    }

    @Override
    public void onCleared() {
        patternCompleteListener.onCleared();
    }

    public abstract static class OnPatternCompleteListener {
        abstract void onComplete(List<PatternLockView.Dot> pattern);

        void onStarted() {
        }

        void onProgress(List<PatternLockView.Dot> progressPattern) {
        }

        void onCleared() {
        }
    }
    public static void setPatternCompleteListener(OnPatternCompleteListener patternCompleteListener) {
        patternCompleteListener = patternCompleteListener;
    }

}
