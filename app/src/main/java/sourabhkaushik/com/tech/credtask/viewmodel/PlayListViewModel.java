package sourabhkaushik.com.tech.credtask.viewmodel;

import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.location.Location;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import sourabhkaushik.com.tech.credtask.MainApplication;
import sourabhkaushik.com.tech.credtask.R;
import sourabhkaushik.com.tech.credtask.Utils.AppUtils;
import sourabhkaushik.com.tech.credtask.adapter.PlayListModalAdapter;
import sourabhkaushik.com.tech.credtask.adapter.PlayListTouchHelperAdapter;
import sourabhkaushik.com.tech.credtask.databinding.PlayListLayoutBinding;
import sourabhkaushik.com.tech.credtask.fragments.PlayListFragment;
import sourabhkaushik.com.tech.credtask.interfaces.ListPlayingInterface;
import sourabhkaushik.com.tech.credtask.model.DataModel;
import sourabhkaushik.com.tech.credtask.model.PlayListModel;
import sourabhkaushik.com.tech.credtask.services.MediaPlayerService;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * Created by Sourabh kaushik on 11/16/2019.
 */
public class PlayListViewModel extends BaseObservable implements ListPlayingInterface {

    public int prevPosition=MediaPlayerService.positionToplay;
    private Application application;
    private PlayListLayoutBinding playListLayoutBinding;
    private int prevYaxis;
    private int _yDelta;
    private float scaleImage = 0.17f;
    private float alpha = 1f;
    private int Y = 0;
    private PlayListFragment playListFragment;
    public PlayListModalAdapter adapter;

    public PlayListViewModel( PlayListFragment fragment) {

        this.playListFragment=fragment;

        adapter=new PlayListModalAdapter(MediaPlayerService.albumList,this);

    }


    public void updateList(){
       if(adapter!=null){
           adapter.notifyDataSetChanged();
       }

    }
    @SuppressLint("ClickableViewAccessibility")
    public void init(final PlayListLayoutBinding playListLayoutBinding) {
        this.playListLayoutBinding = playListLayoutBinding;
        PlayListTouchHelperAdapter playListTouchHelperAdapter=new PlayListTouchHelperAdapter(adapter);
        ItemTouchHelper itemTouchHelper=new ItemTouchHelper(playListTouchHelperAdapter);
        itemTouchHelper.attachToRecyclerView(playListLayoutBinding.playListModelList);
        playListLayoutBinding.playListModelList.setAdapter(adapter);

        if(MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getImage().contains("http://")||MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getImage().contains("https://")){

            Glide.with(MainApplication.getAppContext())
                    .load(MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getImage())
                    .apply(bitmapTransform(new BlurTransformation(45, 12)))
                    .into(playListLayoutBinding.plBgImage);

            Glide.with(MainApplication.getAppContext())
                    .load(MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getImage())
                    .into(playListLayoutBinding.toolbarImage);

        }else {

            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            byte[] rawArt;
            Bitmap art;
            BitmapFactory.Options bfo=new BitmapFactory.Options();
            Uri uri= Uri.fromFile(new File(MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getImage()));
            mmr.setDataSource(MainApplication.getAppContext(), uri);
            rawArt = mmr.getEmbeddedPicture();
            if (null != rawArt){
                art = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, bfo);
                Glide.with(MainApplication.getAppContext())
                        .load(art)
                        .apply(bitmapTransform(new BlurTransformation(45, 12)))
                        .into(playListLayoutBinding.plBgImage);

                Glide.with(MainApplication.getAppContext())
                        .load(art)
                        .into(playListLayoutBinding.toolbarImage);
            }

        }


        moveViewToScreenCenter(playListLayoutBinding.toolbarImage);




        playListLayoutBinding.mainPath.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final int Y = (int) event.getRawY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) playListLayoutBinding.playListView.getLayoutParams();
                        _yDelta = Y - lParams.topMargin;
                        break;
                    case MotionEvent.ACTION_UP:
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) playListLayoutBinding.playListView.getLayoutParams();
                        layoutParams.topMargin = 400;
                        int Yaxis = Y - _yDelta;
                        if (Yaxis > 800) {
                            onSwipDown(Yaxis);
                        }
                        alpha = 1f;
                        playListLayoutBinding.getRoot().setAlpha(alpha);
                        playListLayoutBinding.playListView.setLayoutParams(layoutParams);
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        RelativeLayout.LayoutParams layoutPara = (RelativeLayout.LayoutParams) playListLayoutBinding.playListView.getLayoutParams();
                        int Yaxi = Y - _yDelta;

                        if (Yaxi > prevYaxis) {
                            alpha -= 0.005f;
                        } else if (Yaxi < prevYaxis) {
                            alpha += 0.005f;
                        }
                        if(Y<400){
                            return true;
                        }

                        playListLayoutBinding.getRoot().setAlpha(alpha);
                        layoutPara.topMargin = Y - _yDelta;
                        playListLayoutBinding.playListView.setLayoutParams(layoutPara);
                        prevYaxis = Yaxi;
                        break;
                }
                playListLayoutBinding.getRoot().invalidate();
                return true;
            }
        });


    }

    public void onSwipDown(int Yaxis) {
        backToList(playListLayoutBinding.toolbarImage);
        TranslateAnimation translateLayout = new TranslateAnimation(0, 0, Yaxis, 3000);
        translateLayout.setDuration(500);
        translateLayout.setFillAfter(true);
        playListLayoutBinding.playListView.startAnimation(translateLayout);
    }


    private void moveViewToScreenCenter(final ImageView fromImage) {

        AnimationSet animSet = new AnimationSet(true);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playListLayoutBinding.playListView.getLayoutParams();
        params.topMargin = 400;
        playListLayoutBinding.playListView.setLayoutParams(params);
        playListLayoutBinding.playListView.requestLayout();
        playListLayoutBinding.playListView.invalidate();
        animSet.setDuration(500);
        animSet.setFillAfter(true);
        ScaleAnimation scale = new ScaleAnimation(1f, scaleImage, 1f, scaleImage,
                ScaleAnimation.RELATIVE_TO_PARENT, 0, ScaleAnimation.RELATIVE_TO_PARENT, 0);
        animSet.addAnimation(scale);
        TranslateAnimation translate = new TranslateAnimation(0, 440, 0, -240);
        animSet.addAnimation(translate);
        fromImage.startAnimation(animSet);
        TranslateAnimation translateLayout = new TranslateAnimation(0, 0, 1830, 400);
        translateLayout.setDuration(500);
        playListLayoutBinding.playListView.startAnimation(translateLayout);
    }


    private void backToList(final ImageView fromImage) {

        AnimationSet animSet = new AnimationSet(true);
        animSet.setDuration(500);
        animSet.setFillAfter(true);
        ScaleAnimation scale = new ScaleAnimation(0.17f, 1f, 0.17f, 1f,
                ScaleAnimation.RELATIVE_TO_PARENT, 0, ScaleAnimation.RELATIVE_TO_PARENT, 0);
        animSet.addAnimation(scale);
        AlphaAnimation alphaAnimation=new AlphaAnimation(1f,0f);
        animSet.addAnimation(alphaAnimation);
        TranslateAnimation translate = new TranslateAnimation(440, 0, -240, 0);
        animSet.addAnimation(translate);
        animSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                FragmentManager fragmentManager=((AppCompatActivity)playListFragment.getActivity()).getSupportFragmentManager();
                AppCompatActivity appCompatActivity= (AppCompatActivity) playListFragment.getActivity();
                FrameLayout frameLayout=appCompatActivity.findViewById(R.id.playListFragment);
                frameLayout.setVisibility(View.GONE);
                fragmentManager.beginTransaction().remove(playListFragment).commitNowAllowingStateLoss();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        fromImage.startAnimation(animSet);
    }

    public void onItemClickFromList(Integer position){

        playListLayoutBinding.getPlayMusicViewModel().layoutManager.scrollToPosition(position);
        onSwipDown(400);

    }

    @Override
    public void playing() {
        playListFragment.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getImage().contains("http://")||MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getImage().contains("https://")){
                    Glide.with(MainApplication.getAppContext())
                            .load(MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getImage())
                            .apply(bitmapTransform(new BlurTransformation(45, 12)))
                            .into(playListLayoutBinding.plBgImage);

                    Glide.with(MainApplication.getAppContext())
                            .load(MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getImage())
                            .into(playListLayoutBinding.toolbarImage);
                }else {

                    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                    byte[] rawArt;
                    Bitmap art;
                    BitmapFactory.Options bfo=new BitmapFactory.Options();
                    Uri uri= Uri.fromFile(new File(MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getImage()));
                    mmr.setDataSource(MainApplication.getAppContext(), uri);
                    rawArt = mmr.getEmbeddedPicture();
                    if (null != rawArt){
                        art = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, bfo);
                        Glide.with(MainApplication.getAppContext())
                                .load(art)
                                .apply(bitmapTransform(new BlurTransformation(45, 12)))
                                .into(playListLayoutBinding.plBgImage);

                        Glide.with(MainApplication.getAppContext())
                                .load(art)
                                .into(playListLayoutBinding.toolbarImage);
                    }

                }







                adapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void stopped() {
        playListFragment.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
        adapter.notifyDataSetChanged();
            }
        });
    }
}
