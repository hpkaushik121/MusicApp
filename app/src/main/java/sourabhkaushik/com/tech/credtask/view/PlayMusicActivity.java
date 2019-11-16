package sourabhkaushik.com.tech.credtask.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import sourabhkaushik.com.tech.credtask.R;
import sourabhkaushik.com.tech.credtask.databinding.ActivityPlayMusicBinding;
import sourabhkaushik.com.tech.credtask.fragments.PlayListFragment;
import sourabhkaushik.com.tech.credtask.viewmodel.PlayMusicViewModel;

public class PlayMusicActivity extends AppCompatActivity  {

    private ActivityPlayMusicBinding binding;
    public static boolean isInForground=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding= DataBindingUtil.setContentView(this,R.layout.activity_play_music);

        binding.setPlayMusicViewModel(new PlayMusicViewModel(this));
        binding.getPlayMusicViewModel().setBoomButton();
        binding.getPlayMusicViewModel().init(binding);

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

        FragmentManager manager=getSupportFragmentManager();
        Fragment fragment=manager.findFragmentByTag("playList");
        if(fragment!=null){
            try{
                PlayListFragment playListFragment= (PlayListFragment) fragment;
                playListFragment.binding.getPlayListViewModel().onSwipDown(400);
            }catch (Exception e){
                if(e.getMessage() !=null)
                    Log.e("error", e.getMessage());
                else
                    e.printStackTrace();
                manager.beginTransaction().remove(fragment).commit();
            }
            return;
        }else{
            if(getIntent().hasExtra("intent")){
                Intent intent=new Intent(this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
                        |Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return;
            }
        }

        super.onBackPressed();
    }

}
