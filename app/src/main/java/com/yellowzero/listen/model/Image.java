package com.yellowzero.listen.model;

import java.util.List;

public class Image {
    private int id;
    private String weiboId;
    private String urlSmall;
    private String urlLarge;
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

    public String getUrlSmall() {
        return urlSmall;
    }

    public void setUrlSmall(String urlSmall) {
        this.urlSmall = urlSmall;
    }

    public String getUrlLarge() {
        return urlLarge;
    }

    public void setUrlLarge(String urlLarge) {
        this.urlLarge = urlLarge;
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
