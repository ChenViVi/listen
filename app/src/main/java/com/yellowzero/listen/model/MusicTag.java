package com.yellowzero.listen.model;

import java.io.Serializable;

public class MusicTag implements Serializable {
    public static final int ID_FAV = -1;
    public static final int ID_LOCAL = -2;
    public static final int ID_LOCAL_ARTIST = -3;
    public static final int ID_LOCAL_OTHER = -4;

    private int id;
    private String name;
    private String cover;
    private int coverRes;
    private long count;

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

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getCoverRes() {
        return coverRes;
    }

    public void setCoverRes(int coverRes) {
        this.coverRes = coverRes;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
