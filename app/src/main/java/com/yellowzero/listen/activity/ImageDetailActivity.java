package com.yellowzero.listen.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.allen.library.RxHttpUtils;
import com.allen.library.bean.BaseData;
import com.allen.library.download.DownloadObserver;
import com.allen.library.interceptor.Transformer;
import com.allen.library.observer.StringObserver;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.material.snackbar.Snackbar;
import com.yellowzero.listen.App;
import com.yellowzero.listen.R;
import com.yellowzero.listen.adapter.ImageDetailAdapter;
import com.yellowzero.listen.model.Image;
import com.yellowzero.listen.model.entity.ImageEntity;
import com.yellowzero.listen.model.entity.ImageEntityDao;
import com.yellowzero.listen.observer.DataObserver;
import com.yellowzero.listen.service.ImageService;
import com.yellowzero.listen.util.PackageUtil;
import com.yellowzero.listen.view.VerticalLayoutManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ImageDetailActivity extends AppCompatActivity {

    private static final String KEY_POSITION= "position";
    private static final String KEY_TAG_ID= "tagId";
    private static final String KEY_IMAGE= "image";

    private static final int PAGE_SIZE = 20;

    private Image image;
    private int position;
    private Integer tagId;
    private Image currentImage;
    private List<Image> itemList = new ArrayList<>();
    private ImageDetailAdapter adapter;
    private ImageEntityDao imageEntityDao;

    private TextView tvName;
    private TextView tvViewCount;
    private TextView tvLikeCount;
    private TextView tvDownload;
    private ImageView ivAvatar;
    private ImageView ivLike;
    private ImageView ivDownload;
    private View llDownload;
    private View llShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);
        position = getIntent().getIntExtra(KEY_POSITION, 0);
        tagId = getIntent().getIntExtra(KEY_TAG_ID, -1);
        if (tagId == -1)
            tagId = null;
        image = currentImage = (Image) getIntent().getSerializableExtra(KEY_IMAGE);
        if (image == null)
            return;
        itemList.add(image);
        imageEntityDao = ((App) getApplication()).getDaoSession().getImageEntityDao();
        Toolbar toolbar = findViewById(R.id.toolbar);
        tvName = findViewById(R.id.tvName);
        tvViewCount = findViewById(R.id.tvViewCount);
        tvLikeCount = findViewById(R.id.tvLikeCount);
        tvDownload = findViewById(R.id.tvDownload);
        ivAvatar = findViewById(R.id.ivAvatar);
        ivLike = findViewById(R.id.ivLike);
        ivDownload = findViewById(R.id.ivDownload);
        llDownload = findViewById(R.id.llDownload);
        llShare = findViewById(R.id.llShare);
        RecyclerView rvList = findViewById(R.id.rvList);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        adapter = new ImageDetailAdapter(this, itemList);
        VerticalLayoutManager layoutManager = new VerticalLayoutManager(this);
        layoutManager.setOnPageSlideListener(new VerticalLayoutManager.OnPageSlideListener() {
            @Override
            public void onPageSelected(int position, boolean isBottom) {
                currentImage = itemList.get(position);
                loadBarView();
                if (isBottom)
                   loadList();
            }
        });
        rvList.setLayoutManager(layoutManager);
        rvList.setAdapter(adapter);
        loadBarView();
    }

    public static void start(Context context, Integer tagId, int position, @NonNull Image image) {
        Intent intent = new Intent(context, ImageDetailActivity.class);
        if (tagId != null)
            intent.putExtra(KEY_TAG_ID, tagId);
        intent.putExtra(KEY_POSITION, position);
        intent.putExtra(KEY_IMAGE, image);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        RxHttpUtils.cancel("download");
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

    private void loadBarView() {
        tvName.setText(currentImage.getUser().getName());
        tvViewCount.setText(String.valueOf(currentImage.getViewCount()));
        tvLikeCount.setText(String.valueOf(currentImage.getLikeCount()));
        ImageEntity imageEntity = imageEntityDao.load(currentImage.getId());
        boolean isLike = false;
        if (imageEntity != null)
            isLike = imageEntity.getLike();
        String path = getExternalFilesDir(null) + File.separator + image.getImageName();
        File file = new File(path);
        boolean isDownload = file.exists();
        ivLike.setImageResource(isLike ? R.drawable.ic_star_enable : R.drawable.ic_star);
        ivDownload.setImageResource(isDownload ? R.drawable.ic_download_enable : R.drawable.ic_download);
        tvDownload.setText(isDownload ? R.string.tv_downloaded : R.string.tv_download);
        Glide.with(ImageDetailActivity.this)
                .load(currentImage.getUser().getAvatar())
                .placeholder(R.drawable.ic_holder_circle)
                .error(R.drawable.ic_holder_circle)
                .transform(new CircleCrop())
                .into(ivAvatar);
    }

    private void loadList() {
        RxHttpUtils.createApi(ImageService.class)
                .list(tagId, position, PAGE_SIZE)
                .compose(Transformer.<BaseData<List<Image>>>switchSchedulers())
                .subscribe(new DataObserver<List<Image>>() {

                    @Override
                    protected void onSuccess(List<Image> data) {
                        if (data != null && data.size() > 0) {
                            itemList.addAll(data);
                            adapter.notifyDataSetChanged();
                            position = position + PAGE_SIZE;
                        }
                    }
                });
    }

    public void onClickSource(View view) {
        if (PackageUtil.isPackageInstalled(this, "com.sina.weibo")) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse("sinaweibo://detail?mblogid=" + currentImage.getWeiboId()));
            startActivity(intent);
        } else
            WebViewActivity.start(this, "https://m.weibo.cn/detail/" + currentImage.getWeiboId());
    }

    public void onClickLike(View view) {
        ImageEntity imageEntity = imageEntityDao.load(image.getId());
        boolean isNewEntity = false;
        if (imageEntity == null) {
            isNewEntity = true;
            imageEntity = currentImage.toEntity();
        }
        imageEntity.setLike(!imageEntity.getLike());
        if (imageEntity.getLike())
            imageEntity.setLikeCount(imageEntity.getLikeCount() + 1);
        else if(imageEntity.getLikeCount() > 0)
            imageEntity.setLikeCount(imageEntity.getLikeCount() - 1);
        saveImageEntity(imageEntity, isNewEntity);
        ivLike.setImageResource(imageEntity.getLike() ? R.drawable.ic_star_enable : R.drawable.ic_star);
        tvLikeCount.setText(String.valueOf(imageEntity.getLike() ? image.getLikeCount() + 1 : image.getLikeCount()));
        RxHttpUtils.createApi(ImageService.class)
                .like(image.getId(), imageEntity.getLike())
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
        llDownload.setEnabled(false);
        RxHttpUtils
                .downloadFile(currentImage.getImageInfoLarge().getUrl())
                .subscribe(new DownloadObserver(image.getImageName()) {
                    @Override
                    protected String setTag() {
                        return "download";
                    }

                    @Override
                    protected void onError(String errorMsg) {
                        llDownload.setEnabled(true);
                        Toast.makeText(ImageDetailActivity.this, R.string.ts_download_error, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    protected void onSuccess(long bytesRead, long contentLength, float progress, boolean done, String filePath) {
                        tvDownload.setText(String.format(Locale.CHINESE,"%.2f%%", progress));
                        if (done) {
                            llDownload.setEnabled(true);
                            tvDownload.setText(R.string.tv_downloaded);
                            ivDownload.setImageResource(R.drawable.ic_download_enable);
                            Snackbar.make(view, R.string.ts_download_success, Snackbar.LENGTH_LONG).setAction(R.string.tv_open, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    File file = new File(filePath);
                                    Intent intent = new Intent();
                                    intent.setAction(android.content.Intent.ACTION_VIEW);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        Uri contentUri = FileProvider.getUriForFile(ImageDetailActivity.this, getPackageName()+".fileprovider", file);
                                        intent.setDataAndType(contentUri, "image/*");
                                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    } else {
                                        intent.setDataAndType(Uri.fromFile(file), "image/*");
                                    }
                                    startActivity(intent);
                                }
                            }).show();
                        }
                    }
                });
    }

    public void onClickShare(View view) {
        llShare.setEnabled(false);
        String path = getExternalFilesDir(null) + File.separator + image.getImageName();
        File file = new File(path);
        if (file.exists()) {
            share(file);
            llShare.setEnabled(true);
        }
        else {
            llDownload.setEnabled(false);
            RxHttpUtils
                    .downloadFile(currentImage.getImageInfoLarge().getUrl())
                    .subscribe(new DownloadObserver(image.getImageName()) {
                        @Override
                        protected String setTag() {
                            return "download";
                        }

                        @Override
                        protected void onError(String errorMsg) {
                            llDownload.setEnabled(true);
                            llShare.setEnabled(true);
                            Toast.makeText(ImageDetailActivity.this, R.string.ts_download_error, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        protected void onSuccess(long bytesRead, long contentLength, float progress, boolean done, String filePath) {
                            tvDownload.setText(String.format(Locale.CHINESE,"%.2f%%", progress));
                            if (done) {
                                llDownload.setEnabled(true);
                                llShare.setEnabled(true);
                                tvDownload.setText(R.string.tv_downloaded);
                                ivDownload.setImageResource(R.drawable.ic_download_enable);
                                share(new File(filePath));
                            }
                        }
                    });
        }
    }

    private void share(File file) {
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
    }

    private void saveImageEntity(ImageEntity imageEntity, boolean isNewEntity) {
        if (isNewEntity)
            new Thread(new Runnable() {
                @Override
                public void run() {
                    imageEntityDao.insert(imageEntity);
                }
            }).start();
        else
            new Thread(new Runnable() {
                @Override
                public void run() {
                    imageEntityDao.update(imageEntity);
                }
            }).start();
    }
}