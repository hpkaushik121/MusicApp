package sourabhkaushik.com.tech.credtask.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import sourabhkaushik.com.tech.credtask.R;
import sourabhkaushik.com.tech.credtask.databinding.PlayListLayoutBinding;

/**
 * Created by Sourabh kaushik on 11/14/2019.
 */
public class PlayListFragment extends Fragment {


    private PlayListLayoutBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater, R.layout.play_list_layout,container,false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
