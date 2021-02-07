package com.yellowzero.listen.model.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;

@Entity
public class ImageLike {
    @Id
    private long id;
    private String urlSmall;
    private int imageWidth;
    private int imageHeight;
    private String userName;
    private String urlAvatar;
    @Generated(hash = 1247885085)
    public ImageLike(long id, String urlSmall, int imageWidth, int imageHeight,
            String userName, String urlAvatar) {
        this.id = id;
        this.urlSmall = urlSmall;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.userName = userName;
        this.urlAvatar = urlAvatar;
    }
    @Generated(hash = 897914664)
    public ImageLike() {
    }
    public long getId() {
        return this.id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getUrlSmall() {
        return this.urlSmall;
    }
    public void setUrlSmall(String urlSmall) {
        this.urlSmall = urlSmall;
    }
    public int getImageWidth() {
        return this.imageWidth;
    }
    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }
    public int getImageHeight() {
        return this.imageHeight;
    }
    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }
    public String getUserName() {
        return this.userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getUrlAvatar() {
        return this.urlAvatar;
    }
    public void setUrlAvatar(String urlAvatar) {
        this.urlAvatar = urlAvatar;
    }
}
