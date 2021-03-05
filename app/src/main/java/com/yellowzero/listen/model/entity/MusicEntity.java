package com.yellowzero.listen.model.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class MusicEntity {
    @Id
    private long id;
    private String name;
    private String url;
    private String cover;
    private String link;
    @Generated(hash = 1678015931)
    public MusicEntity(long id, String name, String url, String cover,
            String link) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.cover = cover;
        this.link = link;
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
    public String getLink() {
        return this.link;
    }
    public void setLink(String link) {
        this.link = link;
    }
}
