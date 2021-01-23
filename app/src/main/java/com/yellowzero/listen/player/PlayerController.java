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

import android.content.Context;
import android.text.TextUtils;

import com.danikula.videocache.HttpProxyCacheServer;
import com.yellowzero.listen.App;
import com.yellowzero.listen.AppData;
import com.yellowzero.listen.player.bean.base.BaseAlbumItem;
import com.yellowzero.listen.player.bean.base.BaseMusicItem;
import com.yellowzero.listen.player.bean.dto.ChangeMusic;
import com.yellowzero.listen.player.bean.dto.PlayingMusic;
import com.yellowzero.listen.player.contract.IServiceNotifier;
import com.yellowzero.listen.player.helper.MediaPlayerHelper;
import com.yellowzero.listen.player.helper.PlayerFileNameGenerator;
import com.yellowzero.listen.util.NetworkUtil;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

/**
 * Create by KunMinX at 18/9/25
 */
public class PlayerController<B extends BaseAlbumItem, M extends BaseMusicItem> {

    protected PlayingInfoManager<B, M> mPlayingInfoManager = new PlayingInfoManager<>();
    protected boolean mIsPaused;
    protected boolean mIsChangingPlayingMusic;

    protected HttpProxyCacheServer proxy;

    private final MutableLiveData<ChangeMusic> changeMusicLiveData = new MutableLiveData<>();
    private final MutableLiveData<PlayingMusic> playingMusicLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> pauseLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> playModeLiveData = new MutableLiveData<>();

    private IServiceNotifier mIServiceNotifier;

    private PlayingMusic mCurrentPlay = new PlayingMusic("00:00", "00:00");
    private ChangeMusic mChangeMusic = new ChangeMusic();

    public void init(Context context, List<String> extraFormatList, IServiceNotifier iServiceNotifier) {
        proxy = App.getProxy(context);
        proxy = new HttpProxyCacheServer.Builder(context.getApplicationContext())
                .fileNameGenerator(new PlayerFileNameGenerator())
                .maxCacheSize(2147483648L) // 2GB
                .build();


        mIServiceNotifier = iServiceNotifier;

        MediaPlayerHelper.getInstance().initAssetManager(context);

        if (extraFormatList != null) {
            MediaPlayerHelper.getInstance().getFormatList().addAll(extraFormatList);
        }
    }

    public boolean isInit() {
        return mPlayingInfoManager.isInit();
    }

    public void loadAlbum(B musicAlbum) {
        setAlbum(musicAlbum, 0);
    }

    private void setAlbum(B musicAlbum, int albumIndex) {
        mPlayingInfoManager.setMusicAlbum(musicAlbum);
        mPlayingInfoManager.setAlbumIndex(albumIndex);
        setChangingPlayingMusic(true);
    }

    public void loadAlbum(B musicAlbum, int albumIndex) {
        setAlbum(musicAlbum, albumIndex);
        playAudio();
    }

    public boolean isPlaying() {
        return MediaPlayerHelper.getInstance().getMediaPlayer().isPlaying();
    }

    public boolean isPaused() {
        return mIsPaused;
    }

    /**
     * @param albumIndex 从 album 进来的一定是 album 列表的 index
     */
    public void playAudio(int albumIndex) {
        if (isPlaying() && albumIndex == mPlayingInfoManager.getAlbumIndex()) {
            return;
        }

        mPlayingInfoManager.setAlbumIndex(albumIndex);
        setChangingPlayingMusic(true);
        playAudio();
    }


    public void playAudio() {
        if (mIsChangingPlayingMusic) {
            MediaPlayerHelper.getInstance().getMediaPlayer().stop();
            getUrlAndPlay();
        } else if (mIsPaused) {
            resumeAudio();
        }
    }

    private void getUrlAndPlay() {
        String url = null;
        M freeMusic = null;
        freeMusic = mPlayingInfoManager.getCurrentPlayingMusic();
        url = freeMusic.getUrl();

        if (TextUtils.isEmpty(url)) {
            pauseAudio();
        } else {
            if ((url.contains("http:") || url.contains("ftp:") || url.contains("https:"))) {
                int networkState = NetworkUtil.CURRENT_STATE;
                if (networkState == NetworkUtil.STATE_WIFI ||
                        (proxy.isCached(url)) ||
                        (networkState == NetworkUtil.STATE_MOBILE && AppData.ENABLE_MUSIC_MOBILE)) {
                    MediaPlayerHelper.getInstance().play(proxy.getProxyUrl(url));
                    afterPlay();
                } else {
                    if (getRepeatMode() == PlayingInfoManager.MODE_LIST_CYCLE)
                        pauseAudio();
                    else
                        playNext();
                }
            } else if (url.contains("storage")) {
                MediaPlayerHelper.getInstance().play(url);
                afterPlay();
            } else {
                MediaPlayerHelper.getInstance().playAsset(url);
                afterPlay();
            }
        }
    }

    protected void afterPlay() {
        setChangingPlayingMusic(false);
        bindProgressListener();
        mIsPaused = false;
        pauseLiveData.setValue(false);
        if (mIServiceNotifier != null) {
            mIServiceNotifier.notifyService(true);
        }
    }

    private void bindProgressListener() {
        MediaPlayerHelper.getInstance().setProgressInterval(1000).setMediaPlayerHelperCallBack(
                (state, mediaPlayerHelper, args) -> {
                    if (state == MediaPlayerHelper.CallBackState.PROGRESS) {
                        int position = mediaPlayerHelper.getMediaPlayer().getCurrentPosition();
                        int duration = mediaPlayerHelper.getMediaPlayer().getDuration();
                        mCurrentPlay.setNowTime(calculateTime(position / 1000));
                        mCurrentPlay.setAllTime(calculateTime(duration / 1000));
                        mCurrentPlay.setDuration(duration);
                        mCurrentPlay.setPlayerPosition(position);
                        playingMusicLiveData.setValue(mCurrentPlay);
                        if (mCurrentPlay.getAllTime().equals(mCurrentPlay.getNowTime())
                                //容许两秒内的误差，有的内容它就是会差那么 1 秒
                                || duration / 1000 - position / 1000 < 2) {
                            if (getRepeatMode() == PlayingInfoManager.MODE_SINGLE_CYCLE) {
                                playAgain();
                            } else {
                                playNext();
                            }
                        }
                    }
                });
    }

    public void requestLastPlayingInfo() {
        playingMusicLiveData.setValue(mCurrentPlay);
        changeMusicLiveData.setValue(mChangeMusic);
        pauseLiveData.setValue(mIsPaused);
    }

    public void setSeek(int progress) {
        MediaPlayerHelper.getInstance().getMediaPlayer().seekTo(progress);
    }

    public String getTrackTime(int progress) {
        return calculateTime(progress / 1000);
    }

    private String calculateTime(int time) {
        int minute;
        int second;
        if (time >= 60) {
            minute = time / 60;
            second = time % 60;
            return (minute < 10 ? "0" + minute : "" + minute) + (second < 10 ? ":0" + second : ":" + second);
        } else {
            second = time;
            if (second < 10) {
                return "00:0" + second;
            }
            return "00:" + second;
        }
    }


    public void playNext() {
        mPlayingInfoManager.countNextIndex();
        setChangingPlayingMusic(true);
        playAudio();
    }


    public void playPrevious() {
        mPlayingInfoManager.countPreviousIndex();
        setChangingPlayingMusic(true);
        playAudio();
    }


    public void playAgain() {
        setChangingPlayingMusic(true);
        playAudio();
    }


    public void pauseAudio() {
        MediaPlayerHelper.getInstance().getMediaPlayer().pause();
        mIsPaused = true;
        pauseLiveData.setValue(true);
        if (mIServiceNotifier != null) {
            mIServiceNotifier.notifyService(true);
        }
    }


    public void resumeAudio() {
        MediaPlayerHelper.getInstance().getMediaPlayer().start();
        mIsPaused = false;
        pauseLiveData.setValue(false);
        if (mIServiceNotifier != null) {
            mIServiceNotifier.notifyService(true);
        }
    }


    public void clear() {
        MediaPlayerHelper.getInstance().getMediaPlayer().stop();
        MediaPlayerHelper.getInstance().getMediaPlayer().reset();
        pauseLiveData.setValue(true);
        //这里设为true是因为可能通知栏清除后，还可能在页面中点击播放
        resetIsChangingPlayingChapter();
        MediaPlayerHelper.getInstance().setProgressInterval(1000).setMediaPlayerHelperCallBack(null);
        if (mIServiceNotifier != null) {
            mIServiceNotifier.notifyService(false);
        }
    }

    public void resetIsChangingPlayingChapter() {
        mIsChangingPlayingMusic = true;
        setChangingPlayingMusic(true);
    }

    public void changeMode() {
        playModeLiveData.setValue(mPlayingInfoManager.changeMode());
    }

    public B getAlbum() {
        return mPlayingInfoManager.getMusicAlbum();
    }

    //播放列表展示用
    public List<M> getAlbumMusics() {
        return mPlayingInfoManager.getOriginPlayingList();
    }

    public void setChangingPlayingMusic(boolean changingPlayingMusic) {
        mIsChangingPlayingMusic = changingPlayingMusic;
        if (mIsChangingPlayingMusic) {
            mChangeMusic.setBaseInfo(mPlayingInfoManager.getMusicAlbum(), getCurrentPlayingMusic());
            changeMusicLiveData.setValue(mChangeMusic);
            mCurrentPlay.setBaseInfo(mPlayingInfoManager.getMusicAlbum(), getCurrentPlayingMusic());
        }
    }

    public int getAlbumIndex() {
        return mPlayingInfoManager.getAlbumIndex();
    }

    public LiveData<ChangeMusic> getChangeMusicLiveData() {
        return changeMusicLiveData;
    }

    public LiveData<PlayingMusic> getPlayingMusicLiveData() {
        return playingMusicLiveData;
    }

    public LiveData<Boolean> getPauseLiveData() {
        return pauseLiveData;
    }

    public LiveData<Integer> getPlayModeLiveData() {
        return playModeLiveData;
    }

    public int getRepeatMode() {
        return mPlayingInfoManager.getRepeatMode();
    }

    public void togglePlay() {
        if (isPlaying()) {
            pauseAudio();
        } else {
            playAudio();
        }
    }

    public M getCurrentPlayingMusic() {
        return mPlayingInfoManager.getCurrentPlayingMusic();
    }
}
