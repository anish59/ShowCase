package com.showcase.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.showcase.R;

/**
 * Created by anish on 29-06-2017.
 */

public class ImageInfoDialog extends Dialog  {
    private TextView txtImageName;
    private TextView txtSize;
    private TextView txtCreatedDate;
    private TextView txtPath;
    private Button btnOk;

    public ImageInfoDialog(@NonNull Context context, String name, String createdDate, String size, String path) {
        super(context);
        init();
        setData(name, createdDate, size, path);
        initListener();
        show();
    }

    private void initListener() {
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void setData(String name, String createdDate, String size, String path) {
        txtImageName.setText(name);
        txtCreatedDate.setText(createdDate);
        txtSize.setText(size);
        txtPath.setText(path);
    }

    private void init() {
        View view = getLayoutInflater().inflate(R.layout.dialog_image_info, null, false);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(view);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);
        this.setCanceledOnTouchOutside(true);

        btnOk = (Button) view.findViewById(R.id.btnOk);
        txtPath = (TextView) view.findViewById(R.id.txtPath);
        txtCreatedDate = (TextView) view.findViewById(R.id.txtCreatedDate);
        txtSize = (TextView) view.findViewById(R.id.txtSize);
        txtImageName = (TextView) view.findViewById(R.id.txtImageName);
    }

}
