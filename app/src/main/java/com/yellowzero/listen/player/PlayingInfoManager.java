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

package com.yellowzero.listen.player;

import com.yellowzero.listen.AppData;
import com.yellowzero.listen.player.bean.base.BaseAlbumItem;
import com.yellowzero.listen.player.bean.base.BaseMusicItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Create by KunMinX at 18/9/24
 */
public class PlayingInfoManager<B extends BaseAlbumItem, M extends BaseMusicItem> {

    //index of current playing which maybe Shuffled
    private int mPlayIndex = 0;

    //index of current playing which user see in the list
    private int mAlbumIndex = 0;

    public static final int MODE_LIST_CYCLE = 0;
    public static final int MODE_SINGLE_CYCLE = 1;
    public static final int MODE_RANDOM = 2;

    //原始列表
    private List<M> mOriginPlayingList = new ArrayList<>();

    //随机播放列表
    private List<M> mShufflePlayingList = new ArrayList<>();

    //专辑详情
    private B mMusicAlbum;

    boolean isInit() {
        return mMusicAlbum != null;
    }

    private void fitShuffle() {
        mShufflePlayingList.clear();
        mShufflePlayingList.addAll(mOriginPlayingList);
        Collections.shuffle(mShufflePlayingList);
    }

    public int changeMode() {
        if (AppData.MUSIC_REPEAT_MODE == MODE_LIST_CYCLE) {
            AppData.MUSIC_REPEAT_MODE = MODE_SINGLE_CYCLE;
        } else if (AppData.MUSIC_REPEAT_MODE == MODE_SINGLE_CYCLE) {
            AppData.MUSIC_REPEAT_MODE = MODE_RANDOM;
        } else {
            AppData.MUSIC_REPEAT_MODE = MODE_LIST_CYCLE;
        }
        return AppData.MUSIC_REPEAT_MODE;
    }

    public B getMusicAlbum() {
        return mMusicAlbum;
    }

    public void setMusicAlbum(B musicAlbum) {
        this.mMusicAlbum = musicAlbum;
        mOriginPlayingList.clear();
        mOriginPlayingList.addAll(mMusicAlbum.getMusics());
        fitShuffle();
    }

    public List<M> getPlayingList() {
        if (AppData.MUSIC_REPEAT_MODE == MODE_RANDOM) {
            return mShufflePlayingList;
        } else {
            return mOriginPlayingList;
        }
    }

    public List<M> getOriginPlayingList() {
        return mOriginPlayingList;
    }

    public M getCurrentPlayingMusic() {
        if (getPlayingList().isEmpty()) {
            return null;
        }
        return getPlayingList().get(mPlayIndex);
    }

    public int getRepeatMode() {
        return AppData.MUSIC_REPEAT_MODE;
    }

    public void countPreviousIndex() {
        if (mPlayIndex == 0) {
            mPlayIndex = (getPlayingList().size() - 1);
        } else {
            --mPlayIndex;
        }
        mAlbumIndex = mOriginPlayingList.indexOf(getCurrentPlayingMusic());
    }

    public void countNextIndex() {
        if (mPlayIndex == (getPlayingList().size() - 1)) {
            mPlayIndex = 0;
        } else {
            ++mPlayIndex;
        }
        mAlbumIndex = mOriginPlayingList.indexOf(getCurrentPlayingMusic());
    }

    public int getAlbumIndex() {
        return mAlbumIndex;
    }

    public void setAlbumIndex(int albumIndex) {
        mAlbumIndex = albumIndex;
        mPlayIndex = getPlayingList().indexOf(mOriginPlayingList.get(mAlbumIndex));
    }
}
