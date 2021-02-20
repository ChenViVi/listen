package com.yellowzero.listen.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BilibiliFav {

    /**
     * id : 374145799
     * offset : 1
     * index : 1
     * intro : 无
     * attr : 0
     * tid : 137
     * copy_right : 1
     * cnt_info : {"collect":1655,"play":151924,"thumb_up":25032,"thumb_down":0,"share":320,"reply":2154,"danmaku":2747,"coin":7478}
     * cover : http://i2.hdslb.com/bfs/archive/deacfae213651a479a1119e6a5c51443fae3ef30.jpg
     * duration : 292
     * pubtime : 1613561110
     * like_state : 1
     * fav_state : 0
     * page : 1
     * pages : [{"id":298779304,"title":"【黄龄】浴室晚上时间，新春晚会第二弹来啦！","intro":"","duration":292,"link":"","page":1,"metas":[{"quality":15,"size":18629},{"quality":16,"size":18629},{"quality":32,"size":41274},{"quality":48,"size":53319},{"quality":64,"size":88008},{"quality":74,"size":128158},{"quality":80,"size":128158},{"quality":112,"size":248608},{"quality":116,"size":248608},{"quality":66,"size":73073}],"from":"vupload","dimension":{"width":1920,"height":1080,"rotate":0}}]
     * title : 【黄龄】浴室晚上时间，新春晚会第二弹来啦！
     * type : 2
     * upper : {"mid":345630501,"name":"黄龄","face":"http://i0.hdslb.com/bfs/face/389be742ba9e3fc79603eee582b7c1e3811e1935.jpg","followed":1,"fans":0,"vip_type":2,"vip_statue":1,"vip_due_date":1615564800000,"vip_pay_type":0,"official_role":0,"official_title":"","official_desc":""}
     * link : bilibili://video/374145799
     * bv_id : BV1to4y197DK
     * short_link : https://b23.tv/BV1to4y197DK
     * rights : {"bp":0,"elec":0,"download":0,"movie":0,"pay":0,"ugc_pay":0,"hd5":1,"no_reprint":1,"autoplay":1,"no_background":0}
     * elec_info : null
     * coin : {"max_num":2,"coin_number":0}
     */

    @SerializedName("id")
    private Integer id;
    @SerializedName("offset")
    private Integer offset;
    @SerializedName("index")
    private Integer index;
    @SerializedName("intro")
    private String intro;
    @SerializedName("attr")
    private Integer attr;
    @SerializedName("tid")
    private Integer tid;
    @SerializedName("copy_right")
    private Integer copyRight;
    @SerializedName("cnt_info")
    private CntInfoDTO cntInfo;
    @SerializedName("cover")
    private String cover;
    @SerializedName("duration")
    private Integer duration;
    @SerializedName("pubtime")
    private Long pubtime;
    @SerializedName("like_state")
    private Integer likeState;
    @SerializedName("fav_state")
    private Integer favState;
    @SerializedName("page")
    private Integer page;
    @SerializedName("pages")
    private List<PagesDTO> pages;
    @SerializedName("title")
    private String title;
    @SerializedName("type")
    private Integer type;
    @SerializedName("upper")
    private UpperDTO upper;
    @SerializedName("link")
    private String link;
    @SerializedName("bv_id")
    private String bvId;
    @SerializedName("short_link")
    private String shortLink;
    @SerializedName("rights")
    private RightsDTO rights;
    @SerializedName("elec_info")
    private Object elecInfo;
    @SerializedName("coin")
    private CoinDTO coin;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public Integer getAttr() {
        return attr;
    }

    public void setAttr(Integer attr) {
        this.attr = attr;
    }

    public Integer getTid() {
        return tid;
    }

    public void setTid(Integer tid) {
        this.tid = tid;
    }

    public Integer getCopyRight() {
        return copyRight;
    }

    public void setCopyRight(Integer copyRight) {
        this.copyRight = copyRight;
    }

    public CntInfoDTO getCntInfo() {
        return cntInfo;
    }

    public void setCntInfo(CntInfoDTO cntInfo) {
        this.cntInfo = cntInfo;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Long getPubtime() {
        return pubtime;
    }

    public void setPubtime(Long pubtime) {
        this.pubtime = pubtime;
    }

    public Integer getLikeState() {
        return likeState;
    }

    public void setLikeState(Integer likeState) {
        this.likeState = likeState;
    }

    public Integer getFavState() {
        return favState;
    }

    public void setFavState(Integer favState) {
        this.favState = favState;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public List<PagesDTO> getPages() {
        return pages;
    }

    public void setPages(List<PagesDTO> pages) {
        this.pages = pages;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public UpperDTO getUpper() {
        return upper;
    }

    public void setUpper(UpperDTO upper) {
        this.upper = upper;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getBvId() {
        return bvId;
    }

    public void setBvId(String bvId) {
        this.bvId = bvId;
    }

    public String getShortLink() {
        return shortLink;
    }

    public void setShortLink(String shortLink) {
        this.shortLink = shortLink;
    }

    public RightsDTO getRights() {
        return rights;
    }

    public void setRights(RightsDTO rights) {
        this.rights = rights;
    }

    public Object getElecInfo() {
        return elecInfo;
    }

    public void setElecInfo(Object elecInfo) {
        this.elecInfo = elecInfo;
    }

    public CoinDTO getCoin() {
        return coin;
    }

    public void setCoin(CoinDTO coin) {
        this.coin = coin;
    }

    public static class CntInfoDTO {
        /**
         * collect : 1655
         * play : 151924
         * thumb_up : 25032
         * thumb_down : 0
         * share : 320
         * reply : 2154
         * danmaku : 2747
         * coin : 7478
         */

        @SerializedName("collect")
        private Integer collect;
        @SerializedName("play")
        private Integer play;
        @SerializedName("thumb_up")
        private Integer thumbUp;
        @SerializedName("thumb_down")
        private Integer thumbDown;
        @SerializedName("share")
        private Integer share;
        @SerializedName("reply")
        private Integer reply;
        @SerializedName("danmaku")
        private Integer danmaku;
        @SerializedName("coin")
        private Integer coin;

        public Integer getCollect() {
            return collect;
        }

        public void setCollect(Integer collect) {
            this.collect = collect;
        }

        public Integer getPlay() {
            return play;
        }

        public void setPlay(Integer play) {
            this.play = play;
        }

        public Integer getThumbUp() {
            return thumbUp;
        }

        public void setThumbUp(Integer thumbUp) {
            this.thumbUp = thumbUp;
        }

        public Integer getThumbDown() {
            return thumbDown;
        }

        public void setThumbDown(Integer thumbDown) {
            this.thumbDown = thumbDown;
        }

        public Integer getShare() {
            return share;
        }

        public void setShare(Integer share) {
            this.share = share;
        }

        public Integer getReply() {
            return reply;
        }

        public void setReply(Integer reply) {
            this.reply = reply;
        }

        public Integer getDanmaku() {
            return danmaku;
        }

        public void setDanmaku(Integer danmaku) {
            this.danmaku = danmaku;
        }

        public Integer getCoin() {
            return coin;
        }

        public void setCoin(Integer coin) {
            this.coin = coin;
        }
    }

    public static class UpperDTO {
        /**
         * mid : 345630501
         * name : 黄龄
         * face : http://i0.hdslb.com/bfs/face/389be742ba9e3fc79603eee582b7c1e3811e1935.jpg
         * followed : 1
         * fans : 0
         * vip_type : 2
         * vip_statue : 1
         * vip_due_date : 1615564800000
         * vip_pay_type : 0
         * official_role : 0
         * official_title : 
         * official_desc : 
         */

        @SerializedName("mid")
        private Integer mid;
        @SerializedName("name")
        private String name;
        @SerializedName("face")
        private String face;
        @SerializedName("followed")
        private Integer followed;
        @SerializedName("fans")
        private Integer fans;
        @SerializedName("vip_type")
        private Integer vipType;
        @SerializedName("vip_statue")
        private Integer vipStatue;
        @SerializedName("vip_due_date")
        private Long vipDueDate;
        @SerializedName("vip_pay_type")
        private Integer vipPayType;
        @SerializedName("official_role")
        private Integer officialRole;
        @SerializedName("official_title")
        private String officialTitle;
        @SerializedName("official_desc")
        private String officialDesc;

        public Integer getMid() {
            return mid;
        }

        public void setMid(Integer mid) {
            this.mid = mid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFace() {
            return face;
        }

        public void setFace(String face) {
            this.face = face;
        }

        public Integer getFollowed() {
            return followed;
        }

        public void setFollowed(Integer followed) {
            this.followed = followed;
        }

        public Integer getFans() {
            return fans;
        }

        public void setFans(Integer fans) {
            this.fans = fans;
        }

        public Integer getVipType() {
            return vipType;
        }

        public void setVipType(Integer vipType) {
            this.vipType = vipType;
        }

        public Integer getVipStatue() {
            return vipStatue;
        }

        public void setVipStatue(Integer vipStatue) {
            this.vipStatue = vipStatue;
        }

        public Long getVipDueDate() {
            return vipDueDate;
        }

        public void setVipDueDate(Long vipDueDate) {
            this.vipDueDate = vipDueDate;
        }

        public Integer getVipPayType() {
            return vipPayType;
        }

        public void setVipPayType(Integer vipPayType) {
            this.vipPayType = vipPayType;
        }

        public Integer getOfficialRole() {
            return officialRole;
        }

        public void setOfficialRole(Integer officialRole) {
            this.officialRole = officialRole;
        }

        public String getOfficialTitle() {
            return officialTitle;
        }

        public void setOfficialTitle(String officialTitle) {
            this.officialTitle = officialTitle;
        }

        public String getOfficialDesc() {
            return officialDesc;
        }

        public void setOfficialDesc(String officialDesc) {
            this.officialDesc = officialDesc;
        }
    }

    public static class RightsDTO {
        /**
         * bp : 0
         * elec : 0
         * download : 0
         * movie : 0
         * pay : 0
         * ugc_pay : 0
         * hd5 : 1
         * no_reprint : 1
         * autoplay : 1
         * no_background : 0
         */

        @SerializedName("bp")
        private Integer bp;
        @SerializedName("elec")
        private Integer elec;
        @SerializedName("download")
        private Integer download;
        @SerializedName("movie")
        private Integer movie;
        @SerializedName("pay")
        private Integer pay;
        @SerializedName("ugc_pay")
        private Integer ugcPay;
        @SerializedName("hd5")
        private Integer hd5;
        @SerializedName("no_reprint")
        private Integer noReprint;
        @SerializedName("autoplay")
        private Integer autoplay;
        @SerializedName("no_background")
        private Integer noBackground;

        public Integer getBp() {
            return bp;
        }

        public void setBp(Integer bp) {
            this.bp = bp;
        }

        public Integer getElec() {
            return elec;
        }

        public void setElec(Integer elec) {
            this.elec = elec;
        }

        public Integer getDownload() {
            return download;
        }

        public void setDownload(Integer download) {
            this.download = download;
        }

        public Integer getMovie() {
            return movie;
        }

        public void setMovie(Integer movie) {
            this.movie = movie;
        }

        public Integer getPay() {
            return pay;
        }

        public void setPay(Integer pay) {
            this.pay = pay;
        }

        public Integer getUgcPay() {
            return ugcPay;
        }

        public void setUgcPay(Integer ugcPay) {
            this.ugcPay = ugcPay;
        }

        public Integer getHd5() {
            return hd5;
        }

        public void setHd5(Integer hd5) {
            this.hd5 = hd5;
        }

        public Integer getNoReprint() {
            return noReprint;
        }

        public void setNoReprint(Integer noReprint) {
            this.noReprint = noReprint;
        }

        public Integer getAutoplay() {
            return autoplay;
        }

        public void setAutoplay(Integer autoplay) {
            this.autoplay = autoplay;
        }

        public Integer getNoBackground() {
            return noBackground;
        }

        public void setNoBackground(Integer noBackground) {
            this.noBackground = noBackground;
        }
    }

    public static class CoinDTO {
        /**
         * max_num : 2
         * coin_number : 0
         */

        @SerializedName("max_num")
        private Integer maxNum;
        @SerializedName("coin_number")
        private Integer coinNumber;

        public Integer getMaxNum() {
            return maxNum;
        }

        public void setMaxNum(Integer maxNum) {
            this.maxNum = maxNum;
        }

        public Integer getCoinNumber() {
            return coinNumber;
        }

        public void setCoinNumber(Integer coinNumber) {
            this.coinNumber = coinNumber;
        }
    }

    public static class PagesDTO {
        /**
         * id : 298779304
         * title : 【黄龄】浴室晚上时间，新春晚会第二弹来啦！
         * intro : 
         * duration : 292
         * link : 
         * page : 1
         * metas : [{"quality":15,"size":18629},{"quality":16,"size":18629},{"quality":32,"size":41274},{"quality":48,"size":53319},{"quality":64,"size":88008},{"quality":74,"size":128158},{"quality":80,"size":128158},{"quality":112,"size":248608},{"quality":116,"size":248608},{"quality":66,"size":73073}]
         * from : vupload
         * dimension : {"width":1920,"height":1080,"rotate":0}
         */

        @SerializedName("id")
        private Long id;
        @SerializedName("title")
        private String title;
        @SerializedName("intro")
        private String intro;
        @SerializedName("duration")
        private Integer duration;
        @SerializedName("link")
        private String link;
        @SerializedName("page")
        private Integer page;
        @SerializedName("metas")
        private List<MetasDTO> metas;
        @SerializedName("from")
        private String from;
        @SerializedName("dimension")
        private PagesDTO.DimensionDTO dimension;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getIntro() {
            return intro;
        }

        public void setIntro(String intro) {
            this.intro = intro;
        }

        public Integer getDuration() {
            return duration;
        }

        public void setDuration(Integer duration) {
            this.duration = duration;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public Integer getPage() {
            return page;
        }

        public void setPage(Integer page) {
            this.page = page;
        }

        public List<MetasDTO> getMetas() {
            return metas;
        }

        public void setMetas(List<MetasDTO> metas) {
            this.metas = metas;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public DimensionDTO getDimension() {
            return dimension;
        }

        public void setDimension(DimensionDTO dimension) {
            this.dimension = dimension;
        }

        public static class DimensionDTO {
            /**
             * width : 1920
             * height : 1080
             * rotate : 0
             */

            @SerializedName("width")
            private Integer width;
            @SerializedName("height")
            private Integer height;
            @SerializedName("rotate")
            private Integer rotate;

            public Integer getWidth() {
                return width;
            }

            public void setWidth(Integer width) {
                this.width = width;
            }

            public Integer getHeight() {
                return height;
            }

            public void setHeight(Integer height) {
                this.height = height;
            }

            public Integer getRotate() {
                return rotate;
            }

            public void setRotate(Integer rotate) {
                this.rotate = rotate;
            }
        }

        public static class MetasDTO {
            /**
             * quality : 15
             * size : 18629
             */

            @SerializedName("quality")
            private Integer quality;
            @SerializedName("size")
            private Integer size;

            public Integer getQuality() {
                return quality;
            }

            public void setQuality(Integer quality) {
                this.quality = quality;
            }

            public Integer getSize() {
                return size;
            }

            public void setSize(Integer size) {
                this.size = size;
            }
        }
    }
}
