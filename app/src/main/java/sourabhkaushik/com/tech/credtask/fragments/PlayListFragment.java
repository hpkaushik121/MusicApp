package sourabhkaushik.com.tech.credtask.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import sourabhkaushik.com.tech.credtask.MainApplication;
import sourabhkaushik.com.tech.credtask.R;
import sourabhkaushik.com.tech.credtask.databinding.PlayListLayoutBinding;
import sourabhkaushik.com.tech.credtask.viewmodel.PlayListViewModel;
import sourabhkaushik.com.tech.credtask.viewmodel.PlayMusicViewModel;

/**
 * Created by Sourabh kaushik on 11/14/2019.
 */
public class PlayListFragment extends Fragment {

    private PlayMusicViewModel playMusicViewModel;
    private PlayListViewModel playListViewModel;
    public PlayListLayoutBinding binding;


    public PlayListFragment(PlayMusicViewModel playMusicViewModel) {
        this.playMusicViewModel = playMusicViewModel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater, R.layout.play_list_layout,container,false);
        binding.setPlayMusicViewModel(playMusicViewModel);
        binding.setPlayListViewModel(new PlayListViewModel(this));
        binding.getPlayListViewModel().init(binding);
        return binding.getRoot();
    }
}
