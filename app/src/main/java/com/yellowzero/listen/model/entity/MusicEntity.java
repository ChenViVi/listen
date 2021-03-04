package com.yellowzero.listen.model.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class MusicEntity {
    @Id
    private long id;
    private String musicId;
    private String title;
    private String url;
    private String coverImg;

    @Generated(hash = 872911466)
    public MusicEntity(long id, String musicId, String title, String url,
            String coverImg) {
        this.id = id;
        this.musicId = musicId;
        this.title = title;
        this.url = url;
        this.coverImg = coverImg;
    }
    @Generated(hash = 1380251324)
    public MusicEntity() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMusicId() {
        return musicId;
    }

    public void setMusicId(String musicId) {
        this.musicId = musicId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCoverImg() {
        return coverImg;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }
}
