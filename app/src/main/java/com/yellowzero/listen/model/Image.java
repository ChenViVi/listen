package com.yellowzero.listen.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Image implements Serializable {
    private int id;
    private String pid;
    private long weiboId;
    private ImageInfo imageInfoSmall;
    private ImageInfo imageInfoLarge;
    private UserWeibo user;
    private List<ImageTag> tags;
    private String text;
    private int viewCount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public long getWeiboId() {
        return weiboId;
    }

    public void setWeiboId(long weiboId) {
        this.weiboId = weiboId;
    }

    public ImageInfo getImageInfoSmall() {
        return imageInfoSmall;
    }

    public void setImageInfoSmall(ImageInfo imageInfoSmall) {
        this.imageInfoSmall = imageInfoSmall;
    }

    public ImageInfo getImageInfoLarge() {
        return imageInfoLarge;
    }

    public void setImageInfoLarge(ImageInfo imageInfoLarge) {
        this.imageInfoLarge = imageInfoLarge;
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

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public ArrayList<String> getTagList() {
        if (tags == null)
            return null;
        ArrayList<String> tagList = new ArrayList<>();
        for (ImageTag imageTag : tags)
            tagList.add(imageTag.getName());
        return tagList;
    }

    public String getImageName() {
        String suffix = getSuffix();
        if (suffix == null)
            return null;
        return pid + "." + suffix;
    }

    public String getSuffix() {
        if (imageInfoSmall == null || imageInfoSmall.getUrl() == null)
            return null;
        int index = imageInfoSmall.getUrl().lastIndexOf(".");
        if (index == -1) {
            return null;
        } else
            return imageInfoSmall.getUrl().substring(index + 1).toLowerCase();
    }

    public boolean isGif() {
        String suffix = getSuffix();
        if (suffix == null)
            return false;
        return suffix.equals("gif");
    }
}
