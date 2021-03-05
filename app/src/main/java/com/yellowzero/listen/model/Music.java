package com.yellowzero.listen.model;

import com.yellowzero.listen.model.entity.MusicEntity;

public class Music {
    private int id;
    private String name;
    private String url;
    private String cover;
    private String link;
    private int number;
    private boolean isSelected;
    private boolean isAvailable;
    private boolean isCached;

    public Music(){}

    public Music(MusicEntity entity) {
        setName(entity.getName());
        setUrl(entity.getUrl());
        setCover(entity.getCover());
        setLink(entity.getLink());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public boolean isCached() {
        return isCached;
    }

    public void setCached(boolean cached) {
        isCached = cached;
    }

    public boolean isLocal() {
        return !getUrl().startsWith("http");
    }

    public MusicEntity toEntity() {
        MusicEntity entity = new MusicEntity();
        entity.setName(getName());
        entity.setUrl(getUrl());
        entity.setCover(getCover());
        entity.setLink(getLink());
        return entity;
    }
}
