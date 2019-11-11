package sourabhkaushik.com.tech.credtask.view;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import sourabhkaushik.com.tech.credtask.R;
import sourabhkaushik.com.tech.credtask.customRecyclerViews.DialogProgress;
import sourabhkaushik.com.tech.credtask.databinding.ActivityPlayMusicBinding;
import sourabhkaushik.com.tech.credtask.interfaces.RequestListener;
import sourabhkaushik.com.tech.credtask.services.MediaPlayerService;
import sourabhkaushik.com.tech.credtask.viewmodel.PlayListViewModel;

public class PlayMusicActivity extends AppCompatActivity  {

    private ActivityPlayMusicBinding binding;
    public static boolean isInForground=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding= DataBindingUtil.setContentView(this,R.layout.activity_play_music);

        binding.setPlayListViewModel(new PlayListViewModel(this));
        binding.getPlayListViewModel().setBoomButton();
        binding.getPlayListViewModel().init(binding);

        Animation animbounce = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.bounce_interpolator);
        binding.dragTop.startAnimation(animbounce);

    }

    @Override
    protected void onPause() {
        super.onPause();
        isInForground=false;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isInForground=true;
        View decorView = getWindow().getDecorView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }else{
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN  );
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(getIntent().hasExtra("intent")){
            Intent intent=new Intent(this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
            |Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

}
