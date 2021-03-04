package com.yellowzero.listen.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yellowzero.listen.R;
import com.yellowzero.listen.model.MusicTag;
import com.yellowzero.listen.player.bean.DefaultAlbum;

import java.util.ArrayList;
import java.util.List;

public class MusicListFavActivity extends AppCompatActivity {

    private static final String KEY_TAG = "tag";

    private MusicTag tag;
    List<DefaultAlbum.DefaultMusic> musics = new ArrayList<>();
    private DefaultAlbum album = new DefaultAlbum();
    private SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list_fav);
        tag = (MusicTag) getIntent().getSerializableExtra(KEY_TAG);
        if (tag == null)
            return;
        album.setAlbumId(String.valueOf(tag.getId()));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(tag.getName());
        RecyclerView rvList = findViewById(R.id.rvList);
        refreshLayout = findViewById(R.id.refreshLayout);
        ImageView ivCover = findViewById(R.id.ivCover);
        TextView tvName = findViewById(R.id.tvName);
        ImageView ivPlay = findViewById(R.id.ivPlay);
        View llMusic = findViewById(R.id.llMusic);
    }

    public static void start(Context context, MusicTag tag) {
        Intent intent = new Intent(context, MusicListFavActivity.class);
        intent.putExtra(KEY_TAG, tag);
        context.startActivity(intent);
    }
}