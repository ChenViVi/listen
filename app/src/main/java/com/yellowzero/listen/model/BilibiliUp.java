package com.yellowzero.listen.model;

import com.google.gson.annotations.SerializedName;

public class BilibiliUp {

        /**
         * comment : 4199
         * typeid : 31
         * play : 624197
         * pic : //i1.hdslb.com/bfs/archive/166cb88336a8fecc3f07781fe2e4405d5a03fa54.jpg
         * subtitle :
         * description : 无
         * copyright :
         * title : 【黄龄】浴室玩耍时间，你们要的月光全拿走！
         * review : 0
         * author : 黄龄
         * mid : 345630501
         * created : 1607767202
         * length : 05:37
         * video_review : 5678
         * aid : 628115503
         * bvid : BV1dt4y1r7pj
         * hide_click : false
         * is_pay : 0
         * is_union_video : 0
         * is_steins_gate : 0
         */

        private int comment;
        private int typeid;
        private int play;
        private String pic;
        private String subtitle;
        private String description;
        private String copyright;
        private String title;
        private int review;
        private String author;
        private int mid;
        private int created;
        private String length;
        @SerializedName("video_review")
        private int videoReview;
        private int aid;
        private String bvid;
        @SerializedName("hide_click")
        private boolean hideClick;
        @SerializedName("is_pay")
        private int isPay;
        @SerializedName("is_union_video")
        private int isUnionVideo;
        @SerializedName("is_steins_gate")
        private int isSteinsGate;

        public int getComment() {
            return comment;
        }

        public void setComment(int comment) {
            this.comment = comment;
        }

        public int getTypeid() {
            return typeid;
        }

        public void setTypeid(int typeid) {
            this.typeid = typeid;
        }

        public int getPlay() {
            return play;
        }

        public void setPlay(int play) {
            this.play = play;
        }

        public String getPic() {
            return "https:" + pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public String getSubtitle() {
            return subtitle;
        }

        public void setSubtitle(String subtitle) {
            this.subtitle = subtitle;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getCopyright() {
            return copyright;
        }

        public void setCopyright(String copyright) {
            this.copyright = copyright;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getReview() {
            return review;
        }

        public void setReview(int review) {
            this.review = review;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public int getMid() {
            return mid;
        }

        public void setMid(int mid) {
            this.mid = mid;
        }

        public int getCreated() {
            return created;
        }

        public void setCreated(int created) {
            this.created = created;
        }

        public String getLength() {
            return length;
        }

        public void setLength(String length) {
            this.length = length;
        }

        public int getVideoReview() {
            return videoReview;
        }

        public void setVideoReview(int videoReview) {
            this.videoReview = videoReview;
        }

        public int getAid() {
            return aid;
        }

        public void setAid(int aid) {
            this.aid = aid;
        }

        public String getBvid() {
            return bvid;
        }

        public void setBvid(String bvid) {
            this.bvid = bvid;
        }

        public boolean isHideClick() {
            return hideClick;
        }

        public void setHideClick(boolean hideClick) {
            this.hideClick = hideClick;
        }

        public int getIsPay() {
            return isPay;
        }

        public void setIsPay(int isPay) {
            this.isPay = isPay;
        }

        public int getIsUnionVideo() {
            return isUnionVideo;
        }

        public void setIsUnionVideo(int isUnionVideo) {
            this.isUnionVideo = isUnionVideo;
        }

        public int getIsSteinsGate() {
            return isSteinsGate;
        }

        public void setIsSteinsGate(int isSteinsGate) {
            this.isSteinsGate = isSteinsGate;
        }
}
