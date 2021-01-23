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


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

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

        DefaultAlbum.DefaultMusic results = DefaultPlayerManager.getInstance().getCurrentPlayingMusic();
        if (results == null) {
            stopSelf();
            return START_NOT_STICKY;
        }

        mPlayerCallHelper.bindCallListener(getApplicationContext());

        createNotification(results);
        return START_NOT_STICKY;
    }

    private void createNotification(DefaultAlbum.DefaultMusic testMusic) {
        try {
            String title = testMusic.getTitle();
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
                        getString(R.string.notify_of_play), NotificationManager.IMPORTANCE_DEFAULT);
                playChannel.setGroup(GROUP_ID);
                notificationManager.createNotificationChannel(playChannel);
            }

            Notification notification = new NotificationCompat.Builder(
                    getApplicationContext(), CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_play)
                    .setContentIntent(contentIntent)
                    .setOnlyAlertOnce(true)
                    .setContentTitle(title).build();

            notification.contentView = expandedView;

            setListeners(expandedView);

            notification.contentView.setViewVisibility(R.id.player_next, View.VISIBLE);
            notification.contentView.setViewVisibility(R.id.player_previous, View.VISIBLE);

            boolean isPaused = DefaultPlayerManager.getInstance().isPaused();
            notification.contentView.setImageViewResource(R.id.player_pause, isPaused ? R.drawable.ic_play : R.drawable.ic_pause);

            notification.contentView.setTextViewText(R.id.player_song_name, title);
            notification.contentView.setTextViewText(R.id.player_author_name, summary);
            notification.flags |= Notification.FLAG_ONGOING_EVENT;

            notification.contentView.setImageViewResource(R.id.ivCover, R.drawable.ic_pause);
            Glide.with(getApplicationContext()) // safer!
                    .asBitmap()
                    .load(testMusic.getCoverImg())
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
            view.setOnClickPendingIntent(R.id.player_previous, pendingIntent);
            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                    0, new Intent(NOTIFY_CLOSE).setPackage(getPackageName()),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            view.setOnClickPendingIntent(R.id.player_close, pendingIntent);
            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                    0, new Intent(NOTIFY_PAUSE).setPackage(getPackageName()),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            view.setOnClickPendingIntent(R.id.player_pause, pendingIntent);
            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                    0, new Intent(NOTIFY_NEXT).setPackage(getPackageName()),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            view.setOnClickPendingIntent(R.id.player_next, pendingIntent);
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
