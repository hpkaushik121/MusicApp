package sourabhkaushik.com.tech.credtask.viewmodel;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import sourabhkaushik.com.tech.credtask.BR;
import sourabhkaushik.com.tech.credtask.MainApplication;
import sourabhkaushik.com.tech.credtask.R;
import sourabhkaushik.com.tech.credtask.SweetAlert.CustomAlertDialog;
import sourabhkaushik.com.tech.credtask.Utils.AppUtils;
import sourabhkaushik.com.tech.credtask.adapter.PlayListAdapter;
import sourabhkaushik.com.tech.credtask.boommenu.BoomButtons.HamButton;
import sourabhkaushik.com.tech.credtask.boommenu.BoomButtons.OnBMClickListener;
import sourabhkaushik.com.tech.credtask.boommenu.BoomMenuButton;
import sourabhkaushik.com.tech.credtask.boommenu.Util;
import sourabhkaushik.com.tech.credtask.customRecyclerViews.carouselViewPager.CarouselEffectTransformer;
import sourabhkaushik.com.tech.credtask.customRecyclerViews.carousellayoutmanager.CarouselLayoutManager;
import sourabhkaushik.com.tech.credtask.databinding.ActivityPlayMusicBinding;
import sourabhkaushik.com.tech.credtask.fragments.PlayListFragment;
import sourabhkaushik.com.tech.credtask.interfaces.MediaPlayerInterface;
import sourabhkaushik.com.tech.credtask.interfaces.MediaPlayerInterfaceInstance;
import sourabhkaushik.com.tech.credtask.model.DataModel;
import sourabhkaushik.com.tech.credtask.services.MediaPlayerService;
import sourabhkaushik.com.tech.credtask.services.SingleSongIntentService;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * Created by Sourabh kaushik on 11/5/2019.
 */
public class PlayMusicViewModel extends BaseObservable implements MediaPlayerInterface, View.OnClickListener, ViewPager.OnPageChangeListener {

    private PlayListAdapter playListAdapter;
    private AppCompatActivity activity;
    private ActivityPlayMusicBinding activityMainBinding;
    private int _yDelta;
    public ViewPager customViewPagerTop;
    public ViewPager customViewPagerBackGround;
    private int songPlayedSeconds = 0;
    private int alreadyInitiated=-1;
    private boolean ntIntent = false;
    public static boolean isPlaying = true;
    private boolean isScrollDueToPostion = false;
    public static boolean isBuffering = false;
    private int index=0;


    public PlayMusicViewModel(AppCompatActivity activity) {
        this.activity = activity;
        playListAdapter = new PlayListAdapter(this);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void init(final ActivityPlayMusicBinding binding) {
        final ImageView imageView = activity.findViewById(R.id.backBtn);
        MediaPlayerInterfaceInstance.getInstance().setMpinterface(this);
        imageView.setOnClickListener(this);
        activityMainBinding = binding;
        customViewPagerBackGround=binding.viewPagerbackground;
        customViewPagerTop=binding.viewpagerTop;
        initViewPager();
        if (SingleSongIntentService.getInstance().getMediaPlayer().isPlaying()) {
            isPlaying = true;
            activityMainBinding.playBtn.setImageDrawable(activity.getResources().getDrawable(R.drawable.pause_btn));
        } else {
            isPlaying = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                activityMainBinding.seekBar.setMin(0);
            }
            activityMainBinding.seekBar.setMax(100);
            activityMainBinding.seekBar.setProgress(AppUtils.getSeekbarPercentage(SingleSongIntentService.getInstance().songLength, SingleSongIntentService.getInstance().time));
            activityMainBinding.playBtn.setImageDrawable(activity.getResources().getDrawable(R.drawable.play_btn));
        }
//        binding.playList.setLayoutManager(layoutManager);
        binding.playBtn.setOnClickListener(this);
        binding.prevSongBtn.setOnClickListener(this);
        binding.nextSongBtn.setOnClickListener(this);
        binding.viewpagerTop.addOnPageChangeListener(this);
//        binding.playList.addOnScrollListener(new CenterScrollListener());

        binding.seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int progress = binding.seekBar.getMax() - (int) (binding.seekBar.getMax() * (binding.seekBar.getWidth() - motionEvent.getX()) / binding.seekBar.getWidth());
                        if (progress < 0) {
                            progress = 0;
                        }
                        if (progress > binding.seekBar.getMax()) {
                            progress = binding.seekBar.getMax();
                        }
                        binding.seekBar.setProgress(progress);
                        Log.i("radio", "action move");
                        binding.seekBar.setPressed(true);
                        binding.seekBar.setSelected(true);
                        break;
                    case MotionEvent.ACTION_UP:
                        int prog = binding.seekBar.getProgress();
                        MediaPlayerService.mediaPlayerService.setMusicToSec(prog);
                        binding.seekBar.setPressed(false);
                        binding.seekBar.setSelected(false);
                        break;
                }
                return true;
            }
        });

        if (MediaPlayerService.albumList!=null&&MediaPlayerService.albumList.size()!=0) {
            binding.viewpagerTop.postDelayed(new Runnable() {
                @Override
                public void run() {
                    customViewPagerTop.setCurrentItem(MediaPlayerService.positionToplay);
                    onPageSelected(MediaPlayerService.positionToplay);
                    onPageScrollStateChanged(0);
                }
            },100);

            MediaPlayerInterfaceInstance.getInstance().setMpinterface(PlayMusicViewModel.this);

            if (!activity.getIntent().hasExtra("intent")) {
                ntIntent = false;
                binding.rippleContent.startRippleAnimation();
                if(MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getImage().contains("http://")||MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getImage().contains("https://")){
                    isBuffering = true;
                }


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Intent intent = new Intent(activity, MediaPlayerService.class);
                    activity.startForegroundService(intent);
                } else {
                    Intent intent = new Intent(MainApplication.getAppContext().getApplicationContext(), MediaPlayerService.class);
                    activity.startService(intent);


                }
            } else {
                ntIntent = true;
            }

        }

        activityMainBinding.dragTop.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                final int Y = (int) event.getRawY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        LinearLayout.LayoutParams lParams = (LinearLayout.LayoutParams) view.getLayoutParams();
                        _yDelta = Y - lParams.topMargin;
                        break;
                    case MotionEvent.ACTION_UP:
                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
                        layoutParams.topMargin = 0;
                        int Yaxis = Y - _yDelta;
                        if (Yaxis < -300) {
                            onSwipUP();
                        }
                        view.setLayoutParams(layoutParams);
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        LinearLayout.LayoutParams layoutPara = (LinearLayout.LayoutParams) view.getLayoutParams();
                        layoutPara.topMargin = Y - _yDelta;
                        view.setLayoutParams(layoutPara);
                        break;
                }
                binding.getRoot().invalidate();
                return true;
            }
        });


    }
    private void onSwipUP() {
        activityMainBinding.playListFragment.setVisibility(View.VISIBLE);
        activityMainBinding.playListFragment.bringToFront();
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.playListFragment, new PlayListFragment(PlayMusicViewModel.this), "playList")
                .commitNowAllowingStateLoss();
    }


    @Bindable
    public List<DataModel> getDataModels() {
        return MediaPlayerService.albumList;
    }

    @Bindable
    public PlayListAdapter getPlayListAdapter() {
        return playListAdapter;
    }




    @Override
    public void bufferdPercentage(MediaPlayer mediaPlayer, final int playedPerc) {

        activityMainBinding.seekBar.setSecondaryProgress(playedPerc);
    }

    @Override
    public void pauseMusic() {
        if (!isBuffering) {
            setUiStopped();
        }
    }

    @Override
    public void nextMusic() {
        nextSong();
    }

    @Override
    public void prevMusic() {
        prevSong();
    }

    @Override
    public void resumeMusic() {
        if (!isBuffering) {
            setUiPlaying();
        }
    }

    @Override
    public void buffering(MediaPlayer mediaPlayer, boolean isBuffer) {
        if(MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getImage().contains("http://")||
                MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getImage().contains("https://")){
            isBuffering = isBuffer;
            if (isBuffering) {
                setUiBuffering();
            } else {
                if (!isPlaying && mediaPlayer.isPlaying()) {
                    setUiPlaying();
                }
            }
        }


    }

    @Override
    public void songLength(MediaPlayer mediaPlayer, final int songLength) {
        activityMainBinding.totalLength.setText(AppUtils.getsongLength(SingleSongIntentService.getInstance().songLength));

    }

    @Override
    public void songCompleted(MediaPlayer mediaPlayer, int position) {
        if (position < MediaPlayerService.albumList.size() - 1) {
            final int pos=++position;
            activityMainBinding.viewpagerTop.postDelayed(new Runnable() {
                @Override
                public void run() {
                    activityMainBinding.viewpagerTop.setCurrentItem(pos);
                    onPageSelected(pos);
                    onPageScrollStateChanged(0);
                }
            },100);

//            layoutManager.scrollToPosition(++position);
        }
    }

    @Override
    public void songPlayed(MediaPlayer mediaPlayer, final int seconds) {
        songPlayedSeconds = seconds;
        if (mediaPlayer.isPlaying()) {
            setUiPlaying();
            activityMainBinding.seekBar.setProgress(AppUtils.getSeekbarPercentage(SingleSongIntentService.getInstance().songLength, seconds));
            activityMainBinding.totalLength.setText(AppUtils.getsongLength(SingleSongIntentService.getInstance().songLength));
            activityMainBinding.playedLength.setText(AppUtils.getsongLength(seconds));
        }

    }

    @Override
    public void onPositionChange(List<DataModel> list) {

        isScrollDueToPostion = true;
        final int pos=MediaPlayerService.positionToplay;
        activityMainBinding.viewpagerTop.postDelayed(new Runnable() {
            @Override
            public void run() {
                activityMainBinding.viewpagerTop.setCurrentItem(pos);
                onPageSelected(pos);
                onPageScrollStateChanged(0);
            }
        },100);

//        MediaPlayerService.albumList=new ArrayList<>(list);
        notifyPropertyChanged(BR.dataModels);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                activity.onBackPressed();
                break;
            case R.id.playBtn:
                if (!isBuffering) {
                    if (isPlaying) {

                        SingleSongIntentService.getInstance().pauseMusic();

                    } else {
                        SingleSongIntentService.getInstance().resumeMusic(false);
                    }
                } else {
                    AppUtils.showToast(activity.getString(R.string.stillBufering));
                }

                break;
            case R.id.prevSongBtn:
                prevSong();
                break;
            case R.id.nextSongBtn:
                nextSong();
                break;
        }

    }


    private void initViewPager() {
        customViewPagerTop.setClipChildren(false);
        customViewPagerTop.setPageMargin(activity.getResources().getDimensionPixelOffset(R.dimen.pager_margin));
        customViewPagerTop.setOffscreenPageLimit(3);
        customViewPagerBackGround.setOffscreenPageLimit(3);
        customViewPagerTop.setPageTransformer(false, new CarouselEffectTransformer(activity.getApplicationContext())); // Set transformer
    }

//    @Override
//    public void onCenterItemChanged(final int adapterPosition) {
//
//        if (CarouselLayoutManager.INVALID_POSITION != adapterPosition) {
//            activityMainBinding.nextSongBtn.setVisibility(View.VISIBLE);
//            activityMainBinding.prevSongBtn.setVisibility(View.VISIBLE);
//            if (adapterPosition == dataModels.size() - 1) {
//                activityMainBinding.nextSongBtn.setVisibility(View.INVISIBLE);
//            }
//            if (adapterPosition == 0) {
//                activityMainBinding.prevSongBtn.setVisibility(View.INVISIBLE);
//            }
//            final DataModel model = dataModels.get(adapterPosition);
//
//            if(model.getImage().contains("http://")||model.getImage().contains("https://")){
//                Glide.with(activityMainBinding.getRoot())
//                        .load(model.getImage())
//                        .placeholder(R.drawable.music_placeholder)
//                        .apply(bitmapTransform(new BlurTransformation(Util.blurIndex, Util.sampling)))
//                        .into(activityMainBinding.background);
//            }else {
//
//                new setImage().execute(model.getImage(),activityMainBinding.background);
//
//            }
//
//            activityMainBinding.singerName.setText(model.getDescription());
//            activityMainBinding.songName.setText(model.getTitle());
//
//            if (isScrollDueToPostion) {
//                isScrollDueToPostion = false;
//                return;
//            }
//
//
//            activityMainBinding.totalLength.setText(AppUtils.getsongLength(activity.getIntent().getIntExtra("songLength", 0)));
//            activityMainBinding.playedLength.setText(AppUtils.getsongLength(activity.getIntent().getIntExtra("songPlayed", 0)));
//            activityMainBinding.seekBar.setProgress(AppUtils.getSeekbarPercentage(activity.getIntent().getIntExtra("songLength", 0),
//                    activity.getIntent().getIntExtra("songPlayed", 0)));
//            if (isBuffering) {
//                activityMainBinding.rippleContent.startRippleAnimation();
//            }
//            if (!ntIntent && MediaPlayerService.positionToplay != adapterPosition) {
//                activityMainBinding.seekBar.setSecondaryProgress(0);
//                activityMainBinding.rippleContent.startRippleAnimation();
//                isBuffering = true;
//                MediaPlayerService.mediaPlayerService.playSongAtPosition(adapterPosition);
//            }
//            ntIntent = false;
//            setBoomButton();
//        }
//    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        int width = customViewPagerBackGround.getWidth();
        customViewPagerBackGround.scrollTo((int) (width * MediaPlayerService.positionToplay + width * positionOffset), 0);
    }

    @Override
    public void onPageSelected(int position) {
        index = position;

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            if(index==MediaPlayerService.positionToplay&&alreadyInitiated!=-1){

                return;
            }
            alreadyInitiated=0;
            customViewPagerBackGround.setCurrentItem(index);
            activityMainBinding.nextSongBtn.setVisibility(View.VISIBLE);
            activityMainBinding.prevSongBtn.setVisibility(View.VISIBLE);
            if (index == MediaPlayerService.albumList.size() - 1) {
                activityMainBinding.nextSongBtn.setVisibility(View.INVISIBLE);
            }
            if (index == 0) {
                activityMainBinding.prevSongBtn.setVisibility(View.INVISIBLE);
            }
            final DataModel model = MediaPlayerService.albumList.get(index);
            activityMainBinding.singerName.setText(model.getDescription());
            activityMainBinding.songName.setText(model.getTitle());

            if (isScrollDueToPostion) {
                isScrollDueToPostion = false;
                return;
            }


            activityMainBinding.totalLength.setText(AppUtils.getsongLength(SingleSongIntentService.getInstance().songLength));
            activityMainBinding.playedLength.setText(AppUtils.getsongLength(SingleSongIntentService.getInstance().time));
            activityMainBinding.seekBar.setProgress(AppUtils.getSeekbarPercentage(SingleSongIntentService.getInstance().songLength,
                    SingleSongIntentService.getInstance().time));
            if (isBuffering) {
                activityMainBinding.rippleContent.startRippleAnimation();
            }
            if (!ntIntent && MediaPlayerService.positionToplay != index) {
                activityMainBinding.seekBar.setSecondaryProgress(0);
                activityMainBinding.rippleContent.startRippleAnimation();
                isBuffering = true;
                MediaPlayerService.mediaPlayerService.playSongAtPosition(index);
            }
            ntIntent = false;
            setBoomButton();
        }
    }

    private void setUiPlaying() {
        activityMainBinding.seekBar.setEnabled(true);
        isPlaying = true;
        isBuffering = false;
        FragmentManager fragmentManager = ((AppCompatActivity) activity).getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag("playList");
        if (fragment != null) {

            try {
                PlayListFragment playListFragment = (PlayListFragment) fragment;
                if (playListFragment.binding.getPlayListViewModel().prevPosition != MediaPlayerService.positionToplay) {
                    playListFragment.binding.getPlayListViewModel().prevPosition=MediaPlayerService.positionToplay;
                    playListFragment.binding.getPlayListViewModel().playing();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        activityMainBinding.playBtn.setImageDrawable(activity.getResources().getDrawable(R.drawable.pause_btn));
        activityMainBinding.rippleContent.stopRippleAnimation();
        MediaPlayerService.mediaPlayerService.updateNotification(
                songPlayedSeconds,
                MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getTitle(),
                MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getDescription(),
                MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getImage(),
                BitmapFactory.decodeResource(activityMainBinding.getRoot().getResources(),
                        R.drawable.pause_black)
        );

    }


    private void setUiStopped() {
        isPlaying = false;
        FragmentManager fragmentManager = ((AppCompatActivity) activity).getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag("playList");
        if (fragment != null) {
            try {
                PlayListFragment playListFragment = (PlayListFragment) fragment;
                playListFragment.binding.getPlayListViewModel().stopped();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        activityMainBinding.playBtn.setImageDrawable(activity.getResources().getDrawable(R.drawable.play_btn));
        MediaPlayerService.mediaPlayerService.updateNotification(
                songPlayedSeconds,
                MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getTitle(),
                MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getDescription(),
                MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getImage(),
                BitmapFactory.decodeResource(activityMainBinding.getRoot().getResources(),
                        R.drawable.arrow_play)
        );
    }

    private void setUiBuffering() {
        activityMainBinding.seekBar.setEnabled(false);
        if (isPlaying) {
            setUiStopped();
        }
        activityMainBinding.rippleContent.startRippleAnimation();
        MediaPlayerService.mediaPlayerService.updateNotification(
                songPlayedSeconds,
                MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getTitle(),
                MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getDescription(),
                MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getImage(),
                BitmapFactory.decodeResource(activityMainBinding.getRoot().getResources(),
                        R.drawable.arrow_play)
        );
    }

    private void nextSong() {
        int position=activityMainBinding.viewpagerTop.getCurrentItem();
//        int position = layoutManager.getCenterItemPosition();
        if (position < MediaPlayerService.albumList.size() - 1) {
            final int pos=++position;
            activityMainBinding.viewpagerTop.postDelayed(new Runnable() {
                @Override
                public void run() {
                    activityMainBinding.viewpagerTop.setCurrentItem(pos);
                    onPageSelected(pos);
                    onPageScrollStateChanged(0);
                }
            },100);

//            layoutManager.scrollToPosition(++position);

        }
    }

    private void prevSong() {
        int position = activityMainBinding.viewpagerTop.getCurrentItem();
        if (position > 0) {
            final int pos=--position;
            activityMainBinding.viewpagerTop.postDelayed(new Runnable() {
                @Override
                public void run() {
                    activityMainBinding.viewpagerTop.setCurrentItem(pos);
                    onPageSelected(pos);
                    onPageScrollStateChanged(0);
                }
            },100);

//            layoutManager.scrollToPosition(--position);
        }
    }


    public void setBoomButton() {
        BoomMenuButton bmb = activity.findViewById(R.id.bmb);
        if (bmb.getBuilders().size() > 0) {
            if (bmb.getBoomButtons().size() > 1) {

                //setting global
                bmb.getBoomButtons().get(0).subNormalText = MediaPlayerService.albumList.size() < 1 ?
                        "completed" : MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getTitle();
                bmb.getBoomButtons().get(1).subNormalText = MediaPlayerService.albumList.size() < 1 ?
                        "subscribed" : MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getDescription();


                //notifying view
                TextView textView = bmb.getBoomButtons().get(0).getSubTextView();
                textView.setText(MediaPlayerService.albumList.size() < 1 ?
                        "completed" : MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getTitle());
                TextView subtextView = bmb.getBoomButtons().get(1).getSubTextView();
                subtextView.setText(MediaPlayerService.albumList.size() < 1 ?
                        "subscribed" : MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getDescription());

            }
            return;
        }
        for (int i = 0; i < bmb.getPiecePlaceEnum().pieceNumber(); i++) {
            final int pos = i;
            HamButton.Builder builder = new HamButton.Builder()
                    .normalImageRes(i == 0 ? R.drawable.money_return : R.drawable.capture)
                    .normalText(i == 0 ? "Purchase " : "Subscribe ")
                    .subNormalText(i == 0 ?
                            MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getTitle() :
                            MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getDescription())
                    .rotateImage(true)
                    .pieceColorRes(R.color.white)
                    .listener(new OnBMClickListener() {
                        @Override
                        public void onBoomButtonClick(int index) {
                            // When the boom-button corresponding this builder is clicked.
                            CustomAlertDialog pDialog = new CustomAlertDialog(activity, CustomAlertDialog.SUCCESS_TYPE);
                            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                            pDialog.setTitleText(pos == 0 ?
                                    MediaPlayerService.albumList.size() < 1 ?
                                            "Success" : MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getTitle() :
                                    MediaPlayerService.albumList.size() < 1 ?
                                            "Subscribed" : MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getDescription());
                            pDialog.setContentText(pos == 0 ? "Purchase completed" : "Successfully subscribed");
                            pDialog.setCancelable(false);
                            pDialog.show();
                        }
                    });
            bmb.addBuilder(builder);
        }


    }

    public void setupView() {
        if(activityMainBinding.viewpagerTop.getCurrentItem()!=CarouselLayoutManager.INVALID_POSITION
                &&activityMainBinding.viewpagerTop.getCurrentItem()!=MediaPlayerService.positionToplay){
            isScrollDueToPostion=true;
            final int pos=MediaPlayerService.positionToplay;
            activityMainBinding.viewpagerTop.postDelayed(new Runnable() {
                @Override
                public void run() {
                    activityMainBinding.viewpagerTop.setCurrentItem(pos);
                    onPageSelected(pos);
                    onPageScrollStateChanged(0);
                }
            },100);

//            layoutManager.scrollToPosition(MediaPlayerService.positionToplay);
        }
    }
}
