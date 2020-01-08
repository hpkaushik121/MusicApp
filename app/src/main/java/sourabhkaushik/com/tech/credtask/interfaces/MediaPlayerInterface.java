package sourabhkaushik.com.tech.credtask.interfaces;

import android.media.MediaPlayer;

import java.util.List;

import sourabhkaushik.com.tech.credtask.model.DataModel;

/**
 * Created by Sourabh kaushik on 11/6/2019.
 */
public interface MediaPlayerInterface {
    void bufferdPercentage(MediaPlayer mediaPlayer,int playedPerc);
    void pauseMusic();
    void nextMusic();
    void prevMusic();
    void resumeMusic();
    void buffering(MediaPlayer mediaPlayer,boolean isBuffering);
    void songLength(MediaPlayer mediaPlayer,int songLength);
    void songCompleted(MediaPlayer mediaPlayer,int position);
    void songPlayed(MediaPlayer mediaPlayer,int seconds);
    void onPositionChange(List<DataModel> list);
}
