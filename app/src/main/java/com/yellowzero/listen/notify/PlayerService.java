/*
 * Copyright 2018-present KunMinX
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

package com.yellowzero.listen.notify;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;
import com.yellowzero.listen.R;
import com.yellowzero.listen.activity.MainActivity;
import com.yellowzero.listen.player.DefaultPlayerManager;
import com.yellowzero.listen.player.bean.DefaultAlbum;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.File;

/**
 * Create by KunMinX at 19/7/17
 */
public class PlayerService extends Service {

    public static final String NOTIFY_PREVIOUS = "com.yellowzero.listen.previous";
    public static final String NOTIFY_CLOSE = "com.yellowzero.listen.close";
    public static final String NOTIFY_PAUSE = "com.yellowzero.listen.pause";
    public static final String NOTIFY_PLAY = "com.yellowzero.listen.play";
    public static final String NOTIFY_NEXT = "com.yellowzero.listen.next";
    private static final String GROUP_ID = "group_001";
    private static final String CHANNEL_ID = "channel_001";
    private static final int NOTIFICATION_ID = 5;
    private PlayerCallHelper mPlayerCallHelper;
    private boolean isFirstNotify = true;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("xxxx", "onCreate");
        DefaultAlbum.DefaultMusic results = DefaultPlayerManager.getInstance().getCurrentPlayingMusic();
        if (results != null)
            createNotification(results);
        if (mPlayerCallHelper == null) {
            mPlayerCallHelper = new PlayerCallHelper(new PlayerCallHelper.PlayerCallHelperListener() {
                @Override
                public void playAudio() {
                    DefaultPlayerManager.getInstance().playAudio();
                }

                @Override
                public boolean isPlaying() {
                    return DefaultPlayerManager.getInstance().isPlaying();
                }

                @Override
                public boolean isPaused() {
                    return DefaultPlayerManager.getInstance().isPaused();
                }

                @Override
                public void pauseAudio() {
                    DefaultPlayerManager.getInstance().pauseAudio();
                }
            });
        }
        mPlayerCallHelper.bindCallListener(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        DefaultAlbum.DefaultMusic results = DefaultPlayerManager.getInstance().getCurrentPlayingMusic();
        if (results == null) {
            stopSelf();
            return START_NOT_STICKY;
        }
        if (isFirstNotify) {
            isFirstNotify = false;
            return START_NOT_STICKY;
        }
        Log.e("xxxx", "onStartCommand");
        createNotification(results);
        return START_NOT_STICKY;
    }

    private void createNotification(DefaultAlbum.DefaultMusic music) {
        try {
            String title = music.getName();
            DefaultAlbum album = DefaultPlayerManager.getInstance().getAlbum();
            String summary = album.getSummary();
            RemoteViews expandedView;
            expandedView = new RemoteViews(
                    getApplicationContext().getPackageName(), R.layout.notify_player);

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setAction("showPlayer");
            PendingIntent contentIntent = PendingIntent.getActivity(
                    this, 0, intent, 0);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationManager notificationManager = (NotificationManager)
                        getSystemService(Context.NOTIFICATION_SERVICE);

                NotificationChannelGroup playGroup = new NotificationChannelGroup(GROUP_ID, getString(R.string.play));
                notificationManager.createNotificationChannelGroup(playGroup);

                NotificationChannel playChannel = new NotificationChannel(CHANNEL_ID,
                        getString(R.string.notify_of_play), NotificationManager.IMPORTANCE_LOW);
                playChannel.setGroup(GROUP_ID);
                notificationManager.createNotificationChannel(playChannel);
            }

            Notification notification = new NotificationCompat.Builder(
                    getApplicationContext(), CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_play)
                    .setContentIntent(contentIntent)
                    .setOnlyAlertOnce(true)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setContentTitle(title).build();

            notification.contentView = expandedView;

            setListeners(expandedView);

            notification.contentView.setViewVisibility(R.id.ivNext, View.VISIBLE);
            notification.contentView.setViewVisibility(R.id.ivPrevious, View.VISIBLE);

            boolean isPaused = DefaultPlayerManager.getInstance().isPaused();
            notification.contentView.setImageViewResource(R.id.ivPlay, isPaused ? R.drawable.ic_play : R.drawable.ic_play_pause);

            notification.contentView.setTextViewText(R.id.tvName, title);
            notification.flags |= Notification.FLAG_ONGOING_EVENT;
            String cover = music.getCover();
            if (cover == null
                    || (!cover.startsWith("http") && !new File(cover).exists()))
                notification.contentView.setImageViewResource(R.id.ivCover, R.drawable.ic_holder_square);
            else
                Glide.with(getApplicationContext()) // safer!
                        .asBitmap()
                        .load(music.getCover())
                        .placeholder(R.drawable.ic_holder_square)
                        .error(R.drawable.ic_holder_square)
                        .into(new NotificationTarget(
                                this,
                                R.id.ivCover,
                                notification.contentView,
                                notification,
                                NOTIFICATION_ID));
            startForeground(NOTIFICATION_ID, notification);

            mPlayerCallHelper.bindRemoteController(getApplicationContext());
            mPlayerCallHelper.requestAudioFocus(title, summary);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setListeners(RemoteViews view) {
        try {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                    0, new Intent(NOTIFY_PREVIOUS).setPackage(getPackageName()),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            view.setOnClickPendingIntent(R.id.ivPrevious, pendingIntent);
            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                    0, new Intent(NOTIFY_CLOSE).setPackage(getPackageName()),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            view.setOnClickPendingIntent(R.id.ivClose, pendingIntent);
            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                    0, new Intent(NOTIFY_PAUSE).setPackage(getPackageName()),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            view.setOnClickPendingIntent(R.id.ivPlay, pendingIntent);
            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                    0, new Intent(NOTIFY_NEXT).setPackage(getPackageName()),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            view.setOnClickPendingIntent(R.id.ivNext, pendingIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPlayerCallHelper.unbindCallListener(getApplicationContext());
        mPlayerCallHelper.unbindRemoteController();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
