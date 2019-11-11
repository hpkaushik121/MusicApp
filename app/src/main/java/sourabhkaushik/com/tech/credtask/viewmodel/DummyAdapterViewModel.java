package sourabhkaushik.com.tech.credtask.viewmodel;

import android.view.View;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import sourabhkaushik.com.tech.credtask.R;
import sourabhkaushik.com.tech.credtask.adapter.DummyAlbumAdapter;
import sourabhkaushik.com.tech.credtask.model.Album;

/**
 * Created by Sourabh kaushik on 11/5/2019.
 */
public class DummyAdapterViewModel extends BaseObservable {

    public DummyAlbumAdapter dummyAlbumAdapter;
    private List<Album> dummyAlbum;
    public DummyAdapterViewModel(DataViewModel model) {
        this.dummyAlbum=new ArrayList<>();
        this.dummyAlbumAdapter=new DummyAlbumAdapter(model);
    }
    @Bindable
    public List<Album> getDummyAlbum() {
        return dummyAlbum;
    }

    @Bindable
    public DummyAlbumAdapter getDummyAlbumAdapter() {
        return dummyAlbumAdapter;
    }

    public void initRecyclerView(List<Album> album ,View view) {
        RecyclerView recyclerView=view.findViewById(R.id.mainDummyList);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(view.getContext(),RecyclerView.HORIZONTAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        dummyAlbum=album;
        notifyPropertyChanged(BR.dummyAlbum);
    }
}
