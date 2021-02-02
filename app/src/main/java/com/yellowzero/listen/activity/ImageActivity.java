package com.yellowzero.listen.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.jaeger.library.StatusBarUtil;
import com.yellowzero.listen.R;
import com.yellowzero.listen.model.Image;
import com.yellowzero.listen.util.PackageUtil;

public class ImageActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener{

    private Image image;
    private View llDetail;
    private ImageView ivImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        StatusBarUtil.setColor(ImageActivity.this, Color.BLACK);
        Toolbar toolbar = findViewById(R.id.toolbar);
        ProgressBar pbLoad = findViewById(R.id.pbLoad);
        TextView tvName = findViewById(R.id.tvName);
        TextView tvText = findViewById(R.id.tvText);
        ImageView ivAvatar = findViewById(R.id.ivAvatar);
        llDetail = findViewById(R.id.llDetail);
        ivImage = findViewById(R.id.ivImage);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        image = (Image) getIntent().getSerializableExtra("image");
        if (image == null)
            return;
        tvName.setText(image.getUser().getName());
        tvText.setText(Html.fromHtml(image.getText()));
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
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
        ivImage.setOnClickListener(this);
        ivImage.setOnLongClickListener(this);
        llDetail.setOnClickListener(this);
        llDetail.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivImage || v.getId() == R.id.llDetail) {
            if (ivImage.getVisibility() == View.VISIBLE) {
                ivImage.setVisibility(View.GONE);
                llDetail.setVisibility(View.VISIBLE);
            } else {
                ivImage.setVisibility(View.VISIBLE);
                llDetail.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (v.getId() == R.id.ivImage || v.getId() == R.id.llDetail) {
            return true;
        }
        return false;
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