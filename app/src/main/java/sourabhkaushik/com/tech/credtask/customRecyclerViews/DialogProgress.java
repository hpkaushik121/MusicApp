package sourabhkaushik.com.tech.credtask.customRecyclerViews;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import sourabhkaushik.com.tech.credtask.R;

/**
 * Created by Sourabh kaushik on 11/9/2019.
 */
public class DialogProgress extends DialogFragment {
    public static final String TAG = "ProgressDialog";

    TextView mheader;

    public static DialogProgress newInstance(Bundle args) {
        DialogProgress fragment = new DialogProgress();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.progress_dialog_fragment, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mheader = (TextView) view.findViewById(R.id.progresstext);
        mheader.setVisibility(View.VISIBLE);
        mheader.setText("Please wait, we are processing your request.");
        if (getArguments() != null && getArguments().get("Processing") != null) {
            mheader.setText(getArguments().getString("Processing"));
        }

    }


    public static void show(FragmentActivity activity) {
        Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag(TAG);
        if (fragment != null) {
            // nothing to do
            return;
        }
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        DialogProgress newDialog = new DialogProgress();
        newDialog.setCancelable(false);
        newDialog.setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Dialog);
        try {
            newDialog.show(ft, TAG);
        } catch (IllegalStateException e) {
            // ignore commit failures due to state loss
            Log.e(DialogFragment.class.getSimpleName(), "State Loss issue with Progress dialog", e);
        }

    }

    public static void showWithTag(FragmentActivity activity, String tag) {

        Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            // nothing to do
            return;
        }
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        DialogProgress newDialog = new DialogProgress();
        newDialog.setCancelable(false);
        newDialog.setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Dialog);
        try {
            newDialog.show(ft, tag);
        } catch (IllegalStateException e) {
            // ignore commit failures due to state loss
            Log.e(DialogFragment.class.getSimpleName(), "State Loss issue with Progress dialog", e);
        }


    }

    public static void showDialogWithCustomText(FragmentActivity activity, Bundle b) {
        Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag(TAG);
        if (fragment != null) {
            // nothing to do
            return;
        }

        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        DialogProgress newDialog = DialogProgress.newInstance(b);
        newDialog.setCancelable(false);
        newDialog.setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Dialog);
        try {
            newDialog.show(ft, TAG);
        } catch (IllegalStateException e) {
            // ignore commit failures due to state loss
            Log.e(DialogFragment.class.getSimpleName(), "State Loss issue with Progress dialog", e);
        }

    }

    public static void hide(FragmentActivity activity) {
        Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag(TAG);
        if (fragment != null) {
            activity.getSupportFragmentManager().beginTransaction().remove(fragment).commitAllowingStateLoss();
        }

    }

    public static void hideWithTag(FragmentActivity activity, String tag) {
        Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            activity.getSupportFragmentManager().beginTransaction().remove(fragment).commitAllowingStateLoss();
        }

    }

    public void setProgress(String progress) {
        mheader.setText(progress);
    }
}
