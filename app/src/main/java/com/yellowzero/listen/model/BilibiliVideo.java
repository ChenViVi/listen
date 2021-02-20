package com.yellowzero.listen.model;

import org.jetbrains.annotations.NotNull;

public class BilibiliVideo {

    private static final String urlPrefix = "https://www.bilibili.com/video/";

    private String title;
    private String cover;
    private String name;
    private String avatar;
    private String url;
    private int playCount;
    private int danmkuCount;

    public BilibiliVideo(@NotNull BilibiliUp bilibiliUp) {
        this.title = bilibiliUp.getTitle();
        this.cover = bilibiliUp.getPic();
        this.name = bilibiliUp.getAuthor();
        this.avatar = null;
        this.url = urlPrefix + bilibiliUp.getBvid();
        this.playCount = bilibiliUp.getPlay();
        this.danmkuCount = bilibiliUp.getVideoReview();
    }

    public BilibiliVideo(@NotNull BilibiliFav bilibiliFav) {
        if (bilibiliFav.getPages() != null && bilibiliFav.getPages().size() > 0)
            this.title = bilibiliFav.getPages().get(0).getTitle();
        else
            title = "";
        this.cover = bilibiliFav.getCover();
        this.name = bilibiliFav.getUpper().getName();
        this.avatar = bilibiliFav.getUpper().getFace();
        this.url = urlPrefix + bilibiliFav.getBvId();
        this.playCount = bilibiliFav.getCntInfo().getPlay();
        this.danmkuCount = bilibiliFav.getCntInfo().getDanmaku();
    }

    public static String getUrlPrefix() {
        return urlPrefix;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPlayCount() {
        return playCount;
    }

    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }

    public int getDanmkuCount() {
        return danmkuCount;
    }

    public void setDanmkuCount(int danmkuCount) {
        this.danmkuCount = danmkuCount;
    }
}
