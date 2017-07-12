package com.showcase.lockScreen;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.showcase.R;

import java.util.List;

/**
 * Created by anish on 06-07-2017.
 */

public class PatternDialog extends Dialog {
    private Context context;
    private OnPatternCompleteListener patternCompleteListener;
    private PatternLockView patternlockview;
    private TextView txtConfirm;

    public PatternDialog(@NonNull Context context, OnPatternCompleteListener patternCompleteListener, boolean isConfirm) {
        super(context);
        this.context = context;
        this.patternCompleteListener = patternCompleteListener;
        init(isConfirm);
        initListener();
        show();
    }

    private void initListener() {
        patternlockview.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {

            }

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                patternCompleteListener.complete(PatternLockUtils.patternToString(patternlockview, pattern));
                dismiss();
            }

            @Override
            public void onCleared() {

            }
        });
    }

    private void init(boolean isConfirm) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_pattern, null, false);
        txtConfirm = (TextView) view.findViewById(R.id.txtConfirm);
        patternlockview = (PatternLockView) view.findViewById(R.id.pattern_lock_view);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(view);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);

        this.setCanceledOnTouchOutside(true);

        if (isConfirm) {
            txtConfirm.setVisibility(View.VISIBLE);
        }
    }

    /*public abstract static class OnPatternCompleteListener {
        abstract void onComplete(List<PatternLockView.Dot> pattern);

        void onStarted() {
        }

        void onProgress(List<PatternLockView.Dot> progressPattern) {
        }

        void onCleared() {
        }
    }*/

   /* public static void setPatternCompleteListener(OnPatternCompleteListener patternCompleteListener) {
        patternCompleteListener = patternCompleteListener;
    }*/

    public interface OnPatternCompleteListener {
        void complete(String pattern);
    }
}
