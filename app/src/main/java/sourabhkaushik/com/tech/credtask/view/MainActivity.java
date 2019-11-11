/*
 * Copyright (c) 2018 Phunware Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package sourabhkaushik.com.tech.credtask.view;

import android.animation.ObjectAnimator;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import java.util.ArrayList;
import java.util.List;

import sourabhkaushik.com.tech.credtask.R;
import sourabhkaushik.com.tech.credtask.customRecyclerViews.DialogProgress;
import sourabhkaushik.com.tech.credtask.databinding.ActivityMainBinding;
import sourabhkaushik.com.tech.credtask.interfaces.MediaPlayerInterface;
import sourabhkaushik.com.tech.credtask.interfaces.MediaPlayerInterfaceInstance;
import sourabhkaushik.com.tech.credtask.interfaces.RequestListener;
import sourabhkaushik.com.tech.credtask.services.MediaPlayerService;
import sourabhkaushik.com.tech.credtask.services.SingleSongIntentService;
import sourabhkaushik.com.tech.credtask.viewmodel.DataViewModel;


/**
 * Created by Gregory Rasmussen on 7/26/17.
 */
public class MainActivity extends AppCompatActivity implements RequestListener, MediaPlayerInterface {
    private DataViewModel dataViewModel;
    public static boolean isInForground=false;
    private ActivityMainBinding binding;
    Parcelable state;
    private Handler mUiHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = bind();

        MediaPlayerInterfaceInstance.getInstance().setMpinterface(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        isInForground=true;
        Window window = getWindow();
        if(SingleSongIntentService.getInstance().getMediaPlayer()!=null
        &&SingleSongIntentService.getInstance().getMediaPlayer().isPlaying()){
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(binding.musicImage, View.ROTATION,
                    0.0f, 360.0f);

            objectAnimator.setDuration(4000);
            objectAnimator.setRepeatCount(Animation.INFINITE);
            objectAnimator.setInterpolator(new LinearInterpolator());
            objectAnimator.start();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimary));
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        isInForground=false;
    }

    private View bind() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        dataViewModel = new DataViewModel(binding.getRoot(),this);
        binding.setViewModel(dataViewModel);

        return binding.getRoot();
    }


    @Override
    public void onStarted() {
        DialogProgress.show(this);
    }

    @Override
    public void onSuccess() {
        DialogProgress.hide(this);

    }

    @Override
    public void OnFailure(String message) {
        DialogProgress.hide(this);
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setCancelable(false)
                .setMessage(message)
                .show();
    }

    @Override
    public void bufferdPercentage(MediaPlayer mediaPlayer, int playedPerc) {

    }

    @Override
    public void pauseMusic() {

    }

    @Override
    public void nextMusic() {

    }

    @Override
    public void prevMusic() {

    }

    @Override
    public void resumeMusic() {

    }

    @Override
    public void buffering(MediaPlayer mediaPlayer, boolean isBuffering) {

    }

    @Override
    public void songLength(MediaPlayer mediaPlayer, int songLength) {

    }

    @Override
    public void songCompleted(MediaPlayer mediaPlayer, int position) {

    }

    @Override
    public void songPlayed(MediaPlayer mediaPlayer, int seconds) {

    }


}
