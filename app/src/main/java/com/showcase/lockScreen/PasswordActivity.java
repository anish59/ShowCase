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
import com.showcase.helper.FunctionHelper;
import com.showcase.helper.PrefUtils;

import java.util.List;

public class PasswordActivity extends AppCompatActivity {
    private Context context;


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
            void onComplete(List<PatternLockView.Dot> pattern) {
                FunctionHelper.logE("pattern:", "" + pattern);
            }
        });
    }
}
