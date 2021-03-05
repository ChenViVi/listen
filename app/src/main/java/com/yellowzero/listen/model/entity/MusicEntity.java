package com.yellowzero.listen.model.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class MusicEntity {
    @Id
    private long id;
    private int type;
    private String name;
    private String url;
    private String cover;
    @Generated(hash = 1241762285)
    public MusicEntity(long id, int type, String name, String url, String cover) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.url = url;
        this.cover = cover;
    }
    @Generated(hash = 1380251324)
    public MusicEntity() {
    }
    public long getId() {
        return this.id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getUrl() {
        return this.url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getCover() {
        return this.cover;
    }
    public void setCover(String cover) {
        this.cover = cover;
    }
}
