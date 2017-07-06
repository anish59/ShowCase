package com.showcase.lockScreen;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.showcase.MainActivity;
import com.showcase.R;
import com.showcase.helper.AppConstants;
import com.showcase.helper.PrefUtils;

import java.util.List;

public class PasswordActivity extends AppCompatActivity {
    boolean isKeepPattern = false, isChangePassword = false;
    private PatternLockView mPatternLockView;
    private TextView txtConfirmAgain;
    private boolean isFromOption;
    private AppConstants.PrivacySetting privacySetting;
    private Context context = PasswordActivity.this;
    private boolean isConfirmNeeded = false;
    private int patternAskedCount = 0;
    private String confirmPattern = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_pattern);
        mPatternLockView = (PatternLockView) findViewById(R.id.pattern_lock_view);
        txtConfirmAgain = (TextView) findViewById(R.id.txtConfirm);
        getDataFromIntent();
        init();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        isFromOption = intent.getBooleanExtra(AppConstants.INTENT_IS_FROM_OPTION, false);
        privacySetting = (AppConstants.PrivacySetting) intent.getSerializableExtra(AppConstants.INTENT_PRIVACY_SETTING);
    }

    private void init() {



        /*if (!isFromOption) {
            if (PrefUtils.getLockStatus(context)) {
                checkPassword(PrefUtils.getUserPin(context));
            }
        }*/
    }


    private void checkPassword(String userPin) {
        if (userPin.isEmpty()) {
            isKeepPattern = true;
        }
        mPatternLockView.addPatternLockListener(mPatternLockViewListener);
    }

    private PatternLockViewListener mPatternLockViewListener = new PatternLockViewListener() {
        @Override
        public void onStarted() {
            Log.d(getClass().getName(), "Pattern drawing started");
        }

        @Override
        public void onProgress(List<PatternLockView.Dot> progressPattern) {
            Log.d(getClass().getName(), "Pattern progress: " + PatternLockUtils.patternToString(mPatternLockView, progressPattern));
        }

        @Override
        public void onComplete(List<PatternLockView.Dot> pattern) {
            Log.d(getClass().getName(), "Pattern complete: " + PatternLockUtils.patternToString(mPatternLockView, pattern));
            String drawnPattern = PatternLockUtils.patternToString(mPatternLockView, pattern);

            if (patternAskedCount == 0) {
                confirmPattern = drawnPattern;
            } else {
                if (confirmPattern.equals(drawnPattern)) {
                    Toast.makeText(context, "Your Gallery is locked", Toast.LENGTH_SHORT).show();
                    onCleared();
                } else {
                    mPatternLockView.setWrongStateColor(getResources().getColor(R.color.wrongPatternColor));
                    return;
                }
            }
            patternAskedCount++;

            isConfirmNeeded = PrefUtils.checkFirstTime(context);
            if (isConfirmNeeded) {
                txtConfirmAgain.setVisibility(View.VISIBLE);
                mPatternLockView.addPatternLockListener(mPatternLockViewListener);
            } else {

            }

          /*  if (isKeepPattern) {
                PrefUtils.setUserPin(context, drawnPattern);
                PrefUtils.setLockStatus(context, true);
            }
            onCleared();*/
        }

        @Override
        public void onCleared() {
            Log.d(getClass().getName(), "Pattern has been cleared");
            startActivity(new Intent(PasswordActivity.this, MainActivity.class));
        }
    };

    private void sendToMainActivity() {
        startActivity(new Intent(context, MainActivity.class));
    }

}
