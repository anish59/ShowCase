package com.showcase.lockScreen;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.showcase.R;
import com.showcase.helper.PrefUtils;

/**
 * Created by anish on 06-07-2017.
 */

public class PinDialog extends Dialog {
    private Context context;
    private OnPinCompleteListener onPinCompleteListener;
    private PinLockView pinlockview;
    private IndicatorDots indicatordots;
    private PinLockListener pinLockListener;
    private TextView txtConfirm;
    private boolean isFromSetting = false;
    private OnPinEnteredListener onPinEnteredListener;

    public PinDialog(@NonNull Context context, OnPinCompleteListener onPinCompleteListener, boolean isConfirm) {
        super(context);
        this.context = context;
        this.onPinCompleteListener = onPinCompleteListener;
        isFromSetting = true;
        initListener();
        init(isConfirm);
        show();
    }

    public PinDialog(@NonNull Context context, OnPinEnteredListener onPinEnteredListener) {
        super(context);
        this.context = context;
        this.onPinEnteredListener = onPinEnteredListener;
        isFromSetting = false;

        initListener();
        init(false);
        show();
    }

    private void initListener() {
        pinLockListener = new PinLockListener() {
            @Override
            public void onComplete(String pin) {
                if (isFromSetting) {
                    onPinCompleteListener.complete(pin);
                    dismiss();
                } else {
                    if (PrefUtils.getUserPassword(context).equals(pin)) {
                        onPinEnteredListener.complete(true);
                        dismiss();
                    } else {
                        onPinEnteredListener.complete(false);
                        pinlockview.resetPinLockView();
                        Toast.makeText(context, "Wrong pin :(", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onEmpty() {

            }

            @Override
            public void onPinChange(int pinLength, String intermediatePin) {

            }
        };
    }


    private void init(boolean isConfirm) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_pin, null, false);
        txtConfirm = (TextView) view.findViewById(R.id.txtConfirm);
        pinlockview = (PinLockView) view.findViewById(R.id.pin_lock_view);
        indicatordots = (IndicatorDots) view.findViewById(R.id.indicator_dots);

        setDialogProperties(view);

        pinlockview.attachIndicatorDots(indicatordots);
        pinlockview.setPinLockListener(pinLockListener);

        pinlockview.setPinLength(4);
        pinlockview.setTextColor(ContextCompat.getColor(context, R.color.white));
        indicatordots.setIndicatorType(IndicatorDots.IndicatorType.FILL_WITH_ANIMATION);

        if (isConfirm) {
            txtConfirm.setVisibility(View.VISIBLE);
        } else {
            txtConfirm.setVisibility(View.GONE);
        }
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
        this.setCanceledOnTouchOutside(false);

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

    public interface OnPinEnteredListener {
        void complete(boolean isCorrect);
    }
}
