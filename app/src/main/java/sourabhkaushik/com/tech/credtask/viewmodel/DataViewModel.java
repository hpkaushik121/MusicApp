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
package sourabhkaushik.com.tech.credtask.viewmodel;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import sourabhkaushik.com.tech.credtask.BR;
import sourabhkaushik.com.tech.credtask.R;
import sourabhkaushik.com.tech.credtask.adapter.DataAdapter;
import sourabhkaushik.com.tech.credtask.adapter.DummyAdapter;
import sourabhkaushik.com.tech.credtask.customRecyclerViews.CardStackLayoutManager;
import sourabhkaushik.com.tech.credtask.customRecyclerViews.CardStackListener;
import sourabhkaushik.com.tech.credtask.customRecyclerViews.CardStackView;
import sourabhkaushik.com.tech.credtask.customRecyclerViews.DialogProgress;
import sourabhkaushik.com.tech.credtask.customRecyclerViews.Direction;
import sourabhkaushik.com.tech.credtask.customRecyclerViews.Duration;
import sourabhkaushik.com.tech.credtask.customRecyclerViews.ResizeWidthAnimation;
import sourabhkaushik.com.tech.credtask.customRecyclerViews.StackFrom;
import sourabhkaushik.com.tech.credtask.customRecyclerViews.SwipeAnimationSetting;
import sourabhkaushik.com.tech.credtask.customRecyclerViews.SwipeableMethod;
import sourabhkaushik.com.tech.credtask.interfaces.MediaPlayerInterface;
import sourabhkaushik.com.tech.credtask.interfaces.MediaPlayerInterfaceInstance;
import sourabhkaushik.com.tech.credtask.interfaces.RequestListener;
import sourabhkaushik.com.tech.credtask.model.Album;
import sourabhkaushik.com.tech.credtask.model.AlbumListModel;
import sourabhkaushik.com.tech.credtask.model.DataModel;
import sourabhkaushik.com.tech.credtask.network.ApiRequest;
import sourabhkaushik.com.tech.credtask.services.MediaPlayerService;
import sourabhkaushik.com.tech.credtask.services.SingleSongIntentService;
import sourabhkaushik.com.tech.credtask.view.PlayMusicActivity;

/**
 * Created by Gregory Rasmussen on 7/26/17.
 */
public class DataViewModel extends BaseObservable implements CardStackListener {
    public DataAdapter adapter;
    private DummyAdapter dummyAdapter;
    public List<DataModel> data;
    private List<Album> dummyData;
    private View view;
    private int currentSongPosition = -1;
    private RequestListener listener;
    private Activity activity;
    private Handler handler = new Handler();
    private ObjectAnimator objectAnimator;
    private CardStackLayoutManager manager;
    private CardStackView recyclerView;
    private String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

    private String[] projection = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION
    };

    public DataViewModel(View view, Activity act) {
        data = new ArrayList<>();
        dummyData = new ArrayList<>();
        activity = act;
        listener = (RequestListener) act;
        adapter = new DataAdapter(this);
        dummyAdapter = new DummyAdapter(this);
        initRecyclerView(view);
        populateData();
    }


    private void initRecyclerView(View view) {
        this.view = view;
        CircleImageView imageView = activity.findViewById(R.id.musicImage);
        objectAnimator = ObjectAnimator.ofFloat(imageView, View.ROTATION,
                0.0f, 360.0f);

        objectAnimator.setDuration(4000);
        objectAnimator.setRepeatCount(Animation.INFINITE);
        objectAnimator.setInterpolator(new LinearInterpolator());
        manager = new CardStackLayoutManager(view.getContext(), this);
        manager.setStackFrom(StackFrom.Top);
        manager.setVisibleCount(3);
        manager.setTranslationInterval(12.0f);
        manager.setScaleInterval(0.95f);
        manager.setSwipeThreshold(0.3f);
        manager.setMaxAlpha(0.3f);
        manager.setMaxDegree(0f);
        manager.setDirections(Direction.HORIZONTAL);
        manager.setCanScrollHorizontal(true);
        manager.setCanScrollVertical(true);
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual);
        SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                .setDirection(Direction.Right)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(new AccelerateInterpolator())
                .build();
        manager.setSwipeAnimationSetting(setting);
        manager.setOverlayInterpolator(new LinearInterpolator());
        recyclerView = view.findViewById(R.id.data_recycler_view);
        RecyclerView dummyRecyclerView = view.findViewById(R.id.dummyRecyclerView);
        dummyRecyclerView.setNestedScrollingEnabled(true);
        dummyRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setLayoutManager(manager);


    }


    @Bindable
    public List<DataModel> getData() {
        return this.data;
    }

    @Bindable
    public List<Album> getDummyData() {
        return this.dummyData;
    }

    @Bindable
    public DataAdapter getAdapter() {
        return this.adapter;
    }

    @Bindable
    public DummyAdapter getDummyAdapter() {
        return this.dummyAdapter;
    }

    private void populateData() {
        // populate the data from the source, such as the database.
//        listener.onStarted();


        Cursor cursor = activity.managedQuery(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null);

        while (cursor.moveToNext()) {
            Log.d("songData", cursor.getString(0) + "||" + cursor.getString(1) + "||" + cursor.getString(2) + "||" + cursor.getString(3) + "||" + cursor.getString(4) + "||" + cursor.getString(5));
            DataModel dataModel = new DataModel();
            dataModel.setDescription(cursor.getString(1));
            dataModel.setImage(cursor.getString(3));
            dataModel.setSongUrl(cursor.getString(3));
            dataModel.setTitle(cursor.getString(2));

            data.add(dataModel);

        }
        notifyPropertyChanged(BR.data);
        notifyPropertyChanged(BR.dummyData);
//        listener.onSuccess();
        LiveData<String> response = ApiRequest.getMusicList();
//        if (response != null) {
//            response.observe((LifecycleOwner) activity, new Observer<String>() {
//                @Override
//                public void onChanged(String s) {
//
//                    if (s != null) {
//                        try {
//
//                            Type listType = new TypeToken<List<Album>>() {
//                            }.getType();
//                            List<Album> itemsList = new Gson().fromJson(s, listType);
//                            AlbumListModel albumListModel = new AlbumListModel();
//                            albumListModel.setStatus(true);
//                            List<Album> albums = new ArrayList<>();
//                            Album album = new Album();
//                            album.setType("Popular");
//                            album.setAlbum(itemsList);
//
//                            albums.add(album);
//                            album = new Album();
//                            album.setType("New Songs");
//                            album.setAlbum(itemsList);
//                            albums.add(album);
//                            album = new Album();
//                            album.setType("New Songs");
//                            album.setAlbum(itemsList);
//                            albums.add(album);
//                            album = new Album();
//                            album.setType("New Songs");
//                            album.setAlbum(itemsList);
//                            albums.add(album);
//                            album = new Album();
//                            album.setType("New Songs");
//                            album.setAlbum(itemsList);
//                            albums.add(album);
//
//                            albumListModel.setAlbums(albums);
//
//
//                            dummyData = albumListModel.getAlbums();
//
//
////                            for (Album item : itemsList) {
////                                DataModel dataModel = new DataModel();
////                                dataModel.setDescription(item.getArtists());
////                                dataModel.setImage(item.getCoverImage());
////                                dataModel.setSongUrl(item.getUrl());
////                                dataModel.setTitle(String.valueOf(item.getSong()));
////
////                                data.add(dataModel);
////                            }
//                            notifyPropertyChanged(BR.data);
//                            notifyPropertyChanged(BR.dummyData);
//                            listener.onSuccess();
//                        } catch (Exception e) {
//                            listener.OnFailure("Something went wrong check your internet connection");
//                        }
//                    } else {
//                        listener.OnFailure("Something went wrong check your internet connection");
//                    }
//
//
//                }
//            });
//        }

    }

    @Override
    public void onCardDragging(Direction direction, float ratio) {

    }


    @Override
    public void onCardSwiped(Direction direction) {
        if (manager.getTopPosition() == data.size()) {
            manager.smoothScrollToPosition(0);
        }
    }

    @Override
    public void onCardRewound() {

    }

    @Override
    public void onCardCanceled() {

    }

    @Override
    public void onCardAppeared(View view, int position) {

    }

    @Override
    public void onCardDisappeared(View view, int position) {

    }

    public void onItemClick(Integer position) {

        if (view != null) {
            Intent intent = new Intent(view.getContext(), PlayMusicActivity.class);
            intent.putExtra("position", position);
            intent.putExtra("title", data.get(position).getTitle());
            intent.putExtra("description", data.get(position).getDescription());
            MediaPlayerService.albumList = data;
            view.getContext().startActivity(intent);
        }


    }

    public void onDummyListItemClick(Integer position, Integer listPosition) {
        if (view != null) {
            List<Album> list = dummyData.get(listPosition).getAlbum();
            List<DataModel> dataModels = new ArrayList<>();
            listener.OnFailure("fragmentTag");
            for (Album item : list) {
                DataModel dataModel = new DataModel();
                dataModel.setDescription(item.getArtists());
                dataModel.setImage(item.getCoverImage());
                dataModel.setSongUrl(item.getUrl());
                dataModel.setTitle(String.valueOf(item.getSong()));
                dataModels.add(dataModel);
            }
            Intent intent = new Intent(view.getContext(), PlayMusicActivity.class);
            intent.putExtra("position", position);
            intent.putExtra("title", dataModels.get(position).getTitle());
            intent.putExtra("description", dataModels.get(position).getDescription());
            MediaPlayerService.albumList = dataModels;

            view.getContext().startActivity(intent);
        }
    }

    public void onControllerClick(View view) {
        if (MediaPlayerService.albumList != null && MediaPlayerService.albumList.size() == 0) {
            return;
        }
        Intent resultIntent = new Intent(activity, PlayMusicActivity.class);
        resultIntent.putExtra("intent", true);
        resultIntent.putExtra("position", MediaPlayerService.positionToplay);
        resultIntent.putExtra("songLength", SingleSongIntentService.getInstance().songLength);
        resultIntent.putExtra("songPlayed", SingleSongIntentService.getInstance().time);
        resultIntent.putExtra("title", MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getTitle());
        resultIntent.putExtra("description", MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getDescription());
        activity.startActivity(resultIntent);
    }

    private void setImage(final int pos) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CircleImageView imageView = activity.findViewById(R.id.musicImage);
                if (SingleSongIntentService.getInstance().getMediaPlayer() != null
                        && SingleSongIntentService.getInstance().getMediaPlayer().isPlaying() && !objectAnimator.isRunning()) {

                    objectAnimator.start();
                } else if (SingleSongIntentService.getInstance().getMediaPlayer() != null
                        && !SingleSongIntentService.getInstance().getMediaPlayer().isPlaying() && objectAnimator.isRunning()) {
                    objectAnimator.cancel();
                }


                if (currentSongPosition != pos) {

                    if(MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getImage().contains("http://")||MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getImage().contains("https://")){
                        Glide.with(activity).load(MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getImage()).into(imageView);
                    }else {

                        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                        byte[] rawArt;
                        Bitmap art;
                        BitmapFactory.Options bfo=new BitmapFactory.Options();
                        Uri uri= Uri.fromFile(new File(MediaPlayerService.albumList.get(MediaPlayerService.positionToplay).getImage()));
                        mmr.setDataSource(activity, uri);
                        rawArt = mmr.getEmbeddedPicture();
                        if (null != rawArt){
                            art = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, bfo);
                            Glide.with(activity).load(art).into(imageView);
                        }

                    }currentSongPosition = pos;
                }
            }
        });

    }


    public void setHandler() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    setImage(MediaPlayerService.positionToplay);

                } catch (Exception e) {

                } finally {
                    handler.postDelayed(this, 1000);
                }

            }
        };
        handler.post(runnable);
    }
}
