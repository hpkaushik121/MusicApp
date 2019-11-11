package sourabhkaushik.com.tech.credtask.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.Bindable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import sourabhkaushik.com.tech.credtask.R;
import sourabhkaushik.com.tech.credtask.databinding.DummyItemLayoutBinding;
import sourabhkaushik.com.tech.credtask.model.Album;
import sourabhkaushik.com.tech.credtask.model.AlbumListModel;
import sourabhkaushik.com.tech.credtask.model.DataModel;
import sourabhkaushik.com.tech.credtask.viewmodel.DataItemViewModel;
import sourabhkaushik.com.tech.credtask.viewmodel.DataViewModel;
import sourabhkaushik.com.tech.credtask.viewmodel.DummyAdapterViewModel;

/**
 * Created by Sourabh kaushik on 11/4/2019.
 */
public class DummyAdapter extends RecyclerView.Adapter<DummyAdapter.DummyViewHolder> {
    private List<Album> dummyData;
    private DataViewModel dataViewModel;
    private Context context;
    private DummyAdapterViewModel viewModel;
    public DummyAdapter(DataViewModel model) {

        this.dataViewModel=model;

        this.dummyData = new ArrayList<>();

    }



    @NonNull
    @Override
    public DummyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context=parent.getContext();
        return new DummyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.dummy_item_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull DummyViewHolder holder, int position) {
        Album album=dummyData.get(position);
       holder.getBinding().setDummyAdapterViewModel(new DummyAdapterViewModel(dataViewModel));
       holder.getBinding().getDummyAdapterViewModel().initRecyclerView(album.getAlbum(),holder.getBinding().getRoot());
        holder.bind(dataViewModel,new DataItemViewModel(album),position);
    }

    @Override
    public int getItemCount() {
        return this.dummyData.size();
    }

    public void updateData(@Nullable List<Album> data) {
        this.dummyData=data;
        notifyDataSetChanged();
    }


    public class DummyViewHolder extends RecyclerView.ViewHolder{
        DummyItemLayoutBinding binding;

        public DummyViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        DummyItemLayoutBinding getBinding(){
            if(binding==null){
                binding = DataBindingUtil.bind(itemView);
            }
            return binding;
        }
        void bind(DataViewModel dataViewModel, DataItemViewModel dummyViewModel, Integer position) {
            if (binding == null) {
                binding = DataBindingUtil.bind(itemView);
            }
            binding.setDataViewModel(dataViewModel);
            binding.setDataItemViewModel(dummyViewModel);
            binding.setPosition(position);
        }


    }
}
