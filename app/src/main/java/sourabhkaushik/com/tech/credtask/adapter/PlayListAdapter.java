package sourabhkaushik.com.tech.credtask.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import sourabhkaushik.com.tech.credtask.R;
import sourabhkaushik.com.tech.credtask.databinding.PlayListItemLayoutBinding;
import sourabhkaushik.com.tech.credtask.model.DataModel;
import sourabhkaushik.com.tech.credtask.viewmodel.DataItemViewModel;
import sourabhkaushik.com.tech.credtask.viewmodel.DataViewModel;
import sourabhkaushik.com.tech.credtask.viewmodel.PlayListViewModel;

/**
 * Created by Sourabh kaushik on 11/5/2019.
 */
public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.PlayListAdapterViewHolder>{

    private List<DataModel> data;
    private PlayListViewModel playListViewModel;
    private Context context;

    public PlayListAdapter(PlayListViewModel playListViewModel) {
        this.playListViewModel = playListViewModel;
        data=new ArrayList<>();
    }

    @NonNull
    @Override
    public PlayListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        return new PlayListAdapterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.play_list_item_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull PlayListAdapterViewHolder holder, int position) {
        DataModel dataModel=data.get(position);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(16));
        holder.getBinding().coverImage.bringToFront();
        Glide.with(context).load(dataModel.getImage()).apply(requestOptions).into(holder.getBinding().coverImage);
        holder.bind(dataModel,position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void updateData(List<DataModel> model){
        this.data=model;
        notifyDataSetChanged();

    }
    public class PlayListAdapterViewHolder extends RecyclerView.ViewHolder{
        PlayListItemLayoutBinding binding;

        public PlayListAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            if(binding==null){
                binding= DataBindingUtil.bind(itemView);
            }
        }

        public PlayListItemLayoutBinding getBinding(){
            if(binding==null){
                binding= DataBindingUtil.bind(itemView);
            }
            return binding;
        }

        public void bind(DataModel model,Integer position){
            binding.setDataModel(model);
            binding.setPlayListViewModel(playListViewModel);
            binding.setPosition(position);
        }
    }
}
