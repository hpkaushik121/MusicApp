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

import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import sourabhkaushik.com.tech.credtask.R;
import sourabhkaushik.com.tech.credtask.customRecyclerViews.DialogProgress;
import sourabhkaushik.com.tech.credtask.databinding.ActivityMainBinding;
import sourabhkaushik.com.tech.credtask.interfaces.RequestListener;
import sourabhkaushik.com.tech.credtask.viewmodel.DataViewModel;


/**
 * Created by Gregory Rasmussen on 7/26/17.
 */
public class MainActivity extends AppCompatActivity implements RequestListener {
    private DataViewModel dataViewModel;
    Parcelable state;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = bind();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Window window = getWindow();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimary));
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private View bind() {

        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
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
}
