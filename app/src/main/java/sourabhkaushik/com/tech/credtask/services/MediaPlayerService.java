package sourabhkaushik.com.tech.credtask.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import sourabhkaushik.com.tech.credtask.R;
import sourabhkaushik.com.tech.credtask.model.Album;
import sourabhkaushik.com.tech.credtask.model.DataModel;
import sourabhkaushik.com.tech.credtask.view.PlayMusicActivity;

/**
 * Created by Sourabh kaushik on 11/6/2019.
 */
public class MediaPlayerService extends Service implements AudioManager.OnAudioFocusChangeListener {

    public static int positionToplay = 0;
    public static MediaPlayerService mediaPlayerService;
    public static List<DataModel> albumList;
    private AudioAttributes playbackAttributes;
    private boolean playbackNowAuthorized=true;
    private boolean playbackDelayed=false;
    private  AudioFocusRequest focusRequest;
    private boolean resumeOnFocusGain=true;
    private NotificationBroadcastReceiver broadcastReceiver;
    private final String focusLock="focusLock";
    private boolean isPausePlayback=false;

    @Override
    public IBinder onBind(Intent intent) {


        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    public void requestFocus() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if(audioManager !=null) {
            int res = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                res = audioManager.requestAudioFocus(focusRequest);

            } else {

                res = audioManager.requestAudioFocus(this,
                        AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN);
            }
            synchronized(focusLock) {
                if (res == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
                    playbackNowAuthorized = false;
                } else if (res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    playbackNowAuthorized = true;
                    playbackNow();
                } else if (res == AudioManager.AUDIOFOCUS_REQUEST_DELAYED) {
                    playbackDelayed = true;
                    playbackNowAuthorized = false;
                }
            }
        }
    }

    private void playbackNow() {
        if(!isPausePlayback){
            return;
        }
        isPausePlayback=false;
        if(SingleSongIntentService.getInstance().getMediaPlayer()!=null &&
                !SingleSongIntentService.getInstance().getMediaPlayer().isPlaying()){

            SingleSongIntentService.getInstance().resumeMusic(true);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(broadcastReceiver);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mediaPlayerService = this;
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("pauseSong");
        broadcastReceiver=new NotificationBroadcastReceiver();
        registerReceiver(broadcastReceiver,intentFilter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            playbackAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();

            focusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(playbackAttributes)
                    .setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener(this)
                    .build();
        }
        requestFocus();

        Bitmap icon = BitmapFactory.decodeResource(getApplication().getResources(),
                android.R.drawable.ic_media_play);
        if (Build.VERSION.SDK_INT >= 26) {
            startForeground(1, getForeGroundNotification(0,"Buffering","","",icon));

        }else{
            startForeground(1,getNotificationOngoing(0,"Buffering","....","",icon));
        }

        playSongAtPosition(positionToplay);

        return START_STICKY;
    }

    public void updateNotification(int time,String title,String text,String image,Bitmap icon) {


        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = getForeGroundNotification(time,title,text,image,icon);

        }else{
            notification=getNotificationOngoing(time,title,text,image,icon);
        }
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(mNotificationManager!=null){
            mNotificationManager.notify(1, notification);
        }

    }
    public Notification getNotificationOngoing(int time,String title, String text, final String image,Bitmap playbtnIcon){
        RemoteViews contentView = getContentView(title,text,image,playbtnIcon);
        Intent resultIntent = new Intent(this, PlayMusicActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        resultIntent.putExtra("intent",true);
        resultIntent.putExtra("position",positionToplay);
        resultIntent.putExtra("songLength",SingleSongIntentService.getInstance().songLength);
        resultIntent.putExtra("songPlayed",time);
        resultIntent.putExtra("title",title);
        resultIntent.putExtra("description",text);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >=21) {
            return new NotificationCompat.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setOngoing(true)
                    .setContentIntent(resultPendingIntent)
                    .setSmallIcon(android.R.drawable.ic_media_play)
                    .setContent(contentView)
                    .setContentText(title)
                    .build();
        }else {
            return new NotificationCompat.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setOngoing(true)
                    .setSmallIcon(android.R.drawable.ic_media_play)
                    .setContentIntent(resultPendingIntent)
                    .setContent(contentView)
                    .setContentText(title)
                    .build();
        }


    }

    public Notification getNotification(int time,String title, String text, final String image,Bitmap playbtnIcon){
        RemoteViews contentView = getContentView(title,text,image,playbtnIcon);
        Intent resultIntent = new Intent(this, PlayMusicActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        resultIntent.putExtra("intent",true);
        resultIntent.putExtra("songLength",SingleSongIntentService.getInstance().songLength);
        resultIntent.putExtra("title",title);
        resultIntent.putExtra("songPlayed",time);
        resultIntent.putExtra("position",positionToplay);
        resultIntent.putExtra("description",text);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >=21) {
            return new NotificationCompat.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setOngoing(false)
                    .setContentIntent(resultPendingIntent)
                    .setSmallIcon(android.R.drawable.ic_media_play)
                    .setContent(contentView)
                    .setContentText(title)
                    .build();
        }else {
            return new NotificationCompat.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setOngoing(false)
                    .setSmallIcon(android.R.drawable.ic_media_play)
                    .setContentIntent(resultPendingIntent)
                    .setContent(contentView)
                    .setContentText(title)
                    .build();
        }
    }

    private RemoteViews getContentView(String title, String text, final String image,Bitmap playbtnIcon) {
        final RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_layout);
        new RemoteViews(getPackageName(), R.layout.notification_layout);
        RequestOptions requestOptions = new RequestOptions();
        contentView.setImageViewBitmap(R.id.playBtnNotification,playbtnIcon);
        requestOptions = requestOptions.transforms(new CenterCrop());
        requestOptions.override(100,100);
        Glide.with(getApplicationContext())
                .asBitmap()
                .apply(requestOptions)
                .load(image)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        contentView.setImageViewBitmap(R.id.image, resource);
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            Palette palette=Palette.generate(resource);
                            GradientDrawable gradientDrawable = new GradientDrawable(
                                    GradientDrawable.Orientation.LEFT_RIGHT, new int[] {
                                    palette.getDominantColor( getColor(R.color.black)),
                                    palette.getDominantColor( getColor(R.color.black)),
                                    palette.getDominantColor( getColor(R.color.black)),
                                    palette.getDominantColor( getColor(R.color.black)),
                                    getColor(R.color.transparent)});
                            contentView.setInt(R.id.prevSongBtnNotification,"setColorFilter",  palette.getLightVibrantColor(getColor(R.color.white)));
                            contentView.setInt(R.id.playBtnNotification,"setColorFilter",  palette.getLightVibrantColor(getColor(R.color.white)));
                            contentView.setInt(R.id.nextSongBtnNotification,"setColorFilter",  palette.getLightVibrantColor(getColor(R.color.white)));
                            contentView.setTextColor(R.id.title,palette.getLightVibrantColor(getColor(R.color.white)));
                            contentView.setTextColor(R.id.text,palette.getLightVibrantColor(getColor(R.color.white)));

                            float dpi = getBaseContext().getResources().getDisplayMetrics().xdpi;
                            float dp = getBaseContext().getResources().getDisplayMetrics().density;

                            Bitmap bitmap = Bitmap.createBitmap(Math.round(288 * dp), Math.round(72 * dp), Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(bitmap);
                            gradientDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                            gradientDrawable.setCornerRadius(5 * (dpi/160));
                            gradientDrawable.draw(canvas);
                            contentView.setImageViewBitmap(R.id.bck_image, bitmap);
//                            contentView.setInt(R.id.close,"setBackground",palette.getDominantColor( getColor(R.color.white)));
//                            contentView.setTextColor(R.id.close, palette.getLightVibrantColor(getColor(R.color.colorPrimary)));
                        }

                    }
                });


        Intent intentExtras=new Intent();
        intentExtras.putExtra("title",title);
        intentExtras.putExtra("text",text);
        intentExtras.putExtra("positionSong",positionToplay+"");
        intentExtras.putExtra("image",image);


        Intent closeButton = new Intent(this,NotificationBroadcastReceiver.class);
        closeButton.putExtras(intentExtras);
        closeButton.setAction("closeService");

        Intent pauseSong = new Intent(this,NotificationBroadcastReceiver.class);
        pauseSong.putExtras(intentExtras);
        pauseSong.setAction("pauseSong");

        Intent nextSong = new Intent(this,NotificationBroadcastReceiver.class);
        nextSong.putExtras(intentExtras);
        nextSong.setAction("nextSong");

        Intent prevSong = new Intent(this,NotificationBroadcastReceiver.class);
        prevSong.putExtras(intentExtras);
        prevSong.setAction("prevSong");



        PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(this, 0, pauseSong,  PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingNextIntent = PendingIntent.getBroadcast(this, 0, nextSong,  PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingPrevIntent = PendingIntent.getBroadcast(this, 0, prevSong,  PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingCloseIntent = PendingIntent.getBroadcast(this, 0, closeButton,  PendingIntent.FLAG_UPDATE_CURRENT);
        contentView.setTextViewText(R.id.title,title);
        contentView.setTextViewText(R.id.text,text);

        contentView.setOnClickPendingIntent(R.id.playBtnNotification, pendingSwitchIntent);
        contentView.setOnClickPendingIntent(R.id.nextSongBtnNotification, pendingNextIntent);
        contentView.setOnClickPendingIntent(R.id.prevSongBtnNotification, pendingPrevIntent);
        contentView.setOnClickPendingIntent(R.id.close, pendingCloseIntent);
        return contentView;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification getForeGroundNotification(int time,String title, String text, final String image,Bitmap playbtnIcon) {
        RemoteViews contentView = getContentView(title,text,image,playbtnIcon);
        String CHANNEL_ID = "my_channel_01";
        Intent resultIntent = new Intent(this, PlayMusicActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        resultIntent.putExtra("intent",true);
        resultIntent.putExtra("title",title);
        resultIntent.putExtra("songPlayed",time);
        resultIntent.putExtra("songLength",SingleSongIntentService.getInstance().songLength);
        resultIntent.putExtra("position",positionToplay);
        resultIntent.putExtra("description",text);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        return new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentTitle(getString(R.string.app_name))
                .setOngoing(false)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(resultPendingIntent)
                .setContent(contentView)
                .setContentText(title)
                .build();
    }


    public void playSongAtPosition(final int position) {
        positionToplay=position;
        SingleSongIntentService.getInstance().playSong(position, albumList.get(position).getSongUrl());


    }

    public void setMusicToSec(int seekBarPerc) {
        if (SingleSongIntentService.getInstance() != null) {
            SingleSongIntentService.getInstance().setMusicToSec(seekBarPerc);
        }
    }


    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                if (playbackDelayed || resumeOnFocusGain) {
                    synchronized(focusLock) {
                        playbackDelayed = false;
                        resumeOnFocusGain = false;
                    }
                    playbackNow();
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                synchronized(focusLock) {
                    resumeOnFocusGain = false;
                    playbackDelayed = false;
                }
                pausePlayback();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                synchronized(focusLock) {
                    resumeOnFocusGain = true;
                    playbackDelayed = false;
                }
                pausePlayback();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // ... pausing or ducking depends on your app
                break;
        }
    }

    private void pausePlayback() {
        if(SingleSongIntentService.getInstance().getMediaPlayer().isPlaying()){
            isPausePlayback=true;
            SingleSongIntentService.getInstance().pauseMusic();
        }

    }

}
