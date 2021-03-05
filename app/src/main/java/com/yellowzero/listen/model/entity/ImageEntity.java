package com.yellowzero.listen.model.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class ImageEntity {
    @Id
    private Long id;
    private String pid;
    private long weiboId;
    private long repostId;
    private String text;
    private String userName;
    private String urlAvatar;
    private String urlSmall;
    private int widthSmall;
    private int heightSmall;
    private String urlLarge;
    private int widthLarge;
    private int heightLarge;
    @Generated(hash = 24207231)
    public ImageEntity(Long id, String pid, long weiboId, long repostId,
            String text, String userName, String urlAvatar, String urlSmall,
            int widthSmall, int heightSmall, String urlLarge, int widthLarge,
            int heightLarge) {
        this.id = id;
        this.pid = pid;
        this.weiboId = weiboId;
        this.repostId = repostId;
        this.text = text;
        this.userName = userName;
        this.urlAvatar = urlAvatar;
        this.urlSmall = urlSmall;
        this.widthSmall = widthSmall;
        this.heightSmall = heightSmall;
        this.urlLarge = urlLarge;
        this.widthLarge = widthLarge;
        this.heightLarge = heightLarge;
    }
    @Generated(hash = 2080458212)
    public ImageEntity() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getPid() {
        return this.pid;
    }
    public void setPid(String pid) {
        this.pid = pid;
    }
    public long getWeiboId() {
        return this.weiboId;
    }
    public void setWeiboId(long weiboId) {
        this.weiboId = weiboId;
    }
    public long getRepostId() {
        return this.repostId;
    }
    public void setRepostId(long repostId) {
        this.repostId = repostId;
    }
    public String getText() {
        return this.text;
    }
    public void setText(String text) {
        this.text = text;
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
    public String getUrlSmall() {
        return this.urlSmall;
    }
    public void setUrlSmall(String urlSmall) {
        this.urlSmall = urlSmall;
    }
    public int getWidthSmall() {
        return this.widthSmall;
    }
    public void setWidthSmall(int widthSmall) {
        this.widthSmall = widthSmall;
    }
    public int getHeightSmall() {
        return this.heightSmall;
    }
    public void setHeightSmall(int heightSmall) {
        this.heightSmall = heightSmall;
    }
    public String getUrlLarge() {
        return this.urlLarge;
    }
    public void setUrlLarge(String urlLarge) {
        this.urlLarge = urlLarge;
    }
    public int getWidthLarge() {
        return this.widthLarge;
    }
    public void setWidthLarge(int widthLarge) {
        this.widthLarge = widthLarge;
    }
    public int getHeightLarge() {
        return this.heightLarge;
    }
    public void setHeightLarge(int heightLarge) {
        this.heightLarge = heightLarge;
    }
}
