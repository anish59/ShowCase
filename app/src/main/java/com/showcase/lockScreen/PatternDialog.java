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
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.andrognito.patternlockview.utils.ResourceUtils;
import com.showcase.R;
import com.showcase.helper.PrefUtils;

import java.util.List;

/**
 * Created by anish on 06-07-2017.
 */

public class PatternDialog extends Dialog {
    private Context context;
    private OnPatternCompleteListener patternCompleteListener;
    private PatternLockView patternlockview;
    private TextView txtConfirm;
    private boolean isFromSettings = false;
    private onPatternDrawnListener onPatternDrawnListener;

    public PatternDialog(@NonNull Context context, OnPatternCompleteListener patternCompleteListener, boolean isConfirm) {
        super(context);
        this.context = context;
        this.patternCompleteListener = patternCompleteListener;
        this.isFromSettings = true;
        init(isConfirm);
        initListener();
        show();
    }

    public PatternDialog(@NonNull Context context, onPatternDrawnListener onPatternDrawnListener) {
        super(context);
        this.context = context;
        this.isFromSettings = false;
        this.onPatternDrawnListener=onPatternDrawnListener;
        init(false);
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
                String inputPattern = PatternLockUtils.patternToString(patternlockview, pattern);
                if (isFromSettings) {
                    patternCompleteListener.complete(inputPattern);
                    dismiss();
                } else {
                    if (PrefUtils.getUserPassword(context).equals(inputPattern)) {
                        patternlockview.setCorrectStateColor(ResourceUtils.getColor(context, R.color.correct_blue));
                        onPatternDrawnListener.complete(true);
                        dismiss();
                    } else {
                        Toast.makeText(context, "Wrong Pattern :(", Toast.LENGTH_SHORT).show();
//                        patternlockview.setWrongStateColor(ResourceUtils.getColor(context, R.color.pomegranate));
                        patternlockview.setNormalStateColor(ResourceUtils.getColor(context, R.color.white));
                        patternlockview.setCorrectStateColor(ResourceUtils.getColor(context, R.color.pomegranate));
                    }
                }
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

        this.setCanceledOnTouchOutside(false);

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

    public interface onPatternDrawnListener {
        void complete(boolean isCorrect);
    }
}
