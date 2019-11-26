package sourabhkaushik.com.tech.credtask.services;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.TimedText;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import sourabhkaushik.com.tech.credtask.MainApplication;
import sourabhkaushik.com.tech.credtask.R;
import sourabhkaushik.com.tech.credtask.Utils.AppUtils;
import sourabhkaushik.com.tech.credtask.interfaces.MediaPlayerInterfaceInstance;
import sourabhkaushik.com.tech.credtask.view.PlayMusicActivity;
import sourabhkaushik.com.tech.credtask.viewmodel.PlayMusicViewModel;

/**
 * Created by Sourabh kaushik on 11/6/2019.
 */
public class SingleSongIntentService implements MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnInfoListener,
        MediaPlayer.OnTimedTextListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    private static SingleSongIntentService singleSongIntentService;
    private Runnable runnable;
    private int positionPlaying;
    private Handler handler;
    private boolean isHandlerAttached = false;
    private MediaPlayer mediaPlayer;
    public int songLength = 1;
    public int time = 1;
    private int seekPostion = 0;
    private boolean isCompletionOnError = false;
    private int bufferedPercentage = 0;

    public static SingleSongIntentService getInstance() {
        if (singleSongIntentService == null) {
            singleSongIntentService = new SingleSongIntentService();
        }
        return singleSongIntentService;
    }


    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    private SingleSongIntentService() {

        mediaPlayer = new MediaPlayer();
        handler = new Handler();
        runnable = new Runnable() {

            @Override
            public void run() {
                try {
                    if(time<=songLength&&mediaPlayer.isPlaying()){
                        time += 1000;

                            MediaPlayerInterfaceInstance.getInstance().getMpinterface().songPlayed(mediaPlayer, time);


                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    handler.postDelayed(this, 1000);
                }
            }
        };


    }

    void playSong(int position, final String songUrl) {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
            }
        }
        if (handler != null
                &&
                runnable != null
                && isHandlerAttached) {
            handler.removeCallbacks(runnable);
            isHandlerAttached = false;
        }
        positionPlaying = position;
        new Thread(new Runnable() {
            @Override
            public void run() {
                mediaPlayer = new MediaPlayer();
                time = 1;
                mediaPlayer.setOnPreparedListener(SingleSongIntentService.getInstance());
                try {
                    mediaPlayer.setDataSource(songUrl); // setup song from https://www.hrupin.com/wp-content/uploads/mp3/testsong_20_sec.mp3 URL to mediaplayer data source
                    mediaPlayer.prepareAsync(); // you must call this method after setup the datasource in setDataSource method. After calling prepare() the instance of MediaPlayer starts load data from URL to internal buffer.

                        MediaPlayerInterfaceInstance.getInstance().getMpinterface().buffering(mediaPlayer, true);

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }).start();


    }

    public void pauseMusic(){
        if(mediaPlayer!=null&&mediaPlayer.isPlaying()){
            if (isHandlerAttached) {
                handler.removeCallbacks(runnable);
                isHandlerAttached = false;
            }

                MediaPlayerInterfaceInstance.getInstance().getMpinterface().pauseMusic();


            mediaPlayer.pause();
            if(!PlayMusicViewModel.isBuffering){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    MediaPlayerService.mediaPlayerService.stopForeground(false);
                }else {
                    ((NotificationManager) MainApplication.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE)).notify(1,MediaPlayerService.mediaPlayerService.getNotification(
                            time,
                            MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getTitle(),
                            MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getDescription(),
                            MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getImage(),
                            BitmapFactory.decodeResource(MainApplication.getAppContext().getResources(),
                                    R.drawable.pause_black)
                    ));
                }
            }


        }
    }

    public void resumeMusic(boolean isAudioFocus){
        if(mediaPlayer!=null&&!mediaPlayer.isPlaying()){
            if (!isHandlerAttached) {
                handler.post(runnable);
                isHandlerAttached = true;
            }
                MediaPlayerInterfaceInstance.getInstance().getMpinterface().resumeMusic();



            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                MediaPlayerService.mediaPlayerService.startForeground(1,
                        MediaPlayerService.mediaPlayerService.getForeGroundNotification(
                                time,
                                MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getTitle(),
                                MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getDescription(),
                                MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getImage(),
                                BitmapFactory.decodeResource(MainApplication.getAppContext().getResources(),
                                        R.drawable.pause_black)
                        ));
            }else{
                MediaPlayerService.mediaPlayerService.getNotificationOngoing(
                        time,
                        MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getTitle(),
                        MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getDescription(),
                        MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getImage(),
                        BitmapFactory.decodeResource(MainApplication.getAppContext().getResources(),
                                R.drawable.pause_black)
                );
            }
            if(!isAudioFocus){
               MediaPlayerService.mediaPlayerService.requestFocus();
           }
            mediaPlayer.start();
        }
    }

    void setMusicToSec(int seekBarPerc) {

        if (mediaPlayer != null) {
            if (isHandlerAttached

            &&(MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getImage().contains("http://")||MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getImage().contains("https://"))) {
                handler.removeCallbacks(runnable);
                isHandlerAttached = false;
            }
            time = (songLength / 100) * seekBarPerc;
            seekPostion = seekBarPerc;

                MediaPlayerInterfaceInstance.getInstance().getMpinterface().buffering(mediaPlayer, true);

            mediaPlayer.seekTo(time);

        }
    }


    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
        bufferedPercentage = i;

            MediaPlayerInterfaceInstance.getInstance().getMpinterface().bufferdPercentage(mediaPlayer,i);

        if (seekPostion != 0 && i > seekPostion && !isHandlerAttached) {

                MediaPlayerInterfaceInstance.getInstance().getMpinterface().buffering(mediaPlayer, false);

            handler.post(runnable);
            isHandlerAttached = true;

        }
    }



    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (!isCompletionOnError) {

            handler.removeCallbacks(runnable);
            isHandlerAttached = false;
            time = 1;

                MediaPlayerInterfaceInstance.getInstance().getMpinterface().songCompleted(mediaPlayer, positionPlaying);

            if (!PlayMusicActivity.isInForground) {
                positionPlaying += 1;
                MediaPlayerService.mediaPlayerService.playSongAtPosition(positionPlaying);
            }
        } else {
            isCompletionOnError = false;
        }

    }

    @Override
    public void onPrepared(MediaPlayer medPlayer) {
        mediaPlayer.start();
        MediaPlayerService.mediaPlayerService.requestFocus();
        songLength = mediaPlayer.getDuration();

            MediaPlayerInterfaceInstance.getInstance().getMpinterface().songLength(mediaPlayer, songLength);
            MediaPlayerInterfaceInstance.getInstance().getMpinterface().buffering(mediaPlayer, false);

        if (!isHandlerAttached) {
            handler.post(runnable);
            isHandlerAttached = true;
        }
        mediaPlayer.setOnErrorListener(SingleSongIntentService.getInstance());
        mediaPlayer.setOnInfoListener(SingleSongIntentService.getInstance());
        mediaPlayer.setOnSeekCompleteListener(SingleSongIntentService.getInstance());

        mediaPlayer.setOnTimedTextListener(SingleSongIntentService.getInstance());
        mediaPlayer.setOnBufferingUpdateListener(SingleSongIntentService.getInstance());
        mediaPlayer.setOnCompletionListener(SingleSongIntentService.getInstance());


    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        isCompletionOnError = true;
        switch (i) {
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                AppUtils.showToast("Media unknown");
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                AppUtils.showToast("Server Unreachable");
                break;
        }
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                if (isHandlerAttached
                &&(MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getImage().contains("http://")||MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getImage().contains("https://"))) {
                    handler.removeCallbacks(runnable);
                    isHandlerAttached = false;
                }

                MediaPlayerInterfaceInstance.getInstance().getMpinterface().buffering(mediaPlayer,true);
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                if (!isHandlerAttached) {
                    handler.post(runnable);
                    isHandlerAttached = true;
                }

                break;
            case MediaPlayer.MEDIA_INFO_AUDIO_NOT_PLAYING:
                AppUtils.showToast("Audio not playing");
                break;

            case MediaUtlist.MEDIA_INFO_UNSUPPORTED_AUDIO:
                AppUtils.showToast("Audio Unsupported");
                break;
        }
        return false;
    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        MediaPlayerService.mediaPlayerService.requestFocus();
        if (seekPostion != 0 && bufferedPercentage > seekPostion && !isHandlerAttached) {

                MediaPlayerInterfaceInstance.getInstance().getMpinterface().buffering(mediaPlayer, false);

            handler.post(runnable);
            isHandlerAttached = true;


        }

    }


    @Override
    public void onTimedText(MediaPlayer mediaPlayer, TimedText timedText) {
        Log.e("timertimedtext", timedText.getText());
    }
}
