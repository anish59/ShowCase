package com.showcase.lockScreen;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.showcase.R;
import com.showcase.helper.FunctionHelper;
import com.showcase.helper.PrefUtils;

public class PasswordActivity extends AppCompatActivity {
    private Context context;
    private String firstPattern = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.password_setting);
        init();
    }


    private void init() {
    }


    public void clickSetPattern(View view) {
        new PatternDialog(context, new PatternDialog.OnPatternCompleteListener() {
            @Override
            public void complete(String pattern) {
                FunctionHelper.logE("#pattern", " " + pattern);
                firstPattern = pattern;
                confirmAgain();
            }
        }, false);// false => setVisibility gone for Confirm again text in Dialog
    }

    private void confirmAgain() {

        new PatternDialog(context, new PatternDialog.OnPatternCompleteListener() {
            @Override
            public void complete(String pattern) {
                FunctionHelper.logE("#confirmPattern:", pattern);
                if (firstPattern.equals(pattern)) {
                    //do Proceed with Prefs
                    PrefUtils.setLockStatus(context, true);
                    PrefUtils.setUserPin(context, pattern);
                    Toast.makeText(context, "Pattern set.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Pattern not matching, please try again :(", Toast.LENGTH_SHORT).show();
                }
            }
        }, true);// true => setVisibility Visible for Confirm again text in Dialog

    }

    public void clickSetPin(View view) {
        new PinDialog(context, new PinDialog.OnPinCompleteListener() {
            @Override
            public void complete(String pin) {
                Toast.makeText(context, pin, Toast.LENGTH_SHORT).show();
            }
        }, false);
    }
}
