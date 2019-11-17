package sourabhkaushik.com.tech.credtask.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import sourabhkaushik.com.tech.credtask.R;
import sourabhkaushik.com.tech.credtask.databinding.DummyItemLayoutBinding;
import sourabhkaushik.com.tech.credtask.databinding.MainViewRecyclerBinding;
import sourabhkaushik.com.tech.credtask.model.Album;
import sourabhkaushik.com.tech.credtask.viewmodel.DataItemViewModel;
import sourabhkaushik.com.tech.credtask.viewmodel.DataViewModel;

/**
 * Created by Sourabh kaushik on 11/5/2019.
 */
public class DummyAlbumAdapter extends RecyclerView.Adapter<DummyAlbumAdapter.DummAlbumViewHolder>{

    public List<Album> albums;
    public DataViewModel dataViewModel;
    private Context mContext;
    private int listPosition;
    public DummyAlbumAdapter(DataViewModel model,int lstPosition) {
        albums=new ArrayList<>();
        this.listPosition=lstPosition;
        this.dataViewModel=model;
    }

    @NonNull
    @Override
    public DummAlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.mContext=parent.getContext();
        return new DummAlbumViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.main_view_recycler,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull DummAlbumViewHolder holder, int position) {
        Album album=albums.get(position);
        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(mContext);
        circularProgressDrawable.setStrokeWidth( 5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(16));
        Glide.with(mContext).load(album.getCoverImage()).placeholder(circularProgressDrawable).apply(requestOptions).into(holder.getBinding().coverImage);
        holder.bind(dataViewModel,album,position);
    }

    @Override
    public int getItemCount() {
        return albums==null?0:albums.size();
    }
    public void updateData(@Nullable List<Album> data) {
        this.albums=data;
        notifyDataSetChanged();
    }

    public class DummAlbumViewHolder extends RecyclerView.ViewHolder{
        MainViewRecyclerBinding binding;


        public DummAlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            if(binding==null){
                binding= DataBindingUtil.bind(itemView);
            }
        }

        MainViewRecyclerBinding getBinding(){
            if(binding==null){
                binding= DataBindingUtil.bind(itemView);
            }
            return binding;
        }
        void bind(DataViewModel dataViewModel, Album album, Integer position) {
            if (binding == null) {
                binding = DataBindingUtil.bind(itemView);
            }
            binding.setDataViewModel(dataViewModel);
            binding.setAlbumModel(album);
            binding.setListPosition(listPosition);
            binding.setPosition(position);
        }

    }
}
