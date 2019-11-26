package sourabhkaushik.com.tech.credtask.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.session.PlaybackState;
import android.os.Build;

import sourabhkaushik.com.tech.credtask.R;
import sourabhkaushik.com.tech.credtask.Utils.AppUtils;
import sourabhkaushik.com.tech.credtask.interfaces.MediaPlayerInterfaceInstance;
import sourabhkaushik.com.tech.credtask.view.MainActivity;
import sourabhkaushik.com.tech.credtask.view.PlayMusicActivity;
import sourabhkaushik.com.tech.credtask.viewmodel.PlayMusicViewModel;

/**
 * Created by Sourabh kaushik on 11/7/2019.
 */
public class NotificationBroadcastReceiver extends BroadcastReceiver {
    private static Boolean ispaused = false;


    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (intent != null && intent.getAction() != null) {
                if (intent.getAction().equalsIgnoreCase("pauseSong")) {

                    if (PlayMusicViewModel.isBuffering) {
                        AppUtils.showToast("buffering is in progress");
                        return;
                    }
                    if (!ispaused) {
                        ispaused = true;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            MediaPlayerService.mediaSession.setPlaybackState(new PlaybackState.Builder()
                                    .setState(PlaybackState.STATE_PAUSED, 0, 0)
                                    .setActions(PlaybackState.ACTION_PLAY_PAUSE | PlaybackState.ACTION_PLAY | PlaybackState.ACTION_SKIP_TO_NEXT|PlaybackState.ACTION_SKIP_TO_PREVIOUS)
                                    .build());
                        }
                        if (!PlayMusicActivity.isInForground) {
                            String title = intent.getStringExtra("title");
                            String text = intent.getStringExtra("text");
                            int time = intent.getIntExtra("songPlayed",0);
                            String image = intent.getStringExtra("image");
                            MediaPlayerService.mediaPlayerService.updateNotification(time,title, text, image, BitmapFactory.decodeResource(context.getResources(),
                                    R.drawable.arrow_play));
                        }


                        SingleSongIntentService.getInstance().pauseMusic();
                    } else {
                        ispaused = false;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            MediaPlayerService.mediaSession.setPlaybackState(new PlaybackState.Builder()
                                    .setState(PlaybackState.STATE_PLAYING, 0, 0)
                                    .setActions(PlaybackState.ACTION_PLAY_PAUSE | PlaybackState.ACTION_PLAY | PlaybackState.ACTION_SKIP_TO_NEXT|PlaybackState.ACTION_SKIP_TO_PREVIOUS)
                                    .build());
                        }
                        if (!PlayMusicActivity.isInForground) {
                            String title = intent.getStringExtra("title");
                            String text = intent.getStringExtra("text");
                            int time = intent.getIntExtra("songPlayed",0);
                            String image = intent.getStringExtra("image");
                            MediaPlayerService.mediaPlayerService.updateNotification(time,title, text, image, BitmapFactory.decodeResource(context.getResources(),
                                    R.drawable.arrow_play));
                        }
                        SingleSongIntentService.getInstance().resumeMusic(false);
                    }

                }
                if (intent.getAction().equalsIgnoreCase("nextSong")) {

                    if (PlayMusicActivity.isInForground) {
                        MediaPlayerInterfaceInstance.getInstance().getMpinterface().nextMusic();
                    } else {
                        int position = Integer.parseInt(intent.getStringExtra("positionSong"));


                        if (position < MediaPlayerService.albumList.size() - 1) {
                            ++position;
                            String title = MediaPlayerService.albumList.get(position).getTitle();
                            String text = MediaPlayerService.albumList.get(position).getDescription();
                            String image = MediaPlayerService.albumList.get(position).getImage();
                            int time = 0;
                            MediaPlayerService.mediaPlayerService.playSongAtPosition(position);
                            MediaPlayerService.mediaPlayerService.updateNotification(time,title, text, image, BitmapFactory.decodeResource(context.getResources(),
                                    R.drawable.arrow_play));
                        }


                    }

                }
                if (intent.getAction().equalsIgnoreCase("prevSong")) {
                    if (PlayMusicActivity.isInForground) {
                        MediaPlayerInterfaceInstance.getInstance().getMpinterface().prevMusic();
                    } else {
                        int position = Integer.parseInt(intent.getStringExtra("positionSong"));
                        if (position > 0) {
                            --position;
                            String title = MediaPlayerService.albumList.get(position).getTitle();
                            String text = MediaPlayerService.albumList.get(position).getDescription();
                            String image = MediaPlayerService.albumList.get(position).getImage();
                            int time = 0;
                            MediaPlayerService.mediaPlayerService.playSongAtPosition(position);
                            MediaPlayerService.mediaPlayerService.updateNotification(time,title, text, image, BitmapFactory.decodeResource(context.getResources(),
                                    R.drawable.arrow_play));
                        }

                    }
                }

                if (intent.getAction().equalsIgnoreCase("closeService")) {
                    MediaPlayerService.mediaPlayerService.stopForeground(true);
                    MediaPlayerService.mediaPlayerService.stopSelf();

                    MediaPlayerService.mediaPlayerService.onDestroy();
                    System.exit(0);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
