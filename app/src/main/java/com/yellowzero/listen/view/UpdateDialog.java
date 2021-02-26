package com.yellowzero.listen.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.allen.library.RxHttpUtils;
import com.allen.library.download.DownloadObserver;
import com.yellowzero.listen.R;
import com.yellowzero.listen.model.AppInfo;

import java.io.File;

public class UpdateDialog extends Dialog {

    private AppInfo appInfo;

    public UpdateDialog(@NonNull Context context, @NonNull AppInfo appInfo) {
        super(context);
        this.appInfo = appInfo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_update);
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
        tvTitle.setText(String.format(getContext().getString(R.string.tv_app_update), appInfo.getName()));
        tvInfo.setText(appInfo.getInfo());
        pbLoad.setVisibility(View.GONE);
        if (appInfo.isForceUpdate()) {
            setCancelable(false);
            tvCancel.setVisibility(View.GONE);
        }
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancel();
                RxHttpUtils.cancel("download_apk");
                dismiss();
            }
        });
        tvDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pbLoad.setVisibility(View.VISIBLE);
                tvDownload.setEnabled(false);
                String url = appInfo.getUrl();
                int index = url.lastIndexOf("/");
                String name = url.substring(index + 1);
                RxHttpUtils
                        .downloadFile(appInfo.getUrl())
                        .subscribe(new DownloadObserver(name) {
                            //可以通过配置tag用于取消下载请求
                            @Override
                            protected String setTag() {
                                return "download_apk";
                            }

                            @Override
                            protected void onError(String errorMsg) {
                                Toast.makeText(getContext(), R.string.ts_download_apk_error, Toast.LENGTH_SHORT).show();
                                dismiss();
                            }

                            @Override
                            protected void onSuccess(long bytesRead, long contentLength, float progress, boolean done, String filePath) {
                                pbLoad.setProgress((int)progress);
                                if (done) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    File file = new File(filePath);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        Uri contentUri = FileProvider.getUriForFile(getContext(), getContext().getPackageName()+".fileprovider", file);
                                        intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                                    } else {
                                        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                                    }
                                    getContext().startActivity(intent);
                                }
                            }
                        });
            }
        });
    }

    public void onCancel() {

    }

    @Override
    protected void onStop() {
        super.onStop();
        RxHttpUtils.cancel("download_apk");
    }
}
