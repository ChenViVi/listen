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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

import com.yellowzero.listen.player.DefaultPlayerManager;

import java.util.Objects;

public class PlayerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Objects.equals(intent.getAction(), Intent.ACTION_MEDIA_BUTTON)) {
            if (intent.getExtras() == null) {
                return;
            }
            KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
            if (keyEvent == null) {
                return;
            }
            if (keyEvent.getAction() != KeyEvent.ACTION_DOWN) {
                return;
            }

            switch (keyEvent.getKeyCode()) {
                case KeyEvent.KEYCODE_HEADSETHOOK:
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    DefaultPlayerManager.getInstance().togglePlay();
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    DefaultPlayerManager.getInstance().playAudio();
                    break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    DefaultPlayerManager.getInstance().pauseAudio();
                    break;
                case KeyEvent.KEYCODE_MEDIA_STOP:
                    DefaultPlayerManager.getInstance().clear();
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    DefaultPlayerManager.getInstance().playNext();
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    DefaultPlayerManager.getInstance().playPrevious();
                    break;
                default:
            }

        } else {
            if (Objects.requireNonNull(intent.getAction()).equals(PlayerService.NOTIFY_PLAY)) {
                DefaultPlayerManager.getInstance().togglePlay();
            } else if (intent.getAction().equals(PlayerService.NOTIFY_PAUSE)
                    || intent.getAction().equals(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
                DefaultPlayerManager.getInstance().pauseAudio();
            } else if (intent.getAction().equals(PlayerService.NOTIFY_NEXT)) {
                DefaultPlayerManager.getInstance().playNext();
            } else if (intent.getAction().equals(PlayerService.NOTIFY_CLOSE)) {
                DefaultPlayerManager.getInstance().clear();
            } else if (intent.getAction().equals(PlayerService.NOTIFY_PREVIOUS)) {
                DefaultPlayerManager.getInstance().playPrevious();
            }
        }
    }
}
