package sourabhkaushik.com.tech.credtask.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import sourabhkaushik.com.tech.credtask.R;
import sourabhkaushik.com.tech.credtask.Utils.AppUtils;
import sourabhkaushik.com.tech.credtask.databinding.PlayListItemLayoutBinding;
import sourabhkaushik.com.tech.credtask.databinding.PlayListModalAdapterBinding;
import sourabhkaushik.com.tech.credtask.interfaces.MediaPlayerInterfaceInstance;
import sourabhkaushik.com.tech.credtask.model.Album;
import sourabhkaushik.com.tech.credtask.model.DataModel;
import sourabhkaushik.com.tech.credtask.model.PlayListModel;
import sourabhkaushik.com.tech.credtask.services.MediaPlayerService;
import sourabhkaushik.com.tech.credtask.viewmodel.PlayListViewModel;
import sourabhkaushik.com.tech.credtask.viewmodel.PlayMusicViewModel;

/**
 * Created by Sourabh kaushik on 11/16/2019.
 */
public class PlayListModalAdapter extends  RecyclerView.Adapter<PlayListModalAdapter.GenericAdapter> implements PlayListTouchHelperAdapter.ItemTouchHelperContract{
    private List<DataModel> data;
    private PlayListViewModel playListViewModel;
    public GenericAdapter holder;

    public PlayListModalAdapter(List<DataModel> data, PlayListViewModel playListViewModel) {
        this.data = data;
        this.playListViewModel = playListViewModel;
    }

    @NonNull
    @Override
    public GenericAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GenericAdapter(LayoutInflater.from(parent.getContext()).inflate(R.layout.play_list_modal_adapter,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull GenericAdapter holder, int position) {
        this.holder=holder;
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(data, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(data, i, i - 1);
            }
        }
        if(fromPosition==MediaPlayerService.positionToplay){
            MediaPlayerService.positionToplay=toPosition;
            Type listType = new TypeToken<List<DataModel>>() {}.getType();
            MediaPlayerInterfaceInstance.getInstance().getMpinterface()
                    .onPositionChange((List<DataModel>) new Gson().fromJson(new Gson().toJson(data),listType),toPosition);
            notifyItemMoved(fromPosition, toPosition);
        }
        else if(toPosition<=MediaPlayerService.positionToplay){
            if(fromPosition>MediaPlayerService.positionToplay){
                MediaPlayerService.positionToplay++;
            }

            Type listType = new TypeToken<List<DataModel>>() {}.getType();
            MediaPlayerInterfaceInstance.getInstance().getMpinterface()
                    .onPositionChange((List<DataModel>) new Gson().fromJson(new Gson().toJson(data),listType),MediaPlayerService.positionToplay);
            notifyItemMoved(fromPosition, toPosition);
        }else {
            Type listType = new TypeToken<List<DataModel>>() {}.getType();
            MediaPlayerInterfaceInstance.getInstance().getMpinterface()
                    .onPositionChange((List<DataModel>) new Gson().fromJson(new Gson().toJson(data),listType),toPosition);
            notifyItemMoved(fromPosition, toPosition);
        }

    }

    @Override
    public void onRowSelected(GenericAdapter myViewHolder) {
//        myViewHolder.binding.getRoot().setBackgroundColor(Color.GRAY);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            myViewHolder.binding.getRoot().setElevation(50);
        }
    }

    @Override
    public void onRowClear(GenericAdapter myViewHolder) {
//        myViewHolder.binding.getRoot().setBackgroundColor(Color.WHITE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            myViewHolder.binding.getRoot().setElevation(0);
        }
    }

    @Override
    public void onItemDismiss(int position) {
        if(position==MediaPlayerService.positionToplay){
            AppUtils.showToast("this song is currently playing");
            notifyDataSetChanged();
            return;

        }
        data.remove(position);
        Type listType = new TypeToken<List<DataModel>>() {}.getType();
        MediaPlayerInterfaceInstance
                .getInstance()
                .getMpinterface()
                .onPositionChange((List<DataModel>) new Gson().fromJson(new Gson().toJson(data),listType),position);
        notifyItemRemoved(position);
    }


    public class GenericAdapter extends RecyclerView.ViewHolder{

        private PlayListModalAdapterBinding binding;
        GenericAdapter(@NonNull View itemView) {
            super(itemView);
            binding= DataBindingUtil.bind(itemView);
        }
        public PlayListModalAdapterBinding getBinding(){
            if(binding==null){
                binding= DataBindingUtil.bind(itemView);
            }
            return binding;
        }

        public void bind(Integer position){
            binding.setPosition(position);
            binding.setPlayListViewModel(playListViewModel);
            binding.title.setText(data.get(position).getTitle());
            CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(binding.getRoot().getContext());
            circularProgressDrawable.setStrokeWidth( 5f);
            circularProgressDrawable.setCenterRadius(30f);
            circularProgressDrawable.start();
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(16));
            Glide.with(binding.getRoot().getContext())
                    .load(data.get(position)
                            .getImage()).placeholder(circularProgressDrawable).apply(requestOptions)
                    .into(binding.songConver);

            binding.titleDescrption.setText(data.get(position).getDescription());
            binding.isPlaying.setVisibility(position== MediaPlayerService.positionToplay?View.VISIBLE:View.GONE);
            binding.setPosition(position);
        }
    }
}
