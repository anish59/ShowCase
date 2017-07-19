package com.showcase.lockScreen;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.showcase.R;
import com.showcase.helper.FunctionHelper;
import com.showcase.helper.PrefUtils;
import com.showcase.helper.UIHelper;

public class PasswordSettingActivity extends AppCompatActivity {
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.password_setting);
    }

    ///////////////////--all clicks--///////////////////////////
    public void clickSetPattern(View view) {
        new PatternDialog(context, new PatternDialog.OnPatternCompleteListener() {
            @Override
            public void complete(String pattern) {
                FunctionHelper.logE("#pattern", " " + pattern);
                confirmPatternAgain(pattern);
            }
        }, false);// false => setVisibility gone for Confirm again text in Dialog
    }

    private void confirmPatternAgain(final String firstPattern) {

        new PatternDialog(context, new PatternDialog.OnPatternCompleteListener() {
            @Override
            public void complete(String pattern) {
                FunctionHelper.logE("#confirmPattern:", pattern);
                if (firstPattern.equals(pattern)) {
                    PrefUtils.setLockStatus(context, true);
                    PrefUtils.setUserPassword(context, pattern);
                    Toast.makeText(context, R.string.pattern_set, Toast.LENGTH_SHORT).show();
                    PrefUtils.setIsPattern(context, true);
                } else {
                    Toast.makeText(context, R.string.pattern_not_macthing_plz_try_again, Toast.LENGTH_SHORT).show();
                }
            }
        }, true);// true => setVisibility Visible for Confirm again text in Dialog

    }

    public void clickSetPin(View view) {
        new PinDialog(context, new PinDialog.OnPinCompleteListener() {
            @Override
            public void complete(String pin) {
                confirmPinAgain(pin);
            }
        }, false);
    }

    private void confirmPinAgain(final String firstPin) {
        new PinDialog(context, new PinDialog.OnPinCompleteListener() {
            @Override
            public void complete(String pin) {
                if (firstPin.equals(pin)) {
                    Toast.makeText(context, R.string.pin_set, Toast.LENGTH_SHORT).show();
                    PrefUtils.setLockStatus(context, true);
                    PrefUtils.setUserPassword(context, pin);
                    PrefUtils.setIsPattern(context, false);
                } else {
                    Toast.makeText(context, R.string.pin_not_matching_please_try_again, Toast.LENGTH_SHORT).show();
                }
            }
        }, true);
    }

    public void clickRemovePassword(View view) {
        UIHelper.dialogWithTwoOpt(context, getString(R.string.are_you_sure_you_want_to_remove_password_lock), new UIHelper.DialogOptionsSelectedListener() {
            @Override
            public void onSelect(boolean isYes) {
                if (isYes) {
                    PrefUtils.setLockStatus(context, false);
                    PrefUtils.setUserPassword(context, "");
                    if (!PrefUtils.getLockStatus(context)) { //lockStatus=> false then only show prompt
                        Toast.makeText(context, "Password Removed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, getString(R.string.yeah_i_m), getString(R.string.cancel));
    }

    ///////////////////--all clicks--///////////////////////////
}
