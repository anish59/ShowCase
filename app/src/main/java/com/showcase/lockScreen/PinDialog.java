package com.showcase.lockScreen;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.showcase.R;

/**
 * Created by anish on 06-07-2017.
 */

public class PinDialog extends Dialog {
    private Context context;
    private OnPinCompleteListener onPinCompleteListener;
    private PinLockView pinlockview;
    private IndicatorDots indicatordots;
    private PinLockListener pinLockListener;

    public PinDialog(@NonNull Context context, OnPinCompleteListener onPinCompleteListener, boolean isConfirm) {
        super(context);
        this.context = context;
        this.onPinCompleteListener = onPinCompleteListener;
        initListener();
        init();
        show();
    }

    private void initListener() {
        pinLockListener = new PinLockListener() {
            @Override
            public void onComplete(String pin) {
                onPinCompleteListener.complete(pin);
                dismiss();
            }

            @Override
            public void onEmpty() {

            }

            @Override
            public void onPinChange(int pinLength, String intermediatePin) {

            }
        };
    }


    private void init() {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_pin, null, false);
        pinlockview = (PinLockView) view.findViewById(R.id.pin_lock_view);
        indicatordots = (IndicatorDots) view.findViewById(R.id.indicator_dots);

        setDialogProperties(view);

        pinlockview.attachIndicatorDots(indicatordots);
        pinlockview.setPinLockListener(pinLockListener);

        pinlockview.setPinLength(4);
        pinlockview.setTextColor(ContextCompat.getColor(context, R.color.white));
        indicatordots.setIndicatorType(IndicatorDots.IndicatorType.FILL_WITH_ANIMATION);
    }

    private void setDialogProperties(View view) {
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

    public interface OnPinCompleteListener {
        void complete(String pin);
    }
}
