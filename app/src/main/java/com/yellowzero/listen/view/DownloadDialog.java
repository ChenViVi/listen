package com.yellowzero.listen.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.yellowzero.listen.R;
import com.yellowzero.listen.model.AppInfo;

public class DownloadDialog extends Dialog {

    private AppInfo appInfo;

    public DownloadDialog(@NonNull Context context, @NonNull AppInfo appInfo) {
        super(context);
        this.appInfo = appInfo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_download);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = displayMetrics.widthPixels;
        getWindow().setAttributes(lp);
        TextView tvTitle = findViewById(R.id.tvTitle);
        TextView tvInfo = findViewById(R.id.tvInfo);
        ProgressBar pbLoad = findViewById(R.id.pbLoad);
        TextView tvDownload = findViewById(R.id.tvDownload);
        TextView tvCancel = findViewById(R.id.tvCancel);
        tvTitle.setText(appInfo.getName());
        tvInfo.setText(appInfo.getInfo());
        pbLoad.setVisibility(View.GONE);
        if (appInfo.isForceUpdate()) {
            setCancelable(false);
            tvCancel.setVisibility(View.GONE);
        }
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tvDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
