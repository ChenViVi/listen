package com.yellowzero.listen.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.allen.library.RxHttpUtils;
import com.allen.library.bean.BaseData;
import com.allen.library.download.DownloadObserver;
import com.allen.library.interceptor.Transformer;
import com.allen.library.observer.StringObserver;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.snackbar.Snackbar;
import com.jaeger.library.StatusBarUtil;
import com.yellowzero.listen.App;
import com.yellowzero.listen.R;
import com.yellowzero.listen.model.Image;
import com.yellowzero.listen.model.entity.ImageEntity;
import com.yellowzero.listen.model.entity.ImageEntityDao;
import com.yellowzero.listen.observer.DataObserver;
import com.yellowzero.listen.service.ImageService;
import com.yellowzero.listen.util.PackageUtil;
import com.yellowzero.listen.view.TagCloudView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ImagedDetailActivity extends AppCompatActivity {

    private static final String KEY_IMAGE= "image";

    private int mXOld, mYOld;
    private File file;
    private Image image;
    private ImageEntityDao imageEntityDao;
    private ImageEntity imageEntity;
    private boolean isNewEntity = false;

    private ProgressBar pbLoad;
    private TextView tvName;
    private TextView tvViewCount;
    private TextView tvLikeCount;
    private TextView tvText;
    private ImageView ivAvatar;
    private TagCloudView viewTag;
    private ImageView ivImage;
    private ImageView ivLike;
    private ImageView ivDownload;
    private ScrollView svDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);
        StatusBarUtil.setColor(ImagedDetailActivity.this, Color.BLACK);
        Toolbar toolbar = findViewById(R.id.toolbar);
        pbLoad = findViewById(R.id.pbLoad);
        tvName = findViewById(R.id.tvName);
        tvViewCount = findViewById(R.id.tvViewCount);
        tvLikeCount = findViewById(R.id.tvLikeCount);
        tvText = findViewById(R.id.tvText);
        ivAvatar = findViewById(R.id.ivAvatar);
        viewTag = findViewById(R.id.viewTag);
        ivLike = findViewById(R.id.ivLike);
        ivDownload = findViewById(R.id.ivDownload);
        ivImage = findViewById(R.id.ivImage);
        svDetail = findViewById(R.id.svDetail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        image = (Image) getIntent().getSerializableExtra(KEY_IMAGE);
        if (image == null)
            return;
        imageEntityDao = ((App) getApplication()).getDaoSession().getImageEntityDao();
        imageEntity = imageEntityDao.load(image.getId());
        if (imageEntity == null) {
            String urlSmall = image.getImageInfoSmall().getUrl();
            int width = image.getImageInfoSmall().getWidth();
            int height = image.getImageInfoSmall().getHeight();
            if (image.isGif()) {
                urlSmall = image.getImageInfoLarge().getUrl();
                width = image.getImageInfoSmall().getWidth();
                height = image.getImageInfoSmall().getHeight();
            }
            imageEntity = new ImageEntity(image.getId(),
                    urlSmall,
                    width, height,
                    image.getUser().getName(),
                    image.getUser().getAvatar(),
                    false, false);
            isNewEntity = true;
        }
        ivLike.setImageResource(imageEntity.getLike() ? R.drawable.ic_star_enable : R.drawable.ic_star);
        ivDownload.setImageResource(imageEntity.getDownload() ? R.drawable.ic_download_enable : R.drawable.ic_download);
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
        if (!image.isLoadFromDb()) {
            loadPage(image);
        } else {
            image = null;
            RxHttpUtils.createApi(ImageService.class)
                    .image(image.getId())
                    .compose(Transformer.<BaseData<Image>>switchSchedulers())
                    .subscribe(new DataObserver<Image>() {
                        @Override
                        protected void onSuccess(Image data) {
                            image = data;
                            loadPage(image);
                        }
                    });
        }
    }

    public static void start(Context context, @NonNull Image image) {
        Intent intent = new Intent(context, ImagedDetailActivity.class);
        intent.putExtra(KEY_IMAGE, image);
        context.startActivity(intent);
    }

    public void onClickSource(View view) {
        if (image == null) {
            Toast.makeText(this, R.string.ts_http_loading, Toast.LENGTH_SHORT).show();
            return;
        }
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
        if (image == null) {
            Toast.makeText(this, R.string.ts_http_loading, Toast.LENGTH_SHORT).show();
            return;
        }
        imageEntity.setLike(!imageEntity.getLike());
        saveImageEntity();
        ivLike.setImageResource(imageEntity.getLike() ? R.drawable.ic_star_enable : R.drawable.ic_star);
        tvLikeCount.setText(String.valueOf(imageEntity.getLike() ? image.getLikeCount() + 1 : image.getLikeCount()));
        RxHttpUtils.createApi(ImageService.class)
                .like(image.getId(), imageEntity.getLike() ? 1 : 0)
                .compose(Transformer.<String>switchSchedulers())
                .subscribe(new StringObserver() {

                    @Override
                    protected void onError(String errorMsg) {

                    }

                    @Override
                    protected void onSuccess(String data) {

                    }
                });
    }

    public void onClickDownload(View view) {
        if (image == null) {
            Toast.makeText(this, R.string.ts_http_loading, Toast.LENGTH_SHORT).show();
            return;
        }
        if (file.exists()) {
            Snackbar.make(view, R.string.ts_download_success, Snackbar.LENGTH_LONG).setAction(R.string.tv_open, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(android.content.Intent.ACTION_VIEW);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Uri contentUri = FileProvider.getUriForFile(ImagedDetailActivity.this, getPackageName()+".fileprovider", file);
                        intent.setDataAndType(contentUri, "image/*");
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } else {
                        intent.setDataAndType(Uri.fromFile(file), "image/*");
                    }
                    startActivity(intent);
                }
            }).show();
            imageEntity.setDownload(true);
            ivDownload.setImageResource(R.drawable.ic_download_enable);
            saveImageEntity();
        } else {
            downloadImage();
            Toast.makeText(this, R.string.ts_download_error, Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickShare(View view) {
        if (image == null) {
            Toast.makeText(this, R.string.ts_http_loading, Toast.LENGTH_SHORT).show();
            return;
        }
        if (file.exists()) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri contentUri = FileProvider.getUriForFile(this, getPackageName()+".fileprovider", file);
                intent.putExtra(Intent.EXTRA_STREAM, contentUri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }else {
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            }
            intent.setType("image/*");
            startActivity(intent);
        } else {
            downloadImage();
            Toast.makeText(this, R.string.ts_download_image_process, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        RxHttpUtils.cancel("download");
        if (!imageEntity.getDownload() && file.exists())
            new Thread(new Runnable() {
                @Override
                public void run() {
                    file.delete();
                }
            }).start();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void loadPage(Image image) {
        file = new File(getExternalFilesDir(null) + File.separator + image.getImageName());
        tvName.setText(image.getUser().getName());
        tvViewCount.setText(String.valueOf(image.getViewCount() + 1));
        tvLikeCount.setText(String.valueOf(image.getLikeCount()));
        tvText.setText(Html.fromHtml(image.getText()));
        ArrayList<String> tagList = image.getTagList();
        if (tagList != null && tagList.size() != 0) {
            viewTag.setVisibility(View.VISIBLE);
            viewTag.setTags(tagList);
            viewTag.setOnTagClickListener(new TagCloudView.OnTagClickListener() {
                @Override
                public void onTagClick(int position) {
                    ImageActivity.start(ImagedDetailActivity.this, image.getTags().get(position));
                }
            });
        }
        Glide.with(this).load(image.getUser().getAvatar()).transform(new CircleCrop()).into(ivAvatar);
        String suffix = image.getSuffix();
        if (image.isGif())
            Glide.with(this)
                    .asGif()
                    .load(image.getImageInfoLarge().getUrl())
                    .error(R.drawable.ic_holder)
                    .into(new ImageViewTarget<GifDrawable>(ivImage) {
                        @Override
                        protected void setResource(@Nullable GifDrawable resource) {
                            ivImage.setImageDrawable(resource);
                            pbLoad.setVisibility(View.GONE);
                        }
                    });
        else
            Glide.with(this)
                    .asBitmap()
                    .load(image.getImageInfoLarge().getUrl())
                    .error(R.drawable.ic_holder)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            ivImage.setImageBitmap(resource);
                            pbLoad.setVisibility(View.GONE);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
                                        if (suffix.equals("png"))
                                            format = Bitmap.CompressFormat.PNG;
                                        FileOutputStream out = new FileOutputStream(file);
                                        resource.compress(format, 100, out);
                                        out.flush();
                                        out.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
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
        downloadImage();
    }

    private void downloadImage() {
        if (!file.exists())
            RxHttpUtils
                    .downloadFile(image.getImageInfoLarge().getUrl())
                    .subscribe(new DownloadObserver(image.getImageName()) {
                        @Override
                        protected String setTag() {
                            return "download";
                        }

                        @Override
                        protected void onError(String errorMsg) {
                        }

                        @Override
                        protected void onSuccess(long bytesRead, long contentLength, float progress, boolean done, String filePath) {
                        }
                    });
    }

    private void saveImageEntity() {
        if (isNewEntity) {
            isNewEntity = false;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    imageEntityDao.insert(imageEntity);
                }
            }).start();
        } else
            new Thread(new Runnable() {
                @Override
                public void run() {
                    imageEntityDao.update(imageEntity);
                }
            }).start();
    }
}