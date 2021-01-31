package com.yellowzero.listen.model;

import java.util.List;

public class Image {
    private int id;
    private String weiboId;
    private String url;
    private UserWeibo user;
    private List<ImageTag> tags;
    private String text;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWeiboId() {
        return weiboId;
    }

    public void setWeiboId(String weiboId) {
        this.weiboId = weiboId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public UserWeibo getUser() {
        return user;
    }

    public void setUser(UserWeibo user) {
        this.user = user;
    }

    public List<ImageTag> getTags() {
        return tags;
    }

    public void setTags(List<ImageTag> tags) {
        this.tags = tags;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
