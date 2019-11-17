package sourabhkaushik.com.tech.credtask.adapter;

import android.content.Context;
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

import java.util.List;

import sourabhkaushik.com.tech.credtask.R;
import sourabhkaushik.com.tech.credtask.databinding.PlayListItemLayoutBinding;
import sourabhkaushik.com.tech.credtask.databinding.PlayListModalAdapterBinding;
import sourabhkaushik.com.tech.credtask.model.DataModel;
import sourabhkaushik.com.tech.credtask.model.PlayListModel;
import sourabhkaushik.com.tech.credtask.services.MediaPlayerService;
import sourabhkaushik.com.tech.credtask.viewmodel.PlayListViewModel;
import sourabhkaushik.com.tech.credtask.viewmodel.PlayMusicViewModel;

/**
 * Created by Sourabh kaushik on 11/16/2019.
 */
public class PlayListModalAdapter extends  RecyclerView.Adapter<PlayListModalAdapter.GenericAdapter>{
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
