package com.yellowzero.listen.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.allen.library.RxHttpUtils;
import com.allen.library.interceptor.Transformer;
import com.allen.library.observer.StringObserver;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.jaeger.library.StatusBarUtil;
import com.yellowzero.listen.R;
import com.yellowzero.listen.model.Image;
import com.yellowzero.listen.service.ImageService;
import com.yellowzero.listen.util.PackageUtil;
import com.yellowzero.listen.view.TagCloudView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ImageActivity extends AppCompatActivity {

    private int mXOld, mYOld;
    private boolean isDownloadClicked = false;
    private String filePath;
    private Image image;
    private ImageView ivImage;
    private ScrollView svDetail;
    private TagCloudView viewTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        StatusBarUtil.setColor(ImageActivity.this, Color.BLACK);
        Toolbar toolbar = findViewById(R.id.toolbar);
        ProgressBar pbLoad = findViewById(R.id.pbLoad);
        TextView tvName = findViewById(R.id.tvName);
        TextView tvViewCount = findViewById(R.id.tvViewCount);
        TextView tvText = findViewById(R.id.tvText);
        ImageView ivAvatar = findViewById(R.id.ivAvatar);
        ivImage = findViewById(R.id.ivImage);
        svDetail = findViewById(R.id.svDetail);
        viewTag = findViewById(R.id.viewTag);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        image = (Image) getIntent().getSerializableExtra("image");
        if (image == null)
            return;
        filePath = getExternalFilesDir(null) + File.separator + image.getPid() + ".jpg";
        tvName.setText(image.getUser().getName());
        tvViewCount.setText(String.valueOf(image.getViewCount() + 1));
        tvText.setText(Html.fromHtml(image.getText()));
        ArrayList<String> tagList = image.getTagList();
        if (tagList != null && tagList.size() != 0) {
            viewTag.setVisibility(View.VISIBLE);
            viewTag.setTags(tagList);
            viewTag.setOnTagClickListener(new TagCloudView.OnTagClickListener() {
                @Override
                public void onTagClick(int position) {
                    Log.e("xxxxx", "position=" + position);
                }
            });
        }
        Glide.with(this).load(image.getUser().getAvatar()).transform(new CircleCrop()).into(ivAvatar);
        Glide.with(this)
                .asBitmap()
                .load(image.getUrlLarge())
                .error(R.drawable.ic_holder)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        ivImage.setImageBitmap(resource);
                        pbLoad.setVisibility(View.GONE);
                        try {
                            File file = new File(filePath);
                            FileOutputStream out = new FileOutputStream(file);
                            resource.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            out.flush();
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
        RxHttpUtils.createApi(ImageService.class)
                .view(image.getId())
                .compose(Transformer.<String>switchSchedulers())
                .subscribe(new StringObserver() {

                    @Override
                    protected void onError(String errorMsg) {

                    }

                    @Override
                    protected void onSuccess(String data) {

                    }
                });
        ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivImage.setVisibility(View.GONE);
                svDetail.setVisibility(View.VISIBLE);
            }
        });
        svDetail.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mXOld = x;
                    mYOld = y;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (x == mXOld || y == mYOld) {
                        if (ivImage.getVisibility() != View.VISIBLE) {
                            ivImage.setVisibility(View.VISIBLE);
                            svDetail.setVisibility(View.GONE);
                        }
                        return false;
                    }
                }
                return false;
            }
        });
    }

    public void onClickSource(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        if (PackageUtil.isPackageInstalled(this, "com.sina.weibo")) {
            intent.setData(Uri.parse("sinaweibo://detail?mblogid=" + image.getWeiboId()));
        } else {
            intent.setData(Uri.parse("https://m.weibo.cn/detail/" + image.getWeiboId()));
        }
        startActivity(intent);
    }

    public void onClickLike(View view) {

    }

    public void onClickDownload(View view) {
        isDownloadClicked = true;
        if (new File(filePath).exists()) {
            Toast.makeText(this, String.format(getResources().getString(R.string.ts_download_success), filePath), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.ts_download_error, Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickShare(View view) {
        File file = new File(filePath);
        if (file.exists()) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri contentUri = FileProvider.getUriForFile(this, getPackageName()+".fileprovider", file);
                intent.putExtra(Intent.EXTRA_STREAM, contentUri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }else {
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            }
            intent.setType("image/png");
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.ts_download_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (!isDownloadClicked) {
            File file = new File(filePath);
            if (file.exists())
                file.delete();
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}