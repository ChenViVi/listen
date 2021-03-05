/*
 * Copyright 2018-2019 KunMinX
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yellowzero.listen.player.bean.dto;

import com.yellowzero.listen.player.bean.base.BaseAlbumItem;
import com.yellowzero.listen.player.bean.base.BaseArtistItem;
import com.yellowzero.listen.player.bean.base.BaseMusicItem;

/**
 * provide music info when music changed
 *
 * Create by KunMinX at 18/9/24
 */
public class ChangeMusic<B extends BaseAlbumItem, M extends BaseMusicItem, A extends BaseArtistItem> {

    private String name;
    private String summary;
    private A artist;
    private String albumId;
    private String musicId;
    private String img;
    private String url;
    private String link;

    public ChangeMusic() {
    }

    public ChangeMusic(String name, String summary, String albumId, String musicId, String img, A artist) {
        this.name = name;
        this.summary = summary;
        this.albumId = albumId;
        this.musicId = musicId;
        this.img = img;
        this.artist = artist;
    }

    public ChangeMusic(B musicAlbum, int playIndex) {
        this.name = musicAlbum.getTitle();
        this.summary = musicAlbum.getSummary();
        this.albumId = musicAlbum.getAlbumId();
        this.musicId = ((M) musicAlbum.getMusics().get(playIndex)).getMusicId();
        this.img = musicAlbum.getCoverImg();
        this.artist = (A) musicAlbum.getArtist();
    }

    public void setBaseInfo(B musicAlbum, M music) {
        //要用当前实际播放的列表，因为不同模式存在不同的播放列表
        this.name = music.getName();
        this.summary = musicAlbum.getSummary();
        this.albumId = musicAlbum.getAlbumId();
        this.musicId = music.getMusicId();
        this.img = music.getCover();
        this.artist = (A) music.getArtist();
        this.url = music.getUrl();
        this.link = music.getLink();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getMusicId() {
        return musicId;
    }

    public void setMusicId(String musicId) {
        this.musicId = musicId;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public A getArtist() {
        return artist;
    }

    public void setArtist(A artist) {
        this.artist = artist;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
