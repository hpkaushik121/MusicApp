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

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.List;

import sourabhkaushik.com.tech.credtask.R;
import sourabhkaushik.com.tech.credtask.customRecyclerViews.DialogProgress;
import sourabhkaushik.com.tech.credtask.databinding.ActivityMainBinding;
import sourabhkaushik.com.tech.credtask.interfaces.MediaPlayerInterface;
import sourabhkaushik.com.tech.credtask.interfaces.MediaPlayerInterfaceInstance;
import sourabhkaushik.com.tech.credtask.interfaces.RequestListener;
import sourabhkaushik.com.tech.credtask.model.DataModel;
import sourabhkaushik.com.tech.credtask.services.MediaPlayerService;
import sourabhkaushik.com.tech.credtask.services.SingleSongIntentService;
import sourabhkaushik.com.tech.credtask.viewmodel.DataViewModel;


/**
 * Created by Gregory Rasmussen on 7/26/17.
 */
public class MainActivity extends AppCompatActivity implements RequestListener {
    private static final int REQUEST_WRITE_PERMISSION = 99;
    private DataViewModel dataViewModel;
    public static boolean isInForground = false;
    private ActivityMainBinding binding;
    private static final int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 21;
    private Handler mUiHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions();
        bind();


    }
    private void checkPermissions() {
        TedPermission.with(this)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {

                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        checkPermissions();
                    }
                })
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[]
            permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    System.exit(1);
                } else {
                    //Permission Granted-System will work
                    checkPermissions();
                }

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isInForground = true;
        DialogProgress.hideWithTag(this, "fragmentTag");
        Window window = getWindow();
        binding.getViewModel().setHandler();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        isInForground = false;
    }

//    private void requestPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            requestPermissions(new String[] {
//                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
//        } else {
//            bind();
//        }
//    }
    private View bind() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        dataViewModel = new DataViewModel(binding.getRoot(), this);
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
        if (message.equalsIgnoreCase("fragmentTag")) {
            DialogProgress.showWithTag(this, "fragmentTag");
        } else {
            DialogProgress.hide(this);
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setCancelable(false)
                    .setMessage(message)
                    .show();
        }

    }


}
